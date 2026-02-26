package com.example.myaplicacion;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText txtNum1, txtNum2;
    TextView lblRespuesta;
    Spinner spnOpciones;
    Button btnCalcular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNum1 = findViewById(R.id.txtNum1);
        txtNum2 = findViewById(R.id.txtNum2);
        lblRespuesta = findViewById(R.id.lblRespuesta);
        spnOpciones = findViewById(R.id.cboOpciones);
        btnCalcular = findViewById(R.id.btnCalcular);

        btnCalcular.setOnClickListener(v -> calcular());
    }

    private void calcular() {
        String num1Str = txtNum1.getText().toString();
        String num2Str = txtNum2.getText().toString();

        if (num1Str.isEmpty()) {
            lblRespuesta.setText("Ingresa el primer número");
            return;
        }

        double num1 = Double.parseDouble(num1Str);
        double num2 = num2Str.isEmpty() ? 0 : Double.parseDouble(num2Str);

        double respuesta = 0;

        switch (spnOpciones.getSelectedItemPosition()) {
            case 0: // Suma
                respuesta = num1 + num2;
                break;
            case 1: // Resta
                respuesta = num1 - num2;
                break;
            case 2: // Multiplicación
                respuesta = num1 * num2;
                break;
            case 3: // División
                if (num2 != 0) {
                    respuesta = num1 / num2;
                } else {
                    lblRespuesta.setText("Error: división entre 0");
                    return;
                }
                break;
            case 4: // Factorial
                respuesta = factorial((int) num1);
                break;
            case 5: // Porcentaje
                respuesta = (num1 * num2) / 100;
                break;
            case 6: // Exponenciación
                respuesta = Math.pow(num1, num2);
                break;
            case 7: // Raíz
                if (num1 >= 0) {
                    respuesta = Math.sqrt(num1);
                } else {
                    lblRespuesta.setText("Error: raíz negativa");
                    return;
                }
                break;
        }

        lblRespuesta.setText("Respuesta: " + respuesta);
    }

    private int factorial(int n) {
        if (n < 0) return 0;
        int fact = 1;
        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
}