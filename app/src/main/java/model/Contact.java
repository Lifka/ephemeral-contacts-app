package model;

import java.util.Date;

/**
 * Created by lifka on 1/01/17.
 */

public class Contact {

    private Tag tag;
    private String name;
    private int phone;
    private Date expiration;

    public Contact(Tag tag, String name, int phone, Date expiration){
        this.tag = tag;
        this.name = name;
        this.phone = phone;
        this.expiration = expiration;
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

    public int getPhone(){
        return phone;
    }

    public Date getExpiration(){
        return expiration;
    }
}
