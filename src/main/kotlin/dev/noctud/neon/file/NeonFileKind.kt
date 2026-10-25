package dev.noctud.neon.file

import java.util.Locale

/**
 * What a .neon file is for, decided from its name alone so it is free to ask.
 * Config features (parameters, services, PHP classes) only apply where they mean something.
 */
enum class NeonFileKind {
    /** Nette DI config — anything not recognised as one of the kinds below. */
    CONFIG,

    /** PHPStan config (`*phpstan*.neon`), with its own parameter set. */
    PHPSTAN,

    /** Translation catalogue (`messages.en_US.neon`), `%name%` placeholders. */
    TRANSLATION,

    /** ICU translation catalogue (`messages+intl-icu.en_US.neon`), `{name}` placeholders. */
    ICU_TRANSLATION;

    val isTranslation: Boolean
        get() = this == TRANSLATION || this == ICU_TRANSLATION

    companion object {
        // <domain>.<locale>.neon, the naming translation loaders split catalogues by.
        // Locale is a BCP 47 or POSIX tag: language, optional script, optional region (sk, sk_SK, pt-BR, zh-Hant, es-419)
        private val TRANSLATION_NAME = Regex(
            "(.+)\\.([a-z]{2,3})(?:[-_][a-z]{4})?(?:[-_](?:[a-z]{2}|[0-9]{3}))?\\.neon",
            RegexOption.IGNORE_CASE
        )

        private const val ICU_SUFFIX = "+intl-icu"

        // ISO 639-1 codes plus three-letter ones the JDK knows (fil, yue), so `config.dev.neon` is not a locale
        private val LANGUAGES: Set<String> = Locale.getISOLanguages().toSet() + Locale.getAvailableLocales().map { it.language }

        fun of(fileName: String): NeonFileKind {
            if (fileName.contains("phpstan", ignoreCase = true) && fileName.endsWith(".neon")) return PHPSTAN

            val match = TRANSLATION_NAME.matchEntire(fileName) ?: return CONFIG
            val (domain, language) = match.destructured
            if (language.lowercase() !in LANGUAGES) return CONFIG

            return if (domain.endsWith(ICU_SUFFIX)) ICU_TRANSLATION else TRANSLATION
        }
    }
}
