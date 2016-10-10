package com.eduardoapps.comoves;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
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
public class Buscar extends Fragment {

    String busqueda;
    String busqueda2;

    private String id_revista = "1";
    /**
     * ----------------
     **/

    /**
     * Agregamos las variables de las categorias
     ***/

    JSONObject jsonObject;

    String Url_buscar = "https://mipeiper.com/buscar?qr=",
            url_comp = "&id=";

    public static List<Revista> revistabusquedas = new ArrayList<>();

    String URL_Complete;
    private RecyclerView buscar;
    List<Categoria> cat = new ArrayList<>();


    private AdaptadorBusqueda adaptadorBusqueda;

    public Buscar() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buscar, container, false);



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        buscar = (RecyclerView) getView().findViewById(R.id.busqueda);
        buscar.setHasFixedSize(true);

       adaptadorBusqueda = new AdaptadorBusqueda(this,revistabusquedas);
        buscar.setAdapter(adaptadorBusqueda);
        buscar.setLayoutManager(new GridLayoutManager(getActivity(),3));

        //CUANDO EL USUARIO PRESIONA BUSCAR PELICULA
        clearData();
        busqueda = MainActivity.texto;
                     if (!busqueda.equals("")) {
                    clearData(); //removiendo los items, si los hay.
                    Log.d("textp",busqueda);
                    new ObtenerRevistas().execute();
                     }

    }

    //PARA LIMPIAR EL RECYCLER REVISTA ("http://stackoverflow.com/questions/29978695/remove-all-items-from-recyclerview)
    public void clearData() {
        revistabusquedas.clear(); //clear list
        adaptadorBusqueda.notifyDataSetChanged(); //let your adapter know about the changes and reload view.
    }


    public class ObtenerRevistas extends AsyncTask<Void, Void, List> {


        @Override
        protected List doInBackground(Void... params) {
            JSONArray jsonArray = null;
            Categoria obj;
            try {

                    //int Time = 10000;
                    URL_Complete = (Url_buscar + busqueda + url_comp + id_revista).trim();
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
                        obj.setKeyword(busqueda);
                        cat.add(obj);
                      //  Log.d("Lista", "JSON: "+cat.get().getJson() + " Key: " + cat.get(i).getKeyword());

                    } else {
                        Log.d("Conexion a  " + URL_Complete, connection.getResponseMessage());
                    }

                    connection.disconnect();


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

                        revistabusquedas.add(r);
                        Log.d("Agregando revista:", nombre);
                        adaptadorBusqueda.notifyDataSetChanged();

                    }
                }catch (Throwable T){
                    Log.e("Revistas: " + keyw, "Could not parse malformed JSON: \"" + json + "\"");
                }

            }

            //Click para cada elemento en el recycler
            ItemClickSupport.addTo(buscar).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    //Llamar a actividad de desglose de esta revista
                    Log.d("Clic en revista#",String.valueOf(position));
                    Intent intent = new Intent(getActivity(),DesgloseRevista.class);
                    intent.putExtra("portada",revistabusquedas.get(position).getPortada());
                    intent.putExtra("titulo",revistabusquedas.get(position).getTitulo());
                    intent.putExtra("año",revistabusquedas.get(position).getAño());
                    intent.putExtra("ejemplar",String.valueOf(revistabusquedas.get(position).getEjemplar()));

                    startActivity(intent);
                }
            });


        }
    }
}
