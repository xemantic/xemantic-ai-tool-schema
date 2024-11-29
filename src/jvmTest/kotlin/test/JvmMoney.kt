package com.xemantic.ai.tool.schema.test

import java.math.BigDecimal

/**
 * The JVM flavor of [Money] is using the actual `java.math`[BigDecimal].
 *
 * Note: the multiplatform [com.ionspin.kotlin.bignum.decimal.BigDecimal]
 * could be used as well, however standard `java.math.BigDecimal` might come with more features and
 * performance/stability advantage for calculation-heavy tasks.
 */
class JvmMoney(
  private val value: BigDecimal
) : Money {

  override fun plus(
    amount: Money
  ) = JvmMoney(value + (amount as JvmMoney).value)

  override fun compareTo(
    other: Money
  ) = value.compareTo(
    (other as JvmMoney).value
  )

}

actual fun Money(
  amount: String
): Money = JvmMoney(BigDecimal(amount))
