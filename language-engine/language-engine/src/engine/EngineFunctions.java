package engine;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import models.BooleanItem;
import models.ClassItem;
import models.DataResult;
import models.MethodItem;
import models.StringItem;
import models.XMLItem;
import utils.ClassHelper;
import utils.Constants;
import utils.XMLHelper;
import utils.caching.ICache;

public class EngineFunctions implements IEngineFunctions {
    private String projectPath;
    private ClassHelper classHelper;
    private XMLHelper xmlHelper;
    private ICache cache;

    public EngineFunctions(String projectPath, ICache cache) {
        super();
        this.projectPath = projectPath;
        this.classHelper = new ClassHelper(this.projectPath);
        this.xmlHelper = new XMLHelper(this.projectPath);
        this.cache = cache;
    }

    @Override
    public DataResult<List<ClassItem>> getClasses() {
        DataResult<List<ClassItem>> result = cache.fetchGetClasses();

        if (result != null)
            return result;

        this.classHelper.loadJavaFiles(this.projectPath); // Once loaded, it will be ready everywhere in this class
        result = new DataResult<List<ClassItem>>(Constants.TYPE_CLASS_LIST, classHelper.getClasses());

        cache.addGetClasses(result);
        return result;
    }

    @Override
    public DataResult<List<XMLItem>> getXMLs() {
        DataResult<List<XMLItem>> result = null;

        try {
            result = new DataResult<List<XMLItem>>(Constants.TYPE_XML_LIST, this.xmlHelper.getXMLs());
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public DataResult<StringItem> getArg(ClassItem c, String methodName, int argIdx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<BooleanItem> callExists(ClassItem c, String methodName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<String> getFQN(ClassItem c) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<List<String>> getAnnoAttr(ClassItem c, String annotation, String attr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<XMLItem> getElms(XMLItem xml, String selector) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<StringItem> getAttr(XMLItem node, String attrName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<BooleanItem> elementExists(XMLItem xml, String selector) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<StringItem> getName(MethodItem m) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<BooleanItem> pathExists(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<StringItem> subString(String str, int st) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<StringItem> subString(String str, int st, int en) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<StringItem> upperCase(String str) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<List<String>> getAnnotated(String annotation, String entityType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<List<String>> join(List<String> lists) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<BooleanItem> isEmpty(List<String> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<BooleanItem> endsWith(String str, String suffix) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<StringItem> getSN(String className) {
        // TODO Auto-generated method stub
        return null;
    }

}
