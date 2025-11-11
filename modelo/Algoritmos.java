package modelo;

import java.util.*;

public class Algoritmos {
    private DataSet dataSet;
    private String atributoObjetivo;
    
    public Algoritmos(DataSet dataSet) {
        this.dataSet = dataSet;
        this.atributoObjetivo = dataSet.getAtributoObjetivo();
    }
    
    // Calcular entropía
    public double calcularEntropia(List<Map<String, String>> datos) {
        if (datos.isEmpty()) return 0.0;
        
        Map<String, Integer> conteos = new HashMap<>();
        for (Map<String, String> fila : datos) {
            String valor = fila.get(atributoObjetivo);
            conteos.put(valor, conteos.getOrDefault(valor, 0) + 1);
        }
        
        double entropia = 0.0;
        int total = datos.size();
        
        for (int count : conteos.values()) {
            double p = (double) count / total;
            if (p > 0) {
                entropia -= p * (Math.log(p) / Math.log(2));
            }
        }
        
        return entropia;
    }
    
    // Calcular entropía condicional
    public double calcularEntropiaCondicional(List<Map<String, String>> datos, String atributo) {
        Map<String, List<Map<String, String>>> grupos = new HashMap<>();
        
        for (Map<String, String> fila : datos) {
            String valor = fila.get(atributo);
            grupos.computeIfAbsent(valor, k -> new ArrayList<>()).add(fila);
        }
        
        double entropiaCondicional = 0.0;
        int total = datos.size();
        
        for (List<Map<String, String>> grupo : grupos.values()) {
            double peso = (double) grupo.size() / total;
            entropiaCondicional += peso * calcularEntropia(grupo);
        }
        
        return entropiaCondicional;
    }
    
    // Calcular ganancia de información
    public double calcularGanancia(List<Map<String, String>> datos, String atributo) {
        return calcularEntropia(datos) - calcularEntropiaCondicional(datos, atributo);
    }
    
    // Obtener el mejor atributo
    public Map<String, Object> mejorAtributo(List<Map<String, String>> datos, List<String> atributos) {
        String mejorAttr = null;
        double maxGanancia = -1;
        
        for (String attr : atributos) {
            double ganancia = calcularGanancia(datos, attr);
            if (ganancia > maxGanancia) {
                maxGanancia = ganancia;
                mejorAttr = attr;
            }
        }
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("atributo", mejorAttr);
        resultado.put("ganancia", maxGanancia);
        return resultado;
    }
    
    // Construir árbol ID3
    public Nodo construirArbol(List<Map<String, String>> datos, List<String> atributos, int profundidad) {
        // Caso base: todos tienen la misma clase
        Set<String> clases = new HashSet<>();
        for (Map<String, String> fila : datos) {
            clases.add(fila.get(atributoObjetivo));
        }
        
        if (clases.size() == 1) {
            Nodo hoja = new Nodo("hoja");
            hoja.setClase(clases.iterator().next());
            hoja.setCount(datos.size());
            return hoja;
        }
        
        // Caso base: no hay más atributos
        if (atributos.isEmpty()) {
            Map<String, Integer> conteos = new HashMap<>();
            for (Map<String, String> fila : datos) {
                String clase = fila.get(atributoObjetivo);
                conteos.put(clase, conteos.getOrDefault(clase, 0) + 1);
            }
            
            String claseMayoritaria = Collections.max(conteos.entrySet(), Map.Entry.comparingByValue()).getKey();
            Nodo hoja = new Nodo("hoja");
            hoja.setClase(claseMayoritaria);
            hoja.setCount(datos.size());
            return hoja;
        }
        
        // Encontrar mejor atributo
        Map<String, Object> mejor = mejorAtributo(datos, atributos);
        String atributo = (String) mejor.get("atributo");
        double ganancia = (double) mejor.get("ganancia");
        
        Nodo nodo = new Nodo("nodo");
        nodo.setAtributo(atributo);
        nodo.setGanancia(ganancia);
        nodo.setEntropia(calcularEntropia(datos));
        nodo.setCount(datos.size());
        nodo.setProfundidad(profundidad);
        
        // Particionar datos
        Map<String, List<Map<String, String>>> grupos = new HashMap<>();
        for (Map<String, String> fila : datos) {
            String valor = fila.get(atributo);
            grupos.computeIfAbsent(valor, k -> new ArrayList<>()).add(fila);
        }
        
        List<String> atributosRestantes = new ArrayList<>(atributos);
        atributosRestantes.remove(atributo);
        
        for (Map.Entry<String, List<Map<String, String>>> entry : grupos.entrySet()) {
            Nodo hijo = construirArbol(entry.getValue(), atributosRestantes, profundidad + 1);
            nodo.agregarHijo(entry.getKey(), hijo);
        }
        
        return nodo;
    }
    
    // Análisis completo del dataset
    public Map<String, Object> analizar() {
        List<Map<String, String>> datos = dataSet.getDatos();
        
        Map<String, Integer> conteos = new HashMap<>();
        for (Map<String, String> fila : datos) {
            String valor = fila.get(atributoObjetivo);
            conteos.put(valor, conteos.getOrDefault(valor, 0) + 1);
        }
        
        double entropiaTotal = calcularEntropia(datos);
        
        List<Map<String, Object>> analisisAtributos = new ArrayList<>();
        for (String attr : dataSet.getAtributos()) {
            Map<String, Map<String, Integer>> grupos = new HashMap<>();
            
            for (Map<String, String> fila : datos) {
                String valor = fila.get(attr);
                String target = fila.get(atributoObjetivo);
                
                grupos.computeIfAbsent(valor, k -> new HashMap<>());
                grupos.get(valor).put(target, grupos.get(valor).getOrDefault(target, 0) + 1);
            }
            
            double ganancia = calcularGanancia(datos, attr);
            double entropiaCondicional = calcularEntropiaCondicional(datos, attr);
            
            Map<String, Object> analisis = new HashMap<>();
            analisis.put("atributo", attr);
            analisis.put("grupos", grupos);
            analisis.put("ganancia", ganancia);
            analisis.put("entropia", entropiaCondicional);
            
            analisisAtributos.add(analisis);
        }
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("total", datos.size());
        resultado.put("conteos", conteos);
        resultado.put("entropia", entropiaTotal);
        resultado.put("atributos", analisisAtributos);
        
        return resultado;
    }
}
