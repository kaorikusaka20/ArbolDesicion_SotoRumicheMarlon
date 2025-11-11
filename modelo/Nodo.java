package modelo;

import java.util.*;

public class Nodo {
    private String tipo; // "nodo" o "hoja"
    private String atributo;
    private String clase;
    private double ganancia;
    private double entropia;
    private int count;
    private int profundidad;
    private Map<String, Nodo> hijos;
    
    public Nodo(String tipo) {
        this.tipo = tipo;
        this.hijos = new LinkedHashMap<>();
    }
    
    // Getters y Setters
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getAtributo() { return atributo; }
    public void setAtributo(String atributo) { this.atributo = atributo; }
    
    public String getClase() { return clase; }
    public void setClase(String clase) { this.clase = clase; }
    
    public double getGanancia() { return ganancia; }
    public void setGanancia(double ganancia) { this.ganancia = ganancia; }
    
    public double getEntropia() { return entropia; }
    public void setEntropia(double entropia) { this.entropia = entropia; }
    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    
    public int getProfundidad() { return profundidad; }
    public void setProfundidad(int profundidad) { this.profundidad = profundidad; }
    
    public Map<String, Nodo> getHijos() { return hijos; }
    public void agregarHijo(String valor, Nodo hijo) { hijos.put(valor, hijo); }
    
    public boolean esHoja() { return tipo.equals("hoja"); }
}
