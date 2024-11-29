package com.xemantic.ai.tool.schema.test

import com.ionspin.kotlin.bignum.decimal.BigDecimal

/**
 * The `wasmJs` flavor of test money.
 */
class WasmJsMoney(
  private val value: BigDecimal
) : Money {

  override fun plus(
    amount: Money
  ) = WasmJsMoney(value + (amount as WasmJsMoney).value)

  override fun compareTo(
    other: Money
  ) = value.compareTo(
    (other as WasmJsMoney).value
  )

  override fun toString(): String {
    return value.toString(10)
  }

}

actual fun Money(
  amount: String
): Money = WasmJsMoney(BigDecimal.parseString(amount))
