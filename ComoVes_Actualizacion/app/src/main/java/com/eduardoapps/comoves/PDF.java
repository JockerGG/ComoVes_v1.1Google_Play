package com.eduardoapps.comoves;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.support.v7.app.AppCompatActivity;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.AsyncTask;
import com.artifex.mupdfdemo.FilePicker;
import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;

import java.io.File;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

public class PDF extends AppCompatActivity {



    File f = DownloadService.f;
    MuPDFCore core = null;
    MuPDFReaderView reader;
    MuPDFPageAdapter adapter;
    SeekBar scrollBar;
    TextView showPage;
    PDFView pdfView;
    PDialog p;
    Intent i = getIntent();
    Revista r = DesgloseRevista.revista;
    static String titulo;
    static String descr;
    static String ruta;
    static String year;
    static String reviews;
    static String portada;
    static String ejemplar;
    String in;
    boolean guardado = false;
    MisLecturasActivity lectura = new MisLecturasActivity();


    int prog;
    int pageAct, count, page, total;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BRevistas helper = new BRevistas(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        String path = f.getPath();

        r.setRuta(path);

        titulo = r.getTitulo();
        Log.d("TITULO_PDF", titulo);
        descr = r.getDescripcion();
        Log.d("DES_PDF", descr);
        ruta =  r.getRuta();
        Log.d("RUTA _PDF", ruta);
        year = r.getAño();
        Log.d("YEAR_PDF", year);
        reviews = String.valueOf(r.getReviews());
        Log.d("REVIEWS_PDF", String.valueOf(r.getReviews()));
        portada = r.getPortada();
        Log.d("PORTADA", portada);
        ejemplar = String.valueOf(r.getEjemplar());
        Log.d("EJEMPLAR", String.valueOf(r.getEjemplar()));


        final int pages;


        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT ) {          /**INICIO DEL LECTOR EN LA API MENOR A 23**/
            // only for gingerbread and newer versions
            System.out.println("Mostrando lectura para dispositivos menores a Api23");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            p = new PDialog(this, "Cargando Revista...");
            setContentView(R.layout.activity_pdf2);
            pdfView = (PDFView) findViewById(R.id.pdf2);
            scrollBar = (SeekBar) findViewById(R.id.scrollPage2);
            showPage = (TextView) findViewById(R.id.showPage2);

            assert pdfView != null;
            File f = DownloadService.f;
            pdfView.fromFile(f) // all pages are displayed by default
                    .enableSwipe(true)
                    .swipeHorizontal(true)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .onDraw(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                        }
                    })
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            total = nbPages;
                            scrollBar.setMax(nbPages);
                            showPage.setText(String.valueOf(pdfView.getCurrentPage())+"/"+String.valueOf(total));
                            p.hideProgressDialog();
                        }
                    })
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int pages, int pageCount) {
                            showPage.setText(String.valueOf(pdfView.getCurrentPage())+"/"+String.valueOf(total));
                            scrollBar.setProgress(pages);

                        }
                    })
                    .onPageScroll(new OnPageScrollListener() {
                        @Override
                        public void onPageScrolled(int page, float positionOffset) {

                        }
                    })
                    .onError(new OnErrorListener() {
                        @Override
                        public void onError(Throwable t) {

                        }
                    })
                    .enableAnnotationRendering(false)
                    .password(null)
                    .scrollHandle(null)
                    .load();

            scrollBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(progress < total) {
                        pageAct = progress;
                        showPage.setText(String.valueOf(progress) + "/" + String.valueOf(total));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    pdfView.jumpTo(pageAct);

                }
            });


            /**FIN DEL LECTOR EN LA API MENOR A 23**/


        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){ /**INICIO DEL LECTOR EN LA API 23**/
            setContentView(R.layout.activity_pdf);

            p = new PDialog(this, "Cargando Revista...");

            System.out.println("Mostrando lectura para dispositivos mayores a Api23");


            final RelativeLayout layout = (RelativeLayout) findViewById(R.id.pdf);
            scrollBar = (SeekBar) findViewById(R.id.scrollPage);
            showPage = (TextView) findViewById(R.id.showPage);

            try {
                core = new MuPDFCore(this, path);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("No se pudo abrir " + path, e.getMessage());
            }
            reader = new MuPDFReaderView(this);

            adapter = new MuPDFPageAdapter(this, new FilePicker.FilePickerSupport() {
                @Override
                public void performPickFor(FilePicker filePicker) {

                }
            }, core);
            reader.setAdapter(adapter);
            layout.addView(reader);
            p.hideProgressDialog();

            count = adapter.getCount();
            pageAct = reader.getDisplayedViewIndex();
            showPage.setText(String.valueOf(pageAct) + "/" + String.valueOf(count - 1));
            //reader.getCount();
            Log.d("COUNT", String.valueOf(count));


            reader.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    int page = reader.getDisplayedViewIndex();
                    scrollBar.setProgress(page);

                    showPage.setText(String.valueOf(reader.getDisplayedViewIndex()) + "/" + String.valueOf(count - 1));


                    return false;
                }
            });


            scrollBar.setMax(adapter.getCount());

            Log.d("READER COUNT", String.valueOf(reader.getCount()));


            scrollBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    prog = progress;
                    if (progress < count)
                        showPage.setText(String.valueOf(progress) + "/" + String.valueOf(count - 1));


                }


                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //showPage.setVisibility(View.VISIBLE);

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //showPage.setVisibility(View.INVISIBLE);
                    //int progress = seekBar.getProgress();
                    Log.d("PROGRESS", String.valueOf(prog));
                    if (scrollBar.isPressed())
                        ChangePage(count, prog);
                }
            });


        }



    }







    public void ChangePage(int max, int progress){  //Metodo de SeekBar

        if(progress < max){

            reader.setDisplayedViewIndex(progress);
            showPage.setText(String.valueOf(progress)+"/"+String.valueOf(max-1));
        }
    }
                                        /**FIN DEL LECTOR EN LA API 23**/
    @Override
    public void onBackPressed() {
        if(!guardado) {
            Log.d("BORRANDO...", f.getPath());
            f.delete();
        }
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.agregar, menu);;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                Log.i("ActionBar", "Atrás!");
                onBackPressed();
                break;
            default:
                break;
            case R.id.action_add:
                AgregarRevistas();
                guardado = true;
                break;
        }
        return true;

    }



    public void AgregarRevistas(){

        System.out.println("ENTRASTE A AGREGAR REVISTAS");
        new GuardarBD().execute();

    }

    public class GuardarBD extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {



            BRevistas helper = new BRevistas(PDF.this);

             System.out.println("Guardando: Titulo: "+titulo+" Descripcion: "+descr+" PATH: "+ruta+" Año: "+year+" Reviews: "+reviews+" Portada: "+ portada+" Ejemplar: "+ejemplar);

            ERevistas mi_revista = new ERevistas(titulo, descr, ruta, year, reviews, portada, ejemplar);

            helper.InsertarDatos(mi_revista);


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //lectura.Agregar();
            Toast.makeText(PDF.this, "AGREGADA A MIS REVISTAS", Toast.LENGTH_SHORT).show();
            guardado = true;

        }
    }
}
