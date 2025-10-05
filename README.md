# glob-library-java

[![Maven Central](https://img.shields.io/badge/maven--central-0.9.0-blue.svg)](https://search.maven.org/artifact/com.hrakaroo/glob/0.9.0/jar)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A high-performance glob pattern matcher for Java strings with zero dependencies.

## Overview

This library provides a direct implementation of glob pattern matching, offering a safer and faster alternative to regex-based glob implementations. Unlike regular expressions, glob patterns are simple, intuitive, and free from catastrophic backtracking issues.

## Why Use This Library?

Most glob implementations convert patterns to regular expressions, which has several drawbacks:

- **Complexity**: Escaping regex metacharacters correctly is non-trivial (including `\Q` and `\E` sequences)
- **Performance**: Regex engines can suffer catastrophic backtracking with patterns like `*a*b*c*` on long non-matching strings
- **Safety**: Greedy matching can cause unexpected performance degradation

This library implements glob matching directly, providing:

- **Better Performance**: 1.5x faster than regex for typical patterns (see benchmarks)
- **Predictable Behavior**: Non-greedy matching prevents backtracking issues
- **Type Safety**: Compile-time pattern validation

## What is a Glob?

A glob is a simple pattern matching syntax where:
- `*` (or `%`) matches zero or more characters
- `?` (or `_`) matches exactly one character

Common uses:
- **Unix shells**: `*.txt`, `file?.log`
- **SQL LIKE**: `%pattern%`, `user_`

Globs are simpler than regular expressions but sufficient for many matching tasks. See [Wikipedia](https://en.wikipedia.org/wiki/Glob_(programming)) for more details.

## Features

- ✅ **Zero Dependencies**: No runtime dependencies (test and benchmark tools only)
- ✅ **Thread-Safe**: Compiled matchers can be safely shared across threads
- ✅ **High Performance**: 1.5x faster than regex for typical patterns
- ✅ **Low Memory**: Minimal memory footprint with efficient compiled patterns
- ✅ **100% Test Coverage**: Comprehensive JUnit test suite
- ✅ **Well Documented**: Full JavaDoc and benchmarks included
- ✅ **Flexible**: Supports Unix (`*`, `?`) and SQL (`%`, `_`) syntax
- ✅ **Smart Optimizations**: Automatic selection of optimal matching engine

## Installation

### Gradle
```gradle
implementation 'com.hrakaroo:glob:0.9.0'
```

### Maven
```xml
<dependency>
    <groupId>com.hrakaroo</groupId>
    <artifactId>glob</artifactId>
    <version>0.9.0</version>
</dependency>
```

## Quick Start

### Basic Usage (Unix-style)

```java
import com.hrakaroo.glob.GlobPattern;
import com.hrakaroo.glob.MatchingEngine;

// Compile pattern once
MatchingEngine matcher = GlobPattern.compile("dog*cat\\*goat??");

// Use many times (thread-safe)
matcher.matches("dog horse cat*goat!~");  // true
matcher.matches("dogcat*goat..");         // true
matcher.matches("dog catgoat!/");         // false
```

### SQL LIKE Syntax

```java
MatchingEngine matcher = GlobPattern.compile(
    "dog%cat\\%goat_",
    '%',                           // wildcard (zero or more)
    '_',                           // match one
    GlobPattern.HANDLE_ESCAPES
);

matcher.matches("dog horse cat%goat!");  // true
matcher.matches("dogcat%goat.");         // true
```

### Case-Insensitive Matching

```java
MatchingEngine matcher = GlobPattern.compile(
    "Hello*World",
    '*',
    '?',
    GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES
);

matcher.matches("hello beautiful world");  // true
matcher.matches("HELLO WORLD");            // true
```

### Advanced Options

```java
// Disable wildcards (exact match only)
MatchingEngine exact = GlobPattern.compile(
    "exact_string",
    '\0',  // disable wildcard
    '\0',  // disable match-one
    0
);

// Custom characters
MatchingEngine custom = GlobPattern.compile(
    "foo#bar@",
    '#',   // use # as wildcard
    '@',   // use @ as match-one
    GlobPattern.HANDLE_ESCAPES
);
```

## Escape Sequences

When `GlobPattern.HANDLE_ESCAPES` is enabled, the following escape sequences are supported:

| Escape | Result | Description |
|--------|--------|-------------|
| `\\*` | `*` | Literal asterisk |
| `\\%` | `%` | Literal percent |
| `\\?` | `?` | Literal question mark |
| `\\_` | `_` | Literal underscore |
| `\\n` | newline | Line feed |
| `\\r` | return | Carriage return |
| `\\t` | tab | Tab character |
| `\\\\` | `\` | Literal backslash |
| `\\uXXXX` | Unicode | Unicode character (hex) |

Example:
```java
GlobPattern.compile("file\\*.txt");  // Matches "file*.txt" literally
GlobPattern.compile("line1\\nline2"); // Matches string with newline
GlobPattern.compile("\\u0041BC");     // Matches "ABC"
```

## Performance Benchmarks

### JMH Results

Higher scores indicate better throughput. Benchmarks designed to prevent optimization shortcuts.

#### Word Matching
```
Benchmark                           Mode  Cnt    Score    Error   Units
Benchmark1.globWords               thrpt   10   19.460 ± 0.967   ops/s
Benchmark1.greedyRegexWords        thrpt   10   12.609 ± 0.339   ops/s
Benchmark1.nonGreedyRegexWords     thrpt   10   13.291 ± 0.303   ops/s
```
**Result**: Glob is **1.5x faster** than regex

#### Log Line Matching
```
Benchmark                           Mode  Cnt    Score    Error   Units
Benchmark1.globLogLines            thrpt   10   10.707 ± 0.204   ops/s
Benchmark1.greedyRegexLogLines     thrpt   10    8.598 ± 0.247   ops/s
Benchmark1.nonGreedyRegexLogLines  thrpt   10    8.409 ± 0.162   ops/s
```
**Result**: Glob is **1.2x faster** than regex

#### String Comparison
```
Benchmark                                 Mode  Cnt     Score     Error   Units
Benchmark1.globCompare                   thrpt   10   179.345 ±  3.151   ops/s
Benchmark1.globCompareCaseInsensitive    thrpt   10   169.957 ± 23.889   ops/s
Benchmark1.stringCompare                 thrpt   10   211.104 ±  3.435   ops/s
Benchmark1.stringCompareCaseInsensitive  thrpt   10   126.214 ±  5.041   ops/s
```
**Result**: Glob optimization makes it competitive with `String.equals()` and faster than `String.equalsIgnoreCase()`

### Run Benchmarks Yourself

```bash
./gradlew jmh
```

## Implementation Details

### Smart Optimizations

The library automatically selects the most efficient matching engine based on the pattern:

| Pattern Type | Engine | Example | Optimization |
|-------------|---------|---------|--------------|
| Empty | `EmptyOnlyEngine` | `""` | Matches empty strings only |
| Match all | `EverythingEngine` | `*` | Always returns true |
| Exact match | `EqualToEngine` | `foo` | Simple character comparison |
| Starts with | `StartsWithEngine` | `foo*` | Prefix matching |
| Ends with | `EndsWithEngine` | `*foo` | Suffix matching |
| Contains | `ContainsEngine` | `*foo*` | Substring search |
| Complex | `GlobEngine` | `*foo*bar*` | Full glob matching |

### Design Principles

- **Non-greedy matching**: Prevents catastrophic backtracking
- **Compile-time optimization**: Pattern processing happens once during compilation
- **Multiple wildcard folding**: `**` becomes `*` at compile time
- **No recursion**: Stack-based algorithm for predictable performance
- **Minimal allocations**: Reuses compiled pattern data structures
- **Thread-safe**: Immutable compiled patterns

## API Reference

### GlobPattern.compile() Methods

```java
// Default: Unix-style with escapes
MatchingEngine compile(String pattern)

// Custom wildcard and match-one characters
MatchingEngine compile(
    String pattern,
    char wildcardChar,    // e.g., '*' or '%'
    char matchOneChar,    // e.g., '?' or '_'
    int flags             // CASE_INSENSITIVE | HANDLE_ESCAPES
)
```

### Flags

- `GlobPattern.CASE_INSENSITIVE` - Enable case-insensitive matching
- `GlobPattern.HANDLE_ESCAPES` - Enable escape sequence processing
- Use `|` to combine: `CASE_INSENSITIVE | HANDLE_ESCAPES`

### Special Characters

- `GlobPattern.NULL_CHARACTER` (`'\0'`) - Use to disable wildcard or match-one features

### MatchingEngine Interface

```java
boolean matches(String input)       // Test if input matches pattern
int matchingSizeInBytes()          // Memory usage estimate during matching
int staticSizeInBytes()            // Static memory usage of compiled pattern
```

## Testing

The library has **100% test coverage** verified by JaCoCo.

![100% test coverage](./jacoco.png "Jacoco Report")

Run tests:
```bash
./gradlew test
```

Generate coverage report:
```bash
./gradlew jacocoTestReport
# Report at: build/reports/coverage/index.html
```

## Building from Source

```bash
# Clone repository
git clone https://github.com/hrakaroo/glob-library-java.git
cd glob-library-java

# Build
./gradlew build

# Run tests
./gradlew test

# Run benchmarks
./gradlew jmh
```

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Write tests for your changes
4. Ensure 100% test coverage (`./gradlew jacocoTestCoverageVerification`)
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## Use Cases

This library is ideal for:

- **File filtering**: Match filenames against patterns
- **Configuration matching**: Match config keys or values
- **Search functionality**: Simple pattern-based search
- **Database-like filtering**: SQL LIKE functionality in Java
- **Log filtering**: Match log messages by pattern
- **Input validation**: Simple pattern-based validation

## Comparison with Alternatives

| Feature | glob-library-java | Regex | Apache Commons |
|---------|------------------|-------|----------------|
| Performance | ✅ Fast | ⚠️ Can be slow | ⚠️ Converts to regex |
| Backtracking safety | ✅ Yes | ❌ No | ❌ No |
| Dependencies | ✅ Zero | ✅ Built-in | ❌ External |
| Thread-safe | ✅ Yes | ✅ Yes | ✅ Yes |
| Pattern complexity | ⚠️ Simple globs | ✅ Full regex | ⚠️ Simple globs |
| Learning curve | ✅ Easy | ❌ Complex | ✅ Easy |

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.

## Author

**Joshua Gerth** - [hrakaroo](https://github.com/hrakaroo)

## Links

- [GitHub Repository](https://github.com/hrakaroo/glob-library-java)
- [Maven Central](https://search.maven.org/artifact/com.hrakaroo/glob)
- [Issue Tracker](https://github.com/hrakaroo/glob-library-java/issues)

## Acknowledgments

- Designed to avoid the pitfalls of regex-based glob implementations
- Optimized for real-world Java applications
- No external libraries to minimize dependency conflicts
