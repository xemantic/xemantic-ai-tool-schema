package com.xemantic.ai.tool.schema.test

import com.ionspin.kotlin.bignum.decimal.BigDecimal

/**
 * The `native` flavor of test money.
 */
class NativeMoney(
  private val value: BigDecimal
) : Money {

  override fun plus(
    amount: Money
  ) = NativeMoney(value + (amount as NativeMoney).value)

  override fun compareTo(
    other: Money
  ) = value.compareTo(
    (other as NativeMoney).value
  )

}

actual fun Money(
  amount: String
): Money = NativeMoney(BigDecimal.parseString(amount))
