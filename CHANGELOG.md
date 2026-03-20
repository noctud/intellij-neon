# Changelog

## [1.1.0] - Unreleased

### Added

- Bidirectional PHP class references — "Find Usages" on a PHP class now shows usages in `.neon` files
- Rename refactoring support — renaming a PHP class updates references in `.neon` files and vice versa
- Support for `@`-prefixed class references used in Nette service definitions

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
