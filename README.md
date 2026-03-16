Neon for PhpStorm and IntelliJ Idea
=========================================

[![JetBrains Marketplace](https://img.shields.io/jetbrains/plugin/v/28338-neon.svg?label=marketplace)](https://plugins.jetbrains.com/plugin/28338-neon)
[![Build](https://img.shields.io/github/actions/workflow/status/noctud/intellij-neon/build.yaml?branch=main)](https://github.com/noctud/intellij-neon/actions)
![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)
[![Discord](https://img.shields.io/badge/discord-join-5865F2?logo=discord&logoColor=white)](https://discord.noctud.dev)

<!-- Plugin description -->
[Neon](https://github.com/nette/neon) language support for PhpStorm and IntelliJ IDEA. Provides syntax highlighting, code completion for PHP classes and services, code folding, brace matching, structure view, and breadcrumbs navigation.

If you have any problems with the plugin, [create an issue](https://github.com/noctud/intellij-neon/issues/new/choose) or join the [Noctud Discord](https://discord.noctud.dev).
<!-- Plugin description end -->

Installation
------------
Settings → Plugins → Browse repositories → Find "Neon" → Install Plugin → Apply


Installation from .jar file
------------
Download `instrumented.jar` file from [latest release](https://github.com/noctud/intellij-neon/releases) or latest successful [GitHub Actions build](https://github.com/noctud/intellij-neon/actions)


Supported Features
------------------

* Syntax highlighting and code completion for `PHP` classes in `Neon` files

Building
------------

```sh
./gradlew buildPlugin
```

Testing in sandbox IDE
------------

```sh
./gradlew runIde
```
