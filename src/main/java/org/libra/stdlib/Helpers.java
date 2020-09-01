package org.libra.stdlib;


import java.math.BigInteger;
import java.lang.IllegalArgumentException;
import java.lang.IndexOutOfBoundsException;
import org.libra.types.AccountAddress;
import org.libra.types.Script;
import org.libra.types.TransactionArgument;
import org.libra.types.TypeTag;
import com.novi.serde.Int128;
import com.novi.serde.Unsigned;
import com.novi.serde.Bytes;


public final class Helpers {

    /**
     * Build a Libra `Script` from a structured object `ScriptCall`.
     */
    public static Script encode_script(ScriptCall call) {
        EncodingHelper helper = SCRIPT_ENCODER_MAP.get(call.getClass());
        return helper.encode(call);
    }

    /**
     * Try to recognize a Libra `Script` and convert it into a structured object `ScriptCall`.
     */
    public static ScriptCall decode_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        DecodingHelper helper = SCRIPT_DECODER_MAP.get(script.code);
        if (helper == null) {
            throw new IllegalArgumentException("Unknown script bytecode");
        }
        return helper.decode(script);
    }


    /**
     * Add a `Currency` balance to `account`, which will enable `account` to send and receive
     * `Libra<Currency>`.
     * Aborts with NOT_A_CURRENCY if `Currency` is not an accepted currency type in the Libra system
     * Aborts with `LibraAccount::ADD_EXISTING_CURRENCY` if the account already holds a balance in
     * `Currency`.
     */
    public static Script encode_add_currency_to_account_script(TypeTag currency) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ADD_CURRENCY_TO_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList(currency);
        builder.args = java.util.Arrays.asList();
        return builder.build();
    }

    /**
     * Add the `KeyRotationCapability` for `to_recover_account` to the `RecoveryAddress` resource under `recovery_address`.
     *
     * ## Aborts
     * * Aborts with `LibraAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED` if `account` has already delegated its `KeyRotationCapability`.
     * * Aborts with `RecoveryAddress:ENOT_A_RECOVERY_ADDRESS` if `recovery_address` does not have a `RecoveryAddress` resource.
     * * Aborts with `RecoveryAddress::EINVALID_KEY_ROTATION_DELEGATION` if `to_recover_account` and `recovery_address` do not belong to the same VASP.
     */
    public static Script encode_add_recovery_rotation_capability_script(AccountAddress recovery_address) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ADD_RECOVERY_ROTATION_CAPABILITY_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(recovery_address));
        return builder.build();
    }

    /**
     * Append the `hash` to script hashes list allowed to be executed by the network.
     */
    public static Script encode_add_to_script_allow_list_script(Bytes hash, @Unsigned Long sliding_nonce) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ADD_TO_SCRIPT_ALLOW_LIST_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U8Vector(hash), new TransactionArgument.U64(sliding_nonce));
        return builder.build();
    }

    /**
     * Add `new_validator` to the validator set.
     * Fails if the `new_validator` address is already in the validator set
     * or does not have a `ValidatorConfig` resource stored at the address.
     * Emits a NewEpochEvent.
     */
    public static Script encode_add_validator_and_reconfigure_script(@Unsigned Long sliding_nonce, Bytes validator_name, AccountAddress validator_address) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ADD_VALIDATOR_AND_RECONFIGURE_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U8Vector(validator_name), new TransactionArgument.Address(validator_address));
        return builder.build();
    }

    /**
     * Permanently destroy the `Token`s stored in the oldest burn request under the `Preburn` resource.
     * This will only succeed if `account` has a `MintCapability<Token>`, a `Preburn<Token>` resource
     * exists under `preburn_address`, and there is a pending burn request.
     * sliding_nonce is a unique nonce for operation, see sliding_nonce.move for details
     */
    public static Script encode_burn_script(TypeTag token, @Unsigned Long sliding_nonce, AccountAddress preburn_address) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(BURN_CODE);
        builder.ty_args = java.util.Arrays.asList(token);
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(preburn_address));
        return builder.build();
    }

    /**
     * Burn transaction fees that have been collected in the given `currency`
     * and relinquish to the association. The currency must be non-synthetic.
     */
    public static Script encode_burn_txn_fees_script(TypeTag coin_type) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(BURN_TXN_FEES_CODE);
        builder.ty_args = java.util.Arrays.asList(coin_type);
        builder.args = java.util.Arrays.asList();
        return builder.build();
    }

    /**
     * Cancel the oldest burn request from `preburn_address` and return the funds.
     * Fails if the sender does not have a published `BurnCapability<Token>`.
     */
    public static Script encode_cancel_burn_script(TypeTag token, AccountAddress preburn_address) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CANCEL_BURN_CODE);
        builder.ty_args = java.util.Arrays.asList(token);
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(preburn_address));
        return builder.build();
    }

    /**
     * Create a `ChildVASP` account for sender `parent_vasp` at `child_address` with a balance of
     * `child_initial_balance` in `CoinType` and an initial authentication_key
     * `auth_key_prefix | child_address`.
     * If `add_all_currencies` is true, the child address will have a zero balance in all available
     * currencies in the system.
     * This account will a child of the transaction sender, which must be a ParentVASP.
     *
     * ## Aborts
     * The transaction will abort:
     *
     * * If `parent_vasp` is not a parent vasp with error: `Roles::EINVALID_PARENT_ROLE`
     * * If `child_address` already exists with error: `Roles::EROLE_ALREADY_ASSIGNED`
     * * If `parent_vasp` already has 256 child accounts with error: `VASP::ETOO_MANY_CHILDREN`
     * * If `CoinType` is not a registered currency with error: `LibraAccount::ENOT_A_CURRENCY`
     * * If `parent_vasp`'s withdrawal capability has been extracted with error:  `LibraAccount::EWITHDRAWAL_CAPABILITY_ALREADY_EXTRACTED`
     * * If `parent_vasp` doesn't hold `CoinType` and `child_initial_balance > 0` with error: `LibraAccount::EPAYER_DOESNT_HOLD_CURRENCY`
     * * If `parent_vasp` doesn't at least `child_initial_balance` of `CoinType` in its account balance with error: `LibraAccount::EINSUFFICIENT_BALANCE`
     */
    public static Script encode_create_child_vasp_account_script(TypeTag coin_type, AccountAddress child_address, Bytes auth_key_prefix, Boolean add_all_currencies, @Unsigned Long child_initial_balance) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CREATE_CHILD_VASP_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList(coin_type);
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(child_address), new TransactionArgument.U8Vector(auth_key_prefix), new TransactionArgument.Bool(add_all_currencies), new TransactionArgument.U64(child_initial_balance));
        return builder.build();
    }

    /**
     * Create an account with the DesignatedDealer role at `addr` with authentication key
     * `auth_key_prefix` | `addr` and a 0 balance of type `Currency`. If `add_all_currencies` is true,
     * 0 balances for all available currencies in the system will also be added. This can only be
     * invoked by an account with the TreasuryCompliance role.
     */
    public static Script encode_create_designated_dealer_script(TypeTag currency, @Unsigned Long sliding_nonce, AccountAddress addr, Bytes auth_key_prefix, Bytes human_name, Boolean add_all_currencies) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CREATE_DESIGNATED_DEALER_CODE);
        builder.ty_args = java.util.Arrays.asList(currency);
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(addr), new TransactionArgument.U8Vector(auth_key_prefix), new TransactionArgument.U8Vector(human_name), new TransactionArgument.Bool(add_all_currencies));
        return builder.build();
    }

    /**
     * Create an account with the ParentVASP role at `address` with authentication key
     * `auth_key_prefix` | `new_account_address` and a 0 balance of type `currency`. If
     * `add_all_currencies` is true, 0 balances for all available currencies in the system will
     * also be added. This can only be invoked by an TreasuryCompliance account.
     * `sliding_nonce` is a unique nonce for operation, see sliding_nonce.move for details.
     */
    public static Script encode_create_parent_vasp_account_script(TypeTag coin_type, @Unsigned Long sliding_nonce, AccountAddress new_account_address, Bytes auth_key_prefix, Bytes human_name, Boolean add_all_currencies) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CREATE_PARENT_VASP_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList(coin_type);
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(new_account_address), new TransactionArgument.U8Vector(auth_key_prefix), new TransactionArgument.U8Vector(human_name), new TransactionArgument.Bool(add_all_currencies));
        return builder.build();
    }

    /**
     * Extract the `KeyRotationCapability` for `recovery_account` and publish it in a
     * `RecoveryAddress` resource under  `account`.
     * ## Aborts
     * * Aborts with `LibraAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED` if `account` has already delegated its `KeyRotationCapability`.
     * * Aborts with `RecoveryAddress::ENOT_A_VASP` if `account` is not a ParentVASP or ChildVASP
     */
    public static Script encode_create_recovery_address_script() {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CREATE_RECOVERY_ADDRESS_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList();
        return builder.build();
    }

    /**
     * Create a validator account at `new_validator_address` with `auth_key_prefix`and human_name.
     */
    public static Script encode_create_validator_account_script(@Unsigned Long sliding_nonce, AccountAddress new_account_address, Bytes auth_key_prefix, Bytes human_name) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CREATE_VALIDATOR_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(new_account_address), new TransactionArgument.U8Vector(auth_key_prefix), new TransactionArgument.U8Vector(human_name));
        return builder.build();
    }

    /**
     * Create a validator operator account at `new_validator_address` with `auth_key_prefix`and human_name.
     */
    public static Script encode_create_validator_operator_account_script(@Unsigned Long sliding_nonce, AccountAddress new_account_address, Bytes auth_key_prefix, Bytes human_name) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CREATE_VALIDATOR_OPERATOR_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(new_account_address), new TransactionArgument.U8Vector(auth_key_prefix), new TransactionArgument.U8Vector(human_name));
        return builder.build();
    }

    /**
     * Freeze account `address`. Initiator must be authorized.
     * `sliding_nonce` is a unique nonce for operation, see sliding_nonce.move for details.
     */
    public static Script encode_freeze_account_script(@Unsigned Long sliding_nonce, AccountAddress to_freeze_account) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(FREEZE_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(to_freeze_account));
        return builder.build();
    }

    /**
     * Mint `amount_lbr` LBR from the sending account's constituent coins and deposits the
     * resulting LBR into the sending account.
     */
    public static Script encode_mint_lbr_script(@Unsigned Long amount_lbr) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(MINT_LBR_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(amount_lbr));
        return builder.build();
    }

    /**
     * Transfer `amount` coins of type `Currency` from `payer` to `payee` with (optional) associated
     * `metadata` and an (optional) `metadata_signature` on the message
     * `metadata` | `Signer::address_of(payer)` | `amount` | `DualAttestation::DOMAIN_SEPARATOR`.
     * The `metadata` and `metadata_signature` parameters are only required if `amount` >=
     * `DualAttestation::get_cur_microlibra_limit` LBR and `payer` and `payee` are distinct VASPs.
     * However, a transaction sender can opt in to dual attestation even when it is not required (e.g., a DesignatedDealer -> VASP payment) by providing a non-empty `metadata_signature`.
     * Standardized `metadata` LCS format can be found in `libra_types::transaction::metadata::Metadata`.
     *
     * ## Events
     * When this script executes without aborting, it emits two events:
     * `SentPaymentEvent { amount, currency_code = Currency, payee, metadata }`
     * on `payer`'s `LibraAccount::sent_events` handle, and
     *  `ReceivedPaymentEvent { amount, currency_code = Currency, payer, metadata }`
     * on `payee`'s `LibraAccount::received_events` handle.
     *
     * ## Common Aborts
     * These aborts can in occur in any payment.
     * * Aborts with `LibraAccount::EINSUFFICIENT_BALANCE` if `amount` is greater than `payer`'s balance in `Currency`.
     * * Aborts with `LibraAccount::ECOIN_DEPOSIT_IS_ZERO` if `amount` is zero.
     * * Aborts with `LibraAccount::EPAYEE_DOES_NOT_EXIST` if no account exists at the address `payee`.
     * * Aborts with `LibraAccount::EPAYEE_CANT_ACCEPT_CURRENCY_TYPE` if an account exists at `payee`, but it does not accept payments in `Currency`.
     *
     * ## Dual Attestation Aborts
     * These aborts can occur in any payment subject to dual attestation.
     * * Aborts with `DualAttestation::EMALFORMED_METADATA_SIGNATURE` if `metadata_signature`'s is not 64 bytes.
     * * Aborts with `DualAttestation:EINVALID_METADATA_SIGNATURE` if `metadata_signature` does not verify on the message `metadata` | `payer` | `value` | `DOMAIN_SEPARATOR` using the `compliance_public_key` published in the `payee`'s `DualAttestation::Credential` resource.
     *
     * ## Other Aborts
     * These aborts should only happen when `payer` or `payee` have account limit restrictions or
     * have been frozen by Libra administrators.
     * * Aborts with `LibraAccount::EWITHDRAWAL_EXCEEDS_LIMITS` if `payer` has exceeded their daily
     * withdrawal limits.
     * * Aborts with `LibraAccount::EDEPOSIT_EXCEEDS_LIMITS` if `payee` has exceeded their daily deposit limits.
     * * Aborts with `LibraAccount::EACCOUNT_FROZEN` if `payer`'s account is frozen.
     */
    public static Script encode_peer_to_peer_with_metadata_script(TypeTag currency, AccountAddress payee, @Unsigned Long amount, Bytes metadata, Bytes metadata_signature) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(PEER_TO_PEER_WITH_METADATA_CODE);
        builder.ty_args = java.util.Arrays.asList(currency);
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(payee), new TransactionArgument.U64(amount), new TransactionArgument.U8Vector(metadata), new TransactionArgument.U8Vector(metadata_signature));
        return builder.build();
    }

    /**
     * Preburn `amount` `Token`s from `account`.
     * This will only succeed if `account` already has a published `Preburn<Token>` resource.
     */
    public static Script encode_preburn_script(TypeTag token, @Unsigned Long amount) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(PREBURN_CODE);
        builder.ty_args = java.util.Arrays.asList(token);
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(amount));
        return builder.build();
    }

    /**
     * (1) Rotate the authentication key of the sender to `public_key`
     * (2) Publish a resource containing a 32-byte ed25519 public key and the rotation capability
     *     of the sender under the sender's address.
     * Aborts if the sender already has a `SharedEd25519PublicKey` resource.
     * Aborts if the length of `new_public_key` is not 32.
     */
    public static Script encode_publish_shared_ed25519_public_key_script(Bytes public_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(PUBLISH_SHARED_ED25519_PUBLIC_KEY_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U8Vector(public_key));
        return builder.build();
    }

    /**
     * Set validator's config locally.
     * Does not emit NewEpochEvent, the config is NOT changed in the validator set.
     */
    public static Script encode_register_validator_config_script(AccountAddress validator_account, Bytes consensus_pubkey, Bytes validator_network_addresses, Bytes fullnode_network_addresses) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(REGISTER_VALIDATOR_CONFIG_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(validator_account), new TransactionArgument.U8Vector(consensus_pubkey), new TransactionArgument.U8Vector(validator_network_addresses), new TransactionArgument.U8Vector(fullnode_network_addresses));
        return builder.build();
    }

    /**
     * Removes a validator from the validator set.
     * Fails if the validator_address is not in the validator set.
     * Emits a NewEpochEvent.
     */
    public static Script encode_remove_validator_and_reconfigure_script(@Unsigned Long sliding_nonce, Bytes validator_name, AccountAddress validator_address) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(REMOVE_VALIDATOR_AND_RECONFIGURE_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U8Vector(validator_name), new TransactionArgument.Address(validator_address));
        return builder.build();
    }

    /**
     * Rotate the sender's authentication key to `new_key`.
     * `new_key` should be a 256 bit sha3 hash of an ed25519 public key.
     * * Aborts with `LibraAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED` if the `KeyRotationCapability` for `account` has already been extracted.
     * * Aborts with `0` if the key rotation capability held by the account doesn't match the sender's address.
     * * Aborts with `LibraAccount::EMALFORMED_AUTHENTICATION_KEY` if the length of `new_key` != 32.
     */
    public static Script encode_rotate_authentication_key_script(Bytes new_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ROTATE_AUTHENTICATION_KEY_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U8Vector(new_key));
        return builder.build();
    }

    /**
     * Rotate `account`'s authentication key to `new_key`.
     * `new_key` should be a 256 bit sha3 hash of an ed25519 public key. This script also takes
     * `sliding_nonce`, as a unique nonce for this operation. See sliding_nonce.move for details.
     */
    public static Script encode_rotate_authentication_key_with_nonce_script(@Unsigned Long sliding_nonce, Bytes new_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ROTATE_AUTHENTICATION_KEY_WITH_NONCE_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U8Vector(new_key));
        return builder.build();
    }

    /**
     * Rotate `account`'s authentication key to `new_key`.
     * `new_key` should be a 256 bit sha3 hash of an ed25519 public key. This script also takes
     * `sliding_nonce`, as a unique nonce for this operation. See sliding_nonce.move for details.
     */
    public static Script encode_rotate_authentication_key_with_nonce_admin_script(@Unsigned Long sliding_nonce, Bytes new_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ROTATE_AUTHENTICATION_KEY_WITH_NONCE_ADMIN_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U8Vector(new_key));
        return builder.build();
    }

    /**
     * Rotate the authentication key of `account` to `new_key` using the `KeyRotationCapability`
     * stored under `recovery_address`.
     *
     * ## Aborts
     * * Aborts with `RecoveryAddress::ENOT_A_RECOVERY_ADDRESS` if `recovery_address` does not have a `RecoveryAddress` resource
     * * Aborts with `RecoveryAddress::ECANNOT_ROTATE_KEY` if `account` is not `recovery_address` or `to_recover`.
     * * Aborts with `LibraAccount::EMALFORMED_AUTHENTICATION_KEY` if `new_key` is not 32 bytes.
     * * Aborts with `RecoveryAddress::ECANNOT_ROTATE_KEY` if `account` has not delegated its `KeyRotationCapability` to `recovery_address`.
     */
    public static Script encode_rotate_authentication_key_with_recovery_address_script(AccountAddress recovery_address, AccountAddress to_recover, Bytes new_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ROTATE_AUTHENTICATION_KEY_WITH_RECOVERY_ADDRESS_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(recovery_address), new TransactionArgument.Address(to_recover), new TransactionArgument.U8Vector(new_key));
        return builder.build();
    }

    /**
     * Rotate `account`'s base URL to `new_url` and its compliance public key to `new_key`.
     * Aborts if `account` is not a ParentVASP or DesignatedDealer
     * Aborts if `new_key` is not a well-formed public key
     */
    public static Script encode_rotate_dual_attestation_info_script(Bytes new_url, Bytes new_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ROTATE_DUAL_ATTESTATION_INFO_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U8Vector(new_url), new TransactionArgument.U8Vector(new_key));
        return builder.build();
    }

    /**
     * (1) Rotate the public key stored in `account`'s `SharedEd25519PublicKey` resource to
     * `new_public_key`
     * (2) Rotate the authentication key using the capability stored in `account`'s
     * `SharedEd25519PublicKey` to a new value derived from `new_public_key`
     * Aborts if `account` does not have a `SharedEd25519PublicKey` resource.
     * Aborts if the length of `new_public_key` is not 32.
     */
    public static Script encode_rotate_shared_ed25519_public_key_script(Bytes public_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ROTATE_SHARED_ED25519_PUBLIC_KEY_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U8Vector(public_key));
        return builder.build();
    }

    /**
     * Set validator's config and updates the config in the validator set.
     * NewEpochEvent is emitted.
     */
    public static Script encode_set_validator_config_and_reconfigure_script(AccountAddress validator_account, Bytes consensus_pubkey, Bytes validator_network_addresses, Bytes fullnode_network_addresses) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(SET_VALIDATOR_CONFIG_AND_RECONFIGURE_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(validator_account), new TransactionArgument.U8Vector(consensus_pubkey), new TransactionArgument.U8Vector(validator_network_addresses), new TransactionArgument.U8Vector(fullnode_network_addresses));
        return builder.build();
    }

    /**
     * Set validator's operator
     */
    public static Script encode_set_validator_operator_script(Bytes operator_name, AccountAddress operator_account) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(SET_VALIDATOR_OPERATOR_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U8Vector(operator_name), new TransactionArgument.Address(operator_account));
        return builder.build();
    }

    /**
     * Set validator operator as 'operator_account' of validator owner 'account' (via Admin Script).
     * `operator_name` should match expected from operator account. This script also
     * takes `sliding_nonce`, as a unique nonce for this operation. See `Sliding_nonce.move` for details.
     */
    public static Script encode_set_validator_operator_with_nonce_admin_script(@Unsigned Long sliding_nonce, Bytes operator_name, AccountAddress operator_account) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(SET_VALIDATOR_OPERATOR_WITH_NONCE_ADMIN_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U8Vector(operator_name), new TransactionArgument.Address(operator_account));
        return builder.build();
    }

    /**
     * Mint 'mint_amount' to 'designated_dealer_address' for 'tier_index' tier.
     * Max valid tier index is 3 since there are max 4 tiers per DD.
     * Sender should be treasury compliance account and receiver authorized DD.
     * `sliding_nonce` is a unique nonce for operation, see sliding_nonce.move for details.
     */
    public static Script encode_tiered_mint_script(TypeTag coin_type, @Unsigned Long sliding_nonce, AccountAddress designated_dealer_address, @Unsigned Long mint_amount, @Unsigned Long tier_index) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(TIERED_MINT_CODE);
        builder.ty_args = java.util.Arrays.asList(coin_type);
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(designated_dealer_address), new TransactionArgument.U64(mint_amount), new TransactionArgument.U64(tier_index));
        return builder.build();
    }

    /**
     * Unfreeze account `address`. Initiator must be authorized.
     * `sliding_nonce` is a unique nonce for operation, see sliding_nonce.move for details.
     */
    public static Script encode_unfreeze_account_script(@Unsigned Long sliding_nonce, AccountAddress to_unfreeze_account) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(UNFREEZE_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(to_unfreeze_account));
        return builder.build();
    }

    /**
     * Unmints `amount_lbr` LBR from the sending account into the constituent coins and deposits
     * the resulting coins into the sending account.
     */
    public static Script encode_unmint_lbr_script(@Unsigned Long amount_lbr) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(UNMINT_LBR_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(amount_lbr));
        return builder.build();
    }

    /**
     * Update the dual attesation limit to `new_micro_lbr_limit`.
     */
    public static Script encode_update_dual_attestation_limit_script(@Unsigned Long sliding_nonce, @Unsigned Long new_micro_lbr_limit) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(UPDATE_DUAL_ATTESTATION_LIMIT_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U64(new_micro_lbr_limit));
        return builder.build();
    }

    /**
     * Update the on-chain exchange rate to LBR for the given `currency` to be given by
     * `new_exchange_rate_numerator/new_exchange_rate_denominator`.
     */
    public static Script encode_update_exchange_rate_script(TypeTag currency, @Unsigned Long sliding_nonce, @Unsigned Long new_exchange_rate_numerator, @Unsigned Long new_exchange_rate_denominator) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(UPDATE_EXCHANGE_RATE_CODE);
        builder.ty_args = java.util.Arrays.asList(currency);
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U64(new_exchange_rate_numerator), new TransactionArgument.U64(new_exchange_rate_denominator));
        return builder.build();
    }

    /**
     * Update Libra version.
     * `sliding_nonce` is a unique nonce for operation, see sliding_nonce.move for details.
     */
    public static Script encode_update_libra_version_script(@Unsigned Long sliding_nonce, @Unsigned Long major) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(UPDATE_LIBRA_VERSION_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U64(major));
        return builder.build();
    }

    /**
     * Allows--true--or disallows--false--minting of `currency` based upon `allow_minting`.
     */
    public static Script encode_update_minting_ability_script(TypeTag currency, Boolean allow_minting) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(UPDATE_MINTING_ABILITY_CODE);
        builder.ty_args = java.util.Arrays.asList(currency);
        builder.args = java.util.Arrays.asList(new TransactionArgument.Bool(allow_minting));
        return builder.build();
    }

    private static ScriptCall decode_add_currency_to_account_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.AddCurrencyToAccount.Builder builder = new ScriptCall.AddCurrencyToAccount.Builder();
        builder.currency = script.ty_args.get(0);
        return builder.build();
    }

    private static ScriptCall decode_add_recovery_rotation_capability_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.AddRecoveryRotationCapability.Builder builder = new ScriptCall.AddRecoveryRotationCapability.Builder();
        builder.recovery_address = Helpers.decode_address_argument(script.args.get(0));
        return builder.build();
    }

    private static ScriptCall decode_add_to_script_allow_list_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.AddToScriptAllowList.Builder builder = new ScriptCall.AddToScriptAllowList.Builder();
        builder.hash = Helpers.decode_u8vector_argument(script.args.get(0));
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(1));
        return builder.build();
    }

    private static ScriptCall decode_add_validator_and_reconfigure_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.AddValidatorAndReconfigure.Builder builder = new ScriptCall.AddValidatorAndReconfigure.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.validator_name = Helpers.decode_u8vector_argument(script.args.get(1));
        builder.validator_address = Helpers.decode_address_argument(script.args.get(2));
        return builder.build();
    }

    private static ScriptCall decode_burn_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.Burn.Builder builder = new ScriptCall.Burn.Builder();
        builder.token = script.ty_args.get(0);
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.preburn_address = Helpers.decode_address_argument(script.args.get(1));
        return builder.build();
    }

    private static ScriptCall decode_burn_txn_fees_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.BurnTxnFees.Builder builder = new ScriptCall.BurnTxnFees.Builder();
        builder.coin_type = script.ty_args.get(0);
        return builder.build();
    }

    private static ScriptCall decode_cancel_burn_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.CancelBurn.Builder builder = new ScriptCall.CancelBurn.Builder();
        builder.token = script.ty_args.get(0);
        builder.preburn_address = Helpers.decode_address_argument(script.args.get(0));
        return builder.build();
    }

    private static ScriptCall decode_create_child_vasp_account_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.CreateChildVaspAccount.Builder builder = new ScriptCall.CreateChildVaspAccount.Builder();
        builder.coin_type = script.ty_args.get(0);
        builder.child_address = Helpers.decode_address_argument(script.args.get(0));
        builder.auth_key_prefix = Helpers.decode_u8vector_argument(script.args.get(1));
        builder.add_all_currencies = Helpers.decode_bool_argument(script.args.get(2));
        builder.child_initial_balance = Helpers.decode_u64_argument(script.args.get(3));
        return builder.build();
    }

    private static ScriptCall decode_create_designated_dealer_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.CreateDesignatedDealer.Builder builder = new ScriptCall.CreateDesignatedDealer.Builder();
        builder.currency = script.ty_args.get(0);
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.addr = Helpers.decode_address_argument(script.args.get(1));
        builder.auth_key_prefix = Helpers.decode_u8vector_argument(script.args.get(2));
        builder.human_name = Helpers.decode_u8vector_argument(script.args.get(3));
        builder.add_all_currencies = Helpers.decode_bool_argument(script.args.get(4));
        return builder.build();
    }

    private static ScriptCall decode_create_parent_vasp_account_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.CreateParentVaspAccount.Builder builder = new ScriptCall.CreateParentVaspAccount.Builder();
        builder.coin_type = script.ty_args.get(0);
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.new_account_address = Helpers.decode_address_argument(script.args.get(1));
        builder.auth_key_prefix = Helpers.decode_u8vector_argument(script.args.get(2));
        builder.human_name = Helpers.decode_u8vector_argument(script.args.get(3));
        builder.add_all_currencies = Helpers.decode_bool_argument(script.args.get(4));
        return builder.build();
    }

    private static ScriptCall decode_create_recovery_address_script(Script _script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.CreateRecoveryAddress.Builder builder = new ScriptCall.CreateRecoveryAddress.Builder();
        return builder.build();
    }

    private static ScriptCall decode_create_validator_account_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.CreateValidatorAccount.Builder builder = new ScriptCall.CreateValidatorAccount.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.new_account_address = Helpers.decode_address_argument(script.args.get(1));
        builder.auth_key_prefix = Helpers.decode_u8vector_argument(script.args.get(2));
        builder.human_name = Helpers.decode_u8vector_argument(script.args.get(3));
        return builder.build();
    }

    private static ScriptCall decode_create_validator_operator_account_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.CreateValidatorOperatorAccount.Builder builder = new ScriptCall.CreateValidatorOperatorAccount.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.new_account_address = Helpers.decode_address_argument(script.args.get(1));
        builder.auth_key_prefix = Helpers.decode_u8vector_argument(script.args.get(2));
        builder.human_name = Helpers.decode_u8vector_argument(script.args.get(3));
        return builder.build();
    }

    private static ScriptCall decode_freeze_account_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.FreezeAccount.Builder builder = new ScriptCall.FreezeAccount.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.to_freeze_account = Helpers.decode_address_argument(script.args.get(1));
        return builder.build();
    }

    private static ScriptCall decode_mint_lbr_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.MintLbr.Builder builder = new ScriptCall.MintLbr.Builder();
        builder.amount_lbr = Helpers.decode_u64_argument(script.args.get(0));
        return builder.build();
    }

    private static ScriptCall decode_peer_to_peer_with_metadata_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.PeerToPeerWithMetadata.Builder builder = new ScriptCall.PeerToPeerWithMetadata.Builder();
        builder.currency = script.ty_args.get(0);
        builder.payee = Helpers.decode_address_argument(script.args.get(0));
        builder.amount = Helpers.decode_u64_argument(script.args.get(1));
        builder.metadata = Helpers.decode_u8vector_argument(script.args.get(2));
        builder.metadata_signature = Helpers.decode_u8vector_argument(script.args.get(3));
        return builder.build();
    }

    private static ScriptCall decode_preburn_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.Preburn.Builder builder = new ScriptCall.Preburn.Builder();
        builder.token = script.ty_args.get(0);
        builder.amount = Helpers.decode_u64_argument(script.args.get(0));
        return builder.build();
    }

    private static ScriptCall decode_publish_shared_ed25519_public_key_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.PublishSharedEd25519PublicKey.Builder builder = new ScriptCall.PublishSharedEd25519PublicKey.Builder();
        builder.public_key = Helpers.decode_u8vector_argument(script.args.get(0));
        return builder.build();
    }

    private static ScriptCall decode_register_validator_config_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.RegisterValidatorConfig.Builder builder = new ScriptCall.RegisterValidatorConfig.Builder();
        builder.validator_account = Helpers.decode_address_argument(script.args.get(0));
        builder.consensus_pubkey = Helpers.decode_u8vector_argument(script.args.get(1));
        builder.validator_network_addresses = Helpers.decode_u8vector_argument(script.args.get(2));
        builder.fullnode_network_addresses = Helpers.decode_u8vector_argument(script.args.get(3));
        return builder.build();
    }

    private static ScriptCall decode_remove_validator_and_reconfigure_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.RemoveValidatorAndReconfigure.Builder builder = new ScriptCall.RemoveValidatorAndReconfigure.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.validator_name = Helpers.decode_u8vector_argument(script.args.get(1));
        builder.validator_address = Helpers.decode_address_argument(script.args.get(2));
        return builder.build();
    }

    private static ScriptCall decode_rotate_authentication_key_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.RotateAuthenticationKey.Builder builder = new ScriptCall.RotateAuthenticationKey.Builder();
        builder.new_key = Helpers.decode_u8vector_argument(script.args.get(0));
        return builder.build();
    }

    private static ScriptCall decode_rotate_authentication_key_with_nonce_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.RotateAuthenticationKeyWithNonce.Builder builder = new ScriptCall.RotateAuthenticationKeyWithNonce.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.new_key = Helpers.decode_u8vector_argument(script.args.get(1));
        return builder.build();
    }

    private static ScriptCall decode_rotate_authentication_key_with_nonce_admin_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.RotateAuthenticationKeyWithNonceAdmin.Builder builder = new ScriptCall.RotateAuthenticationKeyWithNonceAdmin.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.new_key = Helpers.decode_u8vector_argument(script.args.get(1));
        return builder.build();
    }

    private static ScriptCall decode_rotate_authentication_key_with_recovery_address_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.RotateAuthenticationKeyWithRecoveryAddress.Builder builder = new ScriptCall.RotateAuthenticationKeyWithRecoveryAddress.Builder();
        builder.recovery_address = Helpers.decode_address_argument(script.args.get(0));
        builder.to_recover = Helpers.decode_address_argument(script.args.get(1));
        builder.new_key = Helpers.decode_u8vector_argument(script.args.get(2));
        return builder.build();
    }

    private static ScriptCall decode_rotate_dual_attestation_info_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.RotateDualAttestationInfo.Builder builder = new ScriptCall.RotateDualAttestationInfo.Builder();
        builder.new_url = Helpers.decode_u8vector_argument(script.args.get(0));
        builder.new_key = Helpers.decode_u8vector_argument(script.args.get(1));
        return builder.build();
    }

    private static ScriptCall decode_rotate_shared_ed25519_public_key_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.RotateSharedEd25519PublicKey.Builder builder = new ScriptCall.RotateSharedEd25519PublicKey.Builder();
        builder.public_key = Helpers.decode_u8vector_argument(script.args.get(0));
        return builder.build();
    }

    private static ScriptCall decode_set_validator_config_and_reconfigure_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.SetValidatorConfigAndReconfigure.Builder builder = new ScriptCall.SetValidatorConfigAndReconfigure.Builder();
        builder.validator_account = Helpers.decode_address_argument(script.args.get(0));
        builder.consensus_pubkey = Helpers.decode_u8vector_argument(script.args.get(1));
        builder.validator_network_addresses = Helpers.decode_u8vector_argument(script.args.get(2));
        builder.fullnode_network_addresses = Helpers.decode_u8vector_argument(script.args.get(3));
        return builder.build();
    }

    private static ScriptCall decode_set_validator_operator_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.SetValidatorOperator.Builder builder = new ScriptCall.SetValidatorOperator.Builder();
        builder.operator_name = Helpers.decode_u8vector_argument(script.args.get(0));
        builder.operator_account = Helpers.decode_address_argument(script.args.get(1));
        return builder.build();
    }

    private static ScriptCall decode_set_validator_operator_with_nonce_admin_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.SetValidatorOperatorWithNonceAdmin.Builder builder = new ScriptCall.SetValidatorOperatorWithNonceAdmin.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.operator_name = Helpers.decode_u8vector_argument(script.args.get(1));
        builder.operator_account = Helpers.decode_address_argument(script.args.get(2));
        return builder.build();
    }

    private static ScriptCall decode_tiered_mint_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.TieredMint.Builder builder = new ScriptCall.TieredMint.Builder();
        builder.coin_type = script.ty_args.get(0);
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.designated_dealer_address = Helpers.decode_address_argument(script.args.get(1));
        builder.mint_amount = Helpers.decode_u64_argument(script.args.get(2));
        builder.tier_index = Helpers.decode_u64_argument(script.args.get(3));
        return builder.build();
    }

    private static ScriptCall decode_unfreeze_account_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.UnfreezeAccount.Builder builder = new ScriptCall.UnfreezeAccount.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.to_unfreeze_account = Helpers.decode_address_argument(script.args.get(1));
        return builder.build();
    }

    private static ScriptCall decode_unmint_lbr_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.UnmintLbr.Builder builder = new ScriptCall.UnmintLbr.Builder();
        builder.amount_lbr = Helpers.decode_u64_argument(script.args.get(0));
        return builder.build();
    }

    private static ScriptCall decode_update_dual_attestation_limit_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.UpdateDualAttestationLimit.Builder builder = new ScriptCall.UpdateDualAttestationLimit.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.new_micro_lbr_limit = Helpers.decode_u64_argument(script.args.get(1));
        return builder.build();
    }

    private static ScriptCall decode_update_exchange_rate_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.UpdateExchangeRate.Builder builder = new ScriptCall.UpdateExchangeRate.Builder();
        builder.currency = script.ty_args.get(0);
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.new_exchange_rate_numerator = Helpers.decode_u64_argument(script.args.get(1));
        builder.new_exchange_rate_denominator = Helpers.decode_u64_argument(script.args.get(2));
        return builder.build();
    }

    private static ScriptCall decode_update_libra_version_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.UpdateLibraVersion.Builder builder = new ScriptCall.UpdateLibraVersion.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.major = Helpers.decode_u64_argument(script.args.get(1));
        return builder.build();
    }

    private static ScriptCall decode_update_minting_ability_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.UpdateMintingAbility.Builder builder = new ScriptCall.UpdateMintingAbility.Builder();
        builder.currency = script.ty_args.get(0);
        builder.allow_minting = Helpers.decode_bool_argument(script.args.get(0));
        return builder.build();
    }

    interface EncodingHelper {
        public Script encode(ScriptCall call);
    }

    private static final java.util.Map<Class<?>, EncodingHelper> SCRIPT_ENCODER_MAP = initEncoderMap();

    private static java.util.Map<Class<?>, EncodingHelper> initEncoderMap() {
        java.util.HashMap<Class<?>, EncodingHelper> map = new java.util.HashMap<>();
        map.put(ScriptCall.AddCurrencyToAccount.class, (EncodingHelper)((call) -> {
            ScriptCall.AddCurrencyToAccount obj = (ScriptCall.AddCurrencyToAccount)call;
            return Helpers.encode_add_currency_to_account_script(obj.currency);
        }));
        map.put(ScriptCall.AddRecoveryRotationCapability.class, (EncodingHelper)((call) -> {
            ScriptCall.AddRecoveryRotationCapability obj = (ScriptCall.AddRecoveryRotationCapability)call;
            return Helpers.encode_add_recovery_rotation_capability_script(obj.recovery_address);
        }));
        map.put(ScriptCall.AddToScriptAllowList.class, (EncodingHelper)((call) -> {
            ScriptCall.AddToScriptAllowList obj = (ScriptCall.AddToScriptAllowList)call;
            return Helpers.encode_add_to_script_allow_list_script(obj.hash, obj.sliding_nonce);
        }));
        map.put(ScriptCall.AddValidatorAndReconfigure.class, (EncodingHelper)((call) -> {
            ScriptCall.AddValidatorAndReconfigure obj = (ScriptCall.AddValidatorAndReconfigure)call;
            return Helpers.encode_add_validator_and_reconfigure_script(obj.sliding_nonce, obj.validator_name, obj.validator_address);
        }));
        map.put(ScriptCall.Burn.class, (EncodingHelper)((call) -> {
            ScriptCall.Burn obj = (ScriptCall.Burn)call;
            return Helpers.encode_burn_script(obj.token, obj.sliding_nonce, obj.preburn_address);
        }));
        map.put(ScriptCall.BurnTxnFees.class, (EncodingHelper)((call) -> {
            ScriptCall.BurnTxnFees obj = (ScriptCall.BurnTxnFees)call;
            return Helpers.encode_burn_txn_fees_script(obj.coin_type);
        }));
        map.put(ScriptCall.CancelBurn.class, (EncodingHelper)((call) -> {
            ScriptCall.CancelBurn obj = (ScriptCall.CancelBurn)call;
            return Helpers.encode_cancel_burn_script(obj.token, obj.preburn_address);
        }));
        map.put(ScriptCall.CreateChildVaspAccount.class, (EncodingHelper)((call) -> {
            ScriptCall.CreateChildVaspAccount obj = (ScriptCall.CreateChildVaspAccount)call;
            return Helpers.encode_create_child_vasp_account_script(obj.coin_type, obj.child_address, obj.auth_key_prefix, obj.add_all_currencies, obj.child_initial_balance);
        }));
        map.put(ScriptCall.CreateDesignatedDealer.class, (EncodingHelper)((call) -> {
            ScriptCall.CreateDesignatedDealer obj = (ScriptCall.CreateDesignatedDealer)call;
            return Helpers.encode_create_designated_dealer_script(obj.currency, obj.sliding_nonce, obj.addr, obj.auth_key_prefix, obj.human_name, obj.add_all_currencies);
        }));
        map.put(ScriptCall.CreateParentVaspAccount.class, (EncodingHelper)((call) -> {
            ScriptCall.CreateParentVaspAccount obj = (ScriptCall.CreateParentVaspAccount)call;
            return Helpers.encode_create_parent_vasp_account_script(obj.coin_type, obj.sliding_nonce, obj.new_account_address, obj.auth_key_prefix, obj.human_name, obj.add_all_currencies);
        }));
        map.put(ScriptCall.CreateRecoveryAddress.class, (EncodingHelper)((call) -> {
            ScriptCall.CreateRecoveryAddress obj = (ScriptCall.CreateRecoveryAddress)call;
            return Helpers.encode_create_recovery_address_script();
        }));
        map.put(ScriptCall.CreateValidatorAccount.class, (EncodingHelper)((call) -> {
            ScriptCall.CreateValidatorAccount obj = (ScriptCall.CreateValidatorAccount)call;
            return Helpers.encode_create_validator_account_script(obj.sliding_nonce, obj.new_account_address, obj.auth_key_prefix, obj.human_name);
        }));
        map.put(ScriptCall.CreateValidatorOperatorAccount.class, (EncodingHelper)((call) -> {
            ScriptCall.CreateValidatorOperatorAccount obj = (ScriptCall.CreateValidatorOperatorAccount)call;
            return Helpers.encode_create_validator_operator_account_script(obj.sliding_nonce, obj.new_account_address, obj.auth_key_prefix, obj.human_name);
        }));
        map.put(ScriptCall.FreezeAccount.class, (EncodingHelper)((call) -> {
            ScriptCall.FreezeAccount obj = (ScriptCall.FreezeAccount)call;
            return Helpers.encode_freeze_account_script(obj.sliding_nonce, obj.to_freeze_account);
        }));
        map.put(ScriptCall.MintLbr.class, (EncodingHelper)((call) -> {
            ScriptCall.MintLbr obj = (ScriptCall.MintLbr)call;
            return Helpers.encode_mint_lbr_script(obj.amount_lbr);
        }));
        map.put(ScriptCall.PeerToPeerWithMetadata.class, (EncodingHelper)((call) -> {
            ScriptCall.PeerToPeerWithMetadata obj = (ScriptCall.PeerToPeerWithMetadata)call;
            return Helpers.encode_peer_to_peer_with_metadata_script(obj.currency, obj.payee, obj.amount, obj.metadata, obj.metadata_signature);
        }));
        map.put(ScriptCall.Preburn.class, (EncodingHelper)((call) -> {
            ScriptCall.Preburn obj = (ScriptCall.Preburn)call;
            return Helpers.encode_preburn_script(obj.token, obj.amount);
        }));
        map.put(ScriptCall.PublishSharedEd25519PublicKey.class, (EncodingHelper)((call) -> {
            ScriptCall.PublishSharedEd25519PublicKey obj = (ScriptCall.PublishSharedEd25519PublicKey)call;
            return Helpers.encode_publish_shared_ed25519_public_key_script(obj.public_key);
        }));
        map.put(ScriptCall.RegisterValidatorConfig.class, (EncodingHelper)((call) -> {
            ScriptCall.RegisterValidatorConfig obj = (ScriptCall.RegisterValidatorConfig)call;
            return Helpers.encode_register_validator_config_script(obj.validator_account, obj.consensus_pubkey, obj.validator_network_addresses, obj.fullnode_network_addresses);
        }));
        map.put(ScriptCall.RemoveValidatorAndReconfigure.class, (EncodingHelper)((call) -> {
            ScriptCall.RemoveValidatorAndReconfigure obj = (ScriptCall.RemoveValidatorAndReconfigure)call;
            return Helpers.encode_remove_validator_and_reconfigure_script(obj.sliding_nonce, obj.validator_name, obj.validator_address);
        }));
        map.put(ScriptCall.RotateAuthenticationKey.class, (EncodingHelper)((call) -> {
            ScriptCall.RotateAuthenticationKey obj = (ScriptCall.RotateAuthenticationKey)call;
            return Helpers.encode_rotate_authentication_key_script(obj.new_key);
        }));
        map.put(ScriptCall.RotateAuthenticationKeyWithNonce.class, (EncodingHelper)((call) -> {
            ScriptCall.RotateAuthenticationKeyWithNonce obj = (ScriptCall.RotateAuthenticationKeyWithNonce)call;
            return Helpers.encode_rotate_authentication_key_with_nonce_script(obj.sliding_nonce, obj.new_key);
        }));
        map.put(ScriptCall.RotateAuthenticationKeyWithNonceAdmin.class, (EncodingHelper)((call) -> {
            ScriptCall.RotateAuthenticationKeyWithNonceAdmin obj = (ScriptCall.RotateAuthenticationKeyWithNonceAdmin)call;
            return Helpers.encode_rotate_authentication_key_with_nonce_admin_script(obj.sliding_nonce, obj.new_key);
        }));
        map.put(ScriptCall.RotateAuthenticationKeyWithRecoveryAddress.class, (EncodingHelper)((call) -> {
            ScriptCall.RotateAuthenticationKeyWithRecoveryAddress obj = (ScriptCall.RotateAuthenticationKeyWithRecoveryAddress)call;
            return Helpers.encode_rotate_authentication_key_with_recovery_address_script(obj.recovery_address, obj.to_recover, obj.new_key);
        }));
        map.put(ScriptCall.RotateDualAttestationInfo.class, (EncodingHelper)((call) -> {
            ScriptCall.RotateDualAttestationInfo obj = (ScriptCall.RotateDualAttestationInfo)call;
            return Helpers.encode_rotate_dual_attestation_info_script(obj.new_url, obj.new_key);
        }));
        map.put(ScriptCall.RotateSharedEd25519PublicKey.class, (EncodingHelper)((call) -> {
            ScriptCall.RotateSharedEd25519PublicKey obj = (ScriptCall.RotateSharedEd25519PublicKey)call;
            return Helpers.encode_rotate_shared_ed25519_public_key_script(obj.public_key);
        }));
        map.put(ScriptCall.SetValidatorConfigAndReconfigure.class, (EncodingHelper)((call) -> {
            ScriptCall.SetValidatorConfigAndReconfigure obj = (ScriptCall.SetValidatorConfigAndReconfigure)call;
            return Helpers.encode_set_validator_config_and_reconfigure_script(obj.validator_account, obj.consensus_pubkey, obj.validator_network_addresses, obj.fullnode_network_addresses);
        }));
        map.put(ScriptCall.SetValidatorOperator.class, (EncodingHelper)((call) -> {
            ScriptCall.SetValidatorOperator obj = (ScriptCall.SetValidatorOperator)call;
            return Helpers.encode_set_validator_operator_script(obj.operator_name, obj.operator_account);
        }));
        map.put(ScriptCall.SetValidatorOperatorWithNonceAdmin.class, (EncodingHelper)((call) -> {
            ScriptCall.SetValidatorOperatorWithNonceAdmin obj = (ScriptCall.SetValidatorOperatorWithNonceAdmin)call;
            return Helpers.encode_set_validator_operator_with_nonce_admin_script(obj.sliding_nonce, obj.operator_name, obj.operator_account);
        }));
        map.put(ScriptCall.TieredMint.class, (EncodingHelper)((call) -> {
            ScriptCall.TieredMint obj = (ScriptCall.TieredMint)call;
            return Helpers.encode_tiered_mint_script(obj.coin_type, obj.sliding_nonce, obj.designated_dealer_address, obj.mint_amount, obj.tier_index);
        }));
        map.put(ScriptCall.UnfreezeAccount.class, (EncodingHelper)((call) -> {
            ScriptCall.UnfreezeAccount obj = (ScriptCall.UnfreezeAccount)call;
            return Helpers.encode_unfreeze_account_script(obj.sliding_nonce, obj.to_unfreeze_account);
        }));
        map.put(ScriptCall.UnmintLbr.class, (EncodingHelper)((call) -> {
            ScriptCall.UnmintLbr obj = (ScriptCall.UnmintLbr)call;
            return Helpers.encode_unmint_lbr_script(obj.amount_lbr);
        }));
        map.put(ScriptCall.UpdateDualAttestationLimit.class, (EncodingHelper)((call) -> {
            ScriptCall.UpdateDualAttestationLimit obj = (ScriptCall.UpdateDualAttestationLimit)call;
            return Helpers.encode_update_dual_attestation_limit_script(obj.sliding_nonce, obj.new_micro_lbr_limit);
        }));
        map.put(ScriptCall.UpdateExchangeRate.class, (EncodingHelper)((call) -> {
            ScriptCall.UpdateExchangeRate obj = (ScriptCall.UpdateExchangeRate)call;
            return Helpers.encode_update_exchange_rate_script(obj.currency, obj.sliding_nonce, obj.new_exchange_rate_numerator, obj.new_exchange_rate_denominator);
        }));
        map.put(ScriptCall.UpdateLibraVersion.class, (EncodingHelper)((call) -> {
            ScriptCall.UpdateLibraVersion obj = (ScriptCall.UpdateLibraVersion)call;
            return Helpers.encode_update_libra_version_script(obj.sliding_nonce, obj.major);
        }));
        map.put(ScriptCall.UpdateMintingAbility.class, (EncodingHelper)((call) -> {
            ScriptCall.UpdateMintingAbility obj = (ScriptCall.UpdateMintingAbility)call;
            return Helpers.encode_update_minting_ability_script(obj.currency, obj.allow_minting);
        }));
        return map;
    }

    public static byte[] ADD_CURRENCY_TO_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 2, 3, 2, 6, 4, 8, 2, 5, 10, 7, 7, 17, 26, 8, 43, 16, 0, 0, 0, 1, 0, 1, 1, 1, 0, 2, 1, 6, 12, 0, 1, 9, 0, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 12, 97, 100, 100, 95, 99, 117, 114, 114, 101, 110, 99, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 3, 11, 0, 56, 0, 2};

    public static byte[] ADD_RECOVERY_ROTATION_CAPABILITY_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 2, 4, 4, 3, 8, 10, 5, 18, 15, 7, 33, 107, 8, -116, 1, 16, 0, 0, 0, 1, 0, 2, 1, 0, 0, 3, 0, 1, 0, 1, 4, 2, 3, 0, 1, 6, 12, 1, 8, 0, 2, 8, 0, 5, 0, 2, 6, 12, 5, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 15, 82, 101, 99, 111, 118, 101, 114, 121, 65, 100, 100, 114, 101, 115, 115, 21, 75, 101, 121, 82, 111, 116, 97, 116, 105, 111, 110, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 101, 120, 116, 114, 97, 99, 116, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 23, 97, 100, 100, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 4, 3, 5, 11, 0, 17, 0, 10, 1, 17, 1, 2};

    public static byte[] ADD_TO_SCRIPT_ALLOW_LIST_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 16, 7, 30, 93, 8, 123, 16, 0, 0, 0, 1, 0, 2, 0, 1, 0, 1, 3, 2, 1, 0, 2, 6, 12, 10, 2, 0, 2, 6, 12, 3, 3, 6, 12, 10, 2, 3, 32, 76, 105, 98, 114, 97, 84, 114, 97, 110, 115, 97, 99, 116, 105, 111, 110, 80, 117, 98, 108, 105, 115, 104, 105, 110, 103, 79, 112, 116, 105, 111, 110, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 24, 97, 100, 100, 95, 116, 111, 95, 115, 99, 114, 105, 112, 116, 95, 97, 108, 108, 111, 119, 95, 108, 105, 115, 116, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 1, 7, 10, 0, 10, 2, 17, 1, 11, 0, 11, 1, 17, 0, 2};

    public static byte[] ADD_VALIDATOR_AND_RECONFIGURE_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 6, 3, 6, 15, 5, 21, 24, 7, 45, 92, 8, -119, 1, 16, 0, 0, 0, 1, 0, 2, 1, 3, 0, 1, 0, 2, 4, 2, 3, 0, 0, 5, 4, 1, 0, 2, 6, 12, 3, 0, 1, 5, 1, 10, 2, 2, 6, 12, 5, 4, 6, 12, 3, 10, 2, 5, 2, 1, 3, 11, 76, 105, 98, 114, 97, 83, 121, 115, 116, 101, 109, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 15, 86, 97, 108, 105, 100, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 14, 103, 101, 116, 95, 104, 117, 109, 97, 110, 95, 110, 97, 109, 101, 13, 97, 100, 100, 95, 118, 97, 108, 105, 100, 97, 116, 111, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 6, 18, 10, 0, 10, 1, 17, 0, 10, 3, 17, 1, 11, 2, 33, 12, 4, 11, 4, 3, 14, 11, 0, 1, 6, 0, 0, 0, 0, 0, 0, 0, 0, 39, 11, 0, 10, 3, 17, 2, 2};

    public static byte[] BURN_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 3, 4, 11, 4, 15, 2, 5, 17, 17, 7, 34, 46, 8, 80, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 1, 1, 1, 4, 2, 6, 12, 3, 0, 2, 6, 12, 5, 3, 6, 12, 3, 5, 1, 9, 0, 5, 76, 105, 98, 114, 97, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 4, 98, 117, 114, 110, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 3, 1, 7, 10, 0, 10, 1, 17, 0, 11, 0, 10, 2, 56, 0, 2};

    public static byte[] BURN_TXN_FEES_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 2, 3, 2, 6, 4, 8, 2, 5, 10, 7, 7, 17, 25, 8, 42, 16, 0, 0, 0, 1, 0, 1, 1, 1, 0, 2, 1, 6, 12, 0, 1, 9, 0, 14, 84, 114, 97, 110, 115, 97, 99, 116, 105, 111, 110, 70, 101, 101, 9, 98, 117, 114, 110, 95, 102, 101, 101, 115, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 3, 11, 0, 56, 0, 2};

    public static byte[] CANCEL_BURN_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 2, 3, 2, 6, 4, 8, 2, 5, 10, 8, 7, 18, 25, 8, 43, 16, 0, 0, 0, 1, 0, 1, 1, 1, 0, 2, 2, 6, 12, 5, 0, 1, 9, 0, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 11, 99, 97, 110, 99, 101, 108, 95, 98, 117, 114, 110, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 4, 11, 0, 10, 1, 56, 0, 2};

    public static byte[] CREATE_CHILD_VASP_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 8, 1, 0, 2, 2, 2, 4, 3, 6, 22, 4, 28, 4, 5, 32, 35, 7, 67, 123, 8, -66, 1, 16, 6, -50, 1, 4, 0, 0, 0, 1, 1, 0, 0, 2, 0, 1, 1, 1, 0, 3, 2, 3, 0, 0, 4, 4, 1, 1, 1, 0, 5, 3, 1, 0, 0, 6, 2, 6, 4, 6, 12, 5, 10, 2, 1, 0, 1, 6, 12, 1, 8, 0, 5, 6, 8, 0, 5, 3, 10, 2, 10, 2, 5, 6, 12, 5, 10, 2, 1, 3, 1, 9, 0, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 18, 87, 105, 116, 104, 100, 114, 97, 119, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 25, 99, 114, 101, 97, 116, 101, 95, 99, 104, 105, 108, 100, 95, 118, 97, 115, 112, 95, 97, 99, 99, 111, 117, 110, 116, 27, 101, 120, 116, 114, 97, 99, 116, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 8, 112, 97, 121, 95, 102, 114, 111, 109, 27, 114, 101, 115, 116, 111, 114, 101, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 2, 1, 0, 1, 1, 5, 3, 25, 10, 0, 10, 1, 11, 2, 10, 3, 56, 0, 10, 4, 6, 0, 0, 0, 0, 0, 0, 0, 0, 36, 3, 10, 5, 22, 11, 0, 17, 1, 12, 5, 14, 5, 10, 1, 10, 4, 7, 0, 7, 0, 56, 1, 11, 5, 17, 3, 5, 24, 11, 0, 1, 2};

    public static byte[] CREATE_DESIGNATED_DEALER_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 3, 4, 11, 4, 15, 2, 5, 17, 27, 7, 44, 73, 8, 117, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 1, 1, 1, 4, 2, 6, 12, 3, 0, 5, 6, 12, 5, 10, 2, 10, 2, 1, 6, 6, 12, 3, 5, 10, 2, 10, 2, 1, 1, 9, 0, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 24, 99, 114, 101, 97, 116, 101, 95, 100, 101, 115, 105, 103, 110, 97, 116, 101, 100, 95, 100, 101, 97, 108, 101, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 3, 1, 10, 10, 0, 10, 1, 17, 0, 11, 0, 10, 2, 11, 3, 11, 4, 10, 5, 56, 0, 2};

    public static byte[] CREATE_PARENT_VASP_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 3, 4, 11, 4, 15, 2, 5, 17, 27, 7, 44, 75, 8, 119, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 1, 1, 1, 4, 2, 6, 12, 3, 0, 5, 6, 12, 5, 10, 2, 10, 2, 1, 6, 6, 12, 3, 5, 10, 2, 10, 2, 1, 1, 9, 0, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 26, 99, 114, 101, 97, 116, 101, 95, 112, 97, 114, 101, 110, 116, 95, 118, 97, 115, 112, 95, 97, 99, 99, 111, 117, 110, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 3, 1, 10, 10, 0, 10, 1, 17, 0, 11, 0, 10, 2, 11, 3, 11, 4, 10, 5, 56, 0, 2};

    public static byte[] CREATE_RECOVERY_ADDRESS_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 2, 4, 4, 3, 8, 10, 5, 18, 12, 7, 30, 91, 8, 121, 16, 0, 0, 0, 1, 0, 2, 1, 0, 0, 3, 0, 1, 0, 1, 4, 2, 3, 0, 1, 6, 12, 1, 8, 0, 2, 6, 12, 8, 0, 0, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 15, 82, 101, 99, 111, 118, 101, 114, 121, 65, 100, 100, 114, 101, 115, 115, 21, 75, 101, 121, 82, 111, 116, 97, 116, 105, 111, 110, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 101, 120, 116, 114, 97, 99, 116, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 7, 112, 117, 98, 108, 105, 115, 104, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 3, 5, 10, 0, 11, 0, 17, 0, 17, 1, 2};

    public static byte[] CREATE_VALIDATOR_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 22, 7, 36, 73, 8, 109, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 0, 2, 6, 12, 3, 0, 4, 6, 12, 5, 10, 2, 10, 2, 5, 6, 12, 3, 5, 10, 2, 10, 2, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 24, 99, 114, 101, 97, 116, 101, 95, 118, 97, 108, 105, 100, 97, 116, 111, 114, 95, 97, 99, 99, 111, 117, 110, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 1, 9, 10, 0, 10, 1, 17, 0, 11, 0, 10, 2, 11, 3, 11, 4, 17, 1, 2};

    public static byte[] CREATE_VALIDATOR_OPERATOR_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 22, 7, 36, 82, 8, 118, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 0, 2, 6, 12, 3, 0, 4, 6, 12, 5, 10, 2, 10, 2, 5, 6, 12, 3, 5, 10, 2, 10, 2, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 33, 99, 114, 101, 97, 116, 101, 95, 118, 97, 108, 105, 100, 97, 116, 111, 114, 95, 111, 112, 101, 114, 97, 116, 111, 114, 95, 97, 99, 99, 111, 117, 110, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 1, 9, 10, 0, 10, 1, 17, 0, 11, 0, 10, 2, 11, 3, 11, 4, 17, 1, 2};

    public static byte[] FREEZE_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 14, 7, 28, 66, 8, 94, 16, 0, 0, 0, 1, 0, 2, 0, 1, 0, 1, 3, 2, 1, 0, 2, 6, 12, 5, 0, 2, 6, 12, 3, 3, 6, 12, 3, 5, 15, 65, 99, 99, 111, 117, 110, 116, 70, 114, 101, 101, 122, 105, 110, 103, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 14, 102, 114, 101, 101, 122, 101, 95, 97, 99, 99, 111, 117, 110, 116, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 1, 7, 10, 0, 10, 1, 17, 1, 11, 0, 10, 2, 17, 0, 2};

    public static byte[] MINT_LBR_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 2, 2, 2, 4, 3, 6, 15, 5, 21, 16, 7, 37, 99, 8, -120, 1, 16, 0, 0, 0, 1, 1, 0, 0, 2, 0, 1, 0, 0, 3, 1, 2, 0, 0, 4, 3, 2, 0, 1, 6, 12, 1, 8, 0, 0, 2, 6, 8, 0, 3, 2, 6, 12, 3, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 18, 87, 105, 116, 104, 100, 114, 97, 119, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 27, 101, 120, 116, 114, 97, 99, 116, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 27, 114, 101, 115, 116, 111, 114, 101, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 10, 115, 116, 97, 112, 108, 101, 95, 108, 98, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 4, 1, 9, 11, 0, 17, 0, 12, 2, 14, 2, 10, 1, 17, 2, 11, 2, 17, 1, 2};

    public static byte[] PEER_TO_PEER_WITH_METADATA_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 7, 1, 0, 2, 2, 2, 4, 3, 6, 16, 4, 22, 2, 5, 24, 29, 7, 53, 97, 8, -106, 1, 16, 0, 0, 0, 1, 1, 0, 0, 2, 0, 1, 0, 0, 3, 2, 3, 1, 1, 0, 4, 1, 3, 0, 1, 5, 1, 6, 12, 1, 8, 0, 5, 6, 8, 0, 5, 3, 10, 2, 10, 2, 0, 5, 6, 12, 5, 3, 10, 2, 10, 2, 1, 9, 0, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 18, 87, 105, 116, 104, 100, 114, 97, 119, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 27, 101, 120, 116, 114, 97, 99, 116, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 8, 112, 97, 121, 95, 102, 114, 111, 109, 27, 114, 101, 115, 116, 111, 114, 101, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 4, 1, 12, 11, 0, 17, 0, 12, 5, 14, 5, 10, 1, 10, 2, 11, 3, 11, 4, 56, 0, 11, 5, 17, 2, 2};

    public static byte[] PREBURN_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 7, 1, 0, 2, 2, 2, 4, 3, 6, 16, 4, 22, 2, 5, 24, 21, 7, 45, 96, 8, -115, 1, 16, 0, 0, 0, 1, 1, 0, 0, 2, 0, 1, 0, 0, 3, 2, 3, 1, 1, 0, 4, 1, 3, 0, 1, 5, 1, 6, 12, 1, 8, 0, 3, 6, 12, 6, 8, 0, 3, 0, 2, 6, 12, 3, 1, 9, 0, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 18, 87, 105, 116, 104, 100, 114, 97, 119, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 27, 101, 120, 116, 114, 97, 99, 116, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 7, 112, 114, 101, 98, 117, 114, 110, 27, 114, 101, 115, 116, 111, 114, 101, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 4, 1, 10, 10, 0, 17, 0, 12, 2, 11, 0, 14, 2, 10, 1, 56, 0, 11, 2, 17, 2, 2};

    public static byte[] PUBLISH_SHARED_ED25519_PUBLIC_KEY_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 2, 3, 2, 5, 5, 7, 6, 7, 13, 31, 8, 44, 16, 0, 0, 0, 1, 0, 1, 0, 2, 6, 12, 10, 2, 0, 22, 83, 104, 97, 114, 101, 100, 69, 100, 50, 53, 53, 49, 57, 80, 117, 98, 108, 105, 99, 75, 101, 121, 7, 112, 117, 98, 108, 105, 115, 104, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 4, 11, 0, 11, 1, 17, 0, 2};

    public static byte[] REGISTER_VALIDATOR_CONFIG_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 2, 3, 2, 5, 5, 7, 11, 7, 18, 27, 8, 45, 16, 0, 0, 0, 1, 0, 1, 0, 5, 6, 12, 5, 10, 2, 10, 2, 10, 2, 0, 15, 86, 97, 108, 105, 100, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 10, 115, 101, 116, 95, 99, 111, 110, 102, 105, 103, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 7, 11, 0, 10, 1, 11, 2, 11, 3, 11, 4, 17, 0, 2};

    public static byte[] REMOVE_VALIDATOR_AND_RECONFIGURE_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 6, 3, 6, 15, 5, 21, 24, 7, 45, 95, 8, -116, 1, 16, 0, 0, 0, 1, 0, 2, 1, 3, 0, 1, 0, 2, 4, 2, 3, 0, 0, 5, 4, 1, 0, 2, 6, 12, 3, 0, 1, 5, 1, 10, 2, 2, 6, 12, 5, 4, 6, 12, 3, 10, 2, 5, 2, 1, 3, 11, 76, 105, 98, 114, 97, 83, 121, 115, 116, 101, 109, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 15, 86, 97, 108, 105, 100, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 14, 103, 101, 116, 95, 104, 117, 109, 97, 110, 95, 110, 97, 109, 101, 16, 114, 101, 109, 111, 118, 101, 95, 118, 97, 108, 105, 100, 97, 116, 111, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 6, 18, 10, 0, 10, 1, 17, 0, 10, 3, 17, 1, 11, 2, 33, 12, 4, 11, 4, 3, 14, 11, 0, 1, 6, 0, 0, 0, 0, 0, 0, 0, 0, 39, 11, 0, 10, 3, 17, 2, 2};

    public static byte[] ROTATE_AUTHENTICATION_KEY_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 2, 4, 4, 3, 8, 25, 5, 33, 32, 7, 65, -81, 1, 8, -16, 1, 16, 0, 0, 0, 1, 0, 3, 1, 0, 1, 2, 0, 1, 0, 0, 4, 0, 2, 0, 0, 5, 3, 4, 0, 0, 6, 2, 5, 0, 0, 7, 6, 5, 0, 1, 6, 12, 1, 5, 1, 8, 0, 1, 6, 8, 0, 1, 6, 5, 0, 2, 6, 8, 0, 10, 2, 2, 6, 12, 10, 2, 3, 8, 0, 1, 3, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 6, 83, 105, 103, 110, 101, 114, 10, 97, 100, 100, 114, 101, 115, 115, 95, 111, 102, 21, 75, 101, 121, 82, 111, 116, 97, 116, 105, 111, 110, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 101, 120, 116, 114, 97, 99, 116, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 95, 97, 100, 100, 114, 101, 115, 115, 31, 114, 101, 115, 116, 111, 114, 101, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 25, 114, 111, 116, 97, 116, 101, 95, 97, 117, 116, 104, 101, 110, 116, 105, 99, 97, 116, 105, 111, 110, 95, 107, 101, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 7, 8, 20, 10, 0, 17, 1, 12, 2, 14, 2, 17, 2, 20, 11, 0, 17, 0, 33, 12, 3, 11, 3, 3, 14, 6, 0, 0, 0, 0, 0, 0, 0, 0, 39, 14, 2, 11, 1, 17, 4, 11, 2, 17, 3, 2};

    public static byte[] ROTATE_AUTHENTICATION_KEY_WITH_NONCE_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 2, 4, 4, 3, 8, 20, 5, 28, 23, 7, 51, -96, 1, 8, -45, 1, 16, 0, 0, 0, 1, 0, 3, 1, 0, 1, 2, 0, 1, 0, 0, 4, 2, 3, 0, 0, 5, 3, 1, 0, 0, 6, 4, 1, 0, 2, 6, 12, 3, 0, 1, 6, 12, 1, 8, 0, 2, 6, 8, 0, 10, 2, 3, 6, 12, 3, 10, 2, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 21, 75, 101, 121, 82, 111, 116, 97, 116, 105, 111, 110, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 101, 120, 116, 114, 97, 99, 116, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 114, 101, 115, 116, 111, 114, 101, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 25, 114, 111, 116, 97, 116, 101, 95, 97, 117, 116, 104, 101, 110, 116, 105, 99, 97, 116, 105, 111, 110, 95, 107, 101, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 3, 12, 10, 0, 10, 1, 17, 0, 11, 0, 17, 1, 12, 3, 14, 3, 11, 2, 17, 3, 11, 3, 17, 2, 2};

    public static byte[] ROTATE_AUTHENTICATION_KEY_WITH_NONCE_ADMIN_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 2, 4, 4, 3, 8, 20, 5, 28, 25, 7, 53, -96, 1, 8, -43, 1, 16, 0, 0, 0, 1, 0, 3, 1, 0, 1, 2, 0, 1, 0, 0, 4, 2, 3, 0, 0, 5, 3, 1, 0, 0, 6, 4, 1, 0, 2, 6, 12, 3, 0, 1, 6, 12, 1, 8, 0, 2, 6, 8, 0, 10, 2, 4, 6, 12, 6, 12, 3, 10, 2, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 21, 75, 101, 121, 82, 111, 116, 97, 116, 105, 111, 110, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 101, 120, 116, 114, 97, 99, 116, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 114, 101, 115, 116, 111, 114, 101, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 25, 114, 111, 116, 97, 116, 101, 95, 97, 117, 116, 104, 101, 110, 116, 105, 99, 97, 116, 105, 111, 110, 95, 107, 101, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 3, 12, 11, 0, 10, 2, 17, 0, 11, 1, 17, 1, 12, 4, 14, 4, 11, 3, 17, 3, 11, 4, 17, 2, 2};

    public static byte[] ROTATE_AUTHENTICATION_KEY_WITH_RECOVERY_ADDRESS_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 2, 3, 2, 5, 5, 7, 8, 7, 15, 42, 8, 57, 16, 0, 0, 0, 1, 0, 1, 0, 4, 6, 12, 5, 5, 10, 2, 0, 15, 82, 101, 99, 111, 118, 101, 114, 121, 65, 100, 100, 114, 101, 115, 115, 25, 114, 111, 116, 97, 116, 101, 95, 97, 117, 116, 104, 101, 110, 116, 105, 99, 97, 116, 105, 111, 110, 95, 107, 101, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 6, 11, 0, 10, 1, 10, 2, 11, 3, 17, 0, 2};

    public static byte[] ROTATE_DUAL_ATTESTATION_INFO_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 2, 3, 2, 10, 5, 12, 13, 7, 25, 61, 8, 86, 16, 0, 0, 0, 1, 0, 1, 0, 0, 2, 0, 1, 0, 2, 6, 12, 10, 2, 0, 3, 6, 12, 10, 2, 10, 2, 15, 68, 117, 97, 108, 65, 116, 116, 101, 115, 116, 97, 116, 105, 111, 110, 15, 114, 111, 116, 97, 116, 101, 95, 98, 97, 115, 101, 95, 117, 114, 108, 28, 114, 111, 116, 97, 116, 101, 95, 99, 111, 109, 112, 108, 105, 97, 110, 99, 101, 95, 112, 117, 98, 108, 105, 99, 95, 107, 101, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 1, 7, 10, 0, 11, 1, 17, 0, 11, 0, 11, 2, 17, 1, 2};

    public static byte[] ROTATE_SHARED_ED25519_PUBLIC_KEY_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 2, 3, 2, 5, 5, 7, 6, 7, 13, 34, 8, 47, 16, 0, 0, 0, 1, 0, 1, 0, 2, 6, 12, 10, 2, 0, 22, 83, 104, 97, 114, 101, 100, 69, 100, 50, 53, 53, 49, 57, 80, 117, 98, 108, 105, 99, 75, 101, 121, 10, 114, 111, 116, 97, 116, 101, 95, 107, 101, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 4, 11, 0, 11, 1, 17, 0, 2};

    public static byte[] SET_VALIDATOR_CONFIG_AND_RECONFIGURE_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 15, 7, 29, 69, 8, 98, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 0, 5, 6, 12, 5, 10, 2, 10, 2, 10, 2, 0, 2, 6, 12, 5, 11, 76, 105, 98, 114, 97, 83, 121, 115, 116, 101, 109, 15, 86, 97, 108, 105, 100, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 10, 115, 101, 116, 95, 99, 111, 110, 102, 105, 103, 29, 117, 112, 100, 97, 116, 101, 95, 99, 111, 110, 102, 105, 103, 95, 97, 110, 100, 95, 114, 101, 99, 111, 110, 102, 105, 103, 117, 114, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 10, 10, 0, 10, 1, 11, 2, 11, 3, 11, 4, 17, 0, 11, 0, 10, 1, 17, 1, 2};

    public static byte[] SET_VALIDATOR_OPERATOR_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 19, 7, 33, 68, 8, 101, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 3, 0, 1, 5, 1, 10, 2, 2, 6, 12, 5, 0, 3, 6, 12, 10, 2, 5, 2, 1, 3, 15, 86, 97, 108, 105, 100, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 23, 86, 97, 108, 105, 100, 97, 116, 111, 114, 79, 112, 101, 114, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 14, 103, 101, 116, 95, 104, 117, 109, 97, 110, 95, 110, 97, 109, 101, 12, 115, 101, 116, 95, 111, 112, 101, 114, 97, 116, 111, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 4, 5, 15, 10, 2, 17, 0, 11, 1, 33, 12, 3, 11, 3, 3, 11, 11, 0, 1, 6, 0, 0, 0, 0, 0, 0, 0, 0, 39, 11, 0, 10, 2, 17, 1, 2};

    public static byte[] SET_VALIDATOR_OPERATOR_WITH_NONCE_ADMIN_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 6, 3, 6, 15, 5, 21, 26, 7, 47, 103, 8, -106, 1, 16, 0, 0, 0, 1, 0, 2, 0, 3, 0, 1, 0, 2, 4, 2, 3, 0, 1, 5, 4, 1, 0, 2, 6, 12, 3, 0, 1, 5, 1, 10, 2, 2, 6, 12, 5, 5, 6, 12, 6, 12, 3, 10, 2, 5, 2, 1, 3, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 15, 86, 97, 108, 105, 100, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 23, 86, 97, 108, 105, 100, 97, 116, 111, 114, 79, 112, 101, 114, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 14, 103, 101, 116, 95, 104, 117, 109, 97, 110, 95, 110, 97, 109, 101, 12, 115, 101, 116, 95, 111, 112, 101, 114, 97, 116, 111, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 6, 18, 11, 0, 10, 2, 17, 0, 10, 4, 17, 1, 11, 3, 33, 12, 5, 11, 5, 3, 14, 11, 1, 1, 6, 0, 0, 0, 0, 0, 0, 0, 0, 39, 11, 1, 10, 4, 17, 2, 2};

    public static byte[] TIERED_MINT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 3, 4, 11, 4, 15, 2, 5, 17, 21, 7, 38, 60, 8, 98, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 1, 1, 1, 4, 2, 6, 12, 3, 0, 4, 6, 12, 5, 3, 3, 5, 6, 12, 3, 5, 3, 3, 1, 9, 0, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 11, 116, 105, 101, 114, 101, 100, 95, 109, 105, 110, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 3, 1, 9, 10, 0, 10, 1, 17, 0, 11, 0, 10, 2, 10, 3, 10, 4, 56, 0, 2};

    public static byte[] UNFREEZE_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 14, 7, 28, 68, 8, 96, 16, 0, 0, 0, 1, 0, 2, 0, 1, 0, 1, 3, 2, 1, 0, 2, 6, 12, 5, 0, 2, 6, 12, 3, 3, 6, 12, 3, 5, 15, 65, 99, 99, 111, 117, 110, 116, 70, 114, 101, 101, 122, 105, 110, 103, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 16, 117, 110, 102, 114, 101, 101, 122, 101, 95, 97, 99, 99, 111, 117, 110, 116, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 1, 7, 10, 0, 10, 1, 17, 1, 11, 0, 10, 2, 17, 0, 2};

    public static byte[] UNMINT_LBR_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 2, 2, 2, 4, 3, 6, 15, 5, 21, 16, 7, 37, 101, 8, -118, 1, 16, 0, 0, 0, 1, 1, 0, 0, 2, 0, 1, 0, 0, 3, 1, 2, 0, 0, 4, 3, 2, 0, 1, 6, 12, 1, 8, 0, 0, 2, 6, 8, 0, 3, 2, 6, 12, 3, 12, 76, 105, 98, 114, 97, 65, 99, 99, 111, 117, 110, 116, 18, 87, 105, 116, 104, 100, 114, 97, 119, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 27, 101, 120, 116, 114, 97, 99, 116, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 27, 114, 101, 115, 116, 111, 114, 101, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 12, 117, 110, 115, 116, 97, 112, 108, 101, 95, 108, 98, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 4, 1, 9, 11, 0, 17, 0, 12, 2, 14, 2, 10, 1, 17, 2, 11, 2, 17, 1, 2};

    public static byte[] UPDATE_DUAL_ATTESTATION_LIMIT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 10, 7, 24, 72, 8, 96, 16, 0, 0, 0, 1, 0, 2, 0, 1, 0, 1, 3, 0, 1, 0, 2, 6, 12, 3, 0, 3, 6, 12, 3, 3, 15, 68, 117, 97, 108, 65, 116, 116, 101, 115, 116, 97, 116, 105, 111, 110, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 20, 115, 101, 116, 95, 109, 105, 99, 114, 111, 108, 105, 98, 114, 97, 95, 108, 105, 109, 105, 116, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 1, 7, 10, 0, 10, 1, 17, 1, 11, 0, 10, 2, 17, 0, 2};

    public static byte[] UPDATE_EXCHANGE_RATE_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 7, 1, 0, 6, 2, 6, 4, 3, 10, 16, 4, 26, 2, 5, 28, 25, 7, 53, 100, 8, -103, 1, 16, 0, 0, 0, 1, 0, 2, 0, 0, 2, 0, 0, 3, 0, 1, 0, 2, 4, 2, 3, 0, 1, 5, 4, 3, 1, 1, 2, 6, 2, 3, 3, 1, 8, 0, 2, 6, 12, 3, 0, 2, 6, 12, 8, 0, 4, 6, 12, 3, 3, 3, 1, 9, 0, 12, 70, 105, 120, 101, 100, 80, 111, 105, 110, 116, 51, 50, 5, 76, 105, 98, 114, 97, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 20, 99, 114, 101, 97, 116, 101, 95, 102, 114, 111, 109, 95, 114, 97, 116, 105, 111, 110, 97, 108, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 24, 117, 112, 100, 97, 116, 101, 95, 108, 98, 114, 95, 101, 120, 99, 104, 97, 110, 103, 101, 95, 114, 97, 116, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 5, 1, 11, 10, 0, 10, 1, 17, 1, 10, 2, 10, 3, 17, 0, 12, 4, 11, 0, 11, 4, 56, 0, 2};

    public static byte[] UPDATE_LIBRA_VERSION_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 10, 7, 24, 52, 8, 76, 16, 0, 0, 0, 1, 0, 2, 0, 1, 0, 1, 3, 0, 1, 0, 2, 6, 12, 3, 0, 3, 6, 12, 3, 3, 12, 76, 105, 98, 114, 97, 86, 101, 114, 115, 105, 111, 110, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 3, 115, 101, 116, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 1, 7, 10, 0, 10, 1, 17, 1, 11, 0, 10, 2, 17, 0, 2};

    public static byte[] UPDATE_MINTING_ABILITY_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 2, 3, 2, 6, 4, 8, 2, 5, 10, 8, 7, 18, 29, 8, 47, 16, 0, 0, 0, 1, 0, 1, 1, 1, 0, 2, 2, 6, 12, 1, 0, 1, 9, 0, 5, 76, 105, 98, 114, 97, 22, 117, 112, 100, 97, 116, 101, 95, 109, 105, 110, 116, 105, 110, 103, 95, 97, 98, 105, 108, 105, 116, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 4, 11, 0, 10, 1, 56, 0, 2};

    interface DecodingHelper {
        public ScriptCall decode(Script script);
    }

    private static final java.util.Map<Bytes, DecodingHelper> SCRIPT_DECODER_MAP = initDecoderMap();

    private static java.util.Map<Bytes, DecodingHelper> initDecoderMap() {
        java.util.HashMap<Bytes, DecodingHelper> map = new java.util.HashMap<>();
        map.put(new Bytes(ADD_CURRENCY_TO_ACCOUNT_CODE), (DecodingHelper)((script) -> Helpers.decode_add_currency_to_account_script(script)));
        map.put(new Bytes(ADD_RECOVERY_ROTATION_CAPABILITY_CODE), (DecodingHelper)((script) -> Helpers.decode_add_recovery_rotation_capability_script(script)));
        map.put(new Bytes(ADD_TO_SCRIPT_ALLOW_LIST_CODE), (DecodingHelper)((script) -> Helpers.decode_add_to_script_allow_list_script(script)));
        map.put(new Bytes(ADD_VALIDATOR_AND_RECONFIGURE_CODE), (DecodingHelper)((script) -> Helpers.decode_add_validator_and_reconfigure_script(script)));
        map.put(new Bytes(BURN_CODE), (DecodingHelper)((script) -> Helpers.decode_burn_script(script)));
        map.put(new Bytes(BURN_TXN_FEES_CODE), (DecodingHelper)((script) -> Helpers.decode_burn_txn_fees_script(script)));
        map.put(new Bytes(CANCEL_BURN_CODE), (DecodingHelper)((script) -> Helpers.decode_cancel_burn_script(script)));
        map.put(new Bytes(CREATE_CHILD_VASP_ACCOUNT_CODE), (DecodingHelper)((script) -> Helpers.decode_create_child_vasp_account_script(script)));
        map.put(new Bytes(CREATE_DESIGNATED_DEALER_CODE), (DecodingHelper)((script) -> Helpers.decode_create_designated_dealer_script(script)));
        map.put(new Bytes(CREATE_PARENT_VASP_ACCOUNT_CODE), (DecodingHelper)((script) -> Helpers.decode_create_parent_vasp_account_script(script)));
        map.put(new Bytes(CREATE_RECOVERY_ADDRESS_CODE), (DecodingHelper)((script) -> Helpers.decode_create_recovery_address_script(script)));
        map.put(new Bytes(CREATE_VALIDATOR_ACCOUNT_CODE), (DecodingHelper)((script) -> Helpers.decode_create_validator_account_script(script)));
        map.put(new Bytes(CREATE_VALIDATOR_OPERATOR_ACCOUNT_CODE), (DecodingHelper)((script) -> Helpers.decode_create_validator_operator_account_script(script)));
        map.put(new Bytes(FREEZE_ACCOUNT_CODE), (DecodingHelper)((script) -> Helpers.decode_freeze_account_script(script)));
        map.put(new Bytes(MINT_LBR_CODE), (DecodingHelper)((script) -> Helpers.decode_mint_lbr_script(script)));
        map.put(new Bytes(PEER_TO_PEER_WITH_METADATA_CODE), (DecodingHelper)((script) -> Helpers.decode_peer_to_peer_with_metadata_script(script)));
        map.put(new Bytes(PREBURN_CODE), (DecodingHelper)((script) -> Helpers.decode_preburn_script(script)));
        map.put(new Bytes(PUBLISH_SHARED_ED25519_PUBLIC_KEY_CODE), (DecodingHelper)((script) -> Helpers.decode_publish_shared_ed25519_public_key_script(script)));
        map.put(new Bytes(REGISTER_VALIDATOR_CONFIG_CODE), (DecodingHelper)((script) -> Helpers.decode_register_validator_config_script(script)));
        map.put(new Bytes(REMOVE_VALIDATOR_AND_RECONFIGURE_CODE), (DecodingHelper)((script) -> Helpers.decode_remove_validator_and_reconfigure_script(script)));
        map.put(new Bytes(ROTATE_AUTHENTICATION_KEY_CODE), (DecodingHelper)((script) -> Helpers.decode_rotate_authentication_key_script(script)));
        map.put(new Bytes(ROTATE_AUTHENTICATION_KEY_WITH_NONCE_CODE), (DecodingHelper)((script) -> Helpers.decode_rotate_authentication_key_with_nonce_script(script)));
        map.put(new Bytes(ROTATE_AUTHENTICATION_KEY_WITH_NONCE_ADMIN_CODE), (DecodingHelper)((script) -> Helpers.decode_rotate_authentication_key_with_nonce_admin_script(script)));
        map.put(new Bytes(ROTATE_AUTHENTICATION_KEY_WITH_RECOVERY_ADDRESS_CODE), (DecodingHelper)((script) -> Helpers.decode_rotate_authentication_key_with_recovery_address_script(script)));
        map.put(new Bytes(ROTATE_DUAL_ATTESTATION_INFO_CODE), (DecodingHelper)((script) -> Helpers.decode_rotate_dual_attestation_info_script(script)));
        map.put(new Bytes(ROTATE_SHARED_ED25519_PUBLIC_KEY_CODE), (DecodingHelper)((script) -> Helpers.decode_rotate_shared_ed25519_public_key_script(script)));
        map.put(new Bytes(SET_VALIDATOR_CONFIG_AND_RECONFIGURE_CODE), (DecodingHelper)((script) -> Helpers.decode_set_validator_config_and_reconfigure_script(script)));
        map.put(new Bytes(SET_VALIDATOR_OPERATOR_CODE), (DecodingHelper)((script) -> Helpers.decode_set_validator_operator_script(script)));
        map.put(new Bytes(SET_VALIDATOR_OPERATOR_WITH_NONCE_ADMIN_CODE), (DecodingHelper)((script) -> Helpers.decode_set_validator_operator_with_nonce_admin_script(script)));
        map.put(new Bytes(TIERED_MINT_CODE), (DecodingHelper)((script) -> Helpers.decode_tiered_mint_script(script)));
        map.put(new Bytes(UNFREEZE_ACCOUNT_CODE), (DecodingHelper)((script) -> Helpers.decode_unfreeze_account_script(script)));
        map.put(new Bytes(UNMINT_LBR_CODE), (DecodingHelper)((script) -> Helpers.decode_unmint_lbr_script(script)));
        map.put(new Bytes(UPDATE_DUAL_ATTESTATION_LIMIT_CODE), (DecodingHelper)((script) -> Helpers.decode_update_dual_attestation_limit_script(script)));
        map.put(new Bytes(UPDATE_EXCHANGE_RATE_CODE), (DecodingHelper)((script) -> Helpers.decode_update_exchange_rate_script(script)));
        map.put(new Bytes(UPDATE_LIBRA_VERSION_CODE), (DecodingHelper)((script) -> Helpers.decode_update_libra_version_script(script)));
        map.put(new Bytes(UPDATE_MINTING_ABILITY_CODE), (DecodingHelper)((script) -> Helpers.decode_update_minting_ability_script(script)));
        return map;
    }

    private static Boolean decode_bool_argument(TransactionArgument arg) {
        if (!(arg instanceof TransactionArgument.Bool)) {
            throw new IllegalArgumentException("Was expecting a Bool argument");
        }
        return ((TransactionArgument.Bool) arg).value;
    }


    private static @Unsigned Long decode_u64_argument(TransactionArgument arg) {
        if (!(arg instanceof TransactionArgument.U64)) {
            throw new IllegalArgumentException("Was expecting a U64 argument");
        }
        return ((TransactionArgument.U64) arg).value;
    }


    private static AccountAddress decode_address_argument(TransactionArgument arg) {
        if (!(arg instanceof TransactionArgument.Address)) {
            throw new IllegalArgumentException("Was expecting a Address argument");
        }
        return ((TransactionArgument.Address) arg).value;
    }


    private static Bytes decode_u8vector_argument(TransactionArgument arg) {
        if (!(arg instanceof TransactionArgument.U8Vector)) {
            throw new IllegalArgumentException("Was expecting a U8Vector argument");
        }
        return ((TransactionArgument.U8Vector) arg).value;
    }


}

