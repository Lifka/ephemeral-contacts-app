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
        contacts.addAll(DBManager.getInstancia().getContacts());
        /*************/Log.d("[-------DEBUG-------]", "Manager: Se han cargado " + contacts.size() + " contactos.");
        tags.addAll(DBManager.getInstancia().getTags());
        /*************/Log.d("[-------DEBUG-------]", "Manager: Se han cargado " + tags.size() + " contactos.");
    }

    public ArrayList<Contact> getContacts(){
        return contacts;
    }

    public ArrayList<Tag> getTags(){
        return tags;
    }


}
