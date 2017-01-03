package model;

/**
 * Created by lifka on 1/01/17.
 */

public class Tag {
    private String tag;

    public Tag(String tag){
        this.tag = tag;
    }

    public String getTag(){
        return tag;
    }



    @Override
    public String toString() {
        return getTag();
    }

}
