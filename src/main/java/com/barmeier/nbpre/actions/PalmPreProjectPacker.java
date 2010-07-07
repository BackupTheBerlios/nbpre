/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre.actions;

import com.barmeier.nbpre.NotYetConfiguredException;
import com.barmeier.nbpre.options.PalmSDKSettingsPanel;
import com.barmeier.nbpre.utils.JSLintChecker;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.SwingWorker;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * Packs a project for Palm Pre. This class uses the palm-package executable
 * configured in the options panel. If a package run is requested the class
 * checks if the "Run JSLint" option is selected. If yes JSlint will be started
 * for all JS files and the output will be collected in the JSLint tab.
 * @author barmeier
 */
public class PalmPreProjectPacker {

    /**
     * Runs JSLint for all JS files in a project. The method uses a SwingWorker
     * to execute JSLint in the backgroun to preserver responsiveness of the
     * GUI.
     * After processing all files the "dontLaunchAppOnWarnings" option is
     * checked to prevent start of packaging if warnings are present.
     * @param project The project to run JSLint for all containes JS files.
     * @param executable The program that creates the package.
     * @throws NotYetConfiguredException This exception is thrown if the user
     * has not yet configured the path to the executables.
     */
    public void jslintCheck(final Project project, final String executable) throws NotYetConfiguredException {

        boolean errors_found = false;
        boolean clearFlag = true;
        try {
            if (NbPreferences.forModule(PalmSDKSettingsPanel.class).getBoolean("runJSLint", true)) {
                JSLintChecker jslc = new JSLintChecker();
                Sources sources = ProjectUtils.getSources(project);
                for (SourceGroup sourceGroup : sources.getSourceGroups(Sources.TYPE_GENERIC)) {
                    FileObject rootFolder = sourceGroup.getRootFolder();
                    // notice the boolean parameter, that gives an enumerator which iterates recursively
                    Enumeration<? extends FileObject> children = rootFolder.getChildren(true);
                    while (children.hasMoreElements()) {
                        FileObject next = children.nextElement();
                        if (!next.isFolder() && next.getExt().equalsIgnoreCase("js")) {
                            System.out.println(next.getPath());
                            DataObject dataObject = DataObject.find(next);
                            errors_found = jslc.checkProjectFiles(dataObject, clearFlag) || errors_found;
                            clearFlag = false;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void executePackProject(Project project, String executable) {
        ProcessBuilder procBuilder;
        Process process;
        List<String> cmd;
        String line;
        InputOutput io;
        OutputWriter outputWriter;
        LifecycleManager.getDefault().saveAll();

        String projectPath = project.getProjectDirectory().getPath();

        // get an output window tab
        io = IOProvider.getDefault().getIO("PalmPre", false);
        io.select();
        outputWriter = io.getOut();

        // construct the SWI Prolog process command
        cmd = new ArrayList<String>();
        cmd.add(executable);
        cmd.add("-o");
        cmd.add(projectPath);
        cmd.add(projectPath);

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
        } finally {
            outputWriter.close();
        }
    }

    /**
     * Entry to initiate packaging. The method checks if "runJSLint" is set. If
     * yes the JSLint is applied to all JS files in the project.
     * @param project the project that will packed in this method
     * @throws NotYetConfiguredException This exception is thrown if the user
     * has not yet configured the path to the executables.
     */
    public void packProject(Project project) throws NotYetConfiguredException {

        // First we check if everything is in place and reachable
        String filename = NbPreferences.forModule(PalmSDKSettingsPanel.class).get("packer", "");
        File executable = new File(filename);
        if (!executable.exists() || !executable.canExecute()) {

            throw new NotYetConfiguredException("The palm-pack executable "
                    + "is not executable or cannot be found.\n Pleas check "
                    + "permissions and location of the file.\n Actually "
                    + "configured is: [" + filename + "]\n\n You can change this "
                    + "in the Toole menu under\n"
                    + "Tools->Options->Miscellaneous->PalmSDK.");
        }

        if (NbPreferences.forModule(PalmSDKSettingsPanel.class).getBoolean("runJSLint", true)) {
            jslintCheck(project, filename);
        } 
        executePackProject(project, filename);
        
    }
}
