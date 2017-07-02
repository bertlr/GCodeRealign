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


/**
 *
 * @author Herbert Roider <herbert@roider.at>
 */
public class PanelLinesForm extends JPanel {

    public LinkedList<contourelement> contour;
    public LinkedList<PanelContourelement> panels;

    PanelLinesForm() {
        super();
        panels = new LinkedList<>();

    }

    public void createPanels(LinkedList<contourelement> _contour) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        contour = _contour;

        contourelement prev_ce = null;

        for (contourelement current_ce : contour) {
            PanelContourelement panel = new PanelContourelement(current_ce, prev_ce);
            this.add(panel);

            prev_ce = current_ce;

            panels.add(panel);
        }
        fillForm();
    }

    public void fillForm() {

        for (int i = 0; i < contour.size(); i++) {
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
