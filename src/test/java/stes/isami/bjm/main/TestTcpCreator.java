package stes.isami.bjm.main;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by tctupangiu on 24/05/2017.
 */
public class TestTcpCreator {

    @Test
    public void testClassReader() {
//        TcpJobCreator tcpJobCreator = new TcpJobCreator();
//        String xml = "<job creator=\"core.creator.Creator\"></job>";
//        try {
//            Document document = convertStringToDocument(xml);
//            System.out.print(tcpJobCreator.getCreator(document.getDocumentElement()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }


    }

    private Document convertStringToDocument(String xmlStr) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
        return doc;
    }
}
