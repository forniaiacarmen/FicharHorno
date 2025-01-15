package com.example.ficharhorno;

import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String WIFI_NAME = "wifiempresa";
    private static final String PREFS_NAME = "FichajePrefs";
    private static final String USERNAME_KEY = "username";

    private EditText editTextUsername;
    private Button btnFichar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        editTextUsername = findViewById(R.id.editTextUsername);
        btnFichar = findViewById(R.id.btnFichar);

        // Verificar si estamos conectados a la red WiFi "wifiempresa"
        if (!isConnectedToWifi(WIFI_NAME)) {
            Toast.makeText(this, "No estás conectado a la red wifiempresa.", Toast.LENGTH_LONG).show();
            return;
        }

        // Cargar el nombre del usuario si está guardado
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(USERNAME_KEY, null);

        if (savedUsername != null) {
            // Si ya está guardado, no preguntar por el nombre
            editTextUsername.setText(savedUsername);
            editTextUsername.setEnabled(false); // Deshabilitar el campo
        }

        // Configurar el botón de fichaje
        btnFichar.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            System.out.println(username);
            if (username.isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor, introduce tu nombre.", Toast.LENGTH_SHORT).show();
            } else {
                // Guardar el nombre del usuario en SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(USERNAME_KEY, username);
                editor.apply();

                // Mostrar mensaje de fichaje registrado
                Toast.makeText(MainActivity.this, "Fichaje registrado para: " + username, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Función para verificar si estamos conectados a la red WiFi correcta
    private boolean isConnectedToWifi(String wifiName) {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String connectedNetworkName = wifiInfo.getSSID().replaceAll("\"", ""); // Elimina las comillas si las tiene
        return connectedNetworkName.equals(wifiName);
    }
}