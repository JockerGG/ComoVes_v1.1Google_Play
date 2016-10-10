package com.eduardoapps.comoves;

/**
 * Created by jockergg on 3/10/16.
 */

public class ERevistas {
    public static  final String TABLE_NAME= "revistas";
    public static final  String FIELD_ID = "id";
    public static final  String FIELD_TITULO = "titulo";
    public static final  String FIELD_DESCR = "descripcion";
    public static final  String FIELD_PATH = "ruta";
    public static final  String FIELD_AÑO = "año";
    public static final  String FIELD_REV = "review";
    public static final  String FIELD_ESTADO = "estado";
    public static final  String FIELD_PORTADA = "portada";
    public static final  String FIELD_EJEMPLAR = "ejemplar";
    public static final String CREATE_BD_TABLE= "create table " + TABLE_NAME + "( "
                                                +FIELD_ID+ " integer primary key autoincrement, "
                                                +FIELD_TITULO + " text,"
                                                +FIELD_DESCR + " text,"
                                                +FIELD_PATH + " text,"
                                                +FIELD_AÑO + " text,"
                                                +FIELD_REV + " text,"
                                                +FIELD_PORTADA + " text,"
                                                +FIELD_EJEMPLAR + " text,"
                                                +FIELD_ESTADO+ " INTEGER DEFAULT 0"
                                                +" );";

    private int id, estado;
    private String titulo, descr, path, year, rev, portada, ejemplar;

    public ERevistas(String titulo, String descr, String path, String year, String rev, String portada, String ejemplar) {
        this.titulo = titulo;
        this.descr = descr;
        this.path = path;
        this.year = year;
        this.rev = rev;
        this.portada = portada;
        this.ejemplar = ejemplar;
    }
    public ERevistas(){

    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public String getEjemplar() {
        return ejemplar;
    }

    public void setEjemplar(String ejemplar) {
        this.ejemplar = ejemplar;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }
}
