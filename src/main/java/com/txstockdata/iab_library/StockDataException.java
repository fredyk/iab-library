package com.txstockdata.iab_library;

import android.util.Log;

import org.json.JSONObject;

/**
 * © 2017 Jhon Fredy Magdalena Vila
 */
public abstract class StockDataException extends Throwable {
    private static final String TAG = "StockDataException";

    public StockDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract String getCode();

    @Override
    public void printStackTrace() {
        Log.e(TAG, "StockDataException: " + getCode());

        super.printStackTrace();

    }

    public abstract JSONObject getJsonObject();

    public abstract String getOriginalDescription();
}
