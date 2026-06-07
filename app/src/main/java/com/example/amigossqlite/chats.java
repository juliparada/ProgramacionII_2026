package com.example.amigossqlite;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class chats extends Activity {
    private String idAmigo, nombreAmigo, tokenAmigo, fotoAmigo;
    private String miToken;
    private DatabaseReference databaseReference;
    private ArrayList<mensajes> alMensajes = new ArrayList<>();
    private AdaptadorMensajes adaptadorMensajes;
    private ListView ltsMsg;
    private EditText txtMsg;
    private ImageButton btnEnviar, btnAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        // Obtener datos del amigo del Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idAmigo = extras.getString("idAmigo");
            nombreAmigo = extras.getString("nombre");
            tokenAmigo = extras.getString("to");
            fotoAmigo = extras.getString("urlFoto");
        }

        inicializarComponentes();
        obtenerMiToken();
    }

    private void inicializarComponentes() {
        ltsMsg = findViewById(R.id.ltschtas);
        txtMsg = findViewById(R.id.txtMsgChats);
        btnEnviar = findViewById(R.id.btnEnviarMsg);
        btnAtras = findViewById(R.id.imgAtras);

        TextView lblNombre = findViewById(R.id.lblToChats);
        lblNombre.setText(nombreAmigo);

        ImageView imgFoto = findViewById(R.id.imgFotoAmigoChats);
        if (fotoAmigo != null && !fotoAmigo.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(fotoAmigo);
            if (bitmap != null) {
                imgFoto.setImageBitmap(bitmap);
            }
        }

        btnAtras.setOnClickListener(v -> finish());

        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    private void obtenerMiToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                miToken = task.getResult();
                if (miToken != null) {
                    cargarMensajes();
                }
            } else {
                Toast.makeText(this, "Error al obtener token propio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarMensajes() {
        if (miToken == null || tokenAmigo == null) return;

        String chatId = obtenerChatId(miToken, tokenAmigo);
        databaseReference = FirebaseDatabase.getInstance().getReference("chats").child(chatId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alMensajes.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mensajes msg = dataSnapshot.getValue(mensajes.class);
                    if (msg != null) {
                        alMensajes.add(msg);
                    }
                }
                if (adaptadorMensajes == null) {
                    adaptadorMensajes = new AdaptadorMensajes(chats.this, alMensajes, miToken);
                    ltsMsg.setAdapter(adaptadorMensajes);
                } else {
                    adaptadorMensajes.notifyDataSetChanged();
                }
                ltsMsg.setSelection(alMensajes.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chats.this, "Error al cargar mensajes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarMensaje() {
        String texto = txtMsg.getText().toString().trim();
        if (!texto.isEmpty() && miToken != null && tokenAmigo != null) {
            String chatId = obtenerChatId(miToken, tokenAmigo);
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("chats").child(chatId).push();
            
            String idMsg = db.getKey();
            String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            
            mensajes msg = new mensajes(idMsg, miToken, tokenAmigo, texto, fecha);
            db.setValue(msg).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    txtMsg.setText("");
                } else {
                    Toast.makeText(chats.this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String obtenerChatId(String token1, String token2) {
        // Aseguramos que el ID del chat sea el mismo para ambos usuarios
        if (token1.compareTo(token2) < 0) {
            return token1 + "_" + token2;
        } else {
            return token2 + "_" + token1;
        }
    }
}
