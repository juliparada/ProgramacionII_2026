package com.example.amigossqlite;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    DB db;
    Button btn;
    TextView tempVal;
    String accion="Nuevo", idAmigo="0", urlFoto="";
    Intent tomarFotoIntent;
    FloatingActionButton fab;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.imgFotoAmigo);
        img.setOnClickListener(v->tomarFoto());

        db = new DB(this);

        btn = findViewById(R.id.btnGuardarAmigo);
        btn.setOnClickListener(v->guardarAmigo());

        fab = findViewById(R.id.fabListaAmigo);
    }

    private void tomarFoto(){
        tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fotoAmigo = null;

        try{
            fotoAmigo = crearImgAmigo();
            if(fotoAmigo!=null){
                Uri uriFoto = FileProvider.getUriForFile(MainActivity.this, "com.example.amigossqlite.fileprovider", fotoAmigo);
                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
                startActivityForResult(tomarFotoIntent, 1);
            }else{
                mostrarMsg("No se pudo crear la foto");
            }
        } catch (Exception e) {
            mostrarMsg("Error al tomar la foto: "+ e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==1 && resultCode==RESULT_OK){
                img.setImageURI(Uri.fromFile(new File(urlFoto)));
            }
        } catch (Exception e) {
            mostrarMsg("Error al mostrar la foto: "+ e.getMessage());
        }
    }

    private File crearImgAmigo() throws Exception{
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),
                fileName = "foto_"+ fechaHoraMs;
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if(!dirAlmacenamiento.exists()){
            dirAlmacenamiento.mkdirs();
        }
        File image = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        urlFoto = image.getAbsolutePath();
        return image;
    }

    private void guardarAmigo(){
        tempVal = findViewById(R.id.txtNombreAmigos);
        String nombre = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtDireccionAmigos);
        String direccion = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtTelefonoAmigos);
        String tel = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtEmailAmigos);
        String email = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtDuiAmigos);
        String dui = tempVal.getText().toString();

        String[] datos = {idAmigo, nombre, direccion, tel, email, dui, urlFoto};
        String respuesta = db.administrar_amigos(accion, datos);
        if(respuesta.equals("ok")){
            mostrarMsg("Registro de amigo guardado con éxito.");
        } else {
            mostrarMsg("Error: " + respuesta);
        }
    }

    private void mostrarMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
