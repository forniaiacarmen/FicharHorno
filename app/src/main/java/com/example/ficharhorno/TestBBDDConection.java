package com.example.ficharhorno;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestBBDDConection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Intentar conectar a la base de datos al iniciar la aplicación
        new TestDatabaseConnectionTask().execute();
    }

    /**
     * Método que lee el archivo JSON de configuración y prueba la conexión a la base de datos.
     */
    private class TestDatabaseConnectionTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Intentar leer la configuración de la base de datos desde el archivo JSON
            JSONObject dbConfig = readDatabaseConfigFromJson();

            if (dbConfig != null) {
                String url = dbConfig.optString("db_url");
                int port = dbConfig.optInt("port");
                String user = dbConfig.optString("db_user");
                String password = dbConfig.optString("db_password");

                // Intentar realizar la conexión a la base de datos
                return testDatabaseConnection(url, port, user, password);
            }
            return false;  // Retorna false si no se pudo leer la configuración
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(TestBBDDConection.this, "Conexión a la base de datos exitosa!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TestBBDDConection.this, "Error al conectar a la base de datos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método que lee el archivo JSON de configuración de la base de datos.
     *
     * @return un JSONObject con los datos de conexión.
     */
    private JSONObject readDatabaseConfigFromJson() {
        FileInputStream fis = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            fis = openFileInput("db_config.json");
            reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return new JSONObject(stringBuilder.toString());  // Parsear el contenido del archivo a un JSONObject
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;  // Retorna null si no se pudo leer el archivo
    }

    /**
     * Método para probar la conexión a la base de datos utilizando JDBC.
     *
     * @param url      La URL de la base de datos.
     * @param port     El puerto del servidor de la base de datos.
     * @param user     El nombre de usuario para la base de datos.
     * @param password La contraseña para la base de datos.
     * @return true si la conexión es exitosa, false si falla.
     */
    private boolean testDatabaseConnection(String url, int port, String user, String password) {
        String fullUrl = url + ":" + port;
        Connection connection = null;

        try {
            // Asegúrate de tener el driver JDBC en tu proyecto
            // Class.forName("org.mariadb.jdbc.Driver"); // Si utilizas MariaDB
            connection = DriverManager.getConnection(fullUrl, user, password);
            if (connection != null && !connection.isClosed()) {
                Log.d("DB Connection", "Conexión exitosa a la base de datos.");
                return true;  // Conexión exitosa
            }
        } catch (SQLException e) {
            Log.e("DB Connection", "Error al conectar a la base de datos: " + e.getMessage());
            String a = String.valueOf(e);
            Toast.makeText(TestBBDDConection.this, a, Toast.LENGTH_SHORT).show();


        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();  // Cerrar la conexión
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;  // Conexión fallida
    }
}
