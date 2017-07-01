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

import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.roiderh.gcodeviewer.contourelement;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


/**
 *
 * @author Herbert Roider <herbert@roider.at>
 */
public class PanelLinesForm extends JPanel implements ActionListener, FocusListener {

    public LinkedList<contourelement> contour;
    public LinkedList<PanelContourelement> panels;

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
        System.out.println("focusGained...");

    }

    @Override
    public void focusLost(FocusEvent e) {
        System.out.println("focusLost...");
    }

    public void createPanels(LinkedList<contourelement> _contour) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        contour = _contour;

        contourelement prev_ce = null;

        for (contourelement current_ce : contour) {
            PanelContourelement panel = new PanelContourelement(current_ce, prev_ce);
            panel.addFocusListener(this);
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

            PanelContourelement panel = panels.get(i);
            panel.fillForm();

        }

    }

    public LinkedList<contourelement> getContour() {

        for (int i = 0; i < contour.size(); i++) {
            contourelement ce = contour.get(i);

            PanelContourelement panel = panels.get(i);
            ce = panel.getContourelement();

        }

        return contour;
    }

}
