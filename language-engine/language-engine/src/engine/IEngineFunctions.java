package engine;

import java.util.List;

import models.BooleanItem;
import models.ClassItem;
import models.DataResult;
import models.FieldItem;
import models.InvocationItem;
import models.MethodItem;
import models.StringItem;
import models.XMLItem;

public interface IEngineFunctions {
    /**
     * Return all class items for the project
     * @return
     */
    DataResult<List<ClassItem>> getClasses();

    /**
     * Return all XMLs for the project
     * @return
     */
    DataResult<List<XMLItem>> getXMLs();

    /**
     * Get the argIdx no. argument of methodName invocation in class c
     * @param c
     * @param methodName
     * @param argIdx
     * @return
     */
    DataResult<StringItem> getArg(ClassItem c, String methodName, int argIdx);

    /**
     * Check if methodName is invoked in class c
     * @param c
     * @param methodName
     * @return
     */
    DataResult<BooleanItem> callExists(ClassItem c, String methodName);

    /**
     * Get the fully qualified name of class c
     * @param c
     * @return
     */
    DataResult<String> getFQN(ClassItem c);

    /**
     * Get the attr attribute value for annotation in class c
     * @param c
     * @param annotation
     * @param attr
     * @return
     */
    DataResult<List<String>> getAnnoAttr(ClassItem c, String annotation, String attr);

    /**
     * Select Nodes from xml using selector
     * @param xml
     * @param selector
     * @return
     */
    DataResult<List<XMLItem>> getElms(XMLItem xml, String selector);

    /**
     * Get attrName attribute value from xml node
     * @param node
     * @param attrName
     * @return
     */
    DataResult<StringItem> getAttr(XMLItem node, String attrName);

    /**
     * Check if selector can fetch a node in xml
     * @param xml
     * @param selector
     * @return
     */
    DataResult<BooleanItem> elementExists(XMLItem xml, String selector);

    /**
     * Get name of the method
     * @param m
     * @return
     */
    DataResult<StringItem> getName(MethodItem m);

    /**
     * Check if path exists
     * @param path
     * @return
     */
    DataResult<BooleanItem> pathExists(String path);

    /**
     * Get substring starting from index - st
     * @param str
     * @param st
     * @return
     */
    DataResult<StringItem> subString(String str, int st);

    /**
     * Get substring starting from index - st, & ending at index - en
     * @param str
     * @param st
     * @param en
     * @return
     */
    DataResult<StringItem> subString(String str, int st, int en);

    /**
     * Convert str to uppercase
     * @param str
     * @return
     */
    DataResult<StringItem> upperCase(String str);

    /**
     * Get list of entities of type entity having annotation
     * @param annotation
     * @param entityType
     * @return
     */
    DataResult<List<String>> getAnnotated(String annotation, String entityType);

    /**
     * Join lists
     * @param lists
     * @return
     */
    DataResult<List<String>> join(List<String> lists);

    /**
     * Check if list is empty
     * @param list
     * @return
     */
    DataResult<BooleanItem> isEmpty(List<String> list);

    /**
     * Check if string ends with suffix
     * @param str
     * @param suffix
     * @return
     */
    DataResult<BooleanItem> endsWith(String str, String suffix);

    /**
     * Get short name for class - className
     * @param className
     * @return
     */
    DataResult<StringItem> getSN(String className);

    /**
     * Get all fields of class c
     * @param c
     * @return
     */
    DataResult<List<FieldItem>> getFields(ClassItem c);

    /**
     * Get all methods of class c
     * @param c
     * @return
     */
    DataResult<List<MethodItem>> getMethods(ClassItem c);

    /**
     * Get all invocations of any methods inside class c
     * @param c
     * @return
     */
    DataResult<List<InvocationItem>> getInvocations(ClassItem c);
}
