package model;

import android.provider.BaseColumns;

/**
 * Created by lifka on 30/12/16.
 */
/*

    This class save the names for create the DB

 */
public class DBContract implements BaseColumns {
    public static final String TABLE_NAME_CONTACTS = "contacts";

    public static final String ID_CON = "id_con";
    public static final String TAG_CON = "tag";
    public static final String NAME_CON = "name";
    public static final String PHONE_CON = "phone";
    public static final String DATE_REC_CON = "date_rec";
    public static final String DATE_EXPIRATION_CON = "date_expiration";
    public static final String DATE_EXPIRATION_DAYS_CON = "date_expiration_days";


    //-----------------------------------------------------------

    public static final String TABLE_NAME_TAGS = "tags";

    public static final String ID_TAG = "id";
    public static final String TAG_TAG = "tag";
    public static final String COUNT_TAG = "count";
}