package controller;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.javierizquierdovera.miguelmedina.ephemeralcontacts.view.R;

import java.util.ArrayList;

import model.Contact;

/**
 * Created by lifka on 18/01/17.
 */

/*

    This class sends alerts to Android for add and remove contacts from the real list of contacts (We need it for you can use the contacts with WhatsApp, Telegram, etc)

 */

public class AndroidContactHelper {

    private static AndroidContactHelper instance = new AndroidContactHelper();
    private Context context;

    private AndroidContactHelper(){
    }

    public static AndroidContactHelper getInstancia(){
            return instance;
        }

    public void setContext(Context c){
            this.context = c;
        }


    public int saveContact(Contact contact){

        /*************/Log.d("[-------DEBUG-------]", "AndroidContactHelper: saveContact: Creando contacto en Android --> " + contact.getName());
        int result = 0;

        ArrayList <ContentProviderOperation> datos = new ArrayList < ContentProviderOperation > ();

        datos.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Nombre

        String name = getNameForAndroidContacts(contact);
        datos.add(ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        name).build());

        //------------------------------------------------------ Móvil
        datos.add(ContentProviderOperation.
                newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhone())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());


        // Asking the Contact provider to create a new contact
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, datos);
        } catch (Exception e) {

            /*************/Log.e("[-------DEBUG-------]", "AndroidContactHelper: saveContact: error guardando contacto " + e.getMessage());
            result = -1;


        }

        return result;

    }

    // Devuelve el nombre que tendrá en la agenda del móvil
    public String getNameForAndroidContacts(Contact contact){
        return context.getResources().getString(R.string.tag_contacts) + contact.getName()  +
                context.getResources().getString(R.string.separador_contacts) + contact.getTag().getTag();
    }

    // Comprueba si un contacto concreto sigue existiendo en la agenda del móvil
    private boolean isContactAlive(Contact contact) {
        /*************/Log.d("[-------DEBUG-------]", "AndroidContactHelper: isContactAlive: Comprobando si existe en Android --> " + contact.getName());
        boolean result = false;

        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contact.getPhone()));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                result = true;
                /*************/Log.d("[-------DEBUG-------]", "AndroidContactHelper: isContactAlive: " + contact.getName() + " EXISTE");
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return result;
    }

    // Repasa la colección de contactos de la app, y si alguno no existe ya en el móvil, lo vuelve a crear (esta decisión soluciona varios conflictos)
    // Para evitar contactos fantasma
    public void checkContacts(ArrayList<Contact> contacts){
        /*************/Log.d("[-------DEBUG-------]", "---------------------checkContacts-------------------");
        for (int i = 0; i < contacts.size(); i++){
            if (!isContactAlive(contacts.get(i))) {
                /*************/Log.d("[-------DEBUG-------]", "AndroidContactHelper: checkContacts: El contacto " + contacts.get(i).getName() +
                        " ya no existe en Android --> id=" + contacts.get(i).getId() + " posición de la lista=" + i);
               // Fachada.getInstancia().removeContact(contacts.get(i));
                saveContact(contacts.get(i));
            }
        }
    }


    public int removeContact(Contact contact){

        return removeContact(contact.getPhone());
    }



    public int removeContact(String phone){
        int result = -1;

        /*************/Log.e("[-------DEBUG-------]", "AndroidContactHelper: removeContact: Eliminando el contacto " + phone + " de Android");

        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phone));
        Cursor cur = context.getContentResolver().query(contactUri, null, null,
                null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    String lookupKey =
                            cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    Uri uri = Uri.withAppendedPath(
                            ContactsContract.Contacts.CONTENT_LOOKUP_URI,
                            lookupKey);
                    context.getContentResolver().delete(uri, null, null);
                    result = 0;
                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            /*************/Log.e("[-------DEBUG-------]", "AndroidContactHelper: removeContact: El contacto " + phone + " no puede eliminarse de Android --> " + e.getMessage());
            result = -1;
        }

        return result;
    }

}
