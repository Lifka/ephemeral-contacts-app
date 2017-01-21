package com.javierizquierdovera.miguelmedina.ephemeralcontacts.ephemeralcontacts;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import model.Manager;

/**
 * Created by lifka on 20/01/17.
 */

public class AlertExpirationActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertexpirationactivity);


        /*************/Log.d("[-------DEBUG-------]", "AlertExpirationReceiver: onReceive:Recibida alerta");


        Manager.getInstancia().removeContactsExpired();
        finish();

    }

}
