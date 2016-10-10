package com.eduardoapps.comoves;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Eduardo on 17/09/2016.
 */
public class AdaptadorMisLecturas extends RecyclerView.Adapter<AdaptadorMisLecturas.RevistaViewHolder>{

    private List<Revista> listaRevistas;
    private MisLecturasActivity lecturas;

    public class RevistaViewHolder extends RecyclerView.ViewHolder {

        public TextView titulo_lista;
        public ImageView portada;
        public RevistaViewHolder(View v) {
            super(v);
            portada = (ImageView) v.findViewById(R.id.small_portada);
            titulo_lista = (TextView) v.findViewById(R.id.title_list);
        }
    }

    public AdaptadorMisLecturas(MisLecturasActivity lecturas, List<Revista> listaRevistas){
        this.lecturas = lecturas;
        this.listaRevistas = listaRevistas;

        Log.d("Recibiendo lista", String.valueOf(listaRevistas.size()));
    }



    @Override
    public RevistaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_revistas, parent, false);

        return new RevistaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RevistaViewHolder holder, int position) {


        holder.titulo_lista.setText(listaRevistas.get(position).getTitulo());
        Glide.with(lecturas).load(listaRevistas.get(position).getPortada()).into(holder.portada);
    }

    @Override
    public int getItemCount() {
        return listaRevistas.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}