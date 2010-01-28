/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.barmeier.nbpre.utils;

import java.awt.Dimension;
import javax.swing.JSeparator;

/**
 *
 * @author barmeier
 */
public class Separator  extends JSeparator {

    public Separator() {
        super(JSeparator.VERTICAL);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(getPreferredSize().width, super.getMaximumSize().height);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(getPreferredSize().width, super.getSize().height);
    }
}

