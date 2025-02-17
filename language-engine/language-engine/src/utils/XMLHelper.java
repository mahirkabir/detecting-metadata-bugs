package utils;

import java.io.File;
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

import engine.EngineFactory;
import engine.IEngineCache;
import models.XMLItem;

public class XMLHelper {
    private List<String> xmlFiles;
    private String projectPath;

    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;
    private IEngineCache engineCache;

    public XMLHelper(String projectPath) {
        super();
        this.xmlFiles = new ArrayList<String>();
        this.projectPath = projectPath;
        this.engineCache = EngineFactory.getEngineCache();
    }

    /**
     * Get all xml file paths under folder
     * 
     * @folder - Folder location
     * @return
     */
    public void loadXMLFiles(String folder) {
        File[] files = new File(folder).listFiles();
        if (files != null && files.length > 0) {
            for (File currFile : files) {
                String fullPath = folder + "\\" + currFile.getName().toString();
                if (currFile.isDirectory()) {
                    this.loadXMLFiles(fullPath.toString());
                } else {
                    if (currFile.getName().endsWith(Constants.EXTENSION_XML))
                        if (EngineFactory.getEngineVersionControl().isNonIgnoredFile(fullPath))
                            this.xmlFiles.add(fullPath);

                    if (currFile.getName().endsWith(Constants.EXTENSION_JAVA)
                            || currFile.getName().endsWith(Constants.EXTENSION_XML)) {
                        if (EngineFactory.getEngineVersionControl().isNonIgnoredFile(fullPath))
                            this.engineCache.addLoadedFilename(fullPath);
                    }
                }
            }
        }
    }

    /**
     * Get all xml items in the specified project
     * 
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public List<XMLItem> getXMLs() throws SAXException, IOException, ParserConfigurationException {
        List<XMLItem> xmlItems = new ArrayList<XMLItem>();

        this.loadXMLFiles(this.projectPath);

        if (this.xmlFiles != null) {
            for (String xmlFilePath : this.xmlFiles) {
                try {
                    XMLItem xmlItem = this.readXML(xmlFilePath);
                    xmlItems.add(xmlItem);
                } catch (Exception ex) {
                    Logger.log("getXMLs() => Error parsing xml file: " + xmlFilePath);
                }
            }
        }

        Logger.log("Total XMLs: " + xmlItems.size());
        return xmlItems;
    }

    public List<XMLItem> getElms(XMLItem parent, String selector) {
        return parent.getChildNodes(selector);
    }

    /**
     * Read and parse a single XML file
     * 
     * @param xmlFilePath
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private XMLItem readXML(String xmlFilePath) throws SAXException, IOException, ParserConfigurationException {
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
        this.document = this.documentBuilder.parse(xmlFilePath);

        NodeList nodeList = document.getElementsByTagName("*");

        XMLItem xmlItem = new XMLItem(xmlFilePath); // Root xml item's id is the file path
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
                    // TODO: Need to support handling nodes other than <bean>
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
