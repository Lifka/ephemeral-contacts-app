package model;

import java.util.Date;

/**
 * Created by lifka on 1/01/17.
 */

public class Contact {

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
}
