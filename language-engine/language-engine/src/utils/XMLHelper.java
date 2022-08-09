package utils;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLHelper {
    private String xmlFilePath;

    public XMLHelper(String xmlFilePath) {
        super();
        this.xmlFilePath = xmlFilePath;
    }

    public void readXMLs() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(this.xmlFilePath);

        NodeList nodeList = document.getElementsByTagName("*");
        int totalNumOfChildren = nodeList.getLength();
        for (int i = 0; i < totalNumOfChildren; ++i) {
            Node child = nodeList.item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("=>" + child.getNodeName());

                NodeList grandchildren = child.getChildNodes();
                for (int j = 0; j < grandchildren.getLength(); ++j) {
                    Node grandchild = grandchildren.item(j);
                    if (grandchild.getNodeType() == Node.ELEMENT_NODE) {
                        System.out.println("\t==>" +
                                " " + grandchild.getNodeType() +
                                " " + grandchild.getNodeName());
                    }
                }
            }
        }
    }
}
