@file:UseSerializers(BigDecimalSerializer::class)

package com.xemantic.ai.tool.schema.serialization

import com.xemantic.ai.tool.schema.generator.jsonSchemaOf
import com.xemantic.ai.tool.schema.meta.Description
import com.xemantic.ai.tool.schema.meta.Title
import com.xemantic.ai.tool.schema.test.testJson
import io.kotest.assertions.json.shouldEqualJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
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
    testJson.encodeToString(schema) shouldEqualJson $$"""
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
