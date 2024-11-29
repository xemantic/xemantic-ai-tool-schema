package com.xemantic.ai.tool.schema.serialization

import com.xemantic.ai.tool.schema.BaseSchema
import com.xemantic.ai.tool.schema.JsonSchema
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

public object JsonSchemaSerializer : KSerializer<JsonSchema> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
    "JsonSchema"
  )

  override fun serialize(encoder: Encoder, value: JsonSchema) {
    when (value) {
      is JsonSchema.Ref -> {
        encoder.encodeSerializableValue(
          serializer = JsonObject.serializer(),
          value = buildJsonObject {
            put("\$ref", JsonPrimitive(value.ref))
          }
        )
      }
      is BaseSchema -> encoder.encodeSerializableValue(
        serializer = BaseSchema.serializer(),
        value = value
      )
    }
  }

  override fun deserialize(decoder: Decoder): JsonSchema {
    val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException(
      "Can be used only with Json format"
    )
    val json = jsonDecoder.decodeJsonElement().jsonObject
    val ref = json["\$ref"]
    return if (ref != null) {
      JsonSchema.Ref(ref.jsonPrimitive.content)
    } else {
      BaseSchema.serializer().deserialize(decoder)
    }
  }

}
