// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * IntentIdentifier implements Libra Intent Identifier encoding and decoding.
 * @see <a href="https://github.com/libra/lip/blob/master/lips/lip-5.md">LIP-5 Address formatting</a>
 * */
public class IntentIdentifier {

    public static final String LIBRA_SCHEME = "libra";
    public static final String CURRENCY_PARAM_NAME = "c";
    public static final String AMOUNT_PARAM_NAME = "am";

    /**
     * Decode an intent identifier string for the prefix network account identifier.
     *
     * @param prefix           network prefix of the account identifier encoded in intent identifier, throws IllegalArgumentException if it does not match.
     * @param intentIdentifier intent identifier
     * @throws IllegalArgumentException if the account identifier's network prefix does not match given network prefix.
     */
    public static IntentIdentifier decode(AccountIdentifier.NetworkPrefix prefix, String intentIdentifier) {
        URI uri;
        try {
            uri = new URI(intentIdentifier);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        String scheme = uri.getScheme();

        if (!LIBRA_SCHEME.equals(scheme)) {
            throw new IllegalArgumentException(String.format("invalid intent identifier scheme: %s", scheme));
        }

        String query = uri.getQuery();
        String currency = null;
        long amount = 0;
        if (query != null) {
            List<NameValuePair> parse = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
            Map<String, String> collect =
                    parse.stream().collect(Collectors.toMap(NameValuePair::getName,
                            NameValuePair::getValue));

            currency = collect.get(CURRENCY_PARAM_NAME);
            amount = Integer.parseInt(collect.get(AMOUNT_PARAM_NAME));
        }

        AccountIdentifier accountIdentifier = AccountIdentifier.decode(prefix, uri.getHost());
        return new IntentIdentifier(accountIdentifier, currency, amount);
    }

    private final AccountIdentifier accountIdentifier;
    private final String currency;
    private final long amount;

    public IntentIdentifier(AccountIdentifier accountIdentifier, String currency, long amount) {
        this.accountIdentifier = accountIdentifier;
        this.currency = currency;
        this.amount = amount;
    }

    public AccountIdentifier getAccountIdentifier() {
        return accountIdentifier;
    }

    public String getCurrency() {
        return currency;
    }

    public long getAmount() {
        return amount;
    }

    /**
     * Encode intent identifier
     *
     * @return encoded intent identifier string
     */
    public String encode() {
        String encodedAccount = this.accountIdentifier.encodeV1();

        try {
            URI uri = new URI(LIBRA_SCHEME + "://" + encodedAccount);
            URIBuilder uriBuilder = new URIBuilder(uri);

            if (this.amount > 0) {
                uriBuilder.addParameter(AMOUNT_PARAM_NAME, String.valueOf(this.amount));
            }

            if (this.currency != null) {
                uriBuilder.addParameter(CURRENCY_PARAM_NAME, this.currency);
            }

            return uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "IntentIdentifier{" +
                "accountIdentifier=" + accountIdentifier +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntentIdentifier that = (IntentIdentifier) o;
        return amount == that.amount &&
                Objects.equals(accountIdentifier, that.accountIdentifier) &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountIdentifier, currency, amount);
    }
}
