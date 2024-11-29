package com.xemantic.ai.tool.schema.serialization

import com.xemantic.ai.tool.schema.meta.Description
import com.xemantic.ai.tool.schema.meta.Pattern
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

/**
 * A serializer of the `java.math.BigDecimal`.
 *
 * It adds 2 standard annotations:
 * ```
 * @Description("A decimal number")
 * @Pattern("^-?\\d+(\\.\\d+)?$")
 * class BigDecimal
 * ```
 *
 * This serializer can be applied conveniently by putting:
 * ```
 * @file:UseSerializers(BigDecimalSerializer::class)`
 * ```
 * at the top of the file containing classes with [BigDecimal] fields.
 *
 * Note: the serializer will encode any [BigDecimal] value as
 * a plain decimal number string, and expect such a string when decoding.
 */
public object BigDecimalSerializer : KSerializer<BigDecimal> {

  @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
  public override val descriptor: SerialDescriptor = buildSerialDescriptor(
    serialName = "BigDecimal",
    kind = PrimitiveKind.STRING
  ) {
    annotations = listOf(
      Description("A decimal number"),
      Pattern.DECIMAL
    )
  }

  override fun serialize(encoder: Encoder, value: BigDecimal) {
    encoder.encodeString(value.stripTrailingZeros().toPlainString())
  }

  override fun deserialize(
    decoder: Decoder
  ): BigDecimal = BigDecimal(decoder.decodeString())

}
