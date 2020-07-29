// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.dto;

import java.util.Objects;

public class Currency {
    public String code;
    public long scaling_factor;
    public long fractional_part;
    public float to_lbr_exchange_rate;
    public String mint_events_key;
    public String burn_events_key;
    public String preburn_events_key;
    public String cancel_burn_events_key;
    public String exchange_rate_update_events_key;

    @Override
    public String toString() {
        return "Currency{" +
                "code='" + code + '\'' +
                ", scaling_factor=" + scaling_factor +
                ", fractional_part=" + fractional_part +
                ", to_lbr_exchange_rate=" + to_lbr_exchange_rate +
                ", mint_events_key='" + mint_events_key + '\'' +
                ", burn_events_key='" + burn_events_key + '\'' +
                ", preburn_events_key='" + preburn_events_key + '\'' +
                ", cancel_burn_events_key='" + cancel_burn_events_key + '\'' +
                ", exchange_rate_update_events_key='" + exchange_rate_update_events_key + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return scaling_factor == currency.scaling_factor &&
                fractional_part == currency.fractional_part &&
                Float.compare(currency.to_lbr_exchange_rate, to_lbr_exchange_rate) == 0 &&
                Objects.equals(code, currency.code) &&
                Objects.equals(mint_events_key, currency.mint_events_key) &&
                Objects.equals(burn_events_key, currency.burn_events_key) &&
                Objects.equals(preburn_events_key, currency.preburn_events_key) &&
                Objects.equals(cancel_burn_events_key, currency.cancel_burn_events_key) &&
                Objects.equals(exchange_rate_update_events_key, currency.exchange_rate_update_events_key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, scaling_factor, fractional_part, to_lbr_exchange_rate, mint_events_key, burn_events_key, preburn_events_key, cancel_burn_events_key, exchange_rate_update_events_key);
    }

}
