package mepcotterell.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Loads a properties file that's the same name as a Class
 * @author mepcotterell
 */
public class ClassPropertiesFile extends Properties {
    
    /**
     * The constructor
     * @author mepcotterell
     * @param c 
     */
    public ClassPropertiesFile(Class c) {
        
        // call the parent constructor
        super();
        
        // get the file separator
        String sep = System.getProperty("file.separator");
        
        // derive the location of the properties file
        String path = sep + c.getCanonicalName().toString().replace(".", sep) + ".properties";
       
        // try to load the properties file
        try {
            URL propFile = this.getClass().getResource(path);
            this.load(new FileInputStream(propFile.getFile()));
        } catch (Exception e) {
            System.err.println("There was a problem loading the class properties file.");
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
        
    }
    
}
