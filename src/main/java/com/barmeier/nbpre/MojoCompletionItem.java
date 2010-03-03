/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barmeier.nbpre;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JToolTip;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author barmeier
 */
public class MojoCompletionItem implements CompletionItem {

    private String text;
    private String docString;
    private static ImageIcon fieldIcon = new ImageIcon(Utilities.loadImage("com/barmeier/nbpre/images/compl.png"));
    private static Color fieldColor = Color.decode("0x0000B2");
    private int caretOffset;
    private int dotOffset;

    public MojoCompletionItem(String text, String docString, int dotOffset, int caretOffset) {
        this.text = text;
        this.dotOffset = dotOffset;
        this.docString = docString;
        System.out.println("************ "+docString);
        this.caretOffset = caretOffset;
    }

    @Override
    public int getPreferredWidth(Graphics graphics, Font font) {
        return CompletionUtilities.getPreferredWidth(text, null, graphics, font);
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor,
            Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(fieldIcon, text, null, g, defaultFont,
                (selected ? Color.white : fieldColor), width, height, selected);
    }

    @Override
    public CharSequence getSortText() {
        return text;
    }

    public CharSequence getText() {
        return text;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return text;
    }

    @Override
    public void defaultAction(JTextComponent jTextComponent) {
        try {
            StyledDocument doc = (StyledDocument) jTextComponent.getDocument();
            //Here we remove the characters starting at the start offset
            //and ending at the point where the caret is currently found:
            doc.remove(dotOffset, caretOffset - dotOffset);
            doc.insertString(dotOffset, text, null);
            Completion.get().hideAll();
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    @Override
    public void processKeyEvent(KeyEvent arg0) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {

            @Override
            protected void query(CompletionResultSet completionResultSet, Document document, int i) {
                completionResultSet.setDocumentation(new MojoCompletionDocument(MojoCompletionItem.this));
                completionResultSet.finish();
            }
        });
    }

    @Override
    public CompletionTask createToolTipTask() {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {

            @Override
            protected void query(CompletionResultSet completionResultSet, Document document, int i) {
                JToolTip toolTip = new JToolTip();
                toolTip.setTipText("Press Enter to insert \"" + text + "\"");
                completionResultSet.setToolTip(toolTip);
                completionResultSet.finish();
            }
        });
    }

    @Override
    public boolean instantSubstitution(JTextComponent arg0) {
        return false;
    }

    @Override
    public int getSortPriority() {
        return 0;
    }

    /**
     * @return the docString
     */
    public String getDocString() {
        return docString;
    }
}
