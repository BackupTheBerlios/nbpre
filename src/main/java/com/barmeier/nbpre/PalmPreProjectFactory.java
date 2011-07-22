/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre;

import java.io.IOException;
import java.util.Collections;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

/**
 *
 * @author barmeier
 */
@org.openide.util.lookup.ServiceProvider(service = ProjectFactory.class)
public class PalmPreProjectFactory implements ProjectFactory {

    public static final String APP_INFO_FILE = "appinfo.json";
    public static final String APP_DIR = "app";

    //Specifies when a project is a project, i.e.,
    //if the project directory "texts" is present:
    @Override
    public boolean isProject(FileObject projectDirectory) {
        boolean containsProject = false;
        boolean isSynergyProject = false;
        // check for simple project
        containsProject = projectDirectory.getFileObject(APP_INFO_FILE) != null;
        if (!containsProject) {
            isSynergyProject = isSynergyProject(projectDirectory);
        }
        return containsProject || isSynergyProject;
    }

    private boolean isSynergyProject(FileObject projectDirectory) {
        boolean isSynergyProject = false;
        System.out.println(projectDirectory);
        // check for synergy project
        // collect bool Flags
        for (FileObject o : Collections.list(projectDirectory.getFolders(false))) {
            isSynergyProject = o.getFileObject(APP_INFO_FILE) != null || isSynergyProject;
        }
        return isSynergyProject;
    }

    //Specifies when the project will be opened, i.e.,
    //if the project exists:
    @Override
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        return isProject(dir) ? new PalmPreProject(dir, state, isSynergyProject(dir)) : null;
    }

    @Override
    public void saveProject(final Project project) throws IOException, ClassCastException {
        FileObject projectRoot = project.getProjectDirectory();
        if (projectRoot.getFileObject(APP_INFO_FILE) == null) {
            throw new IOException("Project dir " + projectRoot.getPath()
                    + " deleted,"
                    + " cannot save project");
        }
        //Force creation of the texts dir if it was deleted:
        ((PalmPreProject) project).getAppFolder(true);
    }
}
