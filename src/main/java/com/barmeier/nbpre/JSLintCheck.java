/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre;

import com.barmeier.nbpre.utils.JSLintOutputListener;
import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public final class JSLintCheck extends CookieAction {

    @Override
    protected void performAction(Node[] activatedNodes) {
        EditorCookie editorCookie = activatedNodes[0].getLookup().lookup(EditorCookie.class);
        
        DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        String path = (dataObject.getPrimaryFile()).getPath();
        JSLintOutputListener jlo = new JSLintOutputListener();

        // "XML Structure" tab is created in Output Window for writing the list of tags:
        InputOutput io = IOProvider.getDefault().getIO("JSLint", false);
        
        io.select(); //"XML Structure" tab is selected
        try {
            io.getOut().reset();
            //Get the InputStream from the EditorCookie:
            InputStream is = ((org.openide.text.CloneableEditorSupport) editorCookie).getInputStream();
            //Use the NetBeans org.openide.xml.XMLUtil class to create a org.w3c.dom.Document:
            JSLint jsLint = new JSLint();
            List<Issue> issueList = jsLint.lint("TEST", new InputStreamReader(is));
            io.getOut().println("JSLint execution for "+path+" results in "+issueList.size()+" issues.");
            for (Issue issue : issueList) {
                //Print the element and its attributes to the Output window:
                io.getOut().println(path+":"+issue.getLine()+":"+issue.getCharacter()+" "+issue.getReason(),jlo);
            }
            //Close the InputStream:
            is.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    @Override
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    @Override
    public String getName() {
        return "Check with JSLint"; //NbBundle.getMessage(JSLintCheck.class, "CTL_JSLintCheck");
    }

    protected Class[] cookieClasses() {
        return new Class[]{EditorCookie.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

