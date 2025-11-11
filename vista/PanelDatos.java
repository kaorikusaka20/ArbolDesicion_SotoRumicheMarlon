package vista;

import controlador.Controlador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;

public class PanelDatos extends JPanel {
    private Controlador controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar, btnEditar, btnEliminar;
    
    public PanelDatos(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        inicializarComponentes();
        cargarDatos();
    }
    
    private void inicializarComponentes() {
        // Título
        JLabel titulo = new JLabel("DATASET - CONDICIONES CLIMÁTICAS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);
        
        // Tabla
        String[] columnas = {"#", "Clima", "Temperatura", "Humedad", "Vientos", "Pasear"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tabla.setRowHeight(25);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnAgregar = new JButton("Agregar Fila");
        btnEditar = new JButton("Editar Fila");
        btnEliminar = new JButton("Eliminar Fila");
        
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEditar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnAgregar.addActionListener(e -> agregarFila());
        btnEditar.addActionListener(e -> editarFila());
        btnEliminar.addActionListener(e -> eliminarFila());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarDatos() {
        java.util.List<Map<String, String>> datos = controlador.obtenerDatos();

        if (datos.isEmpty()) {
            modeloTabla.setRowCount(0);
            return;
        }

        // Obtener columnas dinámicamente
        Map<String, String> primeraFila = datos.get(0);
        java.util.List<String> columnas = new ArrayList<>();
        columnas.add("#");
        columnas.addAll(primeraFila.keySet());

        // Actualizar modelo de tabla
        modeloTabla.setColumnIdentifiers(columnas.toArray());
        modeloTabla.setRowCount(0);

        int index = 1;
        for (Map<String, String> fila : datos) {
            Object[] rowData = new Object[columnas.size()];
            rowData[0] = index++;
            int col = 1;
            for (String key : primeraFila.keySet()) {
                rowData[col++] = fila.get(key);
            }
            modeloTabla.addRow(rowData);
        }
    }
    
    private void agregarFila() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        JComboBox<String> cmbClima = new JComboBox<>(new String[]{"Soleado", "Nublado", "Lluvioso"});
        JComboBox<String> cmbTemperatura = new JComboBox<>(new String[]{"Caliente", "Templado", "Frio"});
        JComboBox<String> cmbHumedad = new JComboBox<>(new String[]{"Alta", "Normal"});
        JComboBox<String> cmbVientos = new JComboBox<>(new String[]{"True", "False"});
        JComboBox<String> cmbPasear = new JComboBox<>(new String[]{"Si", "No"});
        
        panel.add(new JLabel("Clima:"));
        panel.add(cmbClima);
        panel.add(new JLabel("Temperatura:"));
        panel.add(cmbTemperatura);
        panel.add(new JLabel("Humedad:"));
        panel.add(cmbHumedad);
        panel.add(new JLabel("Vientos:"));
        panel.add(cmbVientos);
        panel.add(new JLabel("Pasear:"));
        panel.add(cmbPasear);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Nueva Fila", 
                                                   JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            controlador.agregarFila(
                (String) cmbClima.getSelectedItem(),
                (String) cmbTemperatura.getSelectedItem(),
                (String) cmbHumedad.getSelectedItem(),
                (String) cmbVientos.getSelectedItem(),
                (String) cmbPasear.getSelectedItem()
            );
        }
    }
    
    private void editarFila() {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        JComboBox<String> cmbClima = new JComboBox<>(new String[]{"Soleado", "Nublado", "Lluvioso"});
        JComboBox<String> cmbTemperatura = new JComboBox<>(new String[]{"Caliente", "Templado", "Frio"});
        JComboBox<String> cmbHumedad = new JComboBox<>(new String[]{"Alta", "Normal"});
        JComboBox<String> cmbVientos = new JComboBox<>(new String[]{"True", "False"});
        JComboBox<String> cmbPasear = new JComboBox<>(new String[]{"Si", "No"});
        
        cmbClima.setSelectedItem(tabla.getValueAt(selectedRow, 1));
        cmbTemperatura.setSelectedItem(tabla.getValueAt(selectedRow, 2));
        cmbHumedad.setSelectedItem(tabla.getValueAt(selectedRow, 3));
        cmbVientos.setSelectedItem(tabla.getValueAt(selectedRow, 4));
        cmbPasear.setSelectedItem(tabla.getValueAt(selectedRow, 5));
        
        panel.add(new JLabel("Clima:"));
        panel.add(cmbClima);
        panel.add(new JLabel("Temperatura:"));
        panel.add(cmbTemperatura);
        panel.add(new JLabel("Humedad:"));
        panel.add(cmbHumedad);
        panel.add(new JLabel("Vientos:"));
        panel.add(cmbVientos);
        panel.add(new JLabel("Pasear:"));
        panel.add(cmbPasear);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Fila", 
                                                   JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            controlador.editarFila(
                selectedRow,
                (String) cmbClima.getSelectedItem(),
                (String) cmbTemperatura.getSelectedItem(),
                (String) cmbHumedad.getSelectedItem(),
                (String) cmbVientos.getSelectedItem(),
                (String) cmbPasear.getSelectedItem()
            );
        }
    }
    
    private void eliminarFila() {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar esta fila?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            controlador.eliminarFila(selectedRow);
        }
    }
    
    public void actualizarTabla() {
        cargarDatos();
    }
}
