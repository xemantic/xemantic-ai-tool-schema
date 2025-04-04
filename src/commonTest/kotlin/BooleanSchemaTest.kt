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

class BooleanSchemaTest {

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
        BooleanSchema().toString() shouldEqualJson /* language=json */ """{"type": "boolean"}"""
    }

}