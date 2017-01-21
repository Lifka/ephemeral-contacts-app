package com.javierizquierdovera.miguelmedina.ephemeralcontacts.ephemeralcontacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import DB.DBManager;
import model.AndroidContactHelper;
import model.Contact;
import model.DateManager;
import model.Manager;

/**
 * Created by lifka on 21/01/17.
 */

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra("ID", -1);
        String tag = intent.getStringExtra("TAG");
        String phone = intent.getStringExtra("PHONE");

        /*************/Log.d("[-------DEBUG-------]", "AlertExpirationReceiver: onReceive:Recibida alerta " + tag + " - " + id);

        if (!DBManager.getInstancia().isOpen() && id != -1) { // El móvil ha permanecido encendido pero la app se ha cerrado
            DBManager.getInstancia().setContext(context);
            DBManager.getInstancia().open();
            DBManager.getInstancia().removeContact(id, tag);
            AndroidContactHelper.getInstancia().setContext(context);
            AndroidContactHelper.getInstancia().removeContact(phone);
        } else if (!DBManager.getInstancia().isOpen() && id == -1) { // El móvil se ha apagado, hay que revisar todos
            DBManager.getInstancia().setContext(context);
            DBManager.getInstancia().open();
            AndroidContactHelper.getInstancia().setContext(context);

            ArrayList<Contact> contacts = DBManager.getInstancia().getContacts();

            for(int i = 0; i < contacts.size(); i++){
                if (DateManager.getInstancia().isExpired(contacts.get(i).getExpiration())){
                    /*************/Log.e("[-------DEBUG-------]", "Manager: removeContactsExpired: El contacto " + contacts.get(i).getName() + " ha caducado (posición=" + i + ")");
                    DBManager.getInstancia().removeContact(contacts.get(i));
                    AndroidContactHelper.getInstancia().removeContact((contacts.get(i)));
                } else {
                    /*************/Log.d("[-------DEBUG-------]", "Manager: removeContactsExpired: El contacto " + contacts.get(i).getName() + " NO ha caducado --> " + contacts.get(i).getExpiration());
                }
            }

        } else { // La app está abierta
            Manager.getInstancia().removeContactsExpired();
            Toast.makeText(context, R.string.toast_caducado, Toast.LENGTH_SHORT).show();
        }

    }
}
