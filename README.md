# libra-client-sdk-java

![Java CI with Gradle](https://github.com/libra/libra-client-sdk-java/workflows/Java%20CI%20with%20Gradle/badge.svg) [![API Reference](https://img.shields.io/badge/api-reference-blue.svg)](https://github.com/libra/libra/blob/master/json-rpc/json-rpc-spec.md) [![Apache V2 License](https://img.shields.io/badge/license-Apache%20V2-blue.svg)](../master/LICENSE)

libra-client-sdk-java is the official Libra Client SDK for the Java programming language.

## Overview of SDK's Packages

**org.libra**

- librasdk: libra JSON-RPC APIs client.
  - dto: JSON-RPC API response data transfer object classes.
  - jsonrpc: JSON-RPC client.
  - Utils: utility functions, data type converting, hashing, signing...
- stdlib: move stdlib script utils. This is generated code, for constructing transaction script playload.
- types: Libra onchain data structure types. Mostly generated code with small extension code for attaching handy functions to generated types.
- Testnet: Testnet utility, minting coins, create Testnet client, chain id, Testnet JSON-RPC URL.

## Examples

See [TestNetIntegrationTest](../master/src/test/java/org/libra/librasdk/TestNetIntegrationTest.java)

## Download

**Gradle**

```gradle
implementation 'org.libra:libra-client-sdk-java:0.0.1'
```

**Maven**

```xml
<dependency>
  <groupId>org.libra</groupId>
  <artifactId>libra-client-sdk-java</artifactId>
  <version>0.0.1</version>
</dependency>
```
[Download Jars](https://search.maven.org/search?q=a:libra-client-sdk-java)

# Development

* [gradle](https://gradle.org/install/)
* JDK 8+

## Build

```
gradle build
```

Jar file location: build/libs/libra-client-sdk-java.jar

## Test

```
gradle test
```

## Upgrade to latest libra testnet release

```
// reset libra submodule to testnet branch, re-generate stdlib and lcs type classes
gradle gen

// confirm everything works
gradle test
```


# License

[Apache License V2](../blob/LICENSE)

# Contributing

[CONTRIBUTING](../blob/CONTRIBUTING.md)

# Features and Plan

[CHECKLIST](../blob/CHECKLIST.md)
