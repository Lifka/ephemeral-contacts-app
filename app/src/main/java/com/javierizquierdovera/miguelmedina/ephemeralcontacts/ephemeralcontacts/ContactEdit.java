package com.javierizquierdovera.miguelmedina.ephemeralcontacts.ephemeralcontacts;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import model.Contact;
import model.DateManager;
import model.Manager;
import model.Tag;

/**
 * Created by lifka on 2/01/17.
 */

public class ContactEdit extends AppCompatActivity implements View.OnClickListener, GestureDetector.OnGestureListener{

    GestureDetector gd;
    private int index = 0;

    private EditText name_edit_text;
    private EditText expiration_edit_text;
    private EditText phone_edit_text;
    private EditText tag_edit_text;
    private TextView info_expiration_text;
    private FloatingActionButton call_button;

    boolean mode_creation = true;

    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactedit);

        index = getIntent().getIntExtra("INDEX", -1);
        mode_creation = (index == -1);

        name_edit_text = (EditText)findViewById(R.id.name);
        expiration_edit_text = (EditText)findViewById(R.id.expiration);
        phone_edit_text = (EditText)findViewById(R.id.phone);
        tag_edit_text = (EditText)findViewById(R.id.tag);
        info_expiration_text = (TextView) findViewById(R.id.expirationtext);

        call_button = (FloatingActionButton)findViewById(R.id.call);



        if (!mode_creation){ // EDITANDO --> Primero se muestra vista no editable
            name_edit_text.setText(Manager.getInstancia().getContacts().get(index).getName());
            info_expiration_text.setText(getResources().getText(R.string.view_expiration_fecha));
            expiration_edit_text.setText(Manager.getInstancia().getContacts().get(index).getExpiration());
            phone_edit_text.setText(Manager.getInstancia().getContacts().get(index).getPhone());
            tag_edit_text.setText("#" + Manager.getInstancia().getContacts().get(index).getTagString()); // Nota: Se añade el #

            editable(false);


        } else { // CREANDO --> Editable directamente

            // No se permite llamar
            call_button.setEnabled(false);
            call_button.setVisibility(View.GONE);

            // El TAG se puede modificar solo en este caso, al crear
            tag_edit_text.setEnabled(true);

            editable(true);
        }


        call_button.setOnClickListener(this);



        // Swipe
        gd = new GestureDetector(this, this);

    }


    public void editable(boolean editable){


        if (editable) {

            if (!mode_creation) {

                // Si se está editando, se muestran los días en lugar de la fecha
                expiration_edit_text.setText(Integer.toString(Manager.getInstancia().getContacts().get(index).getExpirationDays()));
                info_expiration_text.setText(getResources().getText(R.string.view_expiration));
            }

            // Permitir editar;
            name_edit_text.setEnabled(true);
            expiration_edit_text.setEnabled(true);
            phone_edit_text.setEnabled(true);

        } else {

            if (!mode_creation) {

                // ! (Si se está editando, se muestran los días en lugar de la fecha)
                expiration_edit_text.setText((Manager.getInstancia().getContacts().get(index).getExpiration()));
                info_expiration_text.setText(getResources().getText(R.string.view_expiration_fecha));
            }

            // No permitir editar;
            name_edit_text.setEnabled(false);
            expiration_edit_text.setEnabled(false);
            phone_edit_text.setEnabled(false);


            tag_edit_text.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            tag_edit_text.setEnabled(false);

        }



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.call:
                call();
                break;
        }
    }


    public void menuChangeButtons(boolean editando){

        if (editando){
            // Si se está editando no se ve el botón de editar, y si el de guardar
            // botón --> GUARDAR
            menu.findItem(R.id.menu_edit_guardar).setVisible(true);
            menu.findItem(R.id.menu_edit_edit).setVisible(false);
            menu.findItem(R.id.menu_edit_borrar).setVisible(false);

            // Ademñas, el mensaje de volver cambia a "Volver sin guardar"
            menu.findItem(R.id.menu_edit_volver).setTitle(R.string.menu_volver);
        } else {
            // botón --> EDITAR
            menu.findItem(R.id.menu_edit_guardar).setVisible(false);
            menu.findItem(R.id.menu_edit_edit).setVisible(true);
            menu.findItem(R.id.menu_edit_borrar).setVisible(true);

            // ! Además, el mensaje de volver cambia a "Volver sin guardar"
            menu.findItem(R.id.menu_edit_volver).setTitle(R.string.menu_volver_normal);

        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);

        this.menu = menu;

        menuChangeButtons(mode_creation);


        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_edit_guardar:


                if (!checkInputs()){
                    Toast.makeText(this, R.string.error_guardar, Toast.LENGTH_LONG).show();

                } else {
                    guardar();


                    menuChangeButtons(false);

                    // Si se estaba creando, vuelve al menú
                    if (mode_creation){
                        volver();
                    }

                }

                return true;
            case R.id.menu_edit_edit:
                menuChangeButtons(true);
                editable(true);
                return true;
            case R.id.menu_edit_borrar:

                return true;
            case R.id.menu_edit_volver:
                volver();
                return true;
        }

        return false;
    }


    private boolean checkInputs(){
        boolean correct = true;


        String tag_s = tag_edit_text.getText().toString();

        if (tag_s.isEmpty())
            return false;

        int days = Integer.parseInt(expiration_edit_text.getText().toString());

        if (days <= 0)
            return false;

        String name = name_edit_text.getText().toString();
        if (name.isEmpty())
            return false;

        String phone = phone_edit_text.getText().toString();
        if (phone.isEmpty())
            return false;

        return correct;
    }

    private void volver(){
        Intent intent = new Intent(this, ContactList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    private void guardar(){


            String tag_s = tag_edit_text.getText().toString();

            if (tag_s.charAt(0) == '#')
                tag_s = tag_s.substring(1); // Sin #

            Tag tag = new Tag(tag_s);

            int days = Integer.parseInt(expiration_edit_text.getText().toString());

            String name = name_edit_text.getText().toString();
            String phone = phone_edit_text.getText().toString();
            String date = DateManager.getInstancia().getDateExpiration(days);

            Contact contacto = new Contact(tag, name, phone, date, days);

            Manager.getInstancia().addNewContact(contacto);

            Toast.makeText(this, R.string.toast_guardado, Toast.LENGTH_SHORT).show();
            editable(false);
    }

    private void call(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + Manager.getInstancia().getContacts().get(index).getPhone()));
        startActivity(intent);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        float sensibilidad = 50;

        if (motionEvent1.getX() - motionEvent.getX() > sensibilidad){
            guardar();
        }

        return false;
    }
}
