package model;

import android.content.Context;
import android.util.Log;

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

    public void loadAll(){
        contacts.addAll(DBManager.getInstancia().getContacts());
        /*************/Log.d("[-------DEBUG-------]", "Manager: Se han cargado " + contacts.size() + " contactos.");
        tags.addAll(DBManager.getInstancia().getTags());
        /*************/Log.d("[-------DEBUG-------]", "Manager: Se han cargado " + tags.size() + " tags.");
    }

    public void loadByTag(Tag tag){
        tag_saved = tag;
        contacts.clear();
        contacts.addAll(DBManager.getInstancia().getContacts(tag));
        /*************/Log.d("[-------DEBUG-------]", "Manager: Se han cargado " + contacts.size() + " contactos.");
    }

    public ArrayList<Contact> getContacts(){
        return contacts;
    }

    public ArrayList<Tag> getTags(){
        return tags;
    }

    public void addNewContact(Contact new_contact){
        DBManager.getInstancia().insertContact(new_contact);
        contacts.add(new_contact);
    }

    public void removeContacts(ArrayList<Contact> olds){
        for(int i = 0; i < olds.size(); i++){
            removeContact(olds.get(i));
        }

        if (tag_saved != null){
            loadByTag(tag_saved);
        } else {
            loadAll();
        }
    }

    public void removeContact(Contact old){
        DBManager.getInstancia().removeContact(old);
        contacts.remove(old);

        if (tag_saved != null){
            loadByTag(tag_saved);
        } else {
            loadAll();
        }
    }

    public int contactsSize(){
        return contacts.size();
    }

    public void editContact(Contact contact, Contact contact_changed){
        DBManager.getInstancia().updateContact(contact, contact_changed);

        boolean found = false;
        for (int i = 0; i < contactsSize() && !found; i++){
            if (contact.getName().equals(contacts.get(i).getName()) &&
                    contact.getPhone() == contacts.get(i).getPhone()){
                found = true;
                contacts.set(i, contact_changed);
            }
        }
    }

    public void addTagView(Tag tag){
        tags.add(tag);
    }

    public void clearTags(){
        tags.clear();
    }
}
