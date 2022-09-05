package utils;

import models.ClassItem;
import models.DataResult;

public class Helper {
    /**
     * Check if two objects are equal
     * 
     * @param firstResult
     * @param secondResult
     * @return
     */
    public static boolean isEqual(DataResult firstResult, DataResult secondResult) {
        if (firstResult.getType().equals("String")) {
            return firstResult.getResult().equals(secondResult.getResult());
        } else if (firstResult.getType().equals("bool")) {
            return firstResult.getResult() == secondResult.getResult();
        }
        // Note: Currently, we do not have equality comparisons for class, method,
        // field, & file type
        return false;
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
                ClassItem converted = (ClassItem) element;
                result = new DataResult<ClassItem>(iteratorType, converted);
                break;
        }

        return result;
    }
}