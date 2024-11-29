package com.xemantic.ai.tool.schema.meta

import com.xemantic.ai.tool.schema.ContentEncoding
import com.xemantic.ai.tool.schema.StringFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MetaSerializable

/**
 * Adds [title](https://json-schema.org/understanding-json-schema/reference/annotations) keyword
 * to generated JSON Schema of the property.
 *
 * A title will preferably be short, whereas a [Description] will provide a more lengthy explanation
 * about the purpose of the data described by the schema.
 *
 * If the [Title] annotation is not found on the property, the JSON Schema generator will
 * attempt to read it from the class of the property.
 *
 * Note: Quite often the property name is already self-explanatory and does not require a title.
 * In practical use cases, the [Description] might be used much more often than [Title].
 *
 * Example usage:
 * ```
 * @Title("User ID")
 * val id: String
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@MetaSerializable
public annotation class Title(
  val value: String
)

/**
 * Adds [description](https://json-schema.org/understanding-json-schema/reference/annotations) keyword
 * to generated JSON Schema of the property.
 *
 * The [Description] provides explanation about the purpose of the data described by the schema
 * (Compare with [Title]).
 *
 * If the [Description] annotation is not found on the property, the JSON Schema generator will
 * attempt to read it from the class of the property.
 *
 * Example usage:
 * ```
 * @Description("A unique identifier for the user")
 * val id: String
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@MetaSerializable
public annotation class Description(
  val value: String
)

// String annotations
/**
 * Minimal length of the string.
 * Will add [minLength](https://json-schema.org/understanding-json-schema/reference/string#length)
 * keyword to generated JSON schema of the `string` property.
 *
 * Usually used in conjunction with [MaxLength].
 *
 * Example usage:
 * ```
 * @MinLength(8)
 * val password: String
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class MinLength(
  val value: Long
)

/**
 * Maximal length of the string.
 * Will add [maxLength](https://json-schema.org/understanding-json-schema/reference/string#length)
 * keyword to generated JSON schema of the `string` property.
 *
 * Usually used in conjunction with [MinLength].
 *
 * Example usage:
 * ```
 * @MaxLength(50)
 * val username: String
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class MaxLength(
  val value: Long
)

/**
 * The regex pattern the string must follow.
 * Will add [pattern](https://json-schema.org/understanding-json-schema/reference/string#regexp)
 * keyword to generated JSON schema of the `string` property.
 *
 * Example usage:
 * ```
 * @Pattern("[A-Z]{2}\\d{9}")
 * val passportNumber: String
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class Pattern(
  val regex: String
) {

  public companion object {

    /**
     * The regular expression pattern of a decimal number.
     *
     * @see DECIMAL
     */
    public const val DECIMAL_REGEX: String = "^-?\\d+(\\.\\d+)?$"

    /**
     * The default instance of the Pattern annotation for decimal number.
     *
     * @see DECIMAL_REGEX
     */
    public val DECIMAL: Pattern = Pattern(DECIMAL_REGEX)

  }

}

/**
 * The format the string must follow.
 * Will add [format](https://json-schema.org/understanding-json-schema/reference/string#format)
 * keyword to generated JSON schema of the `string` property.
 *
 * Note: the format keyword is provided as a string, and this annotation accepts the
 * [StringFormat] enum of predefined formats. It is possible that a non-standard format,
 * not defined in this enum will be used. In such case rather use [StringFormat]
 * annotation.
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class Format(
  val format: StringFormat
)

/**
 * The format the string must follow.
 * Will add [format](https://json-schema.org/understanding-json-schema/reference/string#format)
 * keyword to generated JSON schema of the `string` property.
 *
 * Note: this annotation should be used only for non-standard formats, which are not
 * covered by the [StringFormat] enum. For standard formats use the [Format] annotation.
 *
 * Example usage:
 * ```
 * @FormatString("color")
 * val favoriteColor: String
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class FormatString(
  val format: String
)

/**
 * The encoding of the string.
 * Will add [contentEncoding](https://json-schema.org/understanding-json-schema/reference/non_json_data)
 * keyword to generated JSON schema of the `string` property.
 *
 * See [ContentEncoding] enum for possible types. It should be used together with [ContentMediaType].
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class Encoding(
  val value: ContentEncoding
)

/**
 * The content media type of the string.
 * Will add [contentMediaType](https://json-schema.org/understanding-json-schema/reference/non_json_data)
 * keyword to generated JSON schema of the `string` property.
 *
 * It should be used together with [ContentEncoding].
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class ContentMediaType(
  val value: String
)

// number annotations
/**
 * The minimal value of the number.
 * Will add [minimum](https://json-schema.org/understanding-json-schema/reference/numeric#range)
 * keyword to generated JSON schema of the `number` property. If [exclusive] flag is `true`,
 * then `exclusiveMinimum` keywords will be used instead.
 *
 * Usually it will be used together with [Max].
 *
 * @param value The minimum value allowed.
 * @param exclusive Whether the minimum is exclusive (default is false).
 *
 * Example usage:
 * ```
 * @Min(0.0)
 * val positiveNumber: Double
 *
 * @Min(1.0, exclusive = true)
 * val greaterThanOne: Double
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class Min(
  val value: Double,
  val exclusive: Boolean = false
)

/**
 * The maximal value of the number.
 * Will add [maximum](https://json-schema.org/understanding-json-schema/reference/numeric#range)
 * keyword to generated JSON schema of the `number` property. If [exclusive] flag is `true`,
 * then `exclusiveMaximum` keywords will be used instead.
 *
 * Usually it will be used together with [Min].
 *
 * @param value The maximum value allowed.
 * @param exclusive Whether the maximum is exclusive (default is false).
 *
 * Example usage:
 * ```
 * @Max(100.0)
 * val percentage: Double
 *
 * @Max(1.0, exclusive = true)
 * val lessThanOne: Double
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class Max(
  val value: Double,
  val exclusive: Boolean = false
)

/**
 * The `multiple of` property of the number.
 * Will add [multipleOf](https://json-schema.org/understanding-json-schema/reference/numeric#multiples)
 * keyword to generated JSON schema of the `number` property.
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class MultipleOf(
  val value: Double
)

// integer annotations
/**
 * The minimal value of the integer.
 * Will add [minimum](https://json-schema.org/understanding-json-schema/reference/numeric#range)
 * keyword to generated JSON schema of the `integer` property. If [exclusive] flag is `true`,
 * then `exclusiveMinimum` keywords will be used instead.
 *
 * Usually it will be used together with [MaxInt].
 *
 * Note: Use this for integer properties. For floating-point numbers, use [Min] instead.
 *
 * @param value The minimum value allowed.
 * @param exclusive Whether the minimum is exclusive (default is false).
 *
 * Example usage:
 * ```
 * @MinInt(0)
 * val nonNegativeInt: Int
 *
 * @MinInt(1, exclusive = true)
 * val positiveInt: Int
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class MinInt(
  val value: Long,
  val exclusive: Boolean = false
)

/**
 * The minimal value of the integer.
 * Will add [maximum](https://json-schema.org/understanding-json-schema/reference/numeric#range)
 * keyword to generated JSON schema of the `integer` property. If [exclusive] flag is `true`,
 * then `exclusiveMaximum` keywords will be used instead.
 *
 * Usually it will be used together with [MinInt].
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class MaxInt(
  val value: Long,
  val exclusive: Boolean = false
)

/**
 * The `multiple of` property of the integer.
 * Will add [multipleOf](https://json-schema.org/understanding-json-schema/reference/numeric#multiples)
 * keyword to generated JSON schema of the `integer` property.
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class MultipleOfInt(
  val value: Long
)

// Array annotations
/**
 * The minimum number of items property of the array.
 * Will add [minItems](https://json-schema.org/understanding-json-schema/reference/array#length)
 * keyword to generated JSON schema of the `array` property.
 *
 * Usually used in conjunction with [MaxItems].
 *
 * Example usage:
 * ```
 * @MinItems(1)
 * val nonEmptyList: List<String>
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class MinItems(
  val value: Long
)

/**
 * The maximum number of items property of the array.
 * Will add [maxItems](https://json-schema.org/understanding-json-schema/reference/array#length)
 * keyword to generated JSON schema of the `array` property.
 *
 * Usually used in conjunction with [MinItems].
 *
 * Example usage:
 * ```
 * @MaxItems(10)
 * val limitedList: List<String>
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class MaxItems(
  val value: Long
)

/**
 * The uniqueness of array items.
 * Will add [uniqueItems](https://json-schema.org/understanding-json-schema/reference/array#uniqueItems)
 * keyword to generated JSON schema of the `array` property.
 *
 * When set to true, all items in the array must be unique.
 * Uniqueness is determined by comparing the JSON representation of each item.
 *
 * Example usage:
 * ```
 * @UniqueItems(true)
 * val tags: List<String>
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.PROPERTY)
@MetaSerializable
public annotation class UniqueItems(
  val value: Boolean
)
