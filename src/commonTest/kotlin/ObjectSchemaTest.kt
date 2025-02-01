/*
 * Copyright 2025 Kazimierz Pogoda / Xemantic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xemantic.ai.tool.schema

import io.kotest.assertions.json.shouldEqualJson
import kotlin.test.Test

class ObjectSchemaTest {

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
                "address" to JsonSchema.Ref(ref = "#/definitions/address")
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
        ObjectSchema().toString() shouldEqualJson /* language=json */ """{"type": "object"}"""
    }

}