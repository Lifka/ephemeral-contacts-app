package model;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lifka on 3/01/17.
 */

public class DateManager {

    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static DateManager instance = new DateManager();

    private DateManager(){
    }

    public static DateManager getInstancia(){
        return instance;
    }

    public String getDateExpiration(int days){

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        String formattedDate = dateFormat.format(cal.getTime());
        return formattedDate;
    }


}
