package com.eduardoapps.comoves;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Eduardo on 22/09/2016.
 */
public class DownloadService extends IntentService {
    FilePDF crearPDF;
    public static final int UPDATE_PROGRESS = 8344;
    public static File f;
    public DownloadService() {
        super("DownloadService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent.getStringExtra("url");
        String ejemplar = intent.getStringExtra("ejemplar");
        String directory = this.getFilesDir().toString();

        f = new File(directory, ejemplar.concat(".pdf"));

        crearPDF = new FilePDF(directory,ejemplar, this);
        try {
            crearPDF.createPDF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());


            Log.d("Ruta", f.getAbsolutePath());

            OutputStream output = new FileOutputStream(f);

            byte data[] = new byte[614400];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                Bundle resultData = new Bundle();
                resultData.putInt("progress" ,(int) (total * 100 / fileLength));
                receiver.send(UPDATE_PROGRESS, resultData);
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();




        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle resultData = new Bundle();
        resultData.putInt("progress" ,100);
        //receiver.send(UPDATE_PROGRESS, resultData);
    }


}


