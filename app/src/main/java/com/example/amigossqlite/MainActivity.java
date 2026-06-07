package com.example.amigossqlite;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    FloatingActionButton fab;
    Button btn;
    TextView tempVal;
    String accion="", idAmigo="", id="", rev="";
    ImageView img;
    String urlCompletaFoto="", getUrlCompletaFotoFirestore ="";
    Intent tomarFotoIntent;
    detectarinternet di;
    DatabaseReference databaseReference;
    String miToken = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        obtenerToken();
        img = findViewById(R.id.imgFotoAmigo);

        btn = findViewById(R.id.btnGuardarAmigo);
        
        // Recibir parámetros para edición
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accion = extras.getString("accion");
            if (accion.equals("modificar")) {
                idAmigo = extras.getString("idAmigo");
                ((TextView)findViewById(R.id.txtNombreAmigos)).setText(extras.getString("nombre"));
                ((TextView)findViewById(R.id.txtDireccionAmigos)).setText(extras.getString("direccion"));
                ((TextView)findViewById(R.id.txtTelefonoAmigos)).setText(extras.getString("telefono"));
                ((TextView)findViewById(R.id.txtEmailAmigos)).setText(extras.getString("email"));
                ((TextView)findViewById(R.id.txtDuiAmigos)).setText(extras.getString("dui"));
                urlCompletaFoto = extras.getString("urlFoto");
                if (urlCompletaFoto != null && !urlCompletaFoto.isEmpty()) {
                    img.setImageURI(Uri.fromFile(new File(urlCompletaFoto)));
                }
                btn.setText("Actualizar Amigo");
            }
        }

        btn.setOnClickListener(view -> {
            if (accion.equals("modificar")) {
                guardarAmigo(idAmigo);
            } else {
                String key = FirebaseDatabase.getInstance().getReference("amigos").push().getKey();
                guardarAmigo(key);
            }
        });

        fab = findViewById(R.id.fabListaAmigo);
        fab.setOnClickListener(view -> finish()); // Regresa a la lista

        tomarFoto();
    }
    private void obtenerToken(){
        try{
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tarea->{
                if(!tarea.isSuccessful()){
                    mostrarMsg("Error al obtener token: "+tarea.getException().getMessage());
                }else{
                    miToken = tarea.getResult();
                }
            });
        }catch (Exception e){
            mostrarMsg("Error al obtener token: "+e.getMessage());
        }
    }
    private void tomarFoto(){
        img.setOnClickListener(view->{
            tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File fotoAmigo = null;
            try{
                fotoAmigo = crearImagenAmigo();
                if( fotoAmigo!=null ){
                    Uri uriFotoAimgo = FileProvider.getUriForFile(MainActivity.this,
                            "com.example.amigossqlite.fileprovider", fotoAmigo);
                    tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoAimgo);
                    startActivityForResult(tomarFotoIntent, 1);
                }else{
                    mostrarMsg("Nose pudo crear la imagen.");
                }
            }catch (Exception e){
                mostrarMsg("Error al tomar foto: "+e.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if( requestCode==1 && resultCode==RESULT_OK ){
                img.setImageURI(Uri.fromFile(new File(urlCompletaFoto)));
            }else{
                mostrarMsg("No se tomo la foto.");
            }
        }catch (Exception e){
            mostrarMsg("Error al tomar la foto: "+e.getMessage());
        }
    }

    private File crearImagenAmigo() throws Exception{
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),
                fileName = "imagen_"+ fechaHoraMs+"_";
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if( dirAlmacenamiento.exists()==false ){
            dirAlmacenamiento.mkdir();
        }
        File image = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        urlCompletaFoto = image.getAbsolutePath();
        return image;
    }
    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    private void abrirVentana(){
        Intent intent = new Intent(this, lista_amigos.class);
        startActivity(intent);
    }
    private void guardarAmigo(String key) {
        try {
            tempVal = findViewById(R.id.txtNombreAmigos);
            String nombre = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtDireccionAmigos);
            String direccion = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtTelefonoAmigos);
            String telefono = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtEmailAmigos);
            String email = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtDuiAmigos);
            String dui = tempVal.getText().toString();

            if( miToken.equals("") || miToken==null ){
                obtenerToken();
            }
            // Usamos la KEY generada como ID del amigo
            amigos amigo = new amigos(key, nombre, direccion, telefono, email, dui, urlCompletaFoto, getUrlCompletaFotoFirestore, miToken);
            
            databaseReference = FirebaseDatabase.getInstance().getReference("amigos");
            databaseReference.child(key).setValue(amigo).addOnSuccessListener(success->{
                mostrarMsg("Registro guardado con exito.");
                abrirVentana();
            }).addOnFailureListener(failure->{
                mostrarMsg("Error al registrar datos: "+failure.getMessage());
            });
        }catch (Exception e){
            mostrarMsg("Error guardar: "+e.getMessage());
        }
    }
    private void subirFotoFirestore(){
        if (urlCompletaFoto == null || urlCompletaFoto.isEmpty()) {
            mostrarMsg("Por favor, toma una foto primero.");
            return;
        }

        File fileToUpload = new File(urlCompletaFoto);
        if (!fileToUpload.exists()) {
            mostrarMsg("El archivo de la foto no existe en el dispositivo.");
            return;
        }

        mostrarMsg("Subiendo foto...");
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        Uri fileUri = Uri.fromFile(fileToUpload);
        
        // Generamos la key de una vez para usarla en el nombre de la foto y en el registro
        String key = FirebaseDatabase.getInstance().getReference("amigos").push().getKey();
        final StorageReference fileRef = reference.child("fotosAmigos/" + key + ".jpg");

        final UploadTask uploadTask = fileRef.putFile(fileUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                getUrlCompletaFotoFirestore = uri.toString();
                guardarAmigo(key);
            }).addOnFailureListener(e -> {
                mostrarMsg("Error al obtener la url de la foto: "+e.getMessage());
            });
        }).addOnFailureListener(e -> {
            mostrarMsg("Error al subir la foto (Verifica si habilitaste Storage en Firebase): "+e.getMessage());
        });
    }
}