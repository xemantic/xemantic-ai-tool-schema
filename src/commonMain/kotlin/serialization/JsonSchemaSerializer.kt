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

package com.xemantic.ai.tool.schema.serialization

import com.xemantic.ai.tool.schema.BaseSchema
import com.xemantic.ai.tool.schema.JsonSchema
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

public object JsonSchemaSerializer : KSerializer<JsonSchema> {

    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor(
        serialName = "JsonSchema",
        kind = PolymorphicKind.SEALED
    )

    override fun serialize(encoder: Encoder, value: JsonSchema) {
        when (value) {
            is JsonSchema.Ref -> encoder.encodeSerializableValue(
                serializer = JsonSchema.Ref.serializer(),
                value = value
            )
            is JsonSchema.Const -> encoder.encodeSerializableValue(
                serializer = JsonSchema.Const.serializer(),
                value = value
            )
            is BaseSchema -> encoder.encodeSerializableValue(
                serializer = BaseSchema.serializer(),
                value = value
            )
        }
    }

    override fun deserialize(decoder: Decoder): JsonSchema {
        val input = decoder as? JsonDecoder ?: throw SerializationException(
            "Can be used only with Json format"
        )
        val tree = input.decodeJsonElement()
        val json = tree.jsonObject
        val ref = json[$$"$ref"]
        return if (ref != null) {
            input.json.decodeFromJsonElement(JsonSchema.Ref.serializer(), tree)
        } else {
            input.json.decodeFromJsonElement(BaseSchema.serializer(), tree)
        }
    }

}
