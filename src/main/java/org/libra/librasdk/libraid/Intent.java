package org.libra.librasdk.libraid;


import org.apache.commons.lang3.ObjectUtils;
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

public class Intent {

    private Account account;
    private String currency;
    private int amount;

    private static final String LIBRA_SCHEME = "libra";
    private static final String CURRENCY_PARAM_NAME = "c";
    private static final String AMOUNT_PARAM_NAME = "am";


    public Intent(Account account, String currency, int amount) {
        this.account = account;
        this.currency = currency;
        this.amount = amount;
    }

    public Intent(Account account) {
        this.account = account;
    }

    public static Intent decodeToIntent(Account.NetworkPrefix prefix, String uriString) throws LibraSDKException {
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
                throw new LibraSDKException(String.format("invalid intent scheme: %s", scheme));
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

        Account account = Account.decodeToAccount(prefix, uri.getHost());
        return new Intent(account, currency, amount);
    }

    public String encode() throws LibraSDKException {
        String encodedAccount = this.account.encode();

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
        Intent intent = (Intent) o;
        return amount == intent.amount && Objects.equals(account, intent.account) && Objects.equals(currency, intent.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, currency, amount);
    }

    public boolean isValuesEqual(Intent intent) {
        return (this.amount == intent.amount && ObjectUtils.compare(this.currency,
                intent.currency) == 0 && this.account.isValuesEqual(intent.account));
    }
}
