import net.sourceforge.plantuml.SourceFileReader
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import java.io.File

defaultTasks("clean", "test", "aggregate")

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.graalvm.buildtools.native") version "0.10.3"
	id("net.serenity-bdd.serenity-gradle-plugin") version "4.0.14"
}

// Add PlantUML library to the buildscript classpath
buildscript {
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("net.sourceforge.plantuml:plantuml:1.2024.2")
	}
}

group = "com.tnfigueiredo"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
	implementation("net.sourceforge.plantuml:plantuml:1.2024.2")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("net.serenity-bdd:serenity-core:4.0.21")
	testImplementation("net.serenity-bdd:serenity-cucumber:4.0.21")
	testImplementation("net.serenity-bdd:serenity-junit5:4.0.21")
	testImplementation("net.serenity-bdd:serenity-spring:4.0.21")
	testImplementation("org.junit.vintage:junit-vintage-engine:5.10.1")
	testImplementation("io.cucumber:cucumber-spring:7.14.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
}

graalvmNative {
	binaries {
		named("main") {
			buildArgs.add("--enable-url-protocols=http,https")
		}
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging.showStandardStreams = true
	finalizedBy("aggregate")
}

tasks.register("generatePlantUML") {
	description = "Generate SVG diagrams from existing PlantUML files"
	group = "documentation"

	// Use project.rootDir to resolve paths correctly
	val inputDir = File(project.rootDir, "../docs/diagrams") // Absolute path to diagrams
	val outputDir = File(project.rootDir, "../docs/assets")  // Absolute path to assets

	doLast {
		// Process each .puml file
		inputDir.walkTopDown().filter { it.extension == "puml" }.forEach { pumlFile ->
			val outputFile = File(outputDir, "${pumlFile.nameWithoutExtension}.svg")
			println("Generating SVG for ${pumlFile.name} -> ${outputFile}")

			try {
				val sourceFileReader = SourceFileReader(
					pumlFile,
					outputFile.parentFile,
					FileFormatOption(FileFormat.SVG)
				)

				val result = sourceFileReader.generatedImages
				if (result.isEmpty()) {
					println("Failed to generate SVG for ${pumlFile.name}")
				} else {
					println("Successfully generated: ${outputFile}")
				}
			} catch (e: Exception) {
				println("Error generating SVG for ${pumlFile.name}: ${e.message}")
			}
		}
	}
}

tasks.named("build") {
	finalizedBy("generatePlantUML")
}

tasks.register("saveTestResults") {
	group = "reporting"
	description = "Cleans the tests directory and copies serenity reports to it."

	val testsDir = File(project.rootDir, "../tests")
	val serenityReportsDir = file("target/site/serenity")

	doLast {
		// Delete all files in the 'tests' directory
		if (testsDir.exists()) {
			println("Deleting files in $testsDir")
			testsDir.deleteRecursively()
			testsDir.mkdirs()
		} else {
			println("$testsDir does not exist. Creating it...")
			testsDir.mkdirs()
		}

		// Copy files from serenityReportsDir to testsDir
		if (serenityReportsDir.exists()) {
			println("Copying files from $serenityReportsDir to $testsDir")
			serenityReportsDir.copyRecursively(testsDir, overwrite = true)
			println("Files copied successfully!")
		} else {
			println("Source directory $serenityReportsDir does not exist!")
		}
	}
}