/*
 * Copyright 2024-2025 Kazimierz Pogoda / Xemantic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xemantic.ai.tool.schema.generator

import com.xemantic.ai.tool.schema.ContentEncoding
import com.xemantic.ai.tool.schema.StringFormat
import com.xemantic.ai.tool.schema.meta.*
import com.xemantic.ai.tool.schema.test.BigDecimal
import com.xemantic.ai.tool.schema.test.Money
import com.xemantic.ai.tool.schema.test.testJson
import io.kotest.assertions.json.shouldEqualJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class JsonSchemaGeneratorTest {

    @Test
    fun `should generate Int schema`() {
        val schema = jsonSchemaOf<Int>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "integer"
            }
        """
    }

    @Test
    fun `should generate Byte schema`() {
        val schema = jsonSchemaOf<Byte>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "integer"
            }
        """
    }

    @Test
    fun `should generate Short schema`() {
        val schema = jsonSchemaOf<Short>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "integer"
            }
        """
    }

    @Test
    fun `should generate Double schema`() {
        val schema = jsonSchemaOf<Double>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "number"
            }
        """
    }

    @Test
    fun `should generate Float schema`() {
        val schema = jsonSchemaOf<Float>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "number"
            }
        """
    }

    @Test
    fun `should generate Boolean schema`() {
        val schema = jsonSchemaOf<Boolean>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "boolean"
            }
        """
    }

    @Test
    fun `should generate Char schema`() {
        val schema = jsonSchemaOf<Char>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "string",
              "minLength": 1,
              "maxLength": 1
            }
        """
    }

    @Test
    fun `should generate String schema`() {
        val schema = jsonSchemaOf<String>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "string"
            }
        """
    }

    @Test
    fun `should generate List of Ints schema`() {
        val schema = jsonSchemaOf<List<Int>>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "array",
              "items": {
                "type": "integer"
              }
            }
        """
    }

    @Test
    fun `should generate List of Bytes schema`() {
        val schema = jsonSchemaOf<List<Byte>>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "array",
              "items": {
                "type": "integer"
              }
            }
        """
    }

    @Test
    fun `should generate List of Shorts schema`() {
        val schema = jsonSchemaOf<List<Short>>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "array",
              "items": {
                "type": "integer"
              }
            }
        """
    }

    @Test
    fun `should generate List of Doubles schema`() {
        val schema = jsonSchemaOf<List<Double>>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "array",
              "items": {
                "type": "number"
              }
            }
        """
    }

    @Test
    fun `should generate List of Floats schema`() {
        val schema = jsonSchemaOf<List<Float>>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "array",
              "items": {
                "type": "number"
              }
            }
        """
    }

    @Test
    fun `should generate List of Booleans schema`() {
        val schema = jsonSchemaOf<List<Boolean>>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "array",
              "items": {
                "type": "boolean"
              }
            }
        """
    }

    @Test
    fun `should generate List of Chars schema`() {
        val schema = jsonSchemaOf<List<Char>>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "array",
              "items": {
                "type": "string",
                "minLength": 1,
                "maxLength": 1
              }
            }
        """
    }

    @Test
    fun `should generate List of Strings schema`() {
        val schema = jsonSchemaOf<List<String>>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "array",
              "items": {
                "type": "string"
              }
            }
        """
    }

    @Test
    fun `should generate List of Lists of Strings schema`() {
        val schema = jsonSchemaOf<List<List<String>>>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "array",
              "items": {
                "type": "array",
                "items": {
                  "type": "string"
                }
              }
            }
        """
    }

    @Test
    fun `should generate schema for a primitive type with title and description`() {
        val schema = jsonSchemaOf<String>(
            title = "foo",
            description = "bar",
        )
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "string",
              "title": "foo",
              "description": "bar"
            }
        """
    }

    /**
     * Address is our first class to test generation of JSON schema.
     *
     * Kotlin serialization allows us to access both - data of an object and metadata of a class.
     * We are using this information to generate JSON schema.
     * All the serialization descriptors are build in compile time, therefore no reflection
     * is needed to access the metadata. Thanks to this fact, it is supported in kotlin multiplatform
     * project, where reflection is generally not supported.
     */
    @Serializable
    @SerialName("address")
    @Title("The full address")
    @Description("An address of a person or an organization")
    data class Address(
        val street: String,
        val city: String,
        @Description("A postal code not limited to particular country")
        @MinLength(3)
        @MaxLength(10)
        val postalCode: String,
        @Pattern("[a-z]{2}")
        val countryCode: String
    )

    @Test
    fun `should generate Address schema`() {
        val schema = jsonSchemaOf<Address>()
        testJson.encodeToString(schema) shouldEqualJson """
            {
              "type": "object",
              "title": "The full address",
              "description": "An address of a person or an organization",            
              "properties": {
                "street": {
                  "type": "string"
                },
                "city": {
                  "type": "string"
                },
                "postalCode": {
                  "type": "string",
                  "description": "A postal code not limited to particular country",
                  "minLength": 3,
                  "maxLength": 10          
                },
                "countryCode": {
                  "type": "string",
                  "pattern": "[a-z]{2}"
                }
              },
              "required": [
                "street",
                "city",
                "postalCode",
                "countryCode"
              ]
            }
        """
    }

    /**
     * The [Person] class adds the 2nd level data structure to JSON schema generation testing,
     * referencing the [Address] and the [Mentor].
     *
     * It contains all the possible annotations and also attributes of type [Instant], [Money] and [BigDecimal],
     * so we can test if generated JSON schema will contain proper `date-time` `type` and regex `pattern`
     * keywords.
     */
    @Serializable
    @Description("Personal data")
    data class Person(
        @Description("The official name")
        val name: String,
        @OptIn(ExperimentalTime::class)
        val birthDate: Instant,
        @Format(StringFormat.EMAIL)
        @MinLength(6)
        @MaxLength(100)
        val email: String? = null,
        @Description("Main address")
        val address: Address?,
        @Description("A list of hobbies of the person")
        @MinItems(0)
        @MaxItems(10)
        @Pattern("[a-z_]")
        @ItemTitle("A hobby item")
        @ItemDescription("A hobby must be a unique identifier consisting out of lower case letters and underscores")
        @UniqueItems
        val hobbies: List<String>? = null,
        val mentors: List<Mentor>? = null,
        val salary: Money,
        val tax: BigDecimal,
        val status: Status,
        @Encoding(ContentEncoding.BASE64)
        @ContentMediaType("image/png")
        val avatar: String,
        @MinInt(0)
        @MaxInt(1000)
        val tokens: Int,
        @MinInt(0, exclusive = true)
        @MaxInt(100, exclusive = true)
        @MultipleOfInt(100)
        val karma: Int,
        @Min(0.0)
        @Max(100.0)
        @MultipleOf(1.0)
        val experience: Double,
        @Min(0.0, exclusive = true)
        @Max(1.0, exclusive = true)
        val factor: Double
    )

    @Title("Entry status")
    @Description("The enumeration of possible entry status states, e.g. 'verification-pending', 'verified'")
    @Suppress("unused") // it is used to generate schema
    enum class Status {
        @SerialName("verification-pending")
        VERIFICATION_PENDING,

        @SerialName("verified")
        VERIFIED
    }

    @Serializable
    @SerialName("mentor")
    data class Mentor(
        val id: String
    )

    @Test
    fun `should generate schema for Person`() {
        val schema = jsonSchemaOf<Person>()
        val schemaJson = testJson.encodeToString(schema)

        // then
        schemaJson shouldEqualJson $$"""
            {
              "type": "object",
              "description": "Personal data",
              "properties": {
                "name": {
                  "type": "string",
                  "description": "The official name"
                },
                "birthDate": {
                  "type": "string",
                  "format": "date-time"
                },
                "email": {
                  "type": "string",
                  "minLength": 6,
                  "maxLength": 100,
                  "format": "email"
                },
                "address": {
                  "$ref": "#/definitions/address",
                  "description": "Main address"
                },
                "hobbies": {
                  "type": "array",
                  "description": "A list of hobbies of the person",
                  "items": {
                    "type": "string",
                    "title": "A hobby item",
                    "description": "A hobby must be a unique identifier consisting out of lower case letters and underscores",
                    "pattern": "[a-z_]"
                  },
                  "minItems": 0,
                  "maxItems": 10,
                  "uniqueItems": true
                },
                "mentors": {
                  "type": "array",
                  "items": {
                    "$ref": "#/definitions/mentor"
                  }
                },
                "salary": {
                  "type": "string",
                  "description": "A monetary amount",
                  "pattern": "^-?[0-9]+\\.[0-9]{2}?$"
                },
                "tax": {
                  "type": "string",
                  "pattern": "^-?\\d+(\\.\\d+)?$"
                },
                "status": {
                  "type": "string",
                  "title": "Entry status",
                  "description": "The enumeration of possible entry status states, e.g. 'verification-pending', 'verified'",
                  "enum": [
                    "verification-pending",
                    "verified"
                  ]
                },
                "avatar": {
                  "type": "string",
                  "contentEncoding": "base64",
                  "contentMediaType": "image/png"
                },
                "tokens": {
                  "type": "integer",
                  "minimum": 0,
                  "maximum": 1000
                },
                "karma": {
                  "type": "integer",
                  "exclusiveMinimum": 0,
                  "exclusiveMaximum": 100
                },
                "experience": {
                  "type": "number",
                  "minimum": 0.0,
                  "maximum": 100.0
                },
                "factor": {
                  "type": "number",
                  "exclusiveMinimum": 0.0,
                  "exclusiveMaximum": 1.0
                }
              },
              "required": [
                "name",
                "birthDate",
                "address",
                "salary",
                "tax",
                "status",
                "avatar",
                "tokens",
                "karma",
                "experience",
                "factor"
              ],
              "definitions": {
                "address": {
                  "type": "object",
                  "title": "The full address",
                  "description": "An address of a person or an organization",
                  "properties": {
                    "street": {
                      "type": "string"
                    },
                    "city": {
                      "type": "string"
                    },
                    "postalCode": {
                      "type": "string",
                      "description": "A postal code not limited to particular country",
                      "minLength": 3,
                      "maxLength": 10
                    },
                    "countryCode": {
                      "type": "string",
                      "pattern": "[a-z]{2}"
                    }
                  },
                  "required": [
                    "street",
                    "city",
                    "postalCode",
                    "countryCode"
                  ]
                },
                "mentor": {
                  "type": "object",
                  "properties": {
                    "id": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "id"
                  ]
                }
              }
            }
        """
    }

    @Test
    fun `should generate schema for Person with inline refs`() {
        val schema = jsonSchemaOf<Person>(inlineRefs = true)
        val schemaJson = testJson.encodeToString(schema)

        // then
        schemaJson shouldEqualJson $$"""
            {
              "type": "object",
              "description": "Personal data",
              "properties": {
                "name": {
                  "type": "string",
                  "description": "The official name"
                },
                "birthDate": {
                  "type": "string",
                  "format": "date-time"
                },
                "email": {
                  "type": "string",
                  "minLength": 6,
                  "maxLength": 100,
                  "format": "email"
                },
                "address": {
                  "type": "object",
                  "title": "The full address",
                  "description": "Main address",
                  "properties": {
                    "street": {
                      "type": "string"
                    },
                    "city": {
                      "type": "string"
                    },
                    "postalCode": {
                      "type": "string",
                      "description": "A postal code not limited to particular country",
                      "minLength": 3,
                      "maxLength": 10
                    },
                    "countryCode": {
                      "type": "string",
                      "pattern": "[a-z]{2}"
                    }
                  },
                  "required": [
                    "street",
                    "city",
                    "postalCode",
                    "countryCode"
                  ]
                },
                "hobbies": {
                  "type": "array",
                  "description": "A list of hobbies of the person",
                  "items": {
                    "type": "string",
                    "title": "A hobby item",
                    "description": "A hobby must be a unique identifier consisting out of lower case letters and underscores",
                    "pattern": "[a-z_]"
                  },
                  "minItems": 0,
                  "maxItems": 10,
                  "uniqueItems": true
                },
                "mentors": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "id": {
                        "type": "string"
                      }
                    },
                    "required": [
                      "id"
                    ]
                  }
                },
                "salary": {
                  "type": "string",
                  "description": "A monetary amount",
                  "pattern": "^-?[0-9]+\\.[0-9]{2}?$"
                },
                "tax": {
                  "type": "string",
                  "pattern": "^-?\\d+(\\.\\d+)?$"
                },
                "status": {
                  "type": "string",
                  "title": "Entry status",
                  "description": "The enumeration of possible entry status states, e.g. 'verification-pending', 'verified'",
                  "enum": [
                    "verification-pending",
                    "verified"
                  ]
                },
                "avatar": {
                  "type": "string",
                  "contentEncoding": "base64",
                  "contentMediaType": "image/png"
                },
                "tokens": {
                  "type": "integer",
                  "minimum": 0,
                  "maximum": 1000
                },
                "karma": {
                  "type": "integer",
                  "exclusiveMinimum": 0,
                  "exclusiveMaximum": 100
                },
                "experience": {
                  "type": "number",
                  "minimum": 0.0,
                  "maximum": 100.0
                },
                "factor": {
                  "type": "number",
                  "exclusiveMinimum": 0.0,
                  "exclusiveMaximum": 1.0
                }
              },
              "required": [
                "name",
                "birthDate",
                "address",
                "salary",
                "tax",
                "status",
                "avatar",
                "tokens",
                "karma",
                "experience",
                "factor"
              ]
            }
        """
    }

    @Serializable
    @SerialName("animal")
    @Description("Animal")
    sealed interface Animal

    @Serializable
    @SerialName("mammal")
    @Description("Mammal")
    sealed interface Mammal : Animal {
        val legs: Int
    }

    @Serializable
    @SerialName("cat")
    @Description("Cat")
    data class Cat(
        @Description("Legs")
        override val legs: Int = 2,
        val name: String,
    ) : Mammal

    @Serializable
    @SerialName("dog")
    @Description("Dog")
    data class Dog(
        @Description("Legs")
        override val legs: Int = 2,
        val name: String,
    ) : Mammal

    @Test
    fun `should generate schema for Animal list`() {
        val schema = jsonSchemaOf<List<Animal>>()
        val schemaJson = testJson.encodeToString(schema)

        // then
        schemaJson shouldEqualJson $$"""
            {
              "type": "array",
              "items": {
                "$ref": "#/definitions/animal",
                "description": "Animal"
              },
              "definitions": {
                "cat": {
                  "type": "object",
                  "description": "Cat",
                  "properties": {
                    "legs": {
                      "type": "integer",
                      "description": "Legs"
                    },
                    "name": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "name"
                  ]
                },
                "dog": {
                  "type": "object",
                  "description": "Dog",
                  "properties": {
                    "legs": {
                      "type": "integer",
                      "description": "Legs"
                    },
                    "name": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "name"
                  ]
                },
                "animal": {
                  "type": "object",
                  "description": "Animal",
                  "oneOf": [
                    {
                      "type": "object",
                      "allOf": [
                        {
                          "type": "object",
                          "properties": {
                            "type": {
                              "const": "cat"
                            }
                          }
                        },
                        {
                          "$ref": "#/definitions/cat"
                        }
                      ]
                    },
                    {
                      "type": "object",
                      "allOf": [
                        {
                          "type": "object",
                          "properties": {
                            "type": {
                              "const": "dog"
                            }
                          }
                        },
                        {
                          "$ref": "#/definitions/dog"
                        }
                      ]
                    }
                  ]
                }
              }
            }
        """
    }

    @Test
    fun `should generate schema for Animal list with inline refs`() {
        val schema = jsonSchemaOf<List<Animal>>(inlineRefs = true)
        val schemaJson = testJson.encodeToString(schema)

        // then
        schemaJson shouldEqualJson $$"""
            {
              "type": "array",
              "items": {
                "type": "object",
                "description": "Animal",
                "oneOf": [
                  {
                    "type": "object",
                    "description": "Cat",
                    "properties": {
                      "type": {
                        "const": "cat"
                      },
                      "legs": {
                        "type": "integer",
                        "description": "Legs"
                      },
                      "name": {
                        "type": "string"
                      }
                    },
                    "required": [
                      "name"
                    ]
                  },
                  {
                    "type": "object",
                    "description": "Dog",
                    "properties": {
                      "type": {
                        "const": "dog"
                      },
                      "legs": {
                        "type": "integer",
                        "description": "Legs"
                      },
                      "name": {
                        "type": "string"
                      }
                    },
                    "required": [
                      "name"
                    ]
                  }
                ]
              }
            }
        """
    }

    /**
     * Test class `Foo` containing monetary amounts.
     *
     * Note: Normally the [Title] and [Description] is already set on the [Money],
     * so we want to test if we can override it.
     */
    @Serializable
    @SerialName("foo")
    @Description("A container of monetary amounts")
    @Suppress("unused") // it is used to generate schema
    class Foo(
        @Title("Money 1, without property description")
        val money1: Money,
        @Title("Money 2, with property description")
        @Description("A monetary amount with property description")
        var money2: Money
    )

    @Test
    fun `should prioritize title and description set on property over the one set for the whole class`() {
        val schema = jsonSchemaOf<Foo>()
        testJson.encodeToString(schema) shouldEqualJson $$"""
            {
              "type": "object",
              "description": "A container of monetary amounts",
              "properties": {
                "money1": {
                  "type": "string",
                  "title": "Money 1, without property description",
                  "description": "A monetary amount",
                  "pattern": "^-?[0-9]+\\.[0-9]{2}?$"
                },
                "money2": {
                  "type": "string",
                  "title": "Money 2, with property description",
                  "description": "A monetary amount with property description",
                  "pattern": "^-?[0-9]+\\.[0-9]{2}?$"
                }
              },
              "required": [
                "money1",
                "money2"
              ]
            }
        """
    }

    @Serializable
    @Suppress("unused") // it is used to generate schema
    class Bar(
        val foo: Foo
    )

    /**
     * It seems that `additionalProperties: false` is preferred in the Open AI API documentation.
     */
    @Test
    fun `should output additionalProperties keyword`() {
        val schema = jsonSchemaOf<Bar>(
            additionalProperties = false
        )
        testJson.encodeToString(schema) shouldEqualJson $$"""
            {
              "type": "object",
              "properties": {
                "foo": {
                  "$ref": "#/definitions/foo"
                }
              },
              "required": [
                "foo"
              ],
              "definitions": {
                "foo": {
                  "type": "object",
                  "description": "A container of monetary amounts",
                  "properties": {
                    "money1": {
                      "type": "string",
                      "title": "Money 1, without property description",
                      "description": "A monetary amount",
                      "pattern": "^-?[0-9]+\\.[0-9]{2}?$"
                    },
                    "money2": {
                      "type": "string",
                      "title": "Money 2, with property description",
                      "description": "A monetary amount with property description",
                      "pattern": "^-?[0-9]+\\.[0-9]{2}?$"
                    }
                  },
                  "required": [
                    "money1",
                    "money2"
                  ],
                  "additionalProperties": false
                }
              },
              "additionalProperties": false
            }
        """
    }

    @Serializable
    @SerialName("node")
    @Suppress("unused") // used in serialization
    class Node(
        @Description("Value stored in the node")
        val value: Int,
        @Description("Left child node")
        val left: Node? = null,
        @Description("Right child node")
        val right: Node? = null
    )

    @Test
    fun `should generate schema of recursive structure`() {
        val schema = jsonSchemaOf<Node>()
        testJson.encodeToString(schema) shouldEqualJson $$"""
            {
              "type": "object",
              "properties": {
                "value": {
                  "type": "integer",
                  "description": "Value stored in the node"
                },
                "left": {
                   "$ref": "#",
                   "description": "Left child node"
                 },
                 "right": {
                   "$ref": "#",
                   "description": "Right child node"
                 }
              },
              "required": ["value"]
            }
        """
    }

    @Test
    fun `should create schema of list of recursive structures`() {
        val schema = jsonSchemaOf<List<Node>>()
        testJson.encodeToString(schema) shouldEqualJson $$"""
            {
              "type": "array",
              "items": {
                "$ref": "#/definitions/node"
              },
              "definitions": {
                "node": {
                  "type": "object",
                  "properties": {
                    "value": {
                      "type": "integer",
                      "description": "Value stored in the node"
                    },
                    "left": {
                      "$ref": "#/definitions/node",
                      "description": "Left child node"
                    },
                    "right": {
                      "$ref": "#/definitions/node",
                      "description": "Right child node"
                    }
                  },
                  "required": [
                    "value"
                  ]
                }
              }
            }
        """
    }

    @Test
    fun `should override title and description of the class`() {
        val schema = jsonSchemaOf<Foo>(
            title = "New title",
            description = "New description"
        )
        testJson.encodeToString(schema) shouldEqualJson $$"""
            {
              "type": "object",
              "title": "New title",
              "description": "New description",
              "properties": {
                "money1": {
                  "type": "string",
                  "title": "Money 1, without property description",
                  "description": "A monetary amount",
                  "pattern": "^-?[0-9]+\\.[0-9]{2}?$"
                },
                "money2": {
                  "type": "string",
                  "title": "Money 2, with property description",
                  "description": "A monetary amount with property description",
                  "pattern": "^-?[0-9]+\\.[0-9]{2}?$"
                }
              },
              "required": [
                "money1",
                "money2"
              ]
            }
        """
    }

}
