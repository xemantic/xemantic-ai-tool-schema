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
    additionalProperties: Boolean? = null
): JsonSchema = generateSchema(
    descriptor = serializer<T>().descriptor,
    title,
    description,
    additionalProperties
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
    additionalProperties: Boolean? = null
): JsonSchema = JsonSchemaGenerator(
    additionalProperties
).generate(
    descriptor,
    title,
    description
)

private class JsonSchemaGenerator(
    private val additionalProperties: Boolean? = null
) {

    private lateinit var rootRef: String

    private var recursiveRoot: Boolean = false

    private var trackedRefs = mutableSetOf<String>()

    private val defs: MutableMap<String, ObjectSchema> = mutableMapOf()

    fun generate(
        descriptor: SerialDescriptor,
        title: String? = null,
        description: String? = null,
    ): JsonSchema {

        rootRef = descriptor.refName

        return generatePropertySchema(
            descriptor,
            title,
            description,
            meta = descriptor.annotations, // TODO do we need this meta?
            isRoot = true
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun generatePropertySchema(
        descriptor: SerialDescriptor,
        title: String? = null,
        description: String? = null,
        meta: List<Annotation>,
        isRoot: Boolean
    ): JsonSchema {
        val baseSetter: BaseSchema.Builder.() -> Unit = {
            this.title = if (isRoot) title else null ?: meta.find<Title>()?.value
            this.description = if (isRoot) description else null ?: meta.find<Description>()?.value
        }
        return when (descriptor.kind) {
            PrimitiveKind.STRING -> stringSchema(baseSetter, meta, descriptor)
            PrimitiveKind.INT,
            PrimitiveKind.LONG,
            PrimitiveKind.SHORT,
            PrimitiveKind.BYTE -> integerSchema(baseSetter, meta)
            PrimitiveKind.FLOAT,
            PrimitiveKind.DOUBLE -> numberSchema(baseSetter, meta)
            PrimitiveKind.BOOLEAN -> booleanSchema(baseSetter)
            PrimitiveKind.CHAR -> charSchema(baseSetter, meta)
            SerialKind.ENUM -> enumProperty(baseSetter, meta, descriptor)
            StructureKind.LIST -> arraySchema(baseSetter, meta, descriptor, isRoot) // TODO this isRoot doesn't make much sense for the array
            StructureKind.MAP -> mapSchema(baseSetter)
            StructureKind.CLASS -> objectOrRootRefSchema(baseSetter, descriptor, isRoot)
            else -> mapSchema(baseSetter) // Default case
        }
    }

    private fun objectOrRootRefSchema(
        baseSetter: BaseSchema.Builder.() -> Unit,
        descriptor: SerialDescriptor,
        isRoot: Boolean
    ): JsonSchema {

        val ref = descriptor.refName

        if (!isRoot && ref == rootRef) {
            recursiveRoot = true
        }

        if (ref !in trackedRefs) {
            trackedRefs += ref
            defs[ref] = generateObjectSchema(baseSetter, descriptor, isRoot)
        }

        if (isRoot) {
            if (recursiveRoot) {
                JsonSchema.Ref(
                    ref = "#/definitions/$ref",
                    definitions = defs
                )
            } else {
                defs[ref]!!
            }
        } else {
            JsonSchema.Ref(
                ref = "#/definitions/$ref"
            )
        }

        val props = mutableMapOf<String, JsonSchema>()
        val req = mutableListOf<String>()

        for (i in 0 until descriptor.elementsCount) {
            val elementDescriptor = descriptor.getElementDescriptor(i)
            val name = descriptor.getElementName(i)
            val meta = descriptor.getElementAnnotations(i) + elementDescriptor.annotations
            val property = generatePropertySchema(
                descriptor = elementDescriptor,
                meta = meta,
                isRoot = false
            )
            props[name] = property
            if (!descriptor.isElementOptional(i)) {
                req.add(name)
            }
        }

        return ObjectSchema {
            baseSetter(this)
            properties = props
            definitions = if (isRoot && defs.isNotEmpty()) defs else null
            required = req
            additionalProperties = this@JsonSchemaGenerator.additionalProperties
        }
    }

    private fun objectSchema(
        baseSetter: BaseSchema.Builder.() -> Unit,
        descriptor: SerialDescriptor,
        isRoot: Boolean
    ): JsonSchema {

    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun arraySchema(
        baseSetter: BaseSchema.Builder.() -> Unit,
        meta: List<Annotation>,
        descriptor: SerialDescriptor,
        isRoot: Boolean
    ): JsonSchema {

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
            meta = elementMeta,
            isRoot = false
        )

        return ArraySchema {
            baseSetter(this)
            items = itemSchema
            minItems = meta.find<MinItems>()?.value
            maxItems = meta.find<MaxItems>()?.value
            uniqueItems = if (meta.find<UniqueItems>() != null) true else null
            if (isRoot && defs.isNotEmpty()) {
                // TODO the recursive root should be checked here?
                definitions = defs
            }
        }

    }

}

// Workaround: dots are not allowed in JSON Schema name,
// if the @SerialName was not specified for the class, then fully qualified class name will be used,
// and we need to transform it into schema acceptable identifier
private val SerialDescriptor.refName get() = serialName.replace('.', '_').trimEnd('?')

private fun enumProperty(
    baseSetter: BaseSchema.Builder.() -> Unit,
    meta: List<Annotation>,
    descriptor: SerialDescriptor
) = StringSchema {
    baseSetter(this)
    this.title = title ?: meta.find<Title>()?.value
    this.description = description ?: meta.find<Description>()?.value
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
    baseSetter: BaseSchema.Builder.() -> Unit,
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
    baseSetter: BaseSchema.Builder.() -> Unit,
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
    baseSetter: BaseSchema.Builder.() -> Unit,
) = BooleanSchema {
    baseSetter(this)
}

private fun stringSchema(
    baseSetter: BaseSchema.Builder.() -> Unit,
    meta: List<Annotation>,
    descriptor: SerialDescriptor,
) = StringSchema {
    baseSetter(this)
    minLength = meta.find<MinLength>()?.value
    maxLength = meta.find<MaxLength>()?.value
    pattern = meta.find<Pattern>()?.regex
    format = meta.find<Format>()?.value?.toString()
        ?: meta.find<FormatString>()?.format
                ?: if (descriptor.serialName == "kotlinx.datetime.Instant") {
            StringFormat.DATE_TIME.toString()
        } else null
    contentEncoding = meta.find<Encoding>()?.value
    contentMediaType = meta.find<ContentMediaType>()?.value
}

private fun charSchema(
    baseSetter: BaseSchema.Builder.() -> Unit,
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

// TODO when is it used?
private fun mapSchema(
    baseSetter: BaseSchema.Builder.() -> Unit
) = ObjectSchema {
    baseSetter(this)
}

private inline fun <reified T : Annotation> List<Annotation>.find(): T? =
    filterIsInstance<T>()
        .firstOrNull()
