package com.eduardoapps.comoves;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    public static List<Revista> revistas_agregadas = new ArrayList<>();
    public static List<String> lista_titulos = new ArrayList<>();  //Lista publica para guardar la lista de los titulo, y verificar si ya existe una revista
    public static String texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lista_titulos.add(null); //agregamos nulo al primer elemento,

        //FALTABA ESTE FRAGMENTO PARA QUE NO CERRARA LA APP EN LOGOUT

        //FIREBASE

       /* firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, InicioSesion.class));
        }

       // FirebaseUser user = firebaseAuth.getCurrentUser(); */


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setFragment(0);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


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

                setFragment(1);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
       if (id == R.id.nav_logout) {
           SharedPreferences Token = getSharedPreferences("USUARIO_ACTIVO", Context.MODE_PRIVATE);
           SharedPreferences.Editor editor = Token.edit();
           editor.remove("TOKEN");
           editor.putBoolean("ACTIVO", false);
           editor.apply();
           editor.commit();
           boolean valor = Token.getBoolean("ACTIVO", true);
           System.out.println("VALOR "+ valor);
           Intent i = new Intent(MainActivity.this, InicioSesion.class);
           startActivity(i);
           finish();

        } else if (id == R.id.nav_lecturas) {

                startActivity(new Intent(MainActivity.this, MisLecturasActivity.class));

        }
        else if (id == R.id.nav_home) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void setFragment(int fragmentID){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        switch(fragmentID){

            case 0:
                Revistas revistas = new Revistas();
                fragmentTransaction.replace(R.id.contenedor_fragments, revistas);
                fragmentTransaction.commit();
                break;

            case 1:
                Buscar buscar = new Buscar();
                fragmentTransaction.replace(R.id.contenedor_fragments,buscar);
                fragmentTransaction.commit();
                break;

            //Se suplio el fragment con una actividad
            /*
            case 2:
                FragmentLecturas lecturas = new FragmentLecturas();
                fragmentTransaction.replace(R.id.contenedor_fragments,lecturas);
                fragmentTransaction.commit();
                break;
               */
            case 3:
                FragmentNoRevistas noRevistas = new FragmentNoRevistas();
                fragmentTransaction.replace(R.id.contenedor_fragments,noRevistas);
                fragmentTransaction.commit();
                break;

        }

    }

}
