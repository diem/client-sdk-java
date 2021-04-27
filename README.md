# client-sdk-java

![Java CI with Gradle](https://github.com/diem/client-sdk-java/workflows/Java%20CI%20with%20Gradle/badge.svg) [![Server API Reference](https://img.shields.io/badge/api-reference-blue.svg)](https://github.com/diem/diem/blob/master/json-rpc/json-rpc-spec.md) [![Apache V2 License](https://img.shields.io/badge/license-Apache%20V2-blue.svg)](../master/LICENSE) [![javadoc](https://img.shields.io/badge/javadoc-1.0.1-brightgreen.svg)](https://javadoc.io/doc/com.diem/client-sdk-java/)

`client-sdk-java` is the official diem Client SDK for the Java programming language.

## Overview of SDK's Packages

### [`com.diem`](./src/main/java/com/com/diem/)

> SPEC = specification
> DIP-X = Diem Improvement Protocol

- `jsonrpc`: diem JSON-RPC APIs client. [SPEC](https://github.com/diem/diem/blob/master/json-rpc/json-rpc-spec.md)
- `stdlib`: generated code, move stdlib script utils for constructing transaction script playload.
- `types`: generated code, diem on-chain data structure types. Mostly generated code with small extension code for attaching handy functions to generated types.
- `utils`: utility functions, account address utils, currency code, hashing, hex encoding / decoding, transaction utils.
- `AccountIdentifier` & `IntentIdentifier`: encoding & decoding diem Account Identifier and Intent URL. [DIP-5](https://dip.diem.com/dip-5/)
- `DiemClient`: interface of JSON-RPC client.
- `DiemException`: root exception of all checked exceptions defined in SDK.
- `PrivateKey`: abstraction for hiding private key details, implement this interface for plugin your customized private key signing logic.
- `Signer`: sign transaction logic.
- `Testnet`: Testnet utility, minting coins, create Testnet client, chain id, Testnet JSON-RPC URL.
- `TransactionMetadata`: utils for creating peer to peer transaction metadata. [DIP-4](https://dip.diem.com/dip-4/)
- `Constants`: static data and JSON-RPC response enum type values.

## Examples

You can find all of the example code under the [`src/test/java/com/diem/examples`](./src/test/java/com/diem/examples/) directory:

* [Create Child VASP Account](./src/test/java/com/diem/examples/CreateChildVASP.java)
* [All Types Peer To Peer Transfer](./src/test/java/com/diem/examples/PeerToPeerTransfer.java)
* [Intent Identifier](./src/test/java/com/diem/examples/IntentId.java)

## Download

**Gradle**

```gradle
implementation 'com.diem:client-sdk-java:1.0.5'
```

**Maven**

```xml
<dependency>
  <groupId>com.diem</groupId>
  <artifactId>client-sdk-java</artifactId>
  <version>1.0.5</version>
</dependency>
```
[Download Jars on Maven](https://repo1.maven.org/maven2/com/diem/client-sdk-java/)

# Development

## System Requirements

* [gradle](https://gradle.org/install/)
* [JDK 8+](https://www.oracle.com/java/technologies/javase-downloads.html)

## Build

```
gradle build
```

Jar file location: build/libs/client-sdk-java.jar

## Test

```
gradle test
```

## Upgrade to latest diem release

```
// checkout diem submodule
git submodule update diem


// reset diem submodule to diem release revision
cd diem
git fetch/checkout/pull origin <revision/branch>

// re-generate stdlib and lcs type classes
gradle gen

// generate jsonrpc types from protobuf definition
gradle generateProto

// confirm everything works
gradle test
```


# License

[Apache License V2](./LICENSE)

# Contributing

[CONTRIBUTING](./CONTRIBUTING.md)
