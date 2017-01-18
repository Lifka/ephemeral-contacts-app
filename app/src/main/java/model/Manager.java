package model;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.javierizquierdovera.miguelmedina.ephemeralcontacts.ephemeralcontacts.R;

import java.util.ArrayList;

import DB.DBManager;

/**
 * Created by lifka on 1/01/17.
 */

public class Manager {
    private static Manager instance = new Manager();
    private ArrayList<Contact> contacts = new ArrayList<>();
    private ArrayList<Tag> tags = new ArrayList<>();
    private Context context;
    private Tag tag_saved = null;

    private Manager(){
    }

    public static Manager getInstancia(){
        return instance;
    }

    public void setContext(Context c){
        this.context = c;
    }

    public void load(Context c){
        setContext(c);
        DBManager.getInstancia().setContext(context);
        DBManager.getInstancia().open();
    }


    public void resetTags() {
        tags.clear();
    }

    public void loadTags(){


        resetTags();
        addTagView(new Tag(context.getResources().getString(R.string.spinner_tag1)));
        tags.addAll(DBManager.getInstancia().getTags());
        /*************/Log.d("[-------DEBUG-------]", "Manager: Se han cargado " + tags.size() + " tags.");
    }

    public void loadAll(){
        tag_saved = null;
        contacts.clear();
        contacts.addAll(DBManager.getInstancia().getContacts());
        checkContacts();
        /*************/Log.d("[-------DEBUG-------]", "Manager: Se han cargado " + contacts.size() + " contactos.");
    }

    public void loadByTag(Tag tag){
        tag_saved = tag;
        contacts.clear();
        contacts.addAll(DBManager.getInstancia().getContacts(tag));
        checkContacts();
        /*************/Log.d("[-------DEBUG-------]", "Manager: Se han cargado " + contacts.size() + " contactos.");
    }

    public ArrayList<Contact> getContacts(){
        return contacts;
    }

    public ArrayList<Tag> getTags(){
        return tags;
    }

    public int addNewContact(Contact new_contact){
        int result = 0;
        result = saveOnMobile(new_contact);

        if (result != -1) {
            result = (int)DBManager.getInstancia().insertContact(new_contact);
        }

        if (result != -1){
            contacts.add(new_contact);
        }

        return result;
    }

    public void removeContacts(ArrayList<Contact> olds){
        for(int i = 0; i < olds.size(); i++){
            removeContact(olds.get(i));
        }
    }

    public void removeContact(Contact old){
        /*************/Log.d("[-------DEBUG-------]", "Manager: removeContact: borrando " + old.getName());
        DBManager.getInstancia().removeContact(old);

        contacts.remove(old);

        removeContactFromMobile(old);
        loadTags();
        if (tag_saved != null){
            loadByTag(tag_saved);
        } else {
            loadAll();
        }
    }

    public int contactsSize(){
        return contacts.size();
    }

    public int editContact(Contact contact, Contact contact_changed){
        int result = 0;
        result = (int)DBManager.getInstancia().updateContact(contact, contact_changed);

        if (result != -1) {
            boolean found = false;
            for (int i = 0; i < contactsSize() && !found; i++) {
                if (contact.getName().equals(contacts.get(i).getName()) &&
                        contact.getPhone() == contacts.get(i).getPhone()) {
                    found = true;
                    contacts.set(i, contact_changed);
                }
            }
        }

        return result;
    }

    public void addTagView(Tag tag){
        tags.add(tag);
    }

    public void clearTags(){
        tags.clear();
    }

    private int saveOnMobile(Contact contact){



        int result = 0;

        ArrayList <ContentProviderOperation> datos = new ArrayList < ContentProviderOperation > ();

        datos.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Nombre

        String name = getNameForMobileContacts(contact);
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

            /*************/Log.e("[-------DEBUG-------]", "Manager: error guardando contacto " + e.getMessage());
            result = -1;


        }

        return result;

    }

    // Devuelve el nombre que tendrá en la agenda del móvil
    private String getNameForMobileContacts(Contact contact){
        return context.getResources().getString(R.string.tag_contacts) + contact.getName()  +
                context.getResources().getString(R.string.separador_contacts) + contact.getTag().getTag();
    }

    // Comprueba si un contacto concreto sigue existiendo en la agenda del móvil
    private boolean isContactAlive(Contact contact) {
        boolean result = false;

        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contact.getPhone()));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                result = true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return result;
    }

    // Repasa la colección de contactos de la app, y si alguno no existe ya en el móvil, lo borra de la app
    // Para evitar contactos fantasma
    private void checkContacts(){
        for (int i = 0; i < contacts.size(); i++){
            if (!isContactAlive(contacts.get(i))) {
                /*************/Log.d("[-------DEBUG-------]", "Manager: checkContacts: El contacto " + contacts.get(i).getName() + " ya no existe en el móvil.");
                removeContact(contacts.get(i));
            }
        }
    }

    private void removeContactFromMobile(Contact contact){

    }

}
