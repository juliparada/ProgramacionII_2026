package com.example.amigossqlite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorMensajes extends BaseAdapter {
    Context context;
    ArrayList<mensajes> alMensajes;
    String miToken;
    LayoutInflater inflater;

    public AdaptadorMensajes(Context context, ArrayList<mensajes> alMensajes, String miToken) {
        this.context = context;
        this.alMensajes = alMensajes;
        this.miToken = miToken;
    }

    @Override
    public int getCount() {
        return alMensajes.size();
    }

    @Override
    public Object getItem(int position) {
        return alMensajes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mensajes msg = alMensajes.get(position);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        View itemView;
        if (msg.getDe().equals(miToken)) {
            itemView = inflater.inflate(R.layout.msgderecha, parent, false);
            TextView tempVal = itemView.findViewById(R.id.lblMsgDerecha);
            tempVal.setText(msg.getMensaje());
        } else {
            itemView = inflater.inflate(R.layout.msgizquierda, parent, false);
            TextView tempVal = itemView.findViewById(R.id.lblMsgIzquierda);
            tempVal.setText(msg.getMensaje());
        }
        
        return itemView;
    }
}
