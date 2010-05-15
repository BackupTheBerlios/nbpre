/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre.utils;

import java.io.File;
import java.util.StringTokenizer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;

/**
 *
 * @author barmeier
 */
public class JSLintOutputListener implements OutputListener {

    @Override
    public void outputLineAction(OutputEvent outputEvent) {
        //DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        String lineString = outputEvent.getLine();

        StringTokenizer st = new StringTokenizer(lineString, ":");
        String filename = "";

        filename = st.nextToken();


        try {
            FileObject fo = FileUtil.toFileObject(new File(filename));
            DataObject dataObject = DataObject.find(fo);
            EditCookie ec = dataObject.getLookup().lookup(EditCookie.class);
            LineCookie lc = (LineCookie) dataObject.getCookie (LineCookie.class);

            if (ec != null) {
                ec.edit();
            }
            OpenCookie oc = dataObject.getLookup().lookup(OpenCookie.class);
            if (oc != null) {
                oc.open();
            }

            String lineNumberString = st.nextToken();
            String columnNumberString = st.nextToken();
            //String raeson = st.nextToken();

            int lineNumber = Integer.parseInt(lineNumberString);
            int columnNumber = Integer.parseInt(columnNumberString.substring(0,columnNumberString.indexOf(" ")));

            Line l = lc.getLineSet().getOriginal(lineNumber);
            System.out.println("Select "+lineNumberString+"["+l.getLineNumber()+"]/"+columnNumberString+":");
            l.show(Line.SHOW_GOTO, columnNumber);
           
        } catch (DataObjectNotFoundException dataObjectNotFoundException) {
            NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Exception(dataObjectNotFoundException);
            DialogDisplayer.getDefault().notify(notifyDescriptor);
        }
    }

    @Override
    public void outputLineCleared(OutputEvent ev) {
        System.out.println("All Cleared");
    }

    @Override
    public void outputLineSelected(OutputEvent ev) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
