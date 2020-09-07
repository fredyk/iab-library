package com.txstockdata.iab_library;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.trivialdrivesample.util.Purchase;

import org.json.JSONException;

import androidx.annotation.NonNull;

/**
 * © 2017 Jhon Fredy Magdalena Vila
 */
public class SkuDetails implements Parcelable {
    private final String mSku;
    private final String mTitle;
    private final String mPrice;
    private final String subscriptionPeriod;
    private Purchase mPurchase;

    public SkuDetails(@NonNull com.example.android.trivialdrivesample.util.SkuDetails skuDetails, Purchase purchase) throws JSONException {
//        new com.example.android.trivialdrivesample.util.SkuDetails(skuDetails.getType(), skuDetails.getJsonSkuDetails());
        mSku = skuDetails.getSku();
        mTitle = skuDetails.getTitle();
        mPrice = skuDetails.getPrice();
        subscriptionPeriod = skuDetails.getSubscriptionPeriod();
        mPurchase = purchase;
    }

    protected SkuDetails(Parcel in) {
        mSku = in.readString();
        mTitle = in.readString();
        mPrice = in.readString();
        mPurchase = in.readParcelable(Purchase.class.getClassLoader());
        subscriptionPeriod = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSku);
        dest.writeString(mTitle);
        dest.writeString(mPrice);
        dest.writeParcelable(mPurchase, flags);
        dest.writeString(subscriptionPeriod);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SkuDetails> CREATOR = new Creator<SkuDetails>() {
        @Override
        public SkuDetails createFromParcel(Parcel in) {
            return new SkuDetails(in);
        }

        @Override
        public SkuDetails[] newArray(int size) {
            return new SkuDetails[size];
        }
    };

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

    public Purchase getPurchase() {
        return mPurchase;
    }

    @Override
    public String toString() {
        return "SkuDetails{" +
                "mSku='" + mSku + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mPrice='" + mPrice + '\'' +
                ", mPurchase='" + mPurchase + '\'' +
                '}';
    }

    public String getSubscriptionPeriod() {
        return subscriptionPeriod;
    }

    public void setPurchase(Purchase purchase) {
        this.mPurchase = purchase;
    }
}
