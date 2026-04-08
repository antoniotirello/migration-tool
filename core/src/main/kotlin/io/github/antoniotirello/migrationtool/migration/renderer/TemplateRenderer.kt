package io.github.antoniotirello.migrationtool.migration.renderer

import com.github.mustachejava.DefaultMustacheFactory
import java.io.StringReader
import java.io.StringWriter

class TemplateRenderer {

    private val mf = DefaultMustacheFactory()

    fun render(template: String, context: Map<String, Any>): String {
        val mustache = mf.compile(StringReader(template), "template")

        val writer = StringWriter()
        mustache.execute(writer, context).flush()

        return writer.toString()
    }
}