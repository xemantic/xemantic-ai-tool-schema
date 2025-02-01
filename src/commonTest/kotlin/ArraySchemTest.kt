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

import com.xemantic.kotlin.test.have
import com.xemantic.kotlin.test.should
import io.kotest.assertions.json.shouldEqualJson
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ArraySchemTest {

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
    fun `should create ArraySchema with definitions`() {
        ArraySchema {
            items = ObjectSchema { }
            definitions = mapOf(
                "item" to ObjectSchema {
                    properties = mapOf(
                        "id" to IntegerSchema { }
                    )
                }
            )
        }.toString() shouldEqualJson /* language=json */ """
            {
              "type": "array",
              "items": {
                "type": "object"
              },
              "definitions": {
                "item": {
                  "type": "object",
                  "properties": {
                    "id": {
                      "type": "integer"
                    }
                  }
                }
              }
            }
        """
    }

    @Test
    fun `should fail to create ArraySchema without items`() {
        assertFailsWith<IllegalArgumentException> {
            ArraySchema().toString()
        } should {
            have(message == "Cannot build ArraySchema without 'items' property")
        }
    }

}
