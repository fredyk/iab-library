package com.txstockdata.iab_library;

import android.content.DialogInterface;
import android.support.annotation.StringRes;

import org.json.JSONObject;

/**
 * © 2017 Jhon Fredy Magdalena Vila
 */
public interface UIErrorHandler {
    void showAlertWarning(String title, String message, String positiveText, DialogInterface.OnClickListener positiveListener, String negativeText, DialogInterface.OnClickListener negativeListener, boolean cancelable);

    void showAlertWarning(@StringRes int title, @StringRes int message, @StringRes int positiveText, DialogInterface.OnClickListener positiveListener, @StringRes int negativeText, DialogInterface.OnClickListener negativeListener, boolean cancelable);

    void showAlertError(String tittle, String message, DialogInterface.OnClickListener button2Listener);

    void showAlertError(String tittle, String message, String button1Text, DialogInterface.OnClickListener button1Listener, DialogInterface.OnClickListener button2Listener);

    void showAlertInfo(String tittle, String message, String button1Text, DialogInterface.OnClickListener button1Listener, String button2Text, DialogInterface.OnClickListener button2Listener);

    void logEventBuyIap();

    void logEventPurchaseSku(String sku);

    void handleError(JSONObject jsonObject);

    void showAlertCorrect(String tittle, String message, DialogInterface.OnClickListener button2Listener);

    void showAlertCorrect(String title, String message);

    void handleError(StockDataException e);

    void handleError(Throwable e);

    void handleError(Throwable e, DialogInterface.OnClickListener onClickListener);

    void alertException(String code, String message, Throwable cause, JSONObject jsonObject, DialogInterface.OnClickListener onClickListener);

    void logException(Throwable e);

    void handleUnknownError(Throwable e);


//    public abstract void alertException(String code, String message, Throwable cause, String option2Text, View.OnClickListener option2Listener);
}
