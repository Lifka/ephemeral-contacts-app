package com.javierizquierdovera.miguelmedina.ephemeralcontacts.view;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import controller.Fachada;
import model.Contact;
import model.Manager;

/**
 * Created by lifka on 2/01/17.
 */

public class AdapterList extends RecyclerView.Adapter<AdapterList.ContactViewHolder>  {

    View.OnClickListener onclicklistener;
    View.OnLongClickListener onlonkclicklistener;
    private HashMap<Integer, Boolean> index_checked = new HashMap<>();
    private boolean isTagList = false;

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView name_contact;
        private TextView phone_contact;
        private TextView tag_contact;
        private CardView card_tag;
        private CheckBox checkbox;

        public ContactViewHolder(View v) {
            super(v);
            name_contact = (TextView) v.findViewById(R.id.name_contact);
            phone_contact = (TextView) v.findViewById(R.id.phone_contact);
            tag_contact = (TextView) v.findViewById(R.id.tag);
            checkbox = (CheckBox) v.findViewById(R.id.checkbox);
            card_tag = (CardView) v.findViewById(R.id.card_tag);
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


        public void setViewTagContact(String tag){
            tag_contact.setText(tag);
        }

        public void hideTag(boolean b){
            if (b) {
                tag_contact.setVisibility(View.GONE);
                card_tag.setVisibility(View.GONE);
            } else {
                tag_contact.setVisibility(View.VISIBLE);
                card_tag.setVisibility(View.VISIBLE);
            }
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

        v.findViewById(R.id.checkbox).setOnClickListener(onclicklistener);
        v.findViewById(R.id.checkbox).setTag(parent.getChildCount());

        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.setViewNameContact(Manager.getInstancia().getContacts().get(position).getName());
        holder.setViewPhoneContact((Manager.getInstancia().getContacts().get(position).getPhone()));
        holder.setViewTagContact("#" + Manager.getInstancia().getContacts().get(position).getTagString());
        holder.hideTag(isTagList);
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



    public int removeSelect(){

        int result = -1;

        ArrayList<Contact> contacts = new ArrayList<>();
        /*************/Log.d("[-------DEBUG-------]", "AdapterList: removeSelect: total a borrar=" + index_checked.size());

        for (int i : index_checked.keySet()){

            /*************/Log.d("[-------DEBUG-------]", "AdapterList: removeSelect: borrando " +
                    (Manager.getInstancia().getContacts().get(i).getName() + " id=" + (Manager.getInstancia().getContacts().get(i).getId())));
            /*************/Log.d("[-------DEBUG-------]", "AdapterList: removeSelect: posición en la lista: " + i);

            contacts.add(Manager.getInstancia().getContacts().get(i));
            notifyItemRemoved(i);
        }


        index_checked.clear();

        result = Fachada.getInstancia().removeContacts(contacts);


        /*************/Log.d("[-------DEBUG-------]", "AdapterList: removeSelect: deleted");
        notifyDataSetChanged();/***/

        return result;
    }

    public void isTagList(boolean b){
        isTagList = b;
    }

}
