/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre;

import com.barmeier.nbpre.actions.AddSceneAction;
import com.barmeier.nbpre.actions.InstallProjectMenuAction;
import com.barmeier.nbpre.actions.LaunchProjectMenuAction;
import com.barmeier.nbpre.actions.PackProjectMenuAction;
import java.awt.Image;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import javax.swing.Action;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author barmeier
 */
public class PalmPreLogicalView implements LogicalViewProvider {

    private final PalmPreProject project;

    public PalmPreLogicalView(PalmPreProject project) {
        this.project = project;
    }

    @Override
    public org.openide.nodes.Node createLogicalView() {
        try {
            //Get the Text directory, creating if deleted
//            FileObject text = project.getAppFolder(true);

            FileObject text = project.getProjectDirectory();

            //Get the DataObject that represents it
            DataFolder textDataObject =
                    DataFolder.findFolder(text);

            //Get its default node-we'll wrap our node around it to change the
            //display name, icon, etc
            Node realTextFolderNode = textDataObject.getNodeDelegate();

            //This FilterNode will be our project node
            return new TextNode(realTextFolderNode, project);

        } catch (DataObjectNotFoundException donfe) {
            Exceptions.printStackTrace(donfe);
            //Fallback-the directory couldn't be created -
            //read-only filesystem or something evil happened
            return new AbstractNode(Children.LEAF);
        }
    }

    /** This is the node you actually see in the project tab for the project */
    private static final class TextNode extends FilterNode {

        final PalmPreProject project;

        public TextNode(Node node, PalmPreProject project) throws DataObjectNotFoundException {
            super(node, new FilterNode.Children(node),
                    //The projects system wants the project in the Node's lookup.
                    //NewAction and friends want the original Node's lookup.
                    //Make a merge of both
                    new ProxyLookup(new Lookup[]{Lookups.singleton(project),
                        node.getLookup()
                    }));
            this.project = project;
        }

        @Override
        public Action[] getActions(boolean arg0) {
            Action[] nodeActions = new Action[13];
            nodeActions[0] = new AddSceneAction(project);
            nodeActions[1] = new PackProjectMenuAction(project);
            nodeActions[2] = new InstallProjectMenuAction(project);
            nodeActions[3] = new LaunchProjectMenuAction(project);

            nodeActions[5] = CommonProjectActions.newFileAction();
            nodeActions[6] = CommonProjectActions.copyProjectAction();
            nodeActions[7] = CommonProjectActions.deleteProjectAction();
            nodeActions[9] = CommonProjectActions.setAsMainProjectAction();
            nodeActions[10] = CommonProjectActions.closeProjectAction();
            nodeActions[12] = CommonProjectActions.customizeProjectAction();
            return nodeActions;
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.loadImage("com/barmeier/nbpre/images/icon.png");
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public String getDisplayName() {
            return project.getProjectDirectory().getName();
        }
//
//        private class MyAction extends AbstractAction {
//
//            public MyAction() {
//                putValue(NAME, "Do Something");
//            }
//
//            public void actionPerformed(ActionEvent e) {
//                //APIObject obj = getLookup().lookup(APIObject.class);
//                JOptionPane.showMessageDialog(null, "Hello from "+project.getProjectDirectory().getPath());
//            }
//        }
    }

    @Override
    public Node findPath(Node root, Object target) {
        //leave unimplemented for now
        return null;
    }
}
