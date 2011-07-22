    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre.template;

import com.barmeier.nbpre.options.PalmSDKSettingsPanel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
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

    private static void createStandardVarsFile(String path) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(path + "/stdvars.js"));
        pw.println("var Foundations = IMPORTS.foundations;");
        pw.println("var Future = Foundations.Control.Future;");
        pw.println("var PalmCall = Foundations.Comms.PalmCall;");
        pw.close();
    }

    private static void createJSONFiles(String path, String templateId) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(path + "/sources.json"));
        pw.println("[");
        pw.println("   {");
        pw.println("      \"library\":{");
        pw.println("         \"name\":\"foundations\",");
        pw.println("         \"version\":\"1.0\"");
        pw.println("      }");
        pw.println("   },");
        pw.println("   {");
        pw.println("      \"source\":\"stdvars.js\"");
        pw.println("   },");
        pw.println("   {");
        pw.println("      \"source\":\"service.js\"");
        pw.println("   }");
        pw.println("]");
        pw.close();
            
        pw = new PrintWriter(new FileWriter(path + "/services.json"));
        pw.println("        {");
        pw.println("   \"id\":\"" + templateId + ".service\",");
        pw.println("   \"description\":\"Test Contact Service\",");
        pw.println("   \"engine\":\"node\",");
        pw.println("   \"activityTimeout\":30,");
        pw.println("   \"services\":[");
        pw.println("      {");
        pw.println("         \"name\":\"" + templateId + ".service\",");
        pw.println("         \"description\":\"Test Contact\",");
        pw.println("         \"globalized\":false,");
        pw.println("         \"commands\":[");
        pw.println("            {");
        pw.println("               \"name\":\"createContactAccount\",");
        pw.println("               \"assistant\":\"createContactAccountAssistant\",");
        pw.println("               \"public\":true");
        pw.println("            },");
        pw.println("            {");
        pw.println("               \"name\":\"writeContacts\",");
        pw.println("               \"assistant\":\"writeContactsAssistant\",");
        pw.println("               \"public\":true");
        pw.println("            }");
        pw.println("         ]");
        pw.println("      }");
        pw.println("   ]");
        pw.println("}");
        pw.close();
    }

    public static void generateProject(String id, String version, String vendor,
            String title, Boolean isSynergyProject, String projectName,
            String projectPath, String templateId) throws IOException {

        ProcessBuilder procBuilder;
        Process process;
        Map<String, String> env;
        List<String> cmd;
        String line;
        String projectCreationPath = projectPath;


        if (isSynergyProject) {
            projectCreationPath = projectCreationPath + "/" + projectName;
            new File(projectCreationPath).mkdirs();
        }

        // First we check if everything is in place and reachable
        String filename = NbPreferences.forModule(PalmSDKSettingsPanel.class).get("generator", "");
        File executable = new File(filename);
        if (!executable.exists() || !executable.canExecute()) {

            NotifyDescriptor nd = new NotifyDescriptor.Message("The palm-generator executable "
                    + "is not executable or cannot be found.\n Pleas check "
                    + "permissions and location of the file.\n Actually "
                    + "configured is: [" + filename + "]\n\n You can change this "
                    + "in the Toole menu under\n"
                    + "Tools->Options->Miscellaneous->PalmSDK.");
            DialogDisplayer.getDefault().notify(nd);
            return;
        }

        // construct the SWI Prolog process command
        // palm-generate -p "{id:com.mystuff.hello, version:'2.1', vendor:'My Stuff', title:'Hello There'}"
        cmd = new ArrayList<String>();
        cmd.add(NbPreferences.forModule(PalmSDKSettingsPanel.class).get("generator", ""));
        cmd.add("-p");
        cmd.add("{id:" + id + ", version:'" + version + "', vendor:'"
                + vendor + "', title:'" + title + "'}");
        cmd.add(projectCreationPath);

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

        if (isSynergyProject) {
            //Creating and filling service folder with standard files
            new File(projectPath + "/" + projectName + ".service/configuration/db/kind").mkdirs();
            createStandardVarsFile(projectPath + "/" + projectName + ".service");
            createJSONFiles(projectPath + "/" + projectName + ".service", templateId);

            new File(projectPath + "/" + projectName + ".package").mkdir();
            new File(projectPath + "/" + projectName + ".accts").mkdir();
            
        }
    }
}
