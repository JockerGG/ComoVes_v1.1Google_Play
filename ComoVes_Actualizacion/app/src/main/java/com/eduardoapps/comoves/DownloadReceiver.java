package com.eduardoapps.comoves;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import java.io.File;

/**
 * Created by Eduardo on 22/09/2016.
 */
public class DownloadReceiver extends ResultReceiver {

    public DownloadReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if (resultCode == DownloadService.UPDATE_PROGRESS) {
            int progress = resultData.getInt("progress");

            if (progress == 100) {
                //Revistas r = DesgloseRevista.r;
                File f = DownloadService.f;

                String titulo = DesgloseRevista.titulo;
                String descr = DesgloseRevista.descripcion;
                String path =  f.getPath();
                String year = DesgloseRevista.año;
                String reviews = DesgloseRevista.reviews;
                String portada = DesgloseRevista.portada;
                String ejemplar = DesgloseRevista.ejemplar;

                PDialog p = DesgloseRevista.p;
                 Context c= DesgloseRevista.c;
                System.out.println("DESCARGA TERMINADA");
                Intent i = new Intent(c, PDF.class);
                /*i.putExtra("titulo", titulo);
                i.putExtra("descripcion", descr);
                i.putExtra("ruta", path);
                i.putExtra("year", year);
                i.putExtra("rev", reviews);
                i.putExtra("portada", portada);
                i.putExtra("ejemplar", ejemplar);

                System.out.println("ENVIANDO: \nTitulo: "+titulo+"\nDescripcion: "+
                        descr+"\nPATH: "+path+"\nAño: "+
                        year+"\nReviews: "+reviews+"\nPortada: "+ portada+"\nEjemplar: "+ejemplar);

                */c.startActivity(i);
                p.hideProgressDialog();



            }
        }
    }
}