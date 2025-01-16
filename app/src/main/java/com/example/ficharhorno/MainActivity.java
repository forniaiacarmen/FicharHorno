package com.example.ficharhorno;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String WIFI_NAME = "MIWIFI_04D8"; // Nombre del WiFi esperado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            // Continúa con el resto de la funcionalidad de la aplicación aquí
        }
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
}
