package com.eduardoapps.comoves;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registro extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordRepetida;
    private Button buttonSignup;
    private ProgressDialog progressDialog;


    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        firebaseAuth = FirebaseAuth.getInstance();

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.nuevo_usuario);
        editTextPassword = (EditText) findViewById(R.id.nueva_contrasena);
        editTextPasswordRepetida = (EditText) findViewById(R.id.repeticion_contrasena);
        buttonSignup = (Button) findViewById(R.id.boton_Signup);

        progressDialog = new ProgressDialog(this);

        final String contrasena = editTextPassword.getText().toString();
        final String contrasena_repetida = editTextPasswordRepetida.getText().toString();

        //attaching listener to button
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(contrasena.equals(contrasena_repetida) && contrasena.length()>=8)
                registerUser();  //No funcionan las condicines.
                //else
                //  Toast.makeText(Registro.this,"Datos de contraseña incorrectos",Toast.LENGTH_LONG).show();


            }
        });

    }



    private void registerUser(){

        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Introduce tu e-mail",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Introduce una contraseña",Toast.LENGTH_SHORT).show();
            return;



        }

        progressDialog.setMessage("Registrando nuevo usuario...");
        progressDialog.show();


        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            Toast.makeText(Registro.this,"Registro exitoso",Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }else{
                            //display some message here
                            Toast.makeText(Registro.this,"Error al registrarse",Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }
}

