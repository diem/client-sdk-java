package org.libra.librasdk.libraid;


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

    public static Intent decodeToIntent(Account.NetworkPrefix prefix, String uriString) throws LibraSDKException {

        String scheme;
        URI uri;
        String currency;
        Integer amount;




        try {
            uri = new URI(uriString);
            scheme = uri.getScheme();
//            currency = url.queryParameter(CURRENCY_PARAM_NAME);
            currency = uri.getQuery();


//            amount = Integer.parseInt(url.queryParameter(AMOUNT_PARAM_NAME));
            amount = Integer.parseInt(uri.getQuery());

            if (currency == null || amount == null || !LIBRA_SCHEME.equals(scheme)) {
                throw new LibraSDKException("");
            }
        } catch (URISyntaxException e) {
            throw new LibraSDKException(e);
        }

        Account account = Account.decodeToAccount(prefix,
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
