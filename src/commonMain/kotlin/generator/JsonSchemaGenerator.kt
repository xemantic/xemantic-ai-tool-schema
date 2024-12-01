package com.xemantic.ai.tool.schema.generator

import com.xemantic.ai.tool.schema.ArraySchema
import com.xemantic.ai.tool.schema.BooleanSchema
import com.xemantic.ai.tool.schema.IntegerSchema
import com.xemantic.ai.tool.schema.JsonSchema
import com.xemantic.ai.tool.schema.NumberSchema
import com.xemantic.ai.tool.schema.ObjectSchema
import com.xemantic.ai.tool.schema.StringFormat
import com.xemantic.ai.tool.schema.StringSchema
import com.xemantic.ai.tool.schema.meta.ContentMediaType
import com.xemantic.ai.tool.schema.meta.Description
import com.xemantic.ai.tool.schema.meta.Encoding
import com.xemantic.ai.tool.schema.meta.Format
import com.xemantic.ai.tool.schema.meta.FormatString
import com.xemantic.ai.tool.schema.meta.ItemDescription
import com.xemantic.ai.tool.schema.meta.ItemTitle
import com.xemantic.ai.tool.schema.meta.Max
import com.xemantic.ai.tool.schema.meta.MaxInt
import com.xemantic.ai.tool.schema.meta.MaxItems
import com.xemantic.ai.tool.schema.meta.MaxLength
import com.xemantic.ai.tool.schema.meta.Min
import com.xemantic.ai.tool.schema.meta.MinInt
import com.xemantic.ai.tool.schema.meta.MinItems
import com.xemantic.ai.tool.schema.meta.MinLength
import com.xemantic.ai.tool.schema.meta.Pattern
import com.xemantic.ai.tool.schema.meta.Title
import com.xemantic.ai.tool.schema.meta.UniqueItems
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlin.collections.set

public inline fun <reified T> jsonSchemaOf(
  outputAdditionalPropertiesFalse: Boolean = false
): JsonSchema = generateSchema(
  descriptor = serializer<T>().descriptor,
  outputAdditionalPropertiesFalse = outputAdditionalPropertiesFalse
)

@OptIn(ExperimentalSerializationApi::class)
public fun generateSchema(
  descriptor: SerialDescriptor,
  outputAdditionalPropertiesFalse: Boolean = false
): JsonSchema {

  val props = mutableMapOf<String, JsonSchema>()
  val req = mutableListOf<String>()
  val defs = mutableMapOf<String, JsonSchema>()

  for (i in 0 until descriptor.elementsCount) {
    val elementDescriptor = descriptor.getElementDescriptor(i)
    val name = descriptor.getElementName(i)
    val meta = descriptor.getElementAnnotations(i) + elementDescriptor.annotations
    val property = generatePropertySchema(
      descriptor = elementDescriptor,
      meta = meta,
      defs = defs,
      outputAdditionalPropertiesFalse = outputAdditionalPropertiesFalse
    )
    props[name] = property
    if (!descriptor.isElementOptional(i)) {
      req.add(name)
    }
  }

  return ObjectSchema {
    title = descriptor.annotations.find<Title>()?.value
    description = descriptor.annotations.find<Description>()?.value
    properties = props
    definitions = if (defs.isNotEmpty()) defs else null
    required = req
    additionalProperties = if (outputAdditionalPropertiesFalse) false else null
  }
}

@OptIn(ExperimentalSerializationApi::class)
private fun generatePropertySchema(
  descriptor: SerialDescriptor,
  meta: List<Annotation>,
  defs: MutableMap<String, JsonSchema>,
  outputAdditionalPropertiesFalse: Boolean
): JsonSchema {
  return when (descriptor.kind) {
    PrimitiveKind.STRING -> stringSchema(meta, descriptor)
    PrimitiveKind.INT, PrimitiveKind.LONG -> integerSchema(meta)
    PrimitiveKind.FLOAT, PrimitiveKind.DOUBLE -> numberSchema(meta)
    PrimitiveKind.BOOLEAN -> booleanSchema(meta)
    SerialKind.ENUM -> enumProperty(meta, descriptor)
    StructureKind.LIST -> arraySchema(meta, descriptor, defs, outputAdditionalPropertiesFalse)
    StructureKind.MAP -> objectSchema(meta)
    StructureKind.CLASS -> {
      // Workaround: dots are not allowed in JSON Schema name,
      // if the @SerialName was not specified for the class, then fully qualified class name will be used,
      // and we need to transform it into schema acceptable identifier
      val refName = descriptor.serialName.replace('.', '_').trimEnd('?')
      defs[refName] = generateSchema(descriptor, outputAdditionalPropertiesFalse)
      JsonSchema.Ref("#/definitions/$refName")
    }
    else -> objectSchema(meta) // Default case
  }
}

private fun enumProperty(
  meta: List<Annotation>,
  descriptor: SerialDescriptor
) = StringSchema {
  title = meta.find<Title>()?.value
  description = meta.find<Description>()?.value
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
  meta: List<Annotation>
): JsonSchema {
  val min = meta.find<MinInt>()
  val max = meta.find<MaxInt>()
  return IntegerSchema {
    title = meta.find<Title>()?.value
    description = meta.find<Description>()?.value
    minimum = if (min != null && !min.exclusive) min.value else null
    maximum = if (max != null && !max.exclusive) max.value else null
    exclusiveMinimum = if (min != null && min.exclusive) min.value else null
    exclusiveMaximum = if (max != null && max.exclusive) max.value else null
  }
}

private fun numberSchema(
  meta: List<Annotation>
): JsonSchema {
  val min = meta.find<Min>()
  val max = meta.find<Max>()
  return NumberSchema {
    title = meta.find<Title>()?.value
    description = meta.find<Description>()?.value
    minimum = if (min != null && !min.exclusive) min.value else null
    maximum = if (max != null && !max.exclusive) max.value else null
    exclusiveMinimum = if (min != null && min.exclusive) min.value else null
    exclusiveMaximum = if (max != null && max.exclusive) max.value else null
  }
}

private fun booleanSchema(
  meta: List<Annotation>
) = BooleanSchema {
  title = meta.find<Title>()?.value
  description = meta.find<Description>()?.value
}

@OptIn(ExperimentalSerializationApi::class)
private fun stringSchema(
  meta: List<Annotation>,
  descriptor: SerialDescriptor
) = StringSchema {
  title = meta.find<Title>()?.value
  description = meta.find<Description>()?.value
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

private fun objectSchema(
  meta: List<Annotation>
) = ObjectSchema {
  title = meta.find<Title>()?.value
  description = meta.find<Description>()?.value
}

@OptIn(ExperimentalSerializationApi::class)
private fun arraySchema(
  meta: List<Annotation>,
  descriptor: SerialDescriptor,
  defs: MutableMap<String, JsonSchema>,
  outputAdditionalPropertiesFalse: Boolean
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
    defs = defs,
    outputAdditionalPropertiesFalse = outputAdditionalPropertiesFalse
  )

  return ArraySchema {
    title = meta.find<Title>()?.value
    description = meta.find<Description>()?.value
    items = itemSchema
    minItems = meta.find<MinItems>()?.value
    maxItems = meta.find<MaxItems>()?.value
    uniqueItems = if(meta.find<UniqueItems>() != null) true else null
  }

}

private inline fun <reified T : Annotation> List<Annotation>.find(): T? =
  filterIsInstance<T>()
    .firstOrNull()
