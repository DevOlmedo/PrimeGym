package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Producto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivos {
    private static final String RUTA = "productos.json";

    // Configura GSON para que el archivo sea lindo (PrettyPrinting)

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // Método para guardar la lista

    public static void guardar(List<Producto> lista) {
        try (FileWriter writer = new FileWriter(RUTA)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para cargar la lista

    public static List<Producto> cargar() {
        File file = new File(RUTA);
        if (!file.exists()) return new ArrayList<>();

        try (FileReader reader = new FileReader(RUTA)) {

            // TypeToken ayuda a GSON a entender que es una Lista de Productos

            return gson.fromJson(reader, new TypeToken<List<Producto>>(){}.getType());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}