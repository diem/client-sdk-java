package com.diem.stdlib;


import java.math.BigInteger;
import java.lang.IllegalArgumentException;
import java.lang.IndexOutOfBoundsException;
import com.diem.types.AccountAddress;
import com.diem.types.Script;
import com.diem.types.ScriptFunction;
import com.diem.types.TransactionPayload;
import com.diem.types.Identifier;
import com.diem.types.ModuleId;
import com.diem.types.TransactionArgument;
import com.diem.types.TypeTag;
import com.novi.bcs.BcsDeserializer;
import com.novi.bcs.BcsSerializer;
import com.novi.serde.Bytes;
import com.novi.serde.Int128;
import com.novi.serde.Unsigned;
import com.novi.serde.DeserializationError;
import com.novi.serde.SerializationError;


public final class Helpers {

    /**
     * Build a Diem {@link com.diem.types.Script} from a structured value {@link ScriptCall}.
     *
     * @param call {@link ScriptCall} value to encode.
     * @return Encoded script.
     */
    public static Script encode_script(ScriptCall call) {
        ScriptEncodingHelper helper = TRANSACTION_SCRIPT_ENCODER_MAP.get(call.getClass());
        return helper.encode(call);
    }

    /**
     * Build a Diem {@link com.diem.types.TransactionPayload} from a structured value {@link ScriptFunctionCall}.
     *
     * @param call {@link ScriptFunctionCall} value to encode.
     * @return Encoded TransactionPayload.
     */
    public static TransactionPayload encode_script_function(ScriptFunctionCall call) {
        ScriptFunctionEncodingHelper helper = SCRIPT_FUNCTION_ENCODER_MAP.get(call.getClass());
        return helper.encode(call);
    }

    /**
     * Try to recognize a Diem {@link com.diem.types.Script} and convert it into a structured value {@code ScriptCall}.
     *
     * @param script {@link com.diem.types.Script} values to decode.
     * @return Decoded {@link ScriptCall} value.
     */
    public static ScriptCall decode_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        TransactionScriptDecodingHelper helper = TRANSACTION_SCRIPT_DECODER_MAP.get(script.code);
        if (helper == null) {
            throw new IllegalArgumentException("Unknown script bytecode");
        }
        return helper.decode(script);
    }


    /**
     * Try to recognize a Diem {@link com.diem.types.TransactionPayload} and convert it into a structured value {@code ScriptFunctionCall}.
     *
     * @param payload {@link com.diem.types.TransactionPayload} values to decode.
     * @return Decoded {@link ScriptFunctionCall} value.
     */
    public static ScriptFunctionCall decode_script_function_payload(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if (payload instanceof TransactionPayload.ScriptFunction) {
            ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
            ScriptFunctionDecodingHelper helper = SCRIPT_FUNCTION_DECODER_MAP.get(script.module.name.value + script.function.value);
            if (helper == null) {
                throw new IllegalArgumentException("Unknown script function");
            }
            return helper.decode(payload);
        } else {
            throw new IllegalArgumentException("Unknown transaction payload");
        }
    }


    /**
     * <p><b>Summary</b></p>
     * Adds a zero {@code Currency} balance to the sending {@code account}. This will enable {@code account} to
     * send, receive, and hold {@code Diem::Diem<Currency>} coins. This transaction can be
     * successfully sent by any account that is allowed to hold balances
     * (e.g., VASP, Designated Dealer).
     *
     * <p><b>Technical Description</b></p>
     * After the successful execution of this transaction the sending account will have a
     * {@code DiemAccount::Balance<Currency>} resource with zero balance published under it. Only
     * accounts that can hold balances can send this transaction, the sending account cannot
     * already have a {@code DiemAccount::Balance<Currency>} published under it.
     *
     * <p><b>Parameters</b></p>
     * | Name       | Type      | Description                                                                                                                                         |
     * | ------     | ------    | -------------                                                                                                                                       |
     * | {@code Currency} | Type      | The Move type for the {@code Currency} being added to the sending account of the transaction. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code account}  | {@code &signer} | The signer of the sending account of the transaction.                                                                                               |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category              | Error Reason                             | Description                                                                |
     * | ----------------            | --------------                           | -------------                                                              |
     * | {@code Errors::NOT_PUBLISHED}     | {@code Diem::ECURRENCY_INFO}                  | The {@code Currency} is not a registered currency on-chain.                      |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code DiemAccount::EROLE_CANT_STORE_BALANCE} | The sending {@code account}'s role does not permit balances.                     |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code DiemAccount::EADD_EXISTING_CURRENCY}   | A balance for {@code Currency} is already published under the sending {@code account}. |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_child_vasp_account}</li></ul>
     * <ul><li>{@code Script::create_parent_vasp_account}</li></ul>
     * <ul><li>{@code Script::peer_to_peer_with_metadata}</li></ul>
     *
     * @param currency {@code TypeTag} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_add_currency_to_account_script(TypeTag currency) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ADD_CURRENCY_TO_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList(currency);
        builder.args = java.util.Arrays.asList();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Adds a zero {@code Currency} balance to the sending {@code account}. This will enable {@code account} to
     * send, receive, and hold {@code Diem::Diem<Currency>} coins. This transaction can be
     * successfully sent by any account that is allowed to hold balances
     * (e.g., VASP, Designated Dealer).
     *
     * <p><b>Technical Description</b></p>
     * After the successful execution of this transaction the sending account will have a
     * {@code DiemAccount::Balance<Currency>} resource with zero balance published under it. Only
     * accounts that can hold balances can send this transaction, the sending account cannot
     * already have a {@code DiemAccount::Balance<Currency>} published under it.
     *
     * <p><b>Parameters</b></p>
     * | Name       | Type     | Description                                                                                                                                         |
     * | ------     | ------   | -------------                                                                                                                                       |
     * | {@code Currency} | Type     | The Move type for the {@code Currency} being added to the sending account of the transaction. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code account}  | {@code signer} | The signer of the sending account of the transaction.                                                                                               |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category              | Error Reason                             | Description                                                                |
     * | ----------------            | --------------                           | -------------                                                              |
     * | {@code Errors::NOT_PUBLISHED}     | {@code Diem::ECURRENCY_INFO}                  | The {@code Currency} is not a registered currency on-chain.                      |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code DiemAccount::EROLE_CANT_STORE_BALANCE} | The sending {@code account}'s role does not permit balances.                     |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code DiemAccount::EADD_EXISTING_CURRENCY}   | A balance for {@code Currency} is already published under the sending {@code account}. |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_child_vasp_account}</li></ul>
     * <ul><li>{@code AccountCreationScripts::create_parent_vasp_account}</li></ul>
     * <ul><li>{@code PaymentScripts::peer_to_peer_with_metadata}</li></ul>
     *
     * @param currency {@code TypeTag} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_add_currency_to_account_script_function(TypeTag currency) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(currency);
        script_function_builder.args = java.util.Arrays.asList();
        script_function_builder.function = new Identifier("add_currency_to_account");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Stores the sending accounts ability to rotate its authentication key with a designated recovery
     * account. Both the sending and recovery accounts need to belong to the same VASP and
     * both be VASP accounts. After this transaction both the sending account and the
     * specified recovery account can rotate the sender account's authentication key.
     *
     * <p><b>Technical Description</b></p>
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
     * <p><b>Parameters</b></p>
     * | Name                 | Type      | Description                                                                                                |
     * | ------               | ------    | -------------                                                                                              |
     * | {@code to_recover_account} | {@code &signer} | The signer reference of the sending account of this transaction.                                           |
     * | {@code recovery_address}   | {@code address} | The account address where the {@code to_recover_account}'s {@code DiemAccount::KeyRotationCapability} will be stored. |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                              | Description                                                                                       |
     * | ----------------           | --------------                                            | -------------                                                                                     |
     * | {@code Errors::INVALID_STATE}    | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code to_recover_account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.    |
     * | {@code Errors::NOT_PUBLISHED}    | {@code RecoveryAddress::ERECOVERY_ADDRESS}                      | {@code recovery_address} does not have a {@code RecoveryAddress} resource published under it.                 |
     * | {@code Errors::INVALID_ARGUMENT} | {@code RecoveryAddress::EINVALID_KEY_ROTATION_DELEGATION}       | {@code to_recover_account} and {@code recovery_address} do not belong to the same VASP.                       |
     * | {@code Errors::LIMIT_EXCEEDED}   | {@code  RecoveryAddress::EMAX_KEYS_REGISTERED}                  | {@code RecoveryAddress::MAX_REGISTERED_KEYS} have already been registered with this {@code recovery_address}. |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_recovery_address}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_recovery_address}</li></ul>
     *
     * @param recovery_address {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_add_recovery_rotation_capability_script(AccountAddress recovery_address) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ADD_RECOVERY_ROTATION_CAPABILITY_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(recovery_address));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Stores the sending accounts ability to rotate its authentication key with a designated recovery
     * account. Both the sending and recovery accounts need to belong to the same VASP and
     * both be VASP accounts. After this transaction both the sending account and the
     * specified recovery account can rotate the sender account's authentication key.
     *
     * <p><b>Technical Description</b></p>
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
     * a recovery account address using the {@code AccountAdministrationScripts::create_recovery_address} transaction script.
     *
     * The sending account's ({@code to_recover_account}) key rotation capability is
     * removed in this transaction and stored in the {@code RecoveryAddress::RecoveryAddress}
     * resource stored under the account at {@code recovery_address}.
     *
     * <p><b>Parameters</b></p>
     * | Name                 | Type      | Description                                                                                               |
     * | ------               | ------    | -------------                                                                                             |
     * | {@code to_recover_account} | {@code signer}  | The signer of the sending account of this transaction.                                                    |
     * | {@code recovery_address}   | {@code address} | The account address where the {@code to_recover_account}'s {@code DiemAccount::KeyRotationCapability} will be stored. |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                              | Description                                                                                       |
     * | ----------------           | --------------                                            | -------------                                                                                     |
     * | {@code Errors::INVALID_STATE}    | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code to_recover_account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.    |
     * | {@code Errors::NOT_PUBLISHED}    | {@code RecoveryAddress::ERECOVERY_ADDRESS}                      | {@code recovery_address} does not have a {@code RecoveryAddress} resource published under it.                 |
     * | {@code Errors::INVALID_ARGUMENT} | {@code RecoveryAddress::EINVALID_KEY_ROTATION_DELEGATION}       | {@code to_recover_account} and {@code recovery_address} do not belong to the same VASP.                       |
     * | {@code Errors::LIMIT_EXCEEDED}   | {@code  RecoveryAddress::EMAX_KEYS_REGISTERED}                  | {@code RecoveryAddress::MAX_REGISTERED_KEYS} have already been registered with this {@code recovery_address}. |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountAdministrationScripts::create_recovery_address}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key_with_recovery_address}</li></ul>
     *
     * @param recovery_address {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_add_recovery_rotation_capability_script_function(AccountAddress recovery_address) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_address_argument(recovery_address));
        script_function_builder.function = new Identifier("add_recovery_rotation_capability");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Adds a validator account to the validator set, and triggers a
     * reconfiguration of the system to admit the account to the validator set for the system. This
     * transaction can only be successfully called by the Diem Root account.
     *
     * <p><b>Technical Description</b></p>
     * This script adds the account at {@code validator_address} to the validator set.
     * This transaction emits a {@code DiemConfig::NewEpochEvent} event and triggers a
     * reconfiguration. Once the reconfiguration triggered by this script's
     * execution has been performed, the account at the {@code validator_address} is
     * considered to be a validator in the network.
     *
     * This transaction script will fail if the {@code validator_address} address is already in the validator set
     * or does not have a {@code ValidatorConfig::ValidatorConfig} resource already published under it.
     *
     * <p><b>Parameters</b></p>
     * | Name                | Type         | Description                                                                                                                        |
     * | ------              | ------       | -------------                                                                                                                      |
     * | {@code dr_account}        | {@code &signer}    | The signer reference of the sending account of this transaction. Must be the Diem Root signer.                                    |
     * | {@code sliding_nonce}     | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                                         |
     * | {@code validator_name}    | {@code vector<u8>} | ASCII-encoded human name for the validator. Must match the human name in the {@code ValidatorConfig::ValidatorConfig} for the validator. |
     * | {@code validator_address} | {@code address}    | The validator account address to be added to the validator set.                                                                    |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param validator_name {@code Bytes} value
     * @param validator_address {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_add_validator_and_reconfigure_script(@Unsigned Long sliding_nonce, Bytes validator_name, AccountAddress validator_address) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ADD_VALIDATOR_AND_RECONFIGURE_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U8Vector(validator_name), new TransactionArgument.Address(validator_address));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Adds a validator account to the validator set, and triggers a
     * reconfiguration of the system to admit the account to the validator set for the system. This
     * transaction can only be successfully called by the Diem Root account.
     *
     * <p><b>Technical Description</b></p>
     * This script adds the account at {@code validator_address} to the validator set.
     * This transaction emits a {@code DiemConfig::NewEpochEvent} event and triggers a
     * reconfiguration. Once the reconfiguration triggered by this script's
     * execution has been performed, the account at the {@code validator_address} is
     * considered to be a validator in the network.
     *
     * This transaction script will fail if the {@code validator_address} address is already in the validator set
     * or does not have a {@code ValidatorConfig::ValidatorConfig} resource already published under it.
     *
     * <p><b>Parameters</b></p>
     * | Name                | Type         | Description                                                                                                                        |
     * | ------              | ------       | -------------                                                                                                                      |
     * | {@code dr_account}        | {@code signer}     | The signer of the sending account of this transaction. Must be the Diem Root signer.                                               |
     * | {@code sliding_nonce}     | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                                         |
     * | {@code validator_name}    | {@code vector<u8>} | ASCII-encoded human name for the validator. Must match the human name in the {@code ValidatorConfig::ValidatorConfig} for the validator. |
     * | {@code validator_address} | {@code address}    | The validator account address to be added to the validator set.                                                                    |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                 | Description                                                                                                                               |
     * | ----------------           | --------------                               | -------------                                                                                                                             |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}               | A {@code SlidingNonce} resource is not published under {@code dr_account}.                                                                            |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}               | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not.                                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}               | The {@code sliding_nonce} is too far in the future.                                                                                             |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}      | The {@code sliding_nonce} has been previously recorded.                                                                                         |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::EDIEM_ROOT}                  | The sending account is not the Diem Root account.                                                                                         |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::EDIEM_ROOT}                          | The sending account is not the Diem Root account.                                                                                         |
     * | 0                          | 0                                            | The provided {@code validator_name} does not match the already-recorded human name for the validator.                                           |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemSystem::EINVALID_PROSPECTIVE_VALIDATOR} | The validator to be added does not have a {@code ValidatorConfig::ValidatorConfig} resource published under it, or its {@code config} field is empty. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemSystem::EALREADY_A_VALIDATOR}           | The {@code validator_address} account is already a registered validator.                                                                        |
     * | {@code Errors::INVALID_STATE}    | {@code DiemConfig::EINVALID_BLOCK_TIME}            | An invalid time value was encountered in reconfiguration. Unlikely to occur.                                                              |
     * | {@code Errors::LIMIT_EXCEEDED}   | {@code DiemSystem::EMAX_VALIDATORS}                | The validator set is already at its maximum size. The validator could not be added.                                                       |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_validator_account}</li></ul>
     * <ul><li>{@code AccountCreationScripts::create_validator_operator_account}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::register_validator_config}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param validator_name {@code Bytes} value
     * @param validator_address {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_add_validator_and_reconfigure_script_function(@Unsigned Long sliding_nonce, Bytes validator_name, AccountAddress validator_address) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_u8vector_argument(validator_name), Helpers.encode_address_argument(validator_address));
        script_function_builder.function = new Identifier("add_validator_and_reconfigure");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("ValidatorAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Burns all coins held in the preburn resource at the specified
     * preburn address and removes them from the system. The sending account must
     * be the Treasury Compliance account.
     * The account that holds the preburn resource will normally be a Designated
     * Dealer, but there are no enforced requirements that it be one.
     *
     * <p><b>Technical Description</b></p>
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
     * <p><b>Events</b></p>
     * The successful execution of this transaction will emit a {@code Diem::BurnEvent} on the event handle
     * held in the {@code Diem::CurrencyInfo<Token>} resource's {@code burn_events} published under
     * {@code 0xA550C18}.
     *
     * <p><b>Parameters</b></p>
     * | Name              | Type      | Description                                                                                                                  |
     * | ------            | ------    | -------------                                                                                                                |
     * | {@code Token}           | Type      | The Move type for the {@code Token} currency being burned. {@code Token} must be an already-registered currency on-chain.                |
     * | {@code tc_account}      | {@code &signer} | The signer reference of the sending account of this transaction, must have a burn capability for {@code Token} published under it. |
     * | {@code sliding_nonce}   | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                                   |
     * | {@code preburn_address} | {@code address} | The address where the coins to-be-burned are currently held.                                                                 |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::burn_txn_fees}</li></ul>
     * <ul><li>{@code Script::cancel_burn}</li></ul>
     * <ul><li>{@code Script::preburn}</li></ul>
     *
     * @param token {@code TypeTag} value
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param preburn_address {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_burn_script(TypeTag token, @Unsigned Long sliding_nonce, AccountAddress preburn_address) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(BURN_CODE);
        builder.ty_args = java.util.Arrays.asList(token);
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(preburn_address));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Burns the transaction fees collected in the {@code CoinType} currency so that the
     * Diem association may reclaim the backing coins off-chain. May only be sent
     * by the Treasury Compliance account.
     *
     * <p><b>Technical Description</b></p>
     * Burns the transaction fees collected in {@code CoinType} so that the
     * association may reclaim the backing coins. Once this transaction has executed
     * successfully all transaction fees that will have been collected in
     * {@code CoinType} since the last time this script was called with that specific
     * currency. Both {@code balance} and {@code preburn} fields in the
     * {@code TransactionFee::TransactionFee<CoinType>} resource published under the {@code 0xB1E55ED}
     * account address will have a value of 0 after the successful execution of this script.
     *
     * <p><b>Events</b></p>
     * The successful execution of this transaction will emit a {@code Diem::BurnEvent} on the event handle
     * held in the {@code Diem::CurrencyInfo<CoinType>} resource's {@code burn_events} published under
     * {@code 0xA550C18}.
     *
     * <p><b>Parameters</b></p>
     * | Name         | Type      | Description                                                                                                                                         |
     * | ------       | ------    | -------------                                                                                                                                       |
     * | {@code CoinType}   | Type      | The Move type for the {@code CoinType} being added to the sending account of the transaction. {@code CoinType} must be an already-registered currency on-chain. |
     * | {@code tc_account} | {@code &signer} | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account.                                           |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                          | Description                                                 |
     * | ----------------           | --------------                        | -------------                                               |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE} | The sending account is not the Treasury Compliance account. |
     * | {@code Errors::NOT_PUBLISHED}    | {@code TransactionFee::ETRANSACTION_FEE}    | {@code CoinType} is not an accepted transaction fee currency.     |
     * | {@code Errors::INVALID_ARGUMENT} | {@code Diem::ECOIN}                        | The collected fees in {@code CoinType} are zero.                  |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::burn}</li></ul>
     * <ul><li>{@code Script::cancel_burn}</li></ul>
     *
     * @param coin_type {@code TypeTag} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_burn_txn_fees_script(TypeTag coin_type) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(BURN_TXN_FEES_CODE);
        builder.ty_args = java.util.Arrays.asList(coin_type);
        builder.args = java.util.Arrays.asList();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Burns the transaction fees collected in the {@code CoinType} currency so that the
     * Diem association may reclaim the backing coins off-chain. May only be sent
     * by the Treasury Compliance account.
     *
     * <p><b>Technical Description</b></p>
     * Burns the transaction fees collected in {@code CoinType} so that the
     * association may reclaim the backing coins. Once this transaction has executed
     * successfully all transaction fees that will have been collected in
     * {@code CoinType} since the last time this script was called with that specific
     * currency. Both {@code balance} and {@code preburn} fields in the
     * {@code TransactionFee::TransactionFee<CoinType>} resource published under the {@code 0xB1E55ED}
     * account address will have a value of 0 after the successful execution of this script.
     *
     * <p><b>Events</b></p>
     * The successful execution of this transaction will emit a {@code Diem::BurnEvent} on the event handle
     * held in the {@code Diem::CurrencyInfo<CoinType>} resource's {@code burn_events} published under
     * {@code 0xA550C18}.
     *
     * <p><b>Parameters</b></p>
     * | Name         | Type     | Description                                                                                                                                         |
     * | ------       | ------   | -------------                                                                                                                                       |
     * | {@code CoinType}   | Type     | The Move type for the {@code CoinType} being added to the sending account of the transaction. {@code CoinType} must be an already-registered currency on-chain. |
     * | {@code tc_account} | {@code signer} | The signer of the sending account of this transaction. Must be the Treasury Compliance account.                                                     |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                          | Description                                                 |
     * | ----------------           | --------------                        | -------------                                               |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE} | The sending account is not the Treasury Compliance account. |
     * | {@code Errors::NOT_PUBLISHED}    | {@code TransactionFee::ETRANSACTION_FEE}    | {@code CoinType} is not an accepted transaction fee currency.     |
     * | {@code Errors::INVALID_ARGUMENT} | {@code Diem::ECOIN}                        | The collected fees in {@code CoinType} are zero.                  |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code TreasuryComplianceScripts::burn_with_amount}</li></ul>
     * <ul><li>{@code TreasuryComplianceScripts::cancel_burn_with_amount}</li></ul>
     *
     * @param coin_type {@code TypeTag} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_burn_txn_fees_script_function(TypeTag coin_type) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(coin_type);
        script_function_builder.args = java.util.Arrays.asList();
        script_function_builder.function = new Identifier("burn_txn_fees");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("TreasuryComplianceScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Burns the coins held in a preburn resource in the preburn queue at the
     * specified preburn address, which are equal to the {@code amount} specified in the
     * transaction. Finds the first relevant outstanding preburn request with
     * matching amount and removes the contained coins from the system. The sending
     * account must be the Treasury Compliance account.
     * The account that holds the preburn queue resource will normally be a Designated
     * Dealer, but there are no enforced requirements that it be one.
     *
     * <p><b>Technical Description</b></p>
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
     * <p><b>Events</b></p>
     * The successful execution of this transaction will emit a {@code Diem::BurnEvent} on the event handle
     * held in the {@code Diem::CurrencyInfo<Token>} resource's {@code burn_events} published under
     * {@code 0xA550C18}.
     *
     * <p><b>Parameters</b></p>
     * | Name              | Type      | Description                                                                                                        |
     * | ------            | ------    | -------------                                                                                                      |
     * | {@code Token}           | Type      | The Move type for the {@code Token} currency being burned. {@code Token} must be an already-registered currency on-chain.      |
     * | {@code tc_account}      | {@code signer}  | The signer of the sending account of this transaction, must have a burn capability for {@code Token} published under it. |
     * | {@code sliding_nonce}   | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                         |
     * | {@code preburn_address} | {@code address} | The address where the coins to-be-burned are currently held.                                                       |
     * | {@code amount}          | {@code u64}     | The amount to be burned.                                                                                           |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category                | Error Reason                            | Description                                                                                                                         |
     * | ----------------              | --------------                          | -------------                                                                                                                       |
     * | {@code Errors::NOT_PUBLISHED}       | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code account}.                                                                         |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not.                                          |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                                                                       |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                                                                   |
     * | {@code Errors::REQUIRES_CAPABILITY} | {@code Diem::EBURN_CAPABILITY}                | The sending {@code account} does not have a {@code Diem::BurnCapability<Token>} published under it.                                             |
     * | {@code Errors::INVALID_STATE}       | {@code Diem::EPREBURN_NOT_FOUND}              | The {@code Diem::PreburnQueue<Token>} resource under {@code preburn_address} does not contain a preburn request with a value matching {@code amount}. |
     * | {@code Errors::NOT_PUBLISHED}       | {@code Diem::EPREBURN_QUEUE}                  | The account at {@code preburn_address} does not have a {@code Diem::PreburnQueue<Token>} resource published under it.                           |
     * | {@code Errors::NOT_PUBLISHED}       | {@code Diem::ECURRENCY_INFO}                  | The specified {@code Token} is not a registered currency on-chain.                                                                        |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code TreasuryComplianceScripts::burn_txn_fees}</li></ul>
     * <ul><li>{@code TreasuryComplianceScripts::cancel_burn_with_amount}</li></ul>
     * <ul><li>{@code TreasuryComplianceScripts::preburn}</li></ul>
     *
     * @param token {@code TypeTag} value
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param preburn_address {@code AccountAddress} value
     * @param amount {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_burn_with_amount_script_function(TypeTag token, @Unsigned Long sliding_nonce, AccountAddress preburn_address, @Unsigned Long amount) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(token);
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_address_argument(preburn_address), Helpers.encode_u64_argument(amount));
        script_function_builder.function = new Identifier("burn_with_amount");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("TreasuryComplianceScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Cancels and returns all coins held in the preburn area under
     * {@code preburn_address} and returns the funds to the {@code preburn_address}'s balance.
     * Can only be successfully sent by an account with Treasury Compliance role.
     *
     * <p><b>Technical Description</b></p>
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
     * <p><b>Events</b></p>
     * The successful execution of this transaction will emit:
     * <ul><li>A {@code Diem::CancelBurnEvent} on the event handle held in the {@code Diem::CurrencyInfo<Token>}</li></ul>
     * resource's {@code burn_events} published under {@code 0xA550C18}.
     * <ul><li>A {@code DiemAccount::ReceivedPaymentEvent} on the {@code preburn_address}'s</li></ul>
     * {@code DiemAccount::DiemAccount} {@code received_events} event handle with both the {@code payer} and {@code payee}
     * being {@code preburn_address}.
     *
     * <p><b>Parameters</b></p>
     * | Name              | Type      | Description                                                                                                                          |
     * | ------            | ------    | -------------                                                                                                                        |
     * | {@code Token}           | Type      | The Move type for the {@code Token} currenty that burning is being cancelled for. {@code Token} must be an already-registered currency on-chain. |
     * | {@code account}         | {@code &signer} | The signer reference of the sending account of this transaction, must have a burn capability for {@code Token} published under it.         |
     * | {@code preburn_address} | {@code address} | The address where the coins to-be-burned are currently held.                                                                         |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::burn_txn_fees}</li></ul>
     * <ul><li>{@code Script::burn}</li></ul>
     * <ul><li>{@code Script::preburn}</li></ul>
     *
     * @param token {@code TypeTag} value
     * @param preburn_address {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_cancel_burn_script(TypeTag token, AccountAddress preburn_address) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CANCEL_BURN_CODE);
        builder.ty_args = java.util.Arrays.asList(token);
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(preburn_address));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Cancels and returns the coins held in the preburn area under
     * {@code preburn_address}, which are equal to the {@code amount} specified in the transaction. Finds the first preburn
     * resource with the matching amount and returns the funds to the {@code preburn_address}'s balance.
     * Can only be successfully sent by an account with Treasury Compliance role.
     *
     * <p><b>Technical Description</b></p>
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
     * <p><b>Events</b></p>
     * The successful execution of this transaction will emit:
     * <ul><li>A {@code Diem::CancelBurnEvent} on the event handle held in the {@code Diem::CurrencyInfo<Token>}</li></ul>
     * resource's {@code burn_events} published under {@code 0xA550C18}.
     * <ul><li>A {@code DiemAccount::ReceivedPaymentEvent} on the {@code preburn_address}'s</li></ul>
     * {@code DiemAccount::DiemAccount} {@code received_events} event handle with both the {@code payer} and {@code payee}
     * being {@code preburn_address}.
     *
     * <p><b>Parameters</b></p>
     * | Name              | Type      | Description                                                                                                                          |
     * | ------            | ------    | -------------                                                                                                                        |
     * | {@code Token}           | Type      | The Move type for the {@code Token} currenty that burning is being cancelled for. {@code Token} must be an already-registered currency on-chain. |
     * | {@code account}         | {@code signer}  | The signer of the sending account of this transaction, must have a burn capability for {@code Token} published under it.                   |
     * | {@code preburn_address} | {@code address} | The address where the coins to-be-burned are currently held.                                                                         |
     * | {@code amount}          | {@code u64}     | The amount to be cancelled.                                                                                                          |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category                | Error Reason                                     | Description                                                                                                                         |
     * | ----------------              | --------------                                   | -------------                                                                                                                       |
     * | {@code Errors::REQUIRES_CAPABILITY} | {@code Diem::EBURN_CAPABILITY}                         | The sending {@code account} does not have a {@code Diem::BurnCapability<Token>} published under it.                                             |
     * | {@code Errors::INVALID_STATE}       | {@code Diem::EPREBURN_NOT_FOUND}                       | The {@code Diem::PreburnQueue<Token>} resource under {@code preburn_address} does not contain a preburn request with a value matching {@code amount}. |
     * | {@code Errors::NOT_PUBLISHED}       | {@code Diem::EPREBURN_QUEUE}                           | The account at {@code preburn_address} does not have a {@code Diem::PreburnQueue<Token>} resource published under it.                           |
     * | {@code Errors::NOT_PUBLISHED}       | {@code Diem::ECURRENCY_INFO}                           | The specified {@code Token} is not a registered currency on-chain.                                                                        |
     * | {@code Errors::INVALID_ARGUMENT}    | {@code DiemAccount::EPAYEE_CANT_ACCEPT_CURRENCY_TYPE}  | The account at {@code preburn_address} doesn't have a balance resource for {@code Token}.                                                       |
     * | {@code Errors::LIMIT_EXCEEDED}      | {@code DiemAccount::EDEPOSIT_EXCEEDS_LIMITS}           | The depositing of the funds held in the prebun area would exceed the {@code account}'s account limits.                                    |
     * | {@code Errors::INVALID_STATE}       | {@code DualAttestation::EPAYEE_COMPLIANCE_KEY_NOT_SET} | The {@code account} does not have a compliance key set on it but dual attestion checking was performed.                                   |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code TreasuryComplianceScripts::burn_txn_fees}</li></ul>
     * <ul><li>{@code TreasuryComplianceScripts::burn_with_amount}</li></ul>
     * <ul><li>{@code TreasuryComplianceScripts::preburn}</li></ul>
     *
     * @param token {@code TypeTag} value
     * @param preburn_address {@code AccountAddress} value
     * @param amount {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_cancel_burn_with_amount_script_function(TypeTag token, AccountAddress preburn_address, @Unsigned Long amount) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(token);
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_address_argument(preburn_address), Helpers.encode_u64_argument(amount));
        script_function_builder.function = new Identifier("cancel_burn_with_amount");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("TreasuryComplianceScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Creates a Child VASP account with its parent being the sending account of the transaction.
     * The sender of the transaction must be a Parent VASP account.
     *
     * <p><b>Technical Description</b></p>
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
     * <p><b>Events</b></p>
     * Successful execution with a {@code child_initial_balance} greater than zero will emit:
     * <ul><li>A {@code DiemAccount::SentPaymentEvent} with the {@code payer} field being the Parent VASP's address,</li></ul>
     * and payee field being {@code child_address}. This is emitted on the Parent VASP's
     * {@code DiemAccount::DiemAccount} {@code sent_events} handle.
     * <ul><li>A {@code DiemAccount::ReceivedPaymentEvent} with the  {@code payer} field being the Parent VASP's address,</li></ul>
     * and payee field being {@code child_address}. This is emitted on the new Child VASPS's
     * {@code DiemAccount::DiemAccount} {@code received_events} handle.
     *
     * <p><b>Parameters</b></p>
     * | Name                    | Type         | Description                                                                                                                                 |
     * | ------                  | ------       | -------------                                                                                                                               |
     * | {@code CoinType}              | Type         | The Move type for the {@code CoinType} that the child account should be created with. {@code CoinType} must be an already-registered currency on-chain. |
     * | {@code parent_vasp}           | {@code &signer}    | The signer reference of the sending account. Must be a Parent VASP account.                                                                 |
     * | {@code child_address}         | {@code address}    | Address of the to-be-created Child VASP account.                                                                                            |
     * | {@code auth_key_prefix}       | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account.                                                    |
     * | {@code add_all_currencies}    | {@code bool}       | Whether to publish balance resources for all known currencies when the account is created.                                                  |
     * | {@code child_initial_balance} | {@code u64}        | The initial balance in {@code CoinType} to give the child account when it's created.                                                              |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_parent_vasp_account}</li></ul>
     * <ul><li>{@code Script::add_currency_to_account}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key}</li></ul>
     * <ul><li>{@code Script::add_recovery_rotation_capability}</li></ul>
     * <ul><li>{@code Script::create_recovery_address}</li></ul>
     *
     * @param coin_type {@code TypeTag} value
     * @param child_address {@code AccountAddress} value
     * @param auth_key_prefix {@code Bytes} value
     * @param add_all_currencies {@code Boolean} value
     * @param child_initial_balance {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_create_child_vasp_account_script(TypeTag coin_type, AccountAddress child_address, Bytes auth_key_prefix, Boolean add_all_currencies, @Unsigned Long child_initial_balance) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CREATE_CHILD_VASP_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList(coin_type);
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(child_address), new TransactionArgument.U8Vector(auth_key_prefix), new TransactionArgument.Bool(add_all_currencies), new TransactionArgument.U64(child_initial_balance));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Creates a Child VASP account with its parent being the sending account of the transaction.
     * The sender of the transaction must be a Parent VASP account.
     *
     * <p><b>Technical Description</b></p>
     * Creates a {@code ChildVASP} account for the sender {@code parent_vasp} at {@code child_address} with a balance of
     * {@code child_initial_balance} in {@code CoinType} and an initial authentication key of
     * {@code auth_key_prefix | child_address}. Authentication key prefixes, and how to construct them from an ed25519 public key is described
     * [here](https://developers.diem.com/docs/core/accounts/#addresses-authentication-keys-and-cryptographic-keys).
     *
     * If {@code add_all_currencies} is true, the child address will have a zero balance in all available
     * currencies in the system.
     *
     * The new account will be a child account of the transaction sender, which must be a
     * Parent VASP account. The child account will be recorded against the limit of
     * child accounts of the creating Parent VASP account.
     *
     * <p><b>Events</b></p>
     * Successful execution will emit:
     * <ul><li>A {@code DiemAccount::CreateAccountEvent} with the {@code created} field being {@code child_address},</li></ul>
     * and the {@code rold_id} field being {@code Roles::CHILD_VASP_ROLE_ID}. This is emitted on the
     * {@code DiemAccount::AccountOperationsCapability} {@code creation_events} handle.
     *
     * Successful execution with a {@code child_initial_balance} greater than zero will additionaly emit:
     * <ul><li>A {@code DiemAccount::SentPaymentEvent} with the {@code payee} field being {@code child_address}.</li></ul>
     * This is emitted on the Parent VASP's {@code DiemAccount::DiemAccount} {@code sent_events} handle.
     * <ul><li>A {@code DiemAccount::ReceivedPaymentEvent} with the  {@code payer} field being the Parent VASP's address.</li></ul>
     * This is emitted on the new Child VASPS's {@code DiemAccount::DiemAccount} {@code received_events} handle.
     *
     * <p><b>Parameters</b></p>
     * | Name                    | Type         | Description                                                                                                                                 |
     * | ------                  | ------       | -------------                                                                                                                               |
     * | {@code CoinType}              | Type         | The Move type for the {@code CoinType} that the child account should be created with. {@code CoinType} must be an already-registered currency on-chain. |
     * | {@code parent_vasp}           | {@code signer}     | The reference of the sending account. Must be a Parent VASP account.                                                                        |
     * | {@code child_address}         | {@code address}    | Address of the to-be-created Child VASP account.                                                                                            |
     * | {@code auth_key_prefix}       | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account.                                                    |
     * | {@code add_all_currencies}    | {@code bool}       | Whether to publish balance resources for all known currencies when the account is created.                                                  |
     * | {@code child_initial_balance} | {@code u64}        | The initial balance in {@code CoinType} to give the child account when it's created.                                                              |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_parent_vasp_account}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::add_currency_to_account}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::add_recovery_rotation_capability}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::create_recovery_address}</li></ul>
     *
     * @param coin_type {@code TypeTag} value
     * @param child_address {@code AccountAddress} value
     * @param auth_key_prefix {@code Bytes} value
     * @param add_all_currencies {@code Boolean} value
     * @param child_initial_balance {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_create_child_vasp_account_script_function(TypeTag coin_type, AccountAddress child_address, Bytes auth_key_prefix, Boolean add_all_currencies, @Unsigned Long child_initial_balance) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(coin_type);
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_address_argument(child_address), Helpers.encode_u8vector_argument(auth_key_prefix), Helpers.encode_bool_argument(add_all_currencies), Helpers.encode_u64_argument(child_initial_balance));
        script_function_builder.function = new Identifier("create_child_vasp_account");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountCreationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Creates a Designated Dealer account with the provided information, and initializes it with
     * default mint tiers. The transaction can only be sent by the Treasury Compliance account.
     *
     * <p><b>Technical Description</b></p>
     * Creates an account with the Designated Dealer role at {@code addr} with authentication key
     * {@code auth_key_prefix} | {@code addr} and a 0 balance of type {@code Currency}. If {@code add_all_currencies} is true,
     * 0 balances for all available currencies in the system will also be added. This can only be
     * invoked by an account with the TreasuryCompliance role.
     *
     * At the time of creation the account is also initialized with default mint tiers of (500_000,
     * 5000_000, 50_000_000, 500_000_000), and preburn areas for each currency that is added to the
     * account.
     *
     * <p><b>Parameters</b></p>
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

     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::tiered_mint}</li></ul>
     * <ul><li>{@code Script::peer_to_peer_with_metadata}</li></ul>
     * <ul><li>{@code Script::rotate_dual_attestation_info}</li></ul>
     *
     * @param currency {@code TypeTag} value
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param addr {@code AccountAddress} value
     * @param auth_key_prefix {@code Bytes} value
     * @param human_name {@code Bytes} value
     * @param add_all_currencies {@code Boolean} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_create_designated_dealer_script(TypeTag currency, @Unsigned Long sliding_nonce, AccountAddress addr, Bytes auth_key_prefix, Bytes human_name, Boolean add_all_currencies) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CREATE_DESIGNATED_DEALER_CODE);
        builder.ty_args = java.util.Arrays.asList(currency);
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(addr), new TransactionArgument.U8Vector(auth_key_prefix), new TransactionArgument.U8Vector(human_name), new TransactionArgument.Bool(add_all_currencies));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Creates a Designated Dealer account with the provided information, and initializes it with
     * default mint tiers. The transaction can only be sent by the Treasury Compliance account.
     *
     * <p><b>Technical Description</b></p>
     * Creates an account with the Designated Dealer role at {@code addr} with authentication key
     * {@code auth_key_prefix} | {@code addr} and a 0 balance of type {@code Currency}. If {@code add_all_currencies} is true,
     * 0 balances for all available currencies in the system will also be added. This can only be
     * invoked by an account with the TreasuryCompliance role.
     * Authentication keys, prefixes, and how to construct them from an ed25519 public key are described
     * [here](https://developers.diem.com/docs/core/accounts/#addresses-authentication-keys-and-cryptographic-keys).
     *
     * At the time of creation the account is also initialized with default mint tiers of (500_000,
     * 5000_000, 50_000_000, 500_000_000), and preburn areas for each currency that is added to the
     * account.
     *
     * <p><b>Events</b></p>
     * Successful execution will emit:
     * <ul><li>A {@code DiemAccount::CreateAccountEvent} with the {@code created} field being {@code addr},</li></ul>
     * and the {@code rold_id} field being {@code Roles::DESIGNATED_DEALER_ROLE_ID}. This is emitted on the
     * {@code DiemAccount::AccountOperationsCapability} {@code creation_events} handle.
     *
     * <p><b>Parameters</b></p>
     * | Name                 | Type         | Description                                                                                                                                         |
     * | ------               | ------       | -------------                                                                                                                                       |
     * | {@code Currency}           | Type         | The Move type for the {@code Currency} that the Designated Dealer should be initialized with. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code tc_account}         | {@code signer}     | The signer of the sending account of this transaction. Must be the Treasury Compliance account.                                                     |
     * | {@code sliding_nonce}      | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                                                          |
     * | {@code addr}               | {@code address}    | Address of the to-be-created Designated Dealer account.                                                                                             |
     * | {@code auth_key_prefix}    | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account.                                                            |
     * | {@code human_name}         | {@code vector<u8>} | ASCII-encoded human name for the Designated Dealer.                                                                                                 |
     * | {@code add_all_currencies} | {@code bool}       | Whether to publish preburn, balance, and tier info resources for all known (SCS) currencies or just {@code Currency} when the account is created.         |
     *

     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code TreasuryComplianceScripts::tiered_mint}</li></ul>
     * <ul><li>{@code PaymentScripts::peer_to_peer_with_metadata}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_dual_attestation_info}</li></ul>
     *
     * @param currency {@code TypeTag} value
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param addr {@code AccountAddress} value
     * @param auth_key_prefix {@code Bytes} value
     * @param human_name {@code Bytes} value
     * @param add_all_currencies {@code Boolean} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_create_designated_dealer_script_function(TypeTag currency, @Unsigned Long sliding_nonce, AccountAddress addr, Bytes auth_key_prefix, Bytes human_name, Boolean add_all_currencies) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(currency);
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_address_argument(addr), Helpers.encode_u8vector_argument(auth_key_prefix), Helpers.encode_u8vector_argument(human_name), Helpers.encode_bool_argument(add_all_currencies));
        script_function_builder.function = new Identifier("create_designated_dealer");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountCreationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Creates a Parent VASP account with the specified human name. Must be called by the Treasury Compliance account.
     *
     * <p><b>Technical Description</b></p>
     * Creates an account with the Parent VASP role at {@code address} with authentication key
     * {@code auth_key_prefix} | {@code new_account_address} and a 0 balance of type {@code CoinType}. If
     * {@code add_all_currencies} is true, 0 balances for all available currencies in the system will
     * also be added. This can only be invoked by an TreasuryCompliance account.
     * {@code sliding_nonce} is a unique nonce for operation, see {@code SlidingNonce} for details.
     *
     * <p><b>Parameters</b></p>
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
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_child_vasp_account}</li></ul>
     * <ul><li>{@code Script::add_currency_to_account}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key}</li></ul>
     * <ul><li>{@code Script::add_recovery_rotation_capability}</li></ul>
     * <ul><li>{@code Script::create_recovery_address}</li></ul>
     * <ul><li>{@code Script::rotate_dual_attestation_info}</li></ul>
     *
     * @param coin_type {@code TypeTag} value
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_account_address {@code AccountAddress} value
     * @param auth_key_prefix {@code Bytes} value
     * @param human_name {@code Bytes} value
     * @param add_all_currencies {@code Boolean} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_create_parent_vasp_account_script(TypeTag coin_type, @Unsigned Long sliding_nonce, AccountAddress new_account_address, Bytes auth_key_prefix, Bytes human_name, Boolean add_all_currencies) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CREATE_PARENT_VASP_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList(coin_type);
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(new_account_address), new TransactionArgument.U8Vector(auth_key_prefix), new TransactionArgument.U8Vector(human_name), new TransactionArgument.Bool(add_all_currencies));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Creates a Parent VASP account with the specified human name. Must be called by the Treasury Compliance account.
     *
     * <p><b>Technical Description</b></p>
     * Creates an account with the Parent VASP role at {@code address} with authentication key
     * {@code auth_key_prefix} | {@code new_account_address} and a 0 balance of type {@code CoinType}. If
     * {@code add_all_currencies} is true, 0 balances for all available currencies in the system will
     * also be added. This can only be invoked by an TreasuryCompliance account.
     * {@code sliding_nonce} is a unique nonce for operation, see {@code SlidingNonce} for details.
     * Authentication keys, prefixes, and how to construct them from an ed25519 public key are described
     * [here](https://developers.diem.com/docs/core/accounts/#addresses-authentication-keys-and-cryptographic-keys).
     *
     * <p><b>Events</b></p>
     * Successful execution will emit:
     * <ul><li>A {@code DiemAccount::CreateAccountEvent} with the {@code created} field being {@code new_account_address},</li></ul>
     * and the {@code rold_id} field being {@code Roles::PARENT_VASP_ROLE_ID}. This is emitted on the
     * {@code DiemAccount::AccountOperationsCapability} {@code creation_events} handle.
     *
     * <p><b>Parameters</b></p>
     * | Name                  | Type         | Description                                                                                                                                                    |
     * | ------                | ------       | -------------                                                                                                                                                  |
     * | {@code CoinType}            | Type         | The Move type for the {@code CoinType} currency that the Parent VASP account should be initialized with. {@code CoinType} must be an already-registered currency on-chain. |
     * | {@code tc_account}          | {@code signer}     | The signer of the sending account of this transaction. Must be the Treasury Compliance account.                                                                |
     * | {@code sliding_nonce}       | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                                                                     |
     * | {@code new_account_address} | {@code address}    | Address of the to-be-created Parent VASP account.                                                                                                              |
     * | {@code auth_key_prefix}     | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account.                                                                       |
     * | {@code human_name}          | {@code vector<u8>} | ASCII-encoded human name for the Parent VASP.                                                                                                                  |
     * | {@code add_all_currencies}  | {@code bool}       | Whether to publish balance resources for all known currencies when the account is created.                                                                     |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_child_vasp_account}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::add_currency_to_account}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::add_recovery_rotation_capability}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::create_recovery_address}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_dual_attestation_info}</li></ul>
     *
     * @param coin_type {@code TypeTag} value
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_account_address {@code AccountAddress} value
     * @param auth_key_prefix {@code Bytes} value
     * @param human_name {@code Bytes} value
     * @param add_all_currencies {@code Boolean} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_create_parent_vasp_account_script_function(TypeTag coin_type, @Unsigned Long sliding_nonce, AccountAddress new_account_address, Bytes auth_key_prefix, Bytes human_name, Boolean add_all_currencies) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(coin_type);
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_address_argument(new_account_address), Helpers.encode_u8vector_argument(auth_key_prefix), Helpers.encode_u8vector_argument(human_name), Helpers.encode_bool_argument(add_all_currencies));
        script_function_builder.function = new Identifier("create_parent_vasp_account");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountCreationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Initializes the sending account as a recovery address that may be used by
     * the VASP that it belongs to. The sending account must be a VASP account.
     * Multiple recovery addresses can exist for a single VASP, but accounts in
     * each must be disjoint.
     *
     * <p><b>Technical Description</b></p>
     * Publishes a {@code RecoveryAddress::RecoveryAddress} resource under {@code account}. It then
     * extracts the {@code DiemAccount::KeyRotationCapability} for {@code account} and adds
     * it to the resource. After the successful execution of this transaction
     * other accounts may add their key rotation to this resource so that {@code account}
     * may be used as a recovery account for those accounts.
     *
     * <p><b>Parameters</b></p>
     * | Name      | Type      | Description                                           |
     * | ------    | ------    | -------------                                         |
     * | {@code account} | {@code &signer} | The signer of the sending account of the transaction. |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category              | Error Reason                                               | Description                                                                                   |
     * | ----------------            | --------------                                             | -------------                                                                                 |
     * | {@code Errors::INVALID_STATE}     | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.          |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code RecoveryAddress::ENOT_A_VASP}                             | {@code account} is not a VASP account.                                                              |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code RecoveryAddress::EKEY_ROTATION_DEPENDENCY_CYCLE}          | A key rotation recovery cycle would be created by adding {@code account}'s key rotation capability. |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code RecoveryAddress::ERECOVERY_ADDRESS}                       | A {@code RecoveryAddress::RecoveryAddress} resource has already been published under {@code account}.     |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::add_recovery_rotation_capability}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_recovery_address}</li></ul>
     *
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_create_recovery_address_script() {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CREATE_RECOVERY_ADDRESS_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Initializes the sending account as a recovery address that may be used by
     * other accounts belonging to the same VASP as {@code account}.
     * The sending account must be a VASP account, and can be either a child or parent VASP account.
     * Multiple recovery addresses can exist for a single VASP, but accounts in
     * each must be disjoint.
     *
     * <p><b>Technical Description</b></p>
     * Publishes a {@code RecoveryAddress::RecoveryAddress} resource under {@code account}. It then
     * extracts the {@code DiemAccount::KeyRotationCapability} for {@code account} and adds
     * it to the resource. After the successful execution of this transaction
     * other accounts may add their key rotation to this resource so that {@code account}
     * may be used as a recovery account for those accounts.
     *
     * <p><b>Parameters</b></p>
     * | Name      | Type     | Description                                           |
     * | ------    | ------   | -------------                                         |
     * | {@code account} | {@code signer} | The signer of the sending account of the transaction. |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category              | Error Reason                                               | Description                                                                                   |
     * | ----------------            | --------------                                             | -------------                                                                                 |
     * | {@code Errors::INVALID_STATE}     | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.          |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code RecoveryAddress::ENOT_A_VASP}                             | {@code account} is not a VASP account.                                                              |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code RecoveryAddress::EKEY_ROTATION_DEPENDENCY_CYCLE}          | A key rotation recovery cycle would be created by adding {@code account}'s key rotation capability. |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code RecoveryAddress::ERECOVERY_ADDRESS}                       | A {@code RecoveryAddress::RecoveryAddress} resource has already been published under {@code account}.     |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::add_recovery_rotation_capability}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_recovery_address}</li></ul>
     *
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_create_recovery_address_script_function() {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList();
        script_function_builder.function = new Identifier("create_recovery_address");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Creates a Validator account. This transaction can only be sent by the Diem
     * Root account.
     *
     * <p><b>Technical Description</b></p>
     * Creates an account with a Validator role at {@code new_account_address}, with authentication key
     * {@code auth_key_prefix} | {@code new_account_address}. It publishes a
     * {@code ValidatorConfig::ValidatorConfig} resource with empty {@code config}, and
     * {@code operator_account} fields. The {@code human_name} field of the
     * {@code ValidatorConfig::ValidatorConfig} is set to the passed in {@code human_name}.
     * This script does not add the validator to the validator set or the system,
     * but only creates the account.
     *
     * <p><b>Parameters</b></p>
     * | Name                  | Type         | Description                                                                                     |
     * | ------                | ------       | -------------                                                                                   |
     * | {@code dr_account}          | {@code &signer}    | The signer reference of the sending account of this transaction. Must be the Diem Root signer. |
     * | {@code sliding_nonce}       | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                      |
     * | {@code new_account_address} | {@code address}    | Address of the to-be-created Validator account.                                                 |
     * | {@code auth_key_prefix}     | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account.        |
     * | {@code human_name}          | {@code vector<u8>} | ASCII-encoded human name for the validator.                                                     |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_account_address {@code AccountAddress} value
     * @param auth_key_prefix {@code Bytes} value
     * @param human_name {@code Bytes} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_create_validator_account_script(@Unsigned Long sliding_nonce, AccountAddress new_account_address, Bytes auth_key_prefix, Bytes human_name) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CREATE_VALIDATOR_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(new_account_address), new TransactionArgument.U8Vector(auth_key_prefix), new TransactionArgument.U8Vector(human_name));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Creates a Validator account. This transaction can only be sent by the Diem
     * Root account.
     *
     * <p><b>Technical Description</b></p>
     * Creates an account with a Validator role at {@code new_account_address}, with authentication key
     * {@code auth_key_prefix} | {@code new_account_address}. It publishes a
     * {@code ValidatorConfig::ValidatorConfig} resource with empty {@code config}, and
     * {@code operator_account} fields. The {@code human_name} field of the
     * {@code ValidatorConfig::ValidatorConfig} is set to the passed in {@code human_name}.
     * This script does not add the validator to the validator set or the system,
     * but only creates the account.
     * Authentication keys, prefixes, and how to construct them from an ed25519 public key are described
     * [here](https://developers.diem.com/docs/core/accounts/#addresses-authentication-keys-and-cryptographic-keys).
     *
     * <p><b>Events</b></p>
     * Successful execution will emit:
     * <ul><li>A {@code DiemAccount::CreateAccountEvent} with the {@code created} field being {@code new_account_address},</li></ul>
     * and the {@code rold_id} field being {@code Roles::VALIDATOR_ROLE_ID}. This is emitted on the
     * {@code DiemAccount::AccountOperationsCapability} {@code creation_events} handle.
     *
     * <p><b>Parameters</b></p>
     * | Name                  | Type         | Description                                                                              |
     * | ------                | ------       | -------------                                                                            |
     * | {@code dr_account}          | {@code signer}     | The signer of the sending account of this transaction. Must be the Diem Root signer.     |
     * | {@code sliding_nonce}       | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.               |
     * | {@code new_account_address} | {@code address}    | Address of the to-be-created Validator account.                                          |
     * | {@code auth_key_prefix}     | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account. |
     * | {@code human_name}          | {@code vector<u8>} | ASCII-encoded human name for the validator.                                              |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_validator_operator_account}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::register_validator_config}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_account_address {@code AccountAddress} value
     * @param auth_key_prefix {@code Bytes} value
     * @param human_name {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_create_validator_account_script_function(@Unsigned Long sliding_nonce, AccountAddress new_account_address, Bytes auth_key_prefix, Bytes human_name) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_address_argument(new_account_address), Helpers.encode_u8vector_argument(auth_key_prefix), Helpers.encode_u8vector_argument(human_name));
        script_function_builder.function = new Identifier("create_validator_account");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountCreationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Creates a Validator Operator account. This transaction can only be sent by the Diem
     * Root account.
     *
     * <p><b>Technical Description</b></p>
     * Creates an account with a Validator Operator role at {@code new_account_address}, with authentication key
     * {@code auth_key_prefix} | {@code new_account_address}. It publishes a
     * {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource with the specified {@code human_name}.
     * This script does not assign the validator operator to any validator accounts but only creates the account.
     *
     * <p><b>Parameters</b></p>
     * | Name                  | Type         | Description                                                                                     |
     * | ------                | ------       | -------------                                                                                   |
     * | {@code dr_account}          | {@code &signer}    | The signer reference of the sending account of this transaction. Must be the Diem Root signer. |
     * | {@code sliding_nonce}       | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                      |
     * | {@code new_account_address} | {@code address}    | Address of the to-be-created Validator account.                                                 |
     * | {@code auth_key_prefix}     | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account.        |
     * | {@code human_name}          | {@code vector<u8>} | ASCII-encoded human name for the validator.                                                     |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_account_address {@code AccountAddress} value
     * @param auth_key_prefix {@code Bytes} value
     * @param human_name {@code Bytes} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_create_validator_operator_account_script(@Unsigned Long sliding_nonce, AccountAddress new_account_address, Bytes auth_key_prefix, Bytes human_name) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(CREATE_VALIDATOR_OPERATOR_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(new_account_address), new TransactionArgument.U8Vector(auth_key_prefix), new TransactionArgument.U8Vector(human_name));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Creates a Validator Operator account. This transaction can only be sent by the Diem
     * Root account.
     *
     * <p><b>Technical Description</b></p>
     * Creates an account with a Validator Operator role at {@code new_account_address}, with authentication key
     * {@code auth_key_prefix} | {@code new_account_address}. It publishes a
     * {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource with the specified {@code human_name}.
     * This script does not assign the validator operator to any validator accounts but only creates the account.
     * Authentication key prefixes, and how to construct them from an ed25519 public key are described
     * [here](https://developers.diem.com/docs/core/accounts/#addresses-authentication-keys-and-cryptographic-keys).
     *
     * <p><b>Events</b></p>
     * Successful execution will emit:
     * <ul><li>A {@code DiemAccount::CreateAccountEvent} with the {@code created} field being {@code new_account_address},</li></ul>
     * and the {@code rold_id} field being {@code Roles::VALIDATOR_OPERATOR_ROLE_ID}. This is emitted on the
     * {@code DiemAccount::AccountOperationsCapability} {@code creation_events} handle.
     *
     * <p><b>Parameters</b></p>
     * | Name                  | Type         | Description                                                                              |
     * | ------                | ------       | -------------                                                                            |
     * | {@code dr_account}          | {@code signer}     | The signer of the sending account of this transaction. Must be the Diem Root signer.     |
     * | {@code sliding_nonce}       | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.               |
     * | {@code new_account_address} | {@code address}    | Address of the to-be-created Validator account.                                          |
     * | {@code auth_key_prefix}     | {@code vector<u8>} | The authentication key prefix that will be used initially for the newly created account. |
     * | {@code human_name}          | {@code vector<u8>} | ASCII-encoded human name for the validator.                                              |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_validator_account}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::register_validator_config}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_account_address {@code AccountAddress} value
     * @param auth_key_prefix {@code Bytes} value
     * @param human_name {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_create_validator_operator_account_script_function(@Unsigned Long sliding_nonce, AccountAddress new_account_address, Bytes auth_key_prefix, Bytes human_name) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_address_argument(new_account_address), Helpers.encode_u8vector_argument(auth_key_prefix), Helpers.encode_u8vector_argument(human_name));
        script_function_builder.function = new Identifier("create_validator_operator_account");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountCreationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Freezes the account at {@code address}. The sending account of this transaction
     * must be the Treasury Compliance account. The account being frozen cannot be
     * the Diem Root or Treasury Compliance account. After the successful
     * execution of this transaction no transactions may be sent from the frozen
     * account, and the frozen account may not send or receive coins.
     *
     * <p><b>Technical Description</b></p>
     * Sets the {@code AccountFreezing::FreezingBit} to {@code true} and emits a
     * {@code AccountFreezing::FreezeAccountEvent}. The transaction sender must be the
     * Treasury Compliance account, but the account at {@code to_freeze_account} must
     * not be either {@code 0xA550C18} (the Diem Root address), or {@code 0xB1E55ED} (the
     * Treasury Compliance address). Note that this is a per-account property
     * e.g., freezing a Parent VASP will not effect the status any of its child
     * accounts and vice versa.
     *

     * <p><b>Events</b></p>
     * Successful execution of this transaction will emit a {@code AccountFreezing::FreezeAccountEvent} on
     * the {@code freeze_event_handle} held in the {@code AccountFreezing::FreezeEventsHolder} resource published
     * under {@code 0xA550C18} with the {@code frozen_address} being the {@code to_freeze_account}.
     *
     * <p><b>Parameters</b></p>
     * | Name                | Type      | Description                                                                                               |
     * | ------              | ------    | -------------                                                                                             |
     * | {@code tc_account}        | {@code &signer} | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account. |
     * | {@code sliding_nonce}     | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                |
     * | {@code to_freeze_account} | {@code address} | The account address to be frozen.                                                                         |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::unfreeze_account}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param to_freeze_account {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_freeze_account_script(@Unsigned Long sliding_nonce, AccountAddress to_freeze_account) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(FREEZE_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(to_freeze_account));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Freezes the account at {@code address}. The sending account of this transaction
     * must be the Treasury Compliance account. The account being frozen cannot be
     * the Diem Root or Treasury Compliance account. After the successful
     * execution of this transaction no transactions may be sent from the frozen
     * account, and the frozen account may not send or receive coins.
     *
     * <p><b>Technical Description</b></p>
     * Sets the {@code AccountFreezing::FreezingBit} to {@code true} and emits a
     * {@code AccountFreezing::FreezeAccountEvent}. The transaction sender must be the
     * Treasury Compliance account, but the account at {@code to_freeze_account} must
     * not be either {@code 0xA550C18} (the Diem Root address), or {@code 0xB1E55ED} (the
     * Treasury Compliance address). Note that this is a per-account property
     * e.g., freezing a Parent VASP will not effect the status any of its child
     * accounts and vice versa.
     *

     * <p><b>Events</b></p>
     * Successful execution of this transaction will emit a {@code AccountFreezing::FreezeAccountEvent} on
     * the {@code freeze_event_handle} held in the {@code AccountFreezing::FreezeEventsHolder} resource published
     * under {@code 0xA550C18} with the {@code frozen_address} being the {@code to_freeze_account}.
     *
     * <p><b>Parameters</b></p>
     * | Name                | Type      | Description                                                                                     |
     * | ------              | ------    | -------------                                                                                   |
     * | {@code tc_account}        | {@code signer}  | The signer of the sending account of this transaction. Must be the Treasury Compliance account. |
     * | {@code sliding_nonce}     | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                      |
     * | {@code to_freeze_account} | {@code address} | The account address to be frozen.                                                               |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code TreasuryComplianceScripts::unfreeze_account}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param to_freeze_account {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_freeze_account_script_function(@Unsigned Long sliding_nonce, AccountAddress to_freeze_account) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_address_argument(to_freeze_account));
        script_function_builder.function = new Identifier("freeze_account");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("TreasuryComplianceScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Initializes the Diem consensus config that is stored on-chain.  This
     * transaction can only be sent from the Diem Root account.
     *
     * <p><b>Technical Description</b></p>
     * Initializes the {@code DiemConsensusConfig} on-chain config to empty and allows future updates from DiemRoot via
     * {@code update_diem_consensus_config}. This doesn't emit a {@code DiemConfig::NewEpochEvent}.
     *
     * <p><b>Parameters</b></p>
     * | Name            | Type      | Description                                                                |
     * | ------          | ------    | -------------                                                              |
     * | {@code account}       | {@code signer} | Signer of the sending account. Must be the Diem Root account.               |
     * | {@code sliding_nonce} | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction. |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                  | Description                                                                                |
     * | ----------------           | --------------                                | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                | A {@code SlidingNonce} resource is not published under {@code account}.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}       | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::EDIEM_ROOT}                   | {@code account} is not the Diem Root account.                                                    |
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_initialize_diem_consensus_config_script_function(@Unsigned Long sliding_nonce) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce));
        script_function_builder.function = new Identifier("initialize_diem_consensus_config");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("SystemAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Transfers a given number of coins in a specified currency from one account to another.
     * Transfers over a specified amount defined on-chain that are between two different VASPs, or
     * other accounts that have opted-in will be subject to on-chain checks to ensure the receiver has
     * agreed to receive the coins.  This transaction can be sent by any account that can hold a
     * balance, and to any account that can hold a balance. Both accounts must hold balances in the
     * currency being transacted.
     *
     * <p><b>Technical Description</b></p>
     *
     * Transfers {@code amount} coins of type {@code Currency} from {@code payer} to {@code payee} with (optional) associated
     * {@code metadata} and an (optional) {@code metadata_signature} on the message
     * {@code metadata} | {@code Signer::address_of(payer)} | {@code amount} | {@code DualAttestation::DOMAIN_SEPARATOR}.
     * The {@code metadata} and {@code metadata_signature} parameters are only required if {@code amount} {@code >=}
     * {@code DualAttestation::get_cur_microdiem_limit} XDX and {@code payer} and {@code payee} are distinct VASPs.
     * However, a transaction sender can opt in to dual attestation even when it is not required
     * (e.g., a DesignatedDealer -{@code >} VASP payment) by providing a non-empty {@code metadata_signature}.
     * Standardized {@code metadata} BCS format can be found in {@code diem_types::transaction::metadata::Metadata}.
     *
     * <p><b>Events</b></p>
     * Successful execution of this script emits two events:
     * <ul><li>A {@code DiemAccount::SentPaymentEvent} on {@code payer}'s {@code DiemAccount::DiemAccount} {@code sent_events} handle; and</li></ul>
     * <ul><li>A {@code DiemAccount::ReceivedPaymentEvent} on {@code payee}'s {@code DiemAccount::DiemAccount} {@code received_events} handle.</li></ul>
     *
     * <p><b>Parameters</b></p>
     * | Name                 | Type         | Description                                                                                                                  |
     * | ------               | ------       | -------------                                                                                                                |
     * | {@code Currency}           | Type         | The Move type for the {@code Currency} being sent in this transaction. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code payer}              | {@code &signer}    | The signer reference of the sending account that coins are being transferred from.                                           |
     * | {@code payee}              | {@code address}    | The address of the account the coins are being transferred to.                                                               |
     * | {@code metadata}           | {@code vector<u8>} | Optional metadata about this payment.                                                                                        |
     * | {@code metadata_signature} | {@code vector<u8>} | Optional signature over {@code metadata} and payment information. See                                                              |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_child_vasp_account}</li></ul>
     * <ul><li>{@code Script::create_parent_vasp_account}</li></ul>
     * <ul><li>{@code Script::add_currency_to_account}</li></ul>
     *
     * @param currency {@code TypeTag} value
     * @param payee {@code AccountAddress} value
     * @param amount {@code @Unsigned Long} value
     * @param metadata {@code Bytes} value
     * @param metadata_signature {@code Bytes} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_peer_to_peer_with_metadata_script(TypeTag currency, AccountAddress payee, @Unsigned Long amount, Bytes metadata, Bytes metadata_signature) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(PEER_TO_PEER_WITH_METADATA_CODE);
        builder.ty_args = java.util.Arrays.asList(currency);
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(payee), new TransactionArgument.U64(amount), new TransactionArgument.U8Vector(metadata), new TransactionArgument.U8Vector(metadata_signature));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Transfers a given number of coins in a specified currency from one account to another.
     * Transfers over a specified amount defined on-chain that are between two different VASPs, or
     * other accounts that have opted-in will be subject to on-chain checks to ensure the receiver has
     * agreed to receive the coins.  This transaction can be sent by any account that can hold a
     * balance, and to any account that can hold a balance. Both accounts must hold balances in the
     * currency being transacted.
     *
     * <p><b>Technical Description</b></p>
     *
     * Transfers {@code amount} coins of type {@code Currency} from {@code payer} to {@code payee} with (optional) associated
     * {@code metadata} and an (optional) {@code metadata_signature} on the message of the form
     * {@code metadata} | {@code Signer::address_of(payer)} | {@code amount} | {@code DualAttestation::DOMAIN_SEPARATOR}, that
     * has been signed by the {@code payee}'s private key associated with the {@code compliance_public_key} held in
     * the {@code payee}'s {@code DualAttestation::Credential}. Both the {@code Signer::address_of(payer)} and {@code amount} fields
     * in the {@code metadata_signature} must be BCS-encoded bytes, and {@code |} denotes concatenation.
     * The {@code metadata} and {@code metadata_signature} parameters are only required if {@code amount} {@code >=}
     * {@code DualAttestation::get_cur_microdiem_limit} XDX and {@code payer} and {@code payee} are distinct VASPs.
     * However, a transaction sender can opt in to dual attestation even when it is not required
     * (e.g., a DesignatedDealer -{@code >} VASP payment) by providing a non-empty {@code metadata_signature}.
     * Standardized {@code metadata} BCS format can be found in {@code diem_types::transaction::metadata::Metadata}.
     *
     * <p><b>Events</b></p>
     * Successful execution of this script emits two events:
     * <ul><li>A {@code DiemAccount::SentPaymentEvent} on {@code payer}'s {@code DiemAccount::DiemAccount} {@code sent_events} handle; and</li></ul>
     * <ul><li>A {@code DiemAccount::ReceivedPaymentEvent} on {@code payee}'s {@code DiemAccount::DiemAccount} {@code received_events} handle.</li></ul>
     *
     * <p><b>Parameters</b></p>
     * | Name                 | Type         | Description                                                                                                                  |
     * | ------               | ------       | -------------                                                                                                                |
     * | {@code Currency}           | Type         | The Move type for the {@code Currency} being sent in this transaction. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code payer}              | {@code signer}     | The signer of the sending account that coins are being transferred from.                                                     |
     * | {@code payee}              | {@code address}    | The address of the account the coins are being transferred to.                                                               |
     * | {@code metadata}           | {@code vector<u8>} | Optional metadata about this payment.                                                                                        |
     * | {@code metadata_signature} | {@code vector<u8>} | Optional signature over {@code metadata} and payment information. See                                                              |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                     | Description                                                                                                                         |
     * | ----------------           | --------------                                   | -------------                                                                                                                       |
     * | {@code Errors::NOT_PUBLISHED}    | {@code DiemAccount::EPAYER_DOESNT_HOLD_CURRENCY}       | {@code payer} doesn't hold a balance in {@code Currency}.                                                                                       |
     * | {@code Errors::LIMIT_EXCEEDED}   | {@code DiemAccount::EINSUFFICIENT_BALANCE}             | {@code amount} is greater than {@code payer}'s balance in {@code Currency}.                                                                           |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::ECOIN_DEPOSIT_IS_ZERO}             | {@code amount} is zero.                                                                                                                   |
     * | {@code Errors::NOT_PUBLISHED}    | {@code DiemAccount::EPAYEE_DOES_NOT_EXIST}             | No account exists at the {@code payee} address.                                                                                           |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EPAYEE_CANT_ACCEPT_CURRENCY_TYPE}  | An account exists at {@code payee}, but it does not accept payments in {@code Currency}.                                                        |
     * | {@code Errors::INVALID_STATE}    | {@code AccountFreezing::EACCOUNT_FROZEN}               | The {@code payee} account is frozen.                                                                                                      |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DualAttestation::EMALFORMED_METADATA_SIGNATURE} | {@code metadata_signature} is not 64 bytes.                                                                                               |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DualAttestation::EINVALID_METADATA_SIGNATURE}   | {@code metadata_signature} does not verify on the against the {@code payee'}s {@code DualAttestation::Credential} {@code compliance_public_key} public key. |
     * | {@code Errors::LIMIT_EXCEEDED}   | {@code DiemAccount::EWITHDRAWAL_EXCEEDS_LIMITS}        | {@code payer} has exceeded its daily withdrawal limits for the backing coins of XDX.                                                      |
     * | {@code Errors::LIMIT_EXCEEDED}   | {@code DiemAccount::EDEPOSIT_EXCEEDS_LIMITS}           | {@code payee} has exceeded its daily deposit limits for XDX.                                                                              |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_child_vasp_account}</li></ul>
     * <ul><li>{@code AccountCreationScripts::create_parent_vasp_account}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::add_currency_to_account}</li></ul>
     *
     * @param currency {@code TypeTag} value
     * @param payee {@code AccountAddress} value
     * @param amount {@code @Unsigned Long} value
     * @param metadata {@code Bytes} value
     * @param metadata_signature {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_peer_to_peer_with_metadata_script_function(TypeTag currency, AccountAddress payee, @Unsigned Long amount, Bytes metadata, Bytes metadata_signature) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(currency);
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_address_argument(payee), Helpers.encode_u64_argument(amount), Helpers.encode_u8vector_argument(metadata), Helpers.encode_u8vector_argument(metadata_signature));
        script_function_builder.function = new Identifier("peer_to_peer_with_metadata");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("PaymentScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Moves a specified number of coins in a given currency from the account's
     * balance to its preburn area after which the coins may be burned. This
     * transaction may be sent by any account that holds a balance and preburn area
     * in the specified currency.
     *
     * <p><b>Technical Description</b></p>
     * Moves the specified {@code amount} of coins in {@code Token} currency from the sending {@code account}'s
     * {@code DiemAccount::Balance<Token>} to the {@code Diem::Preburn<Token>} published under the same
     * {@code account}. {@code account} must have both of these resources published under it at the start of this
     * transaction in order for it to execute successfully.
     *
     * <p><b>Events</b></p>
     * Successful execution of this script emits two events:
     * <ul><li>{@code DiemAccount::SentPaymentEvent } on {@code account}'s {@code DiemAccount::DiemAccount} {@code sent_events}</li></ul>
     * handle with the {@code payee} and {@code payer} fields being {@code account}'s address; and
     * <ul><li>A {@code Diem::PreburnEvent} with {@code Token}'s currency code on the</li></ul>
     * {@code Diem::CurrencyInfo<Token}'s {@code preburn_events} handle for {@code Token} and with
     * {@code preburn_address} set to {@code account}'s address.
     *
     * <p><b>Parameters</b></p>
     * | Name      | Type      | Description                                                                                                                      |
     * | ------    | ------    | -------------                                                                                                                    |
     * | {@code Token}   | Type      | The Move type for the {@code Token} currency being moved to the preburn area. {@code Token} must be an already-registered currency on-chain. |
     * | {@code account} | {@code &signer} | The signer reference of the sending account.                                                                                     |
     * | {@code amount}  | {@code u64}     | The amount in {@code Token} to be moved to the preburn area.                                                                           |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::cancel_burn}</li></ul>
     * <ul><li>{@code Script::burn}</li></ul>
     * <ul><li>{@code Script::burn_txn_fees}</li></ul>
     *
     * @param token {@code TypeTag} value
     * @param amount {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_preburn_script(TypeTag token, @Unsigned Long amount) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(PREBURN_CODE);
        builder.ty_args = java.util.Arrays.asList(token);
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(amount));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Moves a specified number of coins in a given currency from the account's
     * balance to its preburn area after which the coins may be burned. This
     * transaction may be sent by any account that holds a balance and preburn area
     * in the specified currency.
     *
     * <p><b>Technical Description</b></p>
     * Moves the specified {@code amount} of coins in {@code Token} currency from the sending {@code account}'s
     * {@code DiemAccount::Balance<Token>} to the {@code Diem::Preburn<Token>} published under the same
     * {@code account}. {@code account} must have both of these resources published under it at the start of this
     * transaction in order for it to execute successfully.
     *
     * <p><b>Events</b></p>
     * Successful execution of this script emits two events:
     * <ul><li>{@code DiemAccount::SentPaymentEvent } on {@code account}'s {@code DiemAccount::DiemAccount} {@code sent_events}</li></ul>
     * handle with the {@code payee} and {@code payer} fields being {@code account}'s address; and
     * <ul><li>A {@code Diem::PreburnEvent} with {@code Token}'s currency code on the</li></ul>
     * {@code Diem::CurrencyInfo<Token}'s {@code preburn_events} handle for {@code Token} and with
     * {@code preburn_address} set to {@code account}'s address.
     *
     * <p><b>Parameters</b></p>
     * | Name      | Type     | Description                                                                                                                      |
     * | ------    | ------   | -------------                                                                                                                    |
     * | {@code Token}   | Type     | The Move type for the {@code Token} currency being moved to the preburn area. {@code Token} must be an already-registered currency on-chain. |
     * | {@code account} | {@code signer} | The signer of the sending account.                                                                                               |
     * | {@code amount}  | {@code u64}    | The amount in {@code Token} to be moved to the preburn area.                                                                           |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code TreasuryComplianceScripts::cancel_burn_with_amount}</li></ul>
     * <ul><li>{@code TreasuryComplianceScripts::burn_with_amount}</li></ul>
     * <ul><li>{@code TreasuryComplianceScripts::burn_txn_fees}</li></ul>
     *
     * @param token {@code TypeTag} value
     * @param amount {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_preburn_script_function(TypeTag token, @Unsigned Long amount) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(token);
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(amount));
        script_function_builder.function = new Identifier("preburn");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("TreasuryComplianceScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Rotates the authentication key of the sending account to the
     * newly-specified public key and publishes a new shared authentication key
     * under the sender's account. Any account can send this transaction.
     *
     * <p><b>Technical Description</b></p>
     * Rotates the authentication key of the sending account to {@code public_key},
     * and publishes a {@code SharedEd25519PublicKey::SharedEd25519PublicKey} resource
     * containing the 32-byte ed25519 {@code public_key} and the {@code DiemAccount::KeyRotationCapability} for
     * {@code account} under {@code account}.
     *
     * <p><b>Parameters</b></p>
     * | Name         | Type         | Description                                                                               |
     * | ------       | ------       | -------------                                                                             |
     * | {@code account}    | {@code &signer}    | The signer reference of the sending account of the transaction.                           |
     * | {@code public_key} | {@code vector<u8>} | 32-byte Ed25519 public key for {@code account}' authentication key to be rotated to and stored. |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category              | Error Reason                                               | Description                                                                                         |
     * | ----------------            | --------------                                             | -------------                                                                                       |
     * | {@code Errors::INVALID_STATE}     | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability} resource.       |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code SharedEd25519PublicKey::ESHARED_KEY}                      | The {@code SharedEd25519PublicKey::SharedEd25519PublicKey} resource is already published under {@code account}. |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SharedEd25519PublicKey::EMALFORMED_PUBLIC_KEY}            | {@code public_key} is an invalid ed25519 public key.                                                      |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::rotate_shared_ed25519_public_key}</li></ul>
     *
     * @param public_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_publish_shared_ed25519_public_key_script(Bytes public_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(PUBLISH_SHARED_ED25519_PUBLIC_KEY_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U8Vector(public_key));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Rotates the authentication key of the sending account to the newly-specified ed25519 public key and
     * publishes a new shared authentication key derived from that public key under the sender's account.
     * Any account can send this transaction.
     *
     * <p><b>Technical Description</b></p>
     * Rotates the authentication key of the sending account to the
     * [authentication key derived from {@code public_key}](https://developers.diem.com/docs/core/accounts/#addresses-authentication-keys-and-cryptographic-keys)
     * and publishes a {@code SharedEd25519PublicKey::SharedEd25519PublicKey} resource
     * containing the 32-byte ed25519 {@code public_key} and the {@code DiemAccount::KeyRotationCapability} for
     * {@code account} under {@code account}.
     *
     * <p><b>Parameters</b></p>
     * | Name         | Type         | Description                                                                                        |
     * | ------       | ------       | -------------                                                                                      |
     * | {@code account}    | {@code signer}     | The signer of the sending account of the transaction.                                              |
     * | {@code public_key} | {@code vector<u8>} | A valid 32-byte Ed25519 public key for {@code account}'s authentication key to be rotated to and stored. |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category              | Error Reason                                               | Description                                                                                         |
     * | ----------------            | --------------                                             | -------------                                                                                       |
     * | {@code Errors::INVALID_STATE}     | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability} resource.       |
     * | {@code Errors::ALREADY_PUBLISHED} | {@code SharedEd25519PublicKey::ESHARED_KEY}                      | The {@code SharedEd25519PublicKey::SharedEd25519PublicKey} resource is already published under {@code account}. |
     * | {@code Errors::INVALID_ARGUMENT}  | {@code SharedEd25519PublicKey::EMALFORMED_PUBLIC_KEY}            | {@code public_key} is an invalid ed25519 public key.                                                      |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountAdministrationScripts::rotate_shared_ed25519_public_key}</li></ul>
     *
     * @param public_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_publish_shared_ed25519_public_key_script_function(Bytes public_key) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u8vector_argument(public_key));
        script_function_builder.function = new Identifier("publish_shared_ed25519_public_key");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Updates a validator's configuration. This does not reconfigure the system and will not update
     * the configuration in the validator set that is seen by other validators in the network. Can
     * only be successfully sent by a Validator Operator account that is already registered with a
     * validator.
     *
     * <p><b>Technical Description</b></p>
     * This updates the fields with corresponding names held in the {@code ValidatorConfig::ValidatorConfig}
     * config resource held under {@code validator_account}. It does not emit a {@code DiemConfig::NewEpochEvent}
     * so the copy of this config held in the validator set will not be updated, and the changes are
     * only "locally" under the {@code validator_account} account address.
     *
     * <p><b>Parameters</b></p>
     * | Name                          | Type         | Description                                                                                                                  |
     * | ------                        | ------       | -------------                                                                                                                |
     * | {@code validator_operator_account}  | {@code &signer}    | Signer reference of the sending account. Must be the registered validator operator for the validator at {@code validator_address}. |
     * | {@code validator_account}           | {@code address}    | The address of the validator's {@code ValidatorConfig::ValidatorConfig} resource being updated.                                    |
     * | {@code consensus_pubkey}            | {@code vector<u8>} | New Ed25519 public key to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                                         |
     * | {@code validator_network_addresses} | {@code vector<u8>} | New set of {@code validator_network_addresses} to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                       |
     * | {@code fullnode_network_addresses}  | {@code vector<u8>} | New set of {@code fullnode_network_addresses} to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                        |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                   | Description                                                                                           |
     * | ----------------           | --------------                                 | -------------                                                                                         |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorConfig::EVALIDATOR_CONFIG}           | {@code validator_address} does not have a {@code ValidatorConfig::ValidatorConfig} resource published under it.   |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::EINVALID_TRANSACTION_SENDER} | {@code validator_operator_account} is not the registered operator for the validator at {@code validator_address}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::EINVALID_CONSENSUS_KEY}      | {@code consensus_pubkey} is not a valid ed25519 public key.                                                 |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param validator_account {@code AccountAddress} value
     * @param consensus_pubkey {@code Bytes} value
     * @param validator_network_addresses {@code Bytes} value
     * @param fullnode_network_addresses {@code Bytes} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_register_validator_config_script(AccountAddress validator_account, Bytes consensus_pubkey, Bytes validator_network_addresses, Bytes fullnode_network_addresses) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(REGISTER_VALIDATOR_CONFIG_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(validator_account), new TransactionArgument.U8Vector(consensus_pubkey), new TransactionArgument.U8Vector(validator_network_addresses), new TransactionArgument.U8Vector(fullnode_network_addresses));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Updates a validator's configuration. This does not reconfigure the system and will not update
     * the configuration in the validator set that is seen by other validators in the network. Can
     * only be successfully sent by a Validator Operator account that is already registered with a
     * validator.
     *
     * <p><b>Technical Description</b></p>
     * This updates the fields with corresponding names held in the {@code ValidatorConfig::ValidatorConfig}
     * config resource held under {@code validator_account}. It does not emit a {@code DiemConfig::NewEpochEvent}
     * so the copy of this config held in the validator set will not be updated, and the changes are
     * only "locally" under the {@code validator_account} account address.
     *
     * <p><b>Parameters</b></p>
     * | Name                          | Type         | Description                                                                                                        |
     * | ------                        | ------       | -------------                                                                                                      |
     * | {@code validator_operator_account}  | {@code signer}     | Signer of the sending account. Must be the registered validator operator for the validator at {@code validator_address}. |
     * | {@code validator_account}           | {@code address}    | The address of the validator's {@code ValidatorConfig::ValidatorConfig} resource being updated.                          |
     * | {@code consensus_pubkey}            | {@code vector<u8>} | New Ed25519 public key to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                               |
     * | {@code validator_network_addresses} | {@code vector<u8>} | New set of {@code validator_network_addresses} to be used in the updated {@code ValidatorConfig::ValidatorConfig}.             |
     * | {@code fullnode_network_addresses}  | {@code vector<u8>} | New set of {@code fullnode_network_addresses} to be used in the updated {@code ValidatorConfig::ValidatorConfig}.              |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                   | Description                                                                                           |
     * | ----------------           | --------------                                 | -------------                                                                                         |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorConfig::EVALIDATOR_CONFIG}           | {@code validator_address} does not have a {@code ValidatorConfig::ValidatorConfig} resource published under it.   |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::EINVALID_TRANSACTION_SENDER} | {@code validator_operator_account} is not the registered operator for the validator at {@code validator_address}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::EINVALID_CONSENSUS_KEY}      | {@code consensus_pubkey} is not a valid ed25519 public key.                                                 |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_validator_account}</li></ul>
     * <ul><li>{@code AccountCreationScripts::create_validator_operator_account}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param validator_account {@code AccountAddress} value
     * @param consensus_pubkey {@code Bytes} value
     * @param validator_network_addresses {@code Bytes} value
     * @param fullnode_network_addresses {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_register_validator_config_script_function(AccountAddress validator_account, Bytes consensus_pubkey, Bytes validator_network_addresses, Bytes fullnode_network_addresses) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_address_argument(validator_account), Helpers.encode_u8vector_argument(consensus_pubkey), Helpers.encode_u8vector_argument(validator_network_addresses), Helpers.encode_u8vector_argument(fullnode_network_addresses));
        script_function_builder.function = new Identifier("register_validator_config");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("ValidatorAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * This script removes a validator account from the validator set, and triggers a reconfiguration
     * of the system to remove the validator from the system. This transaction can only be
     * successfully called by the Diem Root account.
     *
     * <p><b>Technical Description</b></p>
     * This script removes the account at {@code validator_address} from the validator set. This transaction
     * emits a {@code DiemConfig::NewEpochEvent} event. Once the reconfiguration triggered by this event
     * has been performed, the account at {@code validator_address} is no longer considered to be a
     * validator in the network. This transaction will fail if the validator at {@code validator_address}
     * is not in the validator set.
     *
     * <p><b>Parameters</b></p>
     * | Name                | Type         | Description                                                                                                                        |
     * | ------              | ------       | -------------                                                                                                                      |
     * | {@code dr_account}        | {@code &signer}    | The signer reference of the sending account of this transaction. Must be the Diem Root signer.                                    |
     * | {@code sliding_nonce}     | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                                         |
     * | {@code validator_name}    | {@code vector<u8>} | ASCII-encoded human name for the validator. Must match the human name in the {@code ValidatorConfig::ValidatorConfig} for the validator. |
     * | {@code validator_address} | {@code address}    | The validator account address to be removed from the validator set.                                                                |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param validator_name {@code Bytes} value
     * @param validator_address {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_remove_validator_and_reconfigure_script(@Unsigned Long sliding_nonce, Bytes validator_name, AccountAddress validator_address) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(REMOVE_VALIDATOR_AND_RECONFIGURE_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U8Vector(validator_name), new TransactionArgument.Address(validator_address));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * This script removes a validator account from the validator set, and triggers a reconfiguration
     * of the system to remove the validator from the system. This transaction can only be
     * successfully called by the Diem Root account.
     *
     * <p><b>Technical Description</b></p>
     * This script removes the account at {@code validator_address} from the validator set. This transaction
     * emits a {@code DiemConfig::NewEpochEvent} event. Once the reconfiguration triggered by this event
     * has been performed, the account at {@code validator_address} is no longer considered to be a
     * validator in the network. This transaction will fail if the validator at {@code validator_address}
     * is not in the validator set.
     *
     * <p><b>Parameters</b></p>
     * | Name                | Type         | Description                                                                                                                        |
     * | ------              | ------       | -------------                                                                                                                      |
     * | {@code dr_account}        | {@code signer}     | The signer of the sending account of this transaction. Must be the Diem Root signer.                                               |
     * | {@code sliding_nonce}     | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                                         |
     * | {@code validator_name}    | {@code vector<u8>} | ASCII-encoded human name for the validator. Must match the human name in the {@code ValidatorConfig::ValidatorConfig} for the validator. |
     * | {@code validator_address} | {@code address}    | The validator account address to be removed from the validator set.                                                                |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_validator_account}</li></ul>
     * <ul><li>{@code AccountCreationScripts::create_validator_operator_account}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::register_validator_config}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param validator_name {@code Bytes} value
     * @param validator_address {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_remove_validator_and_reconfigure_script_function(@Unsigned Long sliding_nonce, Bytes validator_name, AccountAddress validator_address) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_u8vector_argument(validator_name), Helpers.encode_address_argument(validator_address));
        script_function_builder.function = new Identifier("remove_validator_and_reconfigure");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("ValidatorAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Rotates the transaction sender's authentication key to the supplied new authentication key. May
     * be sent by any account.
     *
     * <p><b>Technical Description</b></p>
     * Rotate the {@code account}'s {@code DiemAccount::DiemAccount} {@code authentication_key} field to {@code new_key}.
     * {@code new_key} must be a valid ed25519 public key, and {@code account} must not have previously delegated
     * its {@code DiemAccount::KeyRotationCapability}.
     *
     * <p><b>Parameters</b></p>
     * | Name      | Type         | Description                                                 |
     * | ------    | ------       | -------------                                               |
     * | {@code account} | {@code &signer}    | Signer reference of the sending account of the transaction. |
     * | {@code new_key} | {@code vector<u8>} | New ed25519 public key to be used for {@code account}.            |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                               | Description                                                                              |
     * | ----------------           | --------------                                             | -------------                                                                            |
     * | {@code Errors::INVALID_STATE}    | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.     |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY}              | {@code new_key} was an invalid length.                                                         |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::rotate_authentication_key_with_nonce}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_recovery_address}</li></ul>
     *
     * @param new_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_rotate_authentication_key_script(Bytes new_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ROTATE_AUTHENTICATION_KEY_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U8Vector(new_key));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Rotates the {@code account}'s authentication key to the supplied new authentication key. May be sent by any account.
     *
     * <p><b>Technical Description</b></p>
     * Rotate the {@code account}'s {@code DiemAccount::DiemAccount} {@code authentication_key}
     * field to {@code new_key}. {@code new_key} must be a valid authentication key that
     * corresponds to an ed25519 public key as described [here](https://developers.diem.com/docs/core/accounts/#addresses-authentication-keys-and-cryptographic-keys),
     * and {@code account} must not have previously delegated its {@code DiemAccount::KeyRotationCapability}.
     *
     * <p><b>Parameters</b></p>
     * | Name      | Type         | Description                                       |
     * | ------    | ------       | -------------                                     |
     * | {@code account} | {@code signer}     | Signer of the sending account of the transaction. |
     * | {@code new_key} | {@code vector<u8>} | New authentication key to be used for {@code account}.  |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                              | Description                                                                         |
     * | ----------------           | --------------                                            | -------------                                                                       |
     * | {@code Errors::INVALID_STATE}    | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY}              | {@code new_key} was an invalid length.                                                    |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key_with_nonce}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key_with_nonce_admin}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key_with_recovery_address}</li></ul>
     *
     * @param new_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_rotate_authentication_key_script_function(Bytes new_key) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u8vector_argument(new_key));
        script_function_builder.function = new Identifier("rotate_authentication_key");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Rotates the sender's authentication key to the supplied new authentication key. May be sent by
     * any account that has a sliding nonce resource published under it (usually this is Treasury
     * Compliance or Diem Root accounts).
     *
     * <p><b>Technical Description</b></p>
     * Rotates the {@code account}'s {@code DiemAccount::DiemAccount} {@code authentication_key} field to {@code new_key}.
     * {@code new_key} must be a valid ed25519 public key, and {@code account} must not have previously delegated
     * its {@code DiemAccount::KeyRotationCapability}.
     *
     * <p><b>Parameters</b></p>
     * | Name            | Type         | Description                                                                |
     * | ------          | ------       | -------------                                                              |
     * | {@code account}       | {@code &signer}    | Signer reference of the sending account of the transaction.                |
     * | {@code sliding_nonce} | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction. |
     * | {@code new_key}       | {@code vector<u8>} | New ed25519 public key to be used for {@code account}.                           |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                               | Description                                                                                |
     * | ----------------           | --------------                                             | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                             | A {@code SlidingNonce} resource is not published under {@code account}.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                             | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                             | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}                    | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::INVALID_STATE}    | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.       |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY}              | {@code new_key} was an invalid length.                                                           |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::rotate_authentication_key}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_recovery_address}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_rotate_authentication_key_with_nonce_script(@Unsigned Long sliding_nonce, Bytes new_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ROTATE_AUTHENTICATION_KEY_WITH_NONCE_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U8Vector(new_key));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Rotates the sender's authentication key to the supplied new authentication key. May be sent by
     * any account that has a sliding nonce resource published under it (usually this is Treasury
     * Compliance or Diem Root accounts).
     *
     * <p><b>Technical Description</b></p>
     * Rotates the {@code account}'s {@code DiemAccount::DiemAccount} {@code authentication_key}
     * field to {@code new_key}. {@code new_key} must be a valid authentication key that
     * corresponds to an ed25519 public key as described [here](https://developers.diem.com/docs/core/accounts/#addresses-authentication-keys-and-cryptographic-keys),
     * and {@code account} must not have previously delegated its {@code DiemAccount::KeyRotationCapability}.
     *
     * <p><b>Parameters</b></p>
     * | Name            | Type         | Description                                                                |
     * | ------          | ------       | -------------                                                              |
     * | {@code account}       | {@code signer}     | Signer of the sending account of the transaction.                          |
     * | {@code sliding_nonce} | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction. |
     * | {@code new_key}       | {@code vector<u8>} | New authentication key to be used for {@code account}.                           |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                               | Description                                                                                |
     * | ----------------           | --------------                                             | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                             | A {@code SlidingNonce} resource is not published under {@code account}.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                             | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                             | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}                    | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::INVALID_STATE}    | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.       |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY}              | {@code new_key} was an invalid length.                                                           |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key_with_nonce_admin}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key_with_recovery_address}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_rotate_authentication_key_with_nonce_script_function(@Unsigned Long sliding_nonce, Bytes new_key) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_u8vector_argument(new_key));
        script_function_builder.function = new Identifier("rotate_authentication_key_with_nonce");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Rotates the specified account's authentication key to the supplied new authentication key. May
     * only be sent by the Diem Root account as a write set transaction.
     *
     * <p><b>Technical Description</b></p>
     * Rotate the {@code account}'s {@code DiemAccount::DiemAccount} {@code authentication_key} field to {@code new_key}.
     * {@code new_key} must be a valid ed25519 public key, and {@code account} must not have previously delegated
     * its {@code DiemAccount::KeyRotationCapability}.
     *
     * <p><b>Parameters</b></p>
     * | Name            | Type         | Description                                                                                                  |
     * | ------          | ------       | -------------                                                                                                |
     * | {@code dr_account}    | {@code &signer}    | The signer reference of the sending account of the write set transaction. May only be the Diem Root signer. |
     * | {@code account}       | {@code &signer}    | Signer reference of account specified in the {@code execute_as} field of the write set transaction.                |
     * | {@code sliding_nonce} | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction for Diem Root.                    |
     * | {@code new_key}       | {@code vector<u8>} | New ed25519 public key to be used for {@code account}.                                                             |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                               | Description                                                                                                |
     * | ----------------           | --------------                                             | -------------                                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                             | A {@code SlidingNonce} resource is not published under {@code dr_account}.                                             |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                             | The {@code sliding_nonce} in {@code dr_account} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                             | The {@code sliding_nonce} in {@code dr_account} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}                    | The {@code sliding_nonce} in{@code  dr_account} has been previously recorded.                                          |
     * | {@code Errors::INVALID_STATE}    | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.                       |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY}              | {@code new_key} was an invalid length.                                                                           |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::rotate_authentication_key}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_nonce}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_recovery_address}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_rotate_authentication_key_with_nonce_admin_script(@Unsigned Long sliding_nonce, Bytes new_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ROTATE_AUTHENTICATION_KEY_WITH_NONCE_ADMIN_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U8Vector(new_key));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Rotates the specified account's authentication key to the supplied new authentication key. May
     * only be sent by the Diem Root account as a write set transaction.
     *
     * <p><b>Technical Description</b></p>
     * Rotate the {@code account}'s {@code DiemAccount::DiemAccount} {@code authentication_key} field to {@code new_key}.
     * {@code new_key} must be a valid authentication key that corresponds to an ed25519
     * public key as described [here](https://developers.diem.com/docs/core/accounts/#addresses-authentication-keys-and-cryptographic-keys),
     * and {@code account} must not have previously delegated its {@code DiemAccount::KeyRotationCapability}.
     *
     * <p><b>Parameters</b></p>
     * | Name            | Type         | Description                                                                                       |
     * | ------          | ------       | -------------                                                                                     |
     * | {@code dr_account}    | {@code signer}     | The signer of the sending account of the write set transaction. May only be the Diem Root signer. |
     * | {@code account}       | {@code signer}     | Signer of account specified in the {@code execute_as} field of the write set transaction.               |
     * | {@code sliding_nonce} | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction for Diem Root.          |
     * | {@code new_key}       | {@code vector<u8>} | New authentication key to be used for {@code account}.                                                  |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                              | Description                                                                                                |
     * | ----------------           | --------------                                            | -------------                                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                            | A {@code SlidingNonce} resource is not published under {@code dr_account}.                                             |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                            | The {@code sliding_nonce} in {@code dr_account} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                            | The {@code sliding_nonce} in {@code dr_account} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}                   | The {@code sliding_nonce} in{@code  dr_account} has been previously recorded.                                          |
     * | {@code Errors::INVALID_STATE}    | {@code DiemAccount::EKEY_ROTATION_CAPABILITY_ALREADY_EXTRACTED} | {@code account} has already delegated/extracted its {@code DiemAccount::KeyRotationCapability}.                        |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY}              | {@code new_key} was an invalid length.                                                                           |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key_with_nonce}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key_with_recovery_address}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_rotate_authentication_key_with_nonce_admin_script_function(@Unsigned Long sliding_nonce, Bytes new_key) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_u8vector_argument(new_key));
        script_function_builder.function = new Identifier("rotate_authentication_key_with_nonce_admin");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Rotates the authentication key of a specified account that is part of a recovery address to a
     * new authentication key. Only used for accounts that are part of a recovery address (see
     * {@code Script::add_recovery_rotation_capability} for account restrictions).
     *
     * <p><b>Technical Description</b></p>
     * Rotates the authentication key of the {@code to_recover} account to {@code new_key} using the
     * {@code DiemAccount::KeyRotationCapability} stored in the {@code RecoveryAddress::RecoveryAddress} resource
     * published under {@code recovery_address}. This transaction can be sent either by the {@code to_recover}
     * account, or by the account where the {@code RecoveryAddress::RecoveryAddress} resource is published
     * that contains {@code to_recover}'s {@code DiemAccount::KeyRotationCapability}.
     *
     * <p><b>Parameters</b></p>
     * | Name               | Type         | Description                                                                                                                    |
     * | ------             | ------       | -------------                                                                                                                  |
     * | {@code account}          | {@code &signer}    | Signer reference of the sending account of the transaction.                                                                    |
     * | {@code recovery_address} | {@code address}    | Address where {@code RecoveryAddress::RecoveryAddress} that holds {@code to_recover}'s {@code DiemAccount::KeyRotationCapability} is published. |
     * | {@code to_recover}       | {@code address}    | The address of the account whose authentication key will be updated.                                                           |
     * | {@code new_key}          | {@code vector<u8>} | New ed25519 public key to be used for the account at the {@code to_recover} address.                                                 |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                  | Description                                                                                                                                          |
     * | ----------------           | --------------                                | -------------                                                                                                                                        |
     * | {@code Errors::NOT_PUBLISHED}    | {@code RecoveryAddress::ERECOVERY_ADDRESS}          | {@code recovery_address} does not have a {@code RecoveryAddress::RecoveryAddress} resource published under it.                                                   |
     * | {@code Errors::INVALID_ARGUMENT} | {@code RecoveryAddress::ECANNOT_ROTATE_KEY}         | The address of {@code account} is not {@code recovery_address} or {@code to_recover}.                                                                                  |
     * | {@code Errors::INVALID_ARGUMENT} | {@code RecoveryAddress::EACCOUNT_NOT_RECOVERABLE}   | {@code to_recover}'s {@code DiemAccount::KeyRotationCapability}  is not in the {@code RecoveryAddress::RecoveryAddress}  resource published under {@code recovery_address}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY} | {@code new_key} was an invalid length.                                                                                                                     |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::rotate_authentication_key}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_nonce}</li></ul>
     * <ul><li>{@code Script::rotate_authentication_key_with_nonce_admin}</li></ul>
     *
     * @param recovery_address {@code AccountAddress} value
     * @param to_recover {@code AccountAddress} value
     * @param new_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_rotate_authentication_key_with_recovery_address_script(AccountAddress recovery_address, AccountAddress to_recover, Bytes new_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ROTATE_AUTHENTICATION_KEY_WITH_RECOVERY_ADDRESS_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(recovery_address), new TransactionArgument.Address(to_recover), new TransactionArgument.U8Vector(new_key));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Rotates the authentication key of a specified account that is part of a recovery address to a
     * new authentication key. Only used for accounts that are part of a recovery address (see
     * {@code AccountAdministrationScripts::add_recovery_rotation_capability} for account restrictions).
     *
     * <p><b>Technical Description</b></p>
     * Rotates the authentication key of the {@code to_recover} account to {@code new_key} using the
     * {@code DiemAccount::KeyRotationCapability} stored in the {@code RecoveryAddress::RecoveryAddress} resource
     * published under {@code recovery_address}. {@code new_key} must be a valide authentication key as described
     * [here](https://developers.diem.com/docs/core/accounts/#addresses-authentication-keys-and-cryptographic-keys).
     * This transaction can be sent either by the {@code to_recover} account, or by the account where the
     * {@code RecoveryAddress::RecoveryAddress} resource is published that contains {@code to_recover}'s {@code DiemAccount::KeyRotationCapability}.
     *
     * <p><b>Parameters</b></p>
     * | Name               | Type         | Description                                                                                                                   |
     * | ------             | ------       | -------------                                                                                                                 |
     * | {@code account}          | {@code signer}     | Signer of the sending account of the transaction.                                                                             |
     * | {@code recovery_address} | {@code address}    | Address where {@code RecoveryAddress::RecoveryAddress} that holds {@code to_recover}'s {@code DiemAccount::KeyRotationCapability} is published. |
     * | {@code to_recover}       | {@code address}    | The address of the account whose authentication key will be updated.                                                          |
     * | {@code new_key}          | {@code vector<u8>} | New authentication key to be used for the account at the {@code to_recover} address.                                                |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                 | Description                                                                                                                                         |
     * | ----------------           | --------------                               | -------------                                                                                                                                       |
     * | {@code Errors::NOT_PUBLISHED}    | {@code RecoveryAddress::ERECOVERY_ADDRESS}         | {@code recovery_address} does not have a {@code RecoveryAddress::RecoveryAddress} resource published under it.                                                  |
     * | {@code Errors::INVALID_ARGUMENT} | {@code RecoveryAddress::ECANNOT_ROTATE_KEY}        | The address of {@code account} is not {@code recovery_address} or {@code to_recover}.                                                                                 |
     * | {@code Errors::INVALID_ARGUMENT} | {@code RecoveryAddress::EACCOUNT_NOT_RECOVERABLE}  | {@code to_recover}'s {@code DiemAccount::KeyRotationCapability}  is not in the {@code RecoveryAddress::RecoveryAddress}  resource published under {@code recovery_address}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemAccount::EMALFORMED_AUTHENTICATION_KEY} | {@code new_key} was an invalid length.                                                                                                                    |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key_with_nonce}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_authentication_key_with_nonce_admin}</li></ul>
     *
     * @param recovery_address {@code AccountAddress} value
     * @param to_recover {@code AccountAddress} value
     * @param new_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_rotate_authentication_key_with_recovery_address_script_function(AccountAddress recovery_address, AccountAddress to_recover, Bytes new_key) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_address_argument(recovery_address), Helpers.encode_address_argument(to_recover), Helpers.encode_u8vector_argument(new_key));
        script_function_builder.function = new Identifier("rotate_authentication_key_with_recovery_address");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Updates the url used for off-chain communication, and the public key used to verify dual
     * attestation on-chain. Transaction can be sent by any account that has dual attestation
     * information published under it. In practice the only such accounts are Designated Dealers and
     * Parent VASPs.
     *
     * <p><b>Technical Description</b></p>
     * Updates the {@code base_url} and {@code compliance_public_key} fields of the {@code DualAttestation::Credential}
     * resource published under {@code account}. The {@code new_key} must be a valid ed25519 public key.
     *
     * <p><b>Events</b></p>
     * Successful execution of this transaction emits two events:
     * <ul><li>A {@code DualAttestation::ComplianceKeyRotationEvent} containing the new compliance public key, and</li></ul>
     * the blockchain time at which the key was updated emitted on the {@code DualAttestation::Credential}
     * {@code compliance_key_rotation_events} handle published under {@code account}; and
     * <ul><li>A {@code DualAttestation::BaseUrlRotationEvent} containing the new base url to be used for</li></ul>
     * off-chain communication, and the blockchain time at which the url was updated emitted on the
     * {@code DualAttestation::Credential} {@code base_url_rotation_events} handle published under {@code account}.
     *
     * <p><b>Parameters</b></p>
     * | Name      | Type         | Description                                                               |
     * | ------    | ------       | -------------                                                             |
     * | {@code account} | {@code &signer}    | Signer reference of the sending account of the transaction.               |
     * | {@code new_url} | {@code vector<u8>} | ASCII-encoded url to be used for off-chain communication with {@code account}.  |
     * | {@code new_key} | {@code vector<u8>} | New ed25519 public key to be used for on-chain dual attestation checking. |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                           | Description                                                                |
     * | ----------------           | --------------                         | -------------                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code DualAttestation::ECREDENTIAL}         | A {@code DualAttestation::Credential} resource is not published under {@code account}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DualAttestation::EINVALID_PUBLIC_KEY} | {@code new_key} is not a valid ed25519 public key.                               |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_parent_vasp_account}</li></ul>
     * <ul><li>{@code Script::create_designated_dealer}</li></ul>
     * <ul><li>{@code Script::rotate_dual_attestation_info}</li></ul>
     *
     * @param new_url {@code Bytes} value
     * @param new_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_rotate_dual_attestation_info_script(Bytes new_url, Bytes new_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ROTATE_DUAL_ATTESTATION_INFO_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U8Vector(new_url), new TransactionArgument.U8Vector(new_key));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Updates the url used for off-chain communication, and the public key used to verify dual
     * attestation on-chain. Transaction can be sent by any account that has dual attestation
     * information published under it. In practice the only such accounts are Designated Dealers and
     * Parent VASPs.
     *
     * <p><b>Technical Description</b></p>
     * Updates the {@code base_url} and {@code compliance_public_key} fields of the {@code DualAttestation::Credential}
     * resource published under {@code account}. The {@code new_key} must be a valid ed25519 public key.
     *
     * <p><b>Events</b></p>
     * Successful execution of this transaction emits two events:
     * <ul><li>A {@code DualAttestation::ComplianceKeyRotationEvent} containing the new compliance public key, and</li></ul>
     * the blockchain time at which the key was updated emitted on the {@code DualAttestation::Credential}
     * {@code compliance_key_rotation_events} handle published under {@code account}; and
     * <ul><li>A {@code DualAttestation::BaseUrlRotationEvent} containing the new base url to be used for</li></ul>
     * off-chain communication, and the blockchain time at which the url was updated emitted on the
     * {@code DualAttestation::Credential} {@code base_url_rotation_events} handle published under {@code account}.
     *
     * <p><b>Parameters</b></p>
     * | Name      | Type         | Description                                                               |
     * | ------    | ------       | -------------                                                             |
     * | {@code account} | {@code signer}     | Signer of the sending account of the transaction.                         |
     * | {@code new_url} | {@code vector<u8>} | ASCII-encoded url to be used for off-chain communication with {@code account}.  |
     * | {@code new_key} | {@code vector<u8>} | New ed25519 public key to be used for on-chain dual attestation checking. |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                           | Description                                                                |
     * | ----------------           | --------------                         | -------------                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code DualAttestation::ECREDENTIAL}         | A {@code DualAttestation::Credential} resource is not published under {@code account}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DualAttestation::EINVALID_PUBLIC_KEY} | {@code new_key} is not a valid ed25519 public key.                               |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_parent_vasp_account}</li></ul>
     * <ul><li>{@code AccountCreationScripts::create_designated_dealer}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_dual_attestation_info}</li></ul>
     *
     * @param new_url {@code Bytes} value
     * @param new_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_rotate_dual_attestation_info_script_function(Bytes new_url, Bytes new_key) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u8vector_argument(new_url), Helpers.encode_u8vector_argument(new_key));
        script_function_builder.function = new Identifier("rotate_dual_attestation_info");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Rotates the authentication key in a {@code SharedEd25519PublicKey}. This transaction can be sent by
     * any account that has previously published a shared ed25519 public key using
     * {@code Script::publish_shared_ed25519_public_key}.
     *
     * <p><b>Technical Description</b></p>
     * This first rotates the public key stored in {@code account}'s
     * {@code SharedEd25519PublicKey::SharedEd25519PublicKey} resource to {@code public_key}, after which it
     * rotates the authentication key using the capability stored in {@code account}'s
     * {@code SharedEd25519PublicKey::SharedEd25519PublicKey} to a new value derived from {@code public_key}
     *
     * <p><b>Parameters</b></p>
     * | Name         | Type         | Description                                                     |
     * | ------       | ------       | -------------                                                   |
     * | {@code account}    | {@code &signer}    | The signer reference of the sending account of the transaction. |
     * | {@code public_key} | {@code vector<u8>} | 32-byte Ed25519 public key.                                     |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                    | Description                                                                                   |
     * | ----------------           | --------------                                  | -------------                                                                                 |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SharedEd25519PublicKey::ESHARED_KEY}           | A {@code SharedEd25519PublicKey::SharedEd25519PublicKey} resource is not published under {@code account}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SharedEd25519PublicKey::EMALFORMED_PUBLIC_KEY} | {@code public_key} is an invalid ed25519 public key.                                                |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::publish_shared_ed25519_public_key}</li></ul>
     *
     * @param public_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_rotate_shared_ed25519_public_key_script(Bytes public_key) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(ROTATE_SHARED_ED25519_PUBLIC_KEY_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U8Vector(public_key));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Rotates the authentication key in a {@code SharedEd25519PublicKey}. This transaction can be sent by
     * any account that has previously published a shared ed25519 public key using
     * {@code AccountAdministrationScripts::publish_shared_ed25519_public_key}.
     *
     * <p><b>Technical Description</b></p>
     * {@code public_key} must be a valid ed25519 public key.  This transaction first rotates the public key stored in {@code account}'s
     * {@code SharedEd25519PublicKey::SharedEd25519PublicKey} resource to {@code public_key}, after which it
     * rotates the {@code account}'s authentication key to the new authentication key derived from {@code public_key} as defined
     * [here](https://developers.diem.com/docs/core/accounts/#addresses-authentication-keys-and-cryptographic-keys)
     * using the {@code DiemAccount::KeyRotationCapability} stored in {@code account}'s {@code SharedEd25519PublicKey::SharedEd25519PublicKey}.
     *
     * <p><b>Parameters</b></p>
     * | Name         | Type         | Description                                           |
     * | ------       | ------       | -------------                                         |
     * | {@code account}    | {@code signer}     | The signer of the sending account of the transaction. |
     * | {@code public_key} | {@code vector<u8>} | 32-byte Ed25519 public key.                           |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                    | Description                                                                                   |
     * | ----------------           | --------------                                  | -------------                                                                                 |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SharedEd25519PublicKey::ESHARED_KEY}           | A {@code SharedEd25519PublicKey::SharedEd25519PublicKey} resource is not published under {@code account}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SharedEd25519PublicKey::EMALFORMED_PUBLIC_KEY} | {@code public_key} is an invalid ed25519 public key.                                                |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountAdministrationScripts::publish_shared_ed25519_public_key}</li></ul>
     *
     * @param public_key {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_rotate_shared_ed25519_public_key_script_function(Bytes public_key) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u8vector_argument(public_key));
        script_function_builder.function = new Identifier("rotate_shared_ed25519_public_key");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("AccountAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Updates the gas constants stored on chain and used by the VM for gas
     * metering. This transaction can only be sent from the Diem Root account.
     *
     * <p><b>Technical Description</b></p>
     * Updates the on-chain config holding the {@code DiemVMConfig} and emits a
     * {@code DiemConfig::NewEpochEvent} to trigger a reconfiguration of the system.
     *
     * <p><b>Parameters</b></p>
     * | Name                                | Type     | Description                                                                                            |
     * | ------                              | ------   | -------------                                                                                          |
     * | {@code account}                           | {@code signer} | Signer of the sending account. Must be the Diem Root account.                                          |
     * | {@code sliding_nonce}                     | {@code u64}    | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                             |
     * | {@code global_memory_per_byte_cost}       | {@code u64}    | The new cost to read global memory per-byte to be used for gas metering.                               |
     * | {@code global_memory_per_byte_write_cost} | {@code u64}    | The new cost to write global memory per-byte to be used for gas metering.                              |
     * | {@code min_transaction_gas_units}         | {@code u64}    | The new flat minimum amount of gas required for any transaction.                                       |
     * | {@code large_transaction_cutoff}          | {@code u64}    | The new size over which an additional charge will be assessed for each additional byte.                |
     * | {@code intrinsic_gas_per_byte}            | {@code u64}    | The new number of units of gas that to be charged per-byte over the new {@code large_transaction_cutoff}.    |
     * | {@code maximum_number_of_gas_units}       | {@code u64}    | The new maximum number of gas units that can be set in a transaction.                                  |
     * | {@code min_price_per_gas_unit}            | {@code u64}    | The new minimum gas price that can be set for a transaction.                                           |
     * | {@code max_price_per_gas_unit}            | {@code u64}    | The new maximum gas price that can be set for a transaction.                                           |
     * | {@code max_transaction_size_in_bytes}     | {@code u64}    | The new maximum size of a transaction that can be processed.                                           |
     * | {@code gas_unit_scaling_factor}           | {@code u64}    | The new scaling factor to use when scaling between external and internal gas units.                    |
     * | {@code default_account_size}              | {@code u64}    | The new default account size to use when assessing final costs for reads and writes to global storage. |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                | Description                                                                                |
     * | ----------------           | --------------                              | -------------                                                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemVMConfig::EGAS_CONSTANT_INCONSISTENCY} | The provided gas constants are inconsistent.                                               |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}              | A {@code SlidingNonce} resource is not published under {@code account}.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}              | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}              | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}     | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::EDIEM_ROOT}                 | {@code account} is not the Diem Root account.                                                    |
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param global_memory_per_byte_cost {@code @Unsigned Long} value
     * @param global_memory_per_byte_write_cost {@code @Unsigned Long} value
     * @param min_transaction_gas_units {@code @Unsigned Long} value
     * @param large_transaction_cutoff {@code @Unsigned Long} value
     * @param intrinsic_gas_per_byte {@code @Unsigned Long} value
     * @param maximum_number_of_gas_units {@code @Unsigned Long} value
     * @param min_price_per_gas_unit {@code @Unsigned Long} value
     * @param max_price_per_gas_unit {@code @Unsigned Long} value
     * @param max_transaction_size_in_bytes {@code @Unsigned Long} value
     * @param gas_unit_scaling_factor {@code @Unsigned Long} value
     * @param default_account_size {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_set_gas_constants_script_function(@Unsigned Long sliding_nonce, @Unsigned Long global_memory_per_byte_cost, @Unsigned Long global_memory_per_byte_write_cost, @Unsigned Long min_transaction_gas_units, @Unsigned Long large_transaction_cutoff, @Unsigned Long intrinsic_gas_per_byte, @Unsigned Long maximum_number_of_gas_units, @Unsigned Long min_price_per_gas_unit, @Unsigned Long max_price_per_gas_unit, @Unsigned Long max_transaction_size_in_bytes, @Unsigned Long gas_unit_scaling_factor, @Unsigned Long default_account_size) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_u64_argument(global_memory_per_byte_cost), Helpers.encode_u64_argument(global_memory_per_byte_write_cost), Helpers.encode_u64_argument(min_transaction_gas_units), Helpers.encode_u64_argument(large_transaction_cutoff), Helpers.encode_u64_argument(intrinsic_gas_per_byte), Helpers.encode_u64_argument(maximum_number_of_gas_units), Helpers.encode_u64_argument(min_price_per_gas_unit), Helpers.encode_u64_argument(max_price_per_gas_unit), Helpers.encode_u64_argument(max_transaction_size_in_bytes), Helpers.encode_u64_argument(gas_unit_scaling_factor), Helpers.encode_u64_argument(default_account_size));
        script_function_builder.function = new Identifier("set_gas_constants");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("SystemAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Updates a validator's configuration, and triggers a reconfiguration of the system to update the
     * validator set with this new validator configuration.  Can only be successfully sent by a
     * Validator Operator account that is already registered with a validator.
     *
     * <p><b>Technical Description</b></p>
     * This updates the fields with corresponding names held in the {@code ValidatorConfig::ValidatorConfig}
     * config resource held under {@code validator_account}. It then emits a {@code DiemConfig::NewEpochEvent} to
     * trigger a reconfiguration of the system.  This reconfiguration will update the validator set
     * on-chain with the updated {@code ValidatorConfig::ValidatorConfig}.
     *
     * <p><b>Parameters</b></p>
     * | Name                          | Type         | Description                                                                                                                  |
     * | ------                        | ------       | -------------                                                                                                                |
     * | {@code validator_operator_account}  | {@code &signer}    | Signer reference of the sending account. Must be the registered validator operator for the validator at {@code validator_address}. |
     * | {@code validator_account}           | {@code address}    | The address of the validator's {@code ValidatorConfig::ValidatorConfig} resource being updated.                                    |
     * | {@code consensus_pubkey}            | {@code vector<u8>} | New Ed25519 public key to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                                         |
     * | {@code validator_network_addresses} | {@code vector<u8>} | New set of {@code validator_network_addresses} to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                       |
     * | {@code fullnode_network_addresses}  | {@code vector<u8>} | New set of {@code fullnode_network_addresses} to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                        |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                   | Description                                                                                           |
     * | ----------------           | --------------                                 | -------------                                                                                         |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorConfig::EVALIDATOR_CONFIG}           | {@code validator_address} does not have a {@code ValidatorConfig::ValidatorConfig} resource published under it.   |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::EVALIDATOR_OPERATOR}                   | {@code validator_operator_account} does not have a Validator Operator role.                                 |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::EINVALID_TRANSACTION_SENDER} | {@code validator_operator_account} is not the registered operator for the validator at {@code validator_address}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::EINVALID_CONSENSUS_KEY}      | {@code consensus_pubkey} is not a valid ed25519 public key.                                                 |
     * | {@code Errors::INVALID_STATE}    | {@code DiemConfig::EINVALID_BLOCK_TIME}             | An invalid time value was encountered in reconfiguration. Unlikely to occur.                          |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     *
     * @param validator_account {@code AccountAddress} value
     * @param consensus_pubkey {@code Bytes} value
     * @param validator_network_addresses {@code Bytes} value
     * @param fullnode_network_addresses {@code Bytes} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_set_validator_config_and_reconfigure_script(AccountAddress validator_account, Bytes consensus_pubkey, Bytes validator_network_addresses, Bytes fullnode_network_addresses) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(SET_VALIDATOR_CONFIG_AND_RECONFIGURE_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.Address(validator_account), new TransactionArgument.U8Vector(consensus_pubkey), new TransactionArgument.U8Vector(validator_network_addresses), new TransactionArgument.U8Vector(fullnode_network_addresses));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Updates a validator's configuration, and triggers a reconfiguration of the system to update the
     * validator set with this new validator configuration.  Can only be successfully sent by a
     * Validator Operator account that is already registered with a validator.
     *
     * <p><b>Technical Description</b></p>
     * This updates the fields with corresponding names held in the {@code ValidatorConfig::ValidatorConfig}
     * config resource held under {@code validator_account}. It then emits a {@code DiemConfig::NewEpochEvent} to
     * trigger a reconfiguration of the system.  This reconfiguration will update the validator set
     * on-chain with the updated {@code ValidatorConfig::ValidatorConfig}.
     *
     * <p><b>Parameters</b></p>
     * | Name                          | Type         | Description                                                                                                        |
     * | ------                        | ------       | -------------                                                                                                      |
     * | {@code validator_operator_account}  | {@code signer}     | Signer of the sending account. Must be the registered validator operator for the validator at {@code validator_address}. |
     * | {@code validator_account}           | {@code address}    | The address of the validator's {@code ValidatorConfig::ValidatorConfig} resource being updated.                          |
     * | {@code consensus_pubkey}            | {@code vector<u8>} | New Ed25519 public key to be used in the updated {@code ValidatorConfig::ValidatorConfig}.                               |
     * | {@code validator_network_addresses} | {@code vector<u8>} | New set of {@code validator_network_addresses} to be used in the updated {@code ValidatorConfig::ValidatorConfig}.             |
     * | {@code fullnode_network_addresses}  | {@code vector<u8>} | New set of {@code fullnode_network_addresses} to be used in the updated {@code ValidatorConfig::ValidatorConfig}.              |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                   | Description                                                                                           |
     * | ----------------           | --------------                                 | -------------                                                                                         |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorConfig::EVALIDATOR_CONFIG}           | {@code validator_address} does not have a {@code ValidatorConfig::ValidatorConfig} resource published under it.   |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::EVALIDATOR_OPERATOR}                   | {@code validator_operator_account} does not have a Validator Operator role.                                 |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::EINVALID_TRANSACTION_SENDER} | {@code validator_operator_account} is not the registered operator for the validator at {@code validator_address}. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::EINVALID_CONSENSUS_KEY}      | {@code consensus_pubkey} is not a valid ed25519 public key.                                                 |
     * | {@code Errors::INVALID_STATE}    | {@code DiemConfig::EINVALID_BLOCK_TIME}             | An invalid time value was encountered in reconfiguration. Unlikely to occur.                          |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_validator_account}</li></ul>
     * <ul><li>{@code AccountCreationScripts::create_validator_operator_account}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::register_validator_config}</li></ul>
     *
     * @param validator_account {@code AccountAddress} value
     * @param consensus_pubkey {@code Bytes} value
     * @param validator_network_addresses {@code Bytes} value
     * @param fullnode_network_addresses {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_set_validator_config_and_reconfigure_script_function(AccountAddress validator_account, Bytes consensus_pubkey, Bytes validator_network_addresses, Bytes fullnode_network_addresses) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_address_argument(validator_account), Helpers.encode_u8vector_argument(consensus_pubkey), Helpers.encode_u8vector_argument(validator_network_addresses), Helpers.encode_u8vector_argument(fullnode_network_addresses));
        script_function_builder.function = new Identifier("set_validator_config_and_reconfigure");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("ValidatorAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Sets the validator operator for a validator in the validator's configuration resource "locally"
     * and does not reconfigure the system. Changes from this transaction will not picked up by the
     * system until a reconfiguration of the system is triggered. May only be sent by an account with
     * Validator role.
     *
     * <p><b>Technical Description</b></p>
     * Sets the account at {@code operator_account} address and with the specified {@code human_name} as an
     * operator for the sending validator account. The account at {@code operator_account} address must have
     * a Validator Operator role and have a {@code ValidatorOperatorConfig::ValidatorOperatorConfig}
     * resource published under it. The sending {@code account} must be a Validator and have a
     * {@code ValidatorConfig::ValidatorConfig} resource published under it. This script does not emit a
     * {@code DiemConfig::NewEpochEvent} and no reconfiguration of the system is initiated by this script.
     *
     * <p><b>Parameters</b></p>
     * | Name               | Type         | Description                                                                                  |
     * | ------             | ------       | -------------                                                                                |
     * | {@code account}          | {@code &signer}    | The signer reference of the sending account of the transaction.                              |
     * | {@code operator_name}    | {@code vector<u8>} | Validator operator's human name.                                                             |
     * | {@code operator_account} | {@code address}    | Address of the validator operator account to be added as the {@code account} validator's operator. |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                          | Description                                                                                                                                                  |
     * | ----------------           | --------------                                        | -------------                                                                                                                                                |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorOperatorConfig::EVALIDATOR_OPERATOR_CONFIG} | The {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource is not published under {@code operator_account}.                                                   |
     * | 0                          | 0                                                     | The {@code human_name} field of the {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource under {@code operator_account} does not match the provided {@code human_name}. |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::EVALIDATOR}                                   | {@code account} does not have a Validator account role.                                                                                                            |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::ENOT_A_VALIDATOR_OPERATOR}          | The account at {@code operator_account} does not have a {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource.                                               |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorConfig::EVALIDATOR_CONFIG}                  | A {@code ValidatorConfig::ValidatorConfig} is not published under {@code account}.                                                                                       |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param operator_name {@code Bytes} value
     * @param operator_account {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_set_validator_operator_script(Bytes operator_name, AccountAddress operator_account) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(SET_VALIDATOR_OPERATOR_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U8Vector(operator_name), new TransactionArgument.Address(operator_account));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Sets the validator operator for a validator in the validator's configuration resource "locally"
     * and does not reconfigure the system. Changes from this transaction will not picked up by the
     * system until a reconfiguration of the system is triggered. May only be sent by an account with
     * Validator role.
     *
     * <p><b>Technical Description</b></p>
     * Sets the account at {@code operator_account} address and with the specified {@code human_name} as an
     * operator for the sending validator account. The account at {@code operator_account} address must have
     * a Validator Operator role and have a {@code ValidatorOperatorConfig::ValidatorOperatorConfig}
     * resource published under it. The sending {@code account} must be a Validator and have a
     * {@code ValidatorConfig::ValidatorConfig} resource published under it. This script does not emit a
     * {@code DiemConfig::NewEpochEvent} and no reconfiguration of the system is initiated by this script.
     *
     * <p><b>Parameters</b></p>
     * | Name               | Type         | Description                                                                                  |
     * | ------             | ------       | -------------                                                                                |
     * | {@code account}          | {@code signer}     | The signer of the sending account of the transaction.                                        |
     * | {@code operator_name}    | {@code vector<u8>} | Validator operator's human name.                                                             |
     * | {@code operator_account} | {@code address}    | Address of the validator operator account to be added as the {@code account} validator's operator. |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                          | Description                                                                                                                                                  |
     * | ----------------           | --------------                                        | -------------                                                                                                                                                |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorOperatorConfig::EVALIDATOR_OPERATOR_CONFIG} | The {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource is not published under {@code operator_account}.                                                   |
     * | 0                          | 0                                                     | The {@code human_name} field of the {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource under {@code operator_account} does not match the provided {@code human_name}. |
     * | {@code Errors::REQUIRES_ROLE}    | {@code Roles::EVALIDATOR}                                   | {@code account} does not have a Validator account role.                                                                                                            |
     * | {@code Errors::INVALID_ARGUMENT} | {@code ValidatorConfig::ENOT_A_VALIDATOR_OPERATOR}          | The account at {@code operator_account} does not have a {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource.                                               |
     * | {@code Errors::NOT_PUBLISHED}    | {@code ValidatorConfig::EVALIDATOR_CONFIG}                  | A {@code ValidatorConfig::ValidatorConfig} is not published under {@code account}.                                                                                       |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_validator_account}</li></ul>
     * <ul><li>{@code AccountCreationScripts::create_validator_operator_account}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::register_validator_config}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator_with_nonce_admin}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param operator_name {@code Bytes} value
     * @param operator_account {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_set_validator_operator_script_function(Bytes operator_name, AccountAddress operator_account) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u8vector_argument(operator_name), Helpers.encode_address_argument(operator_account));
        script_function_builder.function = new Identifier("set_validator_operator");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("ValidatorAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Sets the validator operator for a validator in the validator's configuration resource "locally"
     * and does not reconfigure the system. Changes from this transaction will not picked up by the
     * system until a reconfiguration of the system is triggered. May only be sent by the Diem Root
     * account as a write set transaction.
     *
     * <p><b>Technical Description</b></p>
     * Sets the account at {@code operator_account} address and with the specified {@code human_name} as an
     * operator for the validator {@code account}. The account at {@code operator_account} address must have a
     * Validator Operator role and have a {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource
     * published under it. The account represented by the {@code account} signer must be a Validator and
     * have a {@code ValidatorConfig::ValidatorConfig} resource published under it. No reconfiguration of
     * the system is initiated by this script.
     *
     * <p><b>Parameters</b></p>
     * | Name               | Type         | Description                                                                                                  |
     * | ------             | ------       | -------------                                                                                                |
     * | {@code dr_account}       | {@code &signer}    | The signer reference of the sending account of the write set transaction. May only be the Diem Root signer. |
     * | {@code account}          | {@code &signer}    | Signer reference of account specified in the {@code execute_as} field of the write set transaction.                |
     * | {@code sliding_nonce}    | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction for Diem Root.                    |
     * | {@code operator_name}    | {@code vector<u8>} | Validator operator's human name.                                                                             |
     * | {@code operator_account} | {@code address}    | Address of the validator operator account to be added as the {@code account} validator's operator.                 |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_validator_account}</li></ul>
     * <ul><li>{@code Script::create_validator_operator_account}</li></ul>
     * <ul><li>{@code Script::register_validator_config}</li></ul>
     * <ul><li>{@code Script::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code Script::set_validator_operator}</li></ul>
     * <ul><li>{@code Script::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param operator_name {@code Bytes} value
     * @param operator_account {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_set_validator_operator_with_nonce_admin_script(@Unsigned Long sliding_nonce, Bytes operator_name, AccountAddress operator_account) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(SET_VALIDATOR_OPERATOR_WITH_NONCE_ADMIN_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U8Vector(operator_name), new TransactionArgument.Address(operator_account));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Sets the validator operator for a validator in the validator's configuration resource "locally"
     * and does not reconfigure the system. Changes from this transaction will not picked up by the
     * system until a reconfiguration of the system is triggered. May only be sent by the Diem Root
     * account as a write set transaction.
     *
     * <p><b>Technical Description</b></p>
     * Sets the account at {@code operator_account} address and with the specified {@code human_name} as an
     * operator for the validator {@code account}. The account at {@code operator_account} address must have a
     * Validator Operator role and have a {@code ValidatorOperatorConfig::ValidatorOperatorConfig} resource
     * published under it. The account represented by the {@code account} signer must be a Validator and
     * have a {@code ValidatorConfig::ValidatorConfig} resource published under it. No reconfiguration of
     * the system is initiated by this script.
     *
     * <p><b>Parameters</b></p>
     * | Name               | Type         | Description                                                                                   |
     * | ------             | ------       | -------------                                                                                 |
     * | {@code dr_account}       | {@code signer}     | Signer of the sending account of the write set transaction. May only be the Diem Root signer. |
     * | {@code account}          | {@code signer}     | Signer of account specified in the {@code execute_as} field of the write set transaction.           |
     * | {@code sliding_nonce}    | {@code u64}        | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction for Diem Root.      |
     * | {@code operator_name}    | {@code vector<u8>} | Validator operator's human name.                                                              |
     * | {@code operator_account} | {@code address}    | Address of the validator operator account to be added as the {@code account} validator's operator.  |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_validator_account}</li></ul>
     * <ul><li>{@code AccountCreationScripts::create_validator_operator_account}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::register_validator_config}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::remove_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::add_validator_and_reconfigure}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_operator}</li></ul>
     * <ul><li>{@code ValidatorAdministrationScripts::set_validator_config_and_reconfigure}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param operator_name {@code Bytes} value
     * @param operator_account {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_set_validator_operator_with_nonce_admin_script_function(@Unsigned Long sliding_nonce, Bytes operator_name, AccountAddress operator_account) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_u8vector_argument(operator_name), Helpers.encode_address_argument(operator_account));
        script_function_builder.function = new Identifier("set_validator_operator_with_nonce_admin");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("ValidatorAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Mints a specified number of coins in a currency to a Designated Dealer. The sending account
     * must be the Treasury Compliance account, and coins can only be minted to a Designated Dealer
     * account.
     *
     * <p><b>Technical Description</b></p>
     * Mints {@code mint_amount} of coins in the {@code CoinType} currency to Designated Dealer account at
     * {@code designated_dealer_address}. The {@code tier_index} parameter specifies which tier should be used to
     * check verify the off-chain approval policy, and is based in part on the on-chain tier values
     * for the specific Designated Dealer, and the number of {@code CoinType} coins that have been minted to
     * the dealer over the past 24 hours. Every Designated Dealer has 4 tiers for each currency that
     * they support. The sending {@code tc_account} must be the Treasury Compliance account, and the
     * receiver an authorized Designated Dealer account.
     *
     * <p><b>Events</b></p>
     * Successful execution of the transaction will emit two events:
     * <ul><li>A {@code Diem::MintEvent} with the amount and currency code minted is emitted on the</li></ul>
     * {@code mint_event_handle} in the stored {@code Diem::CurrencyInfo<CoinType>} resource stored under
     * {@code 0xA550C18}; and
     * <ul><li>A {@code DesignatedDealer::ReceivedMintEvent} with the amount, currency code, and Designated</li></ul>
     * Dealer's address is emitted on the {@code mint_event_handle} in the stored {@code DesignatedDealer::Dealer}
     * resource published under the {@code designated_dealer_address}.
     *
     * <p><b>Parameters</b></p>
     * | Name                        | Type      | Description                                                                                                |
     * | ------                      | ------    | -------------                                                                                              |
     * | {@code CoinType}                  | Type      | The Move type for the {@code CoinType} being minted. {@code CoinType} must be an already-registered currency on-chain. |
     * | {@code tc_account}                | {@code &signer} | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account.  |
     * | {@code sliding_nonce}             | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                 |
     * | {@code designated_dealer_address} | {@code address} | The address of the Designated Dealer account being minted to.                                              |
     * | {@code mint_amount}               | {@code u64}     | The number of coins to be minted.                                                                          |
     * | {@code tier_index}                | {@code u64}     | The mint tier index to use for the Designated Dealer account.                                              |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::create_designated_dealer}</li></ul>
     * <ul><li>{@code Script::peer_to_peer_with_metadata}</li></ul>
     * <ul><li>{@code Script::rotate_dual_attestation_info}</li></ul>
     *
     * @param coin_type {@code TypeTag} value
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param designated_dealer_address {@code AccountAddress} value
     * @param mint_amount {@code @Unsigned Long} value
     * @param tier_index {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_tiered_mint_script(TypeTag coin_type, @Unsigned Long sliding_nonce, AccountAddress designated_dealer_address, @Unsigned Long mint_amount, @Unsigned Long tier_index) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(TIERED_MINT_CODE);
        builder.ty_args = java.util.Arrays.asList(coin_type);
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(designated_dealer_address), new TransactionArgument.U64(mint_amount), new TransactionArgument.U64(tier_index));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Mints a specified number of coins in a currency to a Designated Dealer. The sending account
     * must be the Treasury Compliance account, and coins can only be minted to a Designated Dealer
     * account.
     *
     * <p><b>Technical Description</b></p>
     * Mints {@code mint_amount} of coins in the {@code CoinType} currency to Designated Dealer account at
     * {@code designated_dealer_address}. The {@code tier_index} parameter specifies which tier should be used to
     * check verify the off-chain approval policy, and is based in part on the on-chain tier values
     * for the specific Designated Dealer, and the number of {@code CoinType} coins that have been minted to
     * the dealer over the past 24 hours. Every Designated Dealer has 4 tiers for each currency that
     * they support. The sending {@code tc_account} must be the Treasury Compliance account, and the
     * receiver an authorized Designated Dealer account.
     *
     * <p><b>Events</b></p>
     * Successful execution of the transaction will emit two events:
     * <ul><li>A {@code Diem::MintEvent} with the amount and currency code minted is emitted on the</li></ul>
     * {@code mint_event_handle} in the stored {@code Diem::CurrencyInfo<CoinType>} resource stored under
     * {@code 0xA550C18}; and
     * <ul><li>A {@code DesignatedDealer::ReceivedMintEvent} with the amount, currency code, and Designated</li></ul>
     * Dealer's address is emitted on the {@code mint_event_handle} in the stored {@code DesignatedDealer::Dealer}
     * resource published under the {@code designated_dealer_address}.
     *
     * <p><b>Parameters</b></p>
     * | Name                        | Type      | Description                                                                                                |
     * | ------                      | ------    | -------------                                                                                              |
     * | {@code CoinType}                  | Type      | The Move type for the {@code CoinType} being minted. {@code CoinType} must be an already-registered currency on-chain. |
     * | {@code tc_account}                | {@code signer}  | The signer of the sending account of this transaction. Must be the Treasury Compliance account.            |
     * | {@code sliding_nonce}             | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                 |
     * | {@code designated_dealer_address} | {@code address} | The address of the Designated Dealer account being minted to.                                              |
     * | {@code mint_amount}               | {@code u64}     | The number of coins to be minted.                                                                          |
     * | {@code tier_index}                | {@code u64}     | [Deprecated] The mint tier index to use for the Designated Dealer account. Will be ignored                 |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * | {@code Errors::REQUIRES_CAPABILITY} | {@code Diem::EMINT_CAPABILITY}                    | {@code tc_account} does not have a {@code Diem::MintCapability<CoinType>} resource published under it.                                  |
     * | {@code Errors::INVALID_STATE}       | {@code Diem::EMINTING_NOT_ALLOWED}                | Minting is not currently allowed for {@code CoinType} coins.                                                                       |
     * | {@code Errors::LIMIT_EXCEEDED}      | {@code DiemAccount::EDEPOSIT_EXCEEDS_LIMITS}      | The depositing of the funds would exceed the {@code account}'s account limits.                                                     |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code AccountCreationScripts::create_designated_dealer}</li></ul>
     * <ul><li>{@code PaymentScripts::peer_to_peer_with_metadata}</li></ul>
     * <ul><li>{@code AccountAdministrationScripts::rotate_dual_attestation_info}</li></ul>
     *
     * @param coin_type {@code TypeTag} value
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param designated_dealer_address {@code AccountAddress} value
     * @param mint_amount {@code @Unsigned Long} value
     * @param tier_index {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_tiered_mint_script_function(TypeTag coin_type, @Unsigned Long sliding_nonce, AccountAddress designated_dealer_address, @Unsigned Long mint_amount, @Unsigned Long tier_index) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(coin_type);
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_address_argument(designated_dealer_address), Helpers.encode_u64_argument(mint_amount), Helpers.encode_u64_argument(tier_index));
        script_function_builder.function = new Identifier("tiered_mint");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("TreasuryComplianceScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Unfreezes the account at {@code address}. The sending account of this transaction must be the
     * Treasury Compliance account. After the successful execution of this transaction transactions
     * may be sent from the previously frozen account, and coins may be sent and received.
     *
     * <p><b>Technical Description</b></p>
     * Sets the {@code AccountFreezing::FreezingBit} to {@code false} and emits a
     * {@code AccountFreezing::UnFreezeAccountEvent}. The transaction sender must be the Treasury Compliance
     * account. Note that this is a per-account property so unfreezing a Parent VASP will not effect
     * the status any of its child accounts and vice versa.
     *
     * <p><b>Events</b></p>
     * Successful execution of this script will emit a {@code AccountFreezing::UnFreezeAccountEvent} with
     * the {@code unfrozen_address} set the {@code to_unfreeze_account}'s address.
     *
     * <p><b>Parameters</b></p>
     * | Name                  | Type      | Description                                                                                               |
     * | ------                | ------    | -------------                                                                                             |
     * | {@code tc_account}          | {@code &signer} | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account. |
     * | {@code sliding_nonce}       | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                |
     * | {@code to_unfreeze_account} | {@code address} | The account address to be frozen.                                                                         |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                            | Description                                                                                |
     * | ----------------           | --------------                          | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code account}.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE}   | The sending account is not the Treasury Compliance account.                                |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::freeze_account}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param to_unfreeze_account {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_unfreeze_account_script(@Unsigned Long sliding_nonce, AccountAddress to_unfreeze_account) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(UNFREEZE_ACCOUNT_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.Address(to_unfreeze_account));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Unfreezes the account at {@code address}. The sending account of this transaction must be the
     * Treasury Compliance account. After the successful execution of this transaction transactions
     * may be sent from the previously frozen account, and coins may be sent and received.
     *
     * <p><b>Technical Description</b></p>
     * Sets the {@code AccountFreezing::FreezingBit} to {@code false} and emits a
     * {@code AccountFreezing::UnFreezeAccountEvent}. The transaction sender must be the Treasury Compliance
     * account. Note that this is a per-account property so unfreezing a Parent VASP will not effect
     * the status any of its child accounts and vice versa.
     *
     * <p><b>Events</b></p>
     * Successful execution of this script will emit a {@code AccountFreezing::UnFreezeAccountEvent} with
     * the {@code unfrozen_address} set the {@code to_unfreeze_account}'s address.
     *
     * <p><b>Parameters</b></p>
     * | Name                  | Type      | Description                                                                                     |
     * | ------                | ------    | -------------                                                                                   |
     * | {@code tc_account}          | {@code signer}  | The signer of the sending account of this transaction. Must be the Treasury Compliance account. |
     * | {@code sliding_nonce}       | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                      |
     * | {@code to_unfreeze_account} | {@code address} | The account address to be frozen.                                                               |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                            | Description                                                                                |
     * | ----------------           | --------------                          | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code account}.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE}   | The sending account is not the Treasury Compliance account.                                |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code TreasuryComplianceScripts::freeze_account}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param to_unfreeze_account {@code AccountAddress} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_unfreeze_account_script_function(@Unsigned Long sliding_nonce, AccountAddress to_unfreeze_account) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_address_argument(to_unfreeze_account));
        script_function_builder.function = new Identifier("unfreeze_account");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("TreasuryComplianceScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Updates the Diem consensus config that is stored on-chain and is used by the Consensus.  This
     * transaction can only be sent from the Diem Root account.
     *
     * <p><b>Technical Description</b></p>
     * Updates the {@code DiemConsensusConfig} on-chain config and emits a {@code DiemConfig::NewEpochEvent} to trigger
     * a reconfiguration of the system.
     *
     * <p><b>Parameters</b></p>
     * | Name            | Type          | Description                                                                |
     * | ------          | ------        | -------------                                                              |
     * | {@code account}       | {@code signer}      | Signer of the sending account. Must be the Diem Root account.              |
     * | {@code sliding_nonce} | {@code u64}         | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction. |
     * | {@code config}        | {@code vector<u8>}  | The serialized bytes of consensus config.                                  |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                  | Description                                                                                |
     * | ----------------           | --------------                                | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                | A {@code SlidingNonce} resource is not published under {@code account}.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}       | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::EDIEM_ROOT}                   | {@code account} is not the Diem Root account.                                                    |
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param config {@code Bytes} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_update_diem_consensus_config_script_function(@Unsigned Long sliding_nonce, Bytes config) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_u8vector_argument(config));
        script_function_builder.function = new Identifier("update_diem_consensus_config");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("SystemAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Updates the Diem major version that is stored on-chain and is used by the VM.  This
     * transaction can only be sent from the Diem Root account.
     *
     * <p><b>Technical Description</b></p>
     * Updates the {@code DiemVersion} on-chain config and emits a {@code DiemConfig::NewEpochEvent} to trigger
     * a reconfiguration of the system. The {@code major} version that is passed in must be strictly greater
     * than the current major version held on-chain. The VM reads this information and can use it to
     * preserve backwards compatibility with previous major versions of the VM.
     *
     * <p><b>Parameters</b></p>
     * | Name            | Type      | Description                                                                |
     * | ------          | ------    | -------------                                                              |
     * | {@code account}       | {@code &signer} | Signer reference of the sending account. Must be the Diem Root account.   |
     * | {@code sliding_nonce} | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction. |
     * | {@code major}         | {@code u64}     | The {@code major} version of the VM to be used from this transaction on.         |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                  | Description                                                                                |
     * | ----------------           | --------------                                | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                | A {@code SlidingNonce} resource is not published under {@code account}.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}       | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::EDIEM_ROOT}                  | {@code account} is not the Diem Root account.                                                   |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemVersion::EINVALID_MAJOR_VERSION_NUMBER} | {@code major} is less-than or equal to the current major version stored on-chain.                |
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param major {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_update_diem_version_script(@Unsigned Long sliding_nonce, @Unsigned Long major) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(UPDATE_DIEM_VERSION_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U64(major));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Updates the Diem major version that is stored on-chain and is used by the VM.  This
     * transaction can only be sent from the Diem Root account.
     *
     * <p><b>Technical Description</b></p>
     * Updates the {@code DiemVersion} on-chain config and emits a {@code DiemConfig::NewEpochEvent} to trigger
     * a reconfiguration of the system. The {@code major} version that is passed in must be strictly greater
     * than the current major version held on-chain. The VM reads this information and can use it to
     * preserve backwards compatibility with previous major versions of the VM.
     *
     * <p><b>Parameters</b></p>
     * | Name            | Type     | Description                                                                |
     * | ------          | ------   | -------------                                                              |
     * | {@code account}       | {@code signer} | Signer of the sending account. Must be the Diem Root account.              |
     * | {@code sliding_nonce} | {@code u64}    | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction. |
     * | {@code major}         | {@code u64}    | The {@code major} version of the VM to be used from this transaction on.         |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                                  | Description                                                                                |
     * | ----------------           | --------------                                | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}                | A {@code SlidingNonce} resource is not published under {@code account}.                                |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}                | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}                | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED}       | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::EDIEM_ROOT}                   | {@code account} is not the Diem Root account.                                                    |
     * | {@code Errors::INVALID_ARGUMENT} | {@code DiemVersion::EINVALID_MAJOR_VERSION_NUMBER}  | {@code major} is less-than or equal to the current major version stored on-chain.                |
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param major {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_update_diem_version_script_function(@Unsigned Long sliding_nonce, @Unsigned Long major) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_u64_argument(major));
        script_function_builder.function = new Identifier("update_diem_version");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("SystemAdministrationScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Update the dual attestation limit on-chain. Defined in terms of micro-XDX.  The transaction can
     * only be sent by the Treasury Compliance account.  After this transaction all inter-VASP
     * payments over this limit must be checked for dual attestation.
     *
     * <p><b>Technical Description</b></p>
     * Updates the {@code micro_xdx_limit} field of the {@code DualAttestation::Limit} resource published under
     * {@code 0xA550C18}. The amount is set in micro-XDX.
     *
     * <p><b>Parameters</b></p>
     * | Name                  | Type      | Description                                                                                               |
     * | ------                | ------    | -------------                                                                                             |
     * | {@code tc_account}          | {@code &signer} | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account. |
     * | {@code sliding_nonce}       | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                                |
     * | {@code new_micro_xdx_limit} | {@code u64}     | The new dual attestation limit to be used on-chain.                                                       |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                            | Description                                                                                |
     * | ----------------           | --------------                          | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code tc_account}.                             |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE}   | {@code tc_account} is not the Treasury Compliance account.                                       |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::update_exchange_rate}</li></ul>
     * <ul><li>{@code Script::update_minting_ability}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_micro_xdx_limit {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_update_dual_attestation_limit_script(@Unsigned Long sliding_nonce, @Unsigned Long new_micro_xdx_limit) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(UPDATE_DUAL_ATTESTATION_LIMIT_CODE);
        builder.ty_args = java.util.Arrays.asList();
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U64(new_micro_xdx_limit));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Update the dual attestation limit on-chain. Defined in terms of micro-XDX.  The transaction can
     * only be sent by the Treasury Compliance account.  After this transaction all inter-VASP
     * payments over this limit must be checked for dual attestation.
     *
     * <p><b>Technical Description</b></p>
     * Updates the {@code micro_xdx_limit} field of the {@code DualAttestation::Limit} resource published under
     * {@code 0xA550C18}. The amount is set in micro-XDX.
     *
     * <p><b>Parameters</b></p>
     * | Name                  | Type     | Description                                                                                     |
     * | ------                | ------   | -------------                                                                                   |
     * | {@code tc_account}          | {@code signer} | The signer of the sending account of this transaction. Must be the Treasury Compliance account. |
     * | {@code sliding_nonce}       | {@code u64}    | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for this transaction.                      |
     * | {@code new_micro_xdx_limit} | {@code u64}    | The new dual attestation limit to be used on-chain.                                             |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                            | Description                                                                                |
     * | ----------------           | --------------                          | -------------                                                                              |
     * | {@code Errors::NOT_PUBLISHED}    | {@code SlidingNonce::ESLIDING_NONCE}          | A {@code SlidingNonce} resource is not published under {@code tc_account}.                             |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_OLD}          | The {@code sliding_nonce} is too old and it's impossible to determine if it's duplicated or not. |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_TOO_NEW}          | The {@code sliding_nonce} is too far in the future.                                              |
     * | {@code Errors::INVALID_ARGUMENT} | {@code SlidingNonce::ENONCE_ALREADY_RECORDED} | The {@code sliding_nonce} has been previously recorded.                                          |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE}   | {@code tc_account} is not the Treasury Compliance account.                                       |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code TreasuryComplianceScripts::update_exchange_rate}</li></ul>
     * <ul><li>{@code TreasuryComplianceScripts::update_minting_ability}</li></ul>
     *
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_micro_xdx_limit {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_update_dual_attestation_limit_script_function(@Unsigned Long sliding_nonce, @Unsigned Long new_micro_xdx_limit) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList();
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_u64_argument(new_micro_xdx_limit));
        script_function_builder.function = new Identifier("update_dual_attestation_limit");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("TreasuryComplianceScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Update the rough on-chain exchange rate between a specified currency and XDX (as a conversion
     * to micro-XDX). The transaction can only be sent by the Treasury Compliance account. After this
     * transaction the updated exchange rate will be used for normalization of gas prices, and for
     * dual attestation checking.
     *
     * <p><b>Technical Description</b></p>
     * Updates the on-chain exchange rate from the given {@code Currency} to micro-XDX.  The exchange rate
     * is given by {@code new_exchange_rate_numerator/new_exchange_rate_denominator}.
     *
     * <p><b>Parameters</b></p>
     * | Name                            | Type      | Description                                                                                                                        |
     * | ------                          | ------    | -------------                                                                                                                      |
     * | {@code Currency}                      | Type      | The Move type for the {@code Currency} whose exchange rate is being updated. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code tc_account}                    | {@code &signer} | The signer reference of the sending account of this transaction. Must be the Treasury Compliance account.                          |
     * | {@code sliding_nonce}                 | {@code u64}     | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for the transaction.                                                          |
     * | {@code new_exchange_rate_numerator}   | {@code u64}     | The numerator for the new to micro-XDX exchange rate for {@code Currency}.                                                               |
     * | {@code new_exchange_rate_denominator} | {@code u64}     | The denominator for the new to micro-XDX exchange rate for {@code Currency}.                                                             |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::update_dual_attestation_limit}</li></ul>
     * <ul><li>{@code Script::update_minting_ability}</li></ul>
     *
     * @param currency {@code TypeTag} value
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_exchange_rate_numerator {@code @Unsigned Long} value
     * @param new_exchange_rate_denominator {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_update_exchange_rate_script(TypeTag currency, @Unsigned Long sliding_nonce, @Unsigned Long new_exchange_rate_numerator, @Unsigned Long new_exchange_rate_denominator) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(UPDATE_EXCHANGE_RATE_CODE);
        builder.ty_args = java.util.Arrays.asList(currency);
        builder.args = java.util.Arrays.asList(new TransactionArgument.U64(sliding_nonce), new TransactionArgument.U64(new_exchange_rate_numerator), new TransactionArgument.U64(new_exchange_rate_denominator));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Update the rough on-chain exchange rate between a specified currency and XDX (as a conversion
     * to micro-XDX). The transaction can only be sent by the Treasury Compliance account. After this
     * transaction the updated exchange rate will be used for normalization of gas prices, and for
     * dual attestation checking.
     *
     * <p><b>Technical Description</b></p>
     * Updates the on-chain exchange rate from the given {@code Currency} to micro-XDX.  The exchange rate
     * is given by {@code new_exchange_rate_numerator/new_exchange_rate_denominator}.
     *
     * <p><b>Parameters</b></p>
     * | Name                            | Type     | Description                                                                                                                        |
     * | ------                          | ------   | -------------                                                                                                                      |
     * | {@code Currency}                      | Type     | The Move type for the {@code Currency} whose exchange rate is being updated. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code tc_account}                    | {@code signer} | The signer of the sending account of this transaction. Must be the Treasury Compliance account.                                    |
     * | {@code sliding_nonce}                 | {@code u64}    | The {@code sliding_nonce} (see: {@code SlidingNonce}) to be used for the transaction.                                                          |
     * | {@code new_exchange_rate_numerator}   | {@code u64}    | The numerator for the new to micro-XDX exchange rate for {@code Currency}.                                                               |
     * | {@code new_exchange_rate_denominator} | {@code u64}    | The denominator for the new to micro-XDX exchange rate for {@code Currency}.                                                             |
     *
     * <p><b>Common Abort Conditions</b></p>
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
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code TreasuryComplianceScripts::update_dual_attestation_limit}</li></ul>
     * <ul><li>{@code TreasuryComplianceScripts::update_minting_ability}</li></ul>
     *
     * @param currency {@code TypeTag} value
     * @param sliding_nonce {@code @Unsigned Long} value
     * @param new_exchange_rate_numerator {@code @Unsigned Long} value
     * @param new_exchange_rate_denominator {@code @Unsigned Long} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_update_exchange_rate_script_function(TypeTag currency, @Unsigned Long sliding_nonce, @Unsigned Long new_exchange_rate_numerator, @Unsigned Long new_exchange_rate_denominator) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(currency);
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_u64_argument(sliding_nonce), Helpers.encode_u64_argument(new_exchange_rate_numerator), Helpers.encode_u64_argument(new_exchange_rate_denominator));
        script_function_builder.function = new Identifier("update_exchange_rate");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("TreasuryComplianceScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Script to allow or disallow minting of new coins in a specified currency.  This transaction can
     * only be sent by the Treasury Compliance account.  Turning minting off for a currency will have
     * no effect on coins already in circulation, and coins may still be removed from the system.
     *
     * <p><b>Technical Description</b></p>
     * This transaction sets the {@code can_mint} field of the {@code Diem::CurrencyInfo<Currency>} resource
     * published under {@code 0xA550C18} to the value of {@code allow_minting}. Minting of coins if allowed if
     * this field is set to {@code true} and minting of new coins in {@code Currency} is disallowed otherwise.
     * This transaction needs to be sent by the Treasury Compliance account.
     *
     * <p><b>Parameters</b></p>
     * | Name            | Type      | Description                                                                                                                          |
     * | ------          | ------    | -------------                                                                                                                        |
     * | {@code Currency}      | Type      | The Move type for the {@code Currency} whose minting ability is being updated. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code account}       | {@code &signer} | Signer reference of the sending account. Must be the Diem Root account.                                                             |
     * | {@code allow_minting} | {@code bool}    | Whether to allow minting of new coins in {@code Currency}.                                                                                 |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                          | Description                                          |
     * | ----------------           | --------------                        | -------------                                        |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE} | {@code tc_account} is not the Treasury Compliance account. |
     * | {@code Errors::NOT_PUBLISHED}    | {@code Diem::ECURRENCY_INFO}               | {@code Currency} is not a registered currency on-chain.    |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code Script::update_dual_attestation_limit}</li></ul>
     * <ul><li>{@code Script::update_exchange_rate}</li></ul>
     *
     * @param currency {@code TypeTag} value
     * @param allow_minting {@code Boolean} value
     * @return Encoded {@link com.diem.types.Script} value.
     */
    public static Script encode_update_minting_ability_script(TypeTag currency, Boolean allow_minting) {
        Script.Builder builder = new Script.Builder();
        builder.code = new Bytes(UPDATE_MINTING_ABILITY_CODE);
        builder.ty_args = java.util.Arrays.asList(currency);
        builder.args = java.util.Arrays.asList(new TransactionArgument.Bool(allow_minting));
        return builder.build();
    }

    /**
     * <p><b>Summary</b></p>
     * Script to allow or disallow minting of new coins in a specified currency.  This transaction can
     * only be sent by the Treasury Compliance account.  Turning minting off for a currency will have
     * no effect on coins already in circulation, and coins may still be removed from the system.
     *
     * <p><b>Technical Description</b></p>
     * This transaction sets the {@code can_mint} field of the {@code Diem::CurrencyInfo<Currency>} resource
     * published under {@code 0xA550C18} to the value of {@code allow_minting}. Minting of coins if allowed if
     * this field is set to {@code true} and minting of new coins in {@code Currency} is disallowed otherwise.
     * This transaction needs to be sent by the Treasury Compliance account.
     *
     * <p><b>Parameters</b></p>
     * | Name            | Type     | Description                                                                                                                          |
     * | ------          | ------   | -------------                                                                                                                        |
     * | {@code Currency}      | Type     | The Move type for the {@code Currency} whose minting ability is being updated. {@code Currency} must be an already-registered currency on-chain. |
     * | {@code account}       | {@code signer} | Signer of the sending account. Must be the Diem Root account.                                                                        |
     * | {@code allow_minting} | {@code bool}   | Whether to allow minting of new coins in {@code Currency}.                                                                                 |
     *
     * <p><b>Common Abort Conditions</b></p>
     * | Error Category             | Error Reason                          | Description                                          |
     * | ----------------           | --------------                        | -------------                                        |
     * | {@code Errors::REQUIRES_ADDRESS} | {@code CoreAddresses::ETREASURY_COMPLIANCE} | {@code tc_account} is not the Treasury Compliance account. |
     * | {@code Errors::NOT_PUBLISHED}    | {@code Diem::ECURRENCY_INFO}               | {@code Currency} is not a registered currency on-chain.    |
     *
     * <p><b>Related Scripts</b></p>
     * <ul><li>{@code TreasuryComplianceScripts::update_dual_attestation_limit}</li></ul>
     * <ul><li>{@code TreasuryComplianceScripts::update_exchange_rate}</li></ul>
     *
     * @param currency {@code TypeTag} value
     * @param allow_minting {@code Boolean} value
     * @return Encoded {@link com.diem.types.TransactionPayload} value.
     */
    public static TransactionPayload encode_update_minting_ability_script_function(TypeTag currency, Boolean allow_minting) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(currency);
        script_function_builder.args = java.util.Arrays.asList(Helpers.encode_bool_argument(allow_minting));
        script_function_builder.function = new Identifier("update_minting_ability");
        script_function_builder.module = new ModuleId(AccountAddress.valueOf(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), new Identifier("TreasuryComplianceScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
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

    private static ScriptCall decode_update_diem_version_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.UpdateDiemVersion.Builder builder = new ScriptCall.UpdateDiemVersion.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.major = Helpers.decode_u64_argument(script.args.get(1));
        return builder.build();
    }

    private static ScriptCall decode_update_dual_attestation_limit_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.UpdateDualAttestationLimit.Builder builder = new ScriptCall.UpdateDualAttestationLimit.Builder();
        builder.sliding_nonce = Helpers.decode_u64_argument(script.args.get(0));
        builder.new_micro_xdx_limit = Helpers.decode_u64_argument(script.args.get(1));
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

    private static ScriptCall decode_update_minting_ability_script(Script script) throws IllegalArgumentException, IndexOutOfBoundsException {
        ScriptCall.UpdateMintingAbility.Builder builder = new ScriptCall.UpdateMintingAbility.Builder();
        builder.currency = script.ty_args.get(0);
        builder.allow_minting = Helpers.decode_bool_argument(script.args.get(0));
        return builder.build();
    }

    private static ScriptFunctionCall decode_add_currency_to_account_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.AddCurrencyToAccount.Builder builder = new ScriptFunctionCall.AddCurrencyToAccount.Builder();
        builder.currency = script.ty_args.get(0);
        return builder.build();
    }

    private static ScriptFunctionCall decode_add_recovery_rotation_capability_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.AddRecoveryRotationCapability.Builder builder = new ScriptFunctionCall.AddRecoveryRotationCapability.Builder();
        builder.recovery_address = AccountAddress.bcsDeserialize(script.args.get(0).content());
        return builder.build();
    }

    private static ScriptFunctionCall decode_add_validator_and_reconfigure_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.AddValidatorAndReconfigure.Builder builder = new ScriptFunctionCall.AddValidatorAndReconfigure.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.validator_name = new BcsDeserializer(script.args.get(1).content()).deserialize_bytes();
        builder.validator_address = AccountAddress.bcsDeserialize(script.args.get(2).content());
        return builder.build();
    }

    private static ScriptFunctionCall decode_burn_txn_fees_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.BurnTxnFees.Builder builder = new ScriptFunctionCall.BurnTxnFees.Builder();
        builder.coin_type = script.ty_args.get(0);
        return builder.build();
    }

    private static ScriptFunctionCall decode_burn_with_amount_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.BurnWithAmount.Builder builder = new ScriptFunctionCall.BurnWithAmount.Builder();
        builder.token = script.ty_args.get(0);
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.preburn_address = AccountAddress.bcsDeserialize(script.args.get(1).content());
        builder.amount = new BcsDeserializer(script.args.get(2).content()).deserialize_u64();
        return builder.build();
    }

    private static ScriptFunctionCall decode_cancel_burn_with_amount_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.CancelBurnWithAmount.Builder builder = new ScriptFunctionCall.CancelBurnWithAmount.Builder();
        builder.token = script.ty_args.get(0);
        builder.preburn_address = AccountAddress.bcsDeserialize(script.args.get(0).content());
        builder.amount = new BcsDeserializer(script.args.get(1).content()).deserialize_u64();
        return builder.build();
    }

    private static ScriptFunctionCall decode_create_child_vasp_account_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.CreateChildVaspAccount.Builder builder = new ScriptFunctionCall.CreateChildVaspAccount.Builder();
        builder.coin_type = script.ty_args.get(0);
        builder.child_address = AccountAddress.bcsDeserialize(script.args.get(0).content());
        builder.auth_key_prefix = new BcsDeserializer(script.args.get(1).content()).deserialize_bytes();
        builder.add_all_currencies = new BcsDeserializer(script.args.get(2).content()).deserialize_bool();
        builder.child_initial_balance = new BcsDeserializer(script.args.get(3).content()).deserialize_u64();
        return builder.build();
    }

    private static ScriptFunctionCall decode_create_designated_dealer_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.CreateDesignatedDealer.Builder builder = new ScriptFunctionCall.CreateDesignatedDealer.Builder();
        builder.currency = script.ty_args.get(0);
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.addr = AccountAddress.bcsDeserialize(script.args.get(1).content());
        builder.auth_key_prefix = new BcsDeserializer(script.args.get(2).content()).deserialize_bytes();
        builder.human_name = new BcsDeserializer(script.args.get(3).content()).deserialize_bytes();
        builder.add_all_currencies = new BcsDeserializer(script.args.get(4).content()).deserialize_bool();
        return builder.build();
    }

    private static ScriptFunctionCall decode_create_parent_vasp_account_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.CreateParentVaspAccount.Builder builder = new ScriptFunctionCall.CreateParentVaspAccount.Builder();
        builder.coin_type = script.ty_args.get(0);
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.new_account_address = AccountAddress.bcsDeserialize(script.args.get(1).content());
        builder.auth_key_prefix = new BcsDeserializer(script.args.get(2).content()).deserialize_bytes();
        builder.human_name = new BcsDeserializer(script.args.get(3).content()).deserialize_bytes();
        builder.add_all_currencies = new BcsDeserializer(script.args.get(4).content()).deserialize_bool();
        return builder.build();
    }

    private static ScriptFunctionCall decode_create_recovery_address_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction _script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.CreateRecoveryAddress.Builder builder = new ScriptFunctionCall.CreateRecoveryAddress.Builder();
        return builder.build();
    }

    private static ScriptFunctionCall decode_create_validator_account_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.CreateValidatorAccount.Builder builder = new ScriptFunctionCall.CreateValidatorAccount.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.new_account_address = AccountAddress.bcsDeserialize(script.args.get(1).content());
        builder.auth_key_prefix = new BcsDeserializer(script.args.get(2).content()).deserialize_bytes();
        builder.human_name = new BcsDeserializer(script.args.get(3).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_create_validator_operator_account_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.CreateValidatorOperatorAccount.Builder builder = new ScriptFunctionCall.CreateValidatorOperatorAccount.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.new_account_address = AccountAddress.bcsDeserialize(script.args.get(1).content());
        builder.auth_key_prefix = new BcsDeserializer(script.args.get(2).content()).deserialize_bytes();
        builder.human_name = new BcsDeserializer(script.args.get(3).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_freeze_account_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.FreezeAccount.Builder builder = new ScriptFunctionCall.FreezeAccount.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.to_freeze_account = AccountAddress.bcsDeserialize(script.args.get(1).content());
        return builder.build();
    }

    private static ScriptFunctionCall decode_initialize_diem_consensus_config_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.InitializeDiemConsensusConfig.Builder builder = new ScriptFunctionCall.InitializeDiemConsensusConfig.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        return builder.build();
    }

    private static ScriptFunctionCall decode_peer_to_peer_with_metadata_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.PeerToPeerWithMetadata.Builder builder = new ScriptFunctionCall.PeerToPeerWithMetadata.Builder();
        builder.currency = script.ty_args.get(0);
        builder.payee = AccountAddress.bcsDeserialize(script.args.get(0).content());
        builder.amount = new BcsDeserializer(script.args.get(1).content()).deserialize_u64();
        builder.metadata = new BcsDeserializer(script.args.get(2).content()).deserialize_bytes();
        builder.metadata_signature = new BcsDeserializer(script.args.get(3).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_preburn_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.Preburn.Builder builder = new ScriptFunctionCall.Preburn.Builder();
        builder.token = script.ty_args.get(0);
        builder.amount = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        return builder.build();
    }

    private static ScriptFunctionCall decode_publish_shared_ed25519_public_key_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.PublishSharedEd25519PublicKey.Builder builder = new ScriptFunctionCall.PublishSharedEd25519PublicKey.Builder();
        builder.public_key = new BcsDeserializer(script.args.get(0).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_register_validator_config_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.RegisterValidatorConfig.Builder builder = new ScriptFunctionCall.RegisterValidatorConfig.Builder();
        builder.validator_account = AccountAddress.bcsDeserialize(script.args.get(0).content());
        builder.consensus_pubkey = new BcsDeserializer(script.args.get(1).content()).deserialize_bytes();
        builder.validator_network_addresses = new BcsDeserializer(script.args.get(2).content()).deserialize_bytes();
        builder.fullnode_network_addresses = new BcsDeserializer(script.args.get(3).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_remove_validator_and_reconfigure_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.RemoveValidatorAndReconfigure.Builder builder = new ScriptFunctionCall.RemoveValidatorAndReconfigure.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.validator_name = new BcsDeserializer(script.args.get(1).content()).deserialize_bytes();
        builder.validator_address = AccountAddress.bcsDeserialize(script.args.get(2).content());
        return builder.build();
    }

    private static ScriptFunctionCall decode_rotate_authentication_key_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.RotateAuthenticationKey.Builder builder = new ScriptFunctionCall.RotateAuthenticationKey.Builder();
        builder.new_key = new BcsDeserializer(script.args.get(0).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_rotate_authentication_key_with_nonce_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.RotateAuthenticationKeyWithNonce.Builder builder = new ScriptFunctionCall.RotateAuthenticationKeyWithNonce.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.new_key = new BcsDeserializer(script.args.get(1).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_rotate_authentication_key_with_nonce_admin_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.RotateAuthenticationKeyWithNonceAdmin.Builder builder = new ScriptFunctionCall.RotateAuthenticationKeyWithNonceAdmin.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.new_key = new BcsDeserializer(script.args.get(1).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_rotate_authentication_key_with_recovery_address_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.RotateAuthenticationKeyWithRecoveryAddress.Builder builder = new ScriptFunctionCall.RotateAuthenticationKeyWithRecoveryAddress.Builder();
        builder.recovery_address = AccountAddress.bcsDeserialize(script.args.get(0).content());
        builder.to_recover = AccountAddress.bcsDeserialize(script.args.get(1).content());
        builder.new_key = new BcsDeserializer(script.args.get(2).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_rotate_dual_attestation_info_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.RotateDualAttestationInfo.Builder builder = new ScriptFunctionCall.RotateDualAttestationInfo.Builder();
        builder.new_url = new BcsDeserializer(script.args.get(0).content()).deserialize_bytes();
        builder.new_key = new BcsDeserializer(script.args.get(1).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_rotate_shared_ed25519_public_key_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.RotateSharedEd25519PublicKey.Builder builder = new ScriptFunctionCall.RotateSharedEd25519PublicKey.Builder();
        builder.public_key = new BcsDeserializer(script.args.get(0).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_set_gas_constants_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.SetGasConstants.Builder builder = new ScriptFunctionCall.SetGasConstants.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.global_memory_per_byte_cost = new BcsDeserializer(script.args.get(1).content()).deserialize_u64();
        builder.global_memory_per_byte_write_cost = new BcsDeserializer(script.args.get(2).content()).deserialize_u64();
        builder.min_transaction_gas_units = new BcsDeserializer(script.args.get(3).content()).deserialize_u64();
        builder.large_transaction_cutoff = new BcsDeserializer(script.args.get(4).content()).deserialize_u64();
        builder.intrinsic_gas_per_byte = new BcsDeserializer(script.args.get(5).content()).deserialize_u64();
        builder.maximum_number_of_gas_units = new BcsDeserializer(script.args.get(6).content()).deserialize_u64();
        builder.min_price_per_gas_unit = new BcsDeserializer(script.args.get(7).content()).deserialize_u64();
        builder.max_price_per_gas_unit = new BcsDeserializer(script.args.get(8).content()).deserialize_u64();
        builder.max_transaction_size_in_bytes = new BcsDeserializer(script.args.get(9).content()).deserialize_u64();
        builder.gas_unit_scaling_factor = new BcsDeserializer(script.args.get(10).content()).deserialize_u64();
        builder.default_account_size = new BcsDeserializer(script.args.get(11).content()).deserialize_u64();
        return builder.build();
    }

    private static ScriptFunctionCall decode_set_validator_config_and_reconfigure_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.SetValidatorConfigAndReconfigure.Builder builder = new ScriptFunctionCall.SetValidatorConfigAndReconfigure.Builder();
        builder.validator_account = AccountAddress.bcsDeserialize(script.args.get(0).content());
        builder.consensus_pubkey = new BcsDeserializer(script.args.get(1).content()).deserialize_bytes();
        builder.validator_network_addresses = new BcsDeserializer(script.args.get(2).content()).deserialize_bytes();
        builder.fullnode_network_addresses = new BcsDeserializer(script.args.get(3).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_set_validator_operator_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.SetValidatorOperator.Builder builder = new ScriptFunctionCall.SetValidatorOperator.Builder();
        builder.operator_name = new BcsDeserializer(script.args.get(0).content()).deserialize_bytes();
        builder.operator_account = AccountAddress.bcsDeserialize(script.args.get(1).content());
        return builder.build();
    }

    private static ScriptFunctionCall decode_set_validator_operator_with_nonce_admin_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.SetValidatorOperatorWithNonceAdmin.Builder builder = new ScriptFunctionCall.SetValidatorOperatorWithNonceAdmin.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.operator_name = new BcsDeserializer(script.args.get(1).content()).deserialize_bytes();
        builder.operator_account = AccountAddress.bcsDeserialize(script.args.get(2).content());
        return builder.build();
    }

    private static ScriptFunctionCall decode_tiered_mint_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.TieredMint.Builder builder = new ScriptFunctionCall.TieredMint.Builder();
        builder.coin_type = script.ty_args.get(0);
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.designated_dealer_address = AccountAddress.bcsDeserialize(script.args.get(1).content());
        builder.mint_amount = new BcsDeserializer(script.args.get(2).content()).deserialize_u64();
        builder.tier_index = new BcsDeserializer(script.args.get(3).content()).deserialize_u64();
        return builder.build();
    }

    private static ScriptFunctionCall decode_unfreeze_account_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.UnfreezeAccount.Builder builder = new ScriptFunctionCall.UnfreezeAccount.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.to_unfreeze_account = AccountAddress.bcsDeserialize(script.args.get(1).content());
        return builder.build();
    }

    private static ScriptFunctionCall decode_update_diem_consensus_config_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.UpdateDiemConsensusConfig.Builder builder = new ScriptFunctionCall.UpdateDiemConsensusConfig.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.config = new BcsDeserializer(script.args.get(1).content()).deserialize_bytes();
        return builder.build();
    }

    private static ScriptFunctionCall decode_update_diem_version_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.UpdateDiemVersion.Builder builder = new ScriptFunctionCall.UpdateDiemVersion.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.major = new BcsDeserializer(script.args.get(1).content()).deserialize_u64();
        return builder.build();
    }

    private static ScriptFunctionCall decode_update_dual_attestation_limit_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.UpdateDualAttestationLimit.Builder builder = new ScriptFunctionCall.UpdateDualAttestationLimit.Builder();
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.new_micro_xdx_limit = new BcsDeserializer(script.args.get(1).content()).deserialize_u64();
        return builder.build();
    }

    private static ScriptFunctionCall decode_update_exchange_rate_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.UpdateExchangeRate.Builder builder = new ScriptFunctionCall.UpdateExchangeRate.Builder();
        builder.currency = script.ty_args.get(0);
        builder.sliding_nonce = new BcsDeserializer(script.args.get(0).content()).deserialize_u64();
        builder.new_exchange_rate_numerator = new BcsDeserializer(script.args.get(1).content()).deserialize_u64();
        builder.new_exchange_rate_denominator = new BcsDeserializer(script.args.get(2).content()).deserialize_u64();
        return builder.build();
    }

    private static ScriptFunctionCall decode_update_minting_ability_script_function(TransactionPayload payload) throws DeserializationError, IllegalArgumentException, IndexOutOfBoundsException {
        if(!(payload instanceof TransactionPayload.ScriptFunction)) {
                        throw new IllegalArgumentException("Transaction payload not a Script Function");
                }
        ScriptFunction script = ((TransactionPayload.ScriptFunction)payload).value;
        ScriptFunctionCall.UpdateMintingAbility.Builder builder = new ScriptFunctionCall.UpdateMintingAbility.Builder();
        builder.currency = script.ty_args.get(0);
        builder.allow_minting = new BcsDeserializer(script.args.get(0).content()).deserialize_bool();
        return builder.build();
    }

    interface ScriptEncodingHelper {
        public Script encode(ScriptCall call);
    }

    private static final java.util.Map<Class<?>, ScriptEncodingHelper> TRANSACTION_SCRIPT_ENCODER_MAP = initTransactionScriptEncoderMap();

    private static java.util.Map<Class<?>, ScriptEncodingHelper> initTransactionScriptEncoderMap() {
        java.util.HashMap<Class<?>, ScriptEncodingHelper> map = new java.util.HashMap<>();
        map.put(ScriptCall.AddCurrencyToAccount.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.AddCurrencyToAccount obj = (ScriptCall.AddCurrencyToAccount)call;
            return Helpers.encode_add_currency_to_account_script(obj.currency);
        }));
        map.put(ScriptCall.AddRecoveryRotationCapability.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.AddRecoveryRotationCapability obj = (ScriptCall.AddRecoveryRotationCapability)call;
            return Helpers.encode_add_recovery_rotation_capability_script(obj.recovery_address);
        }));
        map.put(ScriptCall.AddValidatorAndReconfigure.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.AddValidatorAndReconfigure obj = (ScriptCall.AddValidatorAndReconfigure)call;
            return Helpers.encode_add_validator_and_reconfigure_script(obj.sliding_nonce, obj.validator_name, obj.validator_address);
        }));
        map.put(ScriptCall.Burn.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.Burn obj = (ScriptCall.Burn)call;
            return Helpers.encode_burn_script(obj.token, obj.sliding_nonce, obj.preburn_address);
        }));
        map.put(ScriptCall.BurnTxnFees.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.BurnTxnFees obj = (ScriptCall.BurnTxnFees)call;
            return Helpers.encode_burn_txn_fees_script(obj.coin_type);
        }));
        map.put(ScriptCall.CancelBurn.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.CancelBurn obj = (ScriptCall.CancelBurn)call;
            return Helpers.encode_cancel_burn_script(obj.token, obj.preburn_address);
        }));
        map.put(ScriptCall.CreateChildVaspAccount.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.CreateChildVaspAccount obj = (ScriptCall.CreateChildVaspAccount)call;
            return Helpers.encode_create_child_vasp_account_script(obj.coin_type, obj.child_address, obj.auth_key_prefix, obj.add_all_currencies, obj.child_initial_balance);
        }));
        map.put(ScriptCall.CreateDesignatedDealer.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.CreateDesignatedDealer obj = (ScriptCall.CreateDesignatedDealer)call;
            return Helpers.encode_create_designated_dealer_script(obj.currency, obj.sliding_nonce, obj.addr, obj.auth_key_prefix, obj.human_name, obj.add_all_currencies);
        }));
        map.put(ScriptCall.CreateParentVaspAccount.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.CreateParentVaspAccount obj = (ScriptCall.CreateParentVaspAccount)call;
            return Helpers.encode_create_parent_vasp_account_script(obj.coin_type, obj.sliding_nonce, obj.new_account_address, obj.auth_key_prefix, obj.human_name, obj.add_all_currencies);
        }));
        map.put(ScriptCall.CreateRecoveryAddress.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.CreateRecoveryAddress obj = (ScriptCall.CreateRecoveryAddress)call;
            return Helpers.encode_create_recovery_address_script();
        }));
        map.put(ScriptCall.CreateValidatorAccount.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.CreateValidatorAccount obj = (ScriptCall.CreateValidatorAccount)call;
            return Helpers.encode_create_validator_account_script(obj.sliding_nonce, obj.new_account_address, obj.auth_key_prefix, obj.human_name);
        }));
        map.put(ScriptCall.CreateValidatorOperatorAccount.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.CreateValidatorOperatorAccount obj = (ScriptCall.CreateValidatorOperatorAccount)call;
            return Helpers.encode_create_validator_operator_account_script(obj.sliding_nonce, obj.new_account_address, obj.auth_key_prefix, obj.human_name);
        }));
        map.put(ScriptCall.FreezeAccount.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.FreezeAccount obj = (ScriptCall.FreezeAccount)call;
            return Helpers.encode_freeze_account_script(obj.sliding_nonce, obj.to_freeze_account);
        }));
        map.put(ScriptCall.PeerToPeerWithMetadata.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.PeerToPeerWithMetadata obj = (ScriptCall.PeerToPeerWithMetadata)call;
            return Helpers.encode_peer_to_peer_with_metadata_script(obj.currency, obj.payee, obj.amount, obj.metadata, obj.metadata_signature);
        }));
        map.put(ScriptCall.Preburn.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.Preburn obj = (ScriptCall.Preburn)call;
            return Helpers.encode_preburn_script(obj.token, obj.amount);
        }));
        map.put(ScriptCall.PublishSharedEd25519PublicKey.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.PublishSharedEd25519PublicKey obj = (ScriptCall.PublishSharedEd25519PublicKey)call;
            return Helpers.encode_publish_shared_ed25519_public_key_script(obj.public_key);
        }));
        map.put(ScriptCall.RegisterValidatorConfig.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.RegisterValidatorConfig obj = (ScriptCall.RegisterValidatorConfig)call;
            return Helpers.encode_register_validator_config_script(obj.validator_account, obj.consensus_pubkey, obj.validator_network_addresses, obj.fullnode_network_addresses);
        }));
        map.put(ScriptCall.RemoveValidatorAndReconfigure.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.RemoveValidatorAndReconfigure obj = (ScriptCall.RemoveValidatorAndReconfigure)call;
            return Helpers.encode_remove_validator_and_reconfigure_script(obj.sliding_nonce, obj.validator_name, obj.validator_address);
        }));
        map.put(ScriptCall.RotateAuthenticationKey.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.RotateAuthenticationKey obj = (ScriptCall.RotateAuthenticationKey)call;
            return Helpers.encode_rotate_authentication_key_script(obj.new_key);
        }));
        map.put(ScriptCall.RotateAuthenticationKeyWithNonce.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.RotateAuthenticationKeyWithNonce obj = (ScriptCall.RotateAuthenticationKeyWithNonce)call;
            return Helpers.encode_rotate_authentication_key_with_nonce_script(obj.sliding_nonce, obj.new_key);
        }));
        map.put(ScriptCall.RotateAuthenticationKeyWithNonceAdmin.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.RotateAuthenticationKeyWithNonceAdmin obj = (ScriptCall.RotateAuthenticationKeyWithNonceAdmin)call;
            return Helpers.encode_rotate_authentication_key_with_nonce_admin_script(obj.sliding_nonce, obj.new_key);
        }));
        map.put(ScriptCall.RotateAuthenticationKeyWithRecoveryAddress.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.RotateAuthenticationKeyWithRecoveryAddress obj = (ScriptCall.RotateAuthenticationKeyWithRecoveryAddress)call;
            return Helpers.encode_rotate_authentication_key_with_recovery_address_script(obj.recovery_address, obj.to_recover, obj.new_key);
        }));
        map.put(ScriptCall.RotateDualAttestationInfo.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.RotateDualAttestationInfo obj = (ScriptCall.RotateDualAttestationInfo)call;
            return Helpers.encode_rotate_dual_attestation_info_script(obj.new_url, obj.new_key);
        }));
        map.put(ScriptCall.RotateSharedEd25519PublicKey.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.RotateSharedEd25519PublicKey obj = (ScriptCall.RotateSharedEd25519PublicKey)call;
            return Helpers.encode_rotate_shared_ed25519_public_key_script(obj.public_key);
        }));
        map.put(ScriptCall.SetValidatorConfigAndReconfigure.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.SetValidatorConfigAndReconfigure obj = (ScriptCall.SetValidatorConfigAndReconfigure)call;
            return Helpers.encode_set_validator_config_and_reconfigure_script(obj.validator_account, obj.consensus_pubkey, obj.validator_network_addresses, obj.fullnode_network_addresses);
        }));
        map.put(ScriptCall.SetValidatorOperator.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.SetValidatorOperator obj = (ScriptCall.SetValidatorOperator)call;
            return Helpers.encode_set_validator_operator_script(obj.operator_name, obj.operator_account);
        }));
        map.put(ScriptCall.SetValidatorOperatorWithNonceAdmin.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.SetValidatorOperatorWithNonceAdmin obj = (ScriptCall.SetValidatorOperatorWithNonceAdmin)call;
            return Helpers.encode_set_validator_operator_with_nonce_admin_script(obj.sliding_nonce, obj.operator_name, obj.operator_account);
        }));
        map.put(ScriptCall.TieredMint.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.TieredMint obj = (ScriptCall.TieredMint)call;
            return Helpers.encode_tiered_mint_script(obj.coin_type, obj.sliding_nonce, obj.designated_dealer_address, obj.mint_amount, obj.tier_index);
        }));
        map.put(ScriptCall.UnfreezeAccount.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.UnfreezeAccount obj = (ScriptCall.UnfreezeAccount)call;
            return Helpers.encode_unfreeze_account_script(obj.sliding_nonce, obj.to_unfreeze_account);
        }));
        map.put(ScriptCall.UpdateDiemVersion.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.UpdateDiemVersion obj = (ScriptCall.UpdateDiemVersion)call;
            return Helpers.encode_update_diem_version_script(obj.sliding_nonce, obj.major);
        }));
        map.put(ScriptCall.UpdateDualAttestationLimit.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.UpdateDualAttestationLimit obj = (ScriptCall.UpdateDualAttestationLimit)call;
            return Helpers.encode_update_dual_attestation_limit_script(obj.sliding_nonce, obj.new_micro_xdx_limit);
        }));
        map.put(ScriptCall.UpdateExchangeRate.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.UpdateExchangeRate obj = (ScriptCall.UpdateExchangeRate)call;
            return Helpers.encode_update_exchange_rate_script(obj.currency, obj.sliding_nonce, obj.new_exchange_rate_numerator, obj.new_exchange_rate_denominator);
        }));
        map.put(ScriptCall.UpdateMintingAbility.class, (ScriptEncodingHelper)((call) -> {
            ScriptCall.UpdateMintingAbility obj = (ScriptCall.UpdateMintingAbility)call;
            return Helpers.encode_update_minting_ability_script(obj.currency, obj.allow_minting);
        }));
        return map;
    }

    interface ScriptFunctionEncodingHelper {
        public TransactionPayload encode(ScriptFunctionCall call);
    }

    private static final java.util.Map<Class<?>, ScriptFunctionEncodingHelper> SCRIPT_FUNCTION_ENCODER_MAP = initScriptFunctionEncoderMap();

    private static java.util.Map<Class<?>, ScriptFunctionEncodingHelper> initScriptFunctionEncoderMap() {
        java.util.HashMap<Class<?>, ScriptFunctionEncodingHelper> map = new java.util.HashMap<>();
        map.put(ScriptFunctionCall.AddCurrencyToAccount.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.AddCurrencyToAccount obj = (ScriptFunctionCall.AddCurrencyToAccount)call;
            return Helpers.encode_add_currency_to_account_script_function(obj.currency);
        }));
        map.put(ScriptFunctionCall.AddRecoveryRotationCapability.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.AddRecoveryRotationCapability obj = (ScriptFunctionCall.AddRecoveryRotationCapability)call;
            return Helpers.encode_add_recovery_rotation_capability_script_function(obj.recovery_address);
        }));
        map.put(ScriptFunctionCall.AddValidatorAndReconfigure.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.AddValidatorAndReconfigure obj = (ScriptFunctionCall.AddValidatorAndReconfigure)call;
            return Helpers.encode_add_validator_and_reconfigure_script_function(obj.sliding_nonce, obj.validator_name, obj.validator_address);
        }));
        map.put(ScriptFunctionCall.BurnTxnFees.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.BurnTxnFees obj = (ScriptFunctionCall.BurnTxnFees)call;
            return Helpers.encode_burn_txn_fees_script_function(obj.coin_type);
        }));
        map.put(ScriptFunctionCall.BurnWithAmount.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.BurnWithAmount obj = (ScriptFunctionCall.BurnWithAmount)call;
            return Helpers.encode_burn_with_amount_script_function(obj.token, obj.sliding_nonce, obj.preburn_address, obj.amount);
        }));
        map.put(ScriptFunctionCall.CancelBurnWithAmount.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.CancelBurnWithAmount obj = (ScriptFunctionCall.CancelBurnWithAmount)call;
            return Helpers.encode_cancel_burn_with_amount_script_function(obj.token, obj.preburn_address, obj.amount);
        }));
        map.put(ScriptFunctionCall.CreateChildVaspAccount.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.CreateChildVaspAccount obj = (ScriptFunctionCall.CreateChildVaspAccount)call;
            return Helpers.encode_create_child_vasp_account_script_function(obj.coin_type, obj.child_address, obj.auth_key_prefix, obj.add_all_currencies, obj.child_initial_balance);
        }));
        map.put(ScriptFunctionCall.CreateDesignatedDealer.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.CreateDesignatedDealer obj = (ScriptFunctionCall.CreateDesignatedDealer)call;
            return Helpers.encode_create_designated_dealer_script_function(obj.currency, obj.sliding_nonce, obj.addr, obj.auth_key_prefix, obj.human_name, obj.add_all_currencies);
        }));
        map.put(ScriptFunctionCall.CreateParentVaspAccount.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.CreateParentVaspAccount obj = (ScriptFunctionCall.CreateParentVaspAccount)call;
            return Helpers.encode_create_parent_vasp_account_script_function(obj.coin_type, obj.sliding_nonce, obj.new_account_address, obj.auth_key_prefix, obj.human_name, obj.add_all_currencies);
        }));
        map.put(ScriptFunctionCall.CreateRecoveryAddress.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.CreateRecoveryAddress obj = (ScriptFunctionCall.CreateRecoveryAddress)call;
            return Helpers.encode_create_recovery_address_script_function();
        }));
        map.put(ScriptFunctionCall.CreateValidatorAccount.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.CreateValidatorAccount obj = (ScriptFunctionCall.CreateValidatorAccount)call;
            return Helpers.encode_create_validator_account_script_function(obj.sliding_nonce, obj.new_account_address, obj.auth_key_prefix, obj.human_name);
        }));
        map.put(ScriptFunctionCall.CreateValidatorOperatorAccount.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.CreateValidatorOperatorAccount obj = (ScriptFunctionCall.CreateValidatorOperatorAccount)call;
            return Helpers.encode_create_validator_operator_account_script_function(obj.sliding_nonce, obj.new_account_address, obj.auth_key_prefix, obj.human_name);
        }));
        map.put(ScriptFunctionCall.FreezeAccount.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.FreezeAccount obj = (ScriptFunctionCall.FreezeAccount)call;
            return Helpers.encode_freeze_account_script_function(obj.sliding_nonce, obj.to_freeze_account);
        }));
        map.put(ScriptFunctionCall.InitializeDiemConsensusConfig.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.InitializeDiemConsensusConfig obj = (ScriptFunctionCall.InitializeDiemConsensusConfig)call;
            return Helpers.encode_initialize_diem_consensus_config_script_function(obj.sliding_nonce);
        }));
        map.put(ScriptFunctionCall.PeerToPeerWithMetadata.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.PeerToPeerWithMetadata obj = (ScriptFunctionCall.PeerToPeerWithMetadata)call;
            return Helpers.encode_peer_to_peer_with_metadata_script_function(obj.currency, obj.payee, obj.amount, obj.metadata, obj.metadata_signature);
        }));
        map.put(ScriptFunctionCall.Preburn.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.Preburn obj = (ScriptFunctionCall.Preburn)call;
            return Helpers.encode_preburn_script_function(obj.token, obj.amount);
        }));
        map.put(ScriptFunctionCall.PublishSharedEd25519PublicKey.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.PublishSharedEd25519PublicKey obj = (ScriptFunctionCall.PublishSharedEd25519PublicKey)call;
            return Helpers.encode_publish_shared_ed25519_public_key_script_function(obj.public_key);
        }));
        map.put(ScriptFunctionCall.RegisterValidatorConfig.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.RegisterValidatorConfig obj = (ScriptFunctionCall.RegisterValidatorConfig)call;
            return Helpers.encode_register_validator_config_script_function(obj.validator_account, obj.consensus_pubkey, obj.validator_network_addresses, obj.fullnode_network_addresses);
        }));
        map.put(ScriptFunctionCall.RemoveValidatorAndReconfigure.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.RemoveValidatorAndReconfigure obj = (ScriptFunctionCall.RemoveValidatorAndReconfigure)call;
            return Helpers.encode_remove_validator_and_reconfigure_script_function(obj.sliding_nonce, obj.validator_name, obj.validator_address);
        }));
        map.put(ScriptFunctionCall.RotateAuthenticationKey.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.RotateAuthenticationKey obj = (ScriptFunctionCall.RotateAuthenticationKey)call;
            return Helpers.encode_rotate_authentication_key_script_function(obj.new_key);
        }));
        map.put(ScriptFunctionCall.RotateAuthenticationKeyWithNonce.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.RotateAuthenticationKeyWithNonce obj = (ScriptFunctionCall.RotateAuthenticationKeyWithNonce)call;
            return Helpers.encode_rotate_authentication_key_with_nonce_script_function(obj.sliding_nonce, obj.new_key);
        }));
        map.put(ScriptFunctionCall.RotateAuthenticationKeyWithNonceAdmin.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.RotateAuthenticationKeyWithNonceAdmin obj = (ScriptFunctionCall.RotateAuthenticationKeyWithNonceAdmin)call;
            return Helpers.encode_rotate_authentication_key_with_nonce_admin_script_function(obj.sliding_nonce, obj.new_key);
        }));
        map.put(ScriptFunctionCall.RotateAuthenticationKeyWithRecoveryAddress.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.RotateAuthenticationKeyWithRecoveryAddress obj = (ScriptFunctionCall.RotateAuthenticationKeyWithRecoveryAddress)call;
            return Helpers.encode_rotate_authentication_key_with_recovery_address_script_function(obj.recovery_address, obj.to_recover, obj.new_key);
        }));
        map.put(ScriptFunctionCall.RotateDualAttestationInfo.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.RotateDualAttestationInfo obj = (ScriptFunctionCall.RotateDualAttestationInfo)call;
            return Helpers.encode_rotate_dual_attestation_info_script_function(obj.new_url, obj.new_key);
        }));
        map.put(ScriptFunctionCall.RotateSharedEd25519PublicKey.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.RotateSharedEd25519PublicKey obj = (ScriptFunctionCall.RotateSharedEd25519PublicKey)call;
            return Helpers.encode_rotate_shared_ed25519_public_key_script_function(obj.public_key);
        }));
        map.put(ScriptFunctionCall.SetGasConstants.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.SetGasConstants obj = (ScriptFunctionCall.SetGasConstants)call;
            return Helpers.encode_set_gas_constants_script_function(obj.sliding_nonce, obj.global_memory_per_byte_cost, obj.global_memory_per_byte_write_cost, obj.min_transaction_gas_units, obj.large_transaction_cutoff, obj.intrinsic_gas_per_byte, obj.maximum_number_of_gas_units, obj.min_price_per_gas_unit, obj.max_price_per_gas_unit, obj.max_transaction_size_in_bytes, obj.gas_unit_scaling_factor, obj.default_account_size);
        }));
        map.put(ScriptFunctionCall.SetValidatorConfigAndReconfigure.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.SetValidatorConfigAndReconfigure obj = (ScriptFunctionCall.SetValidatorConfigAndReconfigure)call;
            return Helpers.encode_set_validator_config_and_reconfigure_script_function(obj.validator_account, obj.consensus_pubkey, obj.validator_network_addresses, obj.fullnode_network_addresses);
        }));
        map.put(ScriptFunctionCall.SetValidatorOperator.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.SetValidatorOperator obj = (ScriptFunctionCall.SetValidatorOperator)call;
            return Helpers.encode_set_validator_operator_script_function(obj.operator_name, obj.operator_account);
        }));
        map.put(ScriptFunctionCall.SetValidatorOperatorWithNonceAdmin.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.SetValidatorOperatorWithNonceAdmin obj = (ScriptFunctionCall.SetValidatorOperatorWithNonceAdmin)call;
            return Helpers.encode_set_validator_operator_with_nonce_admin_script_function(obj.sliding_nonce, obj.operator_name, obj.operator_account);
        }));
        map.put(ScriptFunctionCall.TieredMint.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.TieredMint obj = (ScriptFunctionCall.TieredMint)call;
            return Helpers.encode_tiered_mint_script_function(obj.coin_type, obj.sliding_nonce, obj.designated_dealer_address, obj.mint_amount, obj.tier_index);
        }));
        map.put(ScriptFunctionCall.UnfreezeAccount.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.UnfreezeAccount obj = (ScriptFunctionCall.UnfreezeAccount)call;
            return Helpers.encode_unfreeze_account_script_function(obj.sliding_nonce, obj.to_unfreeze_account);
        }));
        map.put(ScriptFunctionCall.UpdateDiemConsensusConfig.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.UpdateDiemConsensusConfig obj = (ScriptFunctionCall.UpdateDiemConsensusConfig)call;
            return Helpers.encode_update_diem_consensus_config_script_function(obj.sliding_nonce, obj.config);
        }));
        map.put(ScriptFunctionCall.UpdateDiemVersion.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.UpdateDiemVersion obj = (ScriptFunctionCall.UpdateDiemVersion)call;
            return Helpers.encode_update_diem_version_script_function(obj.sliding_nonce, obj.major);
        }));
        map.put(ScriptFunctionCall.UpdateDualAttestationLimit.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.UpdateDualAttestationLimit obj = (ScriptFunctionCall.UpdateDualAttestationLimit)call;
            return Helpers.encode_update_dual_attestation_limit_script_function(obj.sliding_nonce, obj.new_micro_xdx_limit);
        }));
        map.put(ScriptFunctionCall.UpdateExchangeRate.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.UpdateExchangeRate obj = (ScriptFunctionCall.UpdateExchangeRate)call;
            return Helpers.encode_update_exchange_rate_script_function(obj.currency, obj.sliding_nonce, obj.new_exchange_rate_numerator, obj.new_exchange_rate_denominator);
        }));
        map.put(ScriptFunctionCall.UpdateMintingAbility.class, (ScriptFunctionEncodingHelper)((call) -> {
            ScriptFunctionCall.UpdateMintingAbility obj = (ScriptFunctionCall.UpdateMintingAbility)call;
            return Helpers.encode_update_minting_ability_script_function(obj.currency, obj.allow_minting);
        }));
        return map;
    }

    private static byte[] ADD_CURRENCY_TO_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 2, 3, 2, 6, 4, 8, 2, 5, 10, 7, 7, 17, 25, 8, 42, 16, 0, 0, 0, 1, 0, 1, 1, 1, 0, 2, 1, 6, 12, 0, 1, 9, 0, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 12, 97, 100, 100, 95, 99, 117, 114, 114, 101, 110, 99, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 3, 11, 0, 56, 0, 2};

    private static byte[] ADD_RECOVERY_ROTATION_CAPABILITY_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 2, 4, 4, 3, 8, 10, 5, 18, 15, 7, 33, 106, 8, -117, 1, 16, 0, 0, 0, 1, 0, 2, 1, 0, 0, 3, 0, 1, 0, 1, 4, 2, 3, 0, 1, 6, 12, 1, 8, 0, 2, 8, 0, 5, 0, 2, 6, 12, 5, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 15, 82, 101, 99, 111, 118, 101, 114, 121, 65, 100, 100, 114, 101, 115, 115, 21, 75, 101, 121, 82, 111, 116, 97, 116, 105, 111, 110, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 101, 120, 116, 114, 97, 99, 116, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 23, 97, 100, 100, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 4, 3, 5, 11, 0, 17, 0, 10, 1, 17, 1, 2};

    private static byte[] ADD_VALIDATOR_AND_RECONFIGURE_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 6, 3, 6, 15, 5, 21, 24, 7, 45, 91, 8, -120, 1, 16, 0, 0, 0, 1, 0, 2, 1, 3, 0, 1, 0, 2, 4, 2, 3, 0, 0, 5, 4, 1, 0, 2, 6, 12, 3, 0, 1, 5, 1, 10, 2, 2, 6, 12, 5, 4, 6, 12, 3, 10, 2, 5, 2, 1, 3, 10, 68, 105, 101, 109, 83, 121, 115, 116, 101, 109, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 15, 86, 97, 108, 105, 100, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 14, 103, 101, 116, 95, 104, 117, 109, 97, 110, 95, 110, 97, 109, 101, 13, 97, 100, 100, 95, 118, 97, 108, 105, 100, 97, 116, 111, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 6, 18, 10, 0, 10, 1, 17, 0, 10, 3, 17, 1, 11, 2, 33, 12, 4, 11, 4, 3, 14, 11, 0, 1, 6, 0, 0, 0, 0, 0, 0, 0, 0, 39, 11, 0, 10, 3, 17, 2, 2};

    private static byte[] BURN_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 3, 4, 11, 4, 15, 2, 5, 17, 17, 7, 34, 45, 8, 79, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 1, 1, 1, 4, 2, 6, 12, 3, 0, 2, 6, 12, 5, 3, 6, 12, 3, 5, 1, 9, 0, 4, 68, 105, 101, 109, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 4, 98, 117, 114, 110, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 3, 1, 7, 10, 0, 10, 1, 17, 0, 11, 0, 10, 2, 56, 0, 2};

    private static byte[] BURN_TXN_FEES_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 2, 3, 2, 6, 4, 8, 2, 5, 10, 7, 7, 17, 25, 8, 42, 16, 0, 0, 0, 1, 0, 1, 1, 1, 0, 2, 1, 6, 12, 0, 1, 9, 0, 14, 84, 114, 97, 110, 115, 97, 99, 116, 105, 111, 110, 70, 101, 101, 9, 98, 117, 114, 110, 95, 102, 101, 101, 115, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 3, 11, 0, 56, 0, 2};

    private static byte[] CANCEL_BURN_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 2, 3, 2, 6, 4, 8, 2, 5, 10, 8, 7, 18, 24, 8, 42, 16, 0, 0, 0, 1, 0, 1, 1, 1, 0, 2, 2, 6, 12, 5, 0, 1, 9, 0, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 11, 99, 97, 110, 99, 101, 108, 95, 98, 117, 114, 110, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 4, 11, 0, 10, 1, 56, 0, 2};

    private static byte[] CREATE_CHILD_VASP_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 8, 1, 0, 2, 2, 2, 4, 3, 6, 22, 4, 28, 4, 5, 32, 35, 7, 67, 122, 8, -67, 1, 16, 6, -51, 1, 4, 0, 0, 0, 1, 1, 0, 0, 2, 0, 1, 1, 1, 0, 3, 2, 3, 0, 0, 4, 4, 1, 1, 1, 0, 5, 3, 1, 0, 0, 6, 2, 6, 4, 6, 12, 5, 10, 2, 1, 0, 1, 6, 12, 1, 8, 0, 5, 6, 8, 0, 5, 3, 10, 2, 10, 2, 5, 6, 12, 5, 10, 2, 1, 3, 1, 9, 0, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 18, 87, 105, 116, 104, 100, 114, 97, 119, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 25, 99, 114, 101, 97, 116, 101, 95, 99, 104, 105, 108, 100, 95, 118, 97, 115, 112, 95, 97, 99, 99, 111, 117, 110, 116, 27, 101, 120, 116, 114, 97, 99, 116, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 8, 112, 97, 121, 95, 102, 114, 111, 109, 27, 114, 101, 115, 116, 111, 114, 101, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 2, 1, 0, 1, 1, 5, 3, 25, 10, 0, 10, 1, 11, 2, 10, 3, 56, 0, 10, 4, 6, 0, 0, 0, 0, 0, 0, 0, 0, 36, 3, 10, 5, 22, 11, 0, 17, 1, 12, 5, 14, 5, 10, 1, 10, 4, 7, 0, 7, 0, 56, 1, 11, 5, 17, 3, 5, 24, 11, 0, 1, 2};

    private static byte[] CREATE_DESIGNATED_DEALER_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 3, 4, 11, 4, 15, 2, 5, 17, 27, 7, 44, 72, 8, 116, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 1, 1, 1, 4, 2, 6, 12, 3, 0, 5, 6, 12, 5, 10, 2, 10, 2, 1, 6, 6, 12, 3, 5, 10, 2, 10, 2, 1, 1, 9, 0, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 24, 99, 114, 101, 97, 116, 101, 95, 100, 101, 115, 105, 103, 110, 97, 116, 101, 100, 95, 100, 101, 97, 108, 101, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 3, 1, 10, 10, 0, 10, 1, 17, 0, 11, 0, 10, 2, 11, 3, 11, 4, 10, 5, 56, 0, 2};

    private static byte[] CREATE_PARENT_VASP_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 3, 4, 11, 4, 15, 2, 5, 17, 27, 7, 44, 74, 8, 118, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 1, 1, 1, 4, 2, 6, 12, 3, 0, 5, 6, 12, 5, 10, 2, 10, 2, 1, 6, 6, 12, 3, 5, 10, 2, 10, 2, 1, 1, 9, 0, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 26, 99, 114, 101, 97, 116, 101, 95, 112, 97, 114, 101, 110, 116, 95, 118, 97, 115, 112, 95, 97, 99, 99, 111, 117, 110, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 3, 1, 10, 10, 0, 10, 1, 17, 0, 11, 0, 10, 2, 11, 3, 11, 4, 10, 5, 56, 0, 2};

    private static byte[] CREATE_RECOVERY_ADDRESS_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 2, 4, 4, 3, 8, 10, 5, 18, 12, 7, 30, 90, 8, 120, 16, 0, 0, 0, 1, 0, 2, 1, 0, 0, 3, 0, 1, 0, 1, 4, 2, 3, 0, 1, 6, 12, 1, 8, 0, 2, 6, 12, 8, 0, 0, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 15, 82, 101, 99, 111, 118, 101, 114, 121, 65, 100, 100, 114, 101, 115, 115, 21, 75, 101, 121, 82, 111, 116, 97, 116, 105, 111, 110, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 101, 120, 116, 114, 97, 99, 116, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 7, 112, 117, 98, 108, 105, 115, 104, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 3, 5, 10, 0, 11, 0, 17, 0, 17, 1, 2};

    private static byte[] CREATE_VALIDATOR_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 22, 7, 36, 72, 8, 108, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 0, 2, 6, 12, 3, 0, 4, 6, 12, 5, 10, 2, 10, 2, 5, 6, 12, 3, 5, 10, 2, 10, 2, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 24, 99, 114, 101, 97, 116, 101, 95, 118, 97, 108, 105, 100, 97, 116, 111, 114, 95, 97, 99, 99, 111, 117, 110, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 1, 9, 10, 0, 10, 1, 17, 0, 11, 0, 10, 2, 11, 3, 11, 4, 17, 1, 2};

    private static byte[] CREATE_VALIDATOR_OPERATOR_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 22, 7, 36, 81, 8, 117, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 0, 2, 6, 12, 3, 0, 4, 6, 12, 5, 10, 2, 10, 2, 5, 6, 12, 3, 5, 10, 2, 10, 2, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 33, 99, 114, 101, 97, 116, 101, 95, 118, 97, 108, 105, 100, 97, 116, 111, 114, 95, 111, 112, 101, 114, 97, 116, 111, 114, 95, 97, 99, 99, 111, 117, 110, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 1, 9, 10, 0, 10, 1, 17, 0, 11, 0, 10, 2, 11, 3, 11, 4, 17, 1, 2};

    private static byte[] FREEZE_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 14, 7, 28, 66, 8, 94, 16, 0, 0, 0, 1, 0, 2, 0, 1, 0, 1, 3, 2, 1, 0, 2, 6, 12, 5, 0, 2, 6, 12, 3, 3, 6, 12, 3, 5, 15, 65, 99, 99, 111, 117, 110, 116, 70, 114, 101, 101, 122, 105, 110, 103, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 14, 102, 114, 101, 101, 122, 101, 95, 97, 99, 99, 111, 117, 110, 116, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 1, 7, 10, 0, 10, 1, 17, 1, 11, 0, 10, 2, 17, 0, 2};

    private static byte[] PEER_TO_PEER_WITH_METADATA_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 7, 1, 0, 2, 2, 2, 4, 3, 6, 16, 4, 22, 2, 5, 24, 29, 7, 53, 96, 8, -107, 1, 16, 0, 0, 0, 1, 1, 0, 0, 2, 0, 1, 0, 0, 3, 2, 3, 1, 1, 0, 4, 1, 3, 0, 1, 5, 1, 6, 12, 1, 8, 0, 5, 6, 8, 0, 5, 3, 10, 2, 10, 2, 0, 5, 6, 12, 5, 3, 10, 2, 10, 2, 1, 9, 0, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 18, 87, 105, 116, 104, 100, 114, 97, 119, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 27, 101, 120, 116, 114, 97, 99, 116, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 8, 112, 97, 121, 95, 102, 114, 111, 109, 27, 114, 101, 115, 116, 111, 114, 101, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 4, 1, 12, 11, 0, 17, 0, 12, 5, 14, 5, 10, 1, 10, 2, 11, 3, 11, 4, 56, 0, 11, 5, 17, 2, 2};

    private static byte[] PREBURN_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 7, 1, 0, 2, 2, 2, 4, 3, 6, 16, 4, 22, 2, 5, 24, 21, 7, 45, 95, 8, -116, 1, 16, 0, 0, 0, 1, 1, 0, 0, 2, 0, 1, 0, 0, 3, 2, 3, 1, 1, 0, 4, 1, 3, 0, 1, 5, 1, 6, 12, 1, 8, 0, 3, 6, 12, 6, 8, 0, 3, 0, 2, 6, 12, 3, 1, 9, 0, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 18, 87, 105, 116, 104, 100, 114, 97, 119, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 27, 101, 120, 116, 114, 97, 99, 116, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 7, 112, 114, 101, 98, 117, 114, 110, 27, 114, 101, 115, 116, 111, 114, 101, 95, 119, 105, 116, 104, 100, 114, 97, 119, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 4, 1, 10, 10, 0, 17, 0, 12, 2, 11, 0, 14, 2, 10, 1, 56, 0, 11, 2, 17, 2, 2};

    private static byte[] PUBLISH_SHARED_ED25519_PUBLIC_KEY_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 2, 3, 2, 5, 5, 7, 6, 7, 13, 31, 8, 44, 16, 0, 0, 0, 1, 0, 1, 0, 2, 6, 12, 10, 2, 0, 22, 83, 104, 97, 114, 101, 100, 69, 100, 50, 53, 53, 49, 57, 80, 117, 98, 108, 105, 99, 75, 101, 121, 7, 112, 117, 98, 108, 105, 115, 104, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 4, 11, 0, 11, 1, 17, 0, 2};

    private static byte[] REGISTER_VALIDATOR_CONFIG_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 2, 3, 2, 5, 5, 7, 11, 7, 18, 27, 8, 45, 16, 0, 0, 0, 1, 0, 1, 0, 5, 6, 12, 5, 10, 2, 10, 2, 10, 2, 0, 15, 86, 97, 108, 105, 100, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 10, 115, 101, 116, 95, 99, 111, 110, 102, 105, 103, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 7, 11, 0, 10, 1, 11, 2, 11, 3, 11, 4, 17, 0, 2};

    private static byte[] REMOVE_VALIDATOR_AND_RECONFIGURE_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 6, 3, 6, 15, 5, 21, 24, 7, 45, 94, 8, -117, 1, 16, 0, 0, 0, 1, 0, 2, 1, 3, 0, 1, 0, 2, 4, 2, 3, 0, 0, 5, 4, 1, 0, 2, 6, 12, 3, 0, 1, 5, 1, 10, 2, 2, 6, 12, 5, 4, 6, 12, 3, 10, 2, 5, 2, 1, 3, 10, 68, 105, 101, 109, 83, 121, 115, 116, 101, 109, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 15, 86, 97, 108, 105, 100, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 14, 103, 101, 116, 95, 104, 117, 109, 97, 110, 95, 110, 97, 109, 101, 16, 114, 101, 109, 111, 118, 101, 95, 118, 97, 108, 105, 100, 97, 116, 111, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 6, 18, 10, 0, 10, 1, 17, 0, 10, 3, 17, 1, 11, 2, 33, 12, 4, 11, 4, 3, 14, 11, 0, 1, 6, 0, 0, 0, 0, 0, 0, 0, 0, 39, 11, 0, 10, 3, 17, 2, 2};

    private static byte[] ROTATE_AUTHENTICATION_KEY_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 2, 2, 2, 4, 3, 6, 15, 5, 21, 18, 7, 39, 124, 8, -93, 1, 16, 0, 0, 0, 1, 1, 0, 0, 2, 0, 1, 0, 0, 3, 1, 2, 0, 0, 4, 3, 2, 0, 1, 6, 12, 1, 8, 0, 0, 2, 6, 8, 0, 10, 2, 2, 6, 12, 10, 2, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 21, 75, 101, 121, 82, 111, 116, 97, 116, 105, 111, 110, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 101, 120, 116, 114, 97, 99, 116, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 114, 101, 115, 116, 111, 114, 101, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 25, 114, 111, 116, 97, 116, 101, 95, 97, 117, 116, 104, 101, 110, 116, 105, 99, 97, 116, 105, 111, 110, 95, 107, 101, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 4, 1, 9, 11, 0, 17, 0, 12, 2, 14, 2, 11, 1, 17, 2, 11, 2, 17, 1, 2};

    private static byte[] ROTATE_AUTHENTICATION_KEY_WITH_NONCE_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 2, 4, 4, 3, 8, 20, 5, 28, 23, 7, 51, -97, 1, 8, -46, 1, 16, 0, 0, 0, 1, 0, 3, 1, 0, 1, 2, 0, 1, 0, 0, 4, 2, 3, 0, 0, 5, 3, 1, 0, 0, 6, 4, 1, 0, 2, 6, 12, 3, 0, 1, 6, 12, 1, 8, 0, 2, 6, 8, 0, 10, 2, 3, 6, 12, 3, 10, 2, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 21, 75, 101, 121, 82, 111, 116, 97, 116, 105, 111, 110, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 101, 120, 116, 114, 97, 99, 116, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 114, 101, 115, 116, 111, 114, 101, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 25, 114, 111, 116, 97, 116, 101, 95, 97, 117, 116, 104, 101, 110, 116, 105, 99, 97, 116, 105, 111, 110, 95, 107, 101, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 3, 12, 10, 0, 10, 1, 17, 0, 11, 0, 17, 1, 12, 3, 14, 3, 11, 2, 17, 3, 11, 3, 17, 2, 2};

    private static byte[] ROTATE_AUTHENTICATION_KEY_WITH_NONCE_ADMIN_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 2, 4, 4, 3, 8, 20, 5, 28, 25, 7, 53, -97, 1, 8, -44, 1, 16, 0, 0, 0, 1, 0, 3, 1, 0, 1, 2, 0, 1, 0, 0, 4, 2, 3, 0, 0, 5, 3, 1, 0, 0, 6, 4, 1, 0, 2, 6, 12, 3, 0, 1, 6, 12, 1, 8, 0, 2, 6, 8, 0, 10, 2, 4, 6, 12, 6, 12, 3, 10, 2, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 21, 75, 101, 121, 82, 111, 116, 97, 116, 105, 111, 110, 67, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 101, 120, 116, 114, 97, 99, 116, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 31, 114, 101, 115, 116, 111, 114, 101, 95, 107, 101, 121, 95, 114, 111, 116, 97, 116, 105, 111, 110, 95, 99, 97, 112, 97, 98, 105, 108, 105, 116, 121, 25, 114, 111, 116, 97, 116, 101, 95, 97, 117, 116, 104, 101, 110, 116, 105, 99, 97, 116, 105, 111, 110, 95, 107, 101, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 3, 12, 11, 0, 10, 2, 17, 0, 11, 1, 17, 1, 12, 4, 14, 4, 11, 3, 17, 3, 11, 4, 17, 2, 2};

    private static byte[] ROTATE_AUTHENTICATION_KEY_WITH_RECOVERY_ADDRESS_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 2, 3, 2, 5, 5, 7, 8, 7, 15, 42, 8, 57, 16, 0, 0, 0, 1, 0, 1, 0, 4, 6, 12, 5, 5, 10, 2, 0, 15, 82, 101, 99, 111, 118, 101, 114, 121, 65, 100, 100, 114, 101, 115, 115, 25, 114, 111, 116, 97, 116, 101, 95, 97, 117, 116, 104, 101, 110, 116, 105, 99, 97, 116, 105, 111, 110, 95, 107, 101, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 6, 11, 0, 10, 1, 10, 2, 11, 3, 17, 0, 2};

    private static byte[] ROTATE_DUAL_ATTESTATION_INFO_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 2, 3, 2, 10, 5, 12, 13, 7, 25, 61, 8, 86, 16, 0, 0, 0, 1, 0, 1, 0, 0, 2, 0, 1, 0, 2, 6, 12, 10, 2, 0, 3, 6, 12, 10, 2, 10, 2, 15, 68, 117, 97, 108, 65, 116, 116, 101, 115, 116, 97, 116, 105, 111, 110, 15, 114, 111, 116, 97, 116, 101, 95, 98, 97, 115, 101, 95, 117, 114, 108, 28, 114, 111, 116, 97, 116, 101, 95, 99, 111, 109, 112, 108, 105, 97, 110, 99, 101, 95, 112, 117, 98, 108, 105, 99, 95, 107, 101, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 1, 7, 10, 0, 11, 1, 17, 0, 11, 0, 11, 2, 17, 1, 2};

    private static byte[] ROTATE_SHARED_ED25519_PUBLIC_KEY_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 2, 3, 2, 5, 5, 7, 6, 7, 13, 34, 8, 47, 16, 0, 0, 0, 1, 0, 1, 0, 2, 6, 12, 10, 2, 0, 22, 83, 104, 97, 114, 101, 100, 69, 100, 50, 53, 53, 49, 57, 80, 117, 98, 108, 105, 99, 75, 101, 121, 10, 114, 111, 116, 97, 116, 101, 95, 107, 101, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 4, 11, 0, 11, 1, 17, 0, 2};

    private static byte[] SET_VALIDATOR_CONFIG_AND_RECONFIGURE_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 15, 7, 29, 68, 8, 97, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 0, 5, 6, 12, 5, 10, 2, 10, 2, 10, 2, 0, 2, 6, 12, 5, 10, 68, 105, 101, 109, 83, 121, 115, 116, 101, 109, 15, 86, 97, 108, 105, 100, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 10, 115, 101, 116, 95, 99, 111, 110, 102, 105, 103, 29, 117, 112, 100, 97, 116, 101, 95, 99, 111, 110, 102, 105, 103, 95, 97, 110, 100, 95, 114, 101, 99, 111, 110, 102, 105, 103, 117, 114, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 10, 10, 0, 10, 1, 11, 2, 11, 3, 11, 4, 17, 0, 11, 0, 10, 1, 17, 1, 2};

    private static byte[] SET_VALIDATOR_OPERATOR_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 19, 7, 33, 68, 8, 101, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 3, 0, 1, 5, 1, 10, 2, 2, 6, 12, 5, 0, 3, 6, 12, 10, 2, 5, 2, 1, 3, 15, 86, 97, 108, 105, 100, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 23, 86, 97, 108, 105, 100, 97, 116, 111, 114, 79, 112, 101, 114, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 14, 103, 101, 116, 95, 104, 117, 109, 97, 110, 95, 110, 97, 109, 101, 12, 115, 101, 116, 95, 111, 112, 101, 114, 97, 116, 111, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 4, 5, 15, 10, 2, 17, 0, 11, 1, 33, 12, 3, 11, 3, 3, 11, 11, 0, 1, 6, 0, 0, 0, 0, 0, 0, 0, 0, 39, 11, 0, 10, 2, 17, 1, 2};

    private static byte[] SET_VALIDATOR_OPERATOR_WITH_NONCE_ADMIN_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 6, 3, 6, 15, 5, 21, 26, 7, 47, 103, 8, -106, 1, 16, 0, 0, 0, 1, 0, 2, 0, 3, 0, 1, 0, 2, 4, 2, 3, 0, 1, 5, 4, 1, 0, 2, 6, 12, 3, 0, 1, 5, 1, 10, 2, 2, 6, 12, 5, 5, 6, 12, 6, 12, 3, 10, 2, 5, 2, 1, 3, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 15, 86, 97, 108, 105, 100, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 23, 86, 97, 108, 105, 100, 97, 116, 111, 114, 79, 112, 101, 114, 97, 116, 111, 114, 67, 111, 110, 102, 105, 103, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 14, 103, 101, 116, 95, 104, 117, 109, 97, 110, 95, 110, 97, 109, 101, 12, 115, 101, 116, 95, 111, 112, 101, 114, 97, 116, 111, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 6, 18, 11, 0, 10, 2, 17, 0, 10, 4, 17, 1, 11, 3, 33, 12, 5, 11, 5, 3, 14, 11, 1, 1, 6, 0, 0, 0, 0, 0, 0, 0, 0, 39, 11, 1, 10, 4, 17, 2, 2};

    private static byte[] TIERED_MINT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 4, 3, 4, 11, 4, 15, 2, 5, 17, 21, 7, 38, 59, 8, 97, 16, 0, 0, 0, 1, 1, 2, 0, 1, 0, 0, 3, 2, 1, 1, 1, 1, 4, 2, 6, 12, 3, 0, 4, 6, 12, 5, 3, 3, 5, 6, 12, 3, 5, 3, 3, 1, 9, 0, 11, 68, 105, 101, 109, 65, 99, 99, 111, 117, 110, 116, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 11, 116, 105, 101, 114, 101, 100, 95, 109, 105, 110, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 3, 1, 9, 10, 0, 10, 1, 17, 0, 11, 0, 10, 2, 10, 3, 10, 4, 56, 0, 2};

    private static byte[] UNFREEZE_ACCOUNT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 14, 7, 28, 68, 8, 96, 16, 0, 0, 0, 1, 0, 2, 0, 1, 0, 1, 3, 2, 1, 0, 2, 6, 12, 5, 0, 2, 6, 12, 3, 3, 6, 12, 3, 5, 15, 65, 99, 99, 111, 117, 110, 116, 70, 114, 101, 101, 122, 105, 110, 103, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 16, 117, 110, 102, 114, 101, 101, 122, 101, 95, 97, 99, 99, 111, 117, 110, 116, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 1, 7, 10, 0, 10, 1, 17, 1, 11, 0, 10, 2, 17, 0, 2};

    private static byte[] UPDATE_DIEM_VERSION_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 10, 7, 24, 51, 8, 75, 16, 0, 0, 0, 1, 0, 2, 0, 1, 0, 1, 3, 0, 1, 0, 2, 6, 12, 3, 0, 3, 6, 12, 3, 3, 11, 68, 105, 101, 109, 86, 101, 114, 115, 105, 111, 110, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 3, 115, 101, 116, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 1, 7, 10, 0, 10, 1, 17, 1, 11, 0, 10, 2, 17, 0, 2};

    private static byte[] UPDATE_DUAL_ATTESTATION_LIMIT_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 5, 1, 0, 4, 3, 4, 10, 5, 14, 10, 7, 24, 71, 8, 95, 16, 0, 0, 0, 1, 0, 2, 0, 1, 0, 1, 3, 0, 1, 0, 2, 6, 12, 3, 0, 3, 6, 12, 3, 3, 15, 68, 117, 97, 108, 65, 116, 116, 101, 115, 116, 97, 116, 105, 111, 110, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 19, 115, 101, 116, 95, 109, 105, 99, 114, 111, 100, 105, 101, 109, 95, 108, 105, 109, 105, 116, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 1, 7, 10, 0, 10, 1, 17, 1, 11, 0, 10, 2, 17, 0, 2};

    private static byte[] UPDATE_EXCHANGE_RATE_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 7, 1, 0, 6, 2, 6, 4, 3, 10, 16, 4, 26, 2, 5, 28, 25, 7, 53, 99, 8, -104, 1, 16, 0, 0, 0, 1, 0, 2, 1, 1, 2, 0, 1, 3, 0, 1, 0, 2, 4, 2, 3, 0, 0, 5, 4, 3, 1, 1, 2, 6, 2, 3, 3, 1, 8, 0, 2, 6, 12, 3, 0, 2, 6, 12, 8, 0, 4, 6, 12, 3, 3, 3, 1, 9, 0, 4, 68, 105, 101, 109, 12, 70, 105, 120, 101, 100, 80, 111, 105, 110, 116, 51, 50, 12, 83, 108, 105, 100, 105, 110, 103, 78, 111, 110, 99, 101, 20, 99, 114, 101, 97, 116, 101, 95, 102, 114, 111, 109, 95, 114, 97, 116, 105, 111, 110, 97, 108, 21, 114, 101, 99, 111, 114, 100, 95, 110, 111, 110, 99, 101, 95, 111, 114, 95, 97, 98, 111, 114, 116, 24, 117, 112, 100, 97, 116, 101, 95, 120, 100, 120, 95, 101, 120, 99, 104, 97, 110, 103, 101, 95, 114, 97, 116, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 5, 1, 11, 10, 0, 10, 1, 17, 1, 10, 2, 10, 3, 17, 0, 12, 4, 11, 0, 11, 4, 56, 0, 2};

    private static byte[] UPDATE_MINTING_ABILITY_CODE = {-95, 28, -21, 11, 1, 0, 0, 0, 6, 1, 0, 2, 3, 2, 6, 4, 8, 2, 5, 10, 8, 7, 18, 28, 8, 46, 16, 0, 0, 0, 1, 0, 1, 1, 1, 0, 2, 2, 6, 12, 1, 0, 1, 9, 0, 4, 68, 105, 101, 109, 22, 117, 112, 100, 97, 116, 101, 95, 109, 105, 110, 116, 105, 110, 103, 95, 97, 98, 105, 108, 105, 116, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 4, 11, 0, 10, 1, 56, 0, 2};

    interface TransactionScriptDecodingHelper {
        public ScriptCall decode(Script script);
    }

    private static final java.util.Map<Bytes, TransactionScriptDecodingHelper> TRANSACTION_SCRIPT_DECODER_MAP = initTransactionScriptDecoderMap();

    private static java.util.Map<Bytes, TransactionScriptDecodingHelper> initTransactionScriptDecoderMap() {
        java.util.HashMap<Bytes, TransactionScriptDecodingHelper> map = new java.util.HashMap<>();
        map.put(new Bytes(ADD_CURRENCY_TO_ACCOUNT_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_add_currency_to_account_script(script)));
        map.put(new Bytes(ADD_RECOVERY_ROTATION_CAPABILITY_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_add_recovery_rotation_capability_script(script)));
        map.put(new Bytes(ADD_VALIDATOR_AND_RECONFIGURE_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_add_validator_and_reconfigure_script(script)));
        map.put(new Bytes(BURN_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_burn_script(script)));
        map.put(new Bytes(BURN_TXN_FEES_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_burn_txn_fees_script(script)));
        map.put(new Bytes(CANCEL_BURN_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_cancel_burn_script(script)));
        map.put(new Bytes(CREATE_CHILD_VASP_ACCOUNT_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_create_child_vasp_account_script(script)));
        map.put(new Bytes(CREATE_DESIGNATED_DEALER_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_create_designated_dealer_script(script)));
        map.put(new Bytes(CREATE_PARENT_VASP_ACCOUNT_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_create_parent_vasp_account_script(script)));
        map.put(new Bytes(CREATE_RECOVERY_ADDRESS_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_create_recovery_address_script(script)));
        map.put(new Bytes(CREATE_VALIDATOR_ACCOUNT_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_create_validator_account_script(script)));
        map.put(new Bytes(CREATE_VALIDATOR_OPERATOR_ACCOUNT_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_create_validator_operator_account_script(script)));
        map.put(new Bytes(FREEZE_ACCOUNT_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_freeze_account_script(script)));
        map.put(new Bytes(PEER_TO_PEER_WITH_METADATA_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_peer_to_peer_with_metadata_script(script)));
        map.put(new Bytes(PREBURN_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_preburn_script(script)));
        map.put(new Bytes(PUBLISH_SHARED_ED25519_PUBLIC_KEY_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_publish_shared_ed25519_public_key_script(script)));
        map.put(new Bytes(REGISTER_VALIDATOR_CONFIG_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_register_validator_config_script(script)));
        map.put(new Bytes(REMOVE_VALIDATOR_AND_RECONFIGURE_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_remove_validator_and_reconfigure_script(script)));
        map.put(new Bytes(ROTATE_AUTHENTICATION_KEY_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_rotate_authentication_key_script(script)));
        map.put(new Bytes(ROTATE_AUTHENTICATION_KEY_WITH_NONCE_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_rotate_authentication_key_with_nonce_script(script)));
        map.put(new Bytes(ROTATE_AUTHENTICATION_KEY_WITH_NONCE_ADMIN_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_rotate_authentication_key_with_nonce_admin_script(script)));
        map.put(new Bytes(ROTATE_AUTHENTICATION_KEY_WITH_RECOVERY_ADDRESS_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_rotate_authentication_key_with_recovery_address_script(script)));
        map.put(new Bytes(ROTATE_DUAL_ATTESTATION_INFO_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_rotate_dual_attestation_info_script(script)));
        map.put(new Bytes(ROTATE_SHARED_ED25519_PUBLIC_KEY_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_rotate_shared_ed25519_public_key_script(script)));
        map.put(new Bytes(SET_VALIDATOR_CONFIG_AND_RECONFIGURE_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_set_validator_config_and_reconfigure_script(script)));
        map.put(new Bytes(SET_VALIDATOR_OPERATOR_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_set_validator_operator_script(script)));
        map.put(new Bytes(SET_VALIDATOR_OPERATOR_WITH_NONCE_ADMIN_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_set_validator_operator_with_nonce_admin_script(script)));
        map.put(new Bytes(TIERED_MINT_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_tiered_mint_script(script)));
        map.put(new Bytes(UNFREEZE_ACCOUNT_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_unfreeze_account_script(script)));
        map.put(new Bytes(UPDATE_DIEM_VERSION_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_update_diem_version_script(script)));
        map.put(new Bytes(UPDATE_DUAL_ATTESTATION_LIMIT_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_update_dual_attestation_limit_script(script)));
        map.put(new Bytes(UPDATE_EXCHANGE_RATE_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_update_exchange_rate_script(script)));
        map.put(new Bytes(UPDATE_MINTING_ABILITY_CODE), (TransactionScriptDecodingHelper)((script) -> Helpers.decode_update_minting_ability_script(script)));
        return map;
    }

    interface ScriptFunctionDecodingHelper {
        public ScriptFunctionCall decode(TransactionPayload payload) throws DeserializationError;
    }

    private static final java.util.Map<String, ScriptFunctionDecodingHelper> SCRIPT_FUNCTION_DECODER_MAP = initDecoderMap();

    private static java.util.Map<String, ScriptFunctionDecodingHelper> initDecoderMap() {
        java.util.HashMap<String, ScriptFunctionDecodingHelper> map = new java.util.HashMap<>();
        map.put("AccountAdministrationScriptsadd_currency_to_account", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_add_currency_to_account_script_function(payload)));
        map.put("AccountAdministrationScriptsadd_recovery_rotation_capability", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_add_recovery_rotation_capability_script_function(payload)));
        map.put("ValidatorAdministrationScriptsadd_validator_and_reconfigure", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_add_validator_and_reconfigure_script_function(payload)));
        map.put("TreasuryComplianceScriptsburn_txn_fees", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_burn_txn_fees_script_function(payload)));
        map.put("TreasuryComplianceScriptsburn_with_amount", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_burn_with_amount_script_function(payload)));
        map.put("TreasuryComplianceScriptscancel_burn_with_amount", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_cancel_burn_with_amount_script_function(payload)));
        map.put("AccountCreationScriptscreate_child_vasp_account", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_create_child_vasp_account_script_function(payload)));
        map.put("AccountCreationScriptscreate_designated_dealer", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_create_designated_dealer_script_function(payload)));
        map.put("AccountCreationScriptscreate_parent_vasp_account", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_create_parent_vasp_account_script_function(payload)));
        map.put("AccountAdministrationScriptscreate_recovery_address", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_create_recovery_address_script_function(payload)));
        map.put("AccountCreationScriptscreate_validator_account", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_create_validator_account_script_function(payload)));
        map.put("AccountCreationScriptscreate_validator_operator_account", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_create_validator_operator_account_script_function(payload)));
        map.put("TreasuryComplianceScriptsfreeze_account", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_freeze_account_script_function(payload)));
        map.put("SystemAdministrationScriptsinitialize_diem_consensus_config", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_initialize_diem_consensus_config_script_function(payload)));
        map.put("PaymentScriptspeer_to_peer_with_metadata", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_peer_to_peer_with_metadata_script_function(payload)));
        map.put("TreasuryComplianceScriptspreburn", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_preburn_script_function(payload)));
        map.put("AccountAdministrationScriptspublish_shared_ed25519_public_key", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_publish_shared_ed25519_public_key_script_function(payload)));
        map.put("ValidatorAdministrationScriptsregister_validator_config", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_register_validator_config_script_function(payload)));
        map.put("ValidatorAdministrationScriptsremove_validator_and_reconfigure", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_remove_validator_and_reconfigure_script_function(payload)));
        map.put("AccountAdministrationScriptsrotate_authentication_key", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_rotate_authentication_key_script_function(payload)));
        map.put("AccountAdministrationScriptsrotate_authentication_key_with_nonce", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_rotate_authentication_key_with_nonce_script_function(payload)));
        map.put("AccountAdministrationScriptsrotate_authentication_key_with_nonce_admin", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_rotate_authentication_key_with_nonce_admin_script_function(payload)));
        map.put("AccountAdministrationScriptsrotate_authentication_key_with_recovery_address", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_rotate_authentication_key_with_recovery_address_script_function(payload)));
        map.put("AccountAdministrationScriptsrotate_dual_attestation_info", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_rotate_dual_attestation_info_script_function(payload)));
        map.put("AccountAdministrationScriptsrotate_shared_ed25519_public_key", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_rotate_shared_ed25519_public_key_script_function(payload)));
        map.put("SystemAdministrationScriptsset_gas_constants", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_set_gas_constants_script_function(payload)));
        map.put("ValidatorAdministrationScriptsset_validator_config_and_reconfigure", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_set_validator_config_and_reconfigure_script_function(payload)));
        map.put("ValidatorAdministrationScriptsset_validator_operator", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_set_validator_operator_script_function(payload)));
        map.put("ValidatorAdministrationScriptsset_validator_operator_with_nonce_admin", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_set_validator_operator_with_nonce_admin_script_function(payload)));
        map.put("TreasuryComplianceScriptstiered_mint", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_tiered_mint_script_function(payload)));
        map.put("TreasuryComplianceScriptsunfreeze_account", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_unfreeze_account_script_function(payload)));
        map.put("SystemAdministrationScriptsupdate_diem_consensus_config", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_update_diem_consensus_config_script_function(payload)));
        map.put("SystemAdministrationScriptsupdate_diem_version", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_update_diem_version_script_function(payload)));
        map.put("TreasuryComplianceScriptsupdate_dual_attestation_limit", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_update_dual_attestation_limit_script_function(payload)));
        map.put("TreasuryComplianceScriptsupdate_exchange_rate", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_update_exchange_rate_script_function(payload)));
        map.put("TreasuryComplianceScriptsupdate_minting_ability", (ScriptFunctionDecodingHelper)((payload) -> Helpers.decode_update_minting_ability_script_function(payload)));
        return map;
    }

    private static Bytes encode_bool_argument(Boolean arg) {
        try {

            BcsSerializer s = new BcsSerializer();
            s.serialize_bool(arg);
            return Bytes.valueOf(s.get_bytes());
        
        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type bool");
        }
    }


    private static Bytes encode_u64_argument(@Unsigned Long arg) {
        try {

            BcsSerializer s = new BcsSerializer();
            s.serialize_u64(arg);
            return Bytes.valueOf(s.get_bytes());
        
        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u64");
        }
    }


    private static Bytes encode_address_argument(AccountAddress arg) {
        try {

            return Bytes.valueOf(arg.bcsSerialize());
        
        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type address");
        }
    }


    private static Bytes encode_u8vector_argument(Bytes arg) {
        try {

            BcsSerializer s = new BcsSerializer();
            s.serialize_bytes(arg);
            return Bytes.valueOf(s.get_bytes());
        
        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u8vector");
        }
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

