package com.xemantic.json.schema.test

import com.xemantic.json.schema.serialization.jsonSchemaSerializersModule
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

val testJson = Json {
  serializersModule = jsonSchemaSerializersModule
  prettyPrint = true
  @OptIn(ExperimentalSerializationApi::class)
  prettyPrintIndent = "  "
}
