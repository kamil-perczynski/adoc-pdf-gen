package io.github.kamilperczynski.adocparser.pdf

import io.github.kamilperczynski.adocparser.ast.AdocAST
import io.github.kamilperczynski.adocparser.ast.parseAsciiDocAST
import io.github.kamilperczynski.adocparser.runAsciiDocAntlr
import org.junit.jupiter.api.RepeatedTest
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

class AdocPdfTest {

    @RepeatedTest(20)
    fun testPrintPdf() {
        val file = Paths.get("./LibraryTest-test.pdf")

        if (Files.exists(file)) {
            Files.delete(file)
        }

        val adoc = AdocPdfTest::class.java.classLoader
            .getResourceAsStream("./doc2.adoc")?.let { String(it.readAllBytes()) }
            ?: throw IllegalStateException("Cannot read file")

        val ast: AdocAST

        val parsingTime = measureTimeMillis {
            val parser = runAsciiDocAntlr(adoc)
            ast = parseAsciiDocAST(parser)
        }


        val printingMillis = measureTimeMillis {
            AdocPdf().print(ast, Files.newOutputStream(file))
        }

        print("Parsing time: $parsingTime ms\nPrinting time: $printingMillis ms\n")
    }

}
