/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre.actions;

import com.barmeier.nbpre.options.PalmSDKSettingsPanel;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 * @author barmeier
 */
public class AddSceneAction extends AbstractAction {

    Project project;

    public AddSceneAction(Project project) {
        putValue(NAME, "Add Scene");
        this.project = project;
    }

    public void actionPerformed(ActionEvent e) {
        ProcessBuilder procBuilder;
        Process process;
        Map<String, String> env;
//        File currDir;
        List<String> cmd;
        String line;
        InputOutput io;
        OutputWriter outputWriter;

        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("Scene Name:", "Enter Scene Name");

        if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
            return;
        }

        String sceneName = nd.getInputText();

        String projectPath = project.getProjectDirectory().getPath();

        // get an output window tab
        io = IOProvider.getDefault().getIO("PalmPre", false);
        io.select();
        outputWriter = io.getOut();

        // construct the SWI Prolog process command
        cmd = new ArrayList<String>();
        cmd.add(NbPreferences.forModule(PalmSDKSettingsPanel.class).get("generator", ""));
        cmd.add("-t");
        cmd.add("new_scene");
        cmd.add("-p");
        cmd.add("name=" + sceneName);
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
        }

        try {
            FileObject fo = FileUtil.toFileObject(new File(projectPath + "/app/views/" + sceneName + "/"+sceneName+"-scene.html"));
            DataObject dataObject = DataObject.find(fo);
            EditCookie ec = dataObject.getLookup().lookup(EditCookie.class);
            if (ec != null) {
                ec.edit();
            }
            OpenCookie oc = dataObject.getLookup().lookup(OpenCookie.class);
            if (oc != null) {
                oc.open();
            }
            fo = FileUtil.toFileObject(new File(projectPath + "/app/assistants/" + sceneName + "-assistant.js"));
            dataObject = DataObject.find(fo);
            ec = dataObject.getLookup().lookup(EditCookie.class);
            if (ec != null) {
                ec.edit();
            }
            oc = dataObject.getLookup().lookup(OpenCookie.class);
            if (oc != null) {
                oc.open();
            }
        } catch (DataObjectNotFoundException dataObjectNotFoundException) {
            NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Exception(dataObjectNotFoundException);
            DialogDisplayer.getDefault().notify(notifyDescriptor);
        }
    }
}
