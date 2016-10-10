package com.eduardoapps.comoves;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends Activity {

    public static NoInternet c;
    String urlCredenciales;
    StringTokenizer st;
    String JSON;
    JSONObject jsonObject;
    String token;
    ProgressDialog progressDialog;

    private static final String KEY = "X-AUTHORIZATION";
    private static final String VALUE = "Bc3f9995e0321b7fe8a8de50318a392aadda6a42";


    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 2000;
    //FragmentPeliculas fragmentPeliculas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        c = new NoInternet();
        //configuramos TimerTask para que inicie nuestra actividad principal



                boolean activo = false;
                SharedPreferences Token = getSharedPreferences("USUARIO_ACTIVO", Context.MODE_PRIVATE);
                activo = Token.getBoolean("ACTIVO", false);
                System.out.println("Usuario Activo " + activo);
                token = Token.getString("TOKEN", "");

                if(!token.equals("")){
                    urlCredenciales = ("https://mipeiper.com/api/v1/userdata?token={token}").trim();
                    urlCredenciales = urlCredenciales.replace("{token}", token).trim();
                }

                if(c.isConnected(SplashScreen.this)){
                    if(activo){

                        new ObtenerCredenciales().execute();

                    }else {
                        TimerTask action = new TimerTask() {
                            @Override
                            public void run() {

                                if(c.isConnected(SplashScreen.this)){
                                    startActivity(new Intent(SplashScreen.this,InicioSesion.class));
                                    finish();
                                }

                                else{
                                    startActivity(new Intent(SplashScreen.this, MisLecturasActivity.class));
                                    finish();
                                }

                            }
                        };

//creamos el timer que ejecutará nuestra TimerTask después de 3 segundos
                        new Timer().schedule(action, 2000);
                        }
                    }else{
                    startActivity(new Intent(SplashScreen.this, MisLecturasActivity.class));
                    finish();
                }

            }


//creamos el timer que ejecutará nuestra TimerTask después de 3 segundos




    class ObtenerCredenciales extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SplashScreen.this);
            progressDialog.setMessage("Cargando Datos de Usuario");
            progressDialog.show();
            //showProgressDialog();
        }
        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpURLConnection connection = null;
                String json = "";
                URL url = new URL(urlCredenciales); //urlCredenciales = intent de token, concatenar con url de documento

                System.out.println("MI URL: "+url);
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
                    JSON = json;
                    Log.d("JSON: ", JSON);
                    System.out.println("JSON: "+json);

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
            //hideProgressDialog();
            Log.d("Respuesta JSON", JSON);
            try {
                jsonObject = new JSONObject(JSON);  //Declarar un jsonObject  ---- CONVERTIRMOS LA CADENA OBTENIDA, A UN JSON OBJECT
                Log.d("JSON Object", jsonObject.toString());
            } catch (Throwable t) {
                Log.e("","");
            }
            try {
                JSONObject usuario = jsonObject.getJSONObject("user");
                System.out.println("Aqui!!!"+usuario.toString());
                String endDate = usuario.getString("end_date");

                st= new StringTokenizer(endDate, "T");
                endDate=st.nextToken();
                Calendar c= Calendar.getInstance();
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate=sdf.format(c.getTime());

                System.out.println("FECHA ACTUAL: "+formattedDate);

                Date vencimiento= new Date();
                Date actual= new Date();
                try{
                    System.out.println("COMIENZA LA COMPARACIÓN!!!!!!!!!!!!!!!!!!");
                    vencimiento= sdf.parse(endDate);
                    actual=sdf.parse(formattedDate);
                    if(vencimiento.after(actual)==false){
                        Toast.makeText(SplashScreen.this, "Tu membresía a caducado",Toast.LENGTH_LONG).show();
                        Intent intent= new Intent(SplashScreen.this, InicioSesion.class);
                        progressDialog.dismiss();
                        startActivity(intent);

                    }else{
                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(i);
                        Toast.makeText(SplashScreen.this, "Bienvenido",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();

                    }

                }catch(ParseException e){
                    e.printStackTrace();
                }






            } catch (JSONException e) {
                Log.e("ERROR JSON", e.getMessage());
            }

            //setFragment(0);

        }
    }

}
