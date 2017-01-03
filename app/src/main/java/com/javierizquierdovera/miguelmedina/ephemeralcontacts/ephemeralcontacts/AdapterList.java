package com.javierizquierdovera.miguelmedina.ephemeralcontacts.ephemeralcontacts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import model.Contact;
import model.Manager;

/**
 * Created by lifka on 2/01/17.
 */

public class AdapterList extends RecyclerView.Adapter<AdapterList.ContactViewHolder>  {

    View.OnClickListener onclicklistener;
    View.OnLongClickListener onlonkclicklistener;
    private HashMap<Integer, Boolean> index_checked = new HashMap<>();

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView name_contact;
        private TextView phone_contact;
        private CheckBox checkbox;

        public ContactViewHolder(View v) {
            super(v);
            name_contact = (TextView) v.findViewById(R.id.name_contact);
            phone_contact = (TextView) v.findViewById(R.id.phone_contact);
            checkbox = (CheckBox) v.findViewById(R.id.checkbox);
        }

        public String getNameContact(){
            return name_contact.getText().toString();
        }

        public TextView getViewNameContact(){
            return name_contact;
        }

        public void setViewNameContact(String name){
            name_contact.setText(name);
        }

        public String getPhoneContact(){
            return phone_contact.getText().toString();
        }

        public TextView getViewPhoneContact(){
            return phone_contact;
        }

        public void setViewPhoneContact(String phone){
            phone_contact.setText(phone);
        }

        public boolean isChecked(){
            return checkbox.isChecked();
        }

        public void setChecked(boolean checked){
            checkbox.setChecked(checked);
        }
    }


    public AdapterList(View.OnClickListener onclicklistener, View.OnLongClickListener onlonkclicklistener) {
        this.onclicklistener = onclicklistener;
        this.onlonkclicklistener = onlonkclicklistener;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact, parent, false);
        v.setOnClickListener(onclicklistener);
        v.setOnLongClickListener(onlonkclicklistener);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.setViewNameContact(Manager.getInstancia().getContacts().get(position).getName());
        holder.setViewPhoneContact((Manager.getInstancia().getContacts().get(position).getPhone()));
        boolean checked = false;

        if (index_checked.containsKey(position)){
            checked = true;
        }
        holder.checkbox.setChecked(checked);

    }

    @Override
    public int getItemCount() {
        return Manager.getInstancia().getContacts().size();
    }

    public HashMap<Integer, Boolean> getIndexChecked(){
        return index_checked;
    }



    public void removeSelect(){

        ArrayList<Contact> contacts = new ArrayList<>();

        for (int i : index_checked.keySet()){
            contacts.add(Manager.getInstancia().getContacts().get(i));
            notifyItemRemoved(i);
        }

        index_checked.clear();
        Manager.getInstancia().removeContacts(contacts);
    }

}
