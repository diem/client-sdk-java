package org.libra.librasdk.libraid;


import okhttp3.HttpUrl;
import org.apache.http.client.utils.URIBuilder;
import org.libra.librasdk.LibraSDKException;

import java.net.URI;
import java.net.URISyntaxException;

public class Intent {

    private Account account;
    private String currency;
    private Integer amount;

    private static final String LIBRA_SCHEME = "libra";
    private static final String CURRENCY_PARAM_NAME = "c";
    private static final String AMOUNT_PARAM_NAME = "am";


    public Intent(Account account, String currency, Integer amount) {
        this.account = account;
        this.currency = currency;
        this.amount = amount;
    }

    public Intent(Account account) {
        this.account = account;
    }

    public Intent decodeToIntent(Account.NetworkPrefix prefix, String uriString) throws LibraSDKException {


        final HttpUrl url = HttpUrl.parse(uriString);

//        libra://lbr1p7ujcndcl7nudzwt8fglhx6wxn08kgs5tm6mz4usw5p72t?c=LBR&am=1000000
        String scheme;
        URI uri;
        try {
            uri = new URI(uriString);
            scheme = uri.getScheme();
            final String currency = url.queryParameter(CURRENCY_PARAM_NAME);

            // FIXME: check in test amount = 0
            final Integer amount = Integer.parseInt(url.queryParameter(AMOUNT_PARAM_NAME));

            if (currency == null || amount == null || !LIBRA_SCHEME.equals(scheme)) {
                throw new LibraSDKException("");
            }
        } catch (URISyntaxException e) {
            throw new LibraSDKException(e);
        }

        // lbr1p7ujcndcl7nudzwt8fglhx6wxn08kgs5tm6mz4usw5p72t
        Account account =
                Account.decodeToAccount(prefix,
                        uri.getScheme() + uri.getAuthority() + uri.getPath());


        return new Intent(account, currency, amount);
    }

    public String encode() throws LibraSDKException {
        String encodedAccount = this.account.encode();

        try {
            URI uri = new URI(LIBRA_SCHEME + "://" + encodedAccount);
            URIBuilder uriBuilder = new URIBuilder(uri);

            if (this.amount != null) {
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
}
