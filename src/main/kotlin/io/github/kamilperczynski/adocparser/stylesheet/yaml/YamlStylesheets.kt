package io.github.kamilperczynski.adocparser.stylesheet.yaml

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer
import java.io.InputStream


fun parseYamlStylesheet(reader: InputStream): YamlStylesheet {
    val yaml = Yaml(
        Constructor(YamlStylesheet::class.java, LoaderOptions()),
        Representer(DumperOptions()).also {
            it.propertyUtils.isSkipMissingProperties = true
        },
        DumperOptions()
    )

    return reader.use {
        yaml.load(it) as YamlStylesheet
    }
}
