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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.roiderh.gcodeviewer.contourelement;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.line.LineSegment2D;

/**
 *
 * @author Herbert Roider <herbert@roider.at>
 */
public class PanelLinesForm extends JPanel implements ActionListener, FocusListener {

    public LinkedList<contourelement> contour;
    public LinkedList<JPanel> panels;

    PanelLinesForm() {
        super();
        panels = new LinkedList<>();

    }

    /**
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void focusGained(FocusEvent e) {
        //System.out.println("focusGained");

    }

    @Override
    public void focusLost(FocusEvent e) {
        //System.out.println("focusLost");
    }

    public void createPanels(LinkedList<contourelement> _contour) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        contour = _contour;

        contourelement prev_ce = null;

        for (contourelement current_ce : contour) {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;

            c.gridy = 0;
            c.insets = new Insets(0, 5, 0, 5);  // padding

            c.gridx = 0;
            panel.add(new JLabel("x"), c);
            c.gridx = 1;
            JTextField y_field = new JTextField();
            y_field.setPreferredSize(new Dimension(80, 16));
            y_field.setMinimumSize(new Dimension(60, 16));
            panel.add(y_field, c);
            c.gridx = 2;
            JCheckBox y_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelsLinesForm.jCheckBox_free.text")); // NOI18N);

            panel.add(y_free_field, c);

            c.gridy++;
            c.gridx = 0;
            panel.add(new JLabel("z"), c);
            JTextField x_field = new JTextField();
            x_field.setPreferredSize(new Dimension(80, 16));
            x_field.setMinimumSize(new Dimension(60, 16));
            c.gridx = 1;
            panel.add(x_field, c);
            c.gridx = 2;
            JCheckBox x_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelsLinesForm.jCheckBox_free.text")); // NOI18N);

            panel.add(x_free_field, c);

            if (prev_ce != null) {
                if (current_ce.shape == contourelement.Shape.ARC) {
                    c.gridy++;
                    c.gridx = 0;
                    panel.add(new JLabel(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelsLinesForm.jLabelStartangle.text")), c); // NOI18N);
                    c.gridx = 1;
                    JTextField startangle_field = new JTextField();
                    startangle_field.setPreferredSize(new Dimension(80, 16));
                    startangle_field.setMinimumSize(new Dimension(60, 16));
                    panel.add(startangle_field, c);
                    c.gridx = 2;
                    JCheckBox startangle_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelsLinesForm.jCheckBox_free.text")); // NOI18N);
                    panel.add(startangle_free_field, c);

                    c.gridy++;
                    c.gridx = 0;
                    panel.add(new JLabel(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelsLinesForm.jLabelEndangle.text")), c);
                    JTextField endangle_field = new JTextField();
                    endangle_field.setPreferredSize(new Dimension(80, 16));
                    endangle_field.setMinimumSize(new Dimension(60, 16));
                    c.gridx = 1;
                    panel.add(endangle_field, c);
                    c.gridx = 2;
                    JCheckBox endangle_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelsLinesForm.jCheckBox_free.text")); // NOI18N);
                    panel.add(endangle_free_field, c);

                    c.gridy++;
                    c.gridx = 0;
                    panel.add(new JLabel(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelsLinesForm.jLabelRadius.text")), c);
                    JTextField radius_field = new JTextField();
                    radius_field.setPreferredSize(new Dimension(80, 16));
                    radius_field.setMinimumSize(new Dimension(60, 16));
                    c.gridx = 1;
                    panel.add(radius_field, c);
                    c.gridx = 2;
                    JCheckBox radius_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelsLinesForm.jCheckBox_free.text")); // NOI18N);
                    panel.add(radius_free_field, c);

                    c.gridy++;
                    c.gridx = 0;
                    panel.add(new JLabel(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelsLinesForm.jLabelTangent.text")), c);
                    c.gridx = 1;
                    JCheckBox tangent_field = new JCheckBox();
                    panel.add(tangent_field, c);

                } else if (current_ce.shape == contourelement.Shape.LINE) {

                    c.gridy++;
                    c.gridx = 0;
                    panel.add(new JLabel(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelsLinesForm.jLabelAngle.text")), c);
                    c.gridx = 1;
                    JTextField angle_field = new JTextField();
                    angle_field.setPreferredSize(new Dimension(80, 16));
                    angle_field.setMinimumSize(new Dimension(60, 16));
                    panel.add(angle_field, c);
                    c.gridx = 2;
                    JCheckBox angle_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelsLinesForm.jCheckBox_free.text")); // NOI18N);

                    panel.add(angle_free_field, c);

                    c.gridy++;
                    c.gridx = 0;
                    panel.add(new JLabel(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelsLinesForm.jLabelTangent.text")), c);
                    c.gridx = 1;
                    JCheckBox tangent_field = new JCheckBox();
                    panel.add(tangent_field, c);

                    switch (current_ce.axis_movement) {
                        case 1:
                            angle_field.setEditable(false);
                            angle_free_field.setEnabled(false);
                            y_field.setEditable(false);
                            y_free_field.setEnabled(false);
                            break;
                        case 2:
                            angle_field.setEditable(false);
                            angle_free_field.setEnabled(false);
                            x_field.setEditable(false);
                            x_free_field.setEnabled(false);
                            break;

                    }

                } else {
                    // Fehler
                }

            }
            panel.setBorder(BorderFactory.createLineBorder(Color.black));
            this.add(panel);
//            panel.addMouseListener(new MouseListener() {
//
//                @Override
//                public void mousePressed(MouseEvent me) {
//                    //requestFocus();
//                    System.out.println("Mouse mousePressed in JPanel");
//                }
//
//                @Override
//                public void mouseReleased(MouseEvent me) {
//                System.out.println("Mouse mouseReleased in JPanel");
//                }
//
//                @Override
//                public void mouseClicked(MouseEvent me) {
//                System.out.println("Mouse mouseClicked in JPanel");
//                }
//
//                @Override
//                public void mouseEntered(MouseEvent me) {
//                System.out.println("Mouse mouseEntered in JPanel");
//                }
//
//                @Override
//                public void mouseExited(MouseEvent me) {
//                System.out.println("Mouse mouseExited in JPanel");
//                }
//            });

            prev_ce = current_ce;

            panels.add(panel);
        }
        fillForm();
    }

    public void fillForm() {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("0.###");

        for (int i = 0; i < contour.size(); i++) {
            contourelement ce = contour.get(i);

            JPanel panel = panels.get(i);
            JTextField y_field = (JTextField) panel.getComponent(1);
            JCheckBox y_free_field = (JCheckBox) panel.getComponent(2);
            JTextField x_field = (JTextField) panel.getComponent(4);
            JCheckBox x_free_field = (JCheckBox) panel.getComponent(5);
            y_field.setText(df.format(ce.points.getLast().y * 2.0));
            x_field.setText(df.format(ce.points.getLast().x));

            y_free_field.setSelected(ce.y_free);
            x_free_field.setSelected(ce.x_free);

            if (i > 0) {
                if (ce.shape == contourelement.Shape.ARC) {
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

                    JTextField startangle_field = (JTextField) panel.getComponent(7);
                    JCheckBox startangle_free_field = (JCheckBox) panel.getComponent(8);

                    JTextField endangle_field = (JTextField) panel.getComponent(10);
                    JCheckBox endangle_free_field = (JCheckBox) panel.getComponent(11);

                    JTextField radius_field = (JTextField) panel.getComponent(13);
                    JCheckBox radius_free_field = (JCheckBox) panel.getComponent(14);

                    startangle_field.setText(df.format(startAngle * 180.0 / Math.PI));
                    endangle_field.setText(df.format(endAngle * 180.0 / Math.PI));

                    startangle_free_field.setSelected(ce.startangle_free);
                    endangle_free_field.setSelected(ce.endangle_free);

                    radius_field.setText(df.format(ce.radius));
                    radius_free_field.setSelected(ce.radius_free);

                    JCheckBox tangent_field = (JCheckBox) panel.getComponent(16);
                    tangent_field.setSelected(ce.tangent);

                } else if (ce.shape == contourelement.Shape.LINE) {
                    LineSegment2D geo = (LineSegment2D) ce.curve;
                    double angle = geo.direction().angle();
                    JTextField angle_field = (JTextField) panel.getComponent(7);
                    JCheckBox angle_free_field = (JCheckBox) panel.getComponent(8);

                    angle_field.setText(df.format(angle * 180.0 / Math.PI));
                    angle_free_field.setSelected(ce.angle_free);

                    JCheckBox tangent_field = (JCheckBox) panel.getComponent(10);

                    tangent_field.setSelected(ce.tangent);

                }
            }

        }

    }

    public LinkedList<contourelement> getContour() {

        for (int i = 0; i < contour.size(); i++) {
            contourelement ce = contour.get(i);

            JPanel panel = panels.get(i);
            JTextField y_field = (JTextField) panel.getComponent(1);
            JCheckBox y_free_field = (JCheckBox) panel.getComponent(2);
            JTextField x_field = (JTextField) panel.getComponent(4);
            JCheckBox x_free_field = (JCheckBox) panel.getComponent(5);

            ce.points.getLast().y = Double.parseDouble(y_field.getText()) * 0.5;
            ce.points.getLast().x = Double.parseDouble(x_field.getText());

            ce.y_free = y_free_field.isSelected();
            ce.x_free = x_free_field.isSelected();

            if (i > 0) {
                if (ce.shape == contourelement.Shape.ARC) {
                    JTextField startangle_field = (JTextField) panel.getComponent(7);
                    JCheckBox startangle_free_field = (JCheckBox) panel.getComponent(8);

                    JTextField endangle_field = (JTextField) panel.getComponent(10);
                    JCheckBox endangle_free_field = (JCheckBox) panel.getComponent(11);

                    JTextField radius_field = (JTextField) panel.getComponent(13);
                    JCheckBox radius_free_field = (JCheckBox) panel.getComponent(14);

                    JCheckBox tangent_field = (JCheckBox) panel.getComponent(16);

                    ce.startangle = Double.parseDouble(startangle_field.getText()) * Math.PI / 180.0;
                    ce.endangle = Double.parseDouble(endangle_field.getText()) * Math.PI / 180.0;

                    ce.startangle_free = startangle_free_field.isSelected();
                    ce.endangle_free = endangle_free_field.isSelected();

                    ce.radius = Double.parseDouble(radius_field.getText());
                    ce.radius_free = radius_free_field.isSelected();

                    ce.tangent = tangent_field.isSelected();

                } else if (ce.shape == contourelement.Shape.LINE) {

                    JTextField angle_field = (JTextField) panel.getComponent(7);
                    JCheckBox angle_free_field = (JCheckBox) panel.getComponent(8);

                    JCheckBox tangent_field = (JCheckBox) panel.getComponent(10);

                    ce.angle = Double.parseDouble(angle_field.getText()) * Math.PI / 180.0;
                    ce.angle_free = angle_free_field.isSelected();

                    ce.tangent = tangent_field.isSelected();
                }
            }

        }

        return contour;
    }

}
