package io.github.kamilperczynski.adocparser.pdf

import io.github.kamilperczynski.adocparser.AdocParser
import io.github.kamilperczynski.adocparser.ast.AdocAST
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet
import io.github.kamilperczynski.adocparser.stylesheet.yaml.YamlAdocStylesheet
import io.github.kamilperczynski.adocparser.stylesheet.yaml.parseYamlStylesheet
import org.junit.jupiter.api.RepeatedTest
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

class AdocPdfTest {

    @RepeatedTest(20)
    fun testPrintPdf() {
        val file = Paths.get("./LibraryTest-test.pdf")

        val yamlStylesheet = parseYamlStylesheet(
            this.javaClass.classLoader.getResourceAsStream("./stylesheet.yaml")!!
        )

        val stylesheet: AdocStylesheet = YamlAdocStylesheet(
            Paths.get("/Users/kperczynski/fonties"),
            yamlStylesheet
        )

        if (Files.exists(file)) {
            Files.delete(file)
        }

        val adoc = AdocPdfTest::class.java.classLoader
            .getResourceAsStream("./doc2.adoc")?.let { String(it.readAllBytes()) }
            ?: throw IllegalStateException("Cannot read file")

        val ast: AdocAST

        val parsingTime = measureTimeMillis {
            ast = AdocParser(adoc).parseAdocAst()
        }

        stylesheet.registerFonts()

        val printingMillis = measureTimeMillis {
            AdocPdf(stylesheet).print(ast, Files.newOutputStream(file))
        }

        print("Parsing time: $parsingTime ms\nPrinting time: $printingMillis ms\n")
    }

}
