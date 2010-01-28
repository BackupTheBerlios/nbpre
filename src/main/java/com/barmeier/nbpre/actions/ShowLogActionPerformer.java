/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre.actions;

import com.barmeier.nbpre.PalmPreProject;
import com.barmeier.nbpre.PalmPreProjectFactory;
import com.barmeier.nbpre.options.PalmSDKSettingsPanel;
import com.barmeier.nbpre.utils.ApplicationProperties;
import java.util.concurrent.Future;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.MainProjectSensitiveActions;
import org.netbeans.spi.project.ui.support.ProjectActionPerformer;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public final class ShowLogActionPerformer implements ProjectActionPerformer {

    public static Action createInstance(FileObject o) {
        String icon = (String) o.getAttribute("iconBase");
        String displayName = (String) o.getAttribute("displayName");

        Action action = MainProjectSensitiveActions.mainProjectSensitiveAction(
                new ShowLogActionPerformer(), displayName, new ImageIcon(ImageUtilities.loadImage(icon)));
        action.putValue("iconBase", icon); // support for small/large icons in tolbar (e.g. icon.png/icon24.png)
        return action;
    }

    public boolean enable(Project project) {
        return org.netbeans.api.project.ui.OpenProjects.getDefault().getMainProject() instanceof PalmPreProject;

    }

    public void perform(Project project) {
        FileObject projectRoot = project.getProjectDirectory();
        FileObject appInfo = projectRoot.getFileObject(PalmPreProjectFactory.APP_INFO_FILE);
        ApplicationProperties app = new ApplicationProperties(appInfo.getPath());

        InputOutput io= IOProvider.getDefault().getIO("Palm Log for <"+app.getId()+">",true);
        ExecutionDescriptor ed = new ExecutionDescriptor().inputOutput(io).frontWindow(true).controllable(true);
        ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(
                NbPreferences.forModule(PalmSDKSettingsPanel.class).get("logger", "")).
                addArgument(app.getId()).
                addArgument("-f");
        ExecutionService service = ExecutionService.newService(processBuilder, ed, "palm log");
        Future<Integer> task = service.run();

    }
}

