package com.txstockdata.iab_library;

import android.content.Context;
import android.content.SharedPreferences;


import com.jhonfredy.connectionlib.event.BasicEvent;
import com.jhonfredy.connectionlib.event.Event;

import org.json.JSONObject;

/**
 * © 2017 Jhon Fredy Magdalena Vila
 */
interface IapRequiredOps {

    boolean checkGooglePlaySevices();

    SharedPreferences getPrefs();

    void validateToken(String token, Event<JSONObject> basicEvent);

    UIErrorHandler getUIErrorHandler();

    String getStringErrorNoPlayServices();

    void setSubscriptionsAvailable(boolean b);

    void postPurchaseItem(JSONObject data, String signature, BasicEvent<JSONObject> basicEvent) throws StockDataException;

    void postPurchaseSubscription(JSONObject data, String signature, Event<JSONObject> basicEvent) throws StockDataException;

    void logException(Throwable e);

    String getPurchaseTokenFromPrefs();

    boolean isSubscriptionsAvailable();

    void restartApp();

    String getLanguage(Context context);

    void setCustomLanguage();


//    void alertException(String code, String message, JSONException e);
}
