/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.barmeier.nbpre.actions;

import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class JSLintChecker extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        EditorCookie editorCookie = activatedNodes[0].getLookup().lookup(EditorCookie.class);
        // TODO use editorCookie
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(JSLintChecker.class, "CTL_JSLintChecker");
    }

    protected Class[] cookieClasses() {
        return new Class[] {EditorCookie.class};
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

