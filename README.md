# libra-client-sdk-java

![Java CI with Gradle](https://github.com/libra/libra-client-sdk-java/workflows/Java%20CI%20with%20Gradle/badge.svg) [![API Reference](https://img.shields.io/badge/api-reference-blue.svg)](https://github.com/libra/libra/blob/master/json-rpc/json-rpc-spec.md) [![Apache V2 License](https://img.shields.io/badge/license-Apache%20V2-blue.svg)](../master/LICENSE) [![javadoc](https://javadoc.io/badge2/org.libra/libra-client-sdk-java/javadoc.svg)](https://javadoc.io/doc/org.libra/libra-client-sdk-java)

`libra-client-sdk-java` is the official Libra Client SDK for the Java programming language.

## Overview of SDK's Packages

### [`org.libra`](./src/main/java/com/org/libra/)

> SPEC = specification
> LIP-X = Libra Improvement Protocol

- `jsonrpc`: libra JSON-RPC APIs client. [SPEC](https://github.com/libra/libra/blob/master/json-rpc/json-rpc-spec.md)
- `jsonrpctypes`: generated code, JSON-RPC API response data object classes generated from protobuf definition.
- `stdlib`: generated code, move stdlib script utils for constructing transaction script playload.
- `types`: generated code, Libra on-chain data structure types. Mostly generated code with small extension code for attaching handy functions to generated types.
- `utils`: utility functions, account address utils, currency code, hashing, hex encoding / decoding, transaction utils.
- `AccountIdentifier` & `IntentIdentifier`: encoding & decoding Libra Account Identifier and Intent URL. [LIP-5](https://lip.libra.org/lip-5/)
- `LibraClient`: interface of JSON-RPC client.
- `LibraException`: root exception of all checked exceptions defined in SDK.
- `PrivateKey`: abstraction for hiding private key details, implement this interface for plugin your customized private key signing logic.
- `Signer`: sign transaction logic.
- `Testnet`: Testnet utility, minting coins, create Testnet client, chain id, Testnet JSON-RPC URL.
- `TransactionMetadata`: utils for creating peer to peer transaction metadata. [LIP-4](https://lip.libra.org/lip-4/)

## Examples

You can find all of the example code under the [`src/test/java/org/libra/examples`](./src/test/java/org/libra/examples/) directory:

* [Create Child VASP Account](./src/test/java/org/libra/examples/CreateChildVASP.java)
* [All Types Peer To Peer Transfer](./src/test/java/org/libra/examples/PeerToPeerTransfer.java)
* [Intent Identifier](./src/test/java/org/libra/examples/IntentId.java)
* [Refund](./src/test/java/org/libra/examples/Refund.java)

## Download

**Gradle**

```gradle
implementation 'org.libra:libra-client-sdk-java:0.0.3'
```

**Maven**

```xml
<dependency>
  <groupId>org.libra</groupId>
  <artifactId>libra-client-sdk-java</artifactId>
  <version>0.0.3</version>
</dependency>
```
[Download Jars on Maven](https://search.maven.org/search?q=a:libra-client-sdk-java)

# System Requirements

* [gradle](https://gradle.org/install/)
* JDK 8+ (https://www.oracle.com/java/technologies/javase-downloads.html)

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

[Apache License V2](./LICENSE)

# Contributing

[CONTRIBUTING](./CONTRIBUTING.md)

# Features, Roadmap and Development Plan

[CHECKLIST](./CHECKLIST.md)
