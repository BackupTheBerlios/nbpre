/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre.actions;

import com.barmeier.nbpre.PalmPreProject;
import com.barmeier.nbpre.PalmPreProjectFactory;
import com.barmeier.nbpre.VMSelector;
import com.barmeier.nbpre.options.PalmSDKSettingsPanel;
import com.barmeier.nbpre.utils.ApplicationProperties;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionDescriptor.InputProcessorFactory;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.api.extexecution.input.InputProcessor;
import org.netbeans.api.extexecution.input.InputProcessors;
import org.netbeans.api.extexecution.input.LineProcessor;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.MainProjectSensitiveActions;
import org.netbeans.spi.project.ui.support.ProjectActionPerformer;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public final class StartEmulatorActionPerformer implements ProjectActionPerformer, ActionListener {
    private static boolean isRunning=false;

    public static Action createInstance(FileObject o) {
        String icon = (String) o.getAttribute("iconBase");
        String displayName = (String) o.getAttribute("displayName");

        Action action = MainProjectSensitiveActions.mainProjectSensitiveAction(
                new StartEmulatorActionPerformer(), displayName, new ImageIcon(ImageUtilities.loadImage(icon)));
        action.putValue("iconBase", icon); // support for small/large icons in tolbar (e.g. icon.png/icon24.png)
        return action;
    }

    public boolean enable(Project project) {
        return org.netbeans.api.project.ui.OpenProjects.getDefault().getMainProject() instanceof PalmPreProject && !isRunning;

    }

    public void perform(Project project) {


        FileObject projectRoot = project.getProjectDirectory();
        FileObject appInfo = projectRoot.getFileObject(PalmPreProjectFactory.APP_INFO_FILE);
        final ApplicationProperties app = new ApplicationProperties(appInfo.getPath());

        final ArrayList<String> al = new ArrayList<String>();
        final InputProcessorFactory ipf = new InputProcessorFactory () {

            public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
                return InputProcessors.bridge(new LineProcessor() {
                    public void processLine(String line) {
                        al.add(line);
                    }

                    public void reset() {
                    }

                    public void close() {
                    }
                });
            }
        };

        InputOutput io= IOProvider.getDefault().getIO("Palm Log for <"+app.getId()+">",true);
        ExecutionDescriptor ed = new ExecutionDescriptor().inputOutput(io)
                .frontWindow(true)
                .outProcessorFactory(ipf)
                .controllable(true);
        ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(
                NbPreferences.forModule(PalmSDKSettingsPanel.class).get("emulator", "")).addArgument("--list");
        ExecutionService service = ExecutionService.newService(processBuilder, ed, "palm log");
        Future<Integer> task = service.run();
        try {
            task.get();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        VMSelector vms = new VMSelector();
        DialogDescriptor d=null;
        vms.setVMs(al);
        d = new DialogDescriptor(vms,"Choose your VM", true, this);
        DialogDisplayer.getDefault().notifyLater(d);
        isRunning=true;

    }

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

