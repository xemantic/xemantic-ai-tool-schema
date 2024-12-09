val groupId = "com.xemantic.ai"
val name = "xemantic-ai-tool-schema"
val versionId = "0.1-SNAPSHOT"

rootProject.name = name
gradle.beforeProject {
  group = groupId
  version = versionId
}
