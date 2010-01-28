/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre.template;

import com.barmeier.nbpre.options.PalmSDKSettingsPanel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author barmeier
 */
public class PalmGenerateProject {

    DataObject dataObject;
    String fileName;

    public PalmGenerateProject() {
        this.dataObject = null;
    }

    public static void generateProject(String id, String version, String vendor, String title, String projectPath) {
        ProcessBuilder procBuilder;
        Process process;
        Map<String, String> env;
        List<String> cmd;
        String line;

        // construct the SWI Prolog process command
        //palm-generate -p "{id:com.mystuff.hello, version:'2.1', vendor:'My Stuff', title:'Hello There'}"
        cmd = new ArrayList<String>();
        cmd.add(NbPreferences.forModule(PalmSDKSettingsPanel.class).get("generator", ""));
        cmd.add("-p");
        cmd.add("{id:" + id + ", version:'" + version + "', vendor:'"
                + vendor + "', title:'" + title + "'}");
        cmd.add(projectPath);

        procBuilder = new ProcessBuilder(cmd);
        procBuilder.redirectErrorStream(true);

        try {
            process = procBuilder.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            // TODO: might want to clear the output window first...
            while ((line = br.readLine()) != null) {
            }
            // TODO: close outputwriter
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
