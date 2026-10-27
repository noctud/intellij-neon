package dev.noctud.neon.editor

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class NeonColorsPage : ColorSettingsPage {
    override fun getDisplayName(): String {
        return "Neon"
    }

    override fun getIcon(): Icon? {
        return null
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return ATTRS
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return arrayOf()
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return NeonSyntaxHighlighter()
    }

    override fun getDemoText(): String {
        return "common:\n" +
                "\tparameters:\n" +
                "\t\tdays: [ Mon, Tue, Wed ]\n" +
                "\t\tdays2:\n" +
                "\t\t\t- Mon # Monday\n" +
                "\t\t\t- Tue\n" +
                "\t\t\t- Wed\n" +
                "\n" +
                "\t\t# Third type\n" +
                "\t\tdayNames: { mon: Monday, tue: Tuesday }\n" +
                "\n" +
                "\t\taddress:\n" +
                "\t\t\tstreet: 742 Evergreen Terrace\n" +
                "\t\t\tcity: Springfield\n" +
                "\t\t\tcountry: USA\n" +
                "\n" +
                "\t\tphones: { home: 555-6528, work: 555-7334 }\n" +
                "\t\tphp:\n" +
                "\t\t\tdate.timezone: Europe/Prague\n" +
                "\t\t\tzlib.output_compression: yes  # use gzip\n" +
                "\t\t\tdisplay_errors: off\n" +
                "\t\t\topcache.validate_timestamps: null\n" +
                "\n" +
                "\t\tchildren:\n" +
                "\t\t\t- Bart\n" +
                "\t\t\t- Lisa\n" +
                "\t\t\t- %children.third%\n" +
                "\n" +
                "\t\tentity: Column(<named_arg>type</named_arg>=\"integer\")\n" +
                "\n" +
                "\tservices:\n" +
                "\t\tmyService: Nette\\Object(\"AHOJ\")\n" +
                "\t\tmyService2: Nette\\Something(@myService, 1)\n" +
                "\n" +
                "\n" +
                "production < common:\n" +
                "\tservices:\n" +
                "\t\tauthenticator: Nette\\Authenticator(@db)\n" +
                "\n" +
                "development < common:\n"
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> {
        return TAGS
    }

    companion object {
        val ATTRS: Array<AttributesDescriptor> = arrayOf<AttributesDescriptor>(
            AttributesDescriptor("Bad character", NeonSyntaxHighlighter.UNKNOWN),
            AttributesDescriptor("Comment", NeonSyntaxHighlighter.COMMENT),
            AttributesDescriptor("Key", NeonSyntaxHighlighter.IDENTIFIER),
            AttributesDescriptor("Punctuation", NeonSyntaxHighlighter.INTERPUNCTION),
            AttributesDescriptor("Number", NeonSyntaxHighlighter.NUMBER),
            AttributesDescriptor("Datetime", NeonSyntaxHighlighter.DATETIME),
            AttributesDescriptor("Keyword", NeonSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("Boolean true (true, yes, on)", NeonSyntaxHighlighter.TRUE),
            AttributesDescriptor("Boolean false (false, no, off)", NeonSyntaxHighlighter.FALSE),
            AttributesDescriptor("String", NeonSyntaxHighlighter.STRING),
            AttributesDescriptor("Class reference", NeonSyntaxHighlighter.CLASSNAME),
            AttributesDescriptor("Variable", NeonSyntaxHighlighter.VARIABLE),
            AttributesDescriptor("Named argument", NeonSyntaxHighlighter.NAMED_ARGUMENT),
            AttributesDescriptor("Service reference / PHPStan identifier", NeonSyntaxHighlighter.PHPSTAN_IDENTIFIER),
        )

        private val TAGS: Map<String, TextAttributesKey> = mapOf(
            "named_arg" to NeonSyntaxHighlighter.NAMED_ARGUMENT,
        )
    }
}
