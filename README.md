# libra-client-sdk-java

Java client for the Libra network.

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