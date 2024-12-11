/*
 * Copyright 2024 Kazimierz Pogoda / Xemantic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xemantic.ai.tool.schema

import io.kotest.assertions.json.shouldEqualJson
import com.xemantic.kotlin.test.should
import com.xemantic.kotlin.test.have
import kotlin.test.Test
import kotlin.test.assertFailsWith

class JsonSchemaTest {

  @Test
  fun `should create ObjectSchema`() {
    ObjectSchema {
      title = "Person"
      description = "A person schema"
      properties = mapOf(
        "name" to StringSchema { },
        "age" to IntegerSchema { }
      )
      required = listOf("name")
      additionalProperties = false
    }.toString() shouldEqualJson /* language=json */ """
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
  fun `should create ObjectSchema with definitions`() {
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
    }.toString() shouldEqualJson /* language=json */ $$"""
      {
        "type": "object",
        "title": "Person",
        "properties": {
          "name": {
            "type": "string"
          },
          "address": {
            "$ref": "#/definitions/address"
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
  fun `should create empty ObjectSchema`() {
    ObjectSchema {}.toString() shouldEqualJson /* language=json */ """{"type": "object"}"""
  }

  @Test
  fun `should create StringSchema`() {
    StringSchema {
      title = "Username"
      description = "A username"
      minLength = 3
      maxLength = 20
      pattern = "^[a-zA-Z0-9_]+$"
    }.toString() shouldEqualJson /* language=json */ """
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
  fun `should create StringSchema with format`() {
    StringSchema {
      title = "Email"
      description = "An email"
      minLength = 3
      maxLength = 100
      format(StringFormat.EMAIL)
    }.toString() shouldEqualJson /* language=json */ """
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
  fun `should create StringSchema with enum`() {
    StringSchema {
      title = "Color"
      enum = listOf("red", "green", "blue")
    }.toString() shouldEqualJson /* language=json */ """
      {
        "type": "string",
        "title": "Color",
        "enum": ["red", "green", "blue"]
      }      
    """
  }

  @Test
  fun `should create StringSchema with contentEncoding`() {
    StringSchema {
      title = "Image"
      description = "User's avatar image"
      contentEncoding = ContentEncoding.BASE64
      contentMediaType = "image/png"
    }.toString() shouldEqualJson /* language=json */ """
      {
        "type": "string",
        "title": "Image",
        "description": "User's avatar image",
        "contentEncoding": "base64",
        "contentMediaType": "image/png"
      }            
    """
  }

  @Test
  fun `should create empty StringSchema`() {
    StringSchema {}.toString() shouldEqualJson /* language=json */ """{"type": "string"}"""
  }

  @Test
  fun `should create ArraySchema`() {
    ArraySchema {
      title = "Numbers"
      description = "An array of numbers"
      items = NumberSchema {}
      minItems = 1
      maxItems = 10
      uniqueItems = true
    }.toString() shouldEqualJson /* language=json */ """
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
  fun `should create ArraySchema with ObjectSchema items`() {
    ArraySchema {
      title = "Users"
      items = ObjectSchema {
        properties = mapOf(
          "id" to IntegerSchema {},
          "name" to StringSchema {}
        )
      }
    }.toString() shouldEqualJson /* language=json */ """
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
  fun `should create NumberSchema with inclusive range`() {
    NumberSchema {
      title = "Price"
      description = "A price value"
      minimum = 0.0
      maximum = 1000.0
      multipleOf = 0.01
    }.toString() shouldEqualJson /* language=json */ """
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
  fun `should create NumberSchema with exclusive range`() {
    NumberSchema {
      title = "Price"
      description = "A price value"
      exclusiveMinimum = 0.0
      exclusiveMaximum = 1000.0
    }.toString() shouldEqualJson /* language=json */ """
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
  fun `should create empty NumberSchema`() {
    NumberSchema {}.toString() shouldEqualJson /* language=json */ """{"type": "number"}"""
  }

  @Test
  fun `should create IntegerSchema with inclusive range`() {
    IntegerSchema {
      title = "Age"
      description = "A person's age"
      minimum = 0
      maximum = 120
      multipleOf = 1
    }.toString() shouldEqualJson /* language=json */ """
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
  fun `should create IntegerSchema with exclusive range`() {
    IntegerSchema {
      title = "Age"
      description = "A person's age"
      exclusiveMinimum = 0
      exclusiveMaximum = 120
    }.toString() shouldEqualJson /* language=json */ """
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
  fun `should create empty IntegerSchema`() {
    IntegerSchema {}.toString() shouldEqualJson /* language=json */ """{"type": "integer"}"""
  }

  @Test
  fun `should create BooleanSchema`() {
    BooleanSchema {
      title = "Is Active"
      description = "Whether the user is active"
    }.toString() shouldEqualJson /* language=json */ """
      {
        "type": "boolean",
        "title": "Is Active",
        "description": "Whether the user is active"
      }      
    """
  }

  @Test
  fun `should create empty BooleanSchema`() {
    BooleanSchema {}.toString() shouldEqualJson /* language=json */ """{"type": "boolean"}"""
  }

  @Test
  fun `should create JsonSchemaRef`() {
    JsonSchema.Ref("#/definitions/address").toString() shouldEqualJson /* language=json */ $$"""
      {
        "$ref": "#/definitions/address"
      }
    """
  }

  @Test
  fun `should throw Exception for invalid JSON Pointer passed to JsonSchemaRef`() {
    assertFailsWith<IllegalArgumentException> {
      JsonSchema.Ref("invalid_ref")
    } should {
      have(message == "The 'ref' must start with '#/'")
    }
  }

}
