package model;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    public String getCurrentDate(){
        Date date = new Date();
        return dateFormat.format(date);
    }

    public int getDaysTo(String final_date){

        int days = -1;

        try {
            Date date1 = dateFormat.parse(getCurrentDate());
            Date date2 = dateFormat.parse(final_date);
            long diff = date2.getTime() - date1.getTime();
            days = (int)TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return days+1;
    }


}
