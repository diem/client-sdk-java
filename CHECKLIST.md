
# Basics

- [ ] module structure:
  - libra
    - LibraClient: high level APIs interface, should support application to do easy mock / stub development.
    - jsonrpc: jsonrpc client interface, include plain data classes / structs defined in Libra JSON-RPC SPEC document.
      - types: data transfer object types for jsonrpc client, should match server side JSON-RPC spec data types.
    - stdlib: move stdlib script utils.
    - testnet: testnet utils, should include FaucetService for handing testnet mint.
    - types: Libra onchain data structure types.
    - utils: includes crypto, data types converting and other utils functions
      - signing, sha3 hashing, address parsing and converting, hex encoding / decoding
      - LCS utils
- [ ] JSON-RPC 2.0 Spec:
  - spec version validation.
  - batch requests and responses handling.
- [ ] JSON-RPC client error handling should distinguish the following 3 type errors:
  - Transport layer error, e.g. HTTP call failure.
  - JSON-RPC protocol error: e.g. server respond non json data, or can't be parsed into [Libra JSON-RPC SPEC][1] defined data structure, or missing result & error field.
  - JSON-RPC error: error returned from server.
- [x] https
- [ ] Client connection pool: keep connection alive for less likely getting inconsistent data from connecting to multiple servers.
- [ ] Handle stale responses:
  - [ ] client tracks latest server respond block version and timestamp, raise error when received server response contains stale version / timestamp.
- [ ] Libra User Identifier parsing utils (see [LIP-5][2])
  - bech32 addresses/subaddresses support
- [ ] language specific standard release publish: e.g. java maven central repo, python pip
- [ ] Multi-network: initialize Client with chain id, JSON-RPC server URL
- [ ] Handle unsigned int64 data type properly
- [ ] Validate server chain id: client should be initialized with chain id and validate server respond chain id is same.
- [ ] Validate input parameters, e.g. invalid account address: "kkk". Should return / raise InvalidArgumentError.

# High Level API

- [ ] transfer: wrap peer to peer transfer with metadata script and submit transaction
  - may have option to wait until transaction executed successfully or failed.
- [ ] waitForTransactionExecuted(String accountAddress, long sequence, String signedTranscationHash, long timeout):
  - for given signed transaction sender address, sequence number, expiration time (or 5 sec timeout) to wait and validate execution result is executed, otherwise return/raise an error / flag to tell it is not executed.
  - when signedTransactionHash validation failed, it should return / raise TransactionSequenceNumberConflictError
  - when transaction execution vm_status is not "executed", it should return / raise TransactionExecutionFailure

# Read from Blockchain

- [x] Get metadata 
- [x] Get currencies
- [x] Get events
- [x] Get transactions
- [x] Get account
- [x] Get account transaction 
- [x] Handle error response
- [x] Serialize result JSON to typed data structure
	
# Submit Transaction
 
- [x] Submit [p2p transfer][3] transaction
- [x] Submit other [Move Stdlib scripts][4]
- [ ] Wait for transaction executed:
  - low level API, consider not expose, only for internal or test usage.
  - wait for a transaction by get_transaction by account and transaction sequence, no validation of vm_status and signature.

# Testnet support

- [ ] Gen local wallet account keys: private key, public key, auth key
- [x] Create onchain account and mint coins through Faucet service
- [ ] Handle multiple signature auth key

See [doc][5] for above concepts.

# Examples

- [ ] Query blockchain example
- [ ] Submit p2p transfer transaction example

# Nice to have

- [ ] Async client
- [ ] CLI connects to testnet for trying out features.

[1]: https://github.com/libra/libra/blob/master/json-rpc/json-rpc-spec.md "Libra JSON-RPC SPEC"
[2]: https://github.com/libra/lip/blob/master/lips/lip-5.md "LIP-5"
[3]: https://github.com/libra/libra/blob/master/language/stdlib/transaction_scripts/doc/peer_to_peer_with_metadata.md "P2P Transafer"
[4]: https://github.com/libra/libra/tree/master/language/stdlib/transaction_scripts/doc "Move Stdlib scripts"
[5]: https://github.com/libra/libra/blob/master/client/libra-dev/README.md "Libra Client Dev Doc"
