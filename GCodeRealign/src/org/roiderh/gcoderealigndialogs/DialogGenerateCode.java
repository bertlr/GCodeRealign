/*
 * Copyright (C) 2017 by Herbert Roider <herbert@roider.at>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.roiderh.gcoderealigndialogs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import org.roiderh.gcodeviewer.contourelement;
import org.roiderh.gcodeviewer.gcodereader;
import contoursolveinterface.Contour;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import math.geom2d.conic.CircleArc2D;
import org.roiderh.gcodeviewer.lexer.Gcodereader;
import org.roiderh.gcodeviewer.lexer.GcodereaderConstants;
import org.roiderh.gcodeviewer.lexer.Token;
import org.roiderh.gcodeviewer.parameter;

/**
 *
 * @author Herbert Roider <herbert@roider.at>
 */
public class DialogGenerateCode extends javax.swing.JDialog implements ActionListener, FreedomChangeListener, ValueChangeListener {

    /**
     * all Contourelements:
     */
    public LinkedList<contourelement> c_elements = null;
    gcodereader gr = new gcodereader();
    JFXPanel fxPanel = null;
    Toolpath toolpath = null;
    PanelLinesForm lineElementsForm = new PanelLinesForm();
    /**
     * encapsulate the geometry processor
     */
    Contour pl = new Contour();
    public boolean canceled = true;
    /**
     * if true the test calculation will be performed
     */
    boolean test_calc = false;
    /**
     * timer for the test calculation
     */
    private Timer t = new Timer(1000, this);
    /**
     * @deprecated Field with the generated g-Code:
     */
    public String g_code;
    //public ArrayList<String> g_code_lines;

    /**
     * Creates new form DialogBackTranslationFunction
     */
    public DialogGenerateCode(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        t.start();

    }

    public void initGui() {
        //java.util.ArrayList<String> values = new java.util.ArrayList<>();
        jButtonCancel.setActionCommand("cancel");
        jButtonCancel.addActionListener(this);

        jButtonOk.setActionCommand("ok");
        jButtonOk.addActionListener(this);

        jButtonCalculate.setActionCommand("calculate");
        jButtonCalculate.addActionListener(this);

        this.readContour();
        lineElementsForm.createPanels(c_elements);
        JScrollPane jsp = new JScrollPane(lineElementsForm);
        jSplitPaneViewEditor.setRightComponent(jsp);

        for (JPanel p : lineElementsForm.panels) {
            p.addMouseListener(new MouseListener() {

                @Override
                public void mousePressed(MouseEvent me) {

                }

                @Override
                public void mouseReleased(MouseEvent me) {

                }

                @Override
                public void mouseClicked(MouseEvent me) {

                }

                /**
                 * draw a border over the current panel for a contour element
                 */
                @Override
                public void mouseEntered(MouseEvent me) {
                    int j = lineElementsForm.panels.indexOf(me.getSource());
                    toolpath.highlightElement(j);// first Element is a point
                    for (PanelContourelement pc : lineElementsForm.panels) {
                        pc.setBorder(BorderFactory.createLineBorder(Color.black));
                    }
                    PanelContourelement pc = (PanelContourelement) me.getSource();
                    pc.setBorder(BorderFactory.createLineBorder(Color.GREEN));

                }

                @Override
                public void mouseExited(MouseEvent me) {
                    //int j = lineElementsForm.panels.indexOf(me.getSource());
                    //toolpath.unhighlightElements();
                }
            });
            for (PanelContourelement pc : lineElementsForm.panels) {
                pc.addListener(this);
                pc.addValueChangeListener(this);
            }
        }
        this.fxPanel = new JFXPanel();
        //pack();
        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX();
                test_calc = true;

            }
        });

    }

    /**
     * called if a checkbox for freedom is changed
     */
    @Override
    public void freedomChange() {
        //System.out.println("freedomChange");
        test_calc = true;
    }

    /**
     * called if the focus of textfield is lost
     */
    @Override
    public void valueChange() {
        //System.out.println("valueChange");
        test_calc = true;
    }

    private void initFX() {

        this.jPanelView.setLayout(new BorderLayout());
        this.toolpath = new Toolpath();
        Scene scene = new Scene(this.toolpath);
        this.fxPanel.setScene(scene);
        this.jPanelView.add(fxPanel);

        //pack();
        this.jSplitPaneViewEditor.setDividerLocation(0.66);

        this.fxPanel.addComponentListener(new ComponentListener() {
            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentResized(ComponentEvent e) {
                drawGraph();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });

    }

    /**
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // timer event
        if (e.getSource() == t) {
            if (this.test_calc == false) {
                return;
            }
            LinkedList<contourelement> elements = null;
            elements = lineElementsForm.getContour();
            if (elements == null) {
                return;
            }
            this.test_calc_contour(elements);
            drawGraph();
            this.test_calc = false;
        }
        // Button "calculate" pressed
        if ("calculate".equals(e.getActionCommand())) {

            lineElementsForm.getContour();

            int handle = pl.create();

            for (int i = 0; i < c_elements.size(); i++) {
                contourelement ce = c_elements.get(i);
                if (i == 0) {
                    //System.out.println("Handle:" + handle + ", x" + ce.points.getLast().x + ", xfree=" + contourelement.BooltoInt(ce.x_free) + ", y" + ce.points.getLast().y + ", yfree=" + contourelement.BooltoInt(ce.y_free));
                    pl.addPoint(handle, ce.points.getLast().x, contourelement.BooltoInt(ce.x_free), ce.points.getLast().y, contourelement.BooltoInt(ce.y_free));
                } else if (ce.shape == contourelement.Shape.LINE) {
//                    System.out.println("Handle:" + handle + ", x" + ce.points.getLast().x + ", xfree=" + contourelement.BooltoInt(ce.x_free) + ", y" + ce.points.getLast().y + ", yfree=" + contourelement.BooltoInt(ce.y_free)
//                            + ", angle" + ce.angle + ", anlefree" + contourelement.BooltoInt(ce.angle_free));
                    pl.addLine(handle, ce.points.getLast().x, contourelement.BooltoInt(ce.x_free),
                            ce.points.getLast().y, contourelement.BooltoInt(ce.y_free),
                            ce.angle, contourelement.BooltoInt(ce.angle_free),
                            ce.tangent);
                } else if (ce.shape == contourelement.Shape.ARC) {
                    CircleArc2D geo = (CircleArc2D) ce.curve;
                    double startAngle;
                    double endAngle;
                    if (ce.ccw == true) {
                        startAngle = 0.5 * Math.PI + geo.getStartAngle();
                        endAngle = 0.5 * Math.PI + (geo.getStartAngle() + geo.getAngleExtent());
                    } else {
                        startAngle = -0.5 * Math.PI + geo.getStartAngle();
                        endAngle = -0.5 * Math.PI + (geo.getStartAngle() + geo.getAngleExtent());
                    }
                    if (startAngle > 2.0 * Math.PI) {
                        startAngle -= 2.0 * Math.PI;
                    }
                    if (startAngle < -2.0 * Math.PI) {
                        startAngle += 2.0 * Math.PI;
                    }
                    if (endAngle > 2.0 * Math.PI) {
                        endAngle -= 2.0 * Math.PI;
                    }
                    if (endAngle < -2.0 * Math.PI) {
                        endAngle += 2.0 * Math.PI;
                    }

//                    System.out.println("Handle:" + handle + ", x" + ce.points.getLast().x + ", xfree=" + contourelement.BooltoInt(ce.x_free) + ", centery" + ce.points.getLast().y + ", centeryfree=" + contourelement.BooltoInt(ce.y_center_free)
//                            + ", center_x" + geo.supportingCircle().center().x() + ", xfree=" + contourelement.BooltoInt(ce.x_center_free) + ", y" + geo.supportingCircle().center().y() + ", yfree=" + contourelement.BooltoInt(ce.x_center_free)
//                            + ", rad:" + ce.radius + ", radfree:" + contourelement.BooltoInt(ce.radius_free)
//                            + ", startangle" + ce.startangle + ", startanlefree" + contourelement.BooltoInt(ce.startangle_free)
//                            + ", endangle" + ce.endangle + ", endanlefree" + contourelement.BooltoInt(ce.endangle_free)
//                    );
                    pl.addArc(handle, ce.points.getLast().x, contourelement.BooltoInt(ce.x_free),
                            ce.points.getLast().y, contourelement.BooltoInt(ce.y_free),
                            geo.supportingCircle().center().x(), contourelement.BooltoInt(true),
                            geo.supportingCircle().center().y(), contourelement.BooltoInt(true),
                            ce.radius, contourelement.BooltoInt(ce.radius_free),
                            ce.startangle, contourelement.BooltoInt(ce.startangle_free),
                            ce.endangle, contourelement.BooltoInt(ce.endangle_free),
                            ce.tangent);

                }
                //System.out.println("Solution for " + i + " Element: " + pl.solve(handle));

            }

            int solution = -1;

            solution = pl.solve(handle);

            if (solution != 0) {
                //System.out.println("keine Lösung gefunden!!!");
                JOptionPane.showMessageDialog(null, "Error: no solution found");
                return;
            }
            int size = pl.size(handle);

            //System.out.println("Handle=" + handle);
            for (int j = 0; j < size; j++) {
                double l[] = pl.getSolution(handle, j);
                //System.out.println("x=" + l[0] + ", y=" + l[1] + ", rad=" + l[2]);
                contourelement ce = c_elements.get(j);
                contourelement next_ce = null;
                if ((j + 1) < size) {
                    next_ce = c_elements.get(j + 1);
                }
                if (ce.x_free) {
                    ce.points.getLast().x = l[0];
                    if (next_ce != null) {
                        next_ce.points.getFirst().x = l[0];
                    }
                }
                if (ce.y_free) {
                    ce.points.getLast().y = l[1];
                    if (next_ce != null) {
                        next_ce.points.getFirst().y = l[1];
                    }
                }
                if (ce.shape == contourelement.Shape.ARC) {
                    if (ce.radius_free) {
                        if (l[2] < 0.0) {
                            ce.radius = -l[2];
                        } else {
                            ce.radius = l[2];
                        }

                    }
                }

            }

            gr.calc_contour(c_elements);
            lineElementsForm.fillForm();
            this.drawGraph();

        } else if ("cancel".equals(e.getActionCommand())) {
            this.setVisible(false);
        } else if ("ok".equals(e.getActionCommand())) {
            this.replace_values();
            //this.g_code = this.createGCode();
            this.canceled = false;
            this.setVisible(false);
        }

    }

    /**
     * perform a test calculation and colores the forms for the nodes
     *
     * @param c_elements
     * @return integer 0 if ok otherwise <>0
     */
    public int test_calc_contour(LinkedList<contourelement> c_elements) {

        Contour c = new Contour();
        int handle = c.create();
        int solution = -1;
        System.out.println("handle=" + String.valueOf(handle));

        for (int i = 0; i < c_elements.size(); i++) {
            contourelement ce = c_elements.get(i);
            if (i == 0) {
                //System.out.println("Handle:" + handle + ", x" + ce.points.getLast().x + ", xfree=" + contourelement.BooltoInt(ce.x_free) + ", y" + ce.points.getLast().y + ", yfree=" + contourelement.BooltoInt(ce.y_free));
                c.addPoint(handle, ce.points.getLast().x, contourelement.BooltoInt(ce.x_free), ce.points.getLast().y, contourelement.BooltoInt(ce.y_free));
            } else if (ce.shape == contourelement.Shape.LINE) {
//                    System.out.println("Handle:" + handle + ", x" + ce.points.getLast().x + ", xfree=" + contourelement.BooltoInt(ce.x_free) + ", y" + ce.points.getLast().y + ", yfree=" + contourelement.BooltoInt(ce.y_free)
//                            + ", angle" + ce.angle + ", anlefree" + contourelement.BooltoInt(ce.angle_free));
                c.addLine(handle, ce.points.getLast().x, contourelement.BooltoInt(ce.x_free),
                        ce.points.getLast().y, contourelement.BooltoInt(ce.y_free),
                        ce.angle, contourelement.BooltoInt(ce.angle_free),
                        ce.tangent);
            } else if (ce.shape == contourelement.Shape.ARC) {
                CircleArc2D geo = (CircleArc2D) ce.curve;
                double startAngle;
                double endAngle;
                if (ce.ccw == true) {
                    startAngle = 0.5 * Math.PI + geo.getStartAngle();
                    endAngle = 0.5 * Math.PI + (geo.getStartAngle() + geo.getAngleExtent());
                } else {
                    startAngle = -0.5 * Math.PI + geo.getStartAngle();
                    endAngle = -0.5 * Math.PI + (geo.getStartAngle() + geo.getAngleExtent());
                }
                if (startAngle > 2.0 * Math.PI) {
                    startAngle -= 2.0 * Math.PI;
                }
                if (startAngle < -2.0 * Math.PI) {
                    startAngle += 2.0 * Math.PI;
                }
                if (endAngle > 2.0 * Math.PI) {
                    endAngle -= 2.0 * Math.PI;
                }
                if (endAngle < -2.0 * Math.PI) {
                    endAngle += 2.0 * Math.PI;
                }

//                    System.out.println("Handle:" + handle + ", x" + ce.points.getLast().x + ", xfree=" + contourelement.BooltoInt(ce.x_free) + ", centery" + ce.points.getLast().y + ", centeryfree=" + contourelement.BooltoInt(ce.y_center_free)
//                            + ", center_x" + geo.supportingCircle().center().x() + ", xfree=" + contourelement.BooltoInt(ce.x_center_free) + ", y" + geo.supportingCircle().center().y() + ", yfree=" + contourelement.BooltoInt(ce.x_center_free)
//                            + ", rad:" + ce.radius + ", radfree:" + contourelement.BooltoInt(ce.radius_free)
//                            + ", startangle" + ce.startangle + ", startanlefree" + contourelement.BooltoInt(ce.startangle_free)
//                            + ", endangle" + ce.endangle + ", endanlefree" + contourelement.BooltoInt(ce.endangle_free)
//                    );
                c.addArc(handle, ce.points.getLast().x, contourelement.BooltoInt(ce.x_free),
                        ce.points.getLast().y, contourelement.BooltoInt(ce.y_free),
                        geo.supportingCircle().center().x(), contourelement.BooltoInt(true),
                        geo.supportingCircle().center().y(), contourelement.BooltoInt(true),
                        ce.radius, contourelement.BooltoInt(ce.radius_free),
                        ce.startangle, contourelement.BooltoInt(ce.startangle_free),
                        ce.endangle, contourelement.BooltoInt(ce.endangle_free),
                        ce.tangent);

            }
            solution = c.solve(handle);
            System.out.println("Solution for " + i + " Element: " + solution);
            if (solution == 0) {
                lineElementsForm.panels.get(i).setBackground(Color.decode("#c1f6b9"));

            } else {
                lineElementsForm.panels.get(i).setBackground(Color.decode("#febfc4"));
            }

        }

        solution = c.solve(handle);

        if (solution != 0) {
            System.out.println("no solution");
            //JOptionPane.showMessageDialog(null, "Error: no solution found");
            return solution;
        }
        System.out.println("solution found");
        //this.drawGraph();
        return solution;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPaneViewEditor = new javax.swing.JSplitPane();
        jPanelView = new javax.swing.JPanel();
        jButtonCancel = new javax.swing.JButton();
        jButtonOk = new javax.swing.JButton();
        jButtonCalculate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "DialogGenerateCode.title")); // NOI18N

        jSplitPaneViewEditor.setResizeWeight(0.5);
        jSplitPaneViewEditor.setLastDividerLocation(101);

        javax.swing.GroupLayout jPanelViewLayout = new javax.swing.GroupLayout(jPanelView);
        jPanelView.setLayout(jPanelViewLayout);
        jPanelViewLayout.setHorizontalGroup(
            jPanelViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 973, Short.MAX_VALUE)
        );
        jPanelViewLayout.setVerticalGroup(
            jPanelViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 682, Short.MAX_VALUE)
        );

        jSplitPaneViewEditor.setLeftComponent(jPanelView);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonCancel, org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "DialogGenerateCode.jButtonCancel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonOk, org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "DialogGenerateCode.jButtonOk.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonCalculate, org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "DialogGenerateCode.jButtonCalculate.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(971, Short.MAX_VALUE)
                .addComponent(jButtonCalculate)
                .addGap(18, 18, 18)
                .addComponent(jButtonCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonOk)
                .addGap(67, 67, 67))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPaneViewEditor)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPaneViewEditor)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCalculate)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonOk))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DialogGenerateCode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DialogGenerateCode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DialogGenerateCode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogGenerateCode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogGenerateCode dialog = new DialogGenerateCode(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    /**
     * Create Contour from G-code
     */
    public void readContour() {
        //java.util.ArrayList<String> args = new java.util.ArrayList<>();
        JTextComponent ed = org.netbeans.api.editor.EditorRegistry.lastFocusedComponent();
        if (ed == null) {
            JOptionPane.showMessageDialog(null, "Error: no open editor");
            return;
        }

        int start_offset = ed.getSelectionStart();
        int end_offset = ed.getSelectionEnd();
        Document doc = ed.getDocument();
        Element root = doc.getDefaultRootElement();
        int start_index = root.getElementIndex(start_offset);
        int end_index = root.getElementIndex(end_offset);
        int lineIndex = 0;
        //int elCount = root.getElementCount();
        //int count = 0;
        ArrayList<String> lines = new ArrayList<>();
        try {

            for (lineIndex = start_index; lineIndex <= end_index; lineIndex++) {
                Element contentEl = root.getElement(lineIndex);
                int start = contentEl.getStartOffset();
                int end = contentEl.getEndOffset();
                String line = doc.getText(start, end - start - 1);
                lines.add(line);

            }

            c_elements = gr.read(start_index, lines);
            if (c_elements.isEmpty()) {
                return;
            }

            gr.calc_contour(c_elements);

            //this.cleanup_contour(c_elements);
        } catch (Exception e1) {
            System.out.println("Error " + e1.toString());
            JOptionPane.showMessageDialog(null, "Error: " + e1.toString());
            return;

        }
        //drawGraph();

    }

    public void drawGraph() {
        //System.out.println("hallo ContourComponent");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gr.calc_contour(c_elements);
                toolpath.draw(c_elements);
            }

        });
    }

    /**
     * @deprecated create the G-Code from the c_elements
     *
     * @return
     */
    public String createGCode() {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("0.###");
        int machine = gr.getMachine();

        StringBuilder new_code = new StringBuilder();
        contourelement prev_ce = null;
        String prev_x_val = "";
        String prev_y_val = "";
        for (contourelement ce : c_elements) {

            if (ce.shape == contourelement.Shape.LINE) {
                if (ce.feed == contourelement.Feed.RAPID) {
                    new_code.append("G0");
                } else {
                    new_code.append("G1");
                }
            } else if (ce.shape == contourelement.Shape.ARC) {
                if (ce.ccw) {
                    new_code.append("G3");
                } else {
                    new_code.append("G2");
                }
                if (machine == 0) {
                    new_code.append(" CR=");

                } else {
                    new_code.append(" B");
                }
                new_code.append(df.format(ce.radius));

            }

            String x_val = df.format(ce.points.getLast().x);
            String y_val = df.format(ce.points.getLast().y * 2.0);

            if (prev_y_val.equals(y_val) == false) {
                new_code.append(" X");
                new_code.append(y_val);
            }
            if (prev_x_val.equals(x_val) == false) {
                new_code.append(" Z");
                new_code.append(x_val);
            }

            prev_x_val = x_val;
            prev_y_val = y_val;

            if (ce.transition_elem_size > 0.0) {
                if (machine == 0) {
                    if (ce.transistion_elem == contourelement.Transition.CHAMFER) {
                        new_code.append(" CHR=");
                    } else if (ce.transistion_elem == contourelement.Transition.ROUND) {
                        new_code.append(" RND=");
                    }

                } else if (ce.transistion_elem == contourelement.Transition.CHAMFER) {
                    new_code.append(" B-");
                } else if (ce.transistion_elem == contourelement.Transition.ROUND) {
                    new_code.append(" B");
                }
                new_code.append(df.format(ce.transition_elem_size));
            }
            new_code.append("\n");
            prev_ce = ce;

        }
        return new_code.toString();
    }

    /**
     * relace changed lines in the editor
     */
    void replace_values() {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("0.###");
        int machine = gr.getMachine();

        Token t;

        for (contourelement ce : c_elements) {
            String out_line = "";
            String in_line = ce.line;
            // The parser needs an line break:
            in_line += '\n';
            InputStream istream = new ByteArrayInputStream(in_line.getBytes());
            Gcodereader gr = new Gcodereader(istream);
            int prev_end = 0;
            int curr_start = 0;
            int curr_end = 0;
            ArrayList<String> out_line_arr = new ArrayList<>();
            /*
             read one line
             */
            do {
                t = gr.getNextToken();
                if (t.kind == GcodereaderConstants.EOF) {
                    break;
                }
                parameter para = null;
                boolean found = false;

                System.out.println("Token: " + t.kind + ", " + t.image);
                curr_start = t.beginColumn;
                //curr_end = t.endColumn;
                curr_end = curr_start + t.image.trim().length() - 1;

                para = new parameter();
                // All parameters except G-functions
                if (t.kind == GcodereaderConstants.PARAM || t.kind == GcodereaderConstants.SHORT_PARAM) {

                    para.parse(t.image);

                    if (para.name.compareTo("X") == 0 || para.name.compareTo("Z") == 0) {

                        if (para.name.compareTo("X") == 0) {
                            found = true;
                            out_line_arr.add(ce.line.substring(prev_end, curr_start - 1));
                            out_line_arr.add("X" + df.format(ce.points.getLast().y * 2.0));
                        } else if (para.name.compareTo("Z") == 0) {
                            found = true;
                            out_line_arr.add(ce.line.substring(prev_end, curr_start - 1));
                            out_line_arr.add("Z" + df.format(ce.points.getLast().x));
                        }

                    } else if (para.name.compareTo("B") == 0 || para.name.compareTo("CR") == 0) {
                        if (ce.shape == contourelement.Shape.ARC) {
                            if (para.name.compareTo("CR") == 0) {
                                found = true;
                                out_line_arr.add(ce.line.substring(prev_end, curr_start - 1));
                                out_line_arr.add("CR=" + df.format(ce.radius));
                            } else if (para.name.compareTo("B") == 0) {
                                found = true;
                                out_line_arr.add(ce.line.substring(prev_end, curr_start - 1));
                                out_line_arr.add("B" + df.format(ce.radius));
                            }
                        }
                    } else if (para.name.compareTo("I") == 0 || para.name.compareTo("J") == 0 || para.name.compareTo("K") == 0) {
                        if (ce.shape == contourelement.Shape.ARC) {
                            found = true;
                            out_line_arr.add(ce.line.substring(prev_end, curr_start - 1));
                            // only replace I with radius, not J and K
                            if (para.name.compareTo("I") == 0) {
                                if (machine == 1) {
                                    out_line_arr.add("B" + df.format(ce.radius));
                                } else {
                                    out_line_arr.add("CR=" + df.format(ce.radius));
                                }
                            }

                        }
                    }

                }
                if (found) {
                    prev_end = curr_end;
                }
                //System.out.println(out_line_arr.)
                //t = gr.getNextToken();
            } while (!(t.kind == GcodereaderConstants.EOF));
            curr_start = ce.line.length();
            out_line_arr.add(ce.line.substring(prev_end, curr_start));

            int i = 0;
            out_line = "";
            for (i = 0; i < out_line_arr.size(); i++) {
                out_line += out_line_arr.get(i);
            }
            // replace the modified line:
            System.out.println(out_line);
            try {
                JTextComponent ed = org.netbeans.api.editor.EditorRegistry.lastFocusedComponent();
                Document doc = ed.getDocument();
                Element root = doc.getDefaultRootElement();
                Element contentEl = root.getElement(ce.abs_line_index);
                int start = contentEl.getStartOffset();
                int end = contentEl.getEndOffset();
                doc.remove(start, end - start - 1);
                doc.insertString(start, out_line, null);
            } catch (BadLocationException ex) {
                System.out.println(ex.getMessage());
                JOptionPane.showMessageDialog(null, "Error: " + ex.toString());
                return;
            }

        }

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCalculate;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JPanel jPanelView;
    private javax.swing.JSplitPane jSplitPaneViewEditor;
    // End of variables declaration//GEN-END:variables
}
