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

class IntegerSchemaTest {

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
    fun `should create IntegerSchema with inclusive range while using LongRange`() {
        IntegerSchema {
            title = "Dozen"
            range = 1L..12
        }.toString() shouldEqualJson /* language=json */ """
            {
              "type": "integer",
              "title": "Dozen",
              "minimum": 1,
              "maximum": 12
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
    fun `should create IntegerSchema with negative ranges`() {
        IntegerSchema {
            minimum = -100
            maximum = -1
        }.toString() shouldEqualJson /* language=json */ """
            {
              "type": "integer",
              "minimum": -100,
              "maximum": -1
            }
        """
    }

    @Test
    fun `should create empty IntegerSchema`() {
        IntegerSchema().toString() shouldEqualJson /* language=json */ """{"type": "integer"}"""
    }

}