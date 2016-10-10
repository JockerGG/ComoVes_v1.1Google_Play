package com.eduardoapps.comoves;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class InformacionLectruras extends Activity {
    String ejemplar, portada, titulo, año, path, descr, rev;
    TextView titulo_tv;
    TextView descripcion_tv;
    TextView año_tv;
    TextView voto;

    ImageView portada_iv;
    ImageButton boton_pdf;
    ImageButton boton_misLecturas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_lectruras);

        Intent intent = getIntent();
        //CACHANDO DE LOS INTENT
        portada = intent.getStringExtra("portada");
        titulo = intent.getStringExtra("titulo");
        año = intent.getStringExtra("año");
        ejemplar = intent.getStringExtra("ejemplar");
        path = intent.getStringExtra("path");
        descr = intent.getStringExtra("descripcion");
        rev = intent.getStringExtra("rev");


        titulo_tv = (TextView) findViewById(R.id.titulo_desglose2);
        descripcion_tv = (TextView) findViewById(R.id.descripcion_desglose2);
        año_tv = (TextView) findViewById(R.id.año_desglose2);
        voto = (TextView) findViewById(R.id.voto_desglose2);
        portada_iv = (ImageView) findViewById(R.id.portada_desglose2);
        boton_pdf = (ImageButton) findViewById(R.id.boton_pdf2);
        boton_misLecturas = (ImageButton) findViewById(R.id.boton_misLecturas2);

        titulo_tv.setText(titulo);
        descripcion_tv.setText(Html.fromHtml(descr));
        año_tv.setText(año);
        voto.setText(rev);

        Intent i = new Intent(InformacionLectruras.this, PDFsc.class);
        Log.d("FICHERO", path);
        i.putExtra("path", path);
        i.putExtra("guardado", true);
        startActivity(i);
        finish();

        boton_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(InformacionLectruras.this, PDFsc.class);
                i.putExtra("path", path);
                i.putExtra("guardado", true);
                startActivity(i);

            }
        });

        boton_misLecturas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InformacionLectruras.this, MisLecturasActivity.class);
                startActivity(i);
            }
        });

    }
}
