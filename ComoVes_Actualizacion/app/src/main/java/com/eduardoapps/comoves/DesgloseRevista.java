package com.eduardoapps.comoves;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.eduardoapps.comoves.DownloadService.f;

public class DesgloseRevista extends AppCompatActivity {
    ProgressDialog mProgressDialog;
    JSONObject jsonObject;
    String id_revista = "1";
    String Url = "https://mipeiper.com/api/v1/magazines/" + id_revista + "/";
    //PARA CACHAR DE LOS INTENT
    public static String portada;
    public static String titulo;
    public static String año;
    public static String ejemplar;
    public static String descripcion;
    public static String reviews;
    public static Revista revista;
    String añadida;
    String url;

    //DECLARACION DE TEXT VIEW E IMAGE VIEW
    TextView titulo_tv;
    TextView descripcion_tv;
    TextView año_tv;
    TextView voto;
    TextView añadida_tv;
    TextView te_recomendamos;

    ImageView portada_iv;
    ImageButton boton_pdf;
    ImageButton boton_agregar_revista;
    ImageButton boton_misLecturas;

    //Para las recomendaciones
    String Url_buscar = "https://mipeiper.com/buscar?qr=",
            url_comp = "&id=";
    String URL_Complete;
    private RecyclerView recomendaciones;

    List<String> lista_buscarRecomendaciones = new ArrayList<>();
    List<Categoria> cat = new ArrayList<>();

    List<Revista> listaRecomendaciones = new ArrayList<>();
    private AdaptadorRecomendaciones adaptadorRecomendaciones;


    //
    public static Context c;
    //public static Revistas r = new Revistas();
    public static PDialog p;
    NoInternet n = new NoInternet();

    //PARA LA AUTENTICACION
    private static final String KEY = "X-AUTHORIZATION";
    private static final String VALUE = "Bc3f9995e0321b7fe8a8de50318a392aadda6a42";

    public static List<Revista> revistas_agregadas = MainActivity.revistas_agregadas; //accediendo a la lista revistas agregadas
    public static List<String> lista_titulos = MainActivity.lista_titulos;
    File f = DownloadService.f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.desglose_revista);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //RECOMENDACIONES
        recomendaciones = (RecyclerView) findViewById(R.id.recomendaciones);
        recomendaciones.setHasFixedSize(true);

        adaptadorRecomendaciones = new AdaptadorRecomendaciones(this,listaRecomendaciones);
        recomendaciones.setAdapter(adaptadorRecomendaciones);
        recomendaciones.setLayoutManager(new LinearLayoutManager(DesgloseRevista.this, LinearLayoutManager.HORIZONTAL, false));
        //

        Intent intent = getIntent();
        //CACHANDO DE LOS INTENT
        portada = intent.getStringExtra("portada");
        titulo = intent.getStringExtra("titulo");
        año = intent.getStringExtra("año");
        ejemplar = intent.getStringExtra("ejemplar");

        url = "https://www.mipeiper.com/pdf/{id_revista}/comoves_{ejemplar}.pdf";
        url = url.replace("{id_revista}", id_revista).replace("{ejemplar}",ejemplar).trim();

        //TEXT VIEW E IMAGE VIEW
        titulo_tv = (TextView) findViewById(R.id.titulo_desglose);
        descripcion_tv = (TextView) findViewById(R.id.descripcion_desglose);
        te_recomendamos = (TextView) findViewById(R.id.te_recomendamos);
        año_tv = (TextView) findViewById(R.id.año_desglose);
        //añadida_tv = (TextView) findViewById(R.id.anadidas);
        boton_pdf = (ImageButton) findViewById(R.id.boton_pdf);
        //boton_agregar_revista = (ImageButton) findViewById(R.id.boton_agregar_revista);
        boton_misLecturas = (ImageButton) findViewById(R.id.boton_misLecturas);

        titulo_tv.setText(titulo);
        año_tv.setText(año);
        añadida = "Revista añadida";

        //descripcion_tv.setText(descripcion);

        portada_iv = (ImageView) findViewById(R.id.portada_desglose);
        Glide.with(this).load(portada).into(portada_iv);

        if(n.isConnected(DesgloseRevista.this))
            new ObtenerDatos().execute();

        boton_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = DesgloseRevista.this;

                p = new PDialog(c, "Cargando "+ titulo+", espere por favor...");
                p.showProgressDialog();
                if (n.isConnected(DesgloseRevista.this)) {
                    Intent intent = new Intent(DesgloseRevista.this, DownloadService.class);
                    intent.putExtra("url", url);
                    intent.putExtra("receiver", new DownloadReceiver(new Handler()));
                    intent.putExtra("ejemplar", ejemplar);
                    startService(intent);
                }
                else {
                    p.hideProgressDialog();
                    Toast.makeText(DesgloseRevista.this, "No hay Conexión a Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });



   /*     if(lista_titulos.contains(titulo)){
            boton_agregar_revista.setImageResource(R.drawable.delete);
            añadida_tv.setText("Eliminar revista");

        }

        else{
            boton_agregar_revista.setImageResource(R.drawable.coloradd);
            añadida_tv.setText("Añadir a mis revistas");

        }


        //STATUS BOTON: 1: FUNCIONA COMO AGREGAR REVISTA
        //              0: FUNCIONA COMO ELIMINAR REVISTA
        boton_agregar_revista.setTag(1);       //Boton funciona como agregar revista

     /*   boton_agregar_revista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Revista r = new Revista();

                int status  = (Integer) v.getTag();

                if(status == 1){
                    if(!lista_titulos.contains(titulo)) {
                        r.setTitulo(titulo);
                        r.setAño(año);
                        r.setPortada("https://www.mipeiper.com/cover/" + id_revista + "/Portada_" + String.valueOf(ejemplar) + ".jpg");
                        r.setEjemplar(Integer.valueOf(ejemplar));
                        revistas_agregadas.add(r);

                        lista_titulos.add(titulo);

                        boton_agregar_revista.setImageResource(R.drawable.delete); //cambiando imagen del boton
                        añadida_tv.setText("Eliminar revista");
                        Log.d("Agregando revista:", titulo);
                        Toast.makeText(DesgloseRevista.this, "Agregada a 'Mis lecturas' ", Toast.LENGTH_SHORT).show();

                        v.setTag(0); //boton cambia su funcionalidad a borrar
                    }

                    v.setTag(0); //
                }
                //Boton funciona como eliminar  /**ME QUEDO BUSCANDO DONDE PASA CARLOS LOS DATOS A LA LISTA DE GUARDADO*/
  /*              else {
                    for (Iterator<Revista> iter = revistas_agregadas.listIterator(); iter.hasNext(); ) {
                        r = iter.next();
                        if(r.getTitulo().equals(titulo)){
                            iter.remove();
                            lista_titulos.remove(titulo);
                        }

                    }
                    v.setTag(1);
                    añadida_tv.setText("Añadir revista");
                    boton_agregar_revista.setImageResource(R.drawable.coloradd); //cambiando imagen del boton
                    Toast.makeText(DesgloseRevista.this, "Revista eliminada ", Toast.LENGTH_SHORT).show();

                }




            }
        });
*/
        boton_misLecturas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Revista> revistasagregadas = MisLecturasActivity.revistas_agregadas;

                    startActivity(new Intent(DesgloseRevista.this,MisLecturasActivity.class));
                if(revistasagregadas.size() == 0)
                    Toast.makeText(DesgloseRevista.this,"Aún no tienes Revistas",Toast.LENGTH_SHORT).show();
            }
        });


        //en caso de que la busqueda por titulo no arroje ningun resultado ("Nostalgia setentera","Ciberacoso",etc)



        //Recomendaciones se buscan por una palabra al azar de la lista de
        //Ventajas: muestra distintas revistas cada vez
        //Desventajas: algunas palabras no arrojan ningun resultado (setentera, esperanza, etc)
        /////////////////////////////
        /*
        int rnd = new Random().nextInt(arr.length);
        keyword_recomendaciones =  arr[rnd];
        //Quitando caracteres especiales
        keyword_recomendaciones.replaceAll(",","");
        keyword_recomendaciones.replaceAll("[()-+,^:.?¿!¡/]","");
        */


        String[] arr = titulo.split(" ");
        //lo,y,en,entre,como,para,
        String[] art = {"La", "Los", "El", "la", "los", "el","las","Cuando","Intimamente","Pensar","con","Experimentos",
                "Estevia","dulzura","100","vigilancia","epidemiologica","Nostalgia","setentera","Mensajes","odisea","audio"
                ,"Nanomundo","importancia","Mover","Antimateria","singularidad","Basura","Dinosaurios","Computacion"
                ,"Huracanes","Fracking","Huellas","mosquitos","vuelven","microbioma","Diez","años","del","Zika","Ixtli","cañada"
                ,"Teletransportacion","destierro","sintetizadores","Popocatepetl"};

        for(int i =0;i<arr.length;i++){
            String palabra = arr[i].replace("¿","").replace("?", "").replace(",","").replace(":","").replace(".","").replace("%","").
                    replaceAll("á","a").replaceAll("é","e").replaceAll("í","i").replaceAll("ó","o").replaceAll("ú","u").
                    replaceAll("Á","A").replaceAll("É","E").replaceAll("Í","I").replaceAll("Ó","O").replaceAll("Ú","U");

            Log.d("Palabra obtenida", palabra);
            if(!palabra.equals(art[0]) && !palabra.equals(art[1]) && !palabra.equals(art[2]) && !palabra.equals(art[3]) && !palabra.equals(art[4]) && !palabra.equals(art[5])
                    && !palabra.equals(art[7]) && !palabra.equals(art[8])  && !palabra.equals(art[9])
                    && !palabra.equals(art[10])&& !palabra.equals(art[11])  && !palabra.equals(art[11])
                    && !palabra.equals(art[12]) && !palabra.equals(art[13]) && !palabra.equals(art[14])
                    && !palabra.equals(art[15]) && !palabra.equals(art[16]) && !palabra.equals(art[17])
                    && !palabra.equals(art[18])&& !palabra.equals(art[19]) && !palabra.equals(art[20])
                    && !palabra.equals(art[21]) && !palabra.equals(art[22]) && !palabra.equals(art[23])
                    && !palabra.equals(art[24])  && !palabra.equals(art[25]) && !palabra.equals(art[26 ])
                    && !palabra.equals(art[27]) && !palabra.equals(art[28]) && !palabra.equals(art[29])
                    && !palabra.equals(art[30]) && !palabra.equals(art[31]) && !palabra.equals(art[32])
                    && !palabra.equals(art[33]) && !palabra.equals(art[34])  && !palabra.equals(art[35])
                    && !palabra.equals(art[36]) && !palabra.equals(art[37])  && !palabra.equals(art[38])
                    && !palabra.equals(art[39]) && !palabra.equals(art[40]) && !palabra.equals(art[41])
                    && !palabra.equals(art[42]) && !palabra.equals(art[43]) && !palabra.equals(art[44])
                    && !palabra.equals(art[45])
                    ){
                //Agregando a la lista todas las palabras del arreglo, excepto los articulos
                Log.d("Palabra agregada " , palabra);
                String pbusqueda = palabra;
                lista_buscarRecomendaciones.add(pbusqueda);
                pbusqueda="";
            }
            Log.d("Item i-esimo de array",arr[i]);
        }

        //Agregando una palabra para generar recomendaciones por default
        lista_buscarRecomendaciones.add("agua");

        new ObtenerRecomendaciones().execute();


    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Cargando");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public class ObtenerDatos extends AsyncTask<Void, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showProgressDialog();//Cometario

        }

        @Override
        protected String doInBackground(Void... params) {

            String Url_revistas;
            Url_revistas = (Url + ejemplar).trim();
            Log.d("Url revistas: ", Url_revistas);

            try {
                HttpURLConnection connection = null;
                String json = "";
                URL url = new URL((Url_revistas).trim());
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

                    int pos = sb.toString().indexOf("\"descripcion\"");
                    int pos2 = sb.toString().indexOf("\"reviews\"");

                    //json = sb.toString().substring(pos+15, pos+300);
                    json = sb.toString();

                } else {
                    Log.d("Conexion Desglose: ", connection.getResponseMessage());
                }

                connection.disconnect();
                Log.d("JSON datos Revista: ", json);
                return json;      // Contiene el json que devuelve el servidor.

            } catch (Exception e) {
                Log.e(MainActivity.class.toString(), e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Respuesta JSON", s);

            try {

                jsonObject = new JSONObject(s);  //CONVERTIMOS LA CADENA OBTENIDA, A UN JSON OBJECT
                Log.d("JSON Object", jsonObject.toString());

            } catch (Throwable t) {

                Log.e("Revistas", "Could not parse malformed JSON: \"" + s + "\"");

            }

            descripcion_tv = (TextView) findViewById(R.id.descripcion_desglose);
            voto = (TextView) findViewById(R.id.voto_desglose);

            try {
                JSONObject categoria = jsonObject.getJSONObject("data");


                 descripcion = categoria.getString("descripcion");
                 reviews = categoria.getString("reviews");
                //String year = categories.getString("year");
                // Log.d("Descripcion: ", descripcion);

                descripcion = Html.fromHtml(getShortString(descripcion)).toString();

                descripcion_tv.setText(descripcion);
                voto.setText(reviews);



            } catch (JSONException e) {
                Log.e("ERROR JSON", e.getMessage());
            }

            hideProgressDialog();

            revista = new Revista();

            Log.d("TITULO" ,titulo);
            revista.setTitulo(titulo);;
            Log.d("DESCRIĆION" ,descripcion);
            revista.setDescripcion(descripcion);;
//            Log.d("PATH" , f.getPath());
  //          revista.setRuta(f.getPath());;
            Log.d("YEAR" ,año);
            revista.setAño(año);;
            Log.d("REVIEWS" ,reviews);
            revista.setReviews(Integer.valueOf(reviews));;
            Log.d("PORTADA" ,portada);
            revista.setPortada(portada);;
            Log.d("EJEMPLAR" ,ejemplar);
            revista.setEjemplar(Integer.valueOf(ejemplar));;


        }
    }

    private String getShortString(String string){
        String aux[] = string.split("</p>");
        return aux[0];

    }

    //OBTENER RECOMENDACIONES
    public class ObtenerRecomendaciones extends AsyncTask<Void, Void, List> {

        @Override
        protected List doInBackground(Void... params) {
            JSONArray jsonArray = null;
            Categoria obj;
            try {
                for (int k = 0; k < lista_buscarRecomendaciones.size(); k++) {

                    URL_Complete = (Url_buscar + lista_buscarRecomendaciones.get(k) + url_comp + id_revista).trim();
                    Log.d("URL RECOMENDACIONES", URL_Complete);
                    Log.d("For recomendando por:", lista_buscarRecomendaciones.get(k));
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
                        obj.setKeyword(lista_buscarRecomendaciones.get(k));
                        cat.add(obj);

                        //  Log.d("Lista", "JSON: "+cat.get().getJson() + " Key: " + cat.get(i).getKeyword());

                    } else
                        Log.d("Conexion a  " + URL_Complete, connection.getResponseMessage());

                    connection.disconnect();

                    return cat;
                }

            }

            catch(Exception e)
            {
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
                        if(!titulo.equals(nombre)) {
                            r.setTitulo(nombre);
                            r.setAño(año);
                            r.setEjemplar(ejemplar);
                            r.setPortada("https://www.mipeiper.com/cover/" + id_revista + "/Portada_" + String.valueOf(ejemplar) + ".jpg");

                            te_recomendamos.setText("Te recomendamos");
                            listaRecomendaciones.add(r);
                            Log.d("Agregando revista:", nombre);
                            adaptadorRecomendaciones.notifyDataSetChanged();
                        }


                    }


                }catch (Throwable T){
                    Log.e("Revistas: " + keyw, "Could not parse malformed JSON: \"" + json + "\"");
                }

            }

            //Click para cada elemento en el recycler
            ItemClickSupport.addTo(recomendaciones).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    //Llamar a actividad de desglose de esta revista
                    Log.d("Clic en revista#",String.valueOf(position));
                    Intent intent = new Intent(DesgloseRevista.this,DesgloseRevista.class);
                    intent.putExtra("portada",listaRecomendaciones.get(position).getPortada());
                    intent.putExtra("titulo",listaRecomendaciones.get(position).getTitulo());
                    intent.putExtra("año",listaRecomendaciones.get(position).getAño());
                    intent.putExtra("ejemplar",String.valueOf(listaRecomendaciones.get(position).getEjemplar()));

                    startActivity(intent);
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(DesgloseRevista.this, MainActivity.class));
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
