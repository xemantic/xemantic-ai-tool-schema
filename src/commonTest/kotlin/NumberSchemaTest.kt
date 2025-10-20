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

class NumberSchemaTest {

    @Test
    fun `should create NumberSchema with inclusive range`() {
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
    fun `should create NumberSchema with exclusive range`() {
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
    fun `should create NumberSchema with zero values`() {
        NumberSchema {
            minimum = 0.0
            maximum = 0.0
            multipleOf = 0.0
        }.toString() shouldEqualJson """
            {
              "type": "number",
              "minimum": 0.0,
              "maximum": 0.0,
              "multipleOf": 0.0
            }
        """
    }

    @Test
    fun `should create empty NumberSchema`() {
        NumberSchema().toString() shouldEqualJson """{"type": "number"}"""
    }

}