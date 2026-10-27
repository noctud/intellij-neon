package dev.noctud.neon.ext

import org.junit.Assert
import org.junit.Test

class EnvFileParserTest {
    @Test
    fun testDeclarations() {
        val text = """
            # comment
            DB_HOST=localhost
            export DB_USER=root
              DB_PASS = "secret"
            app_env=dev

            NOT A VARIABLE
            #COMMENTED=1
        """.trimIndent()

        val variables = EnvFileParser.parse(text)
        Assert.assertEquals(listOf("DB_HOST", "DB_USER", "DB_PASS", "app_env"), variables.map { it.name })
        for (variable in variables) {
            Assert.assertEquals(variable.name, text.substring(variable.offset, variable.offset + variable.name.length))
        }
    }

    @Test
    fun testEnvParameter() {
        Assert.assertTrue(EnvFileParser.isEnvParameter("env"))
        Assert.assertTrue(EnvFileParser.isEnvParameter("env.DB_HOST"))
        Assert.assertFalse(EnvFileParser.isEnvParameter("environment"))
        Assert.assertFalse(EnvFileParser.isEnvParameter("DB_HOST"))
    }
}
