package vista;

import controlador.Controlador;
import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private Controlador controlador;
    private JTabbedPane tabbedPane;
    private PanelCSV panelCSV;
    private PanelDatos panelDatos;
    private PanelAnalisis panelAnalisis;
    private PanelArbol panelArbol;
    private PanelPrediccion panelPrediccion;
    
    public VentanaPrincipal(Controlador controlador) {
        this.controlador = controlador;
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        setTitle("Árbol de Decisión ID3 - Con CSV y Predicción");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        
        panelCSV = new PanelCSV(controlador);
        panelDatos = new PanelDatos(controlador);
        panelAnalisis = new PanelAnalisis(controlador);
        panelArbol = new PanelArbol(controlador);
        panelPrediccion = new PanelPrediccion(controlador);
        
        tabbedPane.addTab("Cargar CSV", panelCSV);
        tabbedPane.addTab("Dataset", panelDatos);
        tabbedPane.addTab("Análisis", panelAnalisis);
        tabbedPane.addTab("Árbol", panelArbol);
        tabbedPane.addTab("Predicción", panelPrediccion);
        
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 4) {
                panelPrediccion.actualizarFormulario();
            }
        });
        
        add(tabbedPane);
        crearBarraMenu();
    }
    
    private void crearBarraMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemCargarCSV = new JMenuItem("Cargar CSV...");
        itemCargarCSV.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        
        menuArchivo.add(itemCargarCSV);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemInstrucciones = new JMenuItem("Instrucciones");
        itemInstrucciones.addActionListener(e -> mostrarInstrucciones());
        menuAyuda.add(itemInstrucciones);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuAyuda);
        setJMenuBar(menuBar);
    }
    
    private void mostrarInstrucciones() {
        String inst = "USO DEL SISTEMA:\n\n" +
            "1. Cargar CSV: Seleccione archivo y atributo objetivo\n" +
            "2. Ver Dataset: Verifique los datos cargados\n" +
            "3. Análisis: Ejecute análisis de entropía\n" +
            "4. Árbol: Construya el árbol de decisión\n" +
            "5. Predicción: Ingrese valores y obtenga resultado";
        
        JOptionPane.showMessageDialog(this, inst, "Instrucciones", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void actualizarDatos() {
        panelDatos.actualizarTabla();
        panelAnalisis.limpiar();
        panelArbol.limpiar();
        panelPrediccion.limpiar();
    }
    
    public void actualizarFormularioPrediccion() {
        panelPrediccion.actualizarFormulario();
    }
}
