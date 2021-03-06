/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.barmeier.nbpre.actions;

import com.barmeier.nbpre.NotYetConfiguredException;
import com.barmeier.nbpre.PalmPreProjectFactory;
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
public class PalmPreProjectInstaller {
    DataObject dataObject;
    String fileName;

    public PalmPreProjectInstaller() {
        this.dataObject = null;
    }

    public void installProject(Project project) throws NotYetConfiguredException{
        ProcessBuilder procBuilder;
        Process process;
        Map<String, String> env;

        List<String> cmd;
        String line;
        InputOutput io;
        OutputWriter outputWriter;

        LifecycleManager.getDefault().saveAll();
        // First we check if everything is in place and reachable
        String filename = NbPreferences.forModule(PalmSDKSettingsPanel.class).get("installer", "");
        File executable = new File(filename);
        if (!executable.exists() || !executable.canExecute()) {

            throw new NotYetConfiguredException("The palm-installer executable "
                    + "is not executable or cannot be found.\n Pleas check "
                    + "permissions and location of the file.\n Actually "
                    + "configured is: [" + filename + "]\n\n You can change " +
                    "this in the Toole menu under\n"
                    + "Tools->Options->Miscellaneous->PalmSDK.");
        }
        // get an output window tab
        io = IOProvider.getDefault().getIO("PalmPre", false);
        io.select();
        outputWriter = io.getOut();

        FileObject projectRoot = project.getProjectDirectory();
        FileObject appInfo = projectRoot.getFileObject(PalmPreProjectFactory.APP_INFO_FILE);
        ApplicationProperties app = new ApplicationProperties(appInfo.getPath());

        // construct the SWI Prolog process command
        cmd = new ArrayList<String>();
        cmd.add(filename);
        cmd.add(projectRoot.getPath()+File.separator+app.getId()+"_"+app.getVersion()+"_all.ipk");

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
