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

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    DB db;
    Button btn;
    TextView tempVal;
    String accion = "nuevo", idAmigo = "", urlFoto = "", id = "", rev = "";
    Intent tomarFotoIntent;
    FloatingActionButton fab;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.imgFotoAmigo);
        img.setOnClickListener(v -> tomarFoto());

        db = new DB(this);

        btn = findViewById(R.id.btnGuardarAmigo);
        btn.setOnClickListener(v -> guardarAmigo());

        fab = findViewById(R.id.fabListaAmigo);
        fab.setOnClickListener(v -> regresarListaAmigos());

        mostrarDatosAmigos();
    }

    private void mostrarDatosAmigos() {
        try {
            Bundle parametros = getIntent().getExtras();
            if (parametros != null) {
                accion = parametros.getString("accion", "nuevo");
                if ("modificar".equals(accion)) {
                    JSONObject datos = new JSONObject(parametros.getString("amigos"));
                    id = datos.optString("_id", "");
                    rev = datos.optString("_rev", "");
                    idAmigo = datos.optString("idAmigo", "");

                    tempVal = findViewById(R.id.txtNombreAmigos);
                    tempVal.setText(datos.optString("nombre", ""));

                    tempVal = findViewById(R.id.txtDireccionAmigos);
                    tempVal.setText(datos.optString("direccion", ""));

                    tempVal = findViewById(R.id.txtTelefonoAmigos);
                    tempVal.setText(datos.optString("telefono", ""));

                    tempVal = findViewById(R.id.txtEmailAmigos);
                    tempVal.setText(datos.optString("email", ""));

                    tempVal = findViewById(R.id.txtDuiAmigos);
                    tempVal.setText(datos.optString("dui", ""));

                    urlFoto = datos.optString("foto", "");
                    if (!urlFoto.isEmpty()) {
                        img.setImageURI(Uri.parse(urlFoto));
                    }
                }
            }
        } catch (Exception e) {
            mostrarMsg("Error al mostrar los datos: " + e.getMessage());
        }
    }

    private void tomarFoto() {
        tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fotoAmigo = null;

        try {
            fotoAmigo = crearImgAmigo();
            if (fotoAmigo != null) {
                Uri uriFoto = FileProvider.getUriForFile(MainActivity.this, "com.example.amigossqlite.fileprovider", fotoAmigo);
                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
                startActivityForResult(tomarFotoIntent, 1);
            } else {
                mostrarMsg("No se pudo crear el archivo para la foto");
            }
        } catch (Exception e) {
            mostrarMsg("Error al tomar la foto: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                img.setImageURI(Uri.parse(urlFoto));
            } else {
                mostrarMsg("No fue posible capturar la foto");
            }
        } catch (Exception e) {
            mostrarMsg("Error al procesar la foto: " + e.getMessage());
        }
    }

    private File crearImgAmigo() throws Exception {
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "foto_" + fechaHoraMs;
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if (dirAlmacenamiento != null && !dirAlmacenamiento.exists()) {
            dirAlmacenamiento.mkdirs();
        }
        File image = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        urlFoto = image.getAbsolutePath();
        return image;
    }

    private void guardarAmigo() {
        try {
            String nombre = ((TextView) findViewById(R.id.txtNombreAmigos)).getText().toString();
            String direccion = ((TextView) findViewById(R.id.txtDireccionAmigos)).getText().toString();
            String tel = ((TextView) findViewById(R.id.txtTelefonoAmigos)).getText().toString();
            String email = ((TextView) findViewById(R.id.txtEmailAmigos)).getText().toString();
            String dui = ((TextView) findViewById(R.id.txtDuiAmigos)).getText().toString();

            // Guardar datos en SQLite
            String[] datos = {idAmigo, nombre, direccion, tel, email, dui, urlFoto};
            String respuestaLocal = db.administrar_amigos(accion, datos);

            if (!"ok".equals(respuestaLocal)) {
                mostrarMsg("Error en SQLite: " + respuestaLocal);
                return;
            }

            // Guardar datos en CouchDB
            JSONObject datosAmigos = new JSONObject();
            if ("modificar".equals(accion)) {
                datosAmigos.put("_id", id);
                datosAmigos.put("_rev", rev);
            }
            datosAmigos.put("idAmigo", idAmigo);
            datosAmigos.put("nombre", nombre);
            datosAmigos.put("direccion", direccion);
            datosAmigos.put("telefono", tel);
            datosAmigos.put("email", email);
            datosAmigos.put("dui", dui);
            datosAmigos.put("foto", urlFoto);

            enviarDatosServidor objEnviarDatosServidor = new enviarDatosServidor(this);
            String respuesta = objEnviarDatosServidor.execute(datosAmigos.toString(), "POST", utilidades.url_mto).get();

            JSONObject respuestaJSON = new JSONObject(respuesta);
            if (respuestaJSON.optBoolean("ok", false)) {
                id = respuestaJSON.getString("id");
                rev = respuestaJSON.getString("rev");
                mostrarMsg("Registro guardado con éxito.");
            } else {
                mostrarMsg("Error en el servidor: " + respuestaJSON.optString("reason", "Error desconocido"));
            }
            regresarListaAmigos();
        } catch (Exception e) {
            mostrarMsg("Error al guardar: " + e.getMessage());
        }
    }

    private void mostrarMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void regresarListaAmigos() {
        Intent intent = new Intent(this, lista_amigos.class);
        startActivity(intent);
        finish();
    }
}
