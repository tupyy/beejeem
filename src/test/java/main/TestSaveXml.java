package main;

import configuration.JStesConfiguration;
import configuration.XmlConfigurationWriter;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

/**
 * Created by cosmin on 02/05/2017.
 */
public class TestSaveXml {

    @Test
    public void testSaveXML() {
        JStesConfiguration jStesConfiguration = new JStesConfiguration();
        try {
            jStesConfiguration.loadConfiguration(new File("C:\\Users\\cosmin\\configuration.xml"));
            XmlConfigurationWriter xmlConfigurationWriter = new XmlConfigurationWriter();
            try {
                xmlConfigurationWriter.saveToFile(new File("C:\\Users\\cosmin\\test.xml"),JStesConfiguration.getPreferences());
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert(true);
    }
}
