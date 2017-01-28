package controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.javierizquierdovera.miguelmedina.ephemeralcontacts.view.R;

import java.util.ArrayList;

import controller.Fachada;
import model.DBManager;
import controller.AndroidContactHelper;
import model.Contact;
import controller.DateManager;

/**
 * Created by lifka on 21/01/17.
 */
/*

    This class receive ntifications from Android alarm
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
                    /*************/Log.e("[-------DEBUG-------]", "Fachada: removeContactsExpired: El contacto " + contacts.get(i).getName() + " ha caducado (posición=" + i + ")");
                    DBManager.getInstancia().removeContact(contacts.get(i));
                    AndroidContactHelper.getInstancia().removeContact((contacts.get(i)));
                } else {
                    /*************/Log.d("[-------DEBUG-------]", "Fachada: removeContactsExpired: El contacto " + contacts.get(i).getName() + " NO ha caducado --> " + contacts.get(i).getExpiration());
                }
            }

        } else { // La app está abierta
            Fachada.getInstancia().removeContactsExpired();
            Toast.makeText(context, R.string.toast_caducado, Toast.LENGTH_SHORT).show();
        }

    }
}
