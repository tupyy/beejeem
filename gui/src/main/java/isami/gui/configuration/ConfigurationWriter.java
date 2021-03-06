package stes.isami.bjm.configuration;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;

/**
 * Created by cosmin on 02/05/2017.
 */
public interface ConfigurationWriter {

    public void saveToFile(File file, Preferences preferences) throws ParserConfigurationException, TransformerException;
}
