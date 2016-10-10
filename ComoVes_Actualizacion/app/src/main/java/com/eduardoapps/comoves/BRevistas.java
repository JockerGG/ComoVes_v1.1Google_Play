package com.eduardoapps.comoves;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by jockergg on 3/10/16.
 */

public class BRevistas extends SQLiteOpenHelper {

    private static  final String BD_NAME = "mis_revistas";
    private static final  int SCHEME_VERSION = 1;
    private SQLiteDatabase db;

    public BRevistas(Context context) {
        super(context, BD_NAME, null, SCHEME_VERSION);

        db = this.getWritableDatabase();
    }

    private ContentValues generarValores(ERevistas revistas){

        ContentValues valores = new ContentValues();
        valores.put(ERevistas.FIELD_TITULO, revistas.getTitulo());
        valores.put(ERevistas.FIELD_DESCR, revistas.getDescr());
        valores.put(ERevistas.FIELD_PATH, revistas.getPath());
        valores.put(ERevistas.FIELD_AÑO, revistas.getYear());
        valores.put(ERevistas.FIELD_PORTADA, revistas.getPortada());
        valores.put(ERevistas.FIELD_REV, revistas.getRev());
        valores.put(ERevistas.FIELD_EJEMPLAR, revistas.getEjemplar());

        return valores;

    }

    public void InsertarDatos(ERevistas revistas){

        db.insert(ERevistas.TABLE_NAME, null, generarValores(revistas));
    }

    public ArrayList<ERevistas> getRevistas(){

        ArrayList<ERevistas> revistas = new ArrayList<>();

        String columnas[] = {ERevistas.FIELD_TITULO,ERevistas.FIELD_DESCR,ERevistas.FIELD_PATH,ERevistas.FIELD_AÑO,ERevistas.FIELD_PORTADA,ERevistas.FIELD_REV, ERevistas.FIELD_EJEMPLAR

        }
;
        Cursor c = db.query(ERevistas.TABLE_NAME, columnas,null,null,null,null,null);

        if(c.moveToFirst()){

            do{
                ERevistas r = new ERevistas();

                //r.setId(c.getInt(0));
                r.setTitulo(c.getString(0));
                r.setDescr(c.getString(1));
                r.setPath(c.getString(2));
                r.setYear(c.getString(3));
                r.setPortada(c.getString(4));
                r.setRev(c.getString(5));
                r.setEjemplar(c.getString(6));

                revistas.add(r);
            }while(c.moveToNext());


        }

        return revistas;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ERevistas.CREATE_BD_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
