package utils.caching;

import java.util.List;

import models.BooleanItem;
import models.ClassItem;
import models.DataResult;
import models.FileItem;
import models.StringItem;
import models.XMLItem;

public interface ICache {
    void addGetClasses(DataResult<List<ClassItem>> classes);

    DataResult<List<ClassItem>> fetchGetClasses();

    void addGetXMLs(DataResult<List<FileItem>> xmls);

    DataResult<List<FileItem>> fetchGetXMLs();

    void addGetArg(String classFQN, String methodName, int argIdx, DataResult<StringItem> result);

    DataResult<StringItem> fetchGetArg(String classFQN, String methodName, int argIdx);

    void addCallExists(String classFQN, String methodName, DataResult<BooleanItem> result);

    DataResult<BooleanItem> fetchCallExists(String classFQN, String methodName);

    void addGetAnnoAttr(String classFQN, String annotation, String attr, DataResult<List<String>> result);

    DataResult<List<StringItem>> fetchGetArg(String classFQN, String annotation, String attr);

    void addGetElms(String xmlContent, String selector, DataResult<XMLItem> result);

    DataResult<XMLItem> fetchGetElms(String xmlContent, String selector);

    void addGetAttr(String xmlContent, String attrName, DataResult<StringItem> result);

    DataResult<String> fetchGetAttr(String xmlContent, String attrName);

    void addElementExists(String xmlContent, String attrName, DataResult<BooleanItem> result);

    DataResult<BooleanItem> fetchElementExists(String xmlContent, String attrName);
}
