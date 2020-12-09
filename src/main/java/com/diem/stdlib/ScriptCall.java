package com.diem.stdlib;


public abstract class ScriptCall {

    /**
     * <p><b>Summary</b>
     * Adds a zero {@code Currency} balance to the sending {@code account}. This will enable {@code account} to
     * send, receive, and hold {@code Diem::Diem<Currency>} coins. This transaction can be
     * successfully sent by any account that is allowed to hold balances
     * (e.g., VASP, Designated Dealer).
     *
     * <p><b>Technical Description</b>
     * After the successful execution of this transaction the sending account will have a
     * {@code DiemAccount::Balance<Currency>} resource with zero balance published under it. Only
     * accounts that can hold balances can send this transaction, the sending account cannot
     * already have a {@code DiemAccount::Balance<Currency>} published under it.
     *
     * <p><b>Parameters</b>
     * | Name       | Type      | Description                                                                                                                                         |
     * | ------     | ------    | -------------                                                                                                                                       |
     * | {@code Currency} | Type      | The Move type for the {@code Currency} being added to the sending account of the transaction. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code account}  | {@code &signer} | The signer of the sending account of the transaction.                                                                                               |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category              | Error Reason                             | Description                                                                |
     * | ----------------            | --------------                           | -------------                                                              |
     * | {@code Errors::NOT_PUBLISHED}     | {@code Diem::ECURRENCY_INFO}                  | The {@code Currency} is not a registered currency on-chain.                      |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code DiemAccount::EROLE_CANT_STORE_BALANCE} | The sending {@code account}'s role does not permit balances.                     |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code DiemAccount::EADD_EXISTING_CURRENCY}   | A balance for {@code Currency} is already published under the sending {@code account}. |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_child_vasp_account}</li></ul>
     * <ul><li>{@code Script::create_parent_vasp_account}</li></ul>
     * <ul><li>{@code Script::peer_to_peer_with_metadata}</li></ul>
     */
    public static final class AddCurrencyToAccount extends ScriptCall {
        public final com.diem.types.TypeTag currency;

        public AddCurrencyToAccount(com.diem.types.TypeTag currency) {
            java.util.Objects.requireNonNull(currency, "currency must not be null");
            this.currency = currency;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            AddCurrencyToAccount other = (AddCurrencyToAccount) obj;
            if (!java.util.Objects.equals(this.currency, other.currency)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.currency != null ? this.currency.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.TypeTag currency;

            public AddCurrencyToAccount build() {
                return new AddCurrencyToAccount(
                    currency
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Stores the sending accounts ability to rotate its authentication key with a designated recovery
     * account. Both the sending and recovery accounts need to belong to the same VASP and
     * both be VASP accounts. After this transaction both the sending account and the
     * specified recovery account can rotate the sender account's authentication key.
     *
     * <p><b>Technical Description</b>
     * Adds the {@code DiemAccount::KeyRotationCapability} for the sending account
     * ({@code to_recover_account}) to the {@code RecoveryAddress::RecoveryAddress} resource under
     * {@code recovery_address}. After this transaction has been executed successfully the account at
     * {@code recovery_address} and the {@code to_recover_account} may rotate the authentication key of
     * {@code to_recover_account} (the sender of this transaction).
     *
     * The sending account of this transaction ({@code to_recover_account}) must not have previously given away its unique key
     * rotation capability, and must be a VASP account. The account at {@code recovery_address}
     * must also be a VASP account belonging to the same VASP as the {@code to_recover_account}.
     * Additionally the account at {@code recovery_address} must have already initialized itself as
     * a recovery account address using the {@code Script::create_recovery_address} transaction script.
     *
     * The sending account's ({@code to_recover_account}) key rotation capability is
     * removed in this transaction and stored in the {@code RecoveryAddress::RecoveryAddress}
     * resource stored under the account at {@code recovery_address}.
     *
     * <p><b>Parameters</b>
     * | Name                 | Type      | Description                                                                                                |
     * | ------               | ------    | -------------                                                                                              |
     * | {@code to_recover_account} | {@code &signer} | The signer reference of the sending account of this transaction.                                           |
     * | {@code recovery_address}   | {@code address} | The account address where the {@code to_recover_account}'s {@code DiemAccount::KeyRotationCapability} will be stored. |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                               | Description                                                                                     |
     * | ----------------           | --------------                                             | -------------                                                                                   |
     * | {@code Errors::INVALID_STATE}    | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code to_recover_account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}. |
     * | {@code Errors::NOT_PUBLISHED}    | {@code RecoveryAddress::ERECOVERY_ADDRESS}                       | {@code recovery_address} does not have a {@code RecoveryAddress} resource published under it.               |
     * | {@code Errors::INVALID_ARGUMENT} | {@code RecoveryAddress::EINVALID_KEY_ROTATION_DELEGATION}        | {@code to_recover_account} and {@code recovery_address} do not belong to the same VASP.                     |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_recovery_address}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_recovery_address}</li></ul>
     */
    public static final class AddRecoveryRotationCapability extends ScriptCall {
        public final com.diem.types.AccountAddress recovery_address;

        public AddRecoveryRotationCapability(com.diem.types.AccountAddress recovery_address) {
            java.util.Objects.requireNonNull(recovery_address, "recovery_address must not be null");
            this.recovery_address = recovery_address;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            AddRecoveryRotationCapability other = (AddRecoveryRotationCapability) obj;
            if (!java.util.Objects.equals(this.recovery_address, other.recovery_address)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.recovery_address != null ? this.recovery_address.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.AccountAddress recovery_address;

            public AddRecoveryRotationCapability build() {
                return new AddRecoveryRotationCapability(
                    recovery_address
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Adds a script hash to the transaction allowlist. This transaction
     * can only be sent by the Diem Root account. Scripts with this hash can be
     * sent afterward the successful execution of this script.
     *
     * <p><b>Technical Description</b>
     *
     * The sending account ({@code dr_account}) must be the Diem Root account. The script allow
     * list must not already hold the script {@code hash} being added. The {@code sliding_nonce} must be
     * a valid nonce for the Diem Root account. After this transaction has executed
     * successfully a reconfiguration will be initiated, and the on-chain config
     * {@code DiemTransactionPublishingOption::DiemTransactionPublishingOption}'s
     * {@code script_allow_list} field will contain the new script {@code hash} and transactions
     * with this {@code hash} can be successfully sent to the network.
     *
     * <p><b>Parameters</b>
     * | Name            | Type         | Description                                                                                     |
     * | ------          | ------       | -------------                                                                                   |
     * | {@code dr_account}    | {@code &signer}    | The signer reference of the sending account of this transaction. Must be the Diem Root signer. |
     * | {@code hash}          | {@code vector<u8>} | The hash of the script to be added to the script allowlist.                                     |
     * | {@code sliding_nonce} | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                      |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                                           | Description                                                                                |
     * | ----------------           | --------------                                                         | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                                         | A {@code SlidingNonce} resource is not published under {@code dr_account}.                             |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                                         | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                                         | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}                                | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::EDIEM_ROOT}                                           | The sending account is not the Diem Root account.                                         |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::EDIEM_ROOT}                                                   | The sending account is not the Diem Root account.                                         |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemTransactionPublishingOption::EINVALID_SCRIPT_HASH}               | The script {@code hash} is an invalid length.                                                    |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemTransactionPublishingOption::EALLOWLIST_ALREADY_CONTAINS_SCRIPT} | The on-chain allowlist already contains the script {@code hash}.                                 |
     */
    public static final class AddToScriptAllowList extends ScriptCall {
        public final com.novi.serde.Bytes hash;
        public final @com.novi.serde.Unsigned Long sliding_nonce;

        public AddToScriptAllowList(com.novi.serde.Bytes hash, @com.novi.serde.Unsigned Long sliding_nonce) {
            java.util.Objects.requireNonNull(hash, "hash must not be null");
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            this.hash = hash;
            this.sliding_nonce = sliding_nonce;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            AddToScriptAllowList other = (AddToScriptAllowList) obj;
            if (!java.util.Objects.equals(this.hash, other.hash)) { return false; }
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.hash != null ? this.hash.hashCode() : 0);
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.novi.serde.Bytes hash;
            public @com.novi.serde.Unsigned Long sliding_nonce;

            public AddToScriptAllowList build() {
                return new AddToScriptAllowList(
                    hash,
                    sliding_nonce
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Adds a validator account to the validator set, and triggers a
     * reconfiguration of the system to admit the account to the validator set for the system. This
     * transaction can only be successfully called by the Diem Root account.
     *
     * <p><b>Technical Description</b>
     * This script adds the account at {@code validator_address} to the validator set.
     * This transaction emits a {@code DiemConfig::NewEpochEvent} event and triggers a
     * reconfiguration. Once the reconfiguration triggered by this script's
     * execution has been performed, the account at the {@code validator_address} is
     * considered to be a validator in the network.
     *
     * This transaction script will fail if the {@code validator_address} address is already in the validator set
     * or does not have a {@code ValidatorConfig::ValidatorConfig} resource already published under it.
     *
     * <p><b>Parameters</b>
     * | Name                | Type         | Description                                                                                                                        |
     * | ------              | ------       | -------------                                                                                                                      |
     * | {@code dr_account}        | {@code &signer}    | The signer reference of the sending account of this transaction. Must be the Diem Root signer.                                    |
     * | {@code sliding_nonce}     | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                                         |
     * | {@code validator_name}    | {@code vector<u8>} | ASCII-encoded human name for the validator. Must match the human name in the {@code ValidatorConfig::ValidatorConfig} for the validator. |
     * | {@code validator_address} | {@code address}    | The validator account address to be added to the validator set.                                                                    |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                  | Description                                                                                                                               |
     * | ----------------           | --------------                                | -------------                                                                                                                             |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                | A {@code SlidingNonce} resource is not published under {@code dr_account}.                                                                            |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not.                                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                | The {@code sliding_nonce} is too far in the future.                                                                                             |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}       | The {@code sliding_nonce} has been previously recorded.                                                                                         |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::EDIEM_ROOT}                  | The sending account is not the Diem Root account.                                                                                        |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::EDIEM_ROOT}                          | The sending account is not the Diem Root account.                                                                                        |
     * | 0                          | 0                                             | The provided {@code validator_name} does not match the already-recorded human name for the validator.                                           |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemSystem::EINVALID_PROSPECTIVE_VALIDATOR} | The validator to be added does not have a {@code ValidatorConfig::ValidatorConfig} resource published under it, or its {@code config} field is empty. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemSystem::EALREADY_A_VALIDATOR}           | The {@code validator_address} account is already a registered validator.                                                                        |
     * | {@code Errors::INVALID_STATE}    | {@code DiemConfig::EINVALID_BLOCK_TIME}            | An invalid time value was encountered in reconfiguration. Unlikely to occur.                                                              |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     */
    public static final class AddValidatorAndReconfigure extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.novi.serde.Bytes validator_name;
        public final com.diem.types.AccountAddress validator_address;

        public AddValidatorAndReconfigure(@com.novi.serde.Unsigned Long sliding_nonce, com.novi.serde.Bytes validator_name, com.diem.types.AccountAddress validator_address) {
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(validator_name, "validator_name must not be null");
            java.util.Objects.requireNonNull(validator_address, "validator_address must not be null");
            this.sliding_nonce = sliding_nonce;
            this.validator_name = validator_name;
            this.validator_address = validator_address;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            AddValidatorAndReconfigure other = (AddValidatorAndReconfigure) obj;
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.validator_name, other.validator_name)) { return false; }
            if (!java.util.Objects.equals(this.validator_address, other.validator_address)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.validator_name != null ? this.validator_name.hashCode() : 0);
            value = 31 * value + (this.validator_address != null ? this.validator_address.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.novi.serde.Bytes validator_name;
            public com.diem.types.AccountAddress validator_address;

            public AddValidatorAndReconfigure build() {
                return new AddValidatorAndReconfigure(
                    sliding_nonce,
                    validator_name,
                    validator_address
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Burns all coins held in the preburn resource at the specified
     * preburn address and removes them from the system. The sending account must
     * be the Treasury Compliance account.
     * The account that holds the preburn resource will normally be a Designated
     * Dealer, but there are no enforced requirements that it be one.
     *
     * <p><b>Technical Description</b>
     * This transaction permanently destroys all the coins of {@code Token} type
     * stored in the {@code Diem::Preburn<Token>} resource published under the
     * {@code preburn_address} account address.
     *
     * This transaction will only succeed if the sending {@code account} has a
     * {@code Diem::BurnCapability<Token>}, and a {@code Diem::Preburn<Token>} resource
     * exists under {@code preburn_address}, with a non-zero {@code to_burn} field. After the successful execution
     * of this transaction the {@code total_value} field in the
     * {@code Diem::CurrencyInfo<Token>} resource published under {@code 0xA550C18} will be
     * decremented by the value of the {@code to_burn} field of the preburn resource
     * under {@code preburn_address} immediately before this transaction, and the
     * {@code to_burn} field of the preburn resource will have a zero value.
     *
     * <p><b>Events</b>
     * The successful execution of this transaction will emit a {@code Diem::BurnEvent} on the event handle
     * held in the {@code Diem::CurrencyInfo<Token>} resource's {@code burn_events} published under
     * {@code 0xA550C18}.
     *
     * <p><b>Parameters</b>
     * | Name              | Type      | Description                                                                                                                  |
     * | ------            | ------    | -------------                                                                                                                |
     * | {@code Token}           | Type      | The Move type for the {@code Token} currency being burned. {@code Token} must be an already-registered currency on-chain.                |
     * | {@code tc_account}      | {@code &signer} | The signer reference of the sending account of this transaction, must have a burn capability for {@code Token} published under it. |
     * | {@code sliding_nonce}   | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                                   |
     * | {@code preburn_address} | {@code address} | The address where the coins to-be-burned are currently held.                                                                 |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category                | Error Reason                            | Description                                                                                           |
     * | ----------------              | --------------                          | -------------                                                                                         |
     * | {@code Errors::NOT_PUBLISHED}       | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code account}.                                           |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not.            |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                                         |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                                     |
     * | {@code Errors::REQUIRES_CAPABILITY} | {@code Diem::EBURN_CAPABILITY}               | The sending {@code account} does not have a {@code Diem::BurnCapability<Token>} published under it.              |
     * | {@code Errors::NOT_PUBLISHED}       | {@code Diem::EPREBURN}                       | The account at {@code preburn_address} does not have a {@code Diem::Preburn<Token>} resource published under it. |
     * | {@code Errors::INVALID_STATE}       | {@code Diem::EPREBURN_EMPTY}                 | The {@code Diem::Preburn<Token>} resource is empty (has a value of 0).                                     |
     * | {@code Errors::NOT_PUBLISHED}       | {@code Diem::ECURRENCY_INFO}                 | The specified {@code Token} is not a registered currency on-chain.                                          |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::burn_txn_fees}</li></ul>
     * <ul><li>{@code Script::cancel_burn}</li></ul>
     * <ul><li>{@code Script::preburn}</li></ul>
     */
    public static final class Burn extends ScriptCall {
        public final com.diem.types.TypeTag token;
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.diem.types.AccountAddress preburn_address;

        public Burn(com.diem.types.TypeTag token, @com.novi.serde.Unsigned Long sliding_nonce, com.diem.types.AccountAddress preburn_address) {
            java.util.Objects.requireNonNull(token, "token must not be null");
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(preburn_address, "preburn_address must not be null");
            this.token = token;
            this.sliding_nonce = sliding_nonce;
            this.preburn_address = preburn_address;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Burn other = (Burn) obj;
            if (!java.util.Objects.equals(this.token, other.token)) { return false; }
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.preburn_address, other.preburn_address)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.token != null ? this.token.hashCode() : 0);
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.preburn_address != null ? this.preburn_address.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.TypeTag token;
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.diem.types.AccountAddress preburn_address;

            public Burn build() {
                return new Burn(
                    token,
                    sliding_nonce,
                    preburn_address
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Burns the transaction fees collected in the {@code CoinType} currency so that the
     * Diem association may reclaim the backing coins off-chain. May only be sent
     * by the Treasury Compliance account.
     *
     * <p><b>Technical Description</b>
     * Burns the transaction fees collected in {@code CoinType} so that the
     * association may reclaim the backing coins. Once this transaction has executed
     * successfully all transaction fees that will have been collected in
     * {@code CoinType} since the last time this script was called with that specific
     * currency. Both {@code balance} and {@code preburn} fields in the
     * {@code TransactionFee::TransactionFee<CoinType>} resource published under the {@code 0xB1E55ED}
     * account address will have a value of 0 after the successful execution of this script.
     *
     * <p><b>Events</b>
     * The successful execution of this transaction will emit a {@code Diem::BurnEvent} on the event handle
     * held in the {@code Diem::CurrencyInfo<CoinType>} resource's {@code burn_events} published under
     * {@code 0xA550C18}.
     *
     * <p><b>Parameters</b>
     * | Name         | Type      | Description                                                                                                                                         |
     * | ------       | ------    | -------------                                                                                                                                       |
     * | {@code CoinType}   | Type      | The Move type for the {@code CoinType} being added to the sending account of the transaction. {@code CoinType} must be an already-registered currency on-chain. |
     * | {@code tc_account} | {@code &signer} | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account.                                           |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                          | Description                                                 |
     * | ----------------           | --------------                        | -------------                                               |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE} | The sending account is not the Treasury Compliance account. |
     * | {@code Errors::NOT_PUBLISHED}    | {@code TransactionFee::ETRANSACTION_FEE}    | {@code CoinType} is not an accepted transaction fee currency.     |
     * | {@code Errors::INVALID_ARGUMENT} | {@code Diem::ECOIN}                        | The collected fees in {@code CoinType} are zero.                  |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::burn}</li></ul>
     * <ul><li>{@code Script::cancel_burn}</li></ul>
     */
    public static final class BurnTxnFees extends ScriptCall {
        public final com.diem.types.TypeTag coin_type;

        public BurnTxnFees(com.diem.types.TypeTag coin_type) {
            java.util.Objects.requireNonNull(coin_type, "coin_type must not be null");
            this.coin_type = coin_type;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            BurnTxnFees other = (BurnTxnFees) obj;
            if (!java.util.Objects.equals(this.coin_type, other.coin_type)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.coin_type != null ? this.coin_type.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.TypeTag coin_type;

            public BurnTxnFees build() {
                return new BurnTxnFees(
                    coin_type
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Cancels and returns all coins held in the preburn area under
     * {@code preburn_address} and returns the funds to the {@code preburn_address}'s balance.
     * Can only be successfully sent by an account with Treasury Compliance role.
     *
     * <p><b>Technical Description</b>
     * Cancels and returns all coins held in the {@code Diem::Preburn<Token>} resource under the {@code preburn_address} and
     * return the funds to the {@code preburn_address} account's {@code DiemAccount::Balance<Token>}.
     * The transaction must be sent by an {@code account} with a {@code Diem::BurnCapability<Token>}
     * resource published under it. The account at {@code preburn_address} must have a
     * {@code Diem::Preburn<Token>} resource published under it, and its value must be nonzero. The transaction removes
     * the entire balance held in the {@code Diem::Preburn<Token>} resource, and returns it back to the account's
     * {@code DiemAccount::Balance<Token>} under {@code preburn_address}. Due to this, the account at
     * {@code preburn_address} must already have a balance in the {@code Token} currency published
     * before this script is called otherwise the transaction will fail.
     *
     * <p><b>Events</b>
     * The successful execution of this transaction will emit:
     * <ul><li>A {@code Diem::CancelBurnEvent} on the event handle held in the {@code Diem::CurrencyInfo<Token>}</li></ul>
     * resource's {@code burn_events} published under {@code 0xA550C18}.
     * <ul><li>A {@code DiemAccount::ReceivedPaymentEvent} on the {@code preburn_address}'s</li></ul>
     * {@code DiemAccount::DiemAccount} {@code received_events} event handle with both the {@code payer} and {@code payee}
     * being {@code preburn_address}.
     *
     * <p><b>Parameters</b>
     * | Name              | Type      | Description                                                                                                                          |
     * | ------            | ------    | -------------                                                                                                                        |
     * | {@code Token}           | Type      | The Move type for the {@code Token} currenty that burning is being cancelled for. {@code Token} must be an already-registered currency on-chain. |
     * | {@code account}         | {@code &signer} | The signer reference of the sending account of this transaction, must have a burn capability for {@code Token} published under it.         |
     * | {@code preburn_address} | {@code address} | The address where the coins to-be-burned are currently held.                                                                         |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category                | Error Reason                                     | Description                                                                                           |
     * | ----------------              | --------------                                   | -------------                                                                                         |
     * | {@code Errors::REQUIRES_CAPABILITY} | {@code Diem::EBURN_CAPABILITY}                        | The sending {@code account} does not have a {@code Diem::BurnCapability<Token>} published under it.              |
     * | {@code Errors::NOT_PUBLISHED}       | {@code Diem::EPREBURN}                                | The account at {@code preburn_address} does not have a {@code Diem::Preburn<Token>} resource published under it. |
     * | {@code Errors::NOT_PUBLISHED}       | {@code Diem::ECURRENCY_INFO}                          | The specified {@code Token} is not a registered currency on-chain.                                          |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code DiemAccount::ECOIN_DEPOSIT_IS_ZERO}            | The value held in the preburn resource was zero.                                                      |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code DiemAccount::EPAYEE_CANT_ACCEPT_CURRENCY_TYPE} | The account at {@code preburn_address} doesn't have a balance resource for {@code Token}.                         |
     * | {@code Errors::LIMIT_EXCEEDED}      | {@code DiemAccount::EDEPOSIT_EXCEEDS_LIMITS}          | The depositing of the funds held in the prebun area would exceed the {@code account}'s account limits.      |
     * | {@code Errors::INVALID_STATE}       | {@code DualAttestation::EPAYEE_COMPLIANCE_KEY_NOT_SET} | The {@code account} does not have a compliance key set on it but dual attestion checking was performed.     |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::burn_txn_fees}</li></ul>
     * <ul><li>{@code Script::burn}</li></ul>
     * <ul><li>{@code Script::preburn}</li></ul>
     */
    public static final class CancelBurn extends ScriptCall {
        public final com.diem.types.TypeTag token;
        public final com.diem.types.AccountAddress preburn_address;

        public CancelBurn(com.diem.types.TypeTag token, com.diem.types.AccountAddress preburn_address) {
            java.util.Objects.requireNonNull(token, "token must not be null");
            java.util.Objects.requireNonNull(preburn_address, "preburn_address must not be null");
            this.token = token;
            this.preburn_address = preburn_address;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            CancelBurn other = (CancelBurn) obj;
            if (!java.util.Objects.equals(this.token, other.token)) { return false; }
            if (!java.util.Objects.equals(this.preburn_address, other.preburn_address)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.token != null ? this.token.hashCode() : 0);
            value = 31 * value + (this.preburn_address != null ? this.preburn_address.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.TypeTag token;
            public com.diem.types.AccountAddress preburn_address;

            public CancelBurn build() {
                return new CancelBurn(
                    token,
                    preburn_address
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Creates a Child VASP account with its parent being the sending account of the transaction.
     * The sender of the transaction must be a Parent VASP account.
     *
     * <p><b>Technical Description</b>
     * Creates a {@code ChildVASP} account for the sender {@code parent_vasp} at {@code child_address} with a balance of
     * {@code child_initial_balance} in {@code CoinType} and an initial authentication key of
     * {@code auth_key_prefix | child_address}.
     *
     * If {@code add_all_currencies} is true, the child address will have a zero balance in all available
     * currencies in the system.
     *
     * The new account will be a child account of the transaction sender, which must be a
     * Parent VASP account. The child account will be recorded against the limit of
     * child accounts of the creating Parent VASP account.
     *
     * <p><b>Events</b>
     * Successful execution with a {@code child_initial_balance} greater than zero will emit:
     * <ul><li>A {@code DiemAccount::SentPaymentEvent} with the {@code payer} field being the Parent VASP's address,</li></ul>
     * and payee field being {@code child_address}. This is emitted on the Parent VASP's
     * {@code DiemAccount::DiemAccount} {@code sent_events} handle.
     * <ul><li>A {@code DiemAccount::ReceivedPaymentEvent} with the  {@code payer} field being the Parent VASP's address,</li></ul>
     * and payee field being {@code child_address}. This is emitted on the new Child VASPS's
     * {@code DiemAccount::DiemAccount} {@code received_events} handle.
     *
     * <p><b>Parameters</b>
     * | Name                    | Type         | Description                                                                                                                                 |
     * | ------                  | ------       | -------------                                                                                                                               |
     * | {@code CoinType}              | Type         | The Move type for the {@code CoinType} that the child account should be created with. {@code CoinType} must be an already-registered currency on-chain. |
     * | {@code parent_vasp}           | {@code &signer}    | The signer reference of the sending account. Must be a Parent VASP account.                                                                 |
     * | {@code child_address}         | {@code address}    | Address of the to-be-created Child VASP account.                                                                                            |
     * | {@code auth_key_prefix}       | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account.                                                    |
     * | {@code add_all_currencies}    | {@code bool}       | Whether to publish balance resources for all known currencies when the account is created.                                                  |
     * | {@code child_initial_balance} | {@code u64}        | The initial balance in {@code CoinType} to give the child account when it's created.                                                              |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category              | Error Reason                                             | Description                                                                              |
     * | ----------------            | --------------                                           | -------------                                                                            |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY}            | The {@code auth_key_prefix} was not of length 32.                                              |
     * | {@code Errors::REQUIRES_ROLE}     | {@code Roles::EPARENT_VASP}                                    | The sending account wasn't a Parent VASP account.                                        |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code Roles::EROLE_ID}                                        | The {@code child_address} address is already taken.                                            |
     * | {@code Errors::LIMIT_EXCEEDED}    | {@code VASP::ETOO_MANY_CHILDREN}                               | The sending account has reached the maximum number of allowed child accounts.            |
     * | {@code Errors::NOT_PUBLISHED}     | {@code Diem::ECURRENCY_INFO}                                  | The {@code CoinType} is not a registered currency on-chain.                                    |
     * | {@code Errors::INVALID_STATE}     | {@code DiemAccount::EWITHDRAWAL_CAPABILITY_ALREADY_EXTRACTED} | The withdrawal capability for the sending account has already been extracted.            |
     * | {@code Errors::NOT_PUBLISHED}     | {@code DiemAccount::EPAYER_DOESNT_HOLD_CURRENCY}              | The sending account doesn't have a balance in {@code CoinType}.                                |
     * | {@code Errors::LIMIT_EXCEEDED}    | {@code DiemAccount::EINSUFFICIENT_BALANCE}                    | The sending account doesn't have at least {@code child_initial_balance} of {@code CoinType} balance. |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code DiemAccount::ECANNOT_CREATE_AT_VM_RESERVED}            | The {@code child_address} is the reserved address 0x0.                                         |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_parent_vasp_account}</li></ul>
     * <ul><li>{@code Script::add_currency_to_account}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key}</li></ul>
     * <ul><li>{@code Script::add_recovery_rotation_capability}</li></ul>
     * <ul><li>{@code Script::create_recovery_address}</li></ul>
     */
    public static final class CreateChildVaspAccount extends ScriptCall {
        public final com.diem.types.TypeTag coin_type;
        public final com.diem.types.AccountAddress child_address;
        public final com.novi.serde.Bytes auth_key_prefix;
        public final Boolean add_all_currencies;
        public final @com.novi.serde.Unsigned Long child_initial_balance;

        public CreateChildVaspAccount(com.diem.types.TypeTag coin_type, com.diem.types.AccountAddress child_address, com.novi.serde.Bytes auth_key_prefix, Boolean add_all_currencies, @com.novi.serde.Unsigned Long child_initial_balance) {
            java.util.Objects.requireNonNull(coin_type, "coin_type must not be null");
            java.util.Objects.requireNonNull(child_address, "child_address must not be null");
            java.util.Objects.requireNonNull(auth_key_prefix, "auth_key_prefix must not be null");
            java.util.Objects.requireNonNull(add_all_currencies, "add_all_currencies must not be null");
            java.util.Objects.requireNonNull(child_initial_balance, "child_initial_balance must not be null");
            this.coin_type = coin_type;
            this.child_address = child_address;
            this.auth_key_prefix = auth_key_prefix;
            this.add_all_currencies = add_all_currencies;
            this.child_initial_balance = child_initial_balance;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            CreateChildVaspAccount other = (CreateChildVaspAccount) obj;
            if (!java.util.Objects.equals(this.coin_type, other.coin_type)) { return false; }
            if (!java.util.Objects.equals(this.child_address, other.child_address)) { return false; }
            if (!java.util.Objects.equals(this.auth_key_prefix, other.auth_key_prefix)) { return false; }
            if (!java.util.Objects.equals(this.add_all_currencies, other.add_all_currencies)) { return false; }
            if (!java.util.Objects.equals(this.child_initial_balance, other.child_initial_balance)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.coin_type != null ? this.coin_type.hashCode() : 0);
            value = 31 * value + (this.child_address != null ? this.child_address.hashCode() : 0);
            value = 31 * value + (this.auth_key_prefix != null ? this.auth_key_prefix.hashCode() : 0);
            value = 31 * value + (this.add_all_currencies != null ? this.add_all_currencies.hashCode() : 0);
            value = 31 * value + (this.child_initial_balance != null ? this.child_initial_balance.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.TypeTag coin_type;
            public com.diem.types.AccountAddress child_address;
            public com.novi.serde.Bytes auth_key_prefix;
            public Boolean add_all_currencies;
            public @com.novi.serde.Unsigned Long child_initial_balance;

            public CreateChildVaspAccount build() {
                return new CreateChildVaspAccount(
                    coin_type,
                    child_address,
                    auth_key_prefix,
                    add_all_currencies,
                    child_initial_balance
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Creates a Designated Dealer account with the provided information, and initializes it with
     * default mint tiers. The transaction can only be sent by the Treasury Compliance account.
     *
     * <p><b>Technical Description</b>
     * Creates an account with the Designated Dealer role at {@code addr} with authentication key
     * {@code auth_key_prefix} | {@code addr} and a 0 balance of type {@code Currency}. If {@code add_all_currencies} is true,
     * 0 balances for all available currencies in the system will also be added. This can only be
     * invoked by an account with the TreasuryCompliance role.
     *
     * At the time of creation the account is also initialized with default mint tiers of (500_000,
     * 5000_000, 50_000_000, 500_000_000), and preburn areas for each currency that is added to the
     * account.
     *
     * <p><b>Parameters</b>
     * | Name                 | Type         | Description                                                                                                                                         |
     * | ------               | ------       | -------------                                                                                                                                       |
     * | {@code Currency}           | Type         | The Move type for the {@code Currency} that the Designated Dealer should be initialized with. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code tc_account}         | {@code &signer}    | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account.                                           |
     * | {@code sliding_nonce}      | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                                                          |
     * | {@code addr}               | {@code address}    | Address of the to-be-created Designated Dealer account.                                                                                             |
     * | {@code auth_key_prefix}    | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account.                                                            |
     * | {@code human_name}         | {@code vector<u8>} | ASCII-encoded human name for the Designated Dealer.                                                                                                 |
     * | {@code add_all_currencies} | {@code bool}       | Whether to publish preburn, balance, and tier info resources for all known (SCS) currencies or just {@code Currency} when the account is created.         |
     *

     * <p><b>Common Abort Conditions</b>
     * | Error Category              | Error Reason                            | Description                                                                                |
     * | ----------------            | --------------                          | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}     | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code tc_account}.                             |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS}  | {@code CoreAddresses::ETREASURY_COMPLIANCE}   | The sending account is not the Treasury Compliance account.                                |
     * | {@code Errors::REQUIRES_ROLE}     | {@code Roles::ETREASURY_COMPLIANCE}           | The sending account is not the Treasury Compliance account.                                |
     * | {@code Errors::NOT_PUBLISHED}     | {@code Diem::ECURRENCY_INFO}                 | The {@code Currency} is not a registered currency on-chain.                                      |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code Roles::EROLE_ID}                       | The {@code addr} address is already taken.                                                       |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::tiered_mint}</li></ul>
     * <ul><li>{@code Script::peer_to_peer_with_metadata}</li></ul>
     * <ul><li>{@code Script::rotate_dual_attestation_info}</li></ul>
     */
    public static final class CreateDesignatedDealer extends ScriptCall {
        public final com.diem.types.TypeTag currency;
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.diem.types.AccountAddress addr;
        public final com.novi.serde.Bytes auth_key_prefix;
        public final com.novi.serde.Bytes human_name;
        public final Boolean add_all_currencies;

        public CreateDesignatedDealer(com.diem.types.TypeTag currency, @com.novi.serde.Unsigned Long sliding_nonce, com.diem.types.AccountAddress addr, com.novi.serde.Bytes auth_key_prefix, com.novi.serde.Bytes human_name, Boolean add_all_currencies) {
            java.util.Objects.requireNonNull(currency, "currency must not be null");
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(addr, "addr must not be null");
            java.util.Objects.requireNonNull(auth_key_prefix, "auth_key_prefix must not be null");
            java.util.Objects.requireNonNull(human_name, "human_name must not be null");
            java.util.Objects.requireNonNull(add_all_currencies, "add_all_currencies must not be null");
            this.currency = currency;
            this.sliding_nonce = sliding_nonce;
            this.addr = addr;
            this.auth_key_prefix = auth_key_prefix;
            this.human_name = human_name;
            this.add_all_currencies = add_all_currencies;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            CreateDesignatedDealer other = (CreateDesignatedDealer) obj;
            if (!java.util.Objects.equals(this.currency, other.currency)) { return false; }
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.addr, other.addr)) { return false; }
            if (!java.util.Objects.equals(this.auth_key_prefix, other.auth_key_prefix)) { return false; }
            if (!java.util.Objects.equals(this.human_name, other.human_name)) { return false; }
            if (!java.util.Objects.equals(this.add_all_currencies, other.add_all_currencies)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.currency != null ? this.currency.hashCode() : 0);
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.addr != null ? this.addr.hashCode() : 0);
            value = 31 * value + (this.auth_key_prefix != null ? this.auth_key_prefix.hashCode() : 0);
            value = 31 * value + (this.human_name != null ? this.human_name.hashCode() : 0);
            value = 31 * value + (this.add_all_currencies != null ? this.add_all_currencies.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.TypeTag currency;
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.diem.types.AccountAddress addr;
            public com.novi.serde.Bytes auth_key_prefix;
            public com.novi.serde.Bytes human_name;
            public Boolean add_all_currencies;

            public CreateDesignatedDealer build() {
                return new CreateDesignatedDealer(
                    currency,
                    sliding_nonce,
                    addr,
                    auth_key_prefix,
                    human_name,
                    add_all_currencies
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Creates a Parent VASP account with the specified human name. Must be called by the Treasury Compliance account.
     *
     * <p><b>Technical Description</b>
     * Creates an account with the Parent VASP role at {@code address} with authentication key
     * {@code auth_key_prefix} | {@code new_account_address} and a 0 balance of type {@code CoinType}. If
     * {@code add_all_currencies} is true, 0 balances for all available currencies in the system will
     * also be added. This can only be invoked by an TreasuryCompliance account.
     * {@code sliding_nonce} is a unique nonce for operation, see {@code SlidingNonce} for details.
     *
     * <p><b>Parameters</b>
     * | Name                  | Type         | Description                                                                                                                                                    |
     * | ------                | ------       | -------------                                                                                                                                                  |
     * | {@code CoinType}            | Type         | The Move type for the {@code CoinType} currency that the Parent VASP account should be initialized with. {@code CoinType} must be an already-registered currency on-chain. |
     * | {@code tc_account}          | {@code &signer}    | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account.                                                      |
     * | {@code sliding_nonce}       | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                                                                     |
     * | {@code new_account_address} | {@code address}    | Address of the to-be-created Parent VASP account.                                                                                                              |
     * | {@code auth_key_prefix}     | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account.                                                                       |
     * | {@code human_name}          | {@code vector<u8>} | ASCII-encoded human name for the Parent VASP.                                                                                                                  |
     * | {@code add_all_currencies}  | {@code bool}       | Whether to publish balance resources for all known currencies when the account is created.                                                                     |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category              | Error Reason                            | Description                                                                                |
     * | ----------------            | --------------                          | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}     | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code tc_account}.                             |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS}  | {@code CoreAddresses::ETREASURY_COMPLIANCE}   | The sending account is not the Treasury Compliance account.                                |
     * | {@code Errors::REQUIRES_ROLE}     | {@code Roles::ETREASURY_COMPLIANCE}           | The sending account is not the Treasury Compliance account.                                |
     * | {@code Errors::NOT_PUBLISHED}     | {@code Diem::ECURRENCY_INFO}                 | The {@code CoinType} is not a registered currency on-chain.                                      |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code Roles::EROLE_ID}                       | The {@code new_account_address} address is already taken.                                        |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_child_vasp_account}</li></ul>
     * <ul><li>{@code Script::add_currency_to_account}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key}</li></ul>
     * <ul><li>{@code Script::add_recovery_rotation_capability}</li></ul>
     * <ul><li>{@code Script::create_recovery_address}</li></ul>
     * <ul><li>{@code Script::rotate_dual_attestation_info}</li></ul>
     */
    public static final class CreateParentVaspAccount extends ScriptCall {
        public final com.diem.types.TypeTag coin_type;
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.diem.types.AccountAddress new_account_address;
        public final com.novi.serde.Bytes auth_key_prefix;
        public final com.novi.serde.Bytes human_name;
        public final Boolean add_all_currencies;

        public CreateParentVaspAccount(com.diem.types.TypeTag coin_type, @com.novi.serde.Unsigned Long sliding_nonce, com.diem.types.AccountAddress new_account_address, com.novi.serde.Bytes auth_key_prefix, com.novi.serde.Bytes human_name, Boolean add_all_currencies) {
            java.util.Objects.requireNonNull(coin_type, "coin_type must not be null");
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(new_account_address, "new_account_address must not be null");
            java.util.Objects.requireNonNull(auth_key_prefix, "auth_key_prefix must not be null");
            java.util.Objects.requireNonNull(human_name, "human_name must not be null");
            java.util.Objects.requireNonNull(add_all_currencies, "add_all_currencies must not be null");
            this.coin_type = coin_type;
            this.sliding_nonce = sliding_nonce;
            this.new_account_address = new_account_address;
            this.auth_key_prefix = auth_key_prefix;
            this.human_name = human_name;
            this.add_all_currencies = add_all_currencies;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            CreateParentVaspAccount other = (CreateParentVaspAccount) obj;
            if (!java.util.Objects.equals(this.coin_type, other.coin_type)) { return false; }
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.new_account_address, other.new_account_address)) { return false; }
            if (!java.util.Objects.equals(this.auth_key_prefix, other.auth_key_prefix)) { return false; }
            if (!java.util.Objects.equals(this.human_name, other.human_name)) { return false; }
            if (!java.util.Objects.equals(this.add_all_currencies, other.add_all_currencies)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.coin_type != null ? this.coin_type.hashCode() : 0);
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.new_account_address != null ? this.new_account_address.hashCode() : 0);
            value = 31 * value + (this.auth_key_prefix != null ? this.auth_key_prefix.hashCode() : 0);
            value = 31 * value + (this.human_name != null ? this.human_name.hashCode() : 0);
            value = 31 * value + (this.add_all_currencies != null ? this.add_all_currencies.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.TypeTag coin_type;
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.diem.types.AccountAddress new_account_address;
            public com.novi.serde.Bytes auth_key_prefix;
            public com.novi.serde.Bytes human_name;
            public Boolean add_all_currencies;

            public CreateParentVaspAccount build() {
                return new CreateParentVaspAccount(
                    coin_type,
                    sliding_nonce,
                    new_account_address,
                    auth_key_prefix,
                    human_name,
                    add_all_currencies
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Initializes the sending account as a recovery address that may be used by
     * the VASP that it belongs to. The sending account must be a VASP account.
     * Multiple recovery addresses can exist for a single VASP, but accounts in
     * each must be disjoint.
     *
     * <p><b>Technical Description</b>
     * Publishes a {@code RecoveryAddress::RecoveryAddress} resource under {@code account}. It then
     * extracts the {@code DiemAccount::KeyRotationCapability} for {@code account} and adds
     * it to the resource. After the successful execution of this transaction
     * other accounts may add their key rotation to this resource so that {@code account}
     * may be used as a recovery account for those accounts.
     *
     * <p><b>Parameters</b>
     * | Name      | Type      | Description                                           |
     * | ------    | ------    | -------------                                         |
     * | {@code account} | {@code &signer} | The signer of the sending account of the transaction. |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category              | Error Reason                                               | Description                                                                                   |
     * | ----------------            | --------------                                             | -------------                                                                                 |
     * | {@code Errors::INVALID_STATE}     | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.          |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code RecoveryAddress::ENOT_A_VASP}                             | {@code account} is not a VASP account.                                                              |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code RecoveryAddress::EKEY_ROTATION_DEPENDENCY_CYCLE}          | A key rotation recovery cycle would be created by adding {@code account}'s key rotation capability. |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code RecoveryAddress::ERECOVERY_ADDRESS}                       | A {@code RecoveryAddress::RecoveryAddress} resource has already been published under {@code account}.     |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::add_recovery_rotation_capability}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_recovery_address}</li></ul>
     */
    public static final class CreateRecoveryAddress extends ScriptCall {
        public CreateRecoveryAddress() {
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            CreateRecoveryAddress other = (CreateRecoveryAddress) obj;
            return true;
        }

        public int hashCode() {
            int value = 7;
            return value;
        }

        public static final class Builder {
            public CreateRecoveryAddress build() {
                return new CreateRecoveryAddress(
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Creates a Validator account. This transaction can only be sent by the Diem
     * Root account.
     *
     * <p><b>Technical Description</b>
     * Creates an account with a Validator role at {@code new_account_address}, with authentication key
     * {@code auth_key_prefix} | {@code new_account_address}. It publishes a
     * {@code ValidatorConfig::ValidatorConfig} resource with empty {@code config}, and
     * {@code operator_account} fields. The {@code human_name} field of the
     * {@code ValidatorConfig::ValidatorConfig} is set to the passed in {@code human_name}.
     * This script does not add the validator to the validator set or the system,
     * but only creates the account.
     *
     * <p><b>Parameters</b>
     * | Name                  | Type         | Description                                                                                     |
     * | ------                | ------       | -------------                                                                                   |
     * | {@code dr_account}          | {@code &signer}    | The signer reference of the sending account of this transaction. Must be the Diem Root signer. |
     * | {@code sliding_nonce}       | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                      |
     * | {@code new_account_address} | {@code address}    | Address of the to-be-created Validator account.                                                 |
     * | {@code auth_key_prefix}     | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account.        |
     * | {@code human_name}          | {@code vector<u8>} | ASCII-encoded human name for the validator.                                                     |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category              | Error Reason                            | Description                                                                                |
     * | ----------------            | --------------                          | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}     | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code dr_account}.                             |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS}  | {@code CoreAddresses::EDIEM_ROOT}            | The sending account is not the Diem Root account.                                         |
     * | {@code Errors::REQUIRES_ROLE}     | {@code Roles::EDIEM_ROOT}                    | The sending account is not the Diem Root account.                                         |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code Roles::EROLE_ID}                       | The {@code new_account_address} address is already taken.                                        |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     */
    public static final class CreateValidatorAccount extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.diem.types.AccountAddress new_account_address;
        public final com.novi.serde.Bytes auth_key_prefix;
        public final com.novi.serde.Bytes human_name;

        public CreateValidatorAccount(@com.novi.serde.Unsigned Long sliding_nonce, com.diem.types.AccountAddress new_account_address, com.novi.serde.Bytes auth_key_prefix, com.novi.serde.Bytes human_name) {
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(new_account_address, "new_account_address must not be null");
            java.util.Objects.requireNonNull(auth_key_prefix, "auth_key_prefix must not be null");
            java.util.Objects.requireNonNull(human_name, "human_name must not be null");
            this.sliding_nonce = sliding_nonce;
            this.new_account_address = new_account_address;
            this.auth_key_prefix = auth_key_prefix;
            this.human_name = human_name;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            CreateValidatorAccount other = (CreateValidatorAccount) obj;
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.new_account_address, other.new_account_address)) { return false; }
            if (!java.util.Objects.equals(this.auth_key_prefix, other.auth_key_prefix)) { return false; }
            if (!java.util.Objects.equals(this.human_name, other.human_name)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.new_account_address != null ? this.new_account_address.hashCode() : 0);
            value = 31 * value + (this.auth_key_prefix != null ? this.auth_key_prefix.hashCode() : 0);
            value = 31 * value + (this.human_name != null ? this.human_name.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.diem.types.AccountAddress new_account_address;
            public com.novi.serde.Bytes auth_key_prefix;
            public com.novi.serde.Bytes human_name;

            public CreateValidatorAccount build() {
                return new CreateValidatorAccount(
                    sliding_nonce,
                    new_account_address,
                    auth_key_prefix,
                    human_name
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Creates a Validator Operator account. This transaction can only be sent by the Diem
     * Root account.
     *
     * <p><b>Technical Description</b>
     * Creates an account with a Validator Operator role at {@code new_account_address}, with authentication key
     * {@code auth_key_prefix} | {@code new_account_address}. It publishes a
     * {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource with the specified {@code human_name}.
     * This script does not assign the validator operator to any validator accounts but only creates the account.
     *
     * <p><b>Parameters</b>
     * | Name                  | Type         | Description                                                                                     |
     * | ------                | ------       | -------------                                                                                   |
     * | {@code dr_account}          | {@code &signer}    | The signer reference of the sending account of this transaction. Must be the Diem Root signer. |
     * | {@code sliding_nonce}       | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                      |
     * | {@code new_account_address} | {@code address}    | Address of the to-be-created Validator account.                                                 |
     * | {@code auth_key_prefix}     | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account.        |
     * | {@code human_name}          | {@code vector<u8>} | ASCII-encoded human name for the validator.                                                     |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category              | Error Reason                            | Description                                                                                |
     * | ----------------            | --------------                          | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}     | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code dr_account}.                             |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS}  | {@code CoreAddresses::EDIEM_ROOT}            | The sending account is not the Diem Root account.                                         |
     * | {@code Errors::REQUIRES_ROLE}     | {@code Roles::EDIEM_ROOT}                    | The sending account is not the Diem Root account.                                         |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code Roles::EROLE_ID}                       | The {@code new_account_address} address is already taken.                                        |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     */
    public static final class CreateValidatorOperatorAccount extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.diem.types.AccountAddress new_account_address;
        public final com.novi.serde.Bytes auth_key_prefix;
        public final com.novi.serde.Bytes human_name;

        public CreateValidatorOperatorAccount(@com.novi.serde.Unsigned Long sliding_nonce, com.diem.types.AccountAddress new_account_address, com.novi.serde.Bytes auth_key_prefix, com.novi.serde.Bytes human_name) {
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(new_account_address, "new_account_address must not be null");
            java.util.Objects.requireNonNull(auth_key_prefix, "auth_key_prefix must not be null");
            java.util.Objects.requireNonNull(human_name, "human_name must not be null");
            this.sliding_nonce = sliding_nonce;
            this.new_account_address = new_account_address;
            this.auth_key_prefix = auth_key_prefix;
            this.human_name = human_name;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            CreateValidatorOperatorAccount other = (CreateValidatorOperatorAccount) obj;
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.new_account_address, other.new_account_address)) { return false; }
            if (!java.util.Objects.equals(this.auth_key_prefix, other.auth_key_prefix)) { return false; }
            if (!java.util.Objects.equals(this.human_name, other.human_name)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.new_account_address != null ? this.new_account_address.hashCode() : 0);
            value = 31 * value + (this.auth_key_prefix != null ? this.auth_key_prefix.hashCode() : 0);
            value = 31 * value + (this.human_name != null ? this.human_name.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.diem.types.AccountAddress new_account_address;
            public com.novi.serde.Bytes auth_key_prefix;
            public com.novi.serde.Bytes human_name;

            public CreateValidatorOperatorAccount build() {
                return new CreateValidatorOperatorAccount(
                    sliding_nonce,
                    new_account_address,
                    auth_key_prefix,
                    human_name
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Freezes the account at {@code address}. The sending account of this transaction
     * must be the Treasury Compliance account. The account being frozen cannot be
     * the Diem Root or Treasury Compliance account. After the successful
     * execution of this transaction no transactions may be sent from the frozen
     * account, and the frozen account may not send or receive coins.
     *
     * <p><b>Technical Description</b>
     * Sets the {@code AccountFreezing::FreezingBit} to {@code true} and emits a
     * {@code AccountFreezing::FreezeAccountEvent}. The transaction sender must be the
     * Treasury Compliance account, but the account at {@code to_freeze_account} must
     * not be either {@code 0xA550C18} (the Diem Root address), or {@code 0xB1E55ED} (the
     * Treasury Compliance address). Note that this is a per-account property
     * e.g., freezing a Parent VASP will not effect the status any of its child
     * accounts and vice versa.
     *

     * <p><b>Events</b>
     * Successful execution of this transaction will emit a {@code AccountFreezing::FreezeAccountEvent} on
     * the {@code freeze_event_handle} held in the {@code AccountFreezing::FreezeEventsHolder} resource published
     * under {@code 0xA550C18} with the {@code frozen_address} being the {@code to_freeze_account}.
     *
     * <p><b>Parameters</b>
     * | Name                | Type      | Description                                                                                               |
     * | ------              | ------    | -------------                                                                                             |
     * | {@code tc_account}        | {@code &signer} | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account. |
     * | {@code sliding_nonce}     | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                |
     * | {@code to_freeze_account} | {@code address} | The account address to be frozen.                                                                         |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                 | Description                                                                                |
     * | ----------------           | --------------                               | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}               | A {@code SlidingNonce} resource is not published under {@code tc_account}.                             |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}               | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}               | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}      | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE}        | The sending account is not the Treasury Compliance account.                                |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::ETREASURY_COMPLIANCE}                | The sending account is not the Treasury Compliance account.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code AccountFreezing::ECANNOT_FREEZE_TC}         | {@code to_freeze_account} was the Treasury Compliance account ({@code 0xB1E55ED}).                     |
     * | {@code Errors::INVALID_ARGUMENT} | {@code AccountFreezing::ECANNOT_FREEZE_DIEM_ROOT} | {@code to_freeze_account} was the Diem Root account ({@code 0xA550C18}).                              |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::unfreeze_account}</li></ul>
     */
    public static final class FreezeAccount extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.diem.types.AccountAddress to_freeze_account;

        public FreezeAccount(@com.novi.serde.Unsigned Long sliding_nonce, com.diem.types.AccountAddress to_freeze_account) {
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(to_freeze_account, "to_freeze_account must not be null");
            this.sliding_nonce = sliding_nonce;
            this.to_freeze_account = to_freeze_account;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            FreezeAccount other = (FreezeAccount) obj;
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.to_freeze_account, other.to_freeze_account)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.to_freeze_account != null ? this.to_freeze_account.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.diem.types.AccountAddress to_freeze_account;

            public FreezeAccount build() {
                return new FreezeAccount(
                    sliding_nonce,
                    to_freeze_account
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Transfers a given number of coins in a specified currency from one account to another.
     * Transfers over a specified amount defined on-chain that are between two different VASPs, or
     * other accounts that have opted-in will be subject to on-chain checks to ensure the receiver has
     * agreed to receive the coins.  This transaction can be sent by any account that can hold a
     * balance, and to any account that can hold a balance. Both accounts must hold balances in the
     * currency being transacted.
     *
     * <p><b>Technical Description</b>
     *
     * Transfers {@code amount} coins of type {@code Currency} from {@code payer} to {@code payee} with (optional) associated
     * {@code metadata} and an (optional) {@code metadata_signature} on the message
     * {@code metadata} | {@code Signer::address_of(payer)} | {@code amount} | {@code DualAttestation::DOMAIN_SEPARATOR}.
     * The {@code metadata} and {@code metadata_signature} parameters are only required if {@code amount} {@code >=}
     * {@code DualAttestation::get_cur_microdiem_limit} XDX and {@code payer} and {@code payee} are distinct VASPs.
     * However, a transaction sender can opt in to dual attestation even when it is not required
     * (e.g., a DesignatedDealer -{@code >} VASP payment) by providing a non-empty {@code metadata_signature}.
     * Standardized {@code metadata} LCS format can be found in {@code diem_types::transaction::metadata::Metadata}.
     *
     * <p><b>Events</b>
     * Successful execution of this script emits two events:
     * <ul><li>A {@code DiemAccount::SentPaymentEvent} on {@code payer}'s {@code DiemAccount::DiemAccount} {@code sent_events} handle; and</li></ul>
     * <ul><li>A {@code DiemAccount::ReceivedPaymentEvent} on {@code payee}'s {@code DiemAccount::DiemAccount} {@code received_events} handle.</li></ul>
     *
     * <p><b>Parameters</b>
     * | Name                 | Type         | Description                                                                                                                  |
     * | ------               | ------       | -------------                                                                                                                |
     * | {@code Currency}           | Type         | The Move type for the {@code Currency} being sent in this transaction. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code payer}              | {@code &signer}    | The signer reference of the sending account that coins are being transferred from.                                           |
     * | {@code payee}              | {@code address}    | The address of the account the coins are being transferred to.                                                               |
     * | {@code metadata}           | {@code vector<u8>} | Optional metadata about this payment.                                                                                        |
     * | {@code metadata_signature} | {@code vector<u8>} | Optional signature over {@code metadata} and payment information. See                                                              |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                     | Description                                                                                                                         |
     * | ----------------           | --------------                                   | -------------                                                                                                                       |
     * | {@code Errors::NOT_PUBLISHED}    | {@code DiemAccount::EPAYER_DOESNT_HOLD_CURRENCY}      | {@code payer} doesn't hold a balance in {@code Currency}.                                                                                       |
     * | {@code Errors::LIMIT_EXCEEDED}   | {@code DiemAccount::EINSUFFICIENT_BALANCE}            | {@code amount} is greater than {@code payer}'s balance in {@code Currency}.                                                                           |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::ECOIN_DEPOSIT_IS_ZERO}            | {@code amount} is zero.                                                                                                                   |
     * | {@code Errors::NOT_PUBLISHED}    | {@code DiemAccount::EPAYEE_DOES_NOT_EXIST}            | No account exists at the {@code payee} address.                                                                                           |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EPAYEE_CANT_ACCEPT_CURRENCY_TYPE} | An account exists at {@code payee}, but it does not accept payments in {@code Currency}.                                                        |
     * | {@code Errors::INVALID_STATE}    | {@code AccountFreezing::EACCOUNT_FROZEN}               | The {@code payee} account is frozen.                                                                                                      |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DualAttestation::EMALFORMED_METADATA_SIGNATURE} | {@code metadata_signature} is not 64 bytes.                                                                                               |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DualAttestation::EINVALID_METADATA_SIGNATURE}   | {@code metadata_signature} does not verify on the against the {@code payee'}s {@code DualAttestation::Credential} {@code compliance_public_key} public key. |
     * | {@code Errors::LIMIT_EXCEEDED}   | {@code DiemAccount::EWITHDRAWAL_EXCEEDS_LIMITS}       | {@code payer} has exceeded its daily withdrawal limits for the backing coins of XDX.                                                      |
     * | {@code Errors::LIMIT_EXCEEDED}   | {@code DiemAccount::EDEPOSIT_EXCEEDS_LIMITS}          | {@code payee} has exceeded its daily deposit limits for XDX.                                                                              |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_child_vasp_account}</li></ul>
     * <ul><li>{@code Script::create_parent_vasp_account}</li></ul>
     * <ul><li>{@code Script::add_currency_to_account}</li></ul>
     */
    public static final class PeerToPeerWithMetadata extends ScriptCall {
        public final com.diem.types.TypeTag currency;
        public final com.diem.types.AccountAddress payee;
        public final @com.novi.serde.Unsigned Long amount;
        public final com.novi.serde.Bytes metadata;
        public final com.novi.serde.Bytes metadata_signature;

        public PeerToPeerWithMetadata(com.diem.types.TypeTag currency, com.diem.types.AccountAddress payee, @com.novi.serde.Unsigned Long amount, com.novi.serde.Bytes metadata, com.novi.serde.Bytes metadata_signature) {
            java.util.Objects.requireNonNull(currency, "currency must not be null");
            java.util.Objects.requireNonNull(payee, "payee must not be null");
            java.util.Objects.requireNonNull(amount, "amount must not be null");
            java.util.Objects.requireNonNull(metadata, "metadata must not be null");
            java.util.Objects.requireNonNull(metadata_signature, "metadata_signature must not be null");
            this.currency = currency;
            this.payee = payee;
            this.amount = amount;
            this.metadata = metadata;
            this.metadata_signature = metadata_signature;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PeerToPeerWithMetadata other = (PeerToPeerWithMetadata) obj;
            if (!java.util.Objects.equals(this.currency, other.currency)) { return false; }
            if (!java.util.Objects.equals(this.payee, other.payee)) { return false; }
            if (!java.util.Objects.equals(this.amount, other.amount)) { return false; }
            if (!java.util.Objects.equals(this.metadata, other.metadata)) { return false; }
            if (!java.util.Objects.equals(this.metadata_signature, other.metadata_signature)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.currency != null ? this.currency.hashCode() : 0);
            value = 31 * value + (this.payee != null ? this.payee.hashCode() : 0);
            value = 31 * value + (this.amount != null ? this.amount.hashCode() : 0);
            value = 31 * value + (this.metadata != null ? this.metadata.hashCode() : 0);
            value = 31 * value + (this.metadata_signature != null ? this.metadata_signature.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.TypeTag currency;
            public com.diem.types.AccountAddress payee;
            public @com.novi.serde.Unsigned Long amount;
            public com.novi.serde.Bytes metadata;
            public com.novi.serde.Bytes metadata_signature;

            public PeerToPeerWithMetadata build() {
                return new PeerToPeerWithMetadata(
                    currency,
                    payee,
                    amount,
                    metadata,
                    metadata_signature
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Moves a specified number of coins in a given currency from the account's
     * balance to its preburn area after which the coins may be burned. This
     * transaction may be sent by any account that holds a balance and preburn area
     * in the specified currency.
     *
     * <p><b>Technical Description</b>
     * Moves the specified {@code amount} of coins in {@code Token} currency from the sending {@code account}'s
     * {@code DiemAccount::Balance<Token>} to the {@code Diem::Preburn<Token>} published under the same
     * {@code account}. {@code account} must have both of these resources published under it at the start of this
     * transaction in order for it to execute successfully.
     *
     * <p><b>Events</b>
     * Successful execution of this script emits two events:
     * <ul><li>{@code DiemAccount::SentPaymentEvent } on {@code account}'s {@code DiemAccount::DiemAccount} {@code sent_events}</li></ul>
     * handle with the {@code payee} and {@code payer} fields being {@code account}'s address; and
     * <ul><li>A {@code Diem::PreburnEvent} with {@code Token}'s currency code on the</li></ul>
     * {@code Diem::CurrencyInfo<Token}'s {@code preburn_events} handle for {@code Token} and with
     * {@code preburn_address} set to {@code account}'s address.
     *
     * <p><b>Parameters</b>
     * | Name      | Type      | Description                                                                                                                      |
     * | ------    | ------    | -------------                                                                                                                    |
     * | {@code Token}   | Type      | The Move type for the {@code Token} currency being moved to the preburn area. {@code Token} must be an already-registered currency on-chain. |
     * | {@code account} | {@code &signer} | The signer reference of the sending account.                                                                                     |
     * | {@code amount}  | {@code u64}     | The amount in {@code Token} to be moved to the preburn area.                                                                           |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category           | Error Reason                                             | Description                                                                             |
     * | ----------------         | --------------                                           | -------------                                                                           |
     * | {@code Errors::NOT_PUBLISHED}  | {@code Diem::ECURRENCY_INFO}                                  | The {@code Token} is not a registered currency on-chain.                                      |
     * | {@code Errors::INVALID_STATE}  | {@code DiemAccount::EWITHDRAWAL_CAPABILITY_ALREADY_EXTRACTED} | The withdrawal capability for {@code account} has already been extracted.                     |
     * | {@code Errors::LIMIT_EXCEEDED} | {@code DiemAccount::EINSUFFICIENT_BALANCE}                    | {@code amount} is greater than {@code payer}'s balance in {@code Token}.                                  |
     * | {@code Errors::NOT_PUBLISHED}  | {@code DiemAccount::EPAYER_DOESNT_HOLD_CURRENCY}              | {@code account} doesn't hold a balance in {@code Token}.                                            |
     * | {@code Errors::NOT_PUBLISHED}  | {@code Diem::EPREBURN}                                        | {@code account} doesn't have a {@code Diem::Preburn<Token>} resource published under it.           |
     * | {@code Errors::INVALID_STATE}  | {@code Diem::EPREBURN_OCCUPIED}                               | The {@code value} field in the {@code Diem::Preburn<Token>} resource under the sender is non-zero. |
     * | {@code Errors::NOT_PUBLISHED}  | {@code Roles::EROLE_ID}                                        | The {@code account} did not have a role assigned to it.                                       |
     * | {@code Errors::REQUIRES_ROLE}  | {@code Roles::EDESIGNATED_DEALER}                              | The {@code account} did not have the role of DesignatedDealer.                                |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::cancel_burn}</li></ul>
     * <ul><li>{@code Script::burn}</li></ul>
     * <ul><li>{@code Script::burn_txn_fees}</li></ul>
     */
    public static final class Preburn extends ScriptCall {
        public final com.diem.types.TypeTag token;
        public final @com.novi.serde.Unsigned Long amount;

        public Preburn(com.diem.types.TypeTag token, @com.novi.serde.Unsigned Long amount) {
            java.util.Objects.requireNonNull(token, "token must not be null");
            java.util.Objects.requireNonNull(amount, "amount must not be null");
            this.token = token;
            this.amount = amount;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Preburn other = (Preburn) obj;
            if (!java.util.Objects.equals(this.token, other.token)) { return false; }
            if (!java.util.Objects.equals(this.amount, other.amount)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.token != null ? this.token.hashCode() : 0);
            value = 31 * value + (this.amount != null ? this.amount.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.TypeTag token;
            public @com.novi.serde.Unsigned Long amount;

            public Preburn build() {
                return new Preburn(
                    token,
                    amount
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Rotates the authentication key of the sending account to the
     * newly-specified public key and publishes a new shared authentication key
     * under the sender's account. Any account can send this transaction.
     *
     * <p><b>Technical Description</b>
     * Rotates the authentication key of the sending account to {@code public_key},
     * and publishes a {@code SharedEd25519PublicKey::SharedEd25519PublicKey} resource
     * containing the 32-byte ed25519 {@code public_key} and the {@code DiemAccount::KeyRotationCapability} for
     * {@code account} under {@code account}.
     *
     * <p><b>Parameters</b>
     * | Name         | Type         | Description                                                                               |
     * | ------       | ------       | -------------                                                                             |
     * | {@code account}    | {@code &signer}    | The signer reference of the sending account of the transaction.                           |
     * | {@code public_key} | {@code vector<u8>} | 32-byte Ed25519 public key for {@code account}' authentication key to be rotated to and stored. |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category              | Error Reason                                               | Description                                                                                         |
     * | ----------------            | --------------                                             | -------------                                                                                       |
     * | {@code Errors::INVALID_STATE}     | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability} resource.       |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code SharedEd25519PublicKey::ESHARED_KEY}                      | The {@code SharedEd25519PublicKey::SharedEd25519PublicKey} resource is already published under {@code account}. |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SharedEd25519PublicKey::EMALFORMED_PUBLIC_KEY}            | {@code public_key} is an invalid ed25519 public key.                                                      |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::rotate_shared_ed25519_public_key}</li></ul>
     */
    public static final class PublishSharedEd25519PublicKey extends ScriptCall {
        public final com.novi.serde.Bytes public_key;

        public PublishSharedEd25519PublicKey(com.novi.serde.Bytes public_key) {
            java.util.Objects.requireNonNull(public_key, "public_key must not be null");
            this.public_key = public_key;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PublishSharedEd25519PublicKey other = (PublishSharedEd25519PublicKey) obj;
            if (!java.util.Objects.equals(this.public_key, other.public_key)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.public_key != null ? this.public_key.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.novi.serde.Bytes public_key;

            public PublishSharedEd25519PublicKey build() {
                return new PublishSharedEd25519PublicKey(
                    public_key
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Updates a validator's configuration. This does not reconfigure the system and will not update
     * the configuration in the validator set that is seen by other validators in the network. Can
     * only be successfully sent by a Validator Operator account that is already registered with a
     * validator.
     *
     * <p><b>Technical Description</b>
     * This updates the fields with corresponding names held in the {@code ValidatorConfig::ValidatorConfig}
     * config resource held under {@code validator_account}. It does not emit a {@code DiemConfig::NewEpochEvent}
     * so the copy of this config held in the validator set will not be updated, and the changes are
     * only "locally" under the {@code validator_account} account address.
     *
     * <p><b>Parameters</b>
     * | Name                          | Type         | Description                                                                                                                  |
     * | ------                        | ------       | -------------                                                                                                                |
     * | {@code validator_operator_account}  | {@code &signer}    | Signer reference of the sending account. Must be the registered validator operator for the validator at {@code validator_address}. |
     * | {@code validator_account}           | {@code address}    | The address of the validator's {@code ValidatorConfig::ValidatorConfig} resource being updated.                                    |
     * | {@code consensus_pubkey}            | {@code vector<u8>} | New Ed25519 public key to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                                         |
     * | {@code validator_network_addresses} | {@code vector<u8>} | New set of {@code validator_network_addresses} to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                       |
     * | {@code fullnode_network_addresses}  | {@code vector<u8>} | New set of {@code fullnode_network_addresses} to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                        |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                   | Description                                                                                           |
     * | ----------------           | --------------                                 | -------------                                                                                         |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorConfig::EVALIDATOR_CONFIG}           | {@code validator_address} does not have a {@code ValidatorConfig::ValidatorConfig} resource published under it.   |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::EINVALID_TRANSACTION_SENDER} | {@code validator_operator_account} is not the registered operator for the validator at {@code validator_address}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::EINVALID_CONSENSUS_KEY}      | {@code consensus_pubkey} is not a valid ed25519 public key.                                                 |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     */
    public static final class RegisterValidatorConfig extends ScriptCall {
        public final com.diem.types.AccountAddress validator_account;
        public final com.novi.serde.Bytes consensus_pubkey;
        public final com.novi.serde.Bytes validator_network_addresses;
        public final com.novi.serde.Bytes fullnode_network_addresses;

        public RegisterValidatorConfig(com.diem.types.AccountAddress validator_account, com.novi.serde.Bytes consensus_pubkey, com.novi.serde.Bytes validator_network_addresses, com.novi.serde.Bytes fullnode_network_addresses) {
            java.util.Objects.requireNonNull(validator_account, "validator_account must not be null");
            java.util.Objects.requireNonNull(consensus_pubkey, "consensus_pubkey must not be null");
            java.util.Objects.requireNonNull(validator_network_addresses, "validator_network_addresses must not be null");
            java.util.Objects.requireNonNull(fullnode_network_addresses, "fullnode_network_addresses must not be null");
            this.validator_account = validator_account;
            this.consensus_pubkey = consensus_pubkey;
            this.validator_network_addresses = validator_network_addresses;
            this.fullnode_network_addresses = fullnode_network_addresses;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            RegisterValidatorConfig other = (RegisterValidatorConfig) obj;
            if (!java.util.Objects.equals(this.validator_account, other.validator_account)) { return false; }
            if (!java.util.Objects.equals(this.consensus_pubkey, other.consensus_pubkey)) { return false; }
            if (!java.util.Objects.equals(this.validator_network_addresses, other.validator_network_addresses)) { return false; }
            if (!java.util.Objects.equals(this.fullnode_network_addresses, other.fullnode_network_addresses)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.validator_account != null ? this.validator_account.hashCode() : 0);
            value = 31 * value + (this.consensus_pubkey != null ? this.consensus_pubkey.hashCode() : 0);
            value = 31 * value + (this.validator_network_addresses != null ? this.validator_network_addresses.hashCode() : 0);
            value = 31 * value + (this.fullnode_network_addresses != null ? this.fullnode_network_addresses.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.AccountAddress validator_account;
            public com.novi.serde.Bytes consensus_pubkey;
            public com.novi.serde.Bytes validator_network_addresses;
            public com.novi.serde.Bytes fullnode_network_addresses;

            public RegisterValidatorConfig build() {
                return new RegisterValidatorConfig(
                    validator_account,
                    consensus_pubkey,
                    validator_network_addresses,
                    fullnode_network_addresses
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * This script removes a validator account from the validator set, and triggers a reconfiguration
     * of the system to remove the validator from the system. This transaction can only be
     * successfully called by the Diem Root account.
     *
     * <p><b>Technical Description</b>
     * This script removes the account at {@code validator_address} from the validator set. This transaction
     * emits a {@code DiemConfig::NewEpochEvent} event. Once the reconfiguration triggered by this event
     * has been performed, the account at {@code validator_address} is no longer considered to be a
     * validator in the network. This transaction will fail if the validator at {@code validator_address}
     * is not in the validator set.
     *
     * <p><b>Parameters</b>
     * | Name                | Type         | Description                                                                                                                        |
     * | ------              | ------       | -------------                                                                                                                      |
     * | {@code dr_account}        | {@code &signer}    | The signer reference of the sending account of this transaction. Must be the Diem Root signer.                                    |
     * | {@code sliding_nonce}     | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                                         |
     * | {@code validator_name}    | {@code vector<u8>} | ASCII-encoded human name for the validator. Must match the human name in the {@code ValidatorConfig::ValidatorConfig} for the validator. |
     * | {@code validator_address} | {@code address}    | The validator account address to be removed from the validator set.                                                                |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                            | Description                                                                                     |
     * | ----------------           | --------------                          | -------------                                                                                   |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code dr_account}.                                  |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not.      |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                                   |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                               |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}          | The sending account is not the Diem Root account or Treasury Compliance account                |
     * | 0                          | 0                                       | The provided {@code validator_name} does not match the already-recorded human name for the validator. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemSystem::ENOT_AN_ACTIVE_VALIDATOR} | The validator to be removed is not in the validator set.                                        |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::EDIEM_ROOT}            | The sending account is not the Diem Root account.                                              |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::EDIEM_ROOT}                    | The sending account is not the Diem Root account.                                              |
     * | {@code Errors::INVALID_STATE}    | {@code DiemConfig::EINVALID_BLOCK_TIME}      | An invalid time value was encountered in reconfiguration. Unlikely to occur.                    |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     */
    public static final class RemoveValidatorAndReconfigure extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.novi.serde.Bytes validator_name;
        public final com.diem.types.AccountAddress validator_address;

        public RemoveValidatorAndReconfigure(@com.novi.serde.Unsigned Long sliding_nonce, com.novi.serde.Bytes validator_name, com.diem.types.AccountAddress validator_address) {
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(validator_name, "validator_name must not be null");
            java.util.Objects.requireNonNull(validator_address, "validator_address must not be null");
            this.sliding_nonce = sliding_nonce;
            this.validator_name = validator_name;
            this.validator_address = validator_address;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            RemoveValidatorAndReconfigure other = (RemoveValidatorAndReconfigure) obj;
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.validator_name, other.validator_name)) { return false; }
            if (!java.util.Objects.equals(this.validator_address, other.validator_address)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.validator_name != null ? this.validator_name.hashCode() : 0);
            value = 31 * value + (this.validator_address != null ? this.validator_address.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.novi.serde.Bytes validator_name;
            public com.diem.types.AccountAddress validator_address;

            public RemoveValidatorAndReconfigure build() {
                return new RemoveValidatorAndReconfigure(
                    sliding_nonce,
                    validator_name,
                    validator_address
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Rotates the transaction sender's authentication key to the supplied new authentication key. May
     * be sent by any account.
     *
     * <p><b>Technical Description</b>
     * Rotate the {@code account}'s {@code DiemAccount::DiemAccount} {@code authentication_key} field to {@code new_key}.
     * {@code new_key} must be a valid ed25519 public key, and {@code account} must not have previously delegated
     * its {@code DiemAccount::KeyRotationCapability}.
     *
     * <p><b>Parameters</b>
     * | Name      | Type         | Description                                                 |
     * | ------    | ------       | -------------                                               |
     * | {@code account} | {@code &signer}    | Signer reference of the sending account of the transaction. |
     * | {@code new_key} | {@code vector<u8>} | New ed25519 public key to be used for {@code account}.            |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                               | Description                                                                              |
     * | ----------------           | --------------                                             | -------------                                                                            |
     * | {@code Errors::INVALID_STATE}    | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.     |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY}              | {@code new_key} was an invalid length.                                                         |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::rotate_authentication_key_with_nonce}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_recovery_address}</li></ul>
     */
    public static final class RotateAuthenticationKey extends ScriptCall {
        public final com.novi.serde.Bytes new_key;

        public RotateAuthenticationKey(com.novi.serde.Bytes new_key) {
            java.util.Objects.requireNonNull(new_key, "new_key must not be null");
            this.new_key = new_key;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            RotateAuthenticationKey other = (RotateAuthenticationKey) obj;
            if (!java.util.Objects.equals(this.new_key, other.new_key)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.new_key != null ? this.new_key.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.novi.serde.Bytes new_key;

            public RotateAuthenticationKey build() {
                return new RotateAuthenticationKey(
                    new_key
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Rotates the sender's authentication key to the supplied new authentication key. May be sent by
     * any account that has a sliding nonce resource published under it (usually this is Treasury
     * Compliance or Diem Root accounts).
     *
     * <p><b>Technical Description</b>
     * Rotates the {@code account}'s {@code DiemAccount::DiemAccount} {@code authentication_key} field to {@code new_key}.
     * {@code new_key} must be a valid ed25519 public key, and {@code account} must not have previously delegated
     * its {@code DiemAccount::KeyRotationCapability}.
     *
     * <p><b>Parameters</b>
     * | Name            | Type         | Description                                                                |
     * | ------          | ------       | -------------                                                              |
     * | {@code account}       | {@code &signer}    | Signer reference of the sending account of the transaction.                |
     * | {@code sliding_nonce} | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction. |
     * | {@code new_key}       | {@code vector<u8>} | New ed25519 public key to be used for {@code account}.                           |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                               | Description                                                                                |
     * | ----------------           | --------------                                             | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                             | A {@code SlidingNonce} resource is not published under {@code account}.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                             | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                             | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}                    | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::INVALID_STATE}    | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.       |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY}              | {@code new_key} was an invalid length.                                                           |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::rotate_authentication_key}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_recovery_address}</li></ul>
     */
    public static final class RotateAuthenticationKeyWithNonce extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.novi.serde.Bytes new_key;

        public RotateAuthenticationKeyWithNonce(@com.novi.serde.Unsigned Long sliding_nonce, com.novi.serde.Bytes new_key) {
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(new_key, "new_key must not be null");
            this.sliding_nonce = sliding_nonce;
            this.new_key = new_key;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            RotateAuthenticationKeyWithNonce other = (RotateAuthenticationKeyWithNonce) obj;
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.new_key, other.new_key)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.new_key != null ? this.new_key.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.novi.serde.Bytes new_key;

            public RotateAuthenticationKeyWithNonce build() {
                return new RotateAuthenticationKeyWithNonce(
                    sliding_nonce,
                    new_key
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Rotates the specified account's authentication key to the supplied new authentication key. May
     * only be sent by the Diem Root account as a write set transaction.
     *
     * <p><b>Technical Description</b>
     * Rotate the {@code account}'s {@code DiemAccount::DiemAccount} {@code authentication_key} field to {@code new_key}.
     * {@code new_key} must be a valid ed25519 public key, and {@code account} must not have previously delegated
     * its {@code DiemAccount::KeyRotationCapability}.
     *
     * <p><b>Parameters</b>
     * | Name            | Type         | Description                                                                                                  |
     * | ------          | ------       | -------------                                                                                                |
     * | {@code dr_account}    | {@code &signer}    | The signer reference of the sending account of the write set transaction. May only be the Diem Root signer. |
     * | {@code account}       | {@code &signer}    | Signer reference of account specified in the {@code execute_as} field of the write set transaction.                |
     * | {@code sliding_nonce} | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction for Diem Root.                    |
     * | {@code new_key}       | {@code vector<u8>} | New ed25519 public key to be used for {@code account}.                                                             |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                               | Description                                                                                                |
     * | ----------------           | --------------                                             | -------------                                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                             | A {@code SlidingNonce} resource is not published under {@code dr_account}.                                             |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                             | The {@code sliding_nonce} in {@code dr_account} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                             | The {@code sliding_nonce} in {@code dr_account} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}                    | The {@code sliding_nonce} in{@code  dr_account} has been previously recorded.                                          |
     * | {@code Errors::INVALID_STATE}    | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.                       |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY}              | {@code new_key} was an invalid length.                                                                           |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::rotate_authentication_key}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_nonce}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_recovery_address}</li></ul>
     */
    public static final class RotateAuthenticationKeyWithNonceAdmin extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.novi.serde.Bytes new_key;

        public RotateAuthenticationKeyWithNonceAdmin(@com.novi.serde.Unsigned Long sliding_nonce, com.novi.serde.Bytes new_key) {
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(new_key, "new_key must not be null");
            this.sliding_nonce = sliding_nonce;
            this.new_key = new_key;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            RotateAuthenticationKeyWithNonceAdmin other = (RotateAuthenticationKeyWithNonceAdmin) obj;
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.new_key, other.new_key)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.new_key != null ? this.new_key.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.novi.serde.Bytes new_key;

            public RotateAuthenticationKeyWithNonceAdmin build() {
                return new RotateAuthenticationKeyWithNonceAdmin(
                    sliding_nonce,
                    new_key
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Rotates the authentication key of a specified account that is part of a recovery address to a
     * new authentication key. Only used for accounts that are part of a recovery address (see
     * {@code Script::add_recovery_rotation_capability} for account restrictions).
     *
     * <p><b>Technical Description</b>
     * Rotates the authentication key of the {@code to_recover} account to {@code new_key} using the
     * {@code DiemAccount::KeyRotationCapability} stored in the {@code RecoveryAddress::RecoveryAddress} resource
     * published under {@code recovery_address}. This transaction can be sent either by the {@code to_recover}
     * account, or by the account where the {@code RecoveryAddress::RecoveryAddress} resource is published
     * that contains {@code to_recover}'s {@code DiemAccount::KeyRotationCapability}.
     *
     * <p><b>Parameters</b>
     * | Name               | Type         | Description                                                                                                                    |
     * | ------             | ------       | -------------                                                                                                                  |
     * | {@code account}          | {@code &signer}    | Signer reference of the sending account of the transaction.                                                                    |
     * | {@code recovery_address} | {@code address}    | Address where {@code RecoveryAddress::RecoveryAddress} that holds {@code to_recover}'s {@code DiemAccount::KeyRotationCapability} is published. |
     * | {@code to_recover}       | {@code address}    | The address of the account whose authentication key will be updated.                                                           |
     * | {@code new_key}          | {@code vector<u8>} | New ed25519 public key to be used for the account at the {@code to_recover} address.                                                 |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                  | Description                                                                                                                                          |
     * | ----------------           | --------------                                | -------------                                                                                                                                        |
     * | {@code Errors::NOT_PUBLISHED}    | {@code RecoveryAddress::ERECOVERY_ADDRESS}          | {@code recovery_address} does not have a {@code RecoveryAddress::RecoveryAddress} resource published under it.                                                   |
     * | {@code Errors::INVALID_ARGUMENT} | {@code RecoveryAddress::ECANNOT_ROTATE_KEY}         | The address of {@code account} is not {@code recovery_address} or {@code to_recover}.                                                                                  |
     * | {@code Errors::INVALID_ARGUMENT} | {@code RecoveryAddress::EACCOUNT_NOT_RECOVERABLE}   | {@code to_recover}'s {@code DiemAccount::KeyRotationCapability}  is not in the {@code RecoveryAddress::RecoveryAddress}  resource published under {@code recovery_address}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY} | {@code new_key} was an invalid length.                                                                                                                     |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::rotate_authentication_key}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_nonce}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_nonce_admin}</li></ul>
     */
    public static final class RotateAuthenticationKeyWithRecoveryAddress extends ScriptCall {
        public final com.diem.types.AccountAddress recovery_address;
        public final com.diem.types.AccountAddress to_recover;
        public final com.novi.serde.Bytes new_key;

        public RotateAuthenticationKeyWithRecoveryAddress(com.diem.types.AccountAddress recovery_address, com.diem.types.AccountAddress to_recover, com.novi.serde.Bytes new_key) {
            java.util.Objects.requireNonNull(recovery_address, "recovery_address must not be null");
            java.util.Objects.requireNonNull(to_recover, "to_recover must not be null");
            java.util.Objects.requireNonNull(new_key, "new_key must not be null");
            this.recovery_address = recovery_address;
            this.to_recover = to_recover;
            this.new_key = new_key;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            RotateAuthenticationKeyWithRecoveryAddress other = (RotateAuthenticationKeyWithRecoveryAddress) obj;
            if (!java.util.Objects.equals(this.recovery_address, other.recovery_address)) { return false; }
            if (!java.util.Objects.equals(this.to_recover, other.to_recover)) { return false; }
            if (!java.util.Objects.equals(this.new_key, other.new_key)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.recovery_address != null ? this.recovery_address.hashCode() : 0);
            value = 31 * value + (this.to_recover != null ? this.to_recover.hashCode() : 0);
            value = 31 * value + (this.new_key != null ? this.new_key.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.AccountAddress recovery_address;
            public com.diem.types.AccountAddress to_recover;
            public com.novi.serde.Bytes new_key;

            public RotateAuthenticationKeyWithRecoveryAddress build() {
                return new RotateAuthenticationKeyWithRecoveryAddress(
                    recovery_address,
                    to_recover,
                    new_key
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Updates the url used for off-chain communication, and the public key used to verify dual
     * attestation on-chain. Transaction can be sent by any account that has dual attestation
     * information published under it. In practice the only such accounts are Designated Dealers and
     * Parent VASPs.
     *
     * <p><b>Technical Description</b>
     * Updates the {@code base_url} and {@code compliance_public_key} fields of the {@code DualAttestation::Credential}
     * resource published under {@code account}. The {@code new_key} must be a valid ed25519 public key.
     *
     * <p><b>Events</b>
     * Successful execution of this transaction emits two events:
     * <ul><li>A {@code DualAttestation::ComplianceKeyRotationEvent} containing the new compliance public key, and</li></ul>
     * the blockchain time at which the key was updated emitted on the {@code DualAttestation::Credential}
     * {@code compliance_key_rotation_events} handle published under {@code account}; and
     * <ul><li>A {@code DualAttestation::BaseUrlRotationEvent} containing the new base url to be used for</li></ul>
     * off-chain communication, and the blockchain time at which the url was updated emitted on the
     * {@code DualAttestation::Credential} {@code base_url_rotation_events} handle published under {@code account}.
     *
     * <p><b>Parameters</b>
     * | Name      | Type         | Description                                                               |
     * | ------    | ------       | -------------                                                             |
     * | {@code account} | {@code &signer}    | Signer reference of the sending account of the transaction.               |
     * | {@code new_url} | {@code vector<u8>} | ASCII-encoded url to be used for off-chain communication with {@code account}.  |
     * | {@code new_key} | {@code vector<u8>} | New ed25519 public key to be used for on-chain dual attestation checking. |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                           | Description                                                                |
     * | ----------------           | --------------                         | -------------                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code DualAttestation::ECREDENTIAL}         | A {@code DualAttestation::Credential} resource is not published under {@code account}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DualAttestation::EINVALID_PUBLIC_KEY} | {@code new_key} is not a valid ed25519 public key.                               |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_parent_vasp_account}</li></ul>
     * <ul><li>{@code Script::create_designated_dealer}</li></ul>
     * <ul><li>{@code Script::rotate_dual_attestation_info}</li></ul>
     */
    public static final class RotateDualAttestationInfo extends ScriptCall {
        public final com.novi.serde.Bytes new_url;
        public final com.novi.serde.Bytes new_key;

        public RotateDualAttestationInfo(com.novi.serde.Bytes new_url, com.novi.serde.Bytes new_key) {
            java.util.Objects.requireNonNull(new_url, "new_url must not be null");
            java.util.Objects.requireNonNull(new_key, "new_key must not be null");
            this.new_url = new_url;
            this.new_key = new_key;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            RotateDualAttestationInfo other = (RotateDualAttestationInfo) obj;
            if (!java.util.Objects.equals(this.new_url, other.new_url)) { return false; }
            if (!java.util.Objects.equals(this.new_key, other.new_key)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.new_url != null ? this.new_url.hashCode() : 0);
            value = 31 * value + (this.new_key != null ? this.new_key.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.novi.serde.Bytes new_url;
            public com.novi.serde.Bytes new_key;

            public RotateDualAttestationInfo build() {
                return new RotateDualAttestationInfo(
                    new_url,
                    new_key
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Rotates the authentication key in a {@code SharedEd25519PublicKey}. This transaction can be sent by
     * any account that has previously published a shared ed25519 public key using
     * {@code Script::publish_shared_ed25519_public_key}.
     *
     * <p><b>Technical Description</b>
     * This first rotates the public key stored in {@code account}'s
     * {@code SharedEd25519PublicKey::SharedEd25519PublicKey} resource to {@code public_key}, after which it
     * rotates the authentication key using the capability stored in {@code account}'s
     * {@code SharedEd25519PublicKey::SharedEd25519PublicKey} to a new value derived from {@code public_key}
     *
     * <p><b>Parameters</b>
     * | Name         | Type         | Description                                                     |
     * | ------       | ------       | -------------                                                   |
     * | {@code account}    | {@code &signer}    | The signer reference of the sending account of the transaction. |
     * | {@code public_key} | {@code vector<u8>} | 32-byte Ed25519 public key.                                     |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                    | Description                                                                                   |
     * | ----------------           | --------------                                  | -------------                                                                                 |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SharedEd25519PublicKey::ESHARED_KEY}           | A {@code SharedEd25519PublicKey::SharedEd25519PublicKey} resource is not published under {@code account}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SharedEd25519PublicKey::EMALFORMED_PUBLIC_KEY} | {@code public_key} is an invalid ed25519 public key.                                                |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::publish_shared_ed25519_public_key}</li></ul>
     */
    public static final class RotateSharedEd25519PublicKey extends ScriptCall {
        public final com.novi.serde.Bytes public_key;

        public RotateSharedEd25519PublicKey(com.novi.serde.Bytes public_key) {
            java.util.Objects.requireNonNull(public_key, "public_key must not be null");
            this.public_key = public_key;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            RotateSharedEd25519PublicKey other = (RotateSharedEd25519PublicKey) obj;
            if (!java.util.Objects.equals(this.public_key, other.public_key)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.public_key != null ? this.public_key.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.novi.serde.Bytes public_key;

            public RotateSharedEd25519PublicKey build() {
                return new RotateSharedEd25519PublicKey(
                    public_key
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Updates a validator's configuration, and triggers a reconfiguration of the system to update the
     * validator set with this new validator configuration.  Can only be successfully sent by a
     * Validator Operator account that is already registered with a validator.
     *
     * <p><b>Technical Description</b>
     * This updates the fields with corresponding names held in the {@code ValidatorConfig::ValidatorConfig}
     * config resource held under {@code validator_account}. It then emits a {@code DiemConfig::NewEpochEvent} to
     * trigger a reconfiguration of the system.  This reconfiguration will update the validator set
     * on-chain with the updated {@code ValidatorConfig::ValidatorConfig}.
     *
     * <p><b>Parameters</b>
     * | Name                          | Type         | Description                                                                                                                  |
     * | ------                        | ------       | -------------                                                                                                                |
     * | {@code validator_operator_account}  | {@code &signer}    | Signer reference of the sending account. Must be the registered validator operator for the validator at {@code validator_address}. |
     * | {@code validator_account}           | {@code address}    | The address of the validator's {@code ValidatorConfig::ValidatorConfig} resource being updated.                                    |
     * | {@code consensus_pubkey}            | {@code vector<u8>} | New Ed25519 public key to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                                         |
     * | {@code validator_network_addresses} | {@code vector<u8>} | New set of {@code validator_network_addresses} to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                       |
     * | {@code fullnode_network_addresses}  | {@code vector<u8>} | New set of {@code fullnode_network_addresses} to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                        |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                   | Description                                                                                           |
     * | ----------------           | --------------                                 | -------------                                                                                         |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorConfig::EVALIDATOR_CONFIG}           | {@code validator_address} does not have a {@code ValidatorConfig::ValidatorConfig} resource published under it.   |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::EVALIDATOR_OPERATOR}                   | {@code validator_operator_account} does not have a Validator Operator role.                                 |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::EINVALID_TRANSACTION_SENDER} | {@code validator_operator_account} is not the registered operator for the validator at {@code validator_address}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::EINVALID_CONSENSUS_KEY}      | {@code consensus_pubkey} is not a valid ed25519 public key.                                                 |
     * | {@code Errors::INVALID_STATE}    | {@code DiemConfig::EINVALID_BLOCK_TIME}             | An invalid time value was encountered in reconfiguration. Unlikely to occur.                          |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     */
    public static final class SetValidatorConfigAndReconfigure extends ScriptCall {
        public final com.diem.types.AccountAddress validator_account;
        public final com.novi.serde.Bytes consensus_pubkey;
        public final com.novi.serde.Bytes validator_network_addresses;
        public final com.novi.serde.Bytes fullnode_network_addresses;

        public SetValidatorConfigAndReconfigure(com.diem.types.AccountAddress validator_account, com.novi.serde.Bytes consensus_pubkey, com.novi.serde.Bytes validator_network_addresses, com.novi.serde.Bytes fullnode_network_addresses) {
            java.util.Objects.requireNonNull(validator_account, "validator_account must not be null");
            java.util.Objects.requireNonNull(consensus_pubkey, "consensus_pubkey must not be null");
            java.util.Objects.requireNonNull(validator_network_addresses, "validator_network_addresses must not be null");
            java.util.Objects.requireNonNull(fullnode_network_addresses, "fullnode_network_addresses must not be null");
            this.validator_account = validator_account;
            this.consensus_pubkey = consensus_pubkey;
            this.validator_network_addresses = validator_network_addresses;
            this.fullnode_network_addresses = fullnode_network_addresses;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            SetValidatorConfigAndReconfigure other = (SetValidatorConfigAndReconfigure) obj;
            if (!java.util.Objects.equals(this.validator_account, other.validator_account)) { return false; }
            if (!java.util.Objects.equals(this.consensus_pubkey, other.consensus_pubkey)) { return false; }
            if (!java.util.Objects.equals(this.validator_network_addresses, other.validator_network_addresses)) { return false; }
            if (!java.util.Objects.equals(this.fullnode_network_addresses, other.fullnode_network_addresses)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.validator_account != null ? this.validator_account.hashCode() : 0);
            value = 31 * value + (this.consensus_pubkey != null ? this.consensus_pubkey.hashCode() : 0);
            value = 31 * value + (this.validator_network_addresses != null ? this.validator_network_addresses.hashCode() : 0);
            value = 31 * value + (this.fullnode_network_addresses != null ? this.fullnode_network_addresses.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.AccountAddress validator_account;
            public com.novi.serde.Bytes consensus_pubkey;
            public com.novi.serde.Bytes validator_network_addresses;
            public com.novi.serde.Bytes fullnode_network_addresses;

            public SetValidatorConfigAndReconfigure build() {
                return new SetValidatorConfigAndReconfigure(
                    validator_account,
                    consensus_pubkey,
                    validator_network_addresses,
                    fullnode_network_addresses
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Sets the validator operator for a validator in the validator's configuration resource "locally"
     * and does not reconfigure the system. Changes from this transaction will not picked up by the
     * system until a reconfiguration of the system is triggered. May only be sent by an account with
     * Validator role.
     *
     * <p><b>Technical Description</b>
     * Sets the account at {@code operator_account} address and with the specified {@code human_name} as an
     * operator for the sending validator account. The account at {@code operator_account} address must have
     * a Validator Operator role and have a {@code ValidatorOperatorConfig::ValidatorOperatorConfig}
     * resource published under it. The sending {@code account} must be a Validator and have a
     * {@code ValidatorConfig::ValidatorConfig} resource published under it. This script does not emit a
     * {@code DiemConfig::NewEpochEvent} and no reconfiguration of the system is initiated by this script.
     *
     * <p><b>Parameters</b>
     * | Name               | Type         | Description                                                                                  |
     * | ------             | ------       | -------------                                                                                |
     * | {@code account}          | {@code &signer}    | The signer reference of the sending account of the transaction.                              |
     * | {@code operator_name}    | {@code vector<u8>} | Validator operator's human name.                                                             |
     * | {@code operator_account} | {@code address}    | Address of the validator operator account to be added as the {@code account} validator's operator. |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                          | Description                                                                                                                                                  |
     * | ----------------           | --------------                                        | -------------                                                                                                                                                |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorOperatorConfig::EVALIDATOR_OPERATOR_CONFIG} | The {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource is not published under {@code operator_account}.                                                   |
     * | 0                          | 0                                                     | The {@code human_name} field of the {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource under {@code operator_account} does not match the provided {@code human_name}. |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::EVALIDATOR}                                   | {@code account} does not have a Validator account role.                                                                                                            |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::ENOT_A_VALIDATOR_OPERATOR}          | The account at {@code operator_account} does not have a {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource.                                               |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorConfig::EVALIDATOR_CONFIG}                  | A {@code ValidatorConfig::ValidatorConfig} is not published under {@code account}.                                                                                       |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     */
    public static final class SetValidatorOperator extends ScriptCall {
        public final com.novi.serde.Bytes operator_name;
        public final com.diem.types.AccountAddress operator_account;

        public SetValidatorOperator(com.novi.serde.Bytes operator_name, com.diem.types.AccountAddress operator_account) {
            java.util.Objects.requireNonNull(operator_name, "operator_name must not be null");
            java.util.Objects.requireNonNull(operator_account, "operator_account must not be null");
            this.operator_name = operator_name;
            this.operator_account = operator_account;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            SetValidatorOperator other = (SetValidatorOperator) obj;
            if (!java.util.Objects.equals(this.operator_name, other.operator_name)) { return false; }
            if (!java.util.Objects.equals(this.operator_account, other.operator_account)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.operator_name != null ? this.operator_name.hashCode() : 0);
            value = 31 * value + (this.operator_account != null ? this.operator_account.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.novi.serde.Bytes operator_name;
            public com.diem.types.AccountAddress operator_account;

            public SetValidatorOperator build() {
                return new SetValidatorOperator(
                    operator_name,
                    operator_account
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Sets the validator operator for a validator in the validator's configuration resource "locally"
     * and does not reconfigure the system. Changes from this transaction will not picked up by the
     * system until a reconfiguration of the system is triggered. May only be sent by the Diem Root
     * account as a write set transaction.
     *
     * <p><b>Technical Description</b>
     * Sets the account at {@code operator_account} address and with the specified {@code human_name} as an
     * operator for the validator {@code account}. The account at {@code operator_account} address must have a
     * Validator Operator role and have a {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource
     * published under it. The account represented by the {@code account} signer must be a Validator and
     * have a {@code ValidatorConfig::ValidatorConfig} resource published under it. No reconfiguration of
     * the system is initiated by this script.
     *
     * <p><b>Parameters</b>
     * | Name               | Type         | Description                                                                                                  |
     * | ------             | ------       | -------------                                                                                                |
     * | {@code dr_account}       | {@code &signer}    | The signer reference of the sending account of the write set transaction. May only be the Diem Root signer. |
     * | {@code account}          | {@code &signer}    | Signer reference of account specified in the {@code execute_as} field of the write set transaction.                |
     * | {@code sliding_nonce}    | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction for Diem Root.                    |
     * | {@code operator_name}    | {@code vector<u8>} | Validator operator's human name.                                                                             |
     * | {@code operator_account} | {@code address}    | Address of the validator operator account to be added as the {@code account} validator's operator.                 |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                          | Description                                                                                                                                                  |
     * | ----------------           | --------------                                        | -------------                                                                                                                                                |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                        | A {@code SlidingNonce} resource is not published under {@code dr_account}.                                                                                               |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                        | The {@code sliding_nonce} in {@code dr_account} is too old and it's impossible to determine if it's duplicated or not.                                                   |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                        | The {@code sliding_nonce} in {@code dr_account} is too far in the future.                                                                                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}               | The {@code sliding_nonce} in{@code  dr_account} has been previously recorded.                                                                                            |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                        | The sending account is not the Diem Root account or Treasury Compliance account                                                                             |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorOperatorConfig::EVALIDATOR_OPERATOR_CONFIG} | The {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource is not published under {@code operator_account}.                                                   |
     * | 0                          | 0                                                     | The {@code human_name} field of the {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource under {@code operator_account} does not match the provided {@code human_name}. |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::EVALIDATOR}                                   | {@code account} does not have a Validator account role.                                                                                                            |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::ENOT_A_VALIDATOR_OPERATOR}          | The account at {@code operator_account} does not have a {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource.                                               |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorConfig::EVALIDATOR_CONFIG}                  | A {@code ValidatorConfig::ValidatorConfig} is not published under {@code account}.                                                                                       |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     */
    public static final class SetValidatorOperatorWithNonceAdmin extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.novi.serde.Bytes operator_name;
        public final com.diem.types.AccountAddress operator_account;

        public SetValidatorOperatorWithNonceAdmin(@com.novi.serde.Unsigned Long sliding_nonce, com.novi.serde.Bytes operator_name, com.diem.types.AccountAddress operator_account) {
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(operator_name, "operator_name must not be null");
            java.util.Objects.requireNonNull(operator_account, "operator_account must not be null");
            this.sliding_nonce = sliding_nonce;
            this.operator_name = operator_name;
            this.operator_account = operator_account;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            SetValidatorOperatorWithNonceAdmin other = (SetValidatorOperatorWithNonceAdmin) obj;
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.operator_name, other.operator_name)) { return false; }
            if (!java.util.Objects.equals(this.operator_account, other.operator_account)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.operator_name != null ? this.operator_name.hashCode() : 0);
            value = 31 * value + (this.operator_account != null ? this.operator_account.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.novi.serde.Bytes operator_name;
            public com.diem.types.AccountAddress operator_account;

            public SetValidatorOperatorWithNonceAdmin build() {
                return new SetValidatorOperatorWithNonceAdmin(
                    sliding_nonce,
                    operator_name,
                    operator_account
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Mints a specified number of coins in a currency to a Designated Dealer. The sending account
     * must be the Treasury Compliance account, and coins can only be minted to a Designated Dealer
     * account.
     *
     * <p><b>Technical Description</b>
     * Mints {@code mint_amount} of coins in the {@code CoinType} currency to Designated Dealer account at
     * {@code designated_dealer_address}. The {@code tier_index} parameter specifies which tier should be used to
     * check verify the off-chain approval policy, and is based in part on the on-chain tier values
     * for the specific Designated Dealer, and the number of {@code CoinType} coins that have been minted to
     * the dealer over the past 24 hours. Every Designated Dealer has 4 tiers for each currency that
     * they support. The sending {@code tc_account} must be the Treasury Compliance account, and the
     * receiver an authorized Designated Dealer account.
     *
     * <p><b>Events</b>
     * Successful execution of the transaction will emit two events:
     * <ul><li>A {@code Diem::MintEvent} with the amount and currency code minted is emitted on the</li></ul>
     * {@code mint_event_handle} in the stored {@code Diem::CurrencyInfo<CoinType>} resource stored under
     * {@code 0xA550C18}; and
     * <ul><li>A {@code DesignatedDealer::ReceivedMintEvent} with the amount, currency code, and Designated</li></ul>
     * Dealer's address is emitted on the {@code mint_event_handle} in the stored {@code DesignatedDealer::Dealer}
     * resource published under the {@code designated_dealer_address}.
     *
     * <p><b>Parameters</b>
     * | Name                        | Type      | Description                                                                                                |
     * | ------                      | ------    | -------------                                                                                              |
     * | {@code CoinType}                  | Type      | The Move type for the {@code CoinType} being minted. {@code CoinType} must be an already-registered currency on-chain. |
     * | {@code tc_account}                | {@code &signer} | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account.  |
     * | {@code sliding_nonce}             | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                 |
     * | {@code designated_dealer_address} | {@code address} | The address of the Designated Dealer account being minted to.                                              |
     * | {@code mint_amount}               | {@code u64}     | The number of coins to be minted.                                                                          |
     * | {@code tier_index}                | {@code u64}     | The mint tier index to use for the Designated Dealer account.                                              |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category                | Error Reason                                 | Description                                                                                                                  |
     * | ----------------              | --------------                               | -------------                                                                                                                |
     * | {@code Errors::NOT_PUBLISHED}       | {@code SlidingNonce::ESLIDING_NONCE}               | A {@code SlidingNonce} resource is not published under {@code tc_account}.                                                               |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code SlidingNonce::ENONCE_TOO_OLD}               | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not.                                   |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code SlidingNonce::ENONCE_TOO_NEW}               | The {@code sliding_nonce} is too far in the future.                                                                                |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}      | The {@code sliding_nonce} has been previously recorded.                                                                            |
     * | {@code Errors::REQUIRES_ADDRESS}    | {@code CoreAddresses::ETREASURY_COMPLIANCE}        | {@code tc_account} is not the Treasury Compliance account.                                                                         |
     * | {@code Errors::REQUIRES_ROLE}       | {@code Roles::ETREASURY_COMPLIANCE}                | {@code tc_account} is not the Treasury Compliance account.                                                                         |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code DesignatedDealer::EINVALID_MINT_AMOUNT}     | {@code mint_amount} is zero.                                                                                                       |
     * | {@code Errors::NOT_PUBLISHED}       | {@code DesignatedDealer::EDEALER}                  | {@code DesignatedDealer::Dealer} or {@code DesignatedDealer::TierInfo<CoinType>} resource does not exist at {@code designated_dealer_address}. |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code DesignatedDealer::EINVALID_TIER_INDEX}      | The {@code tier_index} is out of bounds.                                                                                           |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code DesignatedDealer::EINVALID_AMOUNT_FOR_TIER} | {@code mint_amount} exceeds the maximum allowed amount for {@code tier_index}.                                                           |
     * | {@code Errors::REQUIRES_CAPABILITY} | {@code Diem::EMINT_CAPABILITY}                    | {@code tc_account} does not have a {@code Diem::MintCapability<CoinType>} resource published under it.                                  |
     * | {@code Errors::INVALID_STATE}       | {@code Diem::EMINTING_NOT_ALLOWED}                | Minting is not currently allowed for {@code CoinType} coins.                                                                       |
     * | {@code Errors::LIMIT_EXCEEDED}      | {@code DiemAccount::EDEPOSIT_EXCEEDS_LIMITS}      | The depositing of the funds would exceed the {@code account}'s account limits.                                                     |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::create_designated_dealer}</li></ul>
     * <ul><li>{@code Script::peer_to_peer_with_metadata}</li></ul>
     * <ul><li>{@code Script::rotate_dual_attestation_info}</li></ul>
     */
    public static final class TieredMint extends ScriptCall {
        public final com.diem.types.TypeTag coin_type;
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.diem.types.AccountAddress designated_dealer_address;
        public final @com.novi.serde.Unsigned Long mint_amount;
        public final @com.novi.serde.Unsigned Long tier_index;

        public TieredMint(com.diem.types.TypeTag coin_type, @com.novi.serde.Unsigned Long sliding_nonce, com.diem.types.AccountAddress designated_dealer_address, @com.novi.serde.Unsigned Long mint_amount, @com.novi.serde.Unsigned Long tier_index) {
            java.util.Objects.requireNonNull(coin_type, "coin_type must not be null");
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(designated_dealer_address, "designated_dealer_address must not be null");
            java.util.Objects.requireNonNull(mint_amount, "mint_amount must not be null");
            java.util.Objects.requireNonNull(tier_index, "tier_index must not be null");
            this.coin_type = coin_type;
            this.sliding_nonce = sliding_nonce;
            this.designated_dealer_address = designated_dealer_address;
            this.mint_amount = mint_amount;
            this.tier_index = tier_index;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            TieredMint other = (TieredMint) obj;
            if (!java.util.Objects.equals(this.coin_type, other.coin_type)) { return false; }
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.designated_dealer_address, other.designated_dealer_address)) { return false; }
            if (!java.util.Objects.equals(this.mint_amount, other.mint_amount)) { return false; }
            if (!java.util.Objects.equals(this.tier_index, other.tier_index)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.coin_type != null ? this.coin_type.hashCode() : 0);
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.designated_dealer_address != null ? this.designated_dealer_address.hashCode() : 0);
            value = 31 * value + (this.mint_amount != null ? this.mint_amount.hashCode() : 0);
            value = 31 * value + (this.tier_index != null ? this.tier_index.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.TypeTag coin_type;
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.diem.types.AccountAddress designated_dealer_address;
            public @com.novi.serde.Unsigned Long mint_amount;
            public @com.novi.serde.Unsigned Long tier_index;

            public TieredMint build() {
                return new TieredMint(
                    coin_type,
                    sliding_nonce,
                    designated_dealer_address,
                    mint_amount,
                    tier_index
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Unfreezes the account at {@code address}. The sending account of this transaction must be the
     * Treasury Compliance account. After the successful execution of this transaction transactions
     * may be sent from the previously frozen account, and coins may be sent and received.
     *
     * <p><b>Technical Description</b>
     * Sets the {@code AccountFreezing::FreezingBit} to {@code false} and emits a
     * {@code AccountFreezing::UnFreezeAccountEvent}. The transaction sender must be the Treasury Compliance
     * account. Note that this is a per-account property so unfreezing a Parent VASP will not effect
     * the status any of its child accounts and vice versa.
     *
     * <p><b>Events</b>
     * Successful execution of this script will emit a {@code AccountFreezing::UnFreezeAccountEvent} with
     * the {@code unfrozen_address} set the {@code to_unfreeze_account}'s address.
     *
     * <p><b>Parameters</b>
     * | Name                  | Type      | Description                                                                                               |
     * | ------                | ------    | -------------                                                                                             |
     * | {@code tc_account}          | {@code &signer} | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account. |
     * | {@code sliding_nonce}       | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                |
     * | {@code to_unfreeze_account} | {@code address} | The account address to be frozen.                                                                         |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                            | Description                                                                                |
     * | ----------------           | --------------                          | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code account}.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE}   | The sending account is not the Treasury Compliance account.                                |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::freeze_account}</li></ul>
     */
    public static final class UnfreezeAccount extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.diem.types.AccountAddress to_unfreeze_account;

        public UnfreezeAccount(@com.novi.serde.Unsigned Long sliding_nonce, com.diem.types.AccountAddress to_unfreeze_account) {
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(to_unfreeze_account, "to_unfreeze_account must not be null");
            this.sliding_nonce = sliding_nonce;
            this.to_unfreeze_account = to_unfreeze_account;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            UnfreezeAccount other = (UnfreezeAccount) obj;
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.to_unfreeze_account, other.to_unfreeze_account)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.to_unfreeze_account != null ? this.to_unfreeze_account.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public com.diem.types.AccountAddress to_unfreeze_account;

            public UnfreezeAccount build() {
                return new UnfreezeAccount(
                    sliding_nonce,
                    to_unfreeze_account
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Updates the Diem major version that is stored on-chain and is used by the VM.  This
     * transaction can only be sent from the Diem Root account.
     *
     * <p><b>Technical Description</b>
     * Updates the {@code DiemVersion} on-chain config and emits a {@code DiemConfig::NewEpochEvent} to trigger
     * a reconfiguration of the system. The {@code major} version that is passed in must be strictly greater
     * than the current major version held on-chain. The VM reads this information and can use it to
     * preserve backwards compatibility with previous major versions of the VM.
     *
     * <p><b>Parameters</b>
     * | Name            | Type      | Description                                                                |
     * | ------          | ------    | -------------                                                              |
     * | {@code account}       | {@code &signer} | Signer reference of the sending account. Must be the Diem Root account.   |
     * | {@code sliding_nonce} | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction. |
     * | {@code major}         | {@code u64}     | The {@code major} version of the VM to be used from this transaction on.         |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                                  | Description                                                                                |
     * | ----------------           | --------------                                | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                | A {@code SlidingNonce} resource is not published under {@code account}.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}       | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::EDIEM_ROOT}                  | {@code account} is not the Diem Root account.                                                   |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemVersion::EINVALID_MAJOR_VERSION_NUMBER} | {@code major} is less-than or equal to the current major version stored on-chain.                |
     */
    public static final class UpdateDiemVersion extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final @com.novi.serde.Unsigned Long major;

        public UpdateDiemVersion(@com.novi.serde.Unsigned Long sliding_nonce, @com.novi.serde.Unsigned Long major) {
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(major, "major must not be null");
            this.sliding_nonce = sliding_nonce;
            this.major = major;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            UpdateDiemVersion other = (UpdateDiemVersion) obj;
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.major, other.major)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.major != null ? this.major.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public @com.novi.serde.Unsigned Long major;

            public UpdateDiemVersion build() {
                return new UpdateDiemVersion(
                    sliding_nonce,
                    major
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Update the dual attestation limit on-chain. Defined in terms of micro-XDX.  The transaction can
     * only be sent by the Treasury Compliance account.  After this transaction all inter-VASP
     * payments over this limit must be checked for dual attestation.
     *
     * <p><b>Technical Description</b>
     * Updates the {@code micro_xdx_limit} field of the {@code DualAttestation::Limit} resource published under
     * {@code 0xA550C18}. The amount is set in micro-XDX.
     *
     * <p><b>Parameters</b>
     * | Name                  | Type      | Description                                                                                               |
     * | ------                | ------    | -------------                                                                                             |
     * | {@code tc_account}          | {@code &signer} | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account. |
     * | {@code sliding_nonce}       | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                |
     * | {@code new_micro_xdx_limit} | {@code u64}     | The new dual attestation limit to be used on-chain.                                                       |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                            | Description                                                                                |
     * | ----------------           | --------------                          | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code tc_account}.                             |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE}   | {@code tc_account} is not the Treasury Compliance account.                                       |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::update_exchange_rate}</li></ul>
     * <ul><li>{@code Script::update_minting_ability}</li></ul>
     */
    public static final class UpdateDualAttestationLimit extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final @com.novi.serde.Unsigned Long new_micro_xdx_limit;

        public UpdateDualAttestationLimit(@com.novi.serde.Unsigned Long sliding_nonce, @com.novi.serde.Unsigned Long new_micro_xdx_limit) {
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(new_micro_xdx_limit, "new_micro_xdx_limit must not be null");
            this.sliding_nonce = sliding_nonce;
            this.new_micro_xdx_limit = new_micro_xdx_limit;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            UpdateDualAttestationLimit other = (UpdateDualAttestationLimit) obj;
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.new_micro_xdx_limit, other.new_micro_xdx_limit)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.new_micro_xdx_limit != null ? this.new_micro_xdx_limit.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public @com.novi.serde.Unsigned Long new_micro_xdx_limit;

            public UpdateDualAttestationLimit build() {
                return new UpdateDualAttestationLimit(
                    sliding_nonce,
                    new_micro_xdx_limit
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Update the rough on-chain exchange rate between a specified currency and XDX (as a conversion
     * to micro-XDX). The transaction can only be sent by the Treasury Compliance account. After this
     * transaction the updated exchange rate will be used for normalization of gas prices, and for
     * dual attestation checking.
     *
     * <p><b>Technical Description</b>
     * Updates the on-chain exchange rate from the given {@code Currency} to micro-XDX.  The exchange rate
     * is given by {@code new_exchange_rate_numerator/new_exchange_rate_denominator}.
     *
     * <p><b>Parameters</b>
     * | Name                            | Type      | Description                                                                                                                        |
     * | ------                          | ------    | -------------                                                                                                                      |
     * | {@code Currency}                      | Type      | The Move type for the {@code Currency} whose exchange rate is being updated. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code tc_account}                    | {@code &signer} | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account.                          |
     * | {@code sliding_nonce}                 | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for the transaction.                                                          |
     * | {@code new_exchange_rate_numerator}   | {@code u64}     | The numerator for the new to micro-XDX exchange rate for {@code Currency}.                                                               |
     * | {@code new_exchange_rate_denominator} | {@code u64}     | The denominator for the new to micro-XDX exchange rate for {@code Currency}.                                                             |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                            | Description                                                                                |
     * | ----------------           | --------------                          | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code tc_account}.                             |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE}   | {@code tc_account} is not the Treasury Compliance account.                                       |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::ETREASURY_COMPLIANCE}           | {@code tc_account} is not the Treasury Compliance account.                                       |
     * | {@code Errors::INVALID_ARGUMENT} | {@code FixedPoint32::EDENOMINATOR}            | {@code new_exchange_rate_denominator} is zero.                                                   |
     * | {@code Errors::INVALID_ARGUMENT} | {@code FixedPoint32::ERATIO_OUT_OF_RANGE}     | The quotient is unrepresentable as a {@code FixedPoint32}.                                       |
     * | {@code Errors::LIMIT_EXCEEDED}   | {@code FixedPoint32::ERATIO_OUT_OF_RANGE}     | The quotient is unrepresentable as a {@code FixedPoint32}.                                       |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::update_dual_attestation_limit}</li></ul>
     * <ul><li>{@code Script::update_minting_ability}</li></ul>
     */
    public static final class UpdateExchangeRate extends ScriptCall {
        public final com.diem.types.TypeTag currency;
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final @com.novi.serde.Unsigned Long new_exchange_rate_numerator;
        public final @com.novi.serde.Unsigned Long new_exchange_rate_denominator;

        public UpdateExchangeRate(com.diem.types.TypeTag currency, @com.novi.serde.Unsigned Long sliding_nonce, @com.novi.serde.Unsigned Long new_exchange_rate_numerator, @com.novi.serde.Unsigned Long new_exchange_rate_denominator) {
            java.util.Objects.requireNonNull(currency, "currency must not be null");
            java.util.Objects.requireNonNull(sliding_nonce, "sliding_nonce must not be null");
            java.util.Objects.requireNonNull(new_exchange_rate_numerator, "new_exchange_rate_numerator must not be null");
            java.util.Objects.requireNonNull(new_exchange_rate_denominator, "new_exchange_rate_denominator must not be null");
            this.currency = currency;
            this.sliding_nonce = sliding_nonce;
            this.new_exchange_rate_numerator = new_exchange_rate_numerator;
            this.new_exchange_rate_denominator = new_exchange_rate_denominator;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            UpdateExchangeRate other = (UpdateExchangeRate) obj;
            if (!java.util.Objects.equals(this.currency, other.currency)) { return false; }
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.new_exchange_rate_numerator, other.new_exchange_rate_numerator)) { return false; }
            if (!java.util.Objects.equals(this.new_exchange_rate_denominator, other.new_exchange_rate_denominator)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.currency != null ? this.currency.hashCode() : 0);
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.new_exchange_rate_numerator != null ? this.new_exchange_rate_numerator.hashCode() : 0);
            value = 31 * value + (this.new_exchange_rate_denominator != null ? this.new_exchange_rate_denominator.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.TypeTag currency;
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public @com.novi.serde.Unsigned Long new_exchange_rate_numerator;
            public @com.novi.serde.Unsigned Long new_exchange_rate_denominator;

            public UpdateExchangeRate build() {
                return new UpdateExchangeRate(
                    currency,
                    sliding_nonce,
                    new_exchange_rate_numerator,
                    new_exchange_rate_denominator
                );
            }
        }
    }

    /**
     * <p><b>Summary</b>
     * Script to allow or disallow minting of new coins in a specified currency.  This transaction can
     * only be sent by the Treasury Compliance account.  Turning minting off for a currency will have
     * no effect on coins already in circulation, and coins may still be removed from the system.
     *
     * <p><b>Technical Description</b>
     * This transaction sets the {@code can_mint} field of the {@code Diem::CurrencyInfo<Currency>} resource
     * published under {@code 0xA550C18} to the value of {@code allow_minting}. Minting of coins if allowed if
     * this field is set to {@code true} and minting of new coins in {@code Currency} is disallowed otherwise.
     * This transaction needs to be sent by the Treasury Compliance account.
     *
     * <p><b>Parameters</b>
     * | Name            | Type      | Description                                                                                                                          |
     * | ------          | ------    | -------------                                                                                                                        |
     * | {@code Currency}      | Type      | The Move type for the {@code Currency} whose minting ability is being updated. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code account}       | {@code &signer} | Signer reference of the sending account. Must be the Diem Root account.                                                             |
     * | {@code allow_minting} | {@code bool}    | Whether to allow minting of new coins in {@code Currency}.                                                                                 |
     *
     * <p><b>Common Abort Conditions</b>
     * | Error Category             | Error Reason                          | Description                                          |
     * | ----------------           | --------------                        | -------------                                        |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE} | {@code tc_account} is not the Treasury Compliance account. |
     * | {@code Errors::NOT_PUBLISHED}    | {@code Diem::ECURRENCY_INFO}               | {@code Currency} is not a registered currency on-chain.    |
     *
     * <p><b>Related Scripts</b>
     * <ul><li>{@code Script::update_dual_attestation_limit}</li></ul>
     * <ul><li>{@code Script::update_exchange_rate}</li></ul>
     */
    public static final class UpdateMintingAbility extends ScriptCall {
        public final com.diem.types.TypeTag currency;
        public final Boolean allow_minting;

        public UpdateMintingAbility(com.diem.types.TypeTag currency, Boolean allow_minting) {
            java.util.Objects.requireNonNull(currency, "currency must not be null");
            java.util.Objects.requireNonNull(allow_minting, "allow_minting must not be null");
            this.currency = currency;
            this.allow_minting = allow_minting;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            UpdateMintingAbility other = (UpdateMintingAbility) obj;
            if (!java.util.Objects.equals(this.currency, other.currency)) { return false; }
            if (!java.util.Objects.equals(this.allow_minting, other.allow_minting)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.currency != null ? this.currency.hashCode() : 0);
            value = 31 * value + (this.allow_minting != null ? this.allow_minting.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.TypeTag currency;
            public Boolean allow_minting;

            public UpdateMintingAbility build() {
                return new UpdateMintingAbility(
                    currency,
                    allow_minting
                );
            }
        }
    }
}

