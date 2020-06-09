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
package org.roiderh.gcoderealign;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.application.Platform;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReferences;
import org.roiderh.gcoderealigndialogs.DialogGenerateCode;

@ActionID(
        category = "File",
        id = "org.roiderh.gcoderealign.GCodeRealignActionListener"
)
@ActionRegistration(
        iconBase = "org/roiderh/gcoderealign/hi22-gcode-realign.png",
        displayName = "#CTL_GCodeRealignActionListener"
)
@ActionReferences({
    @ActionReference(path = "Toolbars/File", position = 0),
    @ActionReference(path = "Editors/text/plain/Popup"),
    @ActionReference(path = "Editors/text/x-nc/Popup")
})

public final class GCodeRealignActionListener implements ActionListener {

    private String selectedText;

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent ed = org.netbeans.api.editor.EditorRegistry.lastFocusedComponent();
        if (ed == null) {
            JOptionPane.showMessageDialog(null, "Error: no open editor");
            return;
        }
        this.selectedText = ed.getSelectedText();
        if (selectedText == null) {
            JOptionPane.showMessageDialog(null, "Error: no selected Text");
            return;
        }
        if (selectedText.length() < 1) {
            JOptionPane.showMessageDialog(null, "Error: no selected Text");
            return;
        }

        /* Create and display the dialog */
        //Platform.runLater(new Runnable() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    DialogGenerateCode dialog = new DialogGenerateCode(selectedText, org.openide.windows.WindowManager.getDefault().getMainWindow(), true);
                    dialog.initGui();
                    dialog.setLocationRelativeTo(org.openide.windows.WindowManager.getDefault().getMainWindow());
                    
                    dialog.setVisible(true);
                    if (dialog.canceled == false) {
                        JTextComponent ed = org.netbeans.api.editor.EditorRegistry.lastFocusedComponent();
                        if (ed == null) {
                            return;
                        }
                        ed.replaceSelection(dialog.g_code);
                    }

                } catch (Exception e1) {
                    System.out.println("Error " + e1.toString());
                    JOptionPane.showMessageDialog(null, "Error: " + e1.getMessage());
                    return;
                }

            }
        }
        );
    }
}
