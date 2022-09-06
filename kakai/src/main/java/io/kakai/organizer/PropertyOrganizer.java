package io.kakai.organizer;

import io.kakai.Kakai;
import io.kakai.resources.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.kakai.resources.Project.PROPERTY_FILE;
import static io.kakai.resources.Project.RESOURCES_DIR;

public class PropertyOrganizer {

    Logger Log = Logger.getLogger(PropertyOrganizer.class.getName());

    Kakai kakai;
    Resources resources;

    public PropertyOrganizer(Kakai kakai){
        this.kakai = kakai;
        this.resources = new Resources();
    }

    protected InputStream getPropertiesFile() throws Exception{
        InputStream is = null;
        String resourceUri = resources.getResourceUri();
        File file = new File(resourceUri + File.separator + PROPERTY_FILE);
        if(file.exists()) {
            is = new FileInputStream(file);
        }else{
            is = this.getClass().getResourceAsStream(RESOURCES_DIR + PROPERTY_FILE);
        }
        return is;
    }

    public void run() throws IOException {
        InputStream is = null;
        Properties prop;
        try {

            is = getPropertiesFile();

            if(is == null)return;

            prop = new Properties();
            prop.load(is);

            Enumeration properties = prop.propertyNames();
            while (properties.hasMoreElements()) {
                String key = (String) properties.nextElement();
                String value = prop.getProperty(key);
                kakai.getPropertyStorage().getProperties().put(key, value);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }

    }
}
