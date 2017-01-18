package com.javierizquierdovera.miguelmedina.ephemeralcontacts.ephemeralcontacts;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import model.Manager;
import model.Tag;

public class ContactList extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, View.OnLongClickListener{

    private Spinner spinner_tags;
    private RecyclerView list;
    private RecyclerView.Adapter adapter_list;
    private RecyclerView.LayoutManager layout_manager_list;
    private FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactlist);

        setTitle(R.string.contact_list_title);

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
            Manager.getInstancia().loadAll();
        } else {
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
                ((AdapterList)adapter_list).removeSelect();
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
}
