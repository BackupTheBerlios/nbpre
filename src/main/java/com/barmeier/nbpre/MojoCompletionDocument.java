/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.Action;
import java.net.URL;
import org.netbeans.spi.editor.completion.CompletionDocumentation;

/**
 *
 * @author barmeier
 */
public class MojoCompletionDocument implements CompletionDocumentation {

    private MojoCompletionItem item;
    String doc = "";

    public MojoCompletionDocument(MojoCompletionItem item) {
        this.item = item;
        if (item.getDocString() != null) {
            InputStream inputStream = this.getClass().getResourceAsStream(item.getDocString());
            if (inputStream!=null) {
                BufferedReader isr = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    while (isr.ready()) {
                        doc = doc + isr.readLine();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getText() {
        return doc;
    }

    public URL getURL() {
        return null;
    }

    public CompletionDocumentation resolveLink(String string) {
        return null;
    }

    public Action getGotoSourceAction() {
        return null;
    }
}
