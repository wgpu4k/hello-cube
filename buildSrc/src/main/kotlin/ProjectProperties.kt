import org.gradle.api.Project

val Project.useAndroid: Boolean
    get() = (findProperty("useAndroid") as String?).equals("true", ignoreCase = true)