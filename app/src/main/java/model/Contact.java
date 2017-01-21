package model;

import android.util.Log;

import java.util.Date;

/**
 * Created by lifka on 1/01/17.
 */

public class Contact {

    private int id = -1;
    private Tag tag;
    private String name;
    private String phone;
    private String expiration;
    private int days_expiration;

    public Contact(Tag tag, String name, String phone, String expiration, int days_expiration){
        this.tag = tag;
        this.name = name;
        this.phone = phone;
        this.expiration = expiration;
        this.days_expiration = days_expiration;

    }

    public Tag getTag(){
        return tag;
    }

    public String getTagString(){
        return tag.getTag();
    }

    public String getName(){
        return name;
    }

    public String getPhone(){
        return phone;
    }

    public String getExpiration(){
        return expiration;
    }

    public int getExpirationDays(){
        return days_expiration;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){

        if (id == -1){
            /*************/Log.e("[-------DEBUG-------]", "Contact: error: se solicita id no asignada-");
        }

        return id;
    }

    public String toString(){
        return "ID=" + id +
                " | tag=" + tag +
                " | name=" + name +
                " | phone=" + phone +
                " | expiration=" + expiration +
                " | days_expiration=" + days_expiration;
    }
}
