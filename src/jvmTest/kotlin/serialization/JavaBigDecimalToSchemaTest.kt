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

@file:UseSerializers(BigDecimalSerializer::class)

package com.xemantic.ai.tool.schema.serialization

import com.xemantic.ai.tool.schema.generator.jsonSchemaOf
import com.xemantic.ai.tool.schema.meta.Description
import com.xemantic.ai.tool.schema.meta.Title
import com.xemantic.ai.tool.schema.test.testJson
import io.kotest.assertions.json.shouldEqualJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import kotlin.test.Test

class JavaBigDecimalToSchemaTest {

    @Serializable
    data class FinancialReport(
        val netSalesRevenue: BigDecimal,
        // here we are adding title
        @Title("Cost of Goods Sold (COGS)")
        val costOfGoodsSold: BigDecimal,
        // here the description is altered from the default declared for BigDecimal
        @Description("A decimal number of gross profit calculated as Net Sales Revenue - Cost of Goods Sold")
        val grossProfit: BigDecimal
    )

    @Test
    fun `should represent Java BigDecimal as String with pattern and description JSON Schema`() {
        val schema = jsonSchemaOf<FinancialReport>()
        testJson.encodeToString(schema) shouldEqualJson /* language=json */ $$"""
          {
            "type": "object",
            "properties": {
              "netSalesRevenue": {
                "type": "string",
                "description": "A decimal number",
                "pattern": "^-?\\d+(\\.\\d+)?$"
              },
              "costOfGoodsSold": {
                "type": "string",
                "title": "Cost of Goods Sold (COGS)",
                "description": "A decimal number",
                "pattern": "^-?\\d+(\\.\\d+)?$"
              },
              "grossProfit": {
                "type": "string",
                "description": "A decimal number of gross profit calculated as Net Sales Revenue - Cost of Goods Sold",
                "pattern": "^-?\\d+(\\.\\d+)?$"
              }
            },
            "required": [
              "netSalesRevenue",
              "costOfGoodsSold",
              "grossProfit"
            ]
          }
        """
    }

}
