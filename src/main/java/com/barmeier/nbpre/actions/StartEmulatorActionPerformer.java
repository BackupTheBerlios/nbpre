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
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
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
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public final class StartEmulatorActionPerformer implements ProjectActionPerformer, ActionListener {

    private static boolean isRunning = false;
    private VMSelector vms;

    public static Action createInstance(FileObject o) {
        String icon = (String) o.getAttribute("iconBase");
        String displayName = (String) o.getAttribute("displayName");

        Action action = MainProjectSensitiveActions.mainProjectSensitiveAction(
                new StartEmulatorActionPerformer(), displayName, new ImageIcon(ImageUtilities.loadImage(icon)));
        action.putValue("iconBase", icon); // support for small/large icons in tolbar (e.g. icon.png/icon24.png)
        return action;
    }

    @Override
    public boolean enable(Project project) {
        return org.netbeans.api.project.ui.OpenProjects.getDefault().getMainProject() instanceof PalmPreProject && !isRunning;

    }

    private boolean checkNovacom() {
        return true;
    }

    private boolean checkVirtualbox() {
        return true;
    }

    private boolean isNovaRunning() {
        Socket s;
        try {
           s = new Socket("localhost", 6968);
           s.close();
        } catch (UnknownHostException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
        return true;


    }

    @Override
    public void perform(Project project) {
        // First we check if everything is in place and reachable
        String filename = NbPreferences.forModule(PalmSDKSettingsPanel.class).get("vBoxManage", "");
        File executable = new File(filename);
        if (!executable.exists() || !executable.canExecute()) {
            NotifyDescriptor nd = new NotifyDescriptor.Message("The vBoxManage executable "
                    + "is not executable or cannot be found.\n Pleas check "
                    + "permissions and location of the file.\n Actually "
                    + "configured is: [" + filename + "]\n\n You can change this in the Toole menu under\n"
                    + "Tools->Options->Miscellaneous->PalmSDK in the \"VirtualBox Settings\" tab.");
            DialogDisplayer.getDefault().notify(nd);
            return;
        }

        if (!isNovaRunning()) {
            NotifyDescriptor nd = new NotifyDescriptor.Message("The novacomd is not running or is not reachable.\n\n" +
                    "Please make sure you have started the novacomd program and added port 6968\n" +
                    "to your firewall exceptions.");
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        FileObject projectRoot = project.getProjectDirectory();
        FileObject appInfo = projectRoot.getFileObject(PalmPreProjectFactory.APP_INFO_FILE);
        final ApplicationProperties app = new ApplicationProperties(appInfo.getPath());

        final ArrayList<String> al = new ArrayList<String>();
        final InputProcessorFactory ipf = new InputProcessorFactory() {

            @Override
            public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
                return InputProcessors.bridge(new LineProcessor() {

                    @Override
                    public void processLine(String line) {
                        if (line.indexOf("SDK") != -1) {
                            al.add(line.substring(0,line.indexOf("{")).replace("\"", "").trim());
                        }
                    }

                    @Override
                    public void reset() {
                    }

                    @Override
                    public void close() {
                    }
                });
            }
        };

        InputOutput io = IOProvider.getDefault().getIO("Palm Log for <" + app.getId() + ">", true);
        ExecutionDescriptor ed = new ExecutionDescriptor().inputOutput(io).frontWindow(true).outProcessorFactory(ipf).controllable(true);

        ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(
                filename).addArgument("list").
                addArgument("vms");
        ExecutionService service = ExecutionService.newService(processBuilder, ed, "palm vm");
        Future<Integer> task = service.run();
        try {
            task.get();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        vms = new VMSelector();
        DialogDescriptor d = null;
        vms.setVMs(al);
        d = new DialogDescriptor(vms, "Choose your VM", true, this);
        DialogDisplayer.getDefault().notifyLater(d);
        isRunning = true;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != DialogDescriptor.CANCEL_OPTION) {
            String x = vms.getVMString();
            InputOutput io = IOProvider.getDefault().getIO("Palm VM Log", true);
            ExecutionDescriptor ed = new ExecutionDescriptor().inputOutput(io).frontWindow(true).controllable(true);

            String filename = NbPreferences.forModule(PalmSDKSettingsPanel.class).get("virtualBox", "");
            ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(
                    filename).addArgument("--startvm").addArgument(x);
            System.out.println(filename +" "+"--startvm \""+x+"\"" );
            ExecutionService service = ExecutionService.newService(processBuilder, ed, "palm vm");
            Future<Integer> task = service.run();
//            try {
//                task.get();
//            } catch (InterruptedException ex) {
//                Exceptions.printStackTrace(ex);
//            } catch (ExecutionException ex) {
//                Exceptions.printStackTrace(ex);
//            }
        }
    }
}

