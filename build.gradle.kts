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

@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.kotlin.plugin.serialization)
  alias(libs.plugins.kotlin.plugin.power.assert)
  alias(libs.plugins.kotlinx.binary.compatibility.validator)
  alias(libs.plugins.dokka)
  alias(libs.plugins.versions)
  `maven-publish`
  signing
  alias(libs.plugins.publish)
}

val githubAccount = "xemantic"

val javaTarget = libs.versions.javaTarget.get()
val kotlinTarget = KotlinVersion.fromVersion(libs.versions.kotlinTarget.get())

val isReleaseBuild = !project.version.toString().endsWith("-SNAPSHOT")
val githubActor: String? by project
val githubToken: String? by project
val signingKey: String? by project
val signingPassword: String? by project
val sonatypeUser: String? by project
val sonatypePassword: String? by project

println("""
+--------------------------------------------  
| Project: ${project.name}
| Version: ${project.version}
| Release build: $isReleaseBuild
+--------------------------------------------
""".trimIndent()
)

repositories {
  mavenCentral()
}

kotlin {

  explicitApi()

  compilerOptions {
    freeCompilerArgs.add("-Xmulti-dollar-interpolation")
    extraWarnings.set(true)
  }

  jvm {
    compilerOptions {
      apiVersion = kotlinTarget
      languageVersion = kotlinTarget
      jvmTarget = JvmTarget.fromTarget(javaTarget)
      freeCompilerArgs.add("-Xjdk-release=$javaTarget")
      progressiveMode = true
    }
  }

  js {
    browser()
    nodejs()
    binaries.library()
  }


  wasmJs {
    browser()
    nodejs()
    //d8()
    binaries.library()
  }

  wasmWasi {
    nodejs()
    binaries.library()
  }

  // native, see https://kotlinlang.org/docs/native-target-support.html
  // tier 1
  macosX64()
  macosArm64()
  iosSimulatorArm64()
  iosX64()
  iosArm64()

  // tier 2
  linuxX64()
  linuxArm64()
  watchosSimulatorArm64()
  watchosX64()
  watchosArm32()
  watchosArm64()
  tvosSimulatorArm64()
  tvosX64()
  tvosArm64()

  // tier 3
  androidNativeArm32()
  androidNativeArm64()
  androidNativeX86()
  androidNativeX64()
  mingwX64()
  watchosDeviceArm64()

  @OptIn(ExperimentalSwiftExportDsl::class)
  swiftExport {}

  sourceSets {

    commonMain {
      dependencies {
        implementation(libs.kotlinx.serialization.json)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotest.assertions.core)
        implementation(libs.kotest.assertions.json)
        implementation(libs.kotlinx.datetime)
        implementation(libs.bignum)
      }
    }

  }

}

// skip test for certain targets which are not fully supported by kotest
tasks.named("compileTestKotlinWasmWasi") { enabled = false}
tasks.named("compileTestKotlinAndroidNativeArm32") { enabled = false }
tasks.named("compileTestKotlinAndroidNativeArm64") { enabled = false }
tasks.named("compileTestKotlinAndroidNativeX86") { enabled = false }
tasks.named("compileTestKotlinWatchosDeviceArm64") { enabled = false }
tasks.named("compileTestKotlinAndroidNativeX64") { enabled = false }

// skip tests which require XCode components to be installed
tasks.named("tvosSimulatorArm64Test") { enabled = false }
tasks.named("watchosSimulatorArm64Test") { enabled = false }

// https://kotlinlang.org/docs/dokka-migration.html#adjust-configuration-options
dokka {
  pluginsConfiguration.html {
    footerMessage.set("© Xemantic 2024")
  }
}

if (isReleaseBuild) {

  // workaround for KMP/gradle signing issue
  // https://github.com/gradle/gradle/issues/26091
  tasks {
    withType<PublishToMavenRepository> {
      dependsOn(withType<Sign>())
    }
  }

  // Resolves issues with .asc task output of the sign task of native targets.
  // See: https://github.com/gradle/gradle/issues/26132
  // And: https://youtrack.jetbrains.com/issue/KT-46466
  tasks.withType<Sign>().configureEach {
    val pubName = name.removePrefix("sign").removeSuffix("Publication")

    // These tasks only exist for native targets, hence findByName() to avoid trying to find them for other targets

    // Task ':linkDebugTest<platform>' uses this output of task ':sign<platform>Publication' without declaring an explicit or implicit dependency
    tasks.findByName("linkDebugTest$pubName")?.let {
      mustRunAfter(it)
    }
    // Task ':compileTestKotlin<platform>' uses this output of task ':sign<platform>Publication' without declaring an explicit or implicit dependency
    tasks.findByName("compileTestKotlin$pubName")?.let {
      mustRunAfter(it)
    }
  }

  signing {
    useInMemoryPgpKeys(
      signingKey,
      signingPassword
    )
    sign(publishing.publications)
  }

  nexusPublishing {
    repositories {
      sonatype {  //only for users registered in Sonatype after 24 Feb 2021
        nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
        snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        username.set(sonatypeUser)
        password.set(sonatypePassword)
      }
    }
  }

}
