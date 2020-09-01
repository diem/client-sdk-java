# libra-client-sdk-java

Java client for the Libra network.

![Java CI with Gradle](https://github.com/libra/libra-client-sdk-java/workflows/Java%20CI%20with%20Gradle/badge.svg)

# Requirements

* [gradle](https://gradle.org/install/)
* JDK 8+

# Build

```
gradle build
```

Jar file location: build/libs/libra-client-sdk-java.jar

# Test

```
gradle test
```

# Upgrade to latest libra testnet release

```
// reset libra submodule to testnet branch, re-generate stdlib and lcs type classes
gradle gen

// confirm everything works
gradle test
```

# How to use

See [TestNetIntegrationTest](../blob/src/test/java/org/libra/librasdk/TestNetIntegrationTest.java)

# License

[Apache License V2](../blob/LICENSE)

# Contributing

[CONTRIBUTING](../blob/CONTRIBUTING.md)

# Features and Plan

[CHECKLIST](../blob/CHECKLIST.md)
