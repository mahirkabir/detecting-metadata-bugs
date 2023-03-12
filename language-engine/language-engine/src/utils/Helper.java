package utils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.utils.Pair;

import engine.EngineFactory;
import engine.IEngineAssert;
import engine.IEngineDecl;
import engine.IEngineEvaluate;
import engine.IEngineFor;
import engine.IEngineFunctions;
import models.BooleanItem;
import models.ClassItem;
import models.DataResult;
import models.FieldItem;
import models.IntegerItem;
import models.MethodItem;
import models.StringItem;
import models.VersionCategory;
import models.XMLItem;
import parser.ASTAssertStmnt;
import parser.ASTDeclStmnt;
import parser.ASTExpression;
import parser.ASTForStmnt;
import parser.ASTFunctionOrId;
import parser.ASTIdentifier;
import parser.ASTIfStmnt;
import parser.ASTLiteral;
import parser.ASTSimExp;
import parser.ASTType;
import parser.Node;

public class Helper {
    /**
     * Check if two objects are equal
     * 
     * @param firstResult
     * @param secondResult
     * @return
     */
    public static boolean isEqual(DataResult firstResult, DataResult secondResult) {
        boolean isEqual = false;
        if (firstResult.getType().equals(Constants.TYPE_STRING)) {
            isEqual = ((StringItem) firstResult.getResult())
                    .equals((StringItem) secondResult.getResult());
        } else if (firstResult.getType().equals(Constants.TYPE_BOOLEAN)) {
            isEqual = ((BooleanItem) firstResult.getResult())
                    .equals((BooleanItem) secondResult.getResult());
        } else if (firstResult.getType().equals(Constants.TYPE_INTEGER)) {
            isEqual = ((IntegerItem) firstResult.getResult())
                    .equals((IntegerItem) secondResult.getResult());
        }
        // Note: Currently, we do not have equality comparisons for class, method,
        // field, & file type
        return isEqual;
    }

    /**
     * Type cast Object to a datatype and return result object
     * 
     * @param iteratorType
     * @param element
     * @return
     */
    public static DataResult typeCastValue(String iteratorType, Object element) {
        DataResult result = null;

        switch (iteratorType) {
            case Constants.TYPE_CLASS:
                ClassItem convertedClassItem = (ClassItem) element;
                result = new DataResult<ClassItem>(iteratorType, convertedClassItem);
                break;
            case Constants.TYPE_FIELD:
                FieldItem convertedFieldItem = (FieldItem) element;
                result = new DataResult<FieldItem>(Constants.TYPE_FIELD, convertedFieldItem);
                break;

            case Constants.TYPE_METHOD:
                MethodItem convertedMethodItem = (MethodItem) element;
                result = new DataResult<MethodItem>(Constants.TYPE_METHOD, convertedMethodItem);
                break;

            case Constants.TYPE_FILE:
                XMLItem convertedXmlItem = (XMLItem) element;
                result = new DataResult<XMLItem>(Constants.TYPE_XML, convertedXmlItem);
                break;

            case Constants.TYPE_STRING:
                StringItem convertedStringItem = (StringItem) element;
                result = new DataResult<StringItem>(Constants.TYPE_STRING, convertedStringItem);
                break;

            default:
                XMLItem convertedXMLItem = (XMLItem) element;
                result = new DataResult<XMLItem>(iteratorType, convertedXMLItem);
                break;
        }

        return result;
    }

    /**
     * Process a node
     * 
     * @param node
     */
    public static void process(Node node) {
        switch (node.toString()) {
            case Constants.IF_STMNT:
                ASTIfStmnt ifStmnt = (ASTIfStmnt) node;
                ASTExpression ifExpr = (ASTExpression) ifStmnt.jjtGetChild(0);
                IEngineEvaluate evaluator = EngineFactory.getEvaluator();
                boolean result = evaluator.evalBooleanExpr(ifExpr);
                if (result) {
                    System.out.println("Start: " + ifStmnt);
                    int totalChildren = ifStmnt.jjtGetNumChildren();
                    for (int i = 1; i < totalChildren; ++i)
                        Helper.process(ifStmnt.jjtGetChild(i));
                    System.out.println("End: " + ifStmnt);
                }
                break;

            case Constants.DECL_STMNT:
                ASTDeclStmnt declStmnt = (ASTDeclStmnt) node;
                IEngineDecl engineDecl = EngineFactory.getEngineDecl();
                engineDecl.declareVariable(declStmnt);
                break;

            case Constants.FOR_STMNT:
                ASTForStmnt forStmnt = (ASTForStmnt) node;
                System.out.println("Start: " + forStmnt);
                IEngineFor engineFor = EngineFactory.getEngineFor();
                engineFor.process(forStmnt);
                System.out.println("End: " + forStmnt);
                break;

            case Constants.ASSERT_STMNT:
                ASTAssertStmnt assertStmnt = (ASTAssertStmnt) node;
                System.out.println("Start: " + assertStmnt);
                IEngineAssert engineAssert = EngineFactory.getEngineAssert();
                engineAssert.process(assertStmnt);
                System.out.println("End: " + assertStmnt);
                break;

        }
    }

    /**
     * Get loop container
     * 
     * @param containerExp
     * @return
     */
    public static DataResult getContainer(ASTExpression containerExp) {
        IEngineDecl engineDecl = EngineFactory.getEngineDecl();
        IEngineFunctions engineFunctions = EngineFactory.getEngineFunctions();
        DataResult containerValue = null;

        ASTSimExp containerSimExp = (ASTSimExp) containerExp.jjtGetChild(0);
        ASTFunctionOrId containerFunctionOrId = (ASTFunctionOrId) containerSimExp.jjtGetChild(0);
        int totalChildren = containerFunctionOrId.jjtGetNumChildren();
        if (totalChildren == 1) {
            // Container is a variable
            ASTIdentifier containerId = (ASTIdentifier) containerFunctionOrId.jjtGetChild(0);
            String containerVarName = containerId.getIdentifier().toString();
            containerValue = engineDecl.extractVariable(containerVarName);
        } else if (totalChildren == 2) {
            // Container is a function call
            containerValue = engineFunctions.callFunction(containerFunctionOrId);
        }

        return containerValue;
    }

    /**
     * Get loop iterator
     * 
     * @param iteratorExp
     * @return
     */
    public static Pair<ASTType, ASTIdentifier> getIterator(ASTExpression iteratorExp) {
        ASTSimExp iteratorSimExp = (ASTSimExp) iteratorExp.jjtGetChild(0);
        ASTType iteratorType = (ASTType) iteratorSimExp.jjtGetChild(0);
        ASTIdentifier iteratorId = (ASTIdentifier) iteratorSimExp.jjtGetChild(1);
        return new Pair<ASTType, ASTIdentifier>(iteratorType, iteratorId);
    }

    /**
     * Evaluate and return function or variable value
     * 
     * @param node
     * @return
     */
    public static DataResult getFunctionOrIdValue(ASTFunctionOrId node) {
        DataResult result = null;
        IEngineEvaluate evaluator = EngineFactory.getEvaluator();
        if (node.jjtGetNumChildren() == 1)
            // FunctionTail is not there
            result = evaluator.evalId(node);
        else
            // Second child is FunctionTail
            result = evaluator.evalFunction(node);
        return result;
    }

    /**
     * Return literal value
     * 
     * @param node
     * @return
     */
    public static DataResult getLiteralValue(ASTLiteral node) {
        DataResult result = null;
        String value = node.getLitValue();
        if (value.startsWith("\"") && value.endsWith("\""))
            value = value.substring(1, value.length() - 1);
        node.setLitValue(value);

        switch (node.getLitType()) {
            case Constants.TYPE_STRING:
                result = new DataResult<StringItem>(Constants.TYPE_STRING,
                        new StringItem(node.getLitValue()));
                break;
            case Constants.TYPE_INTEGER:
                result = new DataResult<IntegerItem>(Constants.TYPE_INTEGER,
                        new IntegerItem(Integer.parseInt(node.getLitValue())));
                break;
            case Constants.TYPE_BOOLEAN:
                result = new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN,
                        new BooleanItem(node.getLitType().equals(Constants.BOOLEAN_TRUE)));
                break;
        }
        return result;
    }

    /**
     * Format string and replace %s with format values
     * 
     * @param message
     * @param formatValues
     * @return
     */
    public static String formatStr(String message, List<String> formatValues) {
        if (formatValues.size() == 0)
            return message;

        StringBuilder sBuilder = new StringBuilder();
        int n = message.length(), fit = 0;
        for (int i = 0; i < n; ++i) {
            if (message.charAt(i) == '%' && i + 1 < n && message.charAt(i + 1) == 's') {
                sBuilder.append(formatValues.get(fit++));
                ++i;
            } else
                sBuilder.append(message.charAt(i));
        }

        return sBuilder.toString();
    }

    /**
     * 
     * @param dataType
     * @return
     */
    public static boolean isIterable(String dataType) {
        boolean isIterable = false;
        switch (dataType) {
            case Constants.ITERABLE_TYPE_ARRAY_LIST:
            case Constants.ITERABLE_TYPE_COLLECTION:
            case Constants.ITERABLE_TYPE_DEQUE:
            case Constants.ITERABLE_TYPE_HASH_SET:
            case Constants.ITERABLE_TYPE_LINKED_HASH_SET:
            case Constants.ITERABLE_TYPE_LINKED_LIST:
            case Constants.ITERABLE_TYPE_LIST:
            case Constants.ITERABLE_TYPE_PRIORITY_QUEUE:
            case Constants.ITERABLE_TYPE_QUEUE:
            case Constants.ITERABLE_TYPE_SET:
            case Constants.ITERABLE_TYPE_TREE_SET:
            case Constants.ITERABLE_TYPE_VECTOR:
                isIterable = true;
                break;
        }
        return isIterable;
    }

    /**
     * Get assert exists pass fail value
     * 
     * @param simExp
     * @return
     */
    public static DataResult getExistsValue(ASTSimExp simExp) {
        IEngineAssert engineAssert = EngineFactory.getEngineAssert();
        return engineAssert.getExistsValue(simExp);
    }

    /**
     * Check if file path exists
     * 
     * @param path
     * @return
     */
    public static boolean pathExists(String path) {
        Map<String, Boolean> mapLoadedFilenames = EngineFactory.getEngineCache().getLoadedFilenames();
        if (mapLoadedFilenames.containsKey(path))
            return true;

        if (path.startsWith(Constants.CONTEXT_PATH_CLASSPATH))
            path = path.replace(Constants.CONTEXT_PATH_CLASSPATH, "");
        else if (path.startsWith(Constants.CONTEXT_PATH_FILE))
            path = path.replace(Constants.CONTEXT_PATH_FILE, "");
        else if (path.startsWith("./"))
            path = path.substring(2);

        for (Map.Entry<String, Boolean> entry : mapLoadedFilenames.entrySet()) {
            String filename = entry.getKey();
            if (filename.endsWith(path))
                return true;
            try {
                String safeFilename = filename.replace("\\", "\\\\");
                Pattern pattern = Pattern.compile(regexFriendly(path));
                Matcher matcher = pattern.matcher(safeFilename);
                boolean matchFound = matcher.find();
                if (matchFound)
                    return true;
            } catch (Exception ex) {
                utils.Logger.log("Error matching pattern: " + path + " for: " + path);
            }
        }

        return false;
    }

    /**
     * Checkout commitId version for project located in projectPath
     * 
     * @param projectPath
     * @param commitId
     */
    public static void gitCheckout(Path projectPath, String commitId) {
    }

    /***
     * Make the filepath regex friendly
     * 
     * @param filepath
     * @return
     */
    private static String regexFriendly(String filepath) {
        String regexFriendlyVersion = filepath
                .replace("\\", "\\\\")
                .replace(".", "\\.")
                .replace("*", ".*");
        return regexFriendlyVersion;
    }
}