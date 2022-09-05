package utils;

import models.DataResult;

public class Helper {
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
}