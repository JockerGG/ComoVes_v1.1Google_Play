package com.eduardoapps.comoves;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MisLecturasActivity extends AppCompatActivity {

    public static List<Revista> revistas_agregadas = MainActivity.revistas_agregadas;
    public static List<String> lista_titulos = MainActivity.lista_titulos;

    private AdaptadorMisLecturas adaptadorMisLecturas;
    private RecyclerView recycler_lecturas;
    public static String texto = "";
    boolean pase = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_lecturas);
        pase = conectadoWifi();
            Agregar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recycler_lecturas = (RecyclerView) findViewById(R.id.mis_lecturas);
        recycler_lecturas.setHasFixedSize(true);

        adaptadorMisLecturas = new AdaptadorMisLecturas(MisLecturasActivity.this,revistas_agregadas);
        recycler_lecturas.setAdapter(adaptadorMisLecturas);
        recycler_lecturas.setLayoutManager(new GridLayoutManager(MisLecturasActivity.this,3));

        adaptadorMisLecturas.notifyDataSetChanged();

        //Click para cada elemento en el recycler
        ItemClickSupport.addTo(recycler_lecturas).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                //Llamar a actividad de desglose de esta revista
                Log.d("Clic en revista#",String.valueOf(position));
                Intent intent = new Intent(MisLecturasActivity.this,InformacionLectruras.class);

                intent.putExtra("portada",revistas_agregadas.get(position).getPortada());
                intent.putExtra("titulo",revistas_agregadas.get(position).getTitulo());
                intent.putExtra("path",revistas_agregadas.get(position).getRuta());
                intent.putExtra("descripcion",revistas_agregadas.get(position).getDescripcion());
                intent.putExtra("año",revistas_agregadas.get(position).getAño());
                intent.putExtra("rev",revistas_agregadas.get(position).getReviews());
                intent.putExtra("ejemplar",String.valueOf(revistas_agregadas.get(position).getEjemplar()));
                startActivity(intent);


            }
        });
    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Ppermite modificar el hint que el EditText muestra por defecto
        searchView.setQueryHint("Buscar revista");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //TextView searchText = (TextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            //Este metodo se lanza cuando el boton busqueda es presionado


            @Override
            public boolean onQueryTextSubmit(String query) {

                texto = query;
                searchView.setQuery("", false);
                searchView.setIconified(true);

                Log.d("Query", query);

                return true;


            }
            //al modificar el texto del 'Edit text'
            @Override
            public boolean onQueryTextChange(String newText) {

                //System.out.println(newText);
                return true;

            }
        });



        return super.onCreateOptionsMenu(menu);
    }

*/
    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
    public void Agregar(){
        BRevistas helper = new BRevistas(this);
        ArrayList<ERevistas>  revis = helper.getRevistas();


        if(!revis.isEmpty()){
            for(int i = 0; i < revis.size(); i++) {
                ERevistas p = new ERevistas();
                p = revis.get(i);
                Revista r = new Revista();

                r.setTitulo(p.getTitulo());
                r.setDescripcion(p.getDescr());
                r.setRuta(p.getPath());
                r.setAño(p.getYear());
                r.setPortada(p.getPortada());
                r.setReviews(Integer.valueOf(p.getRev()));
                r.setEjemplar(Integer.valueOf(p.getEjemplar()));
                if (revistas_agregadas.isEmpty()) {

                    revistas_agregadas.add(r);
                    lista_titulos.add(r.getTitulo());

                } else {
                    if(!lista_titulos.contains(r.getTitulo())) {

                        revistas_agregadas.add(r);

                        lista_titulos.add(r.getTitulo());
                    }

                    }

                }
            }

        }


    @Override
    public void onBackPressed() {
        finish();
        if(conectadoWifi())
            if(pase)
            startActivity(new Intent(MisLecturasActivity.this, MainActivity.class));
            else
                startActivity(new Intent(MisLecturasActivity.this, InicioSesion.class));

        else
            finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                Log.i("ActionBar", "Atrás!");
                onBackPressed();
                break;
            default:
                break;
        }
        return true;

    }


}
