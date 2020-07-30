
# Basics

- [x] multi-network: chain id
- [ ] validate server chain id
- [x] https
- [ ] batch requests and responses
- [ ] Handle stale responses 
- [ ] Libra User Identifier Parsing
- [ ] Async client
- [ ] Handle unsigned long properly

# Read from Blockchain

- [x] Get metadata 
- [x] Get currencies
- [x] Get events
- [x] Get transactions
- [x] Get account
- [x] Get account transaction 
- [x] Handle error response
- [x] Serialize Json to typed data structure
	
# Submit Transaction
 
- [x] Submit [p2p transfer](https://github.com/libra/libra/blob/master/language/stdlib/transaction_scripts/doc/peer_to_peer_with_metadata.md) transaction
- [x] Submit other [Move Stdlib scripts](https://github.com/libra/libra/tree/master/language/stdlib/transaction_scripts/doc)
- [x] Wait for transaction executed

# Testnet support

- [ ] Gen local wallet account keys: private key, public key, auth key
- [x] Create onchain account and mint coins through Faucet service
- [ ] Handle multiple signature auth key

See [doc](https://github.com/libra/libra/blob/master/client/libra-dev/README.md) for above concepts.