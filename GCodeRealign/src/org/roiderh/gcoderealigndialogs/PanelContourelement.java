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
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import math.geom2d.Point2D;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.line.LineSegment2D;
import org.roiderh.gcodeviewer.contourelement;

/**
 *
 * @author Herbert Roider <herbert@roider.at>
 * a Form for a single contourelement
 */
public class PanelContourelement extends JPanel implements ActionListener, FocusListener {

    private List<FreedomChangeListener> listeners = new ArrayList<>();
    private List<ValueChangeListener> valueChangeListeners = new ArrayList<>();

    public contourelement current_ce = null;
    public contourelement prev_ce = null;

    public void addListener(FreedomChangeListener toAdd) {
        listeners.add(toAdd);
    }

    public void addValueChangeListener(ValueChangeListener toAdd) {
        valueChangeListeners.add(toAdd);
    }

    /**
     * called when freedom checkboxes selected or deselected
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("action: " + e.getSource().getClass());
        for (FreedomChangeListener fchl : this.listeners) {
            fchl.freedomChange();
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        //System.out.println("Focus gained");
    }

    @Override
    public void focusLost(FocusEvent e) {
        System.out.println("Focus lost");
        for (ValueChangeListener vcl : this.valueChangeListeners) {
            vcl.valueChange();
        }

    }

    public PanelContourelement(contourelement _current_ce, contourelement _prev_ce) {
        super();
        current_ce = _current_ce;
        prev_ce = _prev_ce;
        create();

    }

    private void create() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        c.insets = new Insets(0, 3, 0, 3);  // padding

        c.gridx = 0;
        this.add(new JLabel("x"), c);
        c.gridx = 1;
        JTextField y_field = new JTextField();
        //y_field.addFocusListener(this);
        y_field.setPreferredSize(new Dimension(80, 16));
        y_field.setMinimumSize(new Dimension(60, 16));
        y_field.addFocusListener(this);
        this.add(y_field, c);
        c.gridx = 2;
        JCheckBox y_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jCheckBox_free.text")); // NOI18N);
        y_free_field.addActionListener(this);
        //y_free_field.addFocusListener(this);
        this.add(y_free_field, c);

        c.gridy++;
        c.gridx = 0;
        this.add(new JLabel("z"), c);
        JTextField x_field = new JTextField();
        x_field.setPreferredSize(new Dimension(80, 16));
        x_field.setMinimumSize(new Dimension(60, 16));
        x_field.addFocusListener(this);
        c.gridx = 1;
        this.add(x_field, c);
        c.gridx = 2;
        JCheckBox x_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jCheckBox_free.text")); // NOI18N);
        x_free_field.addActionListener(this);
        this.add(x_free_field, c);

        if (prev_ce != null) {
            if (current_ce.shape == contourelement.Shape.ARC) {
                c.gridy++;
                c.gridx = 0;
                this.add(new JLabel(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jLabelStartangle.text")), c); // NOI18N);
                c.gridx = 1;
                JTextField startangle_field = new JTextField();
                startangle_field.setPreferredSize(new Dimension(80, 16));
                startangle_field.setMinimumSize(new Dimension(60, 16));
                startangle_field.addFocusListener(this);
                this.add(startangle_field, c);
                c.gridx = 2;
                JCheckBox startangle_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jCheckBox_free.text")); // NOI18N);
                startangle_free_field.addActionListener(this);
                this.add(startangle_free_field, c);

                c.gridy++;
                c.gridx = 0;
                this.add(new JLabel(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jLabelEndangle.text")), c);
                JTextField endangle_field = new JTextField();
                endangle_field.setPreferredSize(new Dimension(80, 16));
                endangle_field.setMinimumSize(new Dimension(60, 16));
                endangle_field.addFocusListener(this);
                c.gridx = 1;
                this.add(endangle_field, c);
                c.gridx = 2;
                JCheckBox endangle_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jCheckBox_free.text")); // NOI18N);
                endangle_free_field.addActionListener(this);
                this.add(endangle_free_field, c);

                c.gridy++;
                c.gridx = 0;
                this.add(new JLabel(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jLabelRadius.text")), c);
                JTextField radius_field = new JTextField();
                radius_field.setPreferredSize(new Dimension(80, 16));
                radius_field.setMinimumSize(new Dimension(60, 16));
                radius_field.addFocusListener(this);
                c.gridx = 1;
                this.add(radius_field, c);
                c.gridx = 2;
                JCheckBox radius_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jCheckBox_free.text")); // NOI18N);
                radius_free_field.addActionListener(this);
                this.add(radius_free_field, c);

                c.gridy++;
                c.gridx = 0;
                this.add(new JLabel(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jLabelTangent.text")), c);
                c.gridx = 1;
                JCheckBox tangent_field = new JCheckBox();
                tangent_field.addActionListener(this);
                this.add(tangent_field, c);

                //////////////////////
                c.gridy++;
                c.gridx = 0;
                this.add(new JLabel("cx"), c);
                c.gridx = 1;
                JTextField y_center_field = new JTextField();
                y_center_field.setPreferredSize(new Dimension(80, 16));
                y_center_field.setMinimumSize(new Dimension(60, 16));
                y_center_field.addFocusListener(this);
                this.add(y_center_field, c);
                c.gridx = 2;
                JCheckBox y_center_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jCheckBox_free.text")); // NOI18N);
                y_center_free_field.addActionListener(this);
                this.add(y_center_free_field, c);

                c.gridy++;
                c.gridx = 0;
                this.add(new JLabel("cz"), c);
                JTextField x_center_field = new JTextField();
                x_center_field.setPreferredSize(new Dimension(80, 16));
                x_center_field.setMinimumSize(new Dimension(60, 16));
                x_center_field.addFocusListener(this);
                c.gridx = 1;
                this.add(x_center_field, c);
                c.gridx = 2;
                JCheckBox x_center_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jCheckBox_free.text")); // NOI18N);
                x_center_free_field.addActionListener(this);
                this.add(x_center_free_field, c);

                ///////////////////                
                // ortho lines
                switch (current_ce.axis_movement) {
                    case 1:
                        //angle_field.setEditable(false);
                        //angle_free_field.setEnabled(false);
                        y_field.setEditable(false);
                        y_free_field.setEnabled(false);
                        break;
                    case 2:
                        //angle_field.setEditable(false);
                        //angle_free_field.setEnabled(false);
                        x_field.setEditable(false);
                        x_free_field.setEnabled(false);
                        break;

                }

            } else if (current_ce.shape == contourelement.Shape.LINE) {

                c.gridy++;
                c.gridx = 0;
                this.add(new JLabel(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jLabelAngle.text")), c);
                c.gridx = 1;
                JTextField angle_field = new JTextField();
                angle_field.setPreferredSize(new Dimension(80, 16));
                angle_field.setMinimumSize(new Dimension(60, 16));
                angle_field.addFocusListener(this);
                this.add(angle_field, c);
                c.gridx = 2;
                JCheckBox angle_free_field = new JCheckBox(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jCheckBox_free.text")); // NOI18N);
                angle_free_field.addActionListener(this);
                this.add(angle_free_field, c);

                c.gridy++;
                c.gridx = 0;
                this.add(new JLabel(org.openide.util.NbBundle.getMessage(DialogGenerateCode.class, "PanelContourelement.jLabelTangent.text")), c);
                c.gridx = 1;
                JCheckBox tangent_field = new JCheckBox();
                tangent_field.addActionListener(this);
                this.add(tangent_field, c);

                // ortho lines
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
        this.setBorder(BorderFactory.createLineBorder(Color.black));

    }

    public void fillForm() {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("0.###");
        contourelement ce = this.current_ce;

        JTextField y_field = (JTextField) this.getComponent(1);
        JCheckBox y_free_field = (JCheckBox) this.getComponent(2);
        JTextField x_field = (JTextField) this.getComponent(4);
        JCheckBox x_free_field = (JCheckBox) this.getComponent(5);

        y_field.setText(df.format(ce.points.getLast().y * 2.0));
        x_field.setText(df.format(ce.points.getLast().x));

        y_free_field.setSelected(ce.y_free);
        x_free_field.setSelected(ce.x_free);

        if (prev_ce != null) {
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

                JTextField startangle_field = (JTextField) this.getComponent(7);
                JCheckBox startangle_free_field = (JCheckBox) this.getComponent(8);

                JTextField endangle_field = (JTextField) this.getComponent(10);
                JCheckBox endangle_free_field = (JCheckBox) this.getComponent(11);

                JTextField radius_field = (JTextField) this.getComponent(13);
                JCheckBox radius_free_field = (JCheckBox) this.getComponent(14);

                startangle_field.setText(df.format(startAngle * 180.0 / Math.PI));
                endangle_field.setText(df.format(endAngle * 180.0 / Math.PI));

                startangle_free_field.setSelected(ce.startangle_free);
                endangle_free_field.setSelected(ce.endangle_free);

                radius_field.setText(df.format(ce.radius));
                
                radius_free_field.setSelected(ce.radius_free);

                JCheckBox tangent_field = (JCheckBox) this.getComponent(16);
                tangent_field.setSelected(ce.tangent);

                JTextField y_center_field = (JTextField) this.getComponent(18);
                JCheckBox y_center_free_field = (JCheckBox) this.getComponent(19);

                JTextField x_center_field = (JTextField) this.getComponent(21);
                JCheckBox x_center_free_field = (JCheckBox) this.getComponent(22);

                Point2D center = geo.supportingCircle().center();

                y_center_field.setText(df.format(center.y() * 2.0));
                x_center_field.setText(df.format(center.x()));

                y_center_free_field.setSelected(ce.y_center_free);
                x_center_free_field.setSelected(ce.x_center_free);

            } else if (ce.shape == contourelement.Shape.LINE) {
                LineSegment2D geo = (LineSegment2D) ce.curve;
                double angle = geo.direction().angle();
                JTextField angle_field = (JTextField) this.getComponent(7);
                JCheckBox angle_free_field = (JCheckBox) this.getComponent(8);

                angle_field.setText(df.format(angle * 180.0 / Math.PI));
               
                angle_free_field.setSelected(ce.angle_free);

                JCheckBox tangent_field = (JCheckBox) this.getComponent(10);

                tangent_field.setSelected(ce.tangent);

            }
        }
        this.setEditableFields();

    }

    /**
     * set textfields editable for constrainted values
     */
    public void setEditableFields() {

        contourelement ce = this.current_ce;

        JTextField y_field = (JTextField) this.getComponent(1);
        JTextField x_field = (JTextField) this.getComponent(4);

        y_field.setEditable(!ce.y_free);
        x_field.setEditable(!ce.x_free);

        if (prev_ce != null) {
            if (ce.shape == contourelement.Shape.ARC) {

                JTextField startangle_field = (JTextField) this.getComponent(7);
                JTextField endangle_field = (JTextField) this.getComponent(10);
                JTextField radius_field = (JTextField) this.getComponent(13);

                startangle_field.setEditable(!ce.startangle_free);
                endangle_field.setEditable(!ce.endangle_free);
                radius_field.setEditable(!ce.radius_free);

                JTextField y_center_field = (JTextField) this.getComponent(18);
                JTextField x_center_field = (JTextField) this.getComponent(21);

                y_center_field.setEditable(!ce.y_center_free);
                x_center_field.setEditable(!ce.x_center_free);

            } else if (ce.shape == contourelement.Shape.LINE) {

                JTextField angle_field = (JTextField) this.getComponent(7);

                if (ce.axis_movement == 0) { // no orthogonal line
                    angle_field.setEditable(!ce.angle_free);
                }

            }
        }

    }

    public contourelement getContourelement() {
        contourelement ce = this.current_ce;

        JTextField y_field = (JTextField) this.getComponent(1);
        JCheckBox y_free_field = (JCheckBox) this.getComponent(2);
        JTextField x_field = (JTextField) this.getComponent(4);
        JCheckBox x_free_field = (JCheckBox) this.getComponent(5);

        ce.points.getLast().y = Double.parseDouble(y_field.getText()) * 0.5;
        ce.points.getLast().x = Double.parseDouble(x_field.getText());

        ce.y_free = y_free_field.isSelected();
        ce.x_free = x_free_field.isSelected();

        if (prev_ce != null) {
            if (ce.shape == contourelement.Shape.ARC) {
                JTextField startangle_field = (JTextField) this.getComponent(7);
                JCheckBox startangle_free_field = (JCheckBox) this.getComponent(8);

                JTextField endangle_field = (JTextField) this.getComponent(10);
                JCheckBox endangle_free_field = (JCheckBox) this.getComponent(11);

                JTextField radius_field = (JTextField) this.getComponent(13);
                JCheckBox radius_free_field = (JCheckBox) this.getComponent(14);

                JCheckBox tangent_field = (JCheckBox) this.getComponent(16);

                JTextField y_center_field = (JTextField) this.getComponent(18);
                JCheckBox y_center_free_field = (JCheckBox) this.getComponent(19);
                JTextField x_center_field = (JTextField) this.getComponent(21);
                JCheckBox x_center_free_field = (JCheckBox) this.getComponent(22);

                ce.startangle = Double.parseDouble(startangle_field.getText()) * Math.PI / 180.0;
                ce.endangle = Double.parseDouble(endangle_field.getText()) * Math.PI / 180.0;

                ce.startangle_free = startangle_free_field.isSelected();
                ce.endangle_free = endangle_free_field.isSelected();

                ce.radius = Double.parseDouble(radius_field.getText());
                ce.radius_free = radius_free_field.isSelected();

                ce.tangent = tangent_field.isSelected();

                Point2D center = new Point2D(Double.parseDouble(x_center_field.getText()),
                        Double.parseDouble(y_center_field.getText()) * 0.5);
                ce.center = center;
                ce.y_center_free = y_center_free_field.isSelected();
                ce.x_center_free = x_center_free_field.isSelected();

            } else if (ce.shape == contourelement.Shape.LINE) {

                JTextField angle_field = (JTextField) this.getComponent(7);
                JCheckBox angle_free_field = (JCheckBox) this.getComponent(8);

                JCheckBox tangent_field = (JCheckBox) this.getComponent(10);

                ce.angle = Double.parseDouble(angle_field.getText()) * Math.PI / 180.0;
                ce.angle_free = angle_free_field.isSelected();

                ce.tangent = tangent_field.isSelected();
            }
        }

        return ce;
    }

}
