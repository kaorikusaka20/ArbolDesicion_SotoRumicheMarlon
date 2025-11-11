package vista;

import controlador.Controlador;
import modelo.Nodo;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
// ----------------------

public class PanelArbol extends JPanel {
    private Controlador controlador;
    private JButton btnConstruir;
    private JPanel panelArbol; // Este es el panel de DIBUJO
    private JTextArea areaTexto;
    private Nodo raiz;

    // --- VARIABLES PARA ZOOM ---
    private double scale = 1.0;
    private double minScale = 0.1;
    private double maxScale = 5.0;
    private int originalWidth = 2000;  // Ancho base del árbol (sin escalar)
    private int originalHeight = 1500; // Altura base del árbol (sin escalar)
    // ---------------------------

    public PanelArbol(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Panel superior con botón
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnConstruir = new JButton("CONSTRUIR ÁRBOL DE DECISIÓN");
        btnConstruir.setFont(new Font("Arial", Font.BOLD, 16));
        btnConstruir.setPreferredSize(new Dimension(350, 40));
        btnConstruir.addActionListener(e -> construirArbol());
        panelSuperior.add(btnConstruir);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel con pestañas para vista gráfica y textual
        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel gráfico
        panelArbol = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (raiz != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // --- 1. APLICAR ZOOM ---
                    g2d.scale(scale, scale);

                    // --- 2. DIBUJAR ÁRBOL USANDO EL ANCHO ORIGINAL ---
                    // Se usa originalWidth en lugar de getWidth() para que el layout
                    // sea consistente sin importar el zoom.
                    dibujarArbol(g2d, raiz, originalWidth / 2, 50, originalWidth / 4, 1);
                }
            }
        };
        panelArbol.setBackground(Color.WHITE);
        
        // --- 3. AÑADIR LISTENER PARA LA RUEDA DEL RATÓN ---
        panelArbol.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoomFactor = 0.1;
                double newScale = scale;

                if (e.getWheelRotation() < 0) { // Zoom In
                    newScale *= (1 + zoomFactor);
                } else { // Zoom Out
                    newScale *= (1 - zoomFactor);
                }

                // Aplicar límites
                if (newScale < minScale) newScale = minScale;
                if (newScale > maxScale) newScale = maxScale;

                if (newScale != scale) {
                    scale = newScale;

                    // Actualizar el tamaño preferido del panel de dibujo
                    int newWidth = (int) (originalWidth * scale);
                    int newHeight = (int) (originalHeight * scale);
                    panelArbol.setPreferredSize(new Dimension(newWidth, newHeight));

                    // Notificar al JScrollPane que el tamaño cambió
                    panelArbol.revalidate();
                    panelArbol.repaint();
                }
            }
        });
        
        // El JScrollPane ya existe, lo que habilita el Panning (desplazamiento)
        JScrollPane scrollPaneGrafico = new JScrollPane(panelArbol);
        scrollPaneGrafico.setPreferredSize(new Dimension(800, 600));

        // Panel textual
        areaTexto = new JTextArea();
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaTexto.setEditable(false);
        areaTexto.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPaneTexto = new JScrollPane(areaTexto);

        tabbedPane.addTab("Vista Gráfica", scrollPaneGrafico);
        tabbedPane.addTab("Vista Textual", scrollPaneTexto);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void construirArbol() {
        raiz = controlador.construirArbol();
        
        // --- 4. CALCULAR DIMENSIONES ORIGINALES DEL ÁRBOL ---
        int alturaNiveles = calcularAlturaArbol(raiz);
        int maxHojas = calcularAnchoArbol(raiz); // Usamos la nueva función

        // Guardamos las dimensiones base (sin escalar)
        // Altura: 150px por nivel (según tu 'hijoY = y + 150') + padding
        originalHeight = alturaNiveles * 150 + 100; 
        // Ancho: 160px por hoja (140 nodo + 20 espacio)
        originalWidth = maxHojas * 160;

        // Asegurar un ancho mínimo para que el layout inicial no falle
        if (originalWidth < 1000) originalWidth = 1000;

        // Resetear el zoom a 1.0 cada vez que se construye un nuevo árbol
        scale = 1.0; 
        
        // Establecer el tamaño preferido inicial (escala 1.0)
        panelArbol.setPreferredSize(new Dimension(originalWidth, originalHeight));
        // ---------------------------------------------------

        panelArbol.revalidate(); // Avisa al JScrollPane del nuevo tamaño
        panelArbol.repaint();

        // Generar vista textual
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("     CONSTRUCCIÓN DEL ÁRBOL DE DECISIÓN - ID3\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");

        generarTextoArbol(raiz, sb, "", true);

        sb.append("\n═══════════════════════════════════════════════════════════\n");
        sb.append("               ÁRBOL COMPLETADO\n");
        sb.append("═══════════════════════════════════════════════════════════\n");

        areaTexto.setText(sb.toString());
    }

    private void dibujarArbol(Graphics2D g, Nodo nodo, int x, int y, int offsetX, int nivel) {
        if (nodo == null) return;

        int anchoNodo = 140;
        int altoNodo = 80;

        // Dibujar nodo
        if (nodo.esHoja()) {
            g.setColor(new Color(144, 238, 144)); // Verde claro
            g.fillRoundRect(x - anchoNodo / 2, y, anchoNodo, altoNodo, 15, 15);
            g.setColor(Color.BLACK);
            g.drawRoundRect(x - anchoNodo / 2, y, anchoNodo, altoNodo, 15, 15);

            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("HOJA", x - 20, y + 25);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("Clase: " + nodo.getClase(), x - 35, y + 45);
            g.drawString("Count: " + nodo.getCount(), x - 35, y + 65);
        } else {
            g.setColor(new Color(173, 216, 230)); // Azul claro
            g.fillRoundRect(x - anchoNodo / 2, y, anchoNodo, altoNodo, 15, 15);
            g.setColor(Color.BLACK);
            g.drawRoundRect(x - anchoNodo / 2, y, anchoNodo, altoNodo, 15, 15);

            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.drawString(nodo.getAtributo(), x - anchoNodo / 2 + 10, y + 20);
            g.setFont(new Font("Arial", Font.PLAIN, 11));
            g.drawString(String.format("E=%.3f", nodo.getEntropia()), x - anchoNodo / 2 + 10, y + 40);
            g.drawString(String.format("G=%.3f", nodo.getGanancia()), x - anchoNodo / 2 + 10, y + 55);
            g.drawString("n=" + nodo.getCount(), x - anchoNodo / 2 + 10, y + 70);

            // Dibujar hijos
            Map<String, Nodo> hijos = nodo.getHijos();
            if (hijos == null) return; // Chequeo de seguridad
            
            int numHijos = hijos.size();
            int startX = x - (numHijos - 1) * offsetX / 2;
            int index = 0;

            for (Map.Entry<String, Nodo> entry : hijos.entrySet()) {
                int hijoX = startX + index * offsetX;
                int hijoY = y + 150;

                // Dibujar línea
                g.setColor(Color.GRAY);
                g.setStroke(new BasicStroke(2));
                g.drawLine(x, y + altoNodo, hijoX, hijoY);

                // Dibujar etiqueta de la rama
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 11));
                int midX = (x + hijoX) / 2;
                int midY = (y + altoNodo + hijoY) / 2;
                g.drawString(entry.getKey(), midX - 20, midY);

                // Dibujar hijo recursivamente
                // El offsetX se reduce para los sub-árboles
                dibujarArbol(g, entry.getValue(), hijoX, hijoY, offsetX / numHijos, nivel + 1);

                index++;
            }
        }
    }

    private void generarTextoArbol(Nodo nodo, StringBuilder sb, String prefijo, boolean esUltimo) {
        if (nodo == null) return;

        if (nodo.esHoja()) {
            sb.append(prefijo);
            sb.append(esUltimo ? "└─ " : "├─ ");
            sb.append(String.format("HOJA: %s (Count: %d)\n", nodo.getClase(), nodo.getCount()));
        } else {
            if (!prefijo.isEmpty()) {
                sb.append(prefijo);
                sb.append(esUltimo ? "└─ " : "├─ ");
            }
            sb.append(String.format("Nivel %d: E[%d] = %.5f\n",
                    nodo.getProfundidad(), nodo.getCount(), nodo.getEntropia()));

            String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");
            sb.append(nuevoPrefijo);
            sb.append(String.format("→ Mejor atributo: %s (Ganancia: %.5f)\n\n",
                    nodo.getAtributo(), nodo.getGanancia()));

            Map<String, Nodo> hijos = nodo.getHijos();
            if (hijos == null) return; // Chequeo de seguridad

            int index = 0;
            int total = hijos.size();

            for (Map.Entry<String, Nodo> entry : hijos.entrySet()) {
                boolean ultimoHijo = (index == total - 1);
                sb.append(nuevoPrefijo);
                sb.append(String.format("Rama: %s = %s\n", nodo.getAtributo(), entry.getKey()));
                generarTextoArbol(entry.getValue(), sb, nuevoPrefijo, ultimoHijo);
                if (!ultimoHijo) sb.append("\n");
                index++;
            }
        }
    }

    private int calcularAlturaArbol(Nodo nodo) {
        if (nodo == null || nodo.esHoja()) return 1;
        if (nodo.getHijos() == null || nodo.getHijos().isEmpty()) return 1;

        int maxAltura = 0;
        for (Nodo hijo : nodo.getHijos().values()) {
            maxAltura = Math.max(maxAltura, calcularAlturaArbol(hijo));
        }

        return maxAltura + 1;
    }

    // --- 5. NUEVA FUNCIÓN PARA CALCULAR ANCHO MÁXIMO (Nº de Hojas) ---
    private int calcularAnchoArbol(Nodo nodo) {
        if (nodo == null || nodo.esHoja()) {
            return 1;
        }
        if (nodo.getHijos() == null || nodo.getHijos().isEmpty()) {
            return 1; // Tratar como hoja si no tiene hijos
        }
        
        int anchoTotal = 0;
        for (Nodo hijo : nodo.getHijos().values()) {
            anchoTotal += calcularAnchoArbol(hijo);
        }
        return anchoTotal;
    }
    // -----------------------------------------------------------------

    public void limpiar() {
        raiz = null;
        panelArbol.repaint();
        areaTexto.setText("");
    }
}
