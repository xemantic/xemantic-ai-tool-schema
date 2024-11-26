package com.xemantic.json.schema

import io.kotest.assertions.json.shouldEqualJson
import kotlin.test.Test

class JsonSchemaTest {

  @Test
  fun shouldCreateEmptySchemaWithBuilderAndReturnJsonOnToString() {
    JsonSchema {}.toString() shouldEqualJson "{}"
  }

  @Test
  fun shouldCreateSchemaWithBuilderAndReturnJsonOnToString() {
    JsonSchema {
      schema = SchemaVersion.DRAFT_2020_12
      type(JsonSchema.Type.OBJECT)
      properties = mapOf(
        "foo" to JsonSchema {
          type(JsonSchema.Type.STRING)
        },
        "bar" to JsonSchema {
          type(JsonSchema.Type.BOOLEAN)
        }
      )
      required = listOf("foo")
    }.toString() shouldEqualJson """
      {
        "${'$'}schema": "https://json-schema.org/draft/2020-12/schema",
        "type": "object",
        "properties": {
          "foo": {
            "type": "string"
          },
          "bar": {
            "type": "boolean"
          }
        },
        "required": [
          "foo"
        ]
      }
    """
  }

}
