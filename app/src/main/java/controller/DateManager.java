package controller;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by lifka on 3/01/17.
 */
/*

    This class make transforms and calculations with dates

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

    public boolean isExpired(String date){
        boolean result = false;
        boolean expired = false;

        String date_c_str = getCurrentDate();

        try {
            Date curret_date = dateFormat.parse(date_c_str);
            Date expired_date = dateFormat.parse(date);
            if (curret_date.after(expired_date) ||
                    curret_date.equals(expired_date)){
                expired = true;
            }


            /*************/Log.d("[-------DEBUG-------]", "DateManager: isExpired --> " + expired_date +
                    "? | Hora del sistema --> " + curret_date + " | = " + expired);


            result = expired;
        } catch (Exception e) {

            /*************/Log.e("[-------DEBUG-------]", "DateManager: isExpired EX: " +  e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public long getMilisecondsTo(String date){
        long result = -1;

        try {
            Date date1 = dateFormat.parse(getCurrentDate());
            Date date2 = dateFormat.parse(date);
            result = date2.getTime() - date1.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public long getSecondsTo(String date){
        return getMilisecondsTo(date) / 1000;
    }


}
