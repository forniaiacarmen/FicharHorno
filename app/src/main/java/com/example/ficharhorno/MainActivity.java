package com.example.ficharhorno;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

public class MainActivity extends AppCompatActivity {

    private static final String WIFI_NAME = "MIWIFI_04D8"; // Nombre del WiFi esperado
    private EditText editTextUsername;
    private Button btnFichar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        editTextUsername = findViewById(R.id.editTextUsername);
        btnFichar = findViewById(R.id.btnFichar);

        // Leer el archivo JSON al iniciar
        readUsernameFromJson();

        // Verificar permisos de ubicación
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Verificar si los servicios de ubicación están habilitados
        if (!isLocationEnabled()) {
            showToast("Por favor, habilita los servicios de ubicación para usar esta aplicación.");
            finish(); // Cierra la aplicación si no están habilitados
            return;
        }

        // Verificar conexión a la red WiFi
        if (!isConnectedToCorrectWifi()) {
            showToast("Debes estar conectado a la red " + WIFI_NAME + " para usar esta aplicación.");
            finish(); // Cierra la aplicación si no está conectado al WiFi correcto
        } else {
            showToast("Conectado a la red " + WIFI_NAME + ". Puede proceder.");
        }

        btnFichar.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();

            // Validar si el nombre está vacío
            if (username.isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor, introduce tu nombre.", Toast.LENGTH_SHORT).show();
            } else {
                // Guardar el nombre en formato JSON
                saveUsernameToJson(username);
                // Mostrar mensaje de éxito
                Toast.makeText(MainActivity.this, "Fichaje registrado para: " + username, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Verifica si el dispositivo está conectado al WiFi correcto
     *
     * @return true si está conectado a la red especificada, false en caso contrario
     */
    private boolean isConnectedToCorrectWifi() {
        String connectedNetworkName = getConnectedWifiSSID();
        System.out.println("SSID obtenido: " + connectedNetworkName);
        return connectedNetworkName.equals(WIFI_NAME);
    }

    /**
     * Obtiene el SSID de la red WiFi actualmente conectada
     *
     * @return El nombre del SSID o "<unknown ssid>" si no está disponible
     */
    private String getConnectedWifiSSID() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo != null && wifiInfo.getNetworkId() != -1) {
            return wifiInfo.getSSID().replaceAll("\"", ""); // Elimina comillas del SSID si existen
        }
        return "<unknown ssid>";
    }

    /**
     * Verifica si los servicios de ubicación están habilitados
     *
     * @return true si están habilitados, false en caso contrario
     */
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Muestra un mensaje Toast
     *
     * @param message Mensaje a mostrar
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Lee el archivo JSON y saluda al usuario
     */
    private void readUsernameFromJson() {
        File file = new File(getFilesDir(), "user_data.json");
        if (file.exists()) {
            try {
                // Abrir el archivo en modo lectura
                FileInputStream fis = openFileInput("user_data.json");
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader reader = new BufferedReader(inputStreamReader);

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                reader.close();
                // Convertir el contenido a JSON y leer el nombre
                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                String username = jsonObject.optString("user", "Usuario");

                // Saludar al usuario con un Toast
                Toast.makeText(this, "¡Bienvenido, " + username + "!", Toast.LENGTH_SHORT).show();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al leer el archivo JSON", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("JSON", "No se encontró el archivo de usuario.");
        }
    }

    /**
     * Guarda el nombre del usuario en un archivo JSON
     *
     * @param username Nombre del usuario
     */
    private void saveUsernameToJson(String username) {
        JSONObject jsonObject = new JSONObject();
        try {
            // Guardar el nombre del usuario en el JSON
            jsonObject.put("user", username);

            // Abrir el archivo en modo escritura
            FileOutputStream fos = openFileOutput("user_data.json", MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);

            // Escribir el JSON en el archivo
            writer.write(jsonObject.toString());
            writer.close();

            // Agregar un log para confirmar que el archivo se ha guardado correctamente
            Log.d("File Save", "El nombre se ha guardado correctamente en user_data.json");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar el fichaje: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
