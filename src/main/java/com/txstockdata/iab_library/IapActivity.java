package com.txstockdata.iab_library;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import static com.jhonfredy.connectionlib.mstatic.Util.showBundleInfo;
import static com.txstockdata.iab_library.IapHelper.REQUEST_CODE_BILLING;

/**
 * © 2016 Jhon Fredy Magdalena Vila
 */

public abstract class IapActivity extends AppCompatActivity
        implements IapRequiredOps {
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CODE_BILLING:
                if (data != null) {
                    showBundleInfo(data.getExtras());
                    getIapHelper().handleActivityResult(requestCode, resultCode, data);
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public abstract IapHelper getIapHelper();

    ;
}
