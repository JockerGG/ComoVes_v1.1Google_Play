package com.eduardoapps.comoves;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Eduardo on 22/09/2016.
 */
public class FilePDF {

    public static final String PDF_DIRECTORY_NAME = "PDF";
    public static final String FILE_NAME_PDF_EXTENSION = ".pdf";

    private File directory,pdf;




    // Para este constuctor se recomienda usar solo los tres nombres de carpeta en directory
    public FilePDF(String directory, String id, Context context){
        //this.directory = new File(Environment.getExternalStorageDirectory(),directory);
        this.directory = new File(context.getFilesDir(),directory);
        pdf = new File(this.directory,directory.concat(id).concat(FILE_NAME_PDF_EXTENSION));
        Log.d("Archivo",directory.concat(id).concat(FILE_NAME_PDF_EXTENSION ));
    }

    public boolean directoryExists(){
        return directory.exists();
    }

    public boolean pdfExists(){
        return pdf.exists();
    }

    public File getPDF(){
        return pdf;
    }

    public File getDirectory(){
        return directory;
    }

    public Uri getUriByPDF(){
        return Uri.fromFile(pdf);
    }

    private boolean createDirectory(){
        return directory.mkdir();
    }

    protected boolean createPDF() throws IOException {

        if(!directoryExists()) {
            return createDirectory() && pdf.createNewFile();
        }
        return pdf.createNewFile();
    }

}

