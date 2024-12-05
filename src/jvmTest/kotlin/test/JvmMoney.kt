/*
 * Copyright 2024 Kazimierz Pogoda / Xemantic
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

@Suppress("TestFunctionName")
actual fun Money(
  amount: String
): Money = JvmMoney(BigDecimal(amount))
