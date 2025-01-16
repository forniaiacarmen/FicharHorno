package com.example.ficharhorno;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestBBDDConection extends AppCompatActivity {

    // Datos de conexión a la base de datos directamente en la clase
    private static final String DB_URL = "jdbc:mariadb://192.168.1.136:3306/fichar";  // Asegúrate de que la URL sea correcta
    private static final String DB_USER = "empleado";
    private static final String DB_PASSWORD = "pepepepe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Intentar conectar a la base de datos al iniciar la aplicación
        new TestDatabaseConnectionTask().execute();  // Ejecutamos la tarea asincrona
    }

    /**
     * Clase Asincrona para probar la conexión con la base de datos.
     */
    private class TestDatabaseConnectionTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Primero, verifica si el driver está cargado
            if (!isDriverLoaded()) {
                return false; // Si el driver no está cargado, no intentamos conectar
            }

            // Si el driver está cargado, procedemos a intentar la conexión
            return testDatabaseConnection(DB_URL, DB_USER, DB_PASSWORD);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // Llamado al hilo principal después de que la tarea en segundo plano haya terminado
            if (result) {
                Toast.makeText(TestBBDDConection.this, "Conexión a la base de datos exitosa!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TestBBDDConection.this, "Error al conectar a la base de datos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Verifica si el driver JDBC está cargado correctamente.
     *
     * @return true si el driver está cargado, false si no lo está.
     */
    private boolean isDriverLoaded() {
        try {
            // Intentar cargar el driver explícitamente
            Class.forName("org.mariadb.jdbc.Driver");  // Para MariaDB
            Log.d("DB Connection", "Driver cargado correctamente.");
            return true;
        } catch (ClassNotFoundException e) {
            Log.e("DB Connection", "Error al cargar el driver: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método para probar la conexión a la base de datos utilizando JDBC.
     *
     * @param dbUrl   La URL de la base de datos.
     * @param user    El nombre de usuario para la base de datos.
     * @param password La contraseña para la base de datos.
     * @return true si la conexión es exitosa, false si falla.
     */
    private boolean testDatabaseConnection(String dbUrl, String user, String password) {
        Connection connection = null;

        try {
            // Intentar realizar la conexión a la base de datos usando los datos directamente en la clase
            connection = DriverManager.getConnection(dbUrl, user, password);
            if (connection != null && !connection.isClosed()) {
                Log.d("DB Connection", "Conexión exitosa a la base de datos.");
                return true;  // Conexión exitosa
            }
        } catch (SQLException e) {
            Log.e("DB Connection", "Error al conectar a la base de datos: " + e.getMessage());
            String errorMessage = "Error: " + e.getMessage();
            // Mostrar el error específico en un Toast
            Toast.makeText(TestBBDDConection.this, errorMessage, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // Captura otros errores generales
            Log.e("DB Connection", "Error inesperado: " + e.getMessage());
            Toast.makeText(TestBBDDConection.this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();  // Cerrar la conexión
                }
            } catch (SQLException e) {
                Log.e("DB Connection", "Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return false;  // Conexión fallida
    }
}
