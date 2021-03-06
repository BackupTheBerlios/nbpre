/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre.actions;

import com.barmeier.nbpre.NotYetConfiguredException;
import com.barmeier.nbpre.PalmPreProject;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.MainProjectSensitiveActions;
import org.netbeans.spi.project.ui.support.ProjectActionPerformer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;

public final class PalmPreInstallAppActionPerformer implements ProjectActionPerformer {

    public static Action createInstance(FileObject o) {
        String icon = (String) o.getAttribute("iconBase");
        String displayName = (String) o.getAttribute("displayName");

        Action action = MainProjectSensitiveActions.mainProjectSensitiveAction(
                new PalmPreInstallAppActionPerformer(), displayName, new ImageIcon(ImageUtilities.loadImage(icon)));
        action.putValue("iconBase", icon); // support for small/large icons in tolbar (e.g. icon.png/icon24.png)
        return action;
    }

    @Override
    public boolean enable(Project project) {
        return org.netbeans.api.project.ui.OpenProjects.getDefault().getMainProject() instanceof PalmPreProject;

    }

    @Override
    public void perform(final Project project) {

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

