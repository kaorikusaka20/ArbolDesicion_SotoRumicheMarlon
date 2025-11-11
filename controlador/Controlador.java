package modelo;

import java.util.*;

public class DataSet {
    private List<Map<String, String>> datos;
    private String atributoObjetivo;
    private List<String> atributos;
    
    public DataSet(String atributoObjetivo) {
        this.datos = new ArrayList<>();
        this.atributoObjetivo = atributoObjetivo;
        this.atributos = new ArrayList<>();
        cargarDatosIniciales();
    }
    
    private void cargarDatosIniciales() {
        agregarFila("Lluvioso", "Caliente", "Alta", "False", "No");
        agregarFila("Lluvioso", "Caliente", "Alta", "True", "No");
        agregarFila("Nublado", "Caliente", "Alta", "False", "Si");
        agregarFila("Soleado", "Templado", "Alta", "False", "Si");
        agregarFila("Soleado", "Frio", "Normal", "False", "Si");
        agregarFila("Soleado", "Frio", "Normal", "True", "No");
        agregarFila("Nublado", "Frio", "Normal", "True", "Si");
        agregarFila("Lluvioso", "Templado", "Alta", "False", "No");
        agregarFila("Lluvioso", "Frio", "Normal", "False", "Si");
        agregarFila("Soleado", "Templado", "Normal", "False", "Si");
        agregarFila("Lluvioso", "Templado", "Normal", "True", "Si");
        agregarFila("Nublado", "Templado", "Alta", "True", "Si");
        agregarFila("Nublado", "Caliente", "Normal", "False", "Si");
        agregarFila("Soleado", "Templado", "Alta", "True", "No");
        
        if (!datos.isEmpty()) {
            atributos = new ArrayList<>(datos.get(0).keySet());
            atributos.remove(atributoObjetivo);
        }
    }
    
    public void agregarFila(String clima, String temperatura, String humedad, String vientos, String pasear) {
        Map<String, String> fila = new LinkedHashMap<>();
        fila.put("Clima", clima);
        fila.put("Temperatura", temperatura);
        fila.put("Humedad", humedad);
        fila.put("Vientos", vientos);
        fila.put("Pasear", pasear);
        datos.add(fila);
        
        if (atributos.isEmpty() && !datos.isEmpty()) {
            atributos = new ArrayList<>(fila.keySet());
            atributos.remove(atributoObjetivo);
        }
    }
    
    public void editarFila(int index, String clima, String temperatura, String humedad, String vientos, String pasear) {
        if (index >= 0 && index < datos.size()) {
            Map<String, String> fila = datos.get(index);
            fila.put("Clima", clima);
            fila.put("Temperatura", temperatura);
            fila.put("Humedad", humedad);
            fila.put("Vientos", vientos);
            fila.put("Pasear", pasear);
        }
    }
    
    public void eliminarFila(int index) {
        if (index >= 0 && index < datos.size()) {
            datos.remove(index);
        }
    }
    
    public void limpiarDatos() {
    datos.clear();
    atributos.clear();
}

    public void agregarFilaGenerica(Map<String, String> fila) {
        datos.add(new LinkedHashMap<>(fila));

        if (atributos.isEmpty() && !datos.isEmpty()) {
            atributos = new ArrayList<>(fila.keySet());
            atributos.remove(atributoObjetivo);
        }
    }

    public void setAtributoObjetivo(String atributoObjetivo) {
        this.atributoObjetivo = atributoObjetivo;

        if (!datos.isEmpty()) {
            atributos = new ArrayList<>(datos.get(0).keySet());
            atributos.remove(this.atributoObjetivo);
        }
    }
    
    public List<Map<String, String>> getDatos() {
        return new ArrayList<>(datos);
    }
    
    public String getAtributoObjetivo() {
        return atributoObjetivo;
    }
    
    public List<String> getAtributos() {
        return new ArrayList<>(atributos);
    }
    
    public int size() {
        return datos.size();
    }
}
