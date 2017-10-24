package com.txstockdata.iab_library;

import android.support.annotation.NonNull;

import org.json.JSONException;

/**
 * © 2017 Jhon Fredy Magdalena Vila
 */
public class SkuDetails {
    private final String mSku;
    private final String mTitle;
    private final String mPrice;
    private final String mSignature;

    SkuDetails(@NonNull com.example.android.trivialdrivesample.util.SkuDetails skuDetails, String signature) throws JSONException {
//        new com.example.android.trivialdrivesample.util.SkuDetails(skuDetails.getType(), skuDetails.getJsonSkuDetails());
        mSku = skuDetails.getSku();
        mTitle = skuDetails.getTitle();
        mPrice = skuDetails.getPrice();
        mSignature = signature;
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
                ", mSignature='" + mSignature + '\'' +
                '}';
    }
}
