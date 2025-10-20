# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is **xemantic-ai-tool-schema**, a Kotlin multiplatform library that generates JSON Schema from `@Serializable` classes for AI/LLM tool use (function calling). The library bridges Kotlin's type system with JSON Schema, enabling automatic schema generation from annotated Kotlin classes for use with LLM APIs like Anthropic and OpenAI.

## Core Functionality

The library's main entry point is the `jsonSchemaOf<T>()` function in `src/commonMain/kotlin/generator/JsonSchemaGenerator.kt`, which generates JSON Schema from any `@Serializable` Kotlin class. The generator:
- Analyzes `SerialDescriptor` metadata to build schemas
- Supports custom annotations (`@Title`, `@Description`, `@MinLength`, `@MaxLength`, `@Pattern`, `@Format`, etc.) from `src/commonMain/kotlin/meta/JsonSchemaAnnotations.kt`
- Handles nested objects, arrays, primitives, and sealed hierarchies
- Supports `$ref` references for complex types (with optional inlining via `inlineRefs` parameter)
- Manages polymorphic types using `oneOf` and discriminators

The JSON Schema model is defined in `src/commonMain/kotlin/JsonSchema.kt` with sealed interfaces and classes representing different schema types (`ObjectSchema`, `ArraySchema`, `StringSchema`, `NumberSchema`, `IntegerSchema`, `BooleanSchema`, etc.).

## Build Commands

```shell
# Build the project (compiles all targets)
./gradlew build

# Run tests for all platforms
./gradlew test

# Run tests for specific platform
./gradlew jvmTest          # JVM tests only
./gradlew jsTest           # JavaScript tests
./gradlew macosX64Test     # macOS native tests

# Run a single test
./gradlew jvmTest --tests "com.xemantic.ai.tool.schema.generator.JsonSchemaGeneratorTest"

# Clean build
./gradlew clean

# Check for dependency updates
./gradlew dependencyUpdates

# Generate documentation
./gradlew dokkaGeneratePublicationHtml
```

## Architecture

### Source Structure
- `src/commonMain/kotlin/` - Multiplatform core implementation
  - `JsonSchema.kt` - Schema model definitions
  - `generator/JsonSchemaGenerator.kt` - Schema generation logic
  - `meta/JsonSchemaAnnotations.kt` - Validation/constraint annotations
  - `serialization/JsonSchemaSerializer.kt` - Custom serialization
- `src/jvmMain/kotlin/` - JVM-specific extensions (e.g., `BigDecimal` support)
- `src/commonTest/kotlin/` - Shared tests across all platforms
- `src/jvmTest/kotlin/` - JVM-specific tests
- `src/nonJvmTest/kotlin/` - Tests for non-JVM platforms

### Multiplatform Targets

The project supports extensive multiplatform compilation (configured in build.gradle.kts:56-128):
- **JVM** (Java 11 target)
- **JavaScript** (browser + Node.js)
- **WebAssembly** (wasmJs, wasmWasi)
- **Native** platforms (macOS, iOS, Linux, Windows, watchOS, tvOS, Android Native)
- **Swift Export** for iOS/macOS interop

Some test targets are disabled for platforms with incomplete kotest support (build.gradle.kts:173-183).

### Key Implementation Details

1. **Schema Generation Process** (`JsonSchemaGenerator.kt`):
   - Uses `SerialDescriptor` reflection to analyze class structure
   - Tracks references to prevent infinite recursion on circular types
   - Applies annotations from properties and classes to enrich schemas
   - Generates `definitions` map for reusable type definitions

2. **Annotation System** (`JsonSchemaAnnotations.kt`):
   - All annotations are `@MetaSerializable` (kotlinx.serialization metadata)
   - Annotations can target properties, classes, or types
   - Array-specific annotations: `@ItemTitle`, `@ItemDescription`, `@MinItems`, `@MaxItems`, `@UniqueItems`
   - String-specific: `@MinLength`, `@MaxLength`, `@Pattern`, `@Format`, `@FormatString`, `@Encoding`, `@ContentMediaType`
   - Numeric: `@Min`, `@Max`, `@MultipleOf` (for doubles), `@MinInt`, `@MaxInt`, `@MultipleOfInt` (for integers)

3. **Sealed Hierarchies**: The generator handles Kotlin sealed classes by creating `oneOf` schemas with discriminator support.

4. **BigDecimal/Monetary Values**:
   - JVM: Native `java.math.BigDecimal` serialization (see `src/jvmTest/kotlin/serialization/JavaBigDecimalToSchemaTest.kt`)
   - Multiplatform: Interface-based approach via `Money` interface (see `src/commonTest/kotlin/test/Money.kt`)

## Testing Practices

- Tests use kotest assertions with `shouldEqualJson` for schema validation
- Test naming: `` `should generate <type> schema` ``
- Primary test suite: `src/commonTest/kotlin/generator/JsonSchemaGeneratorTest.kt`
- Tests verify both schema structure and JSON serialization output

## Configuration Files

- `build.gradle.kts` - Main build configuration with multiplatform setup
- `gradle/libs.versions.toml` - Centralized dependency version management
- `settings.gradle.kts` - Project name only (minimal configuration)

## Development Notes

- The project uses explicit API mode (`explicitApi()` in build.gradle.kts:60)
- Kotlin compiler options include progressive mode and extra warnings
- Power Assert is configured for custom test assertion functions
- JReleaser is configured for Maven Central publishing and social media announcements
- Binary compatibility validation is enabled via `kotlinx.binary.compatibility.validator`