package com.xemantic.ai.tool.schema

import com.xemantic.ai.tool.schema.serialization.JsonSchemaSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable(with = JsonSchemaSerializer::class)
public sealed interface JsonSchema {

  public class Ref(
    @SerialName("\$ref")
    public val ref: String
  ) : JsonSchema {

    init {
      require(ref.startsWith("#/")) {
        "The 'ref' must start with '#/'"
      }
    }

    override fun toString(): String = """{"${'$'}ref": "$ref"}"""

  }

}

@Serializable
public sealed class BaseSchema(
) : JsonSchema {

  public abstract val title: String?
  public abstract val description: String?

  public open class Builder {
    public var title: String? = null
    public var description: String? = null
  }

  override fun toString(): String =
    jsonSchemaToStringJson.encodeToString(this)

}

/**
 * Schema for JSON objects.
 */
@Serializable
@SerialName("object")
public class ObjectSchema private constructor(
  override val title: String? = null,
  override val description: String? = null,
  public val properties: Map<String, JsonSchema>? = null,
  public val required: List<String>? = null,
  public val definitions: Map<String, JsonSchema>? = null,
  public val additionalProperties: Boolean? = null
) : BaseSchema() {

  public class Builder : BaseSchema.Builder() {

    public var properties: Map<String, JsonSchema>? = null
    public var required: List<String>? = null
    public var definitions: Map<String, JsonSchema>? = null
    public var additionalProperties: Boolean? = null

    public fun build(): ObjectSchema = ObjectSchema(
      title,
      description,
      properties,
      required,
      definitions,
      additionalProperties
    )

  }

}

public fun ObjectSchema(
  block: ObjectSchema.Builder.() -> Unit
): ObjectSchema = ObjectSchema.Builder().also(block).build()

/**
 * Schema for JSON arrays
 */
@Serializable
@SerialName("array")
public class ArraySchema private constructor(
  override val title: String? = null,
  override val description: String? = null,
  public val items: JsonSchema,
  public val minItems: Long? = null,
  public val maxItems: Long? = null,
  public val uniqueItems: Boolean? = null,
) : BaseSchema() {

  public class Builder : BaseSchema.Builder() {

    public var items: JsonSchema? = null
    public var minItems: Long? = null
    public var maxItems: Long? = null
    public var uniqueItems: Boolean? = null

    public fun build(): ArraySchema = ArraySchema(
      title,
      description,
      requireNotNull(items) {
        "cannot build ArraySchema without 'items' property"
      },
      minItems,
      maxItems,
      uniqueItems
    )

  }

}

public fun ArraySchema(
  block: ArraySchema.Builder.() -> Unit
): ArraySchema = ArraySchema.Builder().also(block).build()

/**
 * Schema for strings
 */
@Serializable
@SerialName("string")
public class StringSchema private constructor(
  override val title: String? = null,
  override val description: String? = null,
  public val enum: List<String>? = null,
  public val minLength: Long? = null,
  public val maxLength: Long? = null,
  public val pattern: String? = null,
  public val format: String? = null,
) : BaseSchema() {

  public class Builder : BaseSchema.Builder() {

    public var enum: List<String>? = null
    public var minLength: Long? = null
    public var maxLength: Long? = null
    public var pattern: String? = null
    public var format: String? = null

    public fun format(format: StringFormat) {
      this.format = format.toString()
    }

    public fun build(): StringSchema = StringSchema(
      title,
      description,
      enum,
      minLength,
      maxLength,
      pattern,
      format
    )

  }

}

public fun StringSchema(
  block: StringSchema.Builder.() -> Unit
): StringSchema = StringSchema.Builder().also(block).build()

public interface NumericSchema<T> {

  public val minimum: T?
  public val maximum: T?
  public val exclusiveMinimum: T?
  public val exclusiveMaximum: T?
  public val multipleOf: T?

  public open class Builder<T> : BaseSchema.Builder() {
    public var minimum: T? = null
    public var maximum: T? = null
    public var exclusiveMinimum: T? = null
    public var exclusiveMaximum: T? = null
    public var multipleOf: T? = null
  }

}

/**
 * Schema for numbers (both integer and decimal)
 */
@Serializable
@SerialName("number")
public class NumberSchema private constructor(
  override val title: String? = null,
  override val description: String? = null,
  override val minimum: Double? = null,
  override val maximum: Double? = null,
  override val exclusiveMinimum: Double? = null,
  override val exclusiveMaximum: Double? = null,
  override val multipleOf: Double? = null,
) : BaseSchema(), NumericSchema<Double> {

  public class Builder : NumericSchema.Builder<Double>() {

    public fun build(): NumberSchema = NumberSchema(
      title,
      description,
      minimum,
      maximum,
      exclusiveMinimum,
      exclusiveMaximum,
      multipleOf
    )

  }

}

public fun NumberSchema(
  block: NumberSchema.Builder.() -> Unit
): NumberSchema = NumberSchema.Builder().also(block).build()

/**
 * Schema specifically for integers
 */
@Serializable
@SerialName("integer")
public class IntegerSchema private constructor(
  override val title: String? = null,
  override val description: String? = null,
  override val minimum: Long? = null,
  override val maximum: Long? = null,
  override val exclusiveMinimum: Long? = null,
  override val exclusiveMaximum: Long? = null,
  override val multipleOf: Long? = null,
) : BaseSchema(), NumericSchema<Long> {

  public class Builder : NumericSchema.Builder<Long>() {

    public fun build(): IntegerSchema = IntegerSchema(
      title,
      description,
      minimum,
      maximum,
      exclusiveMinimum,
      exclusiveMaximum,
      multipleOf
    )

  }

}

public fun IntegerSchema(
  block: IntegerSchema.Builder.() -> Unit
): IntegerSchema = IntegerSchema.Builder().also(block).build()

/**
 * Schema for boolean values
 */
@Serializable
@SerialName("boolean")
public class BooleanSchema private constructor(
  override val title: String? = null,
  override val description: String? = null,
) : BaseSchema() {

  public class Builder : BaseSchema.Builder() {

    public fun build(): BooleanSchema = BooleanSchema(
      title,
      description
    )

  }

}

public fun BooleanSchema(
  block: BooleanSchema.Builder.() -> Unit
): BooleanSchema = BooleanSchema.Builder().also(block).build()

/**
 * Json Schema [`string` formats](https://json-schema.org/understanding-json-schema/reference/string#format).
 */
public enum class StringFormat {

  // Dates and times
  /**
   * A `date-time` format.
   *
   * E.g., `2018-11-13T20:20:39+00:00`.
   */
  DATE_TIME,

  /**
   * A `time` format.
   *
   * E.g., `20:20:39+00:00`.
   */
  TIME,

  /**
   * A `date` format.
   *
   * E.g., `2018-11-13`.
   */
  DATE,

  /**
   * A `duration` format.
   *
   * E.g., `P3D`.
   * See [ISO 8601 ABNF for `duration`](https://datatracker.ietf.org/doc/html/rfc3339#appendix-A).
   */
  DURATION,

  // Email addresses
  /**
   * An `email` format.
   *
   * See [RFC 5321, section 4.1.2](https://datatracker.ietf.org/doc/html/rfc5321#section-4.1.2).
   */
  EMAIL,

  /**
   * An `idn-email` format.
   *
   * See [RFC 6531](https://datatracker.ietf.org/doc/html/rfc6531).
   */
  IDN_EMAIL,

  // Hostnames
  /**
   * A `hostname` format.
   *
   * See [RFC 1123, section 2.1](https://datatracker.ietf.org/doc/html/rfc1123#section-2.1)
   */
  HOSTNAME,

  /**
   * An `idn-hostname` format.
   *
   * See [RFC5890, section 2.3.2.3](https://datatracker.ietf.org/doc/html/rfc5890#section-2.3.2.3)
   */
  IDN_HOSTNAME,

  // IP Addresses
  /**
   * An `ipv4` format.
   *
   * IPv4 address, according to dotted-quad ABNF syntax as defined in
   * [RFC 2673, section 3.2](https://datatracker.ietf.org/doc/html/rfc2673#section-3.2)
   */
  IPV4,

  /**
   * An `ipv6` format.
   *
   * IPv6 address, as defined in
   * [RFC 2373, section 2.2](http://tools.ietf.org/html/rfc2373#section-2.2)
   */
  IPV6,

  // Resource identifiers
  /**
   * A `uuid` format.
   *
   * IPv6 address, as defined in
   * [RFC 2373, section 2.2](http://tools.ietf.org/html/rfc2373#section-2.2)
   */
  UUID,


  URI,
  URI_REFERENCE,
  IRI,
  IRI_REFERENCE,

  URI_TEMPLATE,

  JSON_POINTER,
  RELATIVE_JSON_POINTER,  // e.g., "2/foo"

  REGEX;

  override fun toString(): String = name.lowercase().replace('_', '-')

}

private val jsonSchemaToStringJson = Json {
  prettyPrint = true
  @OptIn(ExperimentalSerializationApi::class)
  prettyPrintIndent = "  "
}
