package vista;

import controlador.Controlador;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class PanelAnalisis extends JPanel {
    private Controlador controlador;
    private JTextArea areaAnalisis;
    private JButton btnAnalizar;
    
    public PanelAnalisis(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        // Panel superior con botón
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnAnalizar = new JButton("EJECUTAR ANÁLISIS ID3");
        btnAnalizar.setFont(new Font("Arial", Font.BOLD, 16));
        btnAnalizar.setPreferredSize(new Dimension(300, 40));
        btnAnalizar.addActionListener(e -> ejecutarAnalisis());
        panelSuperior.add(btnAnalizar);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Área de texto para mostrar resultados
        areaAnalisis = new JTextArea();
        areaAnalisis.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaAnalisis.setEditable(false);
        areaAnalisis.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(areaAnalisis);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void ejecutarAnalisis() {
        // 1. OBTENER DATOS (Esto funciona bien)
        Map<String, Object> analisis = controlador.ejecutarAnalisis();
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("           ANÁLISIS INICIAL - ALGORITMO ID3\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");
        
        int total = (int) analisis.get("total");
        @SuppressWarnings("unchecked")
        Map<String, Integer> conteos = (Map<String, Integer>) analisis.get("conteos");
        double entropia = (double) analisis.get("entropia");
        
        // 2. MOSTRAR ENTROPÍA TOTAL (Esto funciona bien)
        sb.append(String.format("Dataset: Total de ejemplos = %d\n", total));
        
        // --- CAMBIO SUTIL: Obtener el nombre del atributo objetivo dinámicamente ---
        String atributoObjetivo = controlador.obtenerAtributoObjetivo();
        if (atributoObjetivo == null) atributoObjetivo = "Objetivo";
        // ------------------------------------------------------------------------
        
        sb.append(String.format("Atributo Objetivo: %s\n", atributoObjetivo));
        
        for (Map.Entry<String, Integer> entry : conteos.entrySet()) {
            sb.append(String.format("  - Clase %s: %d ejemplos\n", entry.getKey(), entry.getValue()));
        }
        
        sb.append("\nENTROPÍA DEL SISTEMA\n");
        sb.append("───────────────────────────────────────────────────────────\n");
        sb.append(String.format("E(%s) = E[%s] = %.5f\n\n", 
            atributoObjetivo, formatearConteos(conteos), entropia));
        
        sb.append("ANÁLISIS DE ATRIBUTOS - PRIMER NIVEL\n");
        sb.append("───────────────────────────────────────────────────────────\n\n");
        
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> atributos = (java.util.List<Map<String, Object>>) analisis.get("atributos");
        
        // 3. MOSTRAR ANÁLISIS DE ATRIBUTOS (Aquí estaba el error)
        for (Map<String, Object> attr : atributos) {
            String nombreAttr = (String) attr.get("atributo");
            double ganancia = (double) attr.get("ganancia");
            double entropiaCondicional = (double) attr.get("entropia");
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Integer>> grupos = (Map<String, Map<String, Integer>>) attr.get("grupos");
            
            sb.append(String.format("Atributo: %s\n", nombreAttr));
            
            // --- INICIO DE LA CORRECCIÓN ---
            for (Map.Entry<String, Map<String, Integer>> grupo : grupos.entrySet()) {
                String valor = grupo.getKey();
                Map<String, Integer> conteosGrupo = grupo.getValue();
                
                // Ya no buscamos "Si" y "No"
                // Pasamos el map completo al método de entropía corregido
                double entropiaGrupo = calcularEntropiaGrupoDinamico(conteosGrupo);
                
                // Construimos la cadena de conteos dinámicamente (ej. [Alta=10, UCI=5])
                StringBuilder conteosStr = new StringBuilder("[");
                boolean primero = true;
                for (Map.Entry<String, Integer> entry : conteosGrupo.entrySet()) {
                    if (!primero) conteosStr.append(", ");
                    conteosStr.append(String.format("%s=%d", entry.getKey(), entry.getValue()));
                    primero = false;
                }
                conteosStr.append("]");
                
                // Imprimimos el formato corregido
                sb.append(String.format("  %-15s: %s, E=%.5f\n", 
                    valor, conteosStr.toString(), entropiaGrupo));
            }
            // --- FIN DE LA CORRECCIÓN ---
            
            sb.append(String.format("  E(%s, %s) = %.5f\n", atributoObjetivo, nombreAttr, entropiaCondicional));
            sb.append(String.format("  Ganancia(%s) = %.5f\n\n", nombreAttr, ganancia));
        }
        
        areaAnalisis.setText(sb.toString());
    }
    
    // Este método está bien, se usa para E[X,Y,Z]
    private String formatearConteos(Map<String, Integer> conteos) {
        StringBuilder sb = new StringBuilder();
        boolean primero = true;
        for (Integer count : conteos.values()) {
            if (!primero) sb.append(",");
            sb.append(count);
            primero = false;
        }
        return sb.toString();
    }
    
    // --- MÉTODO CORREGIDO ---
    // Renombrado para claridad. Ahora calcula la entropía
    // para cualquier número de clases, no solo "Si" y "No".
    private double calcularEntropiaGrupoDinamico(Map<String, Integer> conteosGrupo) {
        int total = 0;
        for (int count : conteosGrupo.values()) {
            total += count;
        }

        if (total == 0) return 0.0;
        
        double entropia = 0.0;
        for (int count : conteosGrupo.values()) {
            if (count > 0) {
                double p = (double) count / total;
                entropia -= p * (Math.log(p) / Math.log(2));
            }
        }
        
        return entropia;
    }
    
    // El método anterior (incorrecto) se elimina o se reemplaza.
    // private double calcularEntropiaGrupo(int no, int si) { ... }
    
    public void limpiar() {
        areaAnalisis.setText("");
    }
}
