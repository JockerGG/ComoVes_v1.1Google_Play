package com.eduardoapps.comoves;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class EntrarRegistrarse extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrar_registrarse);

        ImageButton boton_login = (ImageButton) findViewById(R.id.boton_login);
        ImageButton boton_register = (ImageButton) findViewById(R.id.boton_register);

        boton_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EntrarRegistrarse.this,InicioSesion.class);
                startActivity(i);
            }
        });

        boton_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(EntrarRegistrarse.this,Registro.class);
                startActivity(i);
            }
        });


    }
}
