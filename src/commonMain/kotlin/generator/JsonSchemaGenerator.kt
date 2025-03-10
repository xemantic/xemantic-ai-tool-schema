/*
 * Copyright 2024-2025 Kazimierz Pogoda / Xemantic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xemantic.ai.tool.schema.generator

import com.xemantic.ai.tool.schema.*
import com.xemantic.ai.tool.schema.meta.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.serializer

/**
 * Generates a JSON schema for the specified type [T].
 *
 * This function creates a detailed JSON schema by analyzing the structure and metadata
 * of the [SerialDescriptor] extracted from the type [T]. The type [T] must be [Serializable].
 * It handles nested objects, arrays, and various primitive types, incorporating annotations
 * for additional schema properties.
 *
 * @param T The type for which to generate the JSON schema.
 * @param title An optional title for the schema.
 * @param description An optional description for the schema.
 * @param additionalProperties Determines whether additional properties are allowed in objects.
 *        If null, the schema will not specify this constraint.
 * @return A [JsonSchema] representing the structure of type [T].
 * @see generateSchema
 */
public inline fun <reified T> jsonSchemaOf(
    title: String? = null,
    description: String? = null,
    additionalProperties: Boolean? = null,
    inlineRefs: Boolean? = null
): JsonSchema = generateSchema(
    serializer<T>().descriptor,
    title,
    description,
    additionalProperties,
    inlineRefs,
)

/**
 * Generates a JSON schema from a given [SerialDescriptor].
 *
 * This function creates a detailed JSON schema by analyzing the structure and metadata
 * of the provided [SerialDescriptor]. It handles nested objects, arrays, and various
 * primitive types, incorporating annotations for additional schema properties.
 *
 * @param descriptor The serial descriptor of the type for which to generate the JSON schema.
 * @param title An optional title for the schema.
 * @param description An optional description for the schema.
 * @param additionalProperties Determines whether additional properties are allowed in objects.
 *        If null, the schema will not specify this constraint.
 * @return A [JsonSchema] representing the structure of type described by the [descriptor].
 */
@OptIn(ExperimentalSerializationApi::class)
public fun generateSchema(
    descriptor: SerialDescriptor,
    title: String? = null,
    description: String? = null,
    additionalProperties: Boolean? = null,
    inlineRefs: Boolean? = null
): JsonSchema = JsonSchemaGenerator(
    additionalProperties,
    inlineRefs
).generatePropertySchema(
    descriptor,
    title,
    description
)

private class JsonSchemaGenerator(
    private val additionalProperties: Boolean? = null,
    private val inlineRefs: Boolean? = null
) {

    private var rootRef: String? = null

    private val trackedRefs = mutableSetOf<String>()

    private val defs: MutableMap<String, ObjectSchema> = mutableMapOf()

    fun generatePropertySchema(
        descriptor: SerialDescriptor,
        title: String? = null,
        description: String? = null,
        propertyMeta: List<Annotation> = emptyList()
    ): JsonSchema {
        val typeMeta = descriptor.annotations
        val combinedMeta = propertyMeta + typeMeta
        val base: JsonSchema.Builder.() -> Unit = {
            this.title = title ?: combinedMeta.find<Title>()?.value
            this.description = description ?: combinedMeta.find<Description>()?.value
        }
        return when (descriptor.kind) {
            PrimitiveKind.STRING -> stringSchema(base, combinedMeta, descriptor)
            PrimitiveKind.INT,
            PrimitiveKind.LONG,
            PrimitiveKind.SHORT,
            PrimitiveKind.BYTE -> integerSchema(base, combinedMeta)
            PrimitiveKind.FLOAT,
            PrimitiveKind.DOUBLE -> numberSchema(base, combinedMeta)
            PrimitiveKind.BOOLEAN -> booleanSchema(base)
            PrimitiveKind.CHAR -> charSchema(base, combinedMeta)
            SerialKind.ENUM -> enumProperty(base, descriptor)
            StructureKind.LIST -> arraySchema(base, combinedMeta, descriptor)
            StructureKind.MAP -> mapSchema(base)
            StructureKind.CLASS, @OptIn(ExperimentalSerializationApi::class) PolymorphicKind.SEALED ->
                objectSchemaOrRef(title, description, typeMeta, propertyMeta, descriptor)
            else -> mapSchema(base) // Default case
        }
    }

    private fun objectSchemaOrRef(
        title: String? = null,
        description: String? = null,
        typeMeta: List<Annotation>,
        propertyMeta: List<Annotation>,
        descriptor: SerialDescriptor,
    ): JsonSchema {

        val ref = descriptor.refName

        fun JsonSchema.Ref.Builder.refDefaults() {
            this.title = propertyMeta.find<Title>()?.value
            this.description = propertyMeta.find<Description>()?.value
        }

        return if (ref in trackedRefs) {

            if (ref == rootRef) JsonSchema.Ref("#") { refDefaults() }
            else JsonSchema.Ref("#/definitions/$ref") { refDefaults() }
        } else {

            trackedRefs += ref

            val isRoot = if (rootRef == null) {
                rootRef = ref
                true
            } else {
                false
            }

            val props = mutableMapOf<String, JsonSchema>()
            val req = mutableListOf<String>()
            val oneOf = mutableListOf<JsonSchema>()

            @OptIn(ExperimentalSerializationApi::class)
            if (descriptor.kind == PolymorphicKind.SEALED) {
                val sealedDescriptor = descriptor.getElementDescriptor(1)
                val discriminatorName = descriptor.getElementName(0)

                for (i in 0 until sealedDescriptor.elementsCount) {
                    val classDescriptor = sealedDescriptor.getElementDescriptor(i)
                    val discriminatorValue = sealedDescriptor.getElementName(i)

                    val discriminatorProperty = mapOf(discriminatorName to JsonSchema.Const(discriminatorValue))
                    val schema = objectSchemaOrRef(title, description, classDescriptor.annotations, emptyList(), classDescriptor)

                    oneOf += if (inlineRefs == true) {
                        (schema as ObjectSchema).copy {
                            properties = discriminatorProperty + properties.orEmpty()
                        }
                    } else {
                        val discriminator = ObjectSchema { properties = discriminatorProperty }
                        ObjectSchema { allOf = listOf(discriminator, schema) }
                    }
                }
            } else {
                for (i in 0 until descriptor.elementsCount) {
                    val elementDescriptor = descriptor.getElementDescriptor(i)
                    val name = descriptor.getElementName(i)
                    val property = generatePropertySchema(
                        descriptor = elementDescriptor,
                        propertyMeta = descriptor.getElementAnnotations(i)
                    )
                    props[name] = property
                    if (!descriptor.isElementOptional(i)) {
                        req.add(name)
                    }
                }
            }

            val combinedMeta = if (inlineRefs == true) propertyMeta + typeMeta else typeMeta

            val schema = ObjectSchema {
                this.title = title ?: combinedMeta.find<Title>()?.value
                this.description = description ?: combinedMeta.find<Description>()?.value
                this.oneOf = oneOf.ifEmpty { null }
                properties = props.ifEmpty { null }
                definitions = if (isRoot && defs.isNotEmpty()) defs else null
                required = req.ifEmpty { null }
                additionalProperties = this@JsonSchemaGenerator.additionalProperties
            }

            return if (isRoot || inlineRefs == true) {
                schema
            } else {
                defs[ref] = schema
                JsonSchema.Ref("#/definitions/$ref") { refDefaults() }
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun arraySchema(
        baseSetter: JsonSchema.Builder.() -> Unit,
        meta: List<Annotation>,
        descriptor: SerialDescriptor
    ): JsonSchema {

        val ref = descriptor.refName

        val isRoot = if (rootRef == null) {
            rootRef = ref
            true
        } else false

        val elementDescriptor = descriptor.getElementDescriptor(0)
        val elementMeta = descriptor.getElementAnnotations(0) +
                elementDescriptor.annotations +
                meta
                    .filter { it !is Description && it !is Title }
                    .map {
                        when (it) {
                            is ItemDescription -> Description(it.value)
                            is ItemTitle -> Title(it.value)
                            else -> it
                        }
                    }

        val itemSchema = generatePropertySchema(
            descriptor = elementDescriptor,
            propertyMeta = elementMeta
        )

        return ArraySchema {
            baseSetter(this)
            items = itemSchema
            minItems = meta.find<MinItems>()?.value
            maxItems = meta.find<MaxItems>()?.value
            uniqueItems = if (meta.find<UniqueItems>() != null) true else null
            definitions = if (isRoot && defs.isNotEmpty()) defs else null
        }

    }

}

/**
 * Gets a reference name for the [SerialDescriptor] that is compatible with JSON Schema.
 *
 * JSON Schema does not allow dots in names. If the @[kotlinx.serialization.SerialName]
 * was not specified for the class, the fully qualified class name will be used.
 * This property transforms that name into
 * a schema-acceptable identifier by replacing dots with underscores and trimming any
 * trailing question marks.
 */
private val SerialDescriptor.refName get() = serialName.replace('.', '_').trimEnd('?')

private fun enumProperty(
    baseSetter: JsonSchema.Builder.() -> Unit,
    descriptor: SerialDescriptor
) = StringSchema {
    baseSetter(this)
    enum = descriptor.elementNames().map { it }
}

@OptIn(ExperimentalSerializationApi::class)
private fun SerialDescriptor.elementNames(): List<String> = buildList {
    for (i in 0 until elementsCount) {
        val name = getElementName(i)
        add(name)
    }
}

private fun integerSchema(
    baseSetter: JsonSchema.Builder.() -> Unit,
    meta: List<Annotation>
): JsonSchema {
    val min = meta.find<MinInt>()
    val max = meta.find<MaxInt>()
    return IntegerSchema {
        baseSetter(this)
        minimum = if (min != null && !min.exclusive) min.value else null
        maximum = if (max != null && !max.exclusive) max.value else null
        exclusiveMinimum = if (min != null && min.exclusive) min.value else null
        exclusiveMaximum = if (max != null && max.exclusive) max.value else null
    }
}

private fun numberSchema(
    baseSetter: JsonSchema.Builder.() -> Unit,
    meta: List<Annotation>
): JsonSchema {
    val min = meta.find<Min>()
    val max = meta.find<Max>()
    return NumberSchema {
        baseSetter(this)
        minimum = if (min != null && !min.exclusive) min.value else null
        maximum = if (max != null && !max.exclusive) max.value else null
        exclusiveMinimum = if (min != null && min.exclusive) min.value else null
        exclusiveMaximum = if (max != null && max.exclusive) max.value else null
    }
}

private fun booleanSchema(
    baseSetter: JsonSchema.Builder.() -> Unit,
) = BooleanSchema {
    baseSetter(this)
}

private fun stringSchema(
    baseSetter: JsonSchema.Builder.() -> Unit,
    meta: List<Annotation>,
    descriptor: SerialDescriptor,
) = StringSchema {
    baseSetter(this)
    minLength = meta.find<MinLength>()?.value
    maxLength = meta.find<MaxLength>()?.value
    pattern = meta.find<Pattern>()?.regex
    format = meta.find<Format>()?.value?.toString()
        ?: meta.find<FormatString>()?.format
        ?: getFallbackFormat(descriptor.serialName)?.toString()
    contentEncoding = meta.find<Encoding>()?.value
    contentMediaType = meta.find<ContentMediaType>()?.value
}

private fun charSchema(
    baseSetter: JsonSchema.Builder.() -> Unit,
    meta: List<Annotation>,
) = StringSchema {
    baseSetter(this)
    minLength = 1
    maxLength = 1
    pattern = meta.find<Pattern>()?.regex
    format = meta.find<Format>()?.value?.toString()
        ?: meta.find<FormatString>()?.format
    contentEncoding = meta.find<Encoding>()?.value
    contentMediaType = meta.find<ContentMediaType>()?.value
}

private fun mapSchema(
    baseSetter: JsonSchema.Builder.() -> Unit
) = ObjectSchema {
    baseSetter(this)
}

private inline fun <reified T : Annotation> List<Annotation>.find(): T? =
    filterIsInstance<T>()
        .firstOrNull()

private fun getFallbackFormat(serialName: String): StringFormat? {
    return when (serialName) {
        "kotlin.uuid.Uuid" -> StringFormat.UUID
        "kotlin.time.Duration" -> StringFormat.DURATION
        "kotlin.time.Instant", "kotlinx.datetime.Instant" -> StringFormat.DATE_TIME
        "kotlinx.datetime.LocalDate" -> StringFormat.DATE
        "kotlinx.datetime.LocalTime" -> StringFormat.TIME
        else -> null
    }
}
