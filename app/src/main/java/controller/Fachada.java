package controller;

import android.app.AlarmManager;
import android.content.Context;
import android.util.Log;

import com.javierizquierdovera.miguelmedina.ephemeralcontacts.view.AdapterList;
import com.javierizquierdovera.miguelmedina.ephemeralcontacts.view.R;

import java.util.ArrayList;

import model.Contact;
import model.Manager;
import model.Tag;

/**
 * Created by lifka on 1/01/17.
 */
/*

    The view request modifications to the model with this class

 */
public class Fachada {
    private static Fachada instance = new Fachada();

    private Context context;
    private Tag tag_saved = null;
    private int tag_saved_i = -1;
    private AdapterList observer_list = null;

    private Fachada(){
    }

    public static Fachada getInstancia(){
        return instance;
    }

    public void setContext(Context c){
        this.context = c;
        AndroidContactHelper.getInstancia().setContext(c);
    }

    public void load(Context c){
        setContext(c);
        Manager.getInstancia().load(c);
        Manager.getInstancia().setContext(context);
    }


    public void resetTags() {
        Manager.getInstancia().resetTags();
    }

    public void loadTags(){
        resetTags();
        addTagView(new Tag(context.getResources().getString(R.string.spinner_tag1)));
        Manager.getInstancia().loadTags();
    }

    public void loadAll(){
        /*************/Log.d("[-------DEBUG-------]", "---------------------loadAll-------------------");
        tag_saved = null;
        Manager.getInstancia().loadAll();
        removeContactsExpired();
    }

    public void loadByTag(Tag tag){
        /*************/Log.d("[-------DEBUG-------]", "---------------------loadByTag-------------------");
        Manager.getInstancia().loadByTag(tag);
        tag_saved = tag;
        AndroidContactHelper.getInstancia().checkContacts(Manager.getInstancia().getContacts());
        removeContactsExpired();
    }



    public int addNewContact(Contact new_contact){
        /*************/Log.d("[-------DEBUG-------]", "---------------------addNewContact-------------------");
        int result = 0;
        result = AndroidContactHelper.getInstancia().saveContact(new_contact);

        if (result != -1) {
            result = Manager.getInstancia().addNewContact(new_contact);
        }

        if (result != -1){
            AlarmManagerHelper.getInstancia().setContext(context);
            AlarmManagerHelper.getInstancia().addCaducidad(DateManager.getInstancia().getMilisecondsTo(new_contact.getExpiration()),
                    new_contact);
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
        /*************/Log.d("[-------DEBUG-------]", "Fachada: removeContact: borrando " + old.getName() + " id=" + old.getId());


        int result = 0;
        AndroidContactHelper.getInstancia().removeContact(old); // Este error no se tiene en cuenta por seguridad

        if (result != -1){
            result = Manager.getInstancia().removeContact(old);
        }

        if (result != -1) {
            loadTags();
        }

        AndroidContactHelper.getInstancia().checkContacts(Manager.getInstancia().getContacts());

        return result;
    }

    public int contactsSize(){
        return Manager.getInstancia().contactsSize();
    }

    public int editContact(Contact old, Contact contact_changed){
        /*************/Log.d("[-------DEBUG-------]", "---------------------editContact-------------------");
        int result = Manager.getInstancia().editContact(old, contact_changed);

        return result;
    }

    public void addTagView(Tag tag){
        Manager.getInstancia().addTagView(tag);
    }



    public void removeContactsExpired(){
        /*************/Log.d("[-------DEBUG-------]", "Fachada: removeContactsExpired: Comprobando contactos caducaos...");
        for(int i = 0; i < contactsSize(); i++){
            if (DateManager.getInstancia().isExpired(Manager.getInstancia().getContacts().get(i).getExpiration())){
                Manager.getInstancia().removeContact(Manager.getInstancia().getContacts().get(i));
                i--;
                if (observer_list != null) {
                    observer_list.notifyDataSetChanged();
                }
            }
        }
    }

    public int getTagSavedI(){
        return tag_saved_i;
    }

    public void setTagSavedI(int i){
        this.tag_saved_i = i;
    }
    public void setObserverList(AdapterList observer){
        this.observer_list = observer;
    }
}
