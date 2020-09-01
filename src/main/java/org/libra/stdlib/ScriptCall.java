package org.libra.stdlib;

import java.math.BigInteger;


public abstract class ScriptCall {

    /**
     * Add a `Currency` balance to `account`, which will enable `account` to send and receive
     * `Libra<Currency>`.
     * Aborts with NOT_A_CURRENCY if `Currency` is not an accepted currency type in the Libra system
     * Aborts with `LibraAccount::ADD_EXISTING_CURRENCY` if the account already holds a balance in
     * `Currency`.
     */
    public static final class AddCurrencyToAccount extends ScriptCall {
        public final org.libra.types.TypeTag currency;

        public AddCurrencyToAccount(org.libra.types.TypeTag currency) {
            assert currency != null;
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
            public org.libra.types.TypeTag currency;

            public AddCurrencyToAccount build() {
                return new AddCurrencyToAccount(
                    currency
                );
            }
        }
    }

    /**
     * Add the `KeyRotationCapability` for `to_recover_account` to the `RecoveryAddress` resource under `recovery_address`.
     *
     * ## Aborts
     * * Aborts with `LibraAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED` if `account` has already delegated its `KeyRotationCapability`.
     * * Aborts with `RecoveryAddress:ENOT_A_RECOVERY_ADDRESS` if `recovery_address` does not have a `RecoveryAddress` resource.
     * * Aborts with `RecoveryAddress::EINVALID_KEY_ROTATION_DELEGATION` if `to_recover_account` and `recovery_address` do not belong to the same VASP.
     */
    public static final class AddRecoveryRotationCapability extends ScriptCall {
        public final org.libra.types.AccountAddress recovery_address;

        public AddRecoveryRotationCapability(org.libra.types.AccountAddress recovery_address) {
            assert recovery_address != null;
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
            public org.libra.types.AccountAddress recovery_address;

            public AddRecoveryRotationCapability build() {
                return new AddRecoveryRotationCapability(
                    recovery_address
                );
            }
        }
    }

    /**
     * Append the `hash` to script hashes list allowed to be executed by the network.
     */
    public static final class AddToScriptAllowList extends ScriptCall {
        public final com.novi.serde.Bytes hash;
        public final @com.novi.serde.Unsigned Long sliding_nonce;

        public AddToScriptAllowList(com.novi.serde.Bytes hash, @com.novi.serde.Unsigned Long sliding_nonce) {
            assert hash != null;
            assert sliding_nonce != null;
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
     * Add `new_validator` to the validator set.
     * Fails if the `new_validator` address is already in the validator set
     * or does not have a `ValidatorConfig` resource stored at the address.
     * Emits a NewEpochEvent.
     */
    public static final class AddValidatorAndReconfigure extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.novi.serde.Bytes validator_name;
        public final org.libra.types.AccountAddress validator_address;

        public AddValidatorAndReconfigure(@com.novi.serde.Unsigned Long sliding_nonce, com.novi.serde.Bytes validator_name, org.libra.types.AccountAddress validator_address) {
            assert sliding_nonce != null;
            assert validator_name != null;
            assert validator_address != null;
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
            public org.libra.types.AccountAddress validator_address;

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
     * Permanently destroy the `Token`s stored in the oldest burn request under the `Preburn` resource.
     * This will only succeed if `account` has a `MintCapability<Token>`, a `Preburn<Token>` resource
     * exists under `preburn_address`, and there is a pending burn request.
     * sliding_nonce is a unique nonce for operation, see sliding_nonce.move for details
     */
    public static final class Burn extends ScriptCall {
        public final org.libra.types.TypeTag token;
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final org.libra.types.AccountAddress preburn_address;

        public Burn(org.libra.types.TypeTag token, @com.novi.serde.Unsigned Long sliding_nonce, org.libra.types.AccountAddress preburn_address) {
            assert token != null;
            assert sliding_nonce != null;
            assert preburn_address != null;
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
            public org.libra.types.TypeTag token;
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public org.libra.types.AccountAddress preburn_address;

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
     * Burn transaction fees that have been collected in the given `currency`
     * and relinquish to the association. The currency must be non-synthetic.
     */
    public static final class BurnTxnFees extends ScriptCall {
        public final org.libra.types.TypeTag coin_type;

        public BurnTxnFees(org.libra.types.TypeTag coin_type) {
            assert coin_type != null;
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
            public org.libra.types.TypeTag coin_type;

            public BurnTxnFees build() {
                return new BurnTxnFees(
                    coin_type
                );
            }
        }
    }

    /**
     * Cancel the oldest burn request from `preburn_address` and return the funds.
     * Fails if the sender does not have a published `BurnCapability<Token>`.
     */
    public static final class CancelBurn extends ScriptCall {
        public final org.libra.types.TypeTag token;
        public final org.libra.types.AccountAddress preburn_address;

        public CancelBurn(org.libra.types.TypeTag token, org.libra.types.AccountAddress preburn_address) {
            assert token != null;
            assert preburn_address != null;
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
            public org.libra.types.TypeTag token;
            public org.libra.types.AccountAddress preburn_address;

            public CancelBurn build() {
                return new CancelBurn(
                    token,
                    preburn_address
                );
            }
        }
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
    public static final class CreateChildVaspAccount extends ScriptCall {
        public final org.libra.types.TypeTag coin_type;
        public final org.libra.types.AccountAddress child_address;
        public final com.novi.serde.Bytes auth_key_prefix;
        public final Boolean add_all_currencies;
        public final @com.novi.serde.Unsigned Long child_initial_balance;

        public CreateChildVaspAccount(org.libra.types.TypeTag coin_type, org.libra.types.AccountAddress child_address, com.novi.serde.Bytes auth_key_prefix, Boolean add_all_currencies, @com.novi.serde.Unsigned Long child_initial_balance) {
            assert coin_type != null;
            assert child_address != null;
            assert auth_key_prefix != null;
            assert add_all_currencies != null;
            assert child_initial_balance != null;
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
            public org.libra.types.TypeTag coin_type;
            public org.libra.types.AccountAddress child_address;
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
     * Create an account with the DesignatedDealer role at `addr` with authentication key
     * `auth_key_prefix` | `addr` and a 0 balance of type `Currency`. If `add_all_currencies` is true,
     * 0 balances for all available currencies in the system will also be added. This can only be
     * invoked by an account with the TreasuryCompliance role.
     */
    public static final class CreateDesignatedDealer extends ScriptCall {
        public final org.libra.types.TypeTag currency;
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final org.libra.types.AccountAddress addr;
        public final com.novi.serde.Bytes auth_key_prefix;
        public final com.novi.serde.Bytes human_name;
        public final Boolean add_all_currencies;

        public CreateDesignatedDealer(org.libra.types.TypeTag currency, @com.novi.serde.Unsigned Long sliding_nonce, org.libra.types.AccountAddress addr, com.novi.serde.Bytes auth_key_prefix, com.novi.serde.Bytes human_name, Boolean add_all_currencies) {
            assert currency != null;
            assert sliding_nonce != null;
            assert addr != null;
            assert auth_key_prefix != null;
            assert human_name != null;
            assert add_all_currencies != null;
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
            public org.libra.types.TypeTag currency;
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public org.libra.types.AccountAddress addr;
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
     * Create an account with the ParentVASP role at `address` with authentication key
     * `auth_key_prefix` | `new_account_address` and a 0 balance of type `currency`. If
     * `add_all_currencies` is true, 0 balances for all available currencies in the system will
     * also be added. This can only be invoked by an TreasuryCompliance account.
     * `sliding_nonce` is a unique nonce for operation, see sliding_nonce.move for details.
     */
    public static final class CreateParentVaspAccount extends ScriptCall {
        public final org.libra.types.TypeTag coin_type;
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final org.libra.types.AccountAddress new_account_address;
        public final com.novi.serde.Bytes auth_key_prefix;
        public final com.novi.serde.Bytes human_name;
        public final Boolean add_all_currencies;

        public CreateParentVaspAccount(org.libra.types.TypeTag coin_type, @com.novi.serde.Unsigned Long sliding_nonce, org.libra.types.AccountAddress new_account_address, com.novi.serde.Bytes auth_key_prefix, com.novi.serde.Bytes human_name, Boolean add_all_currencies) {
            assert coin_type != null;
            assert sliding_nonce != null;
            assert new_account_address != null;
            assert auth_key_prefix != null;
            assert human_name != null;
            assert add_all_currencies != null;
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
            public org.libra.types.TypeTag coin_type;
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public org.libra.types.AccountAddress new_account_address;
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
     * Extract the `KeyRotationCapability` for `recovery_account` and publish it in a
     * `RecoveryAddress` resource under  `account`.
     * ## Aborts
     * * Aborts with `LibraAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED` if `account` has already delegated its `KeyRotationCapability`.
     * * Aborts with `RecoveryAddress::ENOT_A_VASP` if `account` is not a ParentVASP or ChildVASP
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
     * Create a validator account at `new_validator_address` with `auth_key_prefix`and human_name.
     */
    public static final class CreateValidatorAccount extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final org.libra.types.AccountAddress new_account_address;
        public final com.novi.serde.Bytes auth_key_prefix;
        public final com.novi.serde.Bytes human_name;

        public CreateValidatorAccount(@com.novi.serde.Unsigned Long sliding_nonce, org.libra.types.AccountAddress new_account_address, com.novi.serde.Bytes auth_key_prefix, com.novi.serde.Bytes human_name) {
            assert sliding_nonce != null;
            assert new_account_address != null;
            assert auth_key_prefix != null;
            assert human_name != null;
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
            public org.libra.types.AccountAddress new_account_address;
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
     * Create a validator operator account at `new_validator_address` with `auth_key_prefix`and human_name.
     */
    public static final class CreateValidatorOperatorAccount extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final org.libra.types.AccountAddress new_account_address;
        public final com.novi.serde.Bytes auth_key_prefix;
        public final com.novi.serde.Bytes human_name;

        public CreateValidatorOperatorAccount(@com.novi.serde.Unsigned Long sliding_nonce, org.libra.types.AccountAddress new_account_address, com.novi.serde.Bytes auth_key_prefix, com.novi.serde.Bytes human_name) {
            assert sliding_nonce != null;
            assert new_account_address != null;
            assert auth_key_prefix != null;
            assert human_name != null;
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
            public org.libra.types.AccountAddress new_account_address;
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
     * Freeze account `address`. Initiator must be authorized.
     * `sliding_nonce` is a unique nonce for operation, see sliding_nonce.move for details.
     */
    public static final class FreezeAccount extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final org.libra.types.AccountAddress to_freeze_account;

        public FreezeAccount(@com.novi.serde.Unsigned Long sliding_nonce, org.libra.types.AccountAddress to_freeze_account) {
            assert sliding_nonce != null;
            assert to_freeze_account != null;
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
            public org.libra.types.AccountAddress to_freeze_account;

            public FreezeAccount build() {
                return new FreezeAccount(
                    sliding_nonce,
                    to_freeze_account
                );
            }
        }
    }

    /**
     * Mint `amount_lbr` LBR from the sending account's constituent coins and deposits the
     * resulting LBR into the sending account.
     */
    public static final class MintLbr extends ScriptCall {
        public final @com.novi.serde.Unsigned Long amount_lbr;

        public MintLbr(@com.novi.serde.Unsigned Long amount_lbr) {
            assert amount_lbr != null;
            this.amount_lbr = amount_lbr;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            MintLbr other = (MintLbr) obj;
            if (!java.util.Objects.equals(this.amount_lbr, other.amount_lbr)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.amount_lbr != null ? this.amount_lbr.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long amount_lbr;

            public MintLbr build() {
                return new MintLbr(
                    amount_lbr
                );
            }
        }
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
    public static final class PeerToPeerWithMetadata extends ScriptCall {
        public final org.libra.types.TypeTag currency;
        public final org.libra.types.AccountAddress payee;
        public final @com.novi.serde.Unsigned Long amount;
        public final com.novi.serde.Bytes metadata;
        public final com.novi.serde.Bytes metadata_signature;

        public PeerToPeerWithMetadata(org.libra.types.TypeTag currency, org.libra.types.AccountAddress payee, @com.novi.serde.Unsigned Long amount, com.novi.serde.Bytes metadata, com.novi.serde.Bytes metadata_signature) {
            assert currency != null;
            assert payee != null;
            assert amount != null;
            assert metadata != null;
            assert metadata_signature != null;
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
            public org.libra.types.TypeTag currency;
            public org.libra.types.AccountAddress payee;
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
     * Preburn `amount` `Token`s from `account`.
     * This will only succeed if `account` already has a published `Preburn<Token>` resource.
     */
    public static final class Preburn extends ScriptCall {
        public final org.libra.types.TypeTag token;
        public final @com.novi.serde.Unsigned Long amount;

        public Preburn(org.libra.types.TypeTag token, @com.novi.serde.Unsigned Long amount) {
            assert token != null;
            assert amount != null;
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
            public org.libra.types.TypeTag token;
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
     * (1) Rotate the authentication key of the sender to `public_key`
     * (2) Publish a resource containing a 32-byte ed25519 public key and the rotation capability
     *     of the sender under the sender's address.
     * Aborts if the sender already has a `SharedEd25519PublicKey` resource.
     * Aborts if the length of `new_public_key` is not 32.
     */
    public static final class PublishSharedEd25519PublicKey extends ScriptCall {
        public final com.novi.serde.Bytes public_key;

        public PublishSharedEd25519PublicKey(com.novi.serde.Bytes public_key) {
            assert public_key != null;
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
     * Set validator's config locally.
     * Does not emit NewEpochEvent, the config is NOT changed in the validator set.
     */
    public static final class RegisterValidatorConfig extends ScriptCall {
        public final org.libra.types.AccountAddress validator_account;
        public final com.novi.serde.Bytes consensus_pubkey;
        public final com.novi.serde.Bytes validator_network_addresses;
        public final com.novi.serde.Bytes fullnode_network_addresses;

        public RegisterValidatorConfig(org.libra.types.AccountAddress validator_account, com.novi.serde.Bytes consensus_pubkey, com.novi.serde.Bytes validator_network_addresses, com.novi.serde.Bytes fullnode_network_addresses) {
            assert validator_account != null;
            assert consensus_pubkey != null;
            assert validator_network_addresses != null;
            assert fullnode_network_addresses != null;
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
            public org.libra.types.AccountAddress validator_account;
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
     * Removes a validator from the validator set.
     * Fails if the validator_address is not in the validator set.
     * Emits a NewEpochEvent.
     */
    public static final class RemoveValidatorAndReconfigure extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.novi.serde.Bytes validator_name;
        public final org.libra.types.AccountAddress validator_address;

        public RemoveValidatorAndReconfigure(@com.novi.serde.Unsigned Long sliding_nonce, com.novi.serde.Bytes validator_name, org.libra.types.AccountAddress validator_address) {
            assert sliding_nonce != null;
            assert validator_name != null;
            assert validator_address != null;
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
            public org.libra.types.AccountAddress validator_address;

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
     * Rotate the sender's authentication key to `new_key`.
     * `new_key` should be a 256 bit sha3 hash of an ed25519 public key.
     * * Aborts with `LibraAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED` if the `KeyRotationCapability` for `account` has already been extracted.
     * * Aborts with `0` if the key rotation capability held by the account doesn't match the sender's address.
     * * Aborts with `LibraAccount::EMALFORMED_AUTHENTICATION_KEY` if the length of `new_key` != 32.
     */
    public static final class RotateAuthenticationKey extends ScriptCall {
        public final com.novi.serde.Bytes new_key;

        public RotateAuthenticationKey(com.novi.serde.Bytes new_key) {
            assert new_key != null;
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
     * Rotate `account`'s authentication key to `new_key`.
     * `new_key` should be a 256 bit sha3 hash of an ed25519 public key. This script also takes
     * `sliding_nonce`, as a unique nonce for this operation. See sliding_nonce.move for details.
     */
    public static final class RotateAuthenticationKeyWithNonce extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.novi.serde.Bytes new_key;

        public RotateAuthenticationKeyWithNonce(@com.novi.serde.Unsigned Long sliding_nonce, com.novi.serde.Bytes new_key) {
            assert sliding_nonce != null;
            assert new_key != null;
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
     * Rotate `account`'s authentication key to `new_key`.
     * `new_key` should be a 256 bit sha3 hash of an ed25519 public key. This script also takes
     * `sliding_nonce`, as a unique nonce for this operation. See sliding_nonce.move for details.
     */
    public static final class RotateAuthenticationKeyWithNonceAdmin extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.novi.serde.Bytes new_key;

        public RotateAuthenticationKeyWithNonceAdmin(@com.novi.serde.Unsigned Long sliding_nonce, com.novi.serde.Bytes new_key) {
            assert sliding_nonce != null;
            assert new_key != null;
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
     * Rotate the authentication key of `account` to `new_key` using the `KeyRotationCapability`
     * stored under `recovery_address`.
     *
     * ## Aborts
     * * Aborts with `RecoveryAddress::ENOT_A_RECOVERY_ADDRESS` if `recovery_address` does not have a `RecoveryAddress` resource
     * * Aborts with `RecoveryAddress::ECANNOT_ROTATE_KEY` if `account` is not `recovery_address` or `to_recover`.
     * * Aborts with `LibraAccount::EMALFORMED_AUTHENTICATION_KEY` if `new_key` is not 32 bytes.
     * * Aborts with `RecoveryAddress::ECANNOT_ROTATE_KEY` if `account` has not delegated its `KeyRotationCapability` to `recovery_address`.
     */
    public static final class RotateAuthenticationKeyWithRecoveryAddress extends ScriptCall {
        public final org.libra.types.AccountAddress recovery_address;
        public final org.libra.types.AccountAddress to_recover;
        public final com.novi.serde.Bytes new_key;

        public RotateAuthenticationKeyWithRecoveryAddress(org.libra.types.AccountAddress recovery_address, org.libra.types.AccountAddress to_recover, com.novi.serde.Bytes new_key) {
            assert recovery_address != null;
            assert to_recover != null;
            assert new_key != null;
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
            public org.libra.types.AccountAddress recovery_address;
            public org.libra.types.AccountAddress to_recover;
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
     * Rotate `account`'s base URL to `new_url` and its compliance public key to `new_key`.
     * Aborts if `account` is not a ParentVASP or DesignatedDealer
     * Aborts if `new_key` is not a well-formed public key
     */
    public static final class RotateDualAttestationInfo extends ScriptCall {
        public final com.novi.serde.Bytes new_url;
        public final com.novi.serde.Bytes new_key;

        public RotateDualAttestationInfo(com.novi.serde.Bytes new_url, com.novi.serde.Bytes new_key) {
            assert new_url != null;
            assert new_key != null;
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
     * (1) Rotate the public key stored in `account`'s `SharedEd25519PublicKey` resource to
     * `new_public_key`
     * (2) Rotate the authentication key using the capability stored in `account`'s
     * `SharedEd25519PublicKey` to a new value derived from `new_public_key`
     * Aborts if `account` does not have a `SharedEd25519PublicKey` resource.
     * Aborts if the length of `new_public_key` is not 32.
     */
    public static final class RotateSharedEd25519PublicKey extends ScriptCall {
        public final com.novi.serde.Bytes public_key;

        public RotateSharedEd25519PublicKey(com.novi.serde.Bytes public_key) {
            assert public_key != null;
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
     * Set validator's config and updates the config in the validator set.
     * NewEpochEvent is emitted.
     */
    public static final class SetValidatorConfigAndReconfigure extends ScriptCall {
        public final org.libra.types.AccountAddress validator_account;
        public final com.novi.serde.Bytes consensus_pubkey;
        public final com.novi.serde.Bytes validator_network_addresses;
        public final com.novi.serde.Bytes fullnode_network_addresses;

        public SetValidatorConfigAndReconfigure(org.libra.types.AccountAddress validator_account, com.novi.serde.Bytes consensus_pubkey, com.novi.serde.Bytes validator_network_addresses, com.novi.serde.Bytes fullnode_network_addresses) {
            assert validator_account != null;
            assert consensus_pubkey != null;
            assert validator_network_addresses != null;
            assert fullnode_network_addresses != null;
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
            public org.libra.types.AccountAddress validator_account;
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
     * Set validator's operator
     */
    public static final class SetValidatorOperator extends ScriptCall {
        public final com.novi.serde.Bytes operator_name;
        public final org.libra.types.AccountAddress operator_account;

        public SetValidatorOperator(com.novi.serde.Bytes operator_name, org.libra.types.AccountAddress operator_account) {
            assert operator_name != null;
            assert operator_account != null;
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
            public org.libra.types.AccountAddress operator_account;

            public SetValidatorOperator build() {
                return new SetValidatorOperator(
                    operator_name,
                    operator_account
                );
            }
        }
    }

    /**
     * Set validator operator as 'operator_account' of validator owner 'account' (via Admin Script).
     * `operator_name` should match expected from operator account. This script also
     * takes `sliding_nonce`, as a unique nonce for this operation. See `Sliding_nonce.move` for details.
     */
    public static final class SetValidatorOperatorWithNonceAdmin extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final com.novi.serde.Bytes operator_name;
        public final org.libra.types.AccountAddress operator_account;

        public SetValidatorOperatorWithNonceAdmin(@com.novi.serde.Unsigned Long sliding_nonce, com.novi.serde.Bytes operator_name, org.libra.types.AccountAddress operator_account) {
            assert sliding_nonce != null;
            assert operator_name != null;
            assert operator_account != null;
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
            public org.libra.types.AccountAddress operator_account;

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
     * Mint 'mint_amount' to 'designated_dealer_address' for 'tier_index' tier.
     * Max valid tier index is 3 since there are max 4 tiers per DD.
     * Sender should be treasury compliance account and receiver authorized DD.
     * `sliding_nonce` is a unique nonce for operation, see sliding_nonce.move for details.
     */
    public static final class TieredMint extends ScriptCall {
        public final org.libra.types.TypeTag coin_type;
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final org.libra.types.AccountAddress designated_dealer_address;
        public final @com.novi.serde.Unsigned Long mint_amount;
        public final @com.novi.serde.Unsigned Long tier_index;

        public TieredMint(org.libra.types.TypeTag coin_type, @com.novi.serde.Unsigned Long sliding_nonce, org.libra.types.AccountAddress designated_dealer_address, @com.novi.serde.Unsigned Long mint_amount, @com.novi.serde.Unsigned Long tier_index) {
            assert coin_type != null;
            assert sliding_nonce != null;
            assert designated_dealer_address != null;
            assert mint_amount != null;
            assert tier_index != null;
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
            public org.libra.types.TypeTag coin_type;
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public org.libra.types.AccountAddress designated_dealer_address;
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
     * Unfreeze account `address`. Initiator must be authorized.
     * `sliding_nonce` is a unique nonce for operation, see sliding_nonce.move for details.
     */
    public static final class UnfreezeAccount extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final org.libra.types.AccountAddress to_unfreeze_account;

        public UnfreezeAccount(@com.novi.serde.Unsigned Long sliding_nonce, org.libra.types.AccountAddress to_unfreeze_account) {
            assert sliding_nonce != null;
            assert to_unfreeze_account != null;
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
            public org.libra.types.AccountAddress to_unfreeze_account;

            public UnfreezeAccount build() {
                return new UnfreezeAccount(
                    sliding_nonce,
                    to_unfreeze_account
                );
            }
        }
    }

    /**
     * Unmints `amount_lbr` LBR from the sending account into the constituent coins and deposits
     * the resulting coins into the sending account.
     */
    public static final class UnmintLbr extends ScriptCall {
        public final @com.novi.serde.Unsigned Long amount_lbr;

        public UnmintLbr(@com.novi.serde.Unsigned Long amount_lbr) {
            assert amount_lbr != null;
            this.amount_lbr = amount_lbr;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            UnmintLbr other = (UnmintLbr) obj;
            if (!java.util.Objects.equals(this.amount_lbr, other.amount_lbr)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.amount_lbr != null ? this.amount_lbr.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long amount_lbr;

            public UnmintLbr build() {
                return new UnmintLbr(
                    amount_lbr
                );
            }
        }
    }

    /**
     * Update the dual attesation limit to `new_micro_lbr_limit`.
     */
    public static final class UpdateDualAttestationLimit extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final @com.novi.serde.Unsigned Long new_micro_lbr_limit;

        public UpdateDualAttestationLimit(@com.novi.serde.Unsigned Long sliding_nonce, @com.novi.serde.Unsigned Long new_micro_lbr_limit) {
            assert sliding_nonce != null;
            assert new_micro_lbr_limit != null;
            this.sliding_nonce = sliding_nonce;
            this.new_micro_lbr_limit = new_micro_lbr_limit;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            UpdateDualAttestationLimit other = (UpdateDualAttestationLimit) obj;
            if (!java.util.Objects.equals(this.sliding_nonce, other.sliding_nonce)) { return false; }
            if (!java.util.Objects.equals(this.new_micro_lbr_limit, other.new_micro_lbr_limit)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.sliding_nonce != null ? this.sliding_nonce.hashCode() : 0);
            value = 31 * value + (this.new_micro_lbr_limit != null ? this.new_micro_lbr_limit.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.novi.serde.Unsigned Long sliding_nonce;
            public @com.novi.serde.Unsigned Long new_micro_lbr_limit;

            public UpdateDualAttestationLimit build() {
                return new UpdateDualAttestationLimit(
                    sliding_nonce,
                    new_micro_lbr_limit
                );
            }
        }
    }

    /**
     * Update the on-chain exchange rate to LBR for the given `currency` to be given by
     * `new_exchange_rate_numerator/new_exchange_rate_denominator`.
     */
    public static final class UpdateExchangeRate extends ScriptCall {
        public final org.libra.types.TypeTag currency;
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final @com.novi.serde.Unsigned Long new_exchange_rate_numerator;
        public final @com.novi.serde.Unsigned Long new_exchange_rate_denominator;

        public UpdateExchangeRate(org.libra.types.TypeTag currency, @com.novi.serde.Unsigned Long sliding_nonce, @com.novi.serde.Unsigned Long new_exchange_rate_numerator, @com.novi.serde.Unsigned Long new_exchange_rate_denominator) {
            assert currency != null;
            assert sliding_nonce != null;
            assert new_exchange_rate_numerator != null;
            assert new_exchange_rate_denominator != null;
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
            public org.libra.types.TypeTag currency;
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
     * Update Libra version.
     * `sliding_nonce` is a unique nonce for operation, see sliding_nonce.move for details.
     */
    public static final class UpdateLibraVersion extends ScriptCall {
        public final @com.novi.serde.Unsigned Long sliding_nonce;
        public final @com.novi.serde.Unsigned Long major;

        public UpdateLibraVersion(@com.novi.serde.Unsigned Long sliding_nonce, @com.novi.serde.Unsigned Long major) {
            assert sliding_nonce != null;
            assert major != null;
            this.sliding_nonce = sliding_nonce;
            this.major = major;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            UpdateLibraVersion other = (UpdateLibraVersion) obj;
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

            public UpdateLibraVersion build() {
                return new UpdateLibraVersion(
                    sliding_nonce,
                    major
                );
            }
        }
    }

    /**
     * Allows--true--or disallows--false--minting of `currency` based upon `allow_minting`.
     */
    public static final class UpdateMintingAbility extends ScriptCall {
        public final org.libra.types.TypeTag currency;
        public final Boolean allow_minting;

        public UpdateMintingAbility(org.libra.types.TypeTag currency, Boolean allow_minting) {
            assert currency != null;
            assert allow_minting != null;
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
            public org.libra.types.TypeTag currency;
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

