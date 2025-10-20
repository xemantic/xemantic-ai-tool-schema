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

class StringSchemaTest {

    @Test
    fun `should create StringSchema`() {
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
    fun `should create StringSchema with format`() {
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
    fun `should create StringSchema with enum`() {
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
    fun `should create StringSchema with contentEncoding`() {
        StringSchema {
            title = "Image"
            description = "User's avatar image"
            contentEncoding = ContentEncoding.BASE64
            contentMediaType = "image/png"
        }.toString() shouldEqualJson """
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
        StringSchema().toString() shouldEqualJson """{"type": "string"}"""
    }

}