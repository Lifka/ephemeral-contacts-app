package model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by lifka on 30/12/16.
 */

public class DBManager {
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Context context = null;
    private static DBManager instance = new DBManager();

    private DBManager(){
    }

    public static DBManager getInstancia(){
        return instance;
    }

    public void setContext(Context c){
        context = c;
        DBHelper.setContext(context);
        dbHelper = DBHelper.getInstance();
    }

    public boolean isOpen(){
        return context != null;
    }

    // ABRIR DB
    public void open(){
        if (context != null) {
            try {
                db = dbHelper.getWritableDatabase();
            } catch (Exception e) {
                /*************/Log.e("[-------DEBUG-------]", "DBManager ERROR: Error obteniendo DB (open()) --> EX: " + e.getMessage());
            }
        }
    }



    /*********************************************************************************/
    //                                  OPERACIONES
     /*********************************************************************************/



    public long insertContact(Contact contact){

        /*
            1 - Obtener el tag de tags
                1a - Si no existe --> Insertarlo
                1b - Si existe --> Actualizar "count" sumando 1
            2 - Insertar tupla en contacts
                2a - Si falla, habría que deshacer el paso 1
         */

        long result = 0;
        int id_tag;

        /*************/Log.d("[-------DEBUG-------]", "DBHelper insertContact: Comienzo de insercción de contacto " + contact.getName() + " en DB ");


        // 1 - Obtener el tag de tags
        id_tag = tagExists(contact.getTag());
        if (id_tag == 0){
            /*************/Log.d("[-------DEBUG-------]", "DBHelper insertContact: el tag " + contact.getTagString() + " no existe. Insertando... ");
            id_tag = insertTag(contact.getTag());
        } else {
            sumCountTo(contact.getTag());
        }



        //2 - Insertar tupla en contacts

        ContentValues values;
        values = new ContentValues();
        values.put(DBContract.TAG_CON, id_tag);
        values.put(DBContract.NAME_CON, contact.getName());
        values.put(DBContract.PHONE_CON, contact.getPhone());
        values.put(DBContract.DATE_EXPIRATION_CON, contact.getExpiration().toString());
        values.put(DBContract.DATE_EXPIRATION_DAYS_CON, contact.getExpirationDays());
        try {
            result = db.insert(DBContract.TABLE_NAME_CONTACTS, null, values);

            if (result == -1){
                /*************/Log.e("[-------DEBUG-------]", "DBHelper insertContact: No se ha podido insertar en DB \"" + contact.getName() + "\"");
            } else {
                /*************/Log.d("[-------DEBUG-------]", "DBHelper insertContact: insertado en DB \"" + contact.getName() + "\"");
            }


        } catch (Exception e){
            /*************/Log.e("[-------DEBUG-------]", "DBHelper ERROR insertContact: insertado en DB \"" + contact.getName() + "\" ---> " + values.toString() + " --- EX: " + e.getMessage());
            subtractCountTo(contact.getTag());
            result = -1;
        }
        values.clear();

        return result;
    }

    public int getIDContact(Contact contacto){

        int result = -1;

        String selection = DBContract.NAME_CON + "=\"" + contacto.getName() + "\" AND " +
                DBContract.PHONE_CON + "=\"" + contacto.getPhone() + "\"";

        try {
            Cursor c = db.query(DBContract.TABLE_NAME_CONTACTS, new String[]{}, selection, null, null, null, null);

            if (c.moveToNext()){
                result = c.getInt(c.getColumnIndex(DBContract.ID_CON));
            }

        } catch (Exception e){
            /*************/Log.e("[-------DEBUG-------]", "DBManager getIDContact: No es posible obtener el id de "+ contacto.getName() + " --> Ex: " + e.getMessage());
        }


        /*************/Log.d("[-------DEBUG-------]", "DBManager getIDContact: id de "+ contacto.getName() + " = " + result);

        return result;

    }

    public int getCount(Tag tag){
        int result = -1;
        String selection = DBContract.TAG_TAG + "=\"" + tag.getTag() + "\"";

        try {
            Cursor c = db.query(DBContract.TABLE_NAME_TAGS, new String[]{}, selection, null, null, null, null);

            if (c.moveToNext()){
                result = c.getInt(c.getColumnIndex(DBContract.COUNT_TAG));
            }

        } catch (Exception e){
            /*************/Log.e("[-------DEBUG-------]", "DBManager getCount: No es posible obtener los usos del tag "+ tag.getTag() + " en la db --> Ex: " + e.getMessage());
        }


        /*************/Log.d("[-------DEBUG-------]", "DBManager getCount: Count de "+ tag.getTag() + " = " + result);

        return result;
    }


    private long sumCountTo(Tag tag){
        long result = 0;

        int count = getCount(tag) + 1;

        ContentValues values = new ContentValues();
        values.put(DBContract.COUNT_TAG, count);
        /*************/Log.d("[-------DEBUG-------]", "DBManager sumCountTo: actualizando count del tag "+ tag.getTag() + " a " + count);

        try {
            db.update(DBContract.TABLE_NAME_TAGS, values, DBContract.TAG_TAG + "='" + tag.getTag() + "'", null);

        } catch (Exception e){
            /*************/Log.e("[-------DEBUG-------]", "DBManager sumCountTo: No es posible sumar un uso al tag "+ tag.getTag() + " en la db --> Ex: " + e.getMessage());
        }

        return result;
    }


    private long subtractCountTo(Tag tag){
        long result = 0;

        int count = getCount(tag);

        if (count == 1){
            // Eliminar
            try {
                /*************/Log.d("[-------DEBUG-------]", "DBManager subtractCountTo: Eliminando tag "+ tag.getTag());
                db.delete(DBContract.TABLE_NAME_TAGS, DBContract.TAG_TAG + "='" + tag.getTag() + "'", null);

            } catch (Exception e){
                /*************/Log.e("[-------DEBUG-------]", "DBManager subtractCountTo: No es posible eliminar el tag "+ tag.getTag() + " de la db --> Ex: " + e.getMessage());
            }

        } else {
            // Restar
            count -= 1;

            /*************/Log.d("[-------DEBUG-------]", "DBManager subtractCountTo: Restando count a tag "+ tag.getTag());

            ContentValues values = new ContentValues();
            values.put(DBContract.COUNT_TAG, count);

            try {
                db.update(DBContract.TABLE_NAME_TAGS, values, DBContract.TAG_TAG + "='" + tag.getTag() + "'", null);

            } catch (Exception e){
                /*************/Log.e("[-------DEBUG-------]", "DBManager subtractCountTo: No es posible restar un uso al tag "+ tag.getTag() + " en la db --> Ex: " + e.getMessage());
            }

        }

        return result;
    }

    public int insertTag(Tag tag){

        int result = 0;
        int count = 1;

        ContentValues values;
        values = new ContentValues();
        values.put(DBContract.TAG_TAG, tag.getTag());
        values.put(DBContract.COUNT_TAG, count);
        try {
            result = (int)db.insert(DBContract.TABLE_NAME_TAGS, null, values);
            /*************/Log.d("[-------DEBUG-------]", "DBHelper insertTag: insertado en DB \"" + tag.getTag() + "\"");
        } catch (Exception e){
            /*************/Log.e("[-------DEBUG-------]", "DBHelper ERROR insertTag: insertado en DB \"" + tag.getTag() + "\" ---> " + values.toString() + " --- EX: " + e.getMessage());
        }
        values.clear();


        return result;
    }

    public int tagExists(Tag tag){
        int result = 0;
        String selection = DBContract.TAG_TAG + "=\"" + tag.getTag() + "\"";

        try {
            Cursor c = db.query(DBContract.TABLE_NAME_TAGS, new String[]{DBContract.ID_TAG, DBContract.TAG_TAG}, selection, null, null, null, null);

            if (c.moveToNext()){
                result = c.getInt(c.getColumnIndex(DBContract.ID_TAG));
            }

        } catch (Exception e){
            /*************/Log.e("[-------DEBUG-------]", "DBManager tagExists: No es posible comprobar si existe el tag "+ tag.getTag() + " en la db --> Ex: " + e.getMessage());
        }


        /*************/Log.d("[-------DEBUG-------]", "DBHelper tagExists: Buscando tag " + tag.getTag() + " --- Encontrado = " + result);

        return result;
    }

    public int removeContact(Contact contact){
        int result = 0;

        /*
            1 - Eliminiar contacto de contacts
            2 - Actualizar tags restandole 1 a "count"
                2a - Si count = 0 --> Borrar tupla de tags
         */

        /*************/Log.d("[-------DEBUG-------]", "DBHelper removeContact: " + " --- id = " +  contact.getId());
        try {
            db.delete(DBContract.TABLE_NAME_CONTACTS, DBContract.ID_CON + "=" + contact.getId(), null);

        } catch (Exception e){
            result = -1;
            /*************/Log.e("[-------DEBUG-------]", "DBManager removeContact: No es posible eliminar eliminar el contacto "+ contact.getName() + " de la db --> Ex: " + e.getMessage());
        }

        subtractCountTo(contact.getTag());

        return result;
    }

    public int removeContact(int contactd, String tag){
        int result = 0;

        /*
            1 - Eliminiar contacto de contacts
            2 - Actualizar tags restandole 1 a "count"
                2a - Si count = 0 --> Borrar tupla de tags
         */

        /*************/Log.d("[-------DEBUG-------]", "DBHelper removeContact: " + " --- id = " +  contactd);
        try {
            db.delete(DBContract.TABLE_NAME_CONTACTS, DBContract.ID_CON + "=" + contactd, null);

        } catch (Exception e){
            result = -1;
            /*************/Log.e("[-------DEBUG-------]", "DBManager removeContact: No es posible eliminar eliminar el contacto "+ contactd + " de la db --> Ex: " + e.getMessage());
        }

        subtractCountTo(new Tag(tag));

        return result;
    }

    public long updateContact(Contact contact, Contact nuevo){
        long result = 0;

        ContentValues values = new ContentValues();
        values.put(DBContract.NAME_CON, nuevo.getName());
        values.put(DBContract.PHONE_CON, nuevo.getPhone());
        values.put(DBContract.DATE_EXPIRATION_CON, nuevo.getExpiration().toString());
        values.put(DBContract.DATE_EXPIRATION_DAYS_CON, nuevo.getExpirationDays());

        try {
            db.update(DBContract.TABLE_NAME_CONTACTS, values, DBContract.ID_CON + "='" + contact.getId() + "'", null);

        } catch (Exception e){
            /*************/Log.e("[-------DEBUG-------]", "DBManager updateContact: No es posible actualizar el contacto "+ contact.getName() + " --> Ex: " + e.getMessage());
            result = -1;
        }


        return result;
    }

    public ArrayList<Contact> getContacts(){
        ArrayList<Contact> contacts = new ArrayList<>();

        int id;
        Tag tag;
        String name;
        String phone;
        String expiration;
        int expiration_days;

        String query = "SELECT * FROM " + DBContract.TABLE_NAME_CONTACTS
                + " a INNER JOIN " + DBContract.TABLE_NAME_TAGS +
                " b ON a." + DBContract.TAG_CON + "=b." + DBContract.ID_TAG;


        /*************/Log.d("[-------DEBUG-------]", "DBManager getContacts: cargando todos los contactos --> " + query);


        try {
            Cursor c = db.rawQuery(query, new String[]{});


            while (c.moveToNext()) {

                id = c.getInt(c.getColumnIndex(DBContract.ID_CON));

                tag = new Tag(c.getString(c.getColumnIndex(DBContract.TAG_TAG)));
                name = c.getString(c.getColumnIndex(DBContract.NAME_CON));
                phone = c.getString(c.getColumnIndex(DBContract.PHONE_CON));
                expiration = c.getString(c.getColumnIndex(DBContract.DATE_EXPIRATION_CON));
                expiration_days = c.getInt(c.getColumnIndex(DBContract.DATE_EXPIRATION_DAYS_CON));

                Contact contacto = new Contact(tag, name, phone, expiration, expiration_days);
                contacto.setId(id);

                /*************/Log.d("[-------DEBUG-------]", "DBManager getContacts: contacto leído de la DB " + contacto.toString());

                contacts.add(contacto);
            }

        } catch (Exception e){
            /*************/Log.e("[-------DEBUG-------]", "DBManager error getContacts: No es posible obtener los contactos de la db --> Ex: " + e.getMessage() + "  Query --> " + query);
        }

        return contacts;
    }


    public ArrayList<Contact> getContacts(Tag t){
        ArrayList<Contact> contacts = new ArrayList<>();

        int id;
        Tag tag;
        String name;
        String phone;
        String expiration;
        int expiration_days;

        String query = "SELECT * FROM " + DBContract.TABLE_NAME_CONTACTS
                + " a INNER JOIN " + DBContract.TABLE_NAME_TAGS +
                " b ON a." + DBContract.TAG_CON + "=b." + DBContract.ID_TAG +
                " WHERE b." + DBContract.TAG_TAG + "=?";


        /*************/Log.d("[-------DEBUG-------]", "DBManager getContacts: cargando contactos con el tag='" + t  + "' --> " + query);


        try {
            Cursor c = db.rawQuery(query, new String[]{ t.getTag()});

            while (c.moveToNext()) {
                id = c.getInt(c.getColumnIndex(DBContract.ID_CON));
                tag = new Tag(c.getString(c.getColumnIndex(DBContract.TAG_TAG)));
                name = c.getString(c.getColumnIndex(DBContract.NAME_CON));
                phone = c.getString(c.getColumnIndex(DBContract.PHONE_CON));
                expiration = c.getString(c.getColumnIndex(DBContract.DATE_EXPIRATION_CON));
                expiration_days = c.getInt(c.getColumnIndex(DBContract.DATE_EXPIRATION_DAYS_CON));


                Contact contacto = new Contact(tag, name, phone, expiration, expiration_days);
                contacto.setId(id);


                /*************/Log.d("[-------DEBUG-------]", "DBManager getContacts: contacto leído de la DB " + contacto.toString());

                contacts.add(contacto);
            }

        } catch (Exception e){
            /*************/Log.e("[-------DEBUG-------]", "DBManager error getContacts: No es posible obtener los contactos de la db --> Ex: " + e.getMessage() + "  Query --> " + query);
        }

        return contacts;
    }

    public ArrayList<Tag> getTags(){
        ArrayList<Tag> tags = new ArrayList<>();

        int id;
        String tag;

        try {
            Cursor c = db.query(DBContract.TABLE_NAME_TAGS, new String[]{DBContract.ID_TAG, DBContract.TAG_TAG}, null, null, null, null, null);

            while (c.moveToNext()) {
                id = c.getInt(c.getColumnIndex(DBContract.ID_TAG));
                tag = c.getString(c.getColumnIndex(DBContract.TAG_TAG));
                tags.add(new Tag(tag));
            }

        } catch (Exception e){
            /*************/Log.e("[-------DEBUG-------]", "DBManager error getTags: No es posible obtener los tags de la db --> Ex: " + e.getMessage());
        }

        return tags;
    }

}
