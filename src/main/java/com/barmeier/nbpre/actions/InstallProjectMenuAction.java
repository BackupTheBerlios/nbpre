/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingWorker;
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
        putValue(NAME, "Install App");
        this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    PalmPreProjectPacker packer = new PalmPreProjectPacker();
                    packer.packProject(project);
                    PalmPreProjectInstaller installer = new PalmPreProjectInstaller();
                    installer.installProject(project);
                    return true;
                }
            };
            worker.execute();
        } catch (/*NotYetConfigured*/Exception ex) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(ex.getMessage());
            DialogDisplayer.getDefault().notify(nd);
        }
    }
}
