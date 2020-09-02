
# Basics

- [ ] module structure: -- Xiao to finailize
  - libra
    - client: Libra JSON-RPC client APIs interface, should support application to do easy mock / stub development.
    - jsonrpc: jsonrpc client
    - utils: includes crypto, data types converting and other utils functions
        - KeysUtils: keys utils, generate public & private keys for testing, create auth key and account address.
        - Signer: sign transaction logic
    - TransactionMetadata: utils for creating peer to peer transaction metadata. (LIP-4)
    - AccountIdentifier: encoding & decoding Libra Account Identifier. (LIP-5)
    - IntentURL: encoding & decoding Libra Intent URL. (LIP-5)
    - TestNet: testnet utils, should include FaucetService for handling testnet mint.
    - stdlib: generated code, move stdlib script encoder & decoder.
    - types: generated code, Libra on-chain data structure types.
- [x] JSON-RPC client error handling should distinguish the following 3 type errors:
  - Transport layer error, e.g. HTTP call failure.
  - JSON-RPC protocol error: e.g. server responds to non json data, or can't be parsed into [Libra JSON-RPC SPEC][1] defined data structure, or missing result & error field.
  - JSON-RPC error: error returned from server.
- [x] https
- [x] Handle stale responses:
  - [x] client tracks latest server response block version and timestamp, raise error when received server response contains stale version / timestamp.
  - [x] parse and use libra_chain_id, libra_ledger_version and libra_ledger_tiemstamp in the JSONRPC response.
  - [ ] raise StaleResponseException, so that caller can retry on the error (most of the cases we should retry).
- [x] language specific standard release publish: java maven central repo
- [x] Multi-network: initialize Client with chain id, JSON-RPC server URL
  - [x] Validate server chain id: client should be initialized with chain id and validate server response chain id is the same.
- [ ] Handle unsigned int64 data type properly
- [ ] Transaction hash: for a given signed transaction, produce hash of the transaction executed.
  - hex-encode(sha3-256([]byte("Transaction")) + []byte {0} + signed transaction bytes)
- [ ] Use okhttp

# [LIP-4][7] support

- [ ] Non-custodial to custodial transaction
- [ ] Custodial to non-custodial transaction
- [ ] Custodial to Custodial transaction
- [ ] Refund

# [LIP-5][2] support

- [ ] Encode and decode account identifier
- [ ] Encode and decode intent identifier

# Read from Blockchain

- [x] Get metadata
- [x] Get currencies
- [x] Get events
- [x] Get transactions
- [x] Get account
- [x] Get account transaction
- [ ] Get account transactions -- low priority
- [x] Handle error response
- [x] Serialize result JSON to typed data structure

# Submit Transaction

- [x] Submit [p2p transfer][3] transaction
- [x] Submit other [Move Stdlib scripts][4]
- [ ] waitForTransaction(accountAddress, sequence, transcationHash, expirationTimeSec, timeout):
  - for given signed transaction sender address, sequence number, expiration time (or 5 sec timeout) to wait and validate execution result is executed, otherwise return/raise an error / flag to tell it is not executed.
  - when signedTransactionHash validation failed, it should return / raise TransactionSequenceNumberConflictError
  - when transaction execution vm_status is not "executed", it should return / raise TransactionExecutionFailure
  - when transaction expired, it should return / raise TransactionExpiredError: compare the transaction expirationTimeSec with response latest ledger timestamp. If response latest ledger timestamp >= transaction expirationTimeSec, then we are sure the transaction will never be executed successfully.
    - Note: response latest ledger timestamp unit is microsecond, expirationTimeSec's unit is second.

# Testnet support

- [x] Generate ed25519 private key, derive ed25519 public keys from private key.
- [x] Generate Single auth-keys
- [x] Mint coins through [Faucet service][6]

See [doc][5] for above concepts.

# Nice to have -- low priority

- [ ] [Multi-signatures support](https://github.com/libra/libra/blob/master/specifications/crypto/spec.md#multi-signatures)
- [ ] JSON-RPC 2.0 Client:
  - [x] spec version validation.
  - [ ] Batch requests and responses handling.
- [ ] Async client
- [ ] CLI connects to testnet for trying out features.
- [ ] Client connection pool.
- [ ] Work under Android environment

## Examples

- [ ] [p2p transfer examples](https://github.com/libra/lip/blob/master/lips/lip-4.md#transaction-examples)
- [ ] refund p2p transfer example
- [ ] create childVASP example
- [ ] Intent identifier encoding, decoding example


[1]: https://github.com/libra/libra/blob/master/json-rpc/json-rpc-spec.md "Libra JSON-RPC SPEC"
[2]: https://github.com/libra/lip/blob/master/lips/lip-5.md "LIP-5"
[3]: https://github.com/libra/libra/blob/master/language/stdlib/transaction_scripts/doc/peer_to_peer_with_metadata.md "P2P Transafer"
[4]: https://github.com/libra/libra/tree/master/language/stdlib/transaction_scripts/doc "Move Stdlib scripts"
[5]: https://github.com/libra/libra/blob/master/client/libra-dev/README.md "Libra Client Dev Doc"
[6]: https://github.com/libra/libra/blob/master/json-rpc/docs/service_testnet_faucet.md "Faucet service"
[7]: https://github.com/libra/lip/blob/master/lips/lip-4.md "Transaction Metadata Specification"
