package vista;

import controlador.Controlador;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;

public class PanelCSV extends JPanel {
    private Controlador controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnCargarCSV, btnVerDatos;
    private JLabel lblEstado;
    private boolean csvCargado = false;
    
    public PanelCSV(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        
        JLabel titulo = new JLabel("CARGAR DATASET DESDE CSV", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelSuperior.add(titulo, BorderLayout.NORTH);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnCargarCSV = new JButton("Cargar Archivo CSV");
        btnCargarCSV.setFont(new Font("Arial", Font.BOLD, 14));
        btnCargarCSV.setPreferredSize(new Dimension(250, 40));
        btnCargarCSV.addActionListener(e -> cargarCSV());
        
        btnVerDatos = new JButton("Ver Datos Actuales");
        btnVerDatos.setFont(new Font("Arial", Font.BOLD, 14));
        btnVerDatos.setPreferredSize(new Dimension(250, 40));
        btnVerDatos.addActionListener(e -> verDatosActuales());
        
        panelBotones.add(btnCargarCSV);
        panelBotones.add(btnVerDatos);
        
        panelSuperior.add(panelBotones, BorderLayout.CENTER);
        
        // Label de estado
        lblEstado = new JLabel("No se ha cargado ningún archivo CSV", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Arial", Font.ITALIC, 12));
        lblEstado.setForeground(Color.GRAY);
        panelSuperior.add(lblEstado, BorderLayout.SOUTH);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel de información
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Formato del CSV"));
        
        JTextArea txtInfo = new JTextArea();
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInfo.setText(
            "El archivo CSV debe cumplir con el siguiente formato:\n\n" +
            "1. La primera fila debe contener los nombres de las columnas\n" +
            "2. La última columna debe ser el atributo objetivo (clase)\n" +
            "3. El separador debe ser coma (,)\n" +
            "4. Ejemplo:\n\n" +
            "   Clima,Temperatura,Humedad,Vientos,Pasear\n" +
            "   Soleado,Caliente,Alta,False,No\n" +
            "   Lluvioso,Templado,Normal,True,Si\n" +
            "   ...\n\n" +
            "Notas:\n" +
            "- El atributo objetivo es la columna que se quiere predecir\n" +
            "- Todos los valores deben ser categóricos (texto)\n" +
            "- No debe haber valores vacíos"
        );
        
        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        panelInfo.add(scrollInfo);
        
        add(panelInfo, BorderLayout.CENTER);
        
        // Tabla para vista previa
        modeloTabla = new DefaultTableModel();
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Monospaced", Font.PLAIN, 11));
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Vista Previa"));
        scrollTabla.setPreferredSize(new Dimension(0, 200));
        
        add(scrollTabla, BorderLayout.SOUTH);
    }
    
    private void cargarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo CSV");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos CSV", "csv");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            
            try {
                // Leer el archivo CSV
                BufferedReader br = new BufferedReader(new FileReader(archivo));
                String linea;
                java.util.List<String[]> datos = new ArrayList<>();
                
                while ((linea = br.readLine()) != null) {
                    // Separar por comas, pero respetando comillas
                    String[] valores = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    // Limpiar comillas si existen
                    for (int i = 0; i < valores.length; i++) {
                        valores[i] = valores[i].trim().replace("\"", "");
                    }
                    datos.add(valores);
                }
                br.close();
                
                if (datos.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "El archivo CSV está vacío", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // La primera línea son los encabezados
                String[] encabezados = datos.get(0);
                
                // Validar que haya al menos 2 columnas
                if (encabezados.length < 2) {
                    JOptionPane.showMessageDialog(this, 
                        "El archivo debe tener al menos 2 columnas", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Mostrar diálogo para seleccionar el atributo objetivo
                String atributoObjetivo = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleccione el atributo objetivo (clase a predecir):",
                    "Atributo Objetivo",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    encabezados,
                    encabezados[encabezados.length - 1]
                );
                
                if (atributoObjetivo == null) {
                    return; // Usuario canceló
                }
                
                // Cargar los datos en el controlador
                boolean exito = controlador.cargarDesdeCSV(datos, atributoObjetivo);
                
                if (exito) {
                    csvCargado = true;
                    lblEstado.setText("✓ Archivo cargado: " + archivo.getName() + 
                                     " (" + (datos.size() - 1) + " registros)");
                    lblEstado.setForeground(new Color(0, 150, 0));
                    
                    // Mostrar vista previa
                    mostrarVistaPrevia(datos);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Archivo CSV cargado exitosamente\n" +
                        "Registros: " + (datos.size() - 1) + "\n" +
                        "Atributo objetivo: " + atributoObjetivo,
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al cargar el archivo CSV\n" +
                        "Verifique el formato de los datos", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al leer el archivo:\n" + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarVistaPrevia(java.util.List<String[]> datos) {
        if (datos.isEmpty()) return;
        
        String[] encabezados = datos.get(0);
        modeloTabla.setColumnIdentifiers(encabezados);
        modeloTabla.setRowCount(0);
        
        // Mostrar hasta 10 filas de ejemplo
        int maxFilas = Math.min(11, datos.size()); // +1 por el encabezado
        for (int i = 1; i < maxFilas; i++) {
            modeloTabla.addRow(datos.get(i));
        }
        
        if (datos.size() > 11) {
            Object[] fila = new Object[encabezados.length];
            fila[0] = "...";
            modeloTabla.addRow(fila);
        }
    }
    
    private void verDatosActuales() {
        java.util.List<Map<String, String>> datos = controlador.obtenerDatos();
        
        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay datos cargados en el sistema", 
                "Información", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Obtener encabezados
        Set<String> keysSet = datos.get(0).keySet();
        String[] encabezados = keysSet.toArray(new String[0]);
        
        modeloTabla.setColumnIdentifiers(encabezados);
        modeloTabla.setRowCount(0);
        
        for (Map<String, String> fila : datos) {
            Object[] valores = new Object[encabezados.length];
            for (int i = 0; i < encabezados.length; i++) {
                valores[i] = fila.get(encabezados[i]);
            }
            modeloTabla.addRow(valores);
        }
        
        lblEstado.setText("✓ Mostrando " + datos.size() + " registros actuales");
        lblEstado.setForeground(new Color(0, 100, 200));
    }
    
    public boolean isCsvCargado() {
        return csvCargado;
    }
    
    public void limpiar() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        csvCargado = false;
        lblEstado.setText("No se ha cargado ningún archivo CSV");
        lblEstado.setForeground(Color.GRAY);
    }
}
