# Changelog

## [1.1.0] - Unreleased

### Added

- Syntax coloring — distinct colors for strings, numbers, datetime, PHP classes, keywords, variables, service references, file paths, and named arguments
- `%variable%` highlighting and autocompletion from `parameters:` sections, Nette defaults, and `.env` files
- `%variable%` Ctrl+Click navigation to parameter definition in neon files or `.env` files
- Warning for undefined `%variable%` parameters
- `@service` reference highlighting, autocompletion from `services:` sections, and Ctrl+Click navigation
- Warning for undefined `@service` references
- Bidirectional PHP class references — "Find Usages" on a PHP class now shows usages in `.neon` files
- Warning for unresolved PHP classes and namespaces in neon values
- Rename refactoring support — renaming a PHP class updates references in `.neon` files and vice versa
- PHPStan support — separate variable system, error identifier highlighting with autocompletion and validation
- File path detection and Ctrl+Click navigation for `.php` and `.neon` paths
- Named argument highlighting inside entity parameters (e.g. `Foo(directory: %tempDir%)`)

### Changed

- Switched from hand-written parser to GrammarKit-generated parser (BNF-based) for better maintainability and extensibility
- Tests run in CI on every push and pull request

### Fixed

- Ctrl+Click navigation to PHP classes with `@` prefix (e.g. `@App\Service\MyService`)

## [1.0.1] - 2025-12-17

### Fixed

- Legacy configurable id calculation mode error

### Improved

- Plugin was rewritten in Kotlin

## [1.0.0] - 2025-08-31

### Fixed

- Build process

### Changed

- Gradle to version 8.7
- Grammar kit and intellij platform versions to latest

### Removed

- Unused libs and ads
