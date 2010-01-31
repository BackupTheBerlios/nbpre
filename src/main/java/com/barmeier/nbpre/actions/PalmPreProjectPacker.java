/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.barmeier.nbpre.actions;

import com.barmeier.nbpre.NotYetConfiguredException;
import com.barmeier.nbpre.options.PalmSDKSettingsPanel;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.NotifyDescriptor;
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
public class PalmPreProjectPacker {
    DataObject dataObject;
    String fileName;

    public PalmPreProjectPacker() {
        this.dataObject = null;
    }

    public void packProject(Project project) throws NotYetConfiguredException {
        ProcessBuilder procBuilder;
        Process process;
        Map<String, String> env;
//        File currDir;
        List<String> cmd;
        String line;
        InputOutput io;
        OutputWriter outputWriter;

        // First we check if everything is in place and reachable
        String filename = NbPreferences.forModule(PalmSDKSettingsPanel.class).get("packer", "");
        File executable = new File(filename);
        if (!executable.exists() || !executable.canExecute()) {

            throw new NotYetConfiguredException("The palm-pack executable " +
                    "is not executable or cannot be found.\n Pleas check " +
                    "permissions and location of the file.\n Actually " +
                    "configured is: ["+filename+"]\n\n You can change this " +
                    "in the Toole menu under\n" +
                    "Tools->Options->Miscellaneous->PalmSDK.");
        }

        LifecycleManager.getDefault().saveAll();

        String projectPath = project.getProjectDirectory().getPath();

        // get an output window tab
        io = IOProvider.getDefault().getIO("PalmPre", false);
        io.select();
        outputWriter = io.getOut();

        // construct the SWI Prolog process command
        cmd = new ArrayList<String>();
        cmd.add(filename);
        cmd.add("-o");
        cmd.add(projectPath);
        cmd.add(projectPath);

        procBuilder = new ProcessBuilder(cmd);
        procBuilder.redirectErrorStream(true);
        // also s/b able to merge it into OutputWriter
//        env = procBuilder.environment();
//        env.put("VAR1", "myValue");
//        env.remove("OTHERVAR");
//        env.put("VAR2", env.get("VAR1") + "suffix");
//        currDir = procBuilder.directory();
//        if (currDir != null) {
//            System.out.printf("Current directory is %s.", currDir.toString());
//        }
//        procBuilder.directory(new File("myDir"));
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
