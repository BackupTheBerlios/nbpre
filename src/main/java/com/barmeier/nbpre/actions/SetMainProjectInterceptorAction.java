/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.barmeier.nbpre.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.api.project.Project;

/**
 *
 * @author barmeier
 */
public class SetMainProjectInterceptorAction extends AbstractAction {

    Project project;

    public SetMainProjectInterceptorAction(Project project) {
        putValue(NAME, "Set as Main Project");
        this.project = project;
    }

    public void actionPerformed(ActionEvent e) {

    }
}
