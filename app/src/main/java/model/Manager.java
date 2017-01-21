package model;

import android.content.Context;
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
    private int tag_saved_i = -1;

    private Manager(){
    }

    public static Manager getInstancia(){
        return instance;
    }

    public void setContext(Context c){
        this.context = c;
        AndroidContactHelper.getInstancia().setContext(c);
    }

    public void load(Context c){
        setContext(c);
        AlarmManagerHelper.getInstancia().setContext(context);
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
        /*************/Log.d("[-------DEBUG-------]", "---------------------loadAll-------------------");
        tag_saved = null;
        contacts.clear();
        contacts.addAll(DBManager.getInstancia().getContacts());
        AndroidContactHelper.getInstancia().checkContacts(contacts);
        removeContactsExpired();
        /*************/Log.d("[-------DEBUG-------]", "Manager: Se han cargado " + contacts.size() + " contactos.");
    }

    public void loadByTag(Tag tag){
        /*************/Log.d("[-------DEBUG-------]", "---------------------loadByTag-------------------");
        tag_saved = tag;
        contacts.clear();
        contacts.addAll(DBManager.getInstancia().getContacts(tag));
        AndroidContactHelper.getInstancia().checkContacts(contacts);
        removeContactsExpired();
        /*************/Log.d("[-------DEBUG-------]", "Manager: Se han cargado " + contacts.size() + " contactos.");
    }

    public ArrayList<Contact> getContacts(){
        return contacts;
    }

    public ArrayList<Tag> getTags(){
        return tags;
    }

    public int addNewContact(Contact new_contact){
        /*************/Log.d("[-------DEBUG-------]", "---------------------addNewContact-------------------");
        int result = 0;
        result = AndroidContactHelper.getInstancia().saveContact(new_contact);

        if (result != -1) {
            result = (int)DBManager.getInstancia().insertContact(new_contact);
            new_contact.setId(DBManager.getInstancia().getIDContact(new_contact));
        }

        if (result != -1){
            AlarmManagerHelper.getInstancia().addCaducidad(DateManager.getInstancia().getMilisecondsTo(new_contact.getExpiration()),
                    Long.valueOf(new_contact.getPhone()), new_contact.getName());
            contacts.add(new_contact);
        }

        return result;
    }

    public int removeContacts(ArrayList<Contact> olds){
        /*************/Log.d("[-------DEBUG-------]", "---------------------removeContacts-------------------");
        int result = 0;

        for(int i = 0; i < olds.size(); i++){
            if (result != -1)
                result = removeContact(olds.get(i));
        }

        return result;
    }

    public int removeContact(Contact old){
        /*************/Log.d("[-------DEBUG-------]", "---------------------removeContact-------------------");
        /*************/Log.d("[-------DEBUG-------]", "Manager: removeContact: borrando " + old.getName() + " id=" + old.getId());


        int result = 0;
        AndroidContactHelper.getInstancia().removeContact(old); // Este error no se tiene en cuenta por seguridad

        if (result != -1){
            result = DBManager.getInstancia().removeContact(old);
        }

        if (result != -1) {
            contacts.remove(old);

            loadTags();
        }

        return result;
    }

    public int contactsSize(){
        return contacts.size();
    }

    public int editContact(Contact old, Contact contact_changed){
        /*************/Log.d("[-------DEBUG-------]", "---------------------editContact-------------------");
        int result = 0;
        /*************/Log.d("[-------DEBUG-------]", "Manager: editContact: actualizando contacto " + old.getName() + " id=" + old.getId());
        result = (int)DBManager.getInstancia().updateContact(old, contact_changed);

        if (result != -1) {
            boolean found = false;
            for (int i = 0; i < contactsSize() && !found; i++) {
                if (old.getId() == contacts.get(i).getId()) {
                    /*************/Log.d("[-------DEBUG-------]", "Manager: editContact: sustituyendo contacto " + contacts.get(i).getName() + " id=" + contacts.get(i).getId()
                    + " i = " + i );
                    found = true;
                    contacts.set(i, contact_changed);
                }
            }

            AndroidContactHelper.getInstancia().removeContact(old);
            result = AndroidContactHelper.getInstancia().saveContact(contact_changed);

        }

        return result;
    }

    public void addTagView(Tag tag){
        tags.add(tag);
    }

    public void clearTags(){
        tags.clear();
    }


    public void removeContactsExpired(){
        /*************/Log.d("[-------DEBUG-------]", "Manager: removeContactsExpired: Comprobando contactos caducaos...");
        for(int i = 0; i < contacts.size(); i++){
            if (DateManager.getInstancia().isExpired(contacts.get(i).getExpiration())){
                /*************/Log.e("[-------DEBUG-------]", "Manager: removeContactsExpired: El contacto " + contacts.get(i).getName() + " ha caducado (posición=" + i + ")");
                removeContact(contacts.get(i));
                i--;
            } else {
                /*************/Log.d("[-------DEBUG-------]", "Manager: removeContactsExpired: El contacto " + contacts.get(i).getName() + " NO ha caducado --> " + contacts.get(i).getExpiration());
            }
        }
    }

    public int getTagSavedI(){
        return tag_saved_i;
    }

    public void setTagSavedI(int i){
        this.tag_saved_i = i;
    }
}
