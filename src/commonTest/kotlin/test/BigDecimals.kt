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
