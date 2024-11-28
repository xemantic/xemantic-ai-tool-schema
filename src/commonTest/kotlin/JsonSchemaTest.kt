package com.xemantic.ai.tool.schema

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrowWithMessage
import kotlin.test.Test

class JsonSchemaTest {

  @Test
  fun shouldCreateObjectSchema() {
    ObjectSchema {
      title = "Person"
      description = "A person schema"
      properties = mapOf(
        "name" to StringSchema { },
        "age" to IntegerSchema { }
      )
      required = listOf("name")
      additionalProperties = false
    }.toString() shouldEqualJson """
      {
        "type": "object",
        "title": "Person",
        "description": "A person schema",
        "properties": {
          "name": {
            "type": "string"
          },
          "age": {
            "type": "integer"
          }
        },
        "required": ["name"],
        "additionalProperties": false
      }      
    """
  }

  @Test
  fun shouldCreateObjectSchemaWithDefinitions() {
    ObjectSchema {
      title = "Person"
      properties = mapOf(
        "name" to StringSchema { },
        "address" to JsonSchema.Ref("#/definitions/address")
      )
      definitions = mapOf(
        "address" to ObjectSchema {
          properties = mapOf(
            "street" to StringSchema { },
            "city" to StringSchema { }
          )
        }
      )
    }.toString() shouldEqualJson """
      {
        "type": "object",
        "title": "Person",
        "properties": {
          "name": {
            "type": "string"
          },
          "address": {
            "${'$'}ref": "#/definitions/address"
          }
        },
        "definitions": {
          "address": {
            "type": "object",
            "properties": {
              "street": {
                "type": "string"
              },
              "city": {
                "type": "string"
              }
            }
          }
        }
      }      
    """
  }

  @Test
  fun shouldCreateEmptyObjectSchema() {
    ObjectSchema {}.toString() shouldEqualJson """{"type": "object"}"""
  }

  @Test
  fun shouldCreateStringSchema() {
    StringSchema {
      title = "Username"
      description = "A username"
      minLength = 3
      maxLength = 20
      pattern = "^[a-zA-Z0-9_]+$"
    }.toString() shouldEqualJson """
      {
        "type": "string",
        "title": "Username",
        "description": "A username",
        "minLength": 3,
        "maxLength": 20,
        "pattern": "^[a-zA-Z0-9_]+$"
      }      
    """
  }

  @Test
  fun shouldCreateStringSchemaWithFormat() {
    StringSchema {
      title = "Email"
      description = "An email"
      minLength = 3
      maxLength = 100
      format(StringFormat.EMAIL)
    }.toString() shouldEqualJson """
      {
        "type": "string",
        "title": "Email",
        "description": "An email",
        "minLength": 3,
        "maxLength": 100,
        "format": "email"
      }      
    """
  }

  @Test
  fun shouldCreateStringSchemaWithEnum() {
    StringSchema {
      title = "Color"
      enum = listOf("red", "green", "blue")
    }.toString() shouldEqualJson """
      {
        "type": "string",
        "title": "Color",
        "enum": ["red", "green", "blue"]
      }      
    """
  }

  @Test
  fun shouldCreateEmptyStringSchema() {
    StringSchema {}.toString() shouldEqualJson """{"type": "string"}"""
  }

  @Test
  fun shouldCreateArraySchema() {
    ArraySchema {
      title = "Numbers"
      description = "An array of numbers"
      items = NumberSchema {}
      minItems = 1
      maxItems = 10
      uniqueItems = true
    }.toString() shouldEqualJson """
      {
        "type": "array",
        "title": "Numbers",
        "description": "An array of numbers",
        "items": {
          "type": "number"
        },
        "minItems": 1,
        "maxItems": 10,
        "uniqueItems": true
      }      
    """
  }

  @Test
  fun shouldCreateArraySchemaWithObjectItems() {
    ArraySchema {
      title = "Users"
      items = ObjectSchema {
        properties = mapOf(
          "id" to IntegerSchema { },
          "name" to StringSchema { }
        )
      }
    }.toString() shouldEqualJson """
      {
        "type": "array",
        "title": "Users",
        "items": {
          "type": "object",
          "properties": {
            "id": {
              "type": "integer"
            },
            "name": {
              "type": "string"
            }
          }
        }
      }      
    """
  }

  @Test
  fun shouldCreateNumberSchemaWithInclusiveRange() {
    NumberSchema {
      title = "Price"
      description = "A price value"
      minimum = 0.0
      maximum = 1000.0
      multipleOf = 0.01
    }.toString() shouldEqualJson """
      {
        "type": "number",
        "title": "Price",
        "description": "A price value",
        "minimum": 0.0,
        "maximum": 1000.0,
        "multipleOf": 0.01
      }      
    """
  }

  @Test
  fun shouldCreateNumberSchemaWithExclusiveRange() {
    NumberSchema {
      title = "Price"
      description = "A price value"
      exclusiveMinimum = 0.0
      exclusiveMaximum = 1000.0
    }.toString() shouldEqualJson """
      {
        "type": "number",
        "title": "Price",
        "description": "A price value",
        "exclusiveMinimum": 0.0,
        "exclusiveMaximum": 1000.0
      }      
    """
  }

  @Test
  fun shouldCreateEmptyNumberSchema() {
    NumberSchema {}.toString() shouldEqualJson """{"type": "number"}"""
  }

  @Test
  fun shouldCreateIntegerSchemaWithInclusiveRange() {
    IntegerSchema {
      title = "Age"
      description = "A person's age"
      minimum = 0
      maximum = 120
      multipleOf = 1
    }.toString() shouldEqualJson """
      {
        "type": "integer",
        "title": "Age",
        "description": "A person's age",
        "minimum": 0,
        "maximum": 120,
        "multipleOf": 1
      }      
    """
  }

  @Test
  fun shouldCreateIntegerSchemaWithExclusiveRange() {
    IntegerSchema {
      title = "Age"
      description = "A person's age"
      exclusiveMinimum = 0
      exclusiveMaximum = 120
    }.toString() shouldEqualJson """
      {
        "type": "integer",
        "title": "Age",
        "description": "A person's age",
        "exclusiveMinimum": 0,
        "exclusiveMaximum": 120
      }      
    """
  }

  @Test
  fun shouldCreateEmptyIntegerSchema() {
    IntegerSchema {}.toString() shouldEqualJson """{"type": "integer"}"""
  }

  @Test
  fun shouldCreateBooleanSchema() {
    BooleanSchema {
      title = "Is Active"
      description = "Whether the user is active"
    }.toString() shouldEqualJson """
      {
        "type": "boolean",
        "title": "Is Active",
        "description": "Whether the user is active"
      }      
    """
  }

  @Test
  fun shouldCreateEmptyBooleanSchema() {
    BooleanSchema {}.toString() shouldEqualJson """{"type": "boolean"}"""
  }

  @Test
  fun shouldCreateJsonSchemaRef() {
    JsonSchema.Ref("#/definitions/address").toString() shouldEqualJson """
      {
        "${'$'}ref": "#/definitions/address"
      }
    """
  }

  @Test
  fun shouldThrowExceptionForInvalidRef() {
    shouldThrowWithMessage<IllegalArgumentException>(
      "The 'ref' must start with '#/'"
    ) {
      JsonSchema.Ref("invalid_ref")
    }
  }

}
