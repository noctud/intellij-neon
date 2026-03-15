package dev.noctud.neon.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import dev.noctud.neon.completion.schema.SchemaProvider
import dev.noctud.neon.lexer.NeonTokenTypes
import dev.noctud.neon.parser.NeonElementTypes
import dev.noctud.neon.psi.elements.NeonFile
import dev.noctud.neon.psi.elements.NeonKey
import dev.noctud.neon.psi.elements.NeonKeyValPair
import dev.noctud.neon.psi.elements.NeonScalar
import java.util.*
import java.util.regex.Pattern

/**
 * Complete keywords
 */
class KeywordCompletionProvider : CompletionProvider<CompletionParameters?>() {
    // CompletionResultSet wants list of LookupElements
    private val KEYWORD_LOOKUPS: MutableList<LookupElementBuilder> = ArrayList<LookupElementBuilder>()
    private val knownKeys = HashMap<String?, Array<String?>?>()
    private val knownKeysPattern = HashMap<Pattern, Array<String?>?>()
    private val knownValues = HashMap<String?, Array<String?>?>()
    private val deprecatedKeys = HashMap<String?, String>()


    init {
        for (keyword in KEYWORDS) KEYWORD_LOOKUPS.add(LookupElementBuilder.create(keyword!!))

        knownKeys[""] = arrayOf<String?>(
            "parameters", "nette", "services", "php", "extensions", "application", "forms", "constants", "search",
            "http", "latte", "mail", "routing", "security", "session", "tracy", "database", "di", "decorator"
        )

        knownKeys["nette"] = arrayOf<String?>(
            "session",
            "application",
            "routing",
            "security",
            "mailer",
            "database",
            "forms",
            "latte",
            "container",
            "debugger"
        )

        knownKeys["application"] = arrayOf<String?>(
            "debugger", "errorPresenter", "catchExceptions", "mapping",
            "scanDirs", "scanComposer", "scanFilter", "silentLinks"
        )
        knownKeys["forms"] = arrayOf<String?>("messages")
        knownKeys["http"] = arrayOf<String?>("proxy", "headers", "frames", "csp", "cspReportOnly", "featurePolicy", "cookieSecure")
        knownKeys["latte"] = arrayOf<String?>("xhtml", "macros", "templateClass", "strictTypes")
        knownKeys["mail"] = arrayOf<String?>(
            "smtp",
            "host",
            "port",
            "username",
            "password",
            "secure",
            "timeout",
            "context",
            "clientHost",
            "persistent"
        )
        knownKeys["routing"] = arrayOf<String?>("debugger", "routes", "routeClass", "cache")
        knownKeys["security"] = arrayOf<String?>("debugger", "users", "roles", "resources")
        knownKeys["session"] = arrayOf<String?>(
            "debugger",
            "autoStart",
            "expiration",
            "handler",
            "cookieSamesite",
            "cookieDomain",
            "cookieLifetime",
            "cookiePath",
            "cookieSecure",
            "lazyWrite",
            "name",
            "savePath"
        )
        knownKeys["di"] = arrayOf<String?>("debugger", "accessors", "excluded", "parentClass", "export")
        knownKeys["tracy"] = arrayOf<String?>(
            "email",
            "fromEmail",
            "logSeverity",
            "editor",
            "browser",
            "errorTemplate",
            "strictMode",
            "maxLength",
            "maxDepth",
            "showLocation",
            "scream",
            "bar",
            "blueScreen",
            "showBar",
            "editorMapping",
            "netteMailer"
        )

        val databaseOptions: Array<String?> = arrayOf(
            "dsn",
            "user",
            "password",
            "options",
            "debugger",
            "explain",
            "reflection",
            "conventions",
            "autowired"
        )

        knownKeys["database"] = databaseOptions
        knownKeysPattern[Pattern.compile("^database\\|[\\w_-]+$")] = databaseOptions

        val searchOptions: Array<String?> =
            arrayOf<String?>("in", "files", "classes", "extends", "implements", "exclude", "tags")

        knownKeys["search"] = searchOptions
        knownKeysPattern[Pattern.compile("^search\\|[\\w_-]+$")] = searchOptions
        knownKeysPattern[Pattern.compile("^search\\|exclude$")] = arrayOf<String?>("classes", "extends", "implements")
        knownKeysPattern[Pattern.compile("^search\\|[\\w_-]+\\|exclude$")] = arrayOf<String?>("classes", "extends", "implements")

        knownKeysPattern[Pattern.compile("^decorator\\|[\\w_\\\\]+$")] = arrayOf<String?>("setup", "tags", "inject")

        knownKeysPattern[Pattern.compile("^di\\|export$")] = arrayOf<String?>("parameters", "tags", "types")

        val serviceKeys = arrayOf<String?>(
            "class",
            "create",
            "factory",
            "implement",
            "setup",
            "tags",
            "arguments",
            "autowired",
            "parameters",
            "inject",
            "imported",
            "alteration",
            "references",
            "tagged",
            "type"
        )

        knownKeysPattern[Pattern.compile("^services\\|([\\\\.\\w_-]+|#)$")] = serviceKeys

        knownValues["http|frames"] = arrayOf<String?>("DENY", "SAMEORIGIN", "ALLOW-FROM ")

        val cspKeys = arrayOf<String?>(
            "base-uri",
            "block-all-mixed-content",
            "child-src",
            "connect-src",
            "default-src",
            "font-src",
            "form-action",
            "frame-ancestors",
            "frame-src",
            "img-src",
            "manifest-src",
            "media-src",
            "object-src",
            "plugin-types",
            "require-sri-for",
            "sandbox",
            "script-src",
            "style-src",
            "upgrade-insecure-requests",
            "worker-src",
            "report-to"
        )

        knownKeysPattern[Pattern.compile("^http\\|csp$")] = cspKeys
        knownKeysPattern[Pattern.compile("^http\\|cspReportOnly$")] = cspKeys

        knownKeysPattern[Pattern.compile("^http\\|featurePolicy$")] = arrayOf<String?>(
            "autoplay",
            "camera",
            "encrypted-media",
            "fullscreen",
            "geolocation",
            "microphone",
            "midi",
            "payment",
            "vr"
        )

        deprecatedKeys["di|accessors"] = ""
        deprecatedKeys["nette|security|frames"] = "http|frames"
        deprecatedKeys["nette|mailer"] = "mail"
        deprecatedKeys["nette|container"] = "di"
        deprecatedKeys["nette|application"] = "application"
        deprecatedKeys["nette|cache"] = "cache"
        deprecatedKeys["nette|database"] = "database"
        deprecatedKeys["nette|forms"] = "forms"
        deprecatedKeys["nette|http"] = "http"
        deprecatedKeys["nette|latte"] = "latte"
        deprecatedKeys["nette|routing"] = "routing"
        deprecatedKeys["nette|security"] = "security"
        deprecatedKeys["nette|session"] = "session"
        deprecatedKeys["nette|debugger"] = "tracy"
    }

    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        results: CompletionResultSet
    ) {
        val curr = params.position.originalElement
        if (curr.node.elementType === NeonTokenTypes.NEON_COMMENT) {
            return
        }
        var hasSomething = false


        val prefixMatcher = results.prefixMatcher
        // hit autocompletion twice -> autodetect
//		if (params.getInvocationCount() >= 2)
        run {
            val schemaProvider = SchemaProvider()
            val tmp = schemaProvider.getKnownTypes(curr.project)

            // dodgy: remap type
            val tmp2 = HashMap<String?, Array<String?>?>()
            for (k in tmp.keys) {
                tmp2[k] = tmp.get(k)!!.toTypedArray<String?>()
            }

            val parent: Array<String?> = getKeyChain(resolveKeyElementForChain(curr, false))
            for (keyName in getCompletionForSection(tmp2, parent)) {
                if (prefixMatcher.prefixMatches(keyName)) {
                    hasSomething = true
                    results.addElement(LookupElementBuilder.create(keyName))
                }
            }
            if (hasSomething && params.invocationCount <= 1) {
                results.stopHere()
            }
        }


        val incompleteKey = CompletionUtil.isIncompleteKey(curr)
        if (curr.parent.parent is NeonKey || incompleteKey) { // key autocompletion
            val parent: Array<String?> = getKeyChain(resolveKeyElementForChain(curr, incompleteKey))
            for (keyName in getCompletionForSection(knownKeys, knownKeysPattern, parent)) {
                if (prefixMatcher.prefixMatches(keyName)) {
                    hasSomething = true
                    val element = LookupElementBuilder.create(keyName + (if (incompleteKey) ": " else ""))
                        .withPresentableText(keyName)
                    results.addElement(element)
                }
            }
        }
        if (curr.parent is NeonScalar && curr.parent.parent !is NeonKey) { // value autocompletion
            for (x in KEYWORD_LOOKUPS) {
                results.addElement(x)
            }


            // smart values
            if (!hasSomething) {
                val parent: Array<String?> = getKeyChain(curr)
                for (value in getCompletionForSection(knownValues, parent)) {
                    if (prefixMatcher.prefixMatches(value)) {
                        hasSomething = true
                        results.addElement(LookupElementBuilder.create(value))
                    }
                }
            }
        }

        if (hasSomething && params.invocationCount <= 1) {
            results.stopHere()
        }
    }

    private fun getCompletionForSection(
        options: HashMap<String?, Array<String?>?>,
        parent: Array<String?>
    ): Array<String> {
        return getCompletionForSection(options, null, parent)
    }

    private fun getCompletionForSection(
        options: HashMap<String?, Array<String?>?>,
        optionsPattern: HashMap<Pattern, Array<String?>?>?,
        parent: Array<String?>
    ): Array<String> {
        val ret: MutableList<String> = ArrayList<String>()

        for (i in 0..(if (parent.isNotEmpty()) 1 else 0)) {
            var parentName = parent.copyOfRange(i, parent.size).filterNotNull().joinToString("|")
            if (deprecatedKeys.containsKey(parentName)) {
                parentName = deprecatedKeys.get(parentName)!!
            }

            var found = options.get(parentName)
            if (found == null && optionsPattern != null) {
                for (pattern in optionsPattern.keys) {
                    if (pattern.matcher(parentName).matches()) {
                        found = optionsPattern.get(pattern)
                        break
                    }
                }
            }
            if (found != null) {
                Collections.addAll<String?>(ret, *found)
            }
        }

        return ret.toTypedArray<String>()
    }

    companion object {
        private val KEYWORDS = arrayOf<String?>( // common
            "true", "false", "yes", "no", "null"
        )

        private fun resolveKeyElementForChain(el: PsiElement, isIncomplete: Boolean): PsiElement? {
            var element = el
            if (isIncomplete) {
                return element.parent
            } else if (element.parent.parent is NeonFile) {
                return element.parent
            } else {
                // literal -> scalar -> [key ->] key-val pair -> any parent
                element = element.parent.parent
                return if (element is NeonKey) element.parent.parent else element.parent
            }
        }

        /**
         * Get full name of property at given element (e.g. common.services.myService1.setup)
         */
        fun getKeyChain(el: PsiElement?): Array<String?> {
            var element = el
            val names: MutableList<String?> = ArrayList<String?>()

            while (element != null) {
                if (element is NeonKeyValPair) {
                    names.add(0, element.keyText)
                } else if (element.node.elementType === NeonElementTypes.ITEM) {
                    names.add(0, "#")
                }

                element = element.parent
            }
            return names.toTypedArray<String?>()
        }
    }
}
