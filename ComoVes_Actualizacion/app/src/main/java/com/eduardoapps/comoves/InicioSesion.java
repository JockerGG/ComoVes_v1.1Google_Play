package com.eduardoapps.comoves;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;



import org.json.JSONArray;
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

public class InicioSesion extends AppCompatActivity {

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    String url = "https://www.mipeiper.com/register";
    String urlCredenciales;
    StringTokenizer st;
    String JSON;
    JSONObject jsonObject;
    private static final String KEY = "X-AUTHORIZATION";
    private static final String VALUE = "Bc3f9995e0321b7fe8a8de50318a392aadda6a42";
    NoInternet c;
    //firebase auth object

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        editTextEmail = (EditText) findViewById(R.id.usuario_login);
        editTextPassword = (EditText) findViewById(R.id.contrasena_login);
        buttonSignIn = (Button) findViewById(R.id.boton_iniciarSesion);
        textViewSignup = (TextView) findViewById(R.id.link);


        textViewSignup.setText(Html.fromHtml("<a href=" + url + ">Registrate Aquí</a>"));
        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(url);

                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });



        c = new NoInternet();

        progressDialog = new ProgressDialog(this);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c.isConnected(InicioSesion.this)) {
                    progressDialog.setMessage(" Iniciando sesion...");
                    progressDialog.show();
                    userLogin();
                }
                else {
                    Toast.makeText(InicioSesion.this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(InicioSesion.this, MisLecturasActivity.class);
                    startActivity(i);
                }
            }
        });
    }


    //method for user login
    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Introduce tu e-mail",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Introduce tu contraseña",Toast.LENGTH_SHORT).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog
        String urlApiValidacion="https://www.mipeiper.com/api/v1/jauth?email="+email+"&password="+password+"&id_revista=1";

        ///PROCESO DE VALIDACIÓN HACIENDO PETICIÓN A LA API DE MIPEIPER
        JsonObjectRequest peticionHTTP= new JsonObjectRequest(Request.Method.GET, urlApiValidacion, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                ;
                try {
                    //JSONObject mainUsers=jsonObject.getJSONObject("token");
                    //JSONObject usuario=mainUsers.getJSONObject(0);
                    String token=jsonObject.getString("token");

                    System.out.println("TOKEN USUARIO: "+token);
                    urlCredenciales = ("https://mipeiper.com/api/v1/userdata?token={token}").trim();
                    urlCredenciales = urlCredenciales.replace("{token}", token).trim();
                    SharedPreferences Token = getSharedPreferences("USUARIO_ACTIVO", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = Token.edit();
                    editor.putBoolean("ACTIVO", true);
                    editor.putString("TOKEN", token);
                    editor.commit();

                    if(!token.equals("")){
                        new ObtenerCredenciales().execute();

                        finish();
                    }

                }catch (JSONException e){
                    Log.e("Error JSON", e.getMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("Respuesta:","Error");
                progressDialog.dismiss();
                Toast.makeText(InicioSesion.this, "Usuario y/o Contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        });

        ApplicationController.getInstance().addToRequestQueue(peticionHTTP);



        //logging in the user
        /*firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if(task.isSuccessful()){
                            //start the profile activity
                            startActivity(new Intent(InicioSesion.this,MainActivity.class));

                            finish();

                        }
                    }
                });*/

    }

    class ObtenerCredenciales extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                        Toast.makeText(InicioSesion.this, "Tu membresía a caducado",Toast.LENGTH_LONG).show();
                        Intent intent= new Intent(InicioSesion.this, InicioSesion.class);
                        startActivity(intent);

                    }else{
                        Intent i = new Intent(InicioSesion.this, MainActivity.class);
                        startActivity(i);
                        Toast.makeText(InicioSesion.this, "Bienvenido",Toast.LENGTH_SHORT).show();
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
