/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre.utils;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author barmeier
 */
public class JSLintChecker {

    public boolean checkFile(String filename, boolean clear) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
        return checkFromStream(fis,filename, clear);

    }

    public boolean checkFromStream(InputStream stream, String filename, boolean clear) throws IOException {
        JSLintOutputListener jlo = new JSLintOutputListener();

        // "XML Structure" tab is created in Output Window for writing the list of tags:
        InputOutput io = IOProvider.getDefault().getIO("JSLint", false);

        io.select(); //"XML Structure" tab is selected
        if (clear) {
            io.getOut().reset();
        }
        //Use the NetBeans org.openide.xml.XMLUtil class to create a org.w3c.dom.Document:
        JSLint jsLint = new JSLint();
        List<Issue> issueList = jsLint.lint("TEST", new InputStreamReader(stream));
        io.getOut().println("JSLint execution for " + filename + " results in " + issueList.size() + " issues.");
        for (Issue issue : issueList) {
            //Print the element and its attributes to the Output window:
            io.getOut().println(filename + ":" + issue.getLine() + ":" + issue.getCharacter() + " " + issue.getReason(), jlo);
        }
        return issueList.size()>0;
    }

    public boolean checkProjectFiles(DataObject dataObject, boolean clear) throws IOException {
        boolean result = true;
        for (FileObject file:dataObject.files()) {
            result = result && checkFile(file.getPath(), clear);
        }
        return result;
    }
}
