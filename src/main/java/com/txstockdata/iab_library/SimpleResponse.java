package com.txstockdata.iab_library;

import org.json.JSONObject;

/**
 * © 2016 Jhon Fredy Magdalena Vila
 */
public interface SimpleResponse {

    int getCode();

    String getMessage();

    JSONObject getJsonObject();

}
