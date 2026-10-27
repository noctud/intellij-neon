# Changelog

## [1.2.0] - 2026-10-27

### Added

- Translation catalogues (`<domain>.<locale>.neon`, e.g. `messages.en_US.neon` or `messages+intl-icu.pt-BR.neon`) are recognized and treated as prose: `%placeholder%` is highlighted without the "might not be defined" warning, and config-only highlighting, warnings, completion and navigation (PHP classes, `@service`, file paths) are turned off there
- `%env.NAME%` autocompletion from `.env` files, Ctrl+Click navigates to the variable in every `.env*` file declaring it, Ctrl+Click on `%env%` offers the `.env*` files
- Weak warning for `%env.NAME%` not declared in any `.env*` file (only when the project has one)

### Changed

- Renaming a PHP class no longer rewrites text in translation catalogues, and they are skipped when collecting parameters and services

### Fixed

- `.env` variables were completed and accepted as bare `%NAME%`, but Nette exposes them as `%env.NAME%`
- `.env` lines with `export`, spaces around `=` or lowercase names were ignored

## [1.1.1] - 2026-10-21

### Changed

- Color keys were renamed, so customized Neon colors are reset to defaults

### Fixed

- IDE error caused by color keys clashing with other plugins (e.g. Groovy)
- Color settings page showing raw keys like `colors.bad.character` instead of readable names
- Service reference / PHPStan identifier color missing from the color settings page
- Named argument color not shown in the color settings preview
- Customized "Class reference" color not applied to short class names (e.g. `Foo` instead of `App\Foo`)

## [1.1.0] - 2026-03-22

### Added

- Syntax coloring for Neon language elements
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
