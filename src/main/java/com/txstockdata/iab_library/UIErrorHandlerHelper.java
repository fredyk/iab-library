package com.txstockdata.iab_library;

import android.content.DialogInterface;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * © 2016 Jhon Fredy Magdalena Vila
 */
public interface UIErrorHandlerHelper {
    public abstract void showAlertError(String tittle, String message, DialogInterface.OnClickListener button2Listener);

    public abstract void showAlertError(String tittle, String message, String button1Text, DialogInterface.OnClickListener button1Listener, DialogInterface.OnClickListener button2Listener);

    public abstract void logEventBuyIap();

    public abstract void logEventPurchaseSku(String sku);

    public abstract void handleError(JSONObject jsonObject);

    public abstract void showAlertCorrect(String title, String message);

    public abstract void handleError(StockDataException e);

    public abstract void handleError(Throwable e);

    public abstract void alertException(String code, String message, Throwable cause, JSONObject jsonObject, DialogInterface.OnClickListener onClickListener);

    public abstract void logException(Throwable e);

    public abstract void handleUnknownError(Throwable e);

//    public abstract void alertException(String code, String message, Throwable cause, String option2Text, View.OnClickListener option2Listener);
}
