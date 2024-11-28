# xemantic-ai-tool-schema

A Kotlin multiplatform JSON Schema library. Useful for AI and LLMs'
[tool use](https://docs.anthropic.com/en/docs/build-with-claude/tool-use)
([function calling](https://platform.openai.com/docs/guides/function-calling)),
as it generates JSON Schema for Kotlin `@Serializable` classes.

## Why?

This library was created to fulfill the need of agentic AI projects created by
[xemantic](https://xemantic.com/). In particular:

* [anthropic-sdk-kotlin](https://github.com/xemantic/anthropic-sdk-kotlin) - an unofficial Kotlin multiplatform variant
 of [Anthropic SDK](https://docs.anthropic.com/en/api/client-sdks).
* [claudine](https://github.com/xemantic/claudine) - AI Agent build on top of this SDK.

These projects are heavily dependent on
[tool use](https://docs.anthropic.com/en/docs/build-with-claude/tool-use)
([function calling](https://platform.openai.com/docs/guides/function-calling)) functionality
provided by many Large Language Models. Thanks to `xemantic-ai-tool-schema`, a Kotlin class,
with possible additional constraints, can be automatically instantiated from
the JSON tool use input provided by the LLM. This way any manual steps of defining JSON schema
for the model are avoided, which reduce a chance for errors in the process, and allows to
rapidly develop even complex data structures passed to an AI agent.

In short the `xemantic-ai-tool-schema` library can generate a
[JSON Schema](https://json-schema.org/) from any Kotlin class marked as `@Serializable`,
according to [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html).

> [!TIP]
> You might be familiar with similar functionality of the
> [Pydantic](https://docs.pydantic.dev/latest/concepts/json_schema/#generating-json-schema)
> Python library, however, the standard Kotlin serialization is already fulfilling model
> metadata provisioning, so this analogy might be misleading.

## Usage

In `build.gradle.kts` add:

```kotlin
plugins {
  kotlin("multiplatform") version "2.1.0" // (or jvm for jvm-only project)
  kotlin("plugin.serialization") version "2.1.0"
}

// ...
dependencies {
  implementation("com.xemantic.ai:xemantic-ai-tool-schema:0.1-SNAPSHOT")
}
```

Then in your code you can define entities like this:

```kotlin
@Serializable
@SerialName("address")
@Title("The full address")
@Description("An address of a person or an organization")
data class Address(
  val street: String,
  val city: String,
  @Description("A postal code not limited to particular country")
  @MinLength(3)
  @MaxLength(10)
  val postalCode: String,
  @Pattern("[a-z]{2}")
  val countryCode: String,
  @Format(StringFormat.EMAIL)
  val email: String?
)
```

And when `generateJsonSchema()` function is invoked:

```kotlin
val schema = generateJsonSchema<Address>()
```

, it will produce a [JsonSchema](src/commonMain/kotlin/JsonSchema.kt) instance, which
serializes to:

```json
{
  "type": "object",
  "title": "The full address",
  "description": "An address of a person or an organization",
  "properties": {
    "street": {
      "type": "string"
    },
    "city": {
      "type": "string"
    },
    "postalCode": {
      "type": "string",
      "description": "A postal code not limited to particular country",
      "minLength": 3,
      "maxLength": 10
    },
    "countryCode": {
      "type": "string",
      "pattern": "[a-z]{2}"
    },
    "email": {
      "type": "string",
      "format": "email"
    }
  },
  "required": [
    "street",
    "city",
    "postalCode",
    "countryCode",
    "email"
  ]
}
```

And this is the input accepted by Large Language Model APIs like
[OpenAI API](https://platform.openai.com/docs/api-reference/introduction)
and [Anthropic API](https://docs.anthropic.com/en/api/getting-started)

More details and use cases in the [JsonSchemaTest](src/commonTest/kotlin/JsonSchemaTest.kt).

> [!NOTE]
> When calling `toString()` function on any instance of `JsonSchema`, it will also produce a
> pretty printed `String` representation of a valid JSON schema,
> which in turn describes the Kotlin class as a serialized JSON.
> This functionality is useful for testing and debugging.

## Development



## Non-recommended usage  

> [!WARNING]
> Even though this library provides basic serializable representation of a JSON Schema, it is not
> meant to fully model general purpose JSON Schema. In particular, it should not be used for deserializing
> existing schemas from JSON.
