# libra-client-sdk-java

Java client for the Libra network.

![Java CI with Gradle](https://github.com/libra/libra-client-sdk-java/workflows/Java%20CI%20with%20Gradle/badge.svg)

# Requirements

* [gradle](https://gradle.org/install/)
* JDK 11+

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
cd libra
git fetch
git reset --hard origin/testnet
cd ..

// re-generate stdlib and lcs type classes
gradle genstdlib

// confirm everything works
gradle test
```

# How to use

See [TestNetIntegrationTest](../blob/src/test/java/org/libra/librasdk/TestNetIntegrationTest.java)

# License

[Apache License V2](../blob/LICENSE)

# Contributing

[CONTRIBUTING](../blob/CONTRIBUTING.md)