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
import math.geom2d.circulinear.CirculinearElement2D;
import math.geom2d.circulinear.PolyCirculinearCurve2D;
import org.roiderh.gcodeviewer.contourelement;
import org.roiderh.gcodeviewer.gcodereader;
import contoursolveinterface.Contour;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import math.geom2d.conic.CircleArc2D;

/**
 *
 * @author Herbert Roider <herbert@roider.at>
 */
public class DialogGenerateCode extends javax.swing.JDialog implements ActionListener, FocusListener {

    /**
     * all Contourelements:
     */
    public LinkedList<contourelement> c_elements = null;
    gcodereader gr = new gcodereader();
    final JFXPanel fxPanel = new JFXPanel();
    Toolpath toolpath = null;
    PanelLinesForm lineElementsForm = new PanelLinesForm();
    Contour pl = new Contour();
    public boolean canceled = true;
    /**
     * Field with the generated g-Code:
     */
    public String g_code;

    /**
     * Creates new form DialogBackTranslationFunction
     */
    public DialogGenerateCode(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

    }

    public DialogGenerateCode(String _g_code, java.awt.Frame parent, boolean modal) throws Exception {
        super(parent, modal);
        initComponents();
        this.g_code = _g_code;

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

                @Override
                public void mouseEntered(MouseEvent me) {
                    int j = lineElementsForm.panels.indexOf(me.getSource());
                    toolpath.highlightElement(j - 1);// first Element is a point
                }

                @Override
                public void mouseExited(MouseEvent me) {
                    int j = lineElementsForm.panels.indexOf(me.getSource());
                    toolpath.unhighlightElements();
                }
            });

        }

        //pack();
        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX();

            }
        });

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

            //System.out.println(System.getProperty("os.arch").toLowerCase(Locale.ENGLISH)); // amd64
            //System.out.println(System.getProperty("os.name").toLowerCase(Locale.ENGLISH)); // linux
            gr.calc_contour(c_elements);
            lineElementsForm.fillForm();
            this.drawGraph();

        } else if ("cancel".equals(e.getActionCommand())) {
            this.setVisible(false);
        } else if ("ok".equals(e.getActionCommand())) {

            this.g_code = this.createGCode();
            this.canceled = false;
            this.setVisible(false);
        }

    }

    @Override
    public void focusGained(FocusEvent e) {
        System.out.println("focusGained");

    }

    @Override
    public void focusLost(FocusEvent e) {
        //System.out.println("focusLost");
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
            .addGap(0, 660, Short.MAX_VALUE)
        );
        jPanelViewLayout.setVerticalGroup(
            jPanelViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 617, Short.MAX_VALUE)
        );

        jSplitPaneViewEditor.setLeftComponent(jPanelView);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonCancel, org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "DialogGenerateCode.jButtonCancel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonOk, org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "DialogGenerateCode.jButtonOk.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonCalculate, org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "DialogGenerateCode.jButtonCalculate.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonCalculate)
                .addGap(18, 18, 18)
                .addComponent(jButtonCancel)
                .addGap(18, 18, 18)
                .addComponent(jButtonOk)
                .addGap(63, 63, 63))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPaneViewEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 1094, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPaneViewEditor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonOk)
                    .addComponent(jButtonCalculate))
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
     * transform and cleanup the contour
     *
     * @param contour
     * @return
     */
    private PolyCirculinearCurve2D<CirculinearElement2D> cleanup_contour(LinkedList<contourelement> contour) {
        PolyCirculinearCurve2D<CirculinearElement2D> elements = new PolyCirculinearCurve2D<>();
        for (contourelement current_ce : contour) {

            if (current_ce.curve == null) {
                continue;
            }
            if (current_ce.curve.length() == 0) {
                continue;
            }
            elements.add(current_ce.curve);

            if (current_ce.transition_curve != null) {
                elements.add(current_ce.transition_curve);
            }
        }
        return elements;

    }

    /**
     * Create Contour from G-code
     */
    public void readContour() {
        //java.util.ArrayList<String> args = new java.util.ArrayList<>();
        try {

            InputStream is = new ByteArrayInputStream(this.g_code.getBytes());
            c_elements = gr.read(is);
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
     * create the G-Code from the c_elements
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCalculate;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JPanel jPanelView;
    private javax.swing.JSplitPane jSplitPaneViewEditor;
    // End of variables declaration//GEN-END:variables
}
