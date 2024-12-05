/*
 * Copyright 2024 Kazimierz Pogoda / Xemantic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xemantic.ai.tool.schema.test

import com.xemantic.ai.tool.schema.meta.Description
import com.xemantic.ai.tool.schema.meta.Pattern
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A monetary amount.
 *
 * Note: this interface will be implemented differently on each platform.
 * In case of `jvm`, it will use `java.math.BigDecimal`, for all the other
 * platforms it will use `com.ionspin.kotlin.bignum.decimal.BigDecimal`.
 *
 * This class represents a typical use case for our test cases.
 */
@Pattern("^-?[0-9]+\\.[0-9]{2}?\$")
@Description("A monetary amount")
@Serializable(MoneySerializer::class)
interface Money {
  operator fun plus(amount: Money): Money
  operator fun compareTo(other: Money): Int
}

expect fun Money(amount: String): Money

object MoneySerializer : KSerializer<Money> {

  // It's a hack to autogenerate a serializer which will retain annotations of serialized class
  @OptIn(ExperimentalSerializationApi::class)
  @Serializer(forClass = Money::class)
  private object AutoSerializer

  @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
  override val descriptor = buildSerialDescriptor(
    serialName = AutoSerializer.descriptor.serialName,
    kind = PrimitiveKind.STRING
  ) {
    annotations = AutoSerializer.descriptor.annotations
  }

  override fun serialize(encoder: Encoder, value: Money) {
    encoder.encodeString(value.toString())
  }

  override fun deserialize(
    decoder: Decoder
  ) = Money(decoder.decodeString())

}
