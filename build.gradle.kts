import gg.jte.ContentType
import java.nio.file.Paths

plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("gg.jte.gradle") version "3.1.1"
}

dependencies {
    implementation("io.javalin:javalin:6.6.0")
    implementation("io.javalin:javalin-rendering:6.6.0")
    implementation("gg.jte:jte:3.2.1")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("com.google.api-client:google-api-client:1.15.1")
    implementation("com.google.apis:google-api-services-youtube:v3-rev222-1.25.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.39.0")
    implementation("io.github.cdimascio:dotenv-java:3.2.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.3")
}

repositories {
    mavenCentral()
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
    options.encoding = "UTF-8"
}

tasks.shadowJar{
    dependsOn(tasks.precompileJte)
    from(jte.targetDirectory)
}

application {
    mainClass = "de.professorsam.songrequest.SongRequest"
}

jte {
    sourceDirectory = Paths.get(project.projectDir.absolutePath, "src", "main", "jte")
    targetDirectory = Paths.get(project.projectDir.absolutePath, "build", "generated", "jte")
    contentType = ContentType.Html
    precompile()
}

