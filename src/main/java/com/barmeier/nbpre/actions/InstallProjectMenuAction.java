/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre.actions;

import com.barmeier.nbpre.NotYetConfiguredException;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author barmeier
 */
public class InstallProjectMenuAction extends AbstractAction {

    Project project;

    public InstallProjectMenuAction(Project project) {
        putValue(NAME, "Install project");
        this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PalmPreProjectInstaller installer = new PalmPreProjectInstaller();
        try {
            installer.installProject(project);
        } catch (NotYetConfiguredException ex) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(ex.getMessage());
            DialogDisplayer.getDefault().notify(nd);
        }
    }
}
