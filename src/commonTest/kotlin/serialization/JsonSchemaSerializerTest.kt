/*
 * Copyright 2024-2025 Kazimierz Pogoda / Xemantic
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

package com.xemantic.ai.tool.schema.serialization

import com.xemantic.ai.tool.schema.JsonSchema
import com.xemantic.ai.tool.schema.test.testJson
import com.xemantic.kotlin.test.be
import com.xemantic.kotlin.test.have
import com.xemantic.kotlin.test.should
import io.kotest.assertions.json.shouldEqualJson
import kotlin.test.Test

class JsonSchemaSerializerTest {

    @Test
    fun `should decode JsonSchema reference`() {
        /* language=json */
        val json = $$"""
          {
            "$ref": "#/definitions/foo"
          }
        """
        testJson.decodeFromString<JsonSchema>(json) should {
            be<JsonSchema.Ref>()
            have(ref == "#/definitions/foo")
        }
    }

    @Test
    fun `decode JSON Schema from JSON`() {
        /* language=json */
        val json = $$"""
          {
            "type": "object",
            "description": "Personal data",
            "properties": {
              "name": {
                "type": "string",
                "description": "The official name"
              },
              "birthDate": {
                "type": "string",
                "format": "date-time"
              },
              "email": {
                "type": "string",
                "minLength": 6,
                "maxLength": 100,
                "format": "email"
              },
              "address": {
                "$ref": "#/definitions/address"
              },
              "hobbies": {
                "type": "array",
                "description": "A list of hobbies of the person",
                "items": {
                  "type": "string",
                  "title": "A hobby item",
                  "description": "A hobby must be a unique identifier consisting out of lower case letters and underscores",
                  "pattern": "[a-z_]"
                },
                "minItems": 0,
                "maxItems": 10,
                "uniqueItems": true
              },
              "mentors": {
                "type": "array",
                "items": {
                  "$ref": "#/definitions/mentor"
                }
              },
              "salary": {
                "type": "string",
                "description": "A monetary amount",
                "pattern": "^-?[0-9]+\\.[0-9]{2}?$"
              },
              "tax": {
                "type": "string",
                "pattern": "^-?\\d+(\\.\\d+)?$"
              },
              "status": {
                "type": "string",
                "title": "Entry status",
                "description": "The enumeration of possible entry status states, e.g. 'verification-pending', 'verified'",
                "enum": [
                  "verification-pending",
                  "verified"
                ]
              },
              "avatar": {
                "type": "string",
                "contentEncoding": "base64",
                "contentMediaType": "image/png"
              },
              "tokens": {
                "type": "integer",
                "minimum": 0,
                "maximum": 1000
              },
              "karma": {
                "type": "integer",
                "exclusiveMinimum": 0,
                "exclusiveMaximum": 100
              },
              "experience": {
                "type": "number",
                "minimum": 0.0,
                "maximum": 100.0
              },
              "factor": {
                "type": "number",
                "exclusiveMinimum": 0.0,
                "exclusiveMaximum": 1.0
              }
            },
            "required": [
              "name",
              "birthDate",
              "address",
              "salary",
              "tax",
              "status",
              "avatar",
              "tokens",
              "karma",
              "experience",
              "factor"
            ],
            "definitions": {
              "address": {
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
                  }
                },
                "required": [
                  "street",
                  "city",
                  "postalCode",
                  "countryCode"
                ]
              },
              "mentor": {
                "type": "object",
                "properties": {
                  "id": {
                    "type": "string"
                  }
                },
                "required": [
                  "id"
                ]
              }
            }
          }
        """

        // when
        val schema = testJson.decodeFromString<JsonSchema>(json)

        // then
        schema.toString() shouldEqualJson json
    }

}
