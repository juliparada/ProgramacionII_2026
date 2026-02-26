package com.example.myaplicacion;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText txtNum1, txtNum2;
    TextView lblRespuesta;
    RadioGroup optOpciones;
    Button btnCalcular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNum1 = findViewById(R.id.txtNum1);
        txtNum2 = findViewById(R.id.txtNum2);
        lblRespuesta = findViewById(R.id.lblRespuesta);
        optOpciones = findViewById(R.id.optOpciones);
        btnCalcular = findViewById(R.id.btnCalcular);

        btnCalcular.setOnClickListener(v -> calcular());

        optOpciones.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.optRaiz || checkedId == R.id.optFactorial) {
                txtNum2.setVisibility(View.GONE);
                txtNum2.setText("");
            } else {
                txtNum2.setVisibility(View.VISIBLE);
            }
        });
    }

    private void calcular() {
        String num1Str = txtNum1.getText().toString();
        String num2Str = txtNum2.getText().toString();

        double num1 = num1Str.isEmpty() ? 0 : Double.parseDouble(num1Str);
        double num2 = num2Str.isEmpty() ? 0 : Double.parseDouble(num2Str);

        int selectedId = optOpciones.getCheckedRadioButtonId();
        double resultado = 0;
        boolean error = false;

        if (selectedId == R.id.optSuma) {
            resultado = num1 + num2;
        } else if (selectedId == R.id.optResta) {
            resultado = num1 - num2;
        } else if (selectedId == R.id.optMultiplicacion) {
            resultado = num1 * num2;
        } else if (selectedId == R.id.optDivision) {
            if (num2 != 0) {
                resultado = num1 / num2;
            } else {
                lblRespuesta.setText("Error: división entre 0");
                error = true;
            }
        } else if (selectedId == R.id.optFactorial) {
            if (num1 < 0 || num1 != (long)num1) {
                lblRespuesta.setText("Error: Factorial de negativo o no entero");
                error = true;
            } else if (num1 > 20) {
                lblRespuesta.setText("Error: Factorial de número muy grande");
                error = true;
            } else {
                resultado = factorial((int) num1);
            }
        } else if (selectedId == R.id.optPorcentaje) {
            resultado = (num1 * num2) / 100;
        } else if (selectedId == R.id.optExponenciacion) {
            resultado = Math.pow(num1, num2);
        } else if (selectedId == R.id.optRaiz) {
            if (num1 >= 0) {
                resultado = Math.sqrt(num1);
            } else {
                lblRespuesta.setText("Error: raíz negativa");
                error = true;
            }
        }

        if (!error) {
            if (resultado % 1 == 0) {
                lblRespuesta.setText("Respuesta: " + (long) resultado);
            } else {
                lblRespuesta.setText("Respuesta: " + resultado);
            }
        }
    }

    private long factorial(int n) {
        if (n == 0) return 1;
        long fact = 1;
        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
}
