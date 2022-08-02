package utils.caching;

import java.util.List;

import javax.swing.plaf.metal.MetalComboBoxUI.MetalPropertyChangeListener;

import models.BooleanItem;
import models.ClassItem;
import models.DataResult;
import models.FileItem;
import models.StringItem;
import models.XMLItem;

public class Cache implements ICache {

    private DataResult<List<ClassItem>> mapGetClasses;

    @Override
    public void addGetClasses(DataResult<List<ClassItem>> classes) {
        this.mapGetClasses = classes;
    }

    @Override
    public DataResult<List<ClassItem>> fetchGetClasses() {
        return this.mapGetClasses;
    }

    @Override
    public void addGetXMLs(DataResult<List<FileItem>> xmls) {
        // TODO Auto-generated method stub

    }

    @Override
    public DataResult<List<FileItem>> fetchGetXMLs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addGetArg(String classFQN, String methodName, int argIdx, DataResult<StringItem> result) {
        // TODO Auto-generated method stub

    }

    @Override
    public DataResult<StringItem> fetchGetArg(String classFQN, String methodName, int argIdx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addCallExists(String classFQN, String methodName, DataResult<BooleanItem> result) {
        // TODO Auto-generated method stub

    }

    @Override
    public DataResult<BooleanItem> fetchCallExists(String classFQN, String methodName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addGetAnnoAttr(String classFQN, String annotation, String attr, DataResult<List<String>> result) {
        // TODO Auto-generated method stub

    }

    @Override
    public DataResult<List<StringItem>> fetchGetArg(String classFQN, String annotation, String attr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addGetElms(String xmlContent, String selector, DataResult<XMLItem> result) {
        // TODO Auto-generated method stub

    }

    @Override
    public DataResult<XMLItem> fetchGetElms(String xmlContent, String selector) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addGetAttr(String xmlContent, String attrName, DataResult<StringItem> result) {
        // TODO Auto-generated method stub

    }

    @Override
    public DataResult<String> fetchGetAttr(String xmlContent, String attrName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addElementExists(String xmlContent, String attrName, DataResult<BooleanItem> result) {
        // TODO Auto-generated method stub

    }

    @Override
    public DataResult<BooleanItem> fetchElementExists(String xmlContent, String attrName) {
        // TODO Auto-generated method stub
        return null;
    }

}
