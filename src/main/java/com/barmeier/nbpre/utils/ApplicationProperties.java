/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.barmeier.nbpre.utils;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author barmeier
 */
public class ApplicationProperties {
    private String title;
    private String type;
    private String main;
    private String icon;
    private String id;
    private String version;
    private String vendor;
    private String removable;

    private String getKey (String line) {
        if (line.indexOf(":")!=-1) {
            return line.substring(0,line.indexOf(":")).trim().replace("\"","");
        }
        else {
            return "";
        }
    }

    private String getValue (String line) {
        if (line.indexOf(":")!=-1) {
            return line.substring(line.indexOf(":")+1, line.length()-1).trim().replace("\"","");
        }
        else {
            return "";
        }
    }



    public ApplicationProperties(String filename) {
        String line, key,value;
        try{
            BufferedReader appInfoFile = new BufferedReader(new FileReader(filename));
            while (appInfoFile.ready()) {
                line = appInfoFile.readLine();
                key=getKey(line);
                if (key.equals("id")) {
                    setId(getValue(line));
                }
                else if(key.equals("type")) {
                    setType(getValue(line));
                }
                else if(key.equals("title")) {
                    setTitle(getValue(line));
                }
                else if(key.equals("main")) {
                    setMain(getValue(line));
                }
                else if(key.equals("icon")) {
                    setIcon(getValue(line));
                }
                else if(key.equals("version")) {
                    setVersion(getValue(line));
                }
                else if(key.equals("vendor")) {
                    setVendor(getValue(line));
                }
                else if(key.equals("removable")) {
                    setRemovable(getValue(line));
                }
            }
            appInfoFile.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the main
     */
    public String getMain() {
        return main;
    }

    /**
     * @param main the main to set
     */
    public void setMain(String main) {
        this.main = main;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the vendor
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @param vendor the vendor to set
     */
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    /**
     * @return the removable
     */
    public String getRemovable() {
        return removable;
    }

    /**
     * @param removable the removable to set
     */
    public void setRemovable(String removable) {
        this.removable = removable;
    }
}
