package com.txstockdata.iab_library;

import org.json.JSONException;

/**
 * © 2017 Jhon Fredy Magdalena Vila
 */
public class SkuDetails {
    private final String mSku;
    private final String mTitle;
    private final String mPrice;

    SkuDetails(com.example.android.trivialdrivesample.util.SkuDetails skuDetails) throws JSONException {
//        new com.example.android.trivialdrivesample.util.SkuDetails(skuDetails.getType(), skuDetails.getJsonSkuDetails());
        mSku = skuDetails.getSku();
        mTitle = skuDetails.getTitle();
        mPrice = skuDetails.getPrice();
    }

    //    @Override
    public String getSku() {
        return mSku;
    }

    //    @Override
    public String getPrice() {
        return mPrice;
    }

    //    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String toString() {
        return "SkuDetails{" +
                "mSku='" + mSku + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mPrice='" + mPrice + '\'' +
                '}';
    }
}
