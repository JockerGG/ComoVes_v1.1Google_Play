package com.eduardoapps.comoves;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Revistas extends Fragment {


    public Revistas() {
        // Required empty public constructor
    }

    private ProgressDialog mProgressDialog;

    /**
     * Ponemos los parametros para la autorización de la API
     **/

    private static final String KEY = "X-AUTHORIZATION";
    private static final String VALUE = "Bc3f9995e0321b7fe8a8de50318a392aadda6a42";

    private String id_revista = "1";
    /**
     * ----------------
     **/

    /**
     * Agregamos las variables de las categorias
     ***/

    JSONObject jsonObject;

    List<String> nombre = new ArrayList<>(),
            keywords = new ArrayList<>(),
            covers = new ArrayList<>(),
            id_categoria = new ArrayList(); //En estás listas guardarmos las diferentes categorias

    String Url_buscar = "https://mipeiper.com/buscar?qr=",
            url_comp = "&id=";

    public static List<Revista> revistaNatura = new ArrayList<>(), revistaRobots = new ArrayList<>(), revistaAnimal = new ArrayList<>();

    String URL_Complete;
    private RecyclerView natura;
    private RecyclerView animal;
    private RecyclerView robots;
    List<Categoria> cat = new ArrayList<>();
    String url = "https://mipeiper.com/api/v1/categories/1/";

    String respuesta;

    private AdaptadorRevistas adaptadorAnimal, adaptadorNatura, adaptadorRobots;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_revistas, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        natura = (RecyclerView) getView().findViewById(R.id.natura);
        animal = (RecyclerView) getView().findViewById(R.id.animal);
        robots = (RecyclerView) getView().findViewById(R.id.robots);
        natura.setHasFixedSize(true);
        animal.setHasFixedSize(true);
        robots.setHasFixedSize(true);
        adaptadorAnimal = new AdaptadorRevistas(this, revistaAnimal);
        adaptadorNatura = new AdaptadorRevistas(this, revistaNatura);
        adaptadorRobots = new AdaptadorRevistas(this, revistaRobots);
        natura.setAdapter(adaptadorNatura);
        //natura.smoothScrollToPosition(3);
        animal.setAdapter(adaptadorAnimal);
        robots.setAdapter(adaptadorRobots);
        natura.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        animal.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        robots.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        clearData();
        //new ConectarBackGround().execute(url, KEY, VALUE);
       NoInternet n = new NoInternet();
        if (n.isConnected(getActivity()))
            new ObtenerRevistas().execute();

    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("cargando");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void clearData() {
        revistaAnimal.clear(); //clear list
        adaptadorAnimal.notifyDataSetChanged();
        revistaNatura.clear();
        adaptadorNatura.notifyDataSetChanged();
        revistaRobots.clear();
        adaptadorRobots.notifyDataSetChanged(); //let your adapter know about the changes and reload view.
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }


    private class ConectarBackGround extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {


            try {
                HttpURLConnection connection = null;
                String json = "";
                URL url = new URL("https://mipeiper.com/api/v1/categories/1");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET"); // Aquí ven que se especifica el tipo de petición al servidor.
                // Agrega el header con llave-valor
                connection.addRequestProperty(KEY, VALUE);  // Aquí van las llaves para poder acceder.
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {    // Si la conexión se logró
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    // La linea de arriba prepara el buffer de datos para recibir lo que envia el servidor
                    String line = null;
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {    //Proceso de lectura de datos y los agrega a line.
                        sb.append(line);
                    }
                    br.close();
                    json = sb.toString();

                } else {
                    Log.d("Conexion: ", connection.getResponseMessage());
                }

                connection.disconnect();

                return json;      // Contiene el json que devuelve el servidor.

            } catch (Exception e) {
                Log.e(MainActivity.class.toString(), e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideProgressDialog();

            Log.d("Respuesta JSON", s);

            try {

                jsonObject = new JSONObject(s);  //CONVERTIRMOS LA CADENA OBTENIDA, A UN JSON OBJECT
                Log.d("JSON Object", jsonObject.toString());

            } catch (Throwable t) {

                Log.e("Revistas", "Could not parse malformed JSON: \"" + s + "\"");

            }

            try {
                JSONArray categoria = jsonObject.getJSONArray("data");
                for (int i = 0; i < categoria.length(); i++) {
                    JSONObject categories = categoria.getJSONObject(i);
                    String name = categories.getString("nombre");
                    String key = categories.getString("keywords");
                    String cvrs = categories.getString("covers");
                    String id = categories.getString("id");

                    nombre.add(name);
                    Log.d("Agregando nombre: " + name, String.valueOf(nombre.size()));
                    keywords.add(key);
                    Log.d("Agregando keyword: " + key, String.valueOf(keywords.size()));
                    covers.add(cvrs);
                    Log.d("Agregando cover: " + cvrs, String.valueOf(covers.size()));
                    id_categoria.add(id);
                    Log.d("Agregando id: " + id, String.valueOf(id_categoria.size()));
                }
                new ObtenerRevistas().execute();
            } catch (JSONException e) {
                Log.e("ERROR JSON", e.getMessage());
            }

        }
    }


    public class ObtenerRevistas extends AsyncTask<Void, Void, List> {


        @Override
        protected List doInBackground(Void... params) {
            JSONArray jsonArray = null;
            Categoria obj;
            keywords.add(0,"natura");
            keywords.add(1,"Animal");
            keywords.add(2,"Robots");
            try {

                for (int i = 0; i < keywords.size(); i++) {
                    //int Time = 10000;
                    URL_Complete = (Url_buscar + keywords.get(i) + url_comp + id_revista).trim();
                    HttpURLConnection connection = null;
                    String json = "";
                    URL url = new URL(URL_Complete);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET"); // Aquí ven que se especifica el tipo de petición al servidor.
                    // Agrega el header con llave-valor
                    //connection.addRequestProperty(KEY, VALUE);  // Aquí van las llaves para poder acceder.

                    //connection.setReadTimeout(Time);
                    //connection.setConnectTimeout(Time);
                    connection.connect();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {    // Si la conexión se logró
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        // La linea de arriba prepara el buffer de datos para recibir lo que envia el servidor
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {    //Proceso de lectura de datos y los agrega a line.
                            sb.append(line);
                        }
                        br.close();
                        json = sb.toString();
                        Log.d("JSON Revistas: ", json);
                        obj = new Categoria();
                        obj.setJson(json);
                        obj.setKeyword(keywords.get(i));
                        cat.add(obj);
                        Log.d("Lista", "JSON: "+cat.get(i).getJson() + " Key: " + cat.get(i).getKeyword());

                    } else {
                        Log.d("Conexion a  " + URL_Complete, connection.getResponseMessage());
                    }

                    connection.disconnect();

                }
                return cat;
            } catch (Exception e) {
                Log.e(MainActivity.class.toString(), e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            Categoria Objcat;

            for(int i = 0; i <list.size(); i++){

                Objcat = cat.get(i);

                String keyw = Objcat.getKeyword();
                String json = Objcat.getJson();

                try {
                    JSONArray jsonArray = new JSONArray(json);

                    for(int j = 0; j < jsonArray.length(); j++){


                        JSONObject rev = jsonArray.getJSONObject(j);

                        String nombre = rev.getString("titulo_portada");
                        String año = rev.getString("year");
                        String views = rev.getString("views");
                        int ejemplar = rev.getInt("ejemplar");

                        Revista r = new Revista();
                        r.setTitulo(nombre);
                        r.setAño(año);
                        r.setEjemplar(ejemplar);
                        r.setPortada("https://www.mipeiper.com/cover/"+id_revista+"/Portada_"+String.valueOf(ejemplar)+".jpg");

                        if (keyw.equals("natura")) {
                            revistaNatura.add(r);
                            Log.d("Agregando revista", nombre);
                            adaptadorNatura.notifyDataSetChanged();
                        }
                        if (keyw.equals("Robots")) {
                            revistaRobots.add(r);
                            Log.d("Agregando revista", nombre);
                            adaptadorRobots.notifyDataSetChanged();
                        }

                        if (keyw.equals("Animal")) {
                            revistaAnimal.add(r);
                            Log.d("Agregando revista", nombre);
                            adaptadorAnimal.notifyDataSetChanged();
                        }
                    }


                }catch (Throwable T){
                    Log.e("Revistas: " + keyw, "Could not parse malformed JSON: \"" + json + "\"");
                }

            }

            //Click para cada elemento en el recycler
            //NATURA
            ItemClickSupport.addTo(natura).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    //Llamar a actividad de desglose de esta revista
                    Log.d("Clic en revista Natura#",String.valueOf(position));
                    Intent intent = new Intent(getActivity(),DesgloseRevista.class);
                    intent.putExtra("portada",revistaNatura.get(position).getPortada());
                    intent.putExtra("titulo",revistaNatura.get(position).getTitulo());
                    intent.putExtra("año",revistaNatura.get(position).getAño());
                    intent.putExtra("ejemplar",String.valueOf(revistaNatura.get(position).getEjemplar()));

                    startActivity(intent);
                }
            });

            //ANIMAL
            ItemClickSupport.addTo(animal).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    Log.d("Clic en revista Animal#",String.valueOf(position));
                    Intent intent = new Intent(getActivity(),DesgloseRevista.class);
                    intent.putExtra("portada",revistaAnimal.get(position).getPortada());
                    intent.putExtra("titulo",revistaAnimal.get(position).getTitulo());
                    intent.putExtra("año",revistaAnimal.get(position).getAño());
                    intent.putExtra("ejemplar",String.valueOf(revistaAnimal.get(position).getEjemplar()));
                    startActivity(intent);
                }
            });
            //ROBOTS
            ItemClickSupport.addTo(robots).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    Log.d("Clic en revista Robots#",String.valueOf(position));
                    Intent intent = new Intent(getActivity(),DesgloseRevista.class);
                    intent.putExtra("portada",revistaRobots.get(position).getPortada());
                    intent.putExtra("titulo",revistaRobots.get(position).getTitulo());
                    intent.putExtra("año",revistaRobots.get(position).getAño());
                    intent.putExtra("ejemplar",String.valueOf(revistaRobots.get(position).getEjemplar()));

                    startActivity(intent);
                }
            });


        }
    }
}