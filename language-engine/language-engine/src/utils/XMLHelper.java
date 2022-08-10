package utils;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import models.XMLItem;

public class XMLHelper {
    private String xmlFilePath;
    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;

    public XMLHelper(String xmlFilePath) {
        super();
        this.xmlFilePath = xmlFilePath;
    }

    public void readXML1() throws ParserConfigurationException, SAXException, IOException {
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
                                " " + grandchild.getNodeName());
                    }
                }
            }
        }
    }

    public XMLItem readXML() throws SAXException, IOException, ParserConfigurationException {
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
        this.document = this.documentBuilder.parse(this.xmlFilePath);

        NodeList nodeList = document.getElementsByTagName("*");

        XMLItem xmlItem = new XMLItem(this.xmlFilePath); // Root xml item's id is the file path
        xmlItem.setItemType(Constants.NODE_TYPE_ROOT);
        int totalNumOfChildren = nodeList.getLength();
        for (int i = 0; i < totalNumOfChildren; ++i) {
            // Tracking all the tags used in the xml
            Node child = nodeList.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE)
                xmlItem.addChildTag(child.getNodeName());
        }

        Node rootNode = nodeList.item(0);
        NamedNodeMap attMap = rootNode.getAttributes();
        int totalAttrs = attMap.getLength();
        for (int i = 0; i < totalAttrs; ++i) {
            Attr attr = (Attr) attMap.item(i);
            xmlItem.addAttr(attr.getName(), attr.getValue());
        }

        return null;
    }
}
