package controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import model.Contact;

/**
 * Created by lifka on 19/01/17.
 */

public class AlarmManagerHelper {

    private static AlarmManagerHelper instance = new AlarmManagerHelper();
    private Context context;
    AlarmManager alarmManager;

    private AlarmManagerHelper(){
    }

    public static AlarmManagerHelper getInstancia(){
        return instance;
    }

    public void setContext(Context c){
        this.context = c;
    }


    public void addCaducidad(long milisec, Contact contact){


        /*********** TIME ********************/
        /*****DEBUG*********///milisec = 10 * 1000;
        /*************/Log.d("[-------DEBUG-------]", "AlarmManagerHelper: addCaducidad: Añadiendo alerta de " + milisec + " milisegundos = "
                + milisec / 1000 + " segundos = " + milisec / 1000 / 60 + " minutos para " + contact.getName());

        milisec = new GregorianCalendar().getTimeInMillis()+milisec;

        /*** DEBUG: ***/
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String debug_info_time = dateFormat.format(milisec);
        /*************/Log.d("[-------DEBUG-------]", "AlarmManagerHelper: caducidad registrada --> " + debug_info_time +
                " | Hora del sistema --> " + dateFormat.format(new Date()));
        /***********************************/


        /*********** DATA ********************/
        int iphone = Integer.valueOf(contact.getPhone());
        /*************/Log.d("[-------DEBUG-------]", "AlarmManagerHelper: ID --> " + iphone);
        /***********************************/

        /*********** INTENT ********************/
        Intent intent = new Intent(context, Receiver.class);
        intent.putExtra("TAG", contact.getTagString());
        intent.putExtra("ID", contact.getId());
        intent.putExtra("PHONE", contact.getPhone());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                iphone, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        /***********************************/

        /*********** MANEGER ********************/
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC, milisec, pendingIntent);
        /***********************************/


    }



}
