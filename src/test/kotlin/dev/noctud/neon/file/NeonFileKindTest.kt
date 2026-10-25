package dev.noctud.neon.file

import org.junit.Assert
import org.junit.Test

class NeonFileKindTest {
    @Test
    fun testConfig() {
        for (name in listOf(
            "config.neon", "services.neon", "common.neon",
            "config.local.neon", "common.prod.neon", "config.dev.neon", "config.test.neon", "config.dist.neon",
            "cs.neon", // locale without a domain is not a catalogue
        )) {
            Assert.assertEquals(name, NeonFileKind.CONFIG, NeonFileKind.of(name))
        }
    }

    @Test
    fun testPhpStan() {
        Assert.assertEquals(NeonFileKind.PHPSTAN, NeonFileKind.of("phpstan.neon"))
        Assert.assertEquals(NeonFileKind.PHPSTAN, NeonFileKind.of("phpstan-baseline.neon"))
    }

    @Test
    fun testTranslation() {
        for (name in listOf(
            "messages.cs.neon", "apiToken.sk_SK.neon", "messages.pt-BR.neon", "messages.pt-br.neon",
            "messages.zh-Hant.neon", "messages.sr_Latn_RS.neon", "messages.es-419.neon", "messages.fil.neon",
            "admin.forms.en_US.neon",
        )) {
            Assert.assertEquals(name, NeonFileKind.TRANSLATION, NeonFileKind.of(name))
        }
    }

    @Test
    fun testIcuTranslation() {
        Assert.assertEquals(NeonFileKind.ICU_TRANSLATION, NeonFileKind.of("messages+intl-icu.pt-BR.neon"))
        Assert.assertEquals(NeonFileKind.ICU_TRANSLATION, NeonFileKind.of("messages+intl-icu.en.neon"))
    }

    @Test
    fun testIsTranslation() {
        Assert.assertTrue(NeonFileKind.TRANSLATION.isTranslation)
        Assert.assertTrue(NeonFileKind.ICU_TRANSLATION.isTranslation)
        Assert.assertFalse(NeonFileKind.CONFIG.isTranslation)
        Assert.assertFalse(NeonFileKind.PHPSTAN.isTranslation)
    }
}
