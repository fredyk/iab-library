package com.txstockdata.iab_library;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;
import com.jhonfredy.connectionlib.event.BasicEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import rx.functions.Action1;

import static com.example.android.trivialdrivesample.util.IabHelper.ITEM_TYPE_INAPP;
import static com.example.android.trivialdrivesample.util.IabHelper.ITEM_TYPE_SUBS;
import static com.example.android.trivialdrivesample.util.IabHelper.RESPONSE_INAPP_PURCHASE_DATA;
import static com.example.android.trivialdrivesample.util.IabHelper.RESPONSE_INAPP_SIGNATURE;
import static com.jhonfredy.connectionlib.mstatic.Util.showBundleInfo;

/**
 * © 2017 Jhon Fredy Magdalena Vila
 */

public class IapHelper {
    private static final String TAG = "IapHelper";

    public static final String BILLING_ERROR = "BILLING_ERROR";
    static final int REQUEST_CODE_BILLING = 1000;
    private static IabResult mResult;

    private IabHelper iabHelper;
    private final IapActivity baseActivity;
    private boolean mSetupDone = false;

    public IapHelper(IapActivity baseActivity, String base64EncodedPublicKeyBilling) {
        this.baseActivity = baseActivity;

        setupIabHelper(base64EncodedPublicKeyBilling);

    }

    public void getPurchases(List<String> skus, rx.functions.Action1<List<SkuDetails>> function) {
        getPurchases(skus, baseActivity, this, iabHelper, function);
    }

    public void validateSubscription(List<String> skus, rx.functions.Action1<String> function) {
        if (function == null) function = IapHelper::self;
        validateSubscription(skus, baseActivity, this, iabHelper, function);
    }

    public void trySubscription(String sku, @NonNull Action1<SkuDetails> success, Action1<Throwable> errorCb) {
        trySubscription(sku, this, iabHelper, baseActivity, success, errorCb);
    }

    public void tryPurchaseItem(IapActivity iapActivity, String sku, IabHelper.OnIabPurchaseFinishedListener listener, Intent data) throws IabHelper.IabAsyncInProgressException {
        doSetupBilling(Collections.singletonList(sku), iapActivity, this, iabHelper, new rx.functions.Action1<Pair<Inventory, List<Purchase>>>() {
            @Override
            public void call(Pair<Inventory, List<Purchase>> result) {
                if (result == null) {
                    Log.e(TAG, "rx.functions.Action1<Pair<Inventory, List<Purchase>>>.call() called with: result = [" + null + "]");
                    listener.onIabPurchaseFinished(null, null, data);
                } else {

                    try {
                        iabHelper.launchPurchaseFlow(iapActivity, sku, REQUEST_CODE_BILLING, new IabHelper.OnIabPurchaseFinishedListener() {
                            @Override
                            public void onIabPurchaseFinished(IabResult result, Purchase info, Intent originalIntent) {
                                try {

                                    if (result.isSuccess()) {

                                        Bundle bundle = originalIntent.getExtras();

                                        JSONObject data = new JSONObject(bundle.getString(RESPONSE_INAPP_PURCHASE_DATA));
                                        String signature = bundle.getString(RESPONSE_INAPP_SIGNATURE);


                                        baseActivity.postPurchaseItem(data, signature, new BasicEvent<JSONObject>() {
                                            @Override
                                            public void onError(JSONObject jsonObject) {
                                                baseActivity.getUIErrorHandler().handleError(jsonObject);
                                                listener.onIabPurchaseFinished(result, null, originalIntent);
                                                super.onError(jsonObject);
                                            }

                                            @Override
                                            public void onSuccess(JSONObject jsonObject) {
                                                try {
                                                    iabHelper.consumeAsync(info, new IabHelper.OnConsumeFinishedListener() {
                                                        @Override
                                                        public void onConsumeFinished(Purchase purchase, IabResult result) {
                                                            Log.i(TAG, "Consumed " + purchase.getOrderId());
                                                            baseActivity.getUIErrorHandler().showAlertCorrect("Correcto", "Suscripción validada correctamente");
                                                            listener.onIabPurchaseFinished(result, info, originalIntent);
                                                        }
                                                    });
                                                } catch (IabHelper.IabAsyncInProgressException e) {
                                                    e.printStackTrace();
                                                    iapActivity.getUIErrorHandler().alertException(BILLING_ERROR, null, e, null, (dialog, which) -> {

                                                        listener.onIabPurchaseFinished(result, info, originalIntent);
                                                    });

                                                }
                                                super.onSuccess(jsonObject);
                                            }

                                            @Override
                                            public void onPostExecute() {
                                                //                                        try {
                                                //                                            function.call(bundle);
                                                //                                        } catch (StockDataException e) {
                                                //                                            e.printStackTrace();
                                                //                                        }
                                                super.onPostExecute();
                                            }
                                        });
                                    } else {
                                        Log.i(TAG, "result: " + result + ", purchase: " + info);
                                    }

                                } catch (StockDataException e) {
                                    e.printStackTrace();
                                    iapActivity.getUIErrorHandler().alertException(e.getCode(), e.getMessage(), e, e.getJsonObject(), null);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    iapActivity.getUIErrorHandler().alertException(null, null, e, null, null);

                                }

                            }
                        }, data);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                        iapActivity.getUIErrorHandler().alertException(null, null, e, null, null);
                    }
                }
            }
        });
    }


    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        handleActivityResult(iabHelper, requestCode, resultCode, data);
    }

    private void setupIabHelper(String base64EncodedPublicKeyBilling) {
        iabHelper = new IabHelper(baseActivity, base64EncodedPublicKeyBilling) {
            @Override
            public void logException(IllegalArgumentException e) {
                baseActivity.getUIErrorHandler().logException(e);
            }
        };

    }


    private static void getPurchases(List<String> skus, IapActivity baseActivity, IapHelper iapHelper, IabHelper iabHelper, rx.functions.Action1<List<SkuDetails>> function) {
        doSetupBilling(skus, baseActivity, iapHelper, iabHelper, pair -> {
            List<SkuDetails> allSkus = new ArrayList<>();
            if (pair != null) {
                Inventory inventory = pair.first;
                for (String sku : skus) {
                    try {
                        Purchase purchase = null;
                        List<Purchase> purchases = pair.second;
                        for (Purchase purchase1 : purchases) {
                            if (purchase1.getSku().equals(sku)) {
                                purchase = purchase1;
                            }
                        }

                        com.example.android.trivialdrivesample.util.SkuDetails skuDetails = inventory.getSkuDetails(sku);
                        if (skuDetails != null) {
                            allSkus.add(new SkuDetails(skuDetails, purchase));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            function.call(allSkus);

        });
    }


    private static Object self(String s) {
        return s;
    }

    private static void validateSubscription(List<String> skus, final IapActivity baseActivity, IapHelper iapHelper, IabHelper iabHelper, @NonNull final rx.functions.Action1<String> function) {

        doSetupBilling(skus, baseActivity, iapHelper, iabHelper, pair -> {

            if (pair == null) {
                function.call(null);
//                    pair = new Pair<>(null, new ArrayList<Purchase>());
            } else {

                String token = baseActivity.getPurchaseTokenFromPrefs();
                if (token != null) {

                    Log.i(TAG, "token\" is not null " + token);

                    baseActivity.validateToken(token, new BasicEvent<JSONObject>() {

                        @Override
                        public void onError(JSONObject jsonObject) {
                            try {
                                checkGoogleReceipt(pair.second, baseActivity, function);
                            } catch (StockDataException e) {
                                e.printStackTrace();
                            }
                            super.onError(jsonObject);
                        }

                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            try {
                                function.call(jsonObject.getString("token"));
                                super.onSuccess(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onError(jsonObject);
                            }
                        }

                        //                    @Override
                        //                    public void onPostExecute() {
                        //
                        //                        try {
                        //                            function.call(null);
                        //                        } catch (StockDataException e) {
                        //                            e.printStackTrace();
                        //                        }
                        //                        super.onPostExecute();
                        //                    }
                    });
                } else {
                    Log.e(TAG, "token\" is null at MainActivityHelper.doSetupBilling");

                    try {
                        checkGoogleReceipt(pair.second, baseActivity, function);
                    } catch (StockDataException e) {
                        e.printStackTrace();
                    }

                }
            }


        });

    }

    private static void doSetupBilling(List<String> skus, final IapActivity baseActivity, IapHelper iapHelper, IabHelper iabHelper, final rx.functions.Action1<Pair<Inventory, List<Purchase>>> function) {

        if (!baseActivity.checkGooglePlaySevices()) return;

        startSetup(iapHelper, iabHelper, result -> {

            if (!result.isSuccess()) {

                Log.e(TAG, "Problem setting up In-app Billing: " + result);
                alertErrorNoPlayServices(baseActivity, function);

            } else {

                try {
                    new Thread(() -> {

                        Looper.prepare();

                        try {
                            iabHelper.queryInventoryAsync(true, null, skus, (result1, inv) -> {
                                Looper looper = Looper.myLooper();

                                if (looper != null) {
                                    Log.i(TAG, String.format(java.util.Locale.getDefault(), "quit loop: %s", looper));
                                    looper.quit();
                                } else {
                                    Log.i(TAG, String.format(java.util.Locale.getDefault(), "null loop %s", "null"));
                                }

                                Log.i(TAG, "onQueryInventoryFinished.result: " + result1.getMessage());
                                if (inv == null) {
                                    Log.e(TAG, "Inventory is null");
                                    baseActivity.setSubscriptionsAvailable(false);
                                    alertErrorNoPlayServices(baseActivity, function);
                                    function.call(null);

                                } else {

                                    List<Purchase> purchases = new ArrayList<>();
                                    for (String sku : skus) {

                                        Log.i(TAG, "getSkuDetails: " + inv.getSkuDetails(sku));

                                        boolean purchased = inv.hasPurchase(sku);
                                        Log.i(TAG, "purchased " + sku + ": " + purchased);

                                        Purchase purchase = inv.getPurchase(sku);
                                        Log.i(TAG, String.format(java.util.Locale.getDefault(), "purchase for sku: %s, %s", sku, purchase));
                                        if (purchase != null) {
                                            int purchaseState = purchase.getPurchaseState();
                                            Log.i(TAG, String.format(java.util.Locale.getDefault(), "purchaseState: %d", purchaseState));
                                            purchases.add(purchase);
                                            String signature = purchase.getSignature();
                                            Log.i(TAG, String.format(java.util.Locale.getDefault(), "IapHelper.doSetupBilling: purchase signature: %s", signature));
                                        }
                                    }

                                    List<Purchase> purchases1 = new ArrayList<>();
                                    for (Purchase purchase : purchases) {
                                        if (purchase.getItemType().equals(ITEM_TYPE_INAPP))
                                            purchases1.add(purchase);
                                    }


                                    if (!purchases1.isEmpty()) {
                                        try {
                                            iabHelper.consumeAsync(purchases1, (purchases2, results) -> {
                                                Log.d(TAG, "onConsumeMultiFinished() called with: purchases2 = [" + purchases2 + "], results = [" + results + "]");

                                                Log.i(TAG, "result message: " + result1.getMessage());

                                                function.call(new Pair<>(inv, purchases));


                                            });
                                        } catch (IabHelper.IabAsyncInProgressException e) {
                                            e.printStackTrace();
                                        }
                                    } else {

                                        Log.i(TAG, "result message: " + result1.getMessage());

                                        function.call(new Pair<>(inv, purchases));


                                    }
                                }

                            });
                        } catch (IabHelper.IabAsyncInProgressException e) {
                            e.printStackTrace();
                            baseActivity.getUIErrorHandler().handleUnknownError(e);

                        }


                        Looper.loop();

                    }).start();

                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    baseActivity.getUIErrorHandler().handleUnknownError(e);
                }


//                    } catch (IabHelper.IabAsyncInProgressException | IllegalArgumentException e) {
//                        e.printStackTrace();
//                        baseActivity.logException(e);
//                    }
            }


//            }
        });

//        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
//
//            @Override
//            public void onIabSetupFinished(IabResult result) {
//
//                if (!result.isSuccess()) {
//
//                    Log.e(TAG, "Problem setting up In-app Billing: " + result);
//                    alertErrorNoPlayServices(baseActivity, function);
//
//                } else {
//
//
////                    try {
//
//                    try {
//                        iabHelper.queryInventoryAsync(true, null, skus, new IabHelper.QueryInventoryFinishedListener() {
//
//                            @Override
//                            public void onQueryInventoryFinished(IabResult result, final Inventory inv) {
//                                Log.i(TAG, "onQueryInventoryFinished.result: " + result.getMessage());
//                                if (inv == null) {
//                                    Log.e(TAG, "Inventory is null");
//                                    baseActivity.setSubscriptionsAvailable(false);
//                                    alertErrorNoPlayServices(baseActivity, function);
//                                    try {
//                                        function.call(null);
//                                    } catch (StockDataException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                } else {
//
//                                    List<Purchase> purchases = new ArrayList<>();
//                                    for (String sku : skus) {
//
//                                        Log.i(TAG, "getSkuDetails: " + inv.getSkuDetails(sku));
//
//                                        boolean purchased = inv.hasPurchase(sku);
//                                        Log.i(TAG, "purchased " + sku + ": " + purchased);
//
//                                        Purchase purchase = inv.getPurchase(sku);
//                                        Log.i(TAG, String.format(java.util.Locale.getDefault(), "purchase for sku: %s, %s", sku, purchase));
//                                        if (purchase != null) {
//                                            int purchaseState = purchase.getPurchaseState();
//                                            Log.i(TAG, String.format(java.util.Locale.getDefault(), "purchaseState: %d", purchaseState));
//                                            purchases.add(purchase);
//                                        }
//                                    }
//
//                                    try {
//
//                                        Log.i(TAG, "result message: " + result.getMessage());
//
//                                        function.call(new Pair<>(inv, purchases));
//
//
//                                    } catch (StockDataException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//
//
//                        });
//
//                    } catch (IllegalStateException | IabHelper.IabAsyncInProgressException e) {
//                        e.printStackTrace();
//                        baseActivity.handleUnknownError(e);
//                    }
//
//
////                    } catch (IabHelper.IabAsyncInProgressException | IllegalArgumentException e) {
////                        e.printStackTrace();
////                        baseActivity.logException(e);
////                    }
//                }
//
//
//            }
//        });
    }

    private static void startSetup(IapHelper iapHelper, IabHelper iabHelper, IabHelper.OnIabSetupFinishedListener listener) {
        if (!iapHelper.mSetupDone) {

            iabHelper.startSetup(result -> {
                iapHelper.mSetupDone = true;
                mResult = result;
                listener.onIabSetupFinished(result);
            });
        } else {
            listener.onIabSetupFinished(mResult);
        }
    }

    private static <T> void alertErrorNoPlayServices(IapActivity baseActivity, rx.functions.Action1<T> function) {
        baseActivity.getUIErrorHandler().showAlertError("Google Play Services", baseActivity.getStringErrorNoPlayServices(), (dialog, which) -> {
            if (function != null)
                function.call(null);
        }, false);

    }

    private static void launchSubscriptionPurchaseFlow(IapHelper iapHelper, IabHelper iabHelper, IapActivity baseActivity, String sku, int requestCodeBuyCash, Action1<Bundle> function, Action1<Throwable> errorCb) throws IabHelper.IabAsyncInProgressException {

        if (!baseActivity.isSubscriptionsAvailable()) {
            alertErrorNoPlayServices(baseActivity, function);
            return;
        }

        startSetup(iapHelper, iabHelper, iabResult -> {
            try {
                iabHelper.launchSubscriptionPurchaseFlow(baseActivity, sku, requestCodeBuyCash, (result, info, originalIntent) -> {
                    Log.i(TAG, "Purchase finished");
                    showBundleInfo(originalIntent.getExtras());
                    try {
                        if (result.isSuccess()) {
                            iabHelper.consumeAsync(info, (purchase, result1) -> Log.i(TAG, "Consumed " + purchase.getOrderId()));
                        } else {
                            Log.i(TAG, "result: " + result + ", purchase: " + info);
                        }
                        if (info != null) {
                            String originalJson = info.getOriginalJson();
                            if (originalJson != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("orignalJson", originalJson);
    //                                                                        mPresenterHelper. logToFirebase(FirebaseEvent.EVENT_BUY_IAP, (me != null) ? (me.getUsername() != null) ? me.getUsername() : "" : "");
    //                            baseActivity .getUIErrorHandler().logEvent(EventFeedback.EVENT_BUY_IAP);
    //                            try {
    //                                if (originalIntent.hasExtra(RESPONSE_INAPP_PURCHASE_DATA)) {
    //                                    JSONObject jsonObject = null;
    //                                    jsonObject = new JSONObject(originalIntent.getStringExtra(RESPONSE_INAPP_PURCHASE_DATA));
    //                                    if (jsonObject.has("orderId")) {
                                baseActivity.getUIErrorHandler().logEventBuyIap();
                                baseActivity.getUIErrorHandler().logEventPurchaseSku(sku);
    //                                    }
    //                                }
    //                            } catch (JSONException e) {
    //                                e.printStackTrace();
    //                                baseActivity.getUIErrorHandler().logEventBuyIap();
    //                                baseActivity.getUIErrorHandler().logEventPurchaseSku(sku);
    //                                baseActivity.getUIErrorHandler().alertException(null, null, e, null, null);
    //                            }

    //                            switch (sku) {
    //                                case SKU:
    //                                    baseActivity.getUIErrorHandler().logEvent(EVENT_PURCHASED_SUBSCRIPTIONS_1_MONTH);
    //                                    break;
    //                                case SKU2:
    //                                    getUIErrorHandler().logEvent(EVENT_PURCHASED_SUBSCRIPTIONS_6_MONTHS);
    //                                    break;
    //                                case SKU3:
    //                                    getUIErrorHandler().logEvent(EVENT_PURCHASED_SUBSCRIPTIONS_1_YEAR);
    ////                                        getUIErrorHandler().logEvent(EVENT_SHOW_SUBSCRIPTIONS_1_YEAR);
    //                                    break;
    //                            }


                                if (function != null)
    //                                                                            function.call(bundle);
                                    function.call(originalIntent.getExtras());
                            }

                        }
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }, null);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
                errorCb.call(e);
            }
        });

    }


    private static void trySubscription(String sku, IapHelper iapHelper, IabHelper iabHelper, IapActivity baseActivity, @NonNull Action1<SkuDetails> success, Action1<Throwable> errorCb) {


        try {

            launchSubscriptionPurchaseFlow(iapHelper, iabHelper, baseActivity, sku, REQUEST_CODE_BILLING, bundle -> {

                if (bundle == null) {
                    baseActivity.getUIErrorHandler().alertException(BILLING_ERROR, null, null, null, null);

                } else {

                    try {
                        JSONObject data = new JSONObject(bundle.getString(RESPONSE_INAPP_PURCHASE_DATA));
                        String signature = bundle.getString(RESPONSE_INAPP_SIGNATURE);

                        baseActivity.postPurchaseSubscription(data, signature, new BasicEvent<JSONObject>() {
                            @Override
                            public void onError(JSONObject jsonObject) {
                                baseActivity.getUIErrorHandler().handleError(jsonObject);
                                super.onError(jsonObject);
                            }

                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                baseActivity.getUIErrorHandler().showAlertCorrect("Correcto", "Suscripción validada correctamente");
                                super.onSuccess(jsonObject);
                            }

                            @Override
                            public void onPostExecute() {
                                try {
                                    success.call(new SkuDetails(new com.example.android.trivialdrivesample.util.SkuDetails(
                                            ITEM_TYPE_SUBS,
                                            data.toString()),
                                            new Purchase(ITEM_TYPE_SUBS, data.toString(), signature)
                                    ));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                super.onPostExecute();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        baseActivity.getUIErrorHandler().alertException(BILLING_ERROR, e.getMessage(), e, null, null);

                    } catch (StockDataException e) {
                        e.printStackTrace();
                    }

                }


            }, errorCb);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
//            baseActivity.getUIErrorHandler().handleError(new TwibexException(Constants.UNKNOWN_ERROR, e.getMessage(), e));
            baseActivity.getUIErrorHandler().handleUnknownError(e);
        }
    }


    private static void handleActivityResult(IabHelper iabHelper, int requestCode, int resultCode, Intent data) {
//        mainActivity.getmMainActivityHelper().getIabHelper().handleActivityResult(requestCode, resultCode, data);
        iabHelper.handleActivityResult(requestCode, resultCode, data);

    }


//    private static void alertErrorNoPlayServices() {
//        getUIErrorHandler().showAlertError("Google Play Services",
//                getBaseActivity().getString(R.string.string_google_play_services_not_available), (dialog, which) -> {
//                    try {
//                        if (function != null)
//                            function.call(null);
//                    } catch (StockDataException e) {
//                        e.printStackTrace();
//                    }
//                });
//
//    }

    static abstract class AsyncQueue<ItemT, FinalT> {

        private List<ItemT> items;
        private int mCurrentItem = 0;
        private FinalT result;
        private rx.functions.Action1<FinalT> function;

        public AsyncQueue(List<ItemT> items, rx.functions.Action1<FinalT> exitCallback) {
            this.function = exitCallback;
            if (items == null) items = new ArrayList<>();
            this.items = items;

        }

        private void dispatchTask() throws StockDataException {
            if (mCurrentItem < items.size()) {
                Log.i(TAG, String.format(java.util.Locale.getDefault(), "dispatch %d", mCurrentItem));
//                                                new DispatchTask(items.get(mCurrentItem), result1 -> {
//                                                    this.result = result1;
//                                                    dispatchTask();
//                                                }).run();
                run(items.get(mCurrentItem), result1 -> {
                    this.result = result1;
                    try {
                        dispatchTask();
                    } catch (StockDataException e) {
                        e.printStackTrace();
                    }
                });
                mCurrentItem++;
            } else {
                Log.i(TAG, String.format(java.util.Locale.getDefault(), "call %s", function));
                function.call(result);
            }

        }

        public void start() throws StockDataException {

            dispatchTask();
        }

        public abstract void run(ItemT t, @NonNull rx.functions.Action1<FinalT> next) throws StockDataException;
    }

    private static <T> void checkGoogleReceipt(List<Purchase> purchases, IapActivity baseActivity, rx.functions.Action1<String> function) throws StockDataException {
//                                    Purchase purchase1 = inv.getPurchase(Constants.SKU);

        new AsyncQueue<Purchase, String>(purchases, function) {
            @Override
            public void run(@NonNull Purchase purchase, @NonNull rx.functions.Action1<String> next) throws StockDataException {

//                                            if (purchase != null) {

                Log.i(TAG, String.format(java.util.Locale.getDefault(), "purchase %s, %s, %s", purchase.getOrderId(), purchase.getSku(), purchase));


                String signature = purchase.getSignature();
                Log.i(TAG, "signature: " + signature);

                String originalJson = purchase.getOriginalJson();
                Log.i(TAG, String.format(java.util.Locale.getDefault(), "originalJson> %s", originalJson));
                try {
                    JSONObject data = new JSONObject(originalJson);

                    baseActivity.postPurchaseSubscription(data, signature, new BasicEvent<JSONObject>() {
                        @Override
                        public void onError(JSONObject jsonObject) {
                            next.call(null);
                            super.onError(jsonObject);
                        }

                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            try {
                                if (function != null)
                                    function.call(jsonObject.getString("token"));
                                super.onSuccess(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onError(jsonObject);
                            }
                        }

                        @Override
                        public void onPostExecute() {
                            super.onPostExecute();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    baseActivity.getUIErrorHandler().alertException(BILLING_ERROR, e.getMessage(), e, null, null);

                }


            }
        }.start();


    }

    public void getSkuDetailsList(List<String> skus, Action1<List<SkuDetails>> cb) {
        // TODO: Revisar, es nueva implementacion

        if (!baseActivity.checkGooglePlaySevices()) return;

        startSetup(this, iabHelper, iabResult -> {

            if (!iabResult.isSuccess()) {

                Log.e(TAG, "Problem setting up In-app Billing: " + iabResult);
                alertErrorNoPlayServices(baseActivity, cb);

            } else {

                new Thread(() -> {

                    Looper.prepare();

                    try {
                        iabHelper.queryInventoryAsync(true, Collections.emptyList(), skus, (result, inv) -> {
                            List<SkuDetails> skuDetailses = new ArrayList<>();
                            for (String sku : skus) {

                                com.example.android.trivialdrivesample.util.SkuDetails skuDetails = inv.getSkuDetails(sku);

                                Purchase purchase = inv.getPurchase(sku);

                                try {
                                    skuDetailses.add(new SkuDetails(skuDetails, purchase));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            cb.call(skuDetailses);
                        });
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                        cb.call(Collections.emptyList());
                    }
                    Looper.loop();

                }).start();
            }
        });


    }

}
