package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.javaparser.utils.Pair;

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

        if (totalNumOfChildren >= 1) {
            Node rootNode = nodeList.item(0);
            Map<String, String> mapAttr = this.getAttrFromNode(rootNode);
            xmlItem.setMapAttr(mapAttr);
        }

        for (int i = 0; i < totalNumOfChildren; ++i) {
            Node child = nodeList.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                xmlItem.addChildTag(child.getNodeName());
                String nodeTag = child.getNodeName().toLowerCase();
                if (nodeTag.equals(Constants.NODE_TYPE_BEAN)) {
                    xmlItem.addChildNode(nodeTag, this.traverse(child, xmlItem, nodeTag));
                }
            }
        }

        return xmlItem;
    }

    private Map<String, String> getAttrFromNode(Node rootNode) {
        Map<String, String> mapAttrs = new HashMap<String, String>();
        NamedNodeMap attMap = rootNode.getAttributes();
        int totalAttrs = attMap.getLength();
        for (int i = 0; i < totalAttrs; ++i) {
            Attr attr = (Attr) attMap.item(i);
            mapAttrs.put(attr.getName(), attr.getValue());
        }
        return mapAttrs;
    }

    private XMLItem traverse(Node node, XMLItem parentNode, String tagName) {
        XMLItem xmlItem = new XMLItem(parentNode.getId() + "-" + tagName);
        xmlItem.setItemType(tagName);
        xmlItem.setMapAttr(this.getAttrFromNode(node));

        String idSuffix = xmlItem.getAttr(Constants.ATTR_ID);
        if (idSuffix.equals(""))
            idSuffix = xmlItem.getAttr(Constants.ATTR_NAME);
        xmlItem.setId(xmlItem.getId() + "-" + idSuffix);

        xmlItem.setDomNode(node);

        NodeList nodeList = node.getChildNodes();
        int totalNumOfChildren = nodeList.getLength();
        for (int i = 0; i < totalNumOfChildren; ++i) {
            Node child = nodeList.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                xmlItem.addChildTag(child.getNodeName());
                String nodeTag = child.getNodeName().toLowerCase();
                xmlItem.addChildNode(nodeTag, this.traverse(child, xmlItem, nodeTag));
            }
        }

        return xmlItem;
    }
}
