package dev.noctud.neon.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.util.ProcessingContext
import dev.noctud.neon.ext.isPhpStan
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonKeyValPair

/**
 * Provides autocompletion for PHPStan error identifiers in phpstan*.neon files
 * at the path: parameters → ignoreErrors → <bullet> → identifier:
 */
class PhpStanIdentifierCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        results: CompletionResultSet
    ) {
        val element = params.position
        if (element.node.elementType !== _NeonTypes.T_LITERAL) return

        val file = element.containingFile ?: return
        if (!file.isPhpStan()) return

        // Check if we're in the value position of an "identifier:" key
        val scalar = element.parent ?: return
        val value = scalar.parent ?: return
        val namedKvp = value.parent ?: return
        if (namedKvp !is NeonKeyValPair || namedKvp.keyText != "identifier") return

        for (identifier in PHPSTAN_IDENTIFIERS) {
            results.addElement(
                LookupElementBuilder.create(identifier)
                    .withIcon(AllIcons.Nodes.Constant)
                    .withTypeText("phpstan")
            )
        }
    }

    companion object {
        // All known PHPStan error identifiers as a Set for fast validation
        val KNOWN_IDENTIFIERS: Set<String> by lazy { PHPSTAN_IDENTIFIERS.toHashSet() }

        // PHPStan error identifiers from https://phpstan.org/error-identifiers
        val PHPSTAN_IDENTIFIERS = listOf(
            "argument.type", "argument.templateType", "argument.namedType",
            "arguments.count",
            "array.invalid",
            "assign.propertyType", "assign.readOnlyProperty",
            "binaryOp.invalid",
            "break.invalid",
            "callable.nonCallable",
            "cast.int", "cast.string",
            "catch.deadType",
            "class.notFound",
            "classConstant.notFound", "classConstant.deprecated",
            "closure.unusedUse",
            "constant.notFound",
            "constructor.unusedParameter",
            "continue.invalid",
            "deadCode.unreachable",
            "empty.notAllowed",
            "enum.abstract", "enum.extends",
            "foreach.nonIterable", "foreach.valueType",
            "function.notFound", "function.alreadyNarrowedType", "function.impossibleType",
            "generator.returnType",
            "identical.alwaysFalse", "identical.alwaysTrue",
            "if.condNotBoolean",
            "isset.offset",
            "match.unhandled",
            "method.notFound", "method.abstract", "method.private", "method.static",
            "method.childParameterType", "method.childReturnType",
            "missingType.return", "missingType.parameter", "missingType.property",
            "missingType.iterableValue", "missingType.generics",
            "new.static",
            "nullCoalesce.expr",
            "offsetAccess.notFound", "offsetAccess.nonOffsetAccessible",
            "offsetAssign.dimType",
            "parameter.defaultValue", "parameter.notFound",
            "phpDoc.parseError",
            "plus.invalid", "minus.invalid",
            "property.notFound", "property.nonObject", "property.readOnly",
            "property.deprecated", "property.unused",
            "return.type", "return.void", "return.missing",
            "staticMethod.notFound", "staticMethod.abstract",
            "staticProperty.notFound",
            "ternary.shortNotAllowed",
            "throw.cannotThrow",
            "trait.unused",
            "unaryMinus.nonNumeric",
            "variable.undefined", "variable.certainty",
            "varTag.type",
            "void.used",
        )

        // All valid identifier group prefixes from https://phpstan.org/error-identifiers
        val PHPSTAN_IDENTIFIER_PREFIXES: Set<String> = setOf(
            "argument", "arguments", "array", "arrayFilter", "arrayUnpacking", "arrayValues",
            "assert", "assign", "assignOp", "attribute", "backtick", "binaryOp",
            "booleanAnd", "booleanNot", "booleanOr", "break", "callable", "cast",
            "catch", "class", "classConstant", "classImplements", "clone", "closure",
            "conditionalType", "consistentConstructor", "constant", "constructor",
            "continue", "deadCode", "declareStrictTypes", "div", "doctrine",
            "doWhile", "echo", "elseif", "empty", "encapsedStringPart", "enum",
            "enumImplements", "equal", "expr", "filterVar", "finally", "for",
            "foreach", "function", "generator", "generics", "greater", "greaterOrEqual",
            "identical", "if", "ignore", "impure", "impureFunction", "impureMethod",
            "include", "includeOnce", "instanceof", "interface", "interfaceExtends",
            "isset", "logicalAnd", "logicalOr", "logicalXor", "magicConstant",
            "match", "method", "methodTag", "minus", "missingType", "mixin", "mod",
            "mul", "nette", "new", "notEqual", "notIdentical", "nullableType",
            "nullCoalesce", "nullsafe", "offsetAccess", "offsetAssign", "outOfClass",
            "paramClosureThis", "parameter", "parameterByRef",
            "paramImmediatelyInvokedCallable", "paramLaterInvokedCallable", "paramOut",
            "phpDoc", "phpParser", "phpstan", "phpstanApi", "phpstanPlayground",
            "phpunit", "pipe", "plus", "possiblyImpure", "postDec", "postInc", "pow",
            "preDec", "preInc", "print", "property", "propertyGetHook", "propertySetHook",
            "propertyTag", "pureFunction", "pureMethod", "regexp", "require",
            "requireExtends", "requireImplements", "requireOnce", "return", "sealed",
            "selfOut", "smaller", "smallerOrEqual", "spaceship", "staticClassAccess",
            "staticMethod", "staticProperty", "switch", "symfonyConsole",
            "symfonyContainer", "ternary", "throw", "throws", "trait", "traitUse",
            "typeAlias", "unaryMinus", "unaryOp", "unaryPlus", "unionType", "unset",
            "use", "variable", "varTag", "void", "while", "whitespace",
        )
    }
}
