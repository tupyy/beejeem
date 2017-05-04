package configuration;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by cosmin on 02/05/2017.
 */
public interface ConfigurationReader {

    /**
     * Read the user configuration and return a list of {@link javafx.beans.property.SimpleStringProperty}
     * @param file
     * @return the list of user preferences
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public List getUserConfiguration(File file) throws ParserConfigurationException, SAXException, IOException;

    public List getJobDefintions(File file) throws ParserConfigurationException, SAXException, IOException;
}
