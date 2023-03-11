package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import utils.Constants;

public class XMLItem {
    private Node domNode;
    private Map<String, String> mapAttr;
    private Map<String, Boolean> mapChildTags; // Has all tags
    private Map<String, List<XMLItem>> mapChildNodes; // Has only intended nodes

    private String id;
    private String itemType;

    public XMLItem(String id) {
        super();
        this.id = id;
        this.mapAttr = new HashMap<>();
        this.mapChildTags = new HashMap<>();
        this.mapChildNodes = new HashMap<>();
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Node getDomNode() {
        return this.domNode;
    }

    public void setDomNode(Node domNode) {
        this.domNode = domNode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getMapAttr() {
        return mapAttr;
    }

    public void setMapAttr(Map<String, String> mapAttr) {
        this.mapAttr = mapAttr;
    }

    public void addAttr(String attr, String value) {
        this.mapAttr.put(attr, attr);
    }

    public String getAttr(String attr) {
        if (this.mapAttr.containsKey(attr))
            return this.mapAttr.get(attr);
        return Constants.EMPTY_STRING;
    }

    public boolean hasAttr(String attr) {
        return this.mapAttr.containsKey(attr);
    }

    public Map<String, String> getAttrMap() {
        return this.mapAttr;
    }

    public Map<String, Boolean> getMapChildTags() {
        return this.mapChildTags;
    }

    public void setMapChildTags(Map<String, Boolean> mapChildTags) {
        this.mapChildTags = mapChildTags;
    }

    public void addChildTag(String tag) {
        this.mapChildTags.put(tag, true);
    }

    public Map<String, List<XMLItem>> getMapChildNodes() {
        return mapChildNodes;
    }

    public void setMapChildNodes(Map<String, List<XMLItem>> mapChildNodes) {
        this.mapChildNodes = mapChildNodes;
    }

    public void addChildNode(String tag, XMLItem node) {
        if (!this.mapChildNodes.containsKey(tag))
            this.mapChildNodes.put(tag, new ArrayList<XMLItem>());
        this.mapChildNodes.get(tag).add(node);
    }

    public List<XMLItem> getChildNodes(String tag) {
        tag = tag.replace("<", "").replace(">", "");
        if (!this.mapChildNodes.containsKey(tag))
            return new ArrayList<XMLItem>();
        return this.mapChildNodes.get(tag);
    }
}
