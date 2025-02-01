# xemantic-ai-tool-schema

AI/LLM [tool use](https://docs.anthropic.com/en/docs/build-with-claude/tool-use) ([function calling](https://platform.openai.com/docs/guides/function-calling)) JSON Schema generator - a Kotlin multiplatform library
it generates JSON Schema for Kotlin `@Serializable` classes.

[<img alt="Maven Central Version" src="https://img.shields.io/maven-central/v/com.xemantic.ai/xemantic-ai-tool-schema">](https://central.sonatype.com/artifact/com.xemantic.ai/xemantic-ai-tool-schema)
[<img alt="GitHub Release Date" src="https://img.shields.io/github/release-date/xemantic/xemantic-ai-tool-schema">](https://github.com/xemantic/xemantic-ai-tool-schema/releases)
[<img alt="license" src="https://img.shields.io/github/license/xemantic/xemantic-ai-tool-schema?color=blue">](https://github.com/xemantic/xemantic-ai-tool-schema/blob/main/LICENSE)

[<img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/xemantic/xemantic-ai-tool-schema/build-main.yml">](https://github.com/xemantic/xemantic-ai-tool-schema/actions/workflows/build-main.yml)
[<img alt="GitHub branch check runs" src="https://img.shields.io/github/check-runs/xemantic/xemantic-ai-tool-schema/main">](https://github.com/xemantic/xemantic-ai-tool-schema/actions/workflows/build-main.yml)
[<img alt="GitHub commits since latest release" src="https://img.shields.io/github/commits-since/xemantic/xemantic-ai-tool-schema/latest">](https://github.com/xemantic/xemantic-ai-tool-schema/commits/main/)
[<img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/xemantic/xemantic-ai-tool-schema">](https://github.com/xemantic/xemantic-ai-tool-schema/commits/main/)

[<img alt="GitHub contributors" src="https://img.shields.io/github/contributors/xemantic/xemantic-ai-tool-schema">](https://github.com/xemantic/xemantic-ai-tool-schema/graphs/contributors)
[<img alt="GitHub commit activity" src="https://img.shields.io/github/commit-activity/t/xemantic/xemantic-ai-tool-schema">](https://github.com/xemantic/xemantic-ai-tool-schema/commits/main/)
[<img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/xemantic/xemantic-ai-tool-schema">]()
[<img alt="GitHub Created At" src="https://img.shields.io/github/created-at/xemantic/xemantic-ai-tool-schema">](https://github.com/xemantic/xemantic-ai-tool-schema/commits)
[<img alt="kotlin version" src="https://img.shields.io/badge/dynamic/toml?url=https%3A%2F%2Fraw.githubusercontent.com%2Fxemantic%2Fxemantic-ai-tool-schema%2Fmain%2Fgradle%2Flibs.versions.toml&query=versions.kotlin&label=kotlin">](https://kotlinlang.org/docs/releases.html)
[<img alt="discord users online" src="https://img.shields.io/discord/811561179280965673">](https://discord.gg/vQktqqN2Vn)
[![Bluesky](https://img.shields.io/badge/Bluesky-0285FF?logo=bluesky&logoColor=fff)](https://bsky.app/profile/xemantic.com)

## Why?

This library was created to fulfill the need of agentic AI projects created by [xemantic](https://xemantic.com/). In particular:

* [anthropic-sdk-kotlin](https://github.com/xemantic/anthropic-sdk-kotlin) - an unofficial Kotlin multiplatform variant of [Anthropic SDK](https://docs.anthropic.com/en/api/client-sdks).
* [claudine](https://github.com/xemantic/claudine) - AI Agent build on top of this SDK.

These projects are heavily dependent on [tool use](https://docs.anthropic.com/en/docs/build-with-claude/tool-use) ([function calling](https://platform.openai.com/docs/guides/function-calling)) functionality provided by many Large Language Models. Thanks to `xemantic-ai-tool-schema`, a Kotlin class, with possible additional constraints, can be automatically instantiated from the JSON tool use input provided by the LLM. This way any manual steps of defining JSON schema for the model are avoided, which reduce a chance for errors in the process, and allows to rapidly develop even complex data structures passed to an AI agent.

In short the `xemantic-ai-tool-schema` library can generate a [JSON Schema](https://json-schema.org/) from any Kotlin class marked as `@Serializable`, according to [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html).

> [!TIP]
> You might be familiar with similar functionality of the [Pydantic](https://docs.pydantic.dev/latest/concepts/json_schema/#generating-json-schema) Python library, however, the standard Kotlin serialization is already fulfilling model metadata provisioning, so this analogy might be misleading.

## Usage

In `build.gradle.kts` add:

```kotlin
plugins {
    kotlin("multiplatform") version "2.1.0" // (or jvm for jvm-only project)
    kotlin("plugin.serialization") version "2.1.0"
}

// ...
dependencies {
    implementation("com.xemantic.ai:xemantic-ai-tool-schema:0.2.1")
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
    val email: String? = null
)
```

And when `jsonSchemaOf()` function is invoked:

```kotlin
val schema = jsonSchemaOf<Address>()
```

It will produce a [JsonSchema](src/commonMain/kotlin/JsonSchema.kt) instance, which serializes to:

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
    "countryCode"
  ]
}
```

And this is the input accepted by Large Language Model APIs like [OpenAI API](https://platform.openai.com/docs/api-reference/introduction) and [Anthropic API](https://docs.anthropic.com/en/api/getting-started).
When requesting a tool use, these LLMs will send a JSON payload adhering to this schema, therefore immediately deserializable as the original `@Serializable` Kotlin class.

More details and use cases in the [JsonSchemaGeneratorTest](src/commonTest/kotlin/generator/JsonSchemaGeneratorTest.kt).

> [!NOTE]
> When calling `toString()` function on any instance of `JsonSchema`, it will also produce a pretty printed `String` representation of a valid JSON schema, which in turn describes the Kotlin class as a serialized JSON. This functionality is useful for testing and debugging.

### Serializing Java `BigDecimal`s

For JVM-only projects, it is possible to specify `java.math.BigDecimal` serialization. It will serialize decimal numbers to strings, and add `description` and `pattern` properties to generated JSON Schema of a `BigDecimal` property.

See [JavaBigDecimalToSchemaTest](src/jvmTest/kotlin/serialization/JavaBigDecimalToSchemaTest.kt) for details.

### Serializing BigDecimal/monetary values in multiplatform way

There is an interface called [Money](src/commonTest/kotlin/test/Money.kt) defined in the tests of this project. It explains how to define and serialize monetary amounts independently of the underlying decimal number and arithmetics provider.

See also [xemantic-ai-money](https://github.com/xemantic/xemantic-ai-money]) project for a ready solution packaged as a library.

## Development

Clone this repo and then in the project dir:

```shell
./gradlew build
```

## Non-recommended usage

> [!WARNING]
> Even though this library provides basic serializable representation of a JSON Schema, it is not meant to fully model general purpose JSON Schema.
> In particular, it should not be used for deserializing existing schemas from JSON.
