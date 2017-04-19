package com.txstockdata.iab_library;

import android.content.SharedPreferences;
import android.os.Bundle;


import com.jhonfredy.connectionlib.event.BasicEvent;
import com.jhonfredy.connectionlib.event.Event;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * © 2016 Jhon Fredy Magdalena Vila
 */
public interface IapRequiredOps {

    boolean checkGooglePlaySevices();

    SharedPreferences getPrefs();

    void validateToken(String token, Event<JSONObject> basicEvent);

    UIErrorHandlerHelper getPresenterUIHelper();

    String getStringErrorNoPlayServices();

    void setSubscriptionsAvailable(boolean b);

    void postPurchaseItem(JSONObject data, String signature, BasicEvent<JSONObject> basicEvent) throws StockDataException;

    void postPurchaseSubscription(JSONObject data, String signature, Event<JSONObject> basicEvent) throws StockDataException;

    void logException(Throwable e);

    String getPurchaseTokenFromPrefs();

    boolean isSubscriptionsAvailable();


//    void alertException(String code, String message, JSONException e);
}
