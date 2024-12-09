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

import com.ionspin.kotlin.bignum.decimal.BigDecimal

/**
 * The `nonJvm` flavor of test money.
 */
class NonJvmMoney(
  private val value: BigDecimal
) : Money {

  override fun plus(
    amount: Money
  ) = NonJvmMoney(value + (amount as NonJvmMoney).value)

  override fun compareTo(
    other: Money
  ) = value.compareTo(
    (other as NonJvmMoney).value
  )

  override fun toString(): String {
    return value.toString(10)
  }

}

@Suppress("TestFunctionName")
actual fun Money(
  amount: String
): Money = NonJvmMoney(BigDecimal.parseString(amount))