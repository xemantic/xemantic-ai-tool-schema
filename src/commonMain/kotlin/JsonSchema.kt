package com.xemantic.ai.tool.schema

import com.xemantic.ai.tool.schema.serialization.JsonSchemaSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Represents a [JSON Schema](https://json-schema.org/) instance.
 */
@Serializable(with = JsonSchemaSerializer::class)
public sealed interface JsonSchema {

  /**
   * Refers another [JsonSchema] through [JSON Pointer](https://datatracker.ietf.org/doc/html/rfc6901).
   *
   * @param ref a string representing JSON Pointer. Note: in JSON it will be serialized as `$ref`.
   * @throws IllegalArgumentException if the [ref] does not start with `#/`.
   */
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


public enum class ContentEncoding {
  @SerialName("quoted-printable")
  QUOTED_PRINTABLE,
  @SerialName("base16")
  BASE16,
  @SerialName("base32")
  BASE32,
  @SerialName("base64")
  BASE64
}

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
  public val contentEncoding: ContentEncoding? = null,
  public val contentMediaType: String? = null
) : BaseSchema() {

  public class Builder : BaseSchema.Builder() {

    public var enum: List<String>? = null
    public var minLength: Long? = null
    public var maxLength: Long? = null
    public var pattern: String? = null
    public var format: String? = null
    public var contentMediaType: String? = null
    public var contentEncoding: ContentEncoding? = null

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
      format,
      contentEncoding,
      contentMediaType
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
   * A Universally Unique Identifier as defined by [RFC 4122](https://datatracker.ietf.org/doc/html/rfc4122).
   * Example: `3e4666bf-d5e5-4aa7-b8ce-cefe41c7568a`
   */
  UUID,

  /**
   * A `uri` format.
   *
   * A universal resource identifier (URI), according to [RFC3986](http://tools.ietf.org/html/rfc3986).
   */
  URI,

  /**
   * A `uri-reference` format.
   *
   * A URI Reference (either a URI or a relative-reference), according to
   * [RFC3986, section 4.1](http://tools.ietf.org/html/rfc3986#section-4.1).
   */
  URI_REFERENCE,

  /**
   * An `iri` format.
   *
   * The internationalized equivalent of a "uri", according to [RFC3987](https://tools.ietf.org/html/rfc3987).
   */
  IRI,

  /**
   * An `iri-reference` format.
   *
   * The internationalized equivalent of a "uri-reference", according to [RFC3987](https://tools.ietf.org/html/rfc3987).
   */
  IRI_REFERENCE,

  /**
   * A `uri-template` format.
   *
   * A URI Template (of any level) according to [RFC6570](https://tools.ietf.org/html/rfc6570).
   */
  URI_TEMPLATE,

  /**
   * A `json-pointer` format.
   *
   * A JSON Pointer, according to [RFC6901](https://tools.ietf.org/html/rfc6901).
   * Should be used only when the entire string contains only JSON Pointer content, e.g. `/foo/bar`.
   * JSON Pointer URI fragments, e.g. `#/foo/bar/` should use `URI_REFERENCE`.
   */
  JSON_POINTER,

  /**
   * A `relative-json-pointer` format.
   *
   * A [relative JSON pointer](https://tools.ietf.org/html/draft-handrews-relative-json-pointer-01).
   */
  RELATIVE_JSON_POINTER,

  /**
   * A `regex` format.
   *
   * A regular expression, which should be valid according to the
   * [ECMA 262](https://www.ecma-international.org/publications-and-standards/standards/ecma-262/) dialect.
   */
  REGEX,

  // Unofficial formats
  /**
   * An unofficial `color` format.
   *
   * Represents a color in hexadecimal format (e.g., "#RRGGBB").
   */
  COLOR,

  /**
   * An unofficial `phone` format.
   *
   * Represents a phone number. The exact format may vary depending on the implementation.
   */
  PHONE,

  /**
   * An unofficial `credit-card` format.
   *
   * Represents a credit card number.
   */
  CREDIT_CARD,

  /**
   * An unofficial `isbn` format.
   *
   * Represents an International Standard Book Number.
   */
  ISBN,

  /**
   * An unofficial `currency` format.
   *
   * Represents a currency code (e.g., "USD", "EUR").
   */
  CURRENCY,

  /**
   * An unofficial `binary` format.
   *
   * Represents binary data, typically base64-encoded.
   */
  BINARY,

  /**
   * An unofficial `md5` format.
   *
   * Represents an MD5 hash.
   */
  MD5,

  /**
   * An unofficial `sha1` format.
   *
   * Represents a SHA-1 hash.
   */
  SHA1,

  /**
   * An unofficial `sha256` format.
   *
   * Represents a SHA-256 hash.
   */
  SHA256,

  /**
   * An unofficial `country-code` format.
   *
   * Represents a country code (e.g., "US", "GB").
   */
  COUNTRY_CODE,

  /**
   * An unofficial `language-code` format.
   *
   * Represents a language code (e.g., "en", "fr").
   */
  LANGUAGE_CODE;

  override fun toString(): String = name.lowercase().replace('_', '-')

}

private val jsonSchemaToStringJson = Json {
  prettyPrint = true
  @OptIn(ExperimentalSerializationApi::class)
  prettyPrintIndent = "  "
}
