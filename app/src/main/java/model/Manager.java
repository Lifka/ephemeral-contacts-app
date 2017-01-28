package model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import controller.AndroidContactHelper;

/**
 * Created by lifka on 24/01/17.
 */
/*

    This class keep the information of the DB and make requests with DBManager

 */
public class Manager {
    private static Manager instance = new Manager();
    private Context context;

    private ArrayList<Contact> contacts = new ArrayList<>();
    private ArrayList<Tag> tags = new ArrayList<>();


    public static Manager getInstancia(){
        return instance;
    }


    public void setContext(Context c){
        this.context = c;
        AndroidContactHelper.getInstancia().setContext(c);
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

        tags.addAll(DBManager.getInstancia().getTags());
        /*************/Log.d("[-------DEBUG-------]", "Fachada: Se han cargado " + tags.size() + " tags.");
    }

    public void loadAll(){
        /*************/Log.d("[-------DEBUG-------]", "---------------------loadAll-------------------");
        contacts.clear();
        contacts.addAll(DBManager.getInstancia().getContacts());
        AndroidContactHelper.getInstancia().checkContacts(contacts);
        /*************/Log.d("[-------DEBUG-------]", "Fachada: Se han cargado " + contacts.size() + " contactos.");
    }

    public void loadByTag(Tag tag){
        /*************/Log.d("[-------DEBUG-------]", "---------------------loadByTag-------------------");
        contacts.clear();
        contacts.addAll(DBManager.getInstancia().getContacts(tag));
    }

    public ArrayList<Contact> getContacts(){
        return contacts;
    }


    public ArrayList<Tag> getTags(){
        return tags;
    }


    public void clearTags(){
        tags.clear();
    }


    public int addNewContact(Contact new_contact){
        /*************/Log.d("[-------DEBUG-------]", "---------------------addNewContact-------------------");


        /*************/Log.d("[-------DEBUG-------]", "addNewContact: " + new_contact.getName() + "  ---  " + new_contact.getTag().toString());

        int result = (int)DBManager.getInstancia().insertContact(new_contact);
        new_contact.setId(DBManager.getInstancia().getIDContact(new_contact));


        if (result != -1){
            contacts.add(new_contact);
        }

        return result;
    }


    public int removeContact(Contact old){
        /*************/Log.d("[-------DEBUG-------]", "---------------------removeContact-------------------");
        /*************/Log.d("[-------DEBUG-------]", "Fachada: removeContact: borrando " + old.getName() + " id=" + old.getId());


        int result = DBManager.getInstancia().removeContact(old);

        if (result != -1) {
            contacts.remove(old);
        }


        return result;
    }

    public int contactsSize(){
        return contacts.size();
    }


    public int editContact(Contact old, Contact contact_changed){
        /*************/Log.d("[-------DEBUG-------]", "---------------------editContact-------------------");
        int result = (int)DBManager.getInstancia().updateContact(old, contact_changed);

        if (result != -1) {
            boolean found = false;
            for (int i = 0; i < contactsSize() && !found; i++) {
                if (old.getId() == contacts.get(i).getId()) {
                    /*************/Log.d("[-------DEBUG-------]", "Fachada: editContact: sustituyendo contacto " + contacts.get(i).getName() + " id=" + contacts.get(i).getId()
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


}
