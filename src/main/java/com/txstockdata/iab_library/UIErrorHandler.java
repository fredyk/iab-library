package com.txstockdata.iab_library;

import android.content.DialogInterface;

import org.json.JSONObject;

import androidx.annotation.StringRes;

/**
 * © 2017 Jhon Fredy Magdalena Vila
 */
public interface UIErrorHandler {
    void showAlertWarning(String title, String message, String positiveText, DialogInterface.OnClickListener positiveListener, String negativeText, DialogInterface.OnClickListener negativeListener, boolean cancelable);

    void showAlertWarning(@StringRes int title, @StringRes int message, @StringRes int positiveText, DialogInterface.OnClickListener positiveListener, @StringRes int negativeText, DialogInterface.OnClickListener negativeListener, boolean cancelable);

    void showAlertError(String title, String message, DialogInterface.OnClickListener positiveListener, boolean cancelable);

    void showAlertError(String tittle, String message, String positiveText, DialogInterface.OnClickListener button1Listener, DialogInterface.OnClickListener button2Listener, boolean cancelable);

    void showAlertInfo(String tittle, String message, String positiveText, DialogInterface.OnClickListener positiveListener, String negativeText, DialogInterface.OnClickListener negativeListener, boolean cancelable);

    void logEventBuyIap();

    void logEventPurchaseSku(String sku);

    void handleError(JSONObject jsonObject);

    void showAlertCorrect(String tittle, String message, DialogInterface.OnClickListener button2Listener);

    void showAlertCorrect(String title, String message);

    void handleError(StockDataException e);

    void alertException(String code, String message, Throwable cause, JSONObject jsonObject, DialogInterface.OnClickListener onClickListener);

    void logException(Throwable e);

    void handleUnknownError(Throwable e);

    void showAlertCorrect(@StringRes int title, @StringRes int message);

    void showAlertError(@StringRes int title, @StringRes int message, @StringRes int positiveText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener, boolean cancelable);

    void logToFirebase(String tag, String s);

}
