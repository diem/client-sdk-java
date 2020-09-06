package org.libra;


import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.libra.librasdk.LibraSDKException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class IntentIdentifier {

    private final AccountIdentifier accountIdentifier;
    private String currency;
    private int amount;

    private static final String LIBRA_SCHEME = "libra";
    private static final String CURRENCY_PARAM_NAME = "c";
    private static final String AMOUNT_PARAM_NAME = "am";


    public IntentIdentifier(AccountIdentifier accountIdentifier, String currency, int amount) {
        this.accountIdentifier = accountIdentifier;
        this.currency = currency;
        this.amount = amount;
    }

    public IntentIdentifier(AccountIdentifier accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    public static IntentIdentifier decodeToIntent(AccountIdentifier.NetworkPrefix prefix, String uriString) throws LibraSDKException {
        String scheme;
        URI uri;
        String query;
        String currency = null;
        int amount = 0;

        try {
            uri = new URI(uriString);
            scheme = uri.getScheme();
            query = uri.getQuery();

            if (!LIBRA_SCHEME.equals(scheme)) {
                throw new LibraSDKException(String.format("invalid intentIdentifier scheme: %s", scheme));
            }

            if (query != null) {
                List<NameValuePair> parse = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
                Map<String, String> collect =
                        parse.stream().collect(Collectors.toMap(NameValuePair::getName,
                                NameValuePair::getValue));

                currency = collect.get(CURRENCY_PARAM_NAME);
                amount = Integer.parseInt(collect.get(AMOUNT_PARAM_NAME));
            }
        } catch (URISyntaxException e) {
            throw new LibraSDKException(e);
        }

        AccountIdentifier accountIdentifier = AccountIdentifier.decodeToAccount(prefix, uri.getHost());
        return new IntentIdentifier(accountIdentifier, currency, amount);
    }

    public String encode() throws LibraSDKException {
        String encodedAccount = this.accountIdentifier.encode();

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
            throw new LibraSDKException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntentIdentifier intentIdentifier = (IntentIdentifier) o;
        return amount == intentIdentifier.amount && Objects.equals(
                accountIdentifier, intentIdentifier.accountIdentifier) && Objects.equals(currency, intentIdentifier.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountIdentifier, currency, amount);
    }
}
