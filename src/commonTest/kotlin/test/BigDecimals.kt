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

package com.xemantic.ai.tool.schema.test

import com.xemantic.ai.tool.schema.meta.Pattern
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * We are not using Java BigDecimal here, instead we defined a typealias to a multiplatform
 * version of it, and add our own serializer, so we can add our own custom annotations.
 */
typealias BigDecimal =
        @Serializable(BigDecimalSerializer::class)
        com.ionspin.kotlin.bignum.decimal.BigDecimal

object BigDecimalSerializer : KSerializer<BigDecimal> {

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor = buildSerialDescriptor(
        serialName = "BigDecimal",
        kind = PrimitiveKind.STRING
    ) {
        annotations = listOf(Pattern.DECIMAL)
    }

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toString(10))
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal.parseString(decoder.decodeString(), 10)
    }

}
