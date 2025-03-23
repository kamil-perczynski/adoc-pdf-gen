package io.github.kamilperczynski.adocparser.stylesheet.yaml

import com.lowagie.text.FontFactory
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class YamlStylesheetTest {
    @Test
    fun `test read yaml stylesheet document`() {
        val yamlStylesheet = parseYamlStylesheet(
            this.javaClass.classLoader.getResourceAsStream("./stylesheet.yaml")!!
        )

        val stylesheet: AdocStylesheet = YamlAdocStylesheet(
            Paths.get("/Users/kperczynski/fonties"),
            yamlStylesheet
        )

        stylesheet.registerFonts()

        val families = FontFactory.getRegisteredFamilies()
        val registeredFonts = FontFactory.getRegisteredFonts()

        assertThat(families).contains("ubuntu", "jetbrains mono")
        assertThat(registeredFonts).contains(
            "ubuntu-bold",
            "ubuntu-italic",
            "ubuntu-bolditalic",
            "jetbrainsmono-bold",
            "jetbrainsmono-italic",
            "jetbrainsmono-bolditalic"
        )
    }
}
