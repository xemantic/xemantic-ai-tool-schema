package com.xemantic.ai.tool.schema.test

import com.ionspin.kotlin.bignum.decimal.BigDecimal

/**
 * The `WasmWasi` flavor of test money.
 */
class WasmWasiMoney(
  private val value: BigDecimal
) : Money {

  override fun plus(
    amount: Money
  ) = WasmWasiMoney(value + (amount as WasmWasiMoney).value)

  override fun compareTo(
    other: Money
  ) = value.compareTo(
    (other as WasmWasiMoney).value
  )

  override fun toString(): String {
    return value.toString(10)
  }

}

actual fun Money(
  amount: String
): Money = WasmWasiMoney(BigDecimal.parseString(amount))
