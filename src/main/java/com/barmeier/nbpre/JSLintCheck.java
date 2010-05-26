/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre;

import com.barmeier.nbpre.utils.JSLintChecker;
import java.io.IOException;
import java.io.InputStream;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;

public final class JSLintCheck extends CookieAction {

    @Override
    protected void performAction(Node[] activatedNodes) {
        InputStream is = null;
        try {
            EditorCookie editorCookie = activatedNodes[0].getLookup().lookup(EditorCookie.class);
            DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
            String path = (dataObject.getPrimaryFile()).getPath();
            is = ((org.openide.text.CloneableEditorSupport) editorCookie).getInputStream();
            JSLintChecker jslc = new JSLintChecker();
            jslc.checkFromStream(is, path, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
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

