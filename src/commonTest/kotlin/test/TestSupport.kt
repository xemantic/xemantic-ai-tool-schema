package com.xemantic.ai.tool.schema.test

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * A pretty printing [Json] for tests.
 */
val testJson = Json {
  prettyPrint = true
  @OptIn(ExperimentalSerializationApi::class)
  prettyPrintIndent = "  "
}
