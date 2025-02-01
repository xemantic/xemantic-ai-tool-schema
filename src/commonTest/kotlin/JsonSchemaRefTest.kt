/*
 * Copyright 2024-2025 Kazimierz Pogoda / Xemantic
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

class JsonSchemaRefTest {

    @Test
    fun `should create JsonSchemaRef`() {
        JsonSchema.Ref(ref = "#/definitions/address").toString() shouldEqualJson /* language=json */ $$"""
          {
            "$ref": "#/definitions/address"
          }
        """
    }

    @Test
    fun `should create JsonSchemaRef with title and description`() {
        JsonSchema.Ref("#/definitions/address") {
            title = "Address Reference"
            description = "Reference to the address definition"
        }.toString() shouldEqualJson /* language=json */ $$"""
          {
            "$ref": "#/definitions/address",
            "title": "Address Reference",
            "description": "Reference to the address definition"
          }
        """
    }

    @Test
    fun `should throw Exception for invalid JSON Pointer passed to JsonSchemaRef`() {
        assertFailsWith<IllegalArgumentException> {
            JsonSchema.Ref(ref = "invalid_ref")
        } should {
            have(message == "The 'ref' must start with '#', was: 'invalid_ref'")
        }
    }

}
