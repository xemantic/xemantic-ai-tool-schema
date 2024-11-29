package com.xemantic.ai.tool.schema.test

import com.ionspin.kotlin.bignum.decimal.BigDecimal

/**
 * The `js` flavor of test money.
 */
class JsMoney(
  private val value: BigDecimal
) : Money {

  override fun plus(
    amount: Money
  ) = JsMoney(value + (amount as JsMoney).value)

  override fun compareTo(
    other: Money
  ) = value.compareTo(
    (other as JsMoney).value
  )

  override fun toString(): String {
    return value.toString(10)
  }

}

actual fun Money(
  amount: String
): Money = JsMoney(BigDecimal.parseString(amount))
