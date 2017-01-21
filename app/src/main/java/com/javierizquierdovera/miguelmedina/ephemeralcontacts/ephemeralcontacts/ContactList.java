package com.javierizquierdovera.miguelmedina.ephemeralcontacts.ephemeralcontacts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import model.Manager;
import model.Tag;

public class ContactList extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, View.OnLongClickListener, GestureDetector.OnGestureListener{

    GestureDetector gd;
    private Spinner spinner_tags;
    private RecyclerView list;
    private RecyclerView.Adapter adapter_list;
    private RecyclerView.LayoutManager layout_manager_list;
    private FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactlist);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_icon_ec);

        setTitle(R.string.contact_list_title);

        // Permisos
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CALL_PHONE},
                1);



        spinner_tags = (Spinner)findViewById(R.id.menu_lista_spinner_tags);


        // Lista -------------

        list = (RecyclerView) findViewById(R.id.lista);
        add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(this);

        // Obtener el Recycler
        list = (RecyclerView) findViewById(R.id.lista);
        list.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        layout_manager_list = new LinearLayoutManager(this);
        list.setLayoutManager(layout_manager_list);

        // Crear un nuevo adaptador
        adapter_list = new AdapterList(this, this);
        list.setAdapter(adapter_list);

        // --------------------

        // Swipe
        gd = new GestureDetector(this, this);


    }


    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.lista, menu);

        Manager.getInstancia().load(this);
        Manager.getInstancia().loadTags();

        // Spinner -----------
        MenuItem item = menu.findItem(R.id.menu_lista_spinner_tags);
        spinner_tags = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<Tag> adapter_spinner = new SpinAdapterTag(this,
                android.R.layout.simple_spinner_item, Manager.getInstancia().getTags());

        spinner_tags.setOnItemSelectedListener(this);

        spinner_tags.setAdapter(adapter_spinner);
        //----------------------

        return true;
    }


    // Spinner ------------------------------------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        /*************/Log.d("[-------DEBUG-------]", "ContactList: Seleccionado Tag --> " + Manager.getInstancia().getTags().get(i));

        if (i == 0){
            ((AdapterList)adapter_list).isTagList(false);
            Manager.getInstancia().loadAll();
        } else {
            ((AdapterList)adapter_list).isTagList(true);
            Manager.getInstancia().loadByTag(Manager.getInstancia().getTags().get(i/* - 1*/));
        }

        adapter_list.notifyDataSetChanged();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    // ---------------------------------------------------------------------------------


    @Override
    public void onClick(View view) {

        int index;

        switch (view.getId()){
            case R.id.checkbox:
                index = (int)view.getTag();
                /*************/Log.d("[-------DEBUG-------]", "ContactList: OnClick: Seleccionado " + index);
                CheckBox checkbox = (CheckBox)view.findViewById(R.id.checkbox);

                boolean checked  = checkbox.isChecked();

                if (checked){
                    ((AdapterList)adapter_list).getIndexChecked().put(index, true);
                } else {
                    ((AdapterList)adapter_list).getIndexChecked().remove(index);
                }

                break;
            case R.id.add:
                editar(-1);

                break;
            default:
                index = list.getChildLayoutPosition(view);

                editar(index);

                break;
        }

    }

    @Override
    public boolean onLongClick(View view) {

        int index = list.getChildLayoutPosition(view);

        switch (view.getId()){

            default:
                // Abrir marcador
                call(index);
                return true;

        }


    }



    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_lista_borrar:

                /*************/Log.d("[-------DEBUG-------]", "ContactList: Menu: deleting...");

                if (((AdapterList)adapter_list).removeSelect() != -1){
                    Toast.makeText(this,R.string.borrado,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,R.string.borrado_error,Toast.LENGTH_SHORT).show();
                }

                return true;
        }


        return false;

    }


    private void editar(int i){
        Intent intent = new Intent(this, ContactView.class);
        intent.putExtra("INDEX", i);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

    }

    private void call(int index){

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + Manager.getInstancia().getContacts().get(index).getPhone()));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied
                    Toast.makeText(this, R.string.toast_permission_denied, Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }

        }
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

        if (motionEvent.getX() - motionEvent1.getX() > sensibilidad){
            editar(-1);
        }


        return true;
    }


}
