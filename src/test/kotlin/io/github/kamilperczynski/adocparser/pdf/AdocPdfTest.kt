package io.github.kamilperczynski.adocparser.pdf

import io.github.kamilperczynski.adocparser.AdocParser
import io.github.kamilperczynski.adocparser.ast.AdocAST
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet
import io.github.kamilperczynski.adocparser.stylesheet.yaml.YamlAdocStylesheet
import io.github.kamilperczynski.adocparser.stylesheet.yaml.parseYamlStylesheet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

private const val TOTAL = "Total"
private const val PARSING = "Parsing"
private const val PRINTING = "Printing"
private const val P90 = "P90"
private const val MEDIAN = "Median"

private const val EPOCHS = 30

private val totalTimes = mutableListOf<Long>()
private val parsingTimes = mutableListOf<Long>()
private val printingTimes = mutableListOf<Long>()

class AdocPdfTest {

    @Test
    fun testMedianAndPercentiles() {
        val sortedTotalTimes = totalTimes.sorted()
        val sortedParsingTimes = parsingTimes.sorted()
        val sortedPrintingTimes = printingTimes.sorted()

        val medianTotal = findPercentile(sortedTotalTimes, 0.5)
        val medianParsing = findPercentile(sortedParsingTimes, 0.5)
        val medianPrinting = findPercentile(sortedPrintingTimes, 0.5)

        val percentile90Total = findPercentile(sortedTotalTimes, 0.9)
        val percentile90Parsing = findPercentile(sortedParsingTimes, 0.9)
        val percentile90Printing = findPercentile(sortedPrintingTimes, 0.9)

        println("-----------------------------------------------------------------")
        printStats(medianTotal, medianParsing, medianPrinting, MEDIAN)
        printStats(percentile90Total, percentile90Parsing, percentile90Printing, P90)

        assertThat(medianTotal).isLessThanOrEqualTo(150)
        assertThat(medianParsing).isLessThanOrEqualTo(40)
        assertThat(medianPrinting).isLessThanOrEqualTo(110)

        assertThat(percentile90Total).isLessThanOrEqualTo(250)
        assertThat(percentile90Parsing).isLessThanOrEqualTo(80)
        assertThat(percentile90Printing).isLessThanOrEqualTo(170)
    }

    @RepeatedTest(EPOCHS)
    @Order(1)
    fun testPrintPdf() {
        val file = Paths.get("./LibraryTest-test.pdf")

        val yamlStylesheet = parseYamlStylesheet(
            this.javaClass.classLoader.getResourceAsStream("./stylesheet.yaml")!!
        )

        val stylesheet: AdocStylesheet = YamlAdocStylesheet(
            Paths.get("./fonts").toAbsolutePath(),
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

        val totalTime = parsingTime + printingMillis
        totalTimes.add(totalTime)
        parsingTimes.add(parsingTime)
        printingTimes.add(printingMillis)

        printStats(totalTime, parsingTime, printingMillis)
    }

}

fun findPercentile(sorted: List<Long>, percentile: Double): Long {
    val index = (percentile * sorted.size).toInt()
    return sorted[index]
}

private fun printStats(totalTime: Long, parsingTime: Long, printingMillis: Long, stat: String = TOTAL) {
    val sb = StringBuilder()
        .append(stat.padEnd(6))
        .append(totalTime.toString().padStart(5))
        .append(" ms | ")
        .append(PARSING)
        .append(parsingTime.toString().padStart(5))
        .append(" ms | ")
        .append(PRINTING)
        .append(printingMillis.toString().padStart(5))
        .append(" ms")

    println(sb)
}
