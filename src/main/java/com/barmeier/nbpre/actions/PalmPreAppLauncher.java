/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.barmeier.nbpre.actions;

import com.barmeier.nbpre.*;
import com.barmeier.nbpre.options.PalmSDKSettingsPanel;
import com.barmeier.nbpre.utils.ApplicationProperties;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.openide.LifecycleManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 * @author barmeier
 */
public class PalmPreAppLauncher {
    DataObject dataObject;
    String fileName;

    public PalmPreAppLauncher() {
        this.dataObject = null;
    }

    public void launchApp(Project project) {
        ProcessBuilder procBuilder;
        Process process;
        Map<String, String> env;
//        File currDir;
        List<String> cmd;
        String line;
        InputOutput io;
        OutputWriter outputWriter;

        LifecycleManager.getDefault().saveAll();
        // Pack App
        PalmPreProjectPacker pppp = new PalmPreProjectPacker();
        pppp.packProject(project);
        //Install App
        PalmPreProjectInstaller pppi = new PalmPreProjectInstaller();
        pppi.installProject(project);

        //Launch App
        FileObject projectRoot = project.getProjectDirectory();
        FileObject appInfo = projectRoot.getFileObject(PalmPreProjectFactory.APP_INFO_FILE);
        ApplicationProperties app = new ApplicationProperties(appInfo.getPath());

        // construct the SWI Prolog process command
        cmd = new ArrayList<String>();
        cmd.add(NbPreferences.forModule(PalmSDKSettingsPanel.class).get("installer", ""));
        cmd.add(projectRoot.getPath()+File.separator+app.getId()+"_"+app.getVersion()+"_all.ipk");

        // get an output window tab
        io = IOProvider.getDefault().getIO("PalmPre", false);
        io.select();
        outputWriter = io.getOut();

        // construct the SWI Prolog process command
        cmd = new ArrayList<String>();
        cmd.add(NbPreferences.forModule(PalmSDKSettingsPanel.class).get("launcher", ""));
        cmd.add(app.getId());

        procBuilder = new ProcessBuilder(cmd);
        procBuilder.redirectErrorStream(true);

        try {
            process = procBuilder.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            // TODO: might want to clear the output window first...
            outputWriter.printf("Output of running %s is:\n\n", cmd.toString());
            while ((line = br.readLine()) != null) {
                outputWriter.println(line);
            }
            // TODO: close outputwriter
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
