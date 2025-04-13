package io.github.kamilperczynski.adocparser.pdf

import io.github.kamilperczynski.adocparser.AdocParser
import io.github.kamilperczynski.adocparser.ast.AdocAST
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet
import io.github.kamilperczynski.adocparser.stylesheet.HashmapFontsCache
import io.github.kamilperczynski.adocparser.stylesheet.yaml.YamlAdocStylesheet
import io.github.kamilperczynski.adocparser.stylesheet.yaml.parseYamlStylesheet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

private const val TOTAL = "Total"
private const val PARSING = "Parsing"
private const val PRINTING = "Printing"
private const val P90 = "P90"
private const val MEDIAN = "Median"

private const val EPOCHS = 30

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdocPdfTests {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class RomanTypographyTest : AdocPdfTest() {

        override val sourceAdocContent: String
            get() = AdocPdfTest::class.java.classLoader
                .getResourceAsStream("./doc2.adoc")?.let { String(it.readAllBytes()) }
                ?: throw IllegalStateException("Cannot read file")

        override val targetPdfFile: Path
            get() = Paths.get("./AdocPdf-Test-Roman.pdf")

        override val stylesheet: AdocStylesheet
            get() {
                val yamlStylesheet =
                    parseYamlStylesheet(this.javaClass.classLoader.getResourceAsStream("./stylesheet.yaml")!!)

                yamlStylesheet.base?.fontFamily = "times"

                val stylesheet: AdocStylesheet =
                    YamlAdocStylesheet(Paths.get("./fonts").toAbsolutePath(), yamlStylesheet, HashmapFontsCache())

                return stylesheet
            }

        override fun assertStats() {
            assertThat(totalTimes.p50).isLessThanOrEqualTo(80)
            assertThat(parsingTimes.p50).isLessThanOrEqualTo(30)
            assertThat(printingTimes.p50).isLessThanOrEqualTo(50)

            assertThat(totalTimes.p90).isLessThanOrEqualTo(80)
            assertThat(parsingTimes.p90).isLessThanOrEqualTo(30)
            assertThat(printingTimes.p90).isLessThanOrEqualTo(50)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class InterTypographyTest : AdocPdfTest() {

        override val sourceAdocContent: String
            get() = AdocPdfTest::class.java.classLoader
                .getResourceAsStream("./doc2.adoc")?.let { String(it.readAllBytes()) }
                ?: throw IllegalStateException("Cannot read file")

        override val targetPdfFile: Path
            get() = Paths.get("./AdocPdf-Test-Inter.pdf")

        override val stylesheet: AdocStylesheet
            get() {
                val yamlStylesheet =
                    parseYamlStylesheet(this.javaClass.classLoader.getResourceAsStream("./stylesheet.yaml")!!)

                val stylesheet: AdocStylesheet =
                    YamlAdocStylesheet(Paths.get("./fonts").toAbsolutePath(), yamlStylesheet, HashmapFontsCache())

                return stylesheet
            }

        override fun assertStats() {
            assertThat(totalTimes.p50).isLessThanOrEqualTo(140)
            assertThat(parsingTimes.p50).isLessThanOrEqualTo(40)
            assertThat(printingTimes.p50).isLessThanOrEqualTo(100)

            assertThat(totalTimes.p90).isLessThanOrEqualTo(300)
            assertThat(parsingTimes.p90).isLessThanOrEqualTo(150)
            assertThat(printingTimes.p90).isLessThanOrEqualTo(150)
        }
    }

}


abstract class AdocPdfTest {

    internal val totalTimes = Stat()
    internal val parsingTimes = Stat()
    internal val printingTimes = Stat()

    @Test
    fun testMedianAndPercentiles() {
        println("-----------------------------------------------------------------")
        printStats(totalTimes.p50, parsingTimes.p50, printingTimes.p50, MEDIAN)
        printStats(totalTimes.p90, parsingTimes.p90, printingTimes.p90, P90)

        assertStats()
    }

    @RepeatedTest(EPOCHS)
    @Order(1)
    fun testPrintPdf() {
        if (Files.exists(targetPdfFile)) {
            Files.delete(targetPdfFile)
        }

        val ast: AdocAST

        val parsingTime = measureTimeMillis {
            ast = AdocParser(sourceAdocContent).parseAdocAst()
        }

        stylesheet.registerFonts()

        val printingMillis = measureTimeMillis {
            AdocPdf(stylesheet).print(ast, Files.newOutputStream(targetPdfFile))
        }

        val totalTime = parsingTime + printingMillis
        totalTimes.add(totalTime)
        parsingTimes.add(parsingTime)
        printingTimes.add(printingMillis)

        printStats(totalTime, parsingTime, printingMillis)
    }

    abstract val sourceAdocContent: String
    abstract val targetPdfFile: Path
    abstract val stylesheet: AdocStylesheet

    abstract fun assertStats()
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

class Stat {

    private val data: MutableList<Long> = mutableListOf()

    fun add(value: Long) {
        data.add(value)
    }

    val p50: Long
        get() = percentile(0.5)

    val p90: Long
        get() = percentile(0.9)

    fun percentile(percentile: Double): Long {
        val sorted = data.sorted()
        val index = (percentile * sorted.size).toInt()
        return sorted[index]
    }

}
