package utils;

import models.EvalResult;

public class Helper {
    public static boolean isEqual(EvalResult firstResult, EvalResult secondResult) {
        if (firstResult.getType().equals("String")) {
            return firstResult.getResult().equals(secondResult.getResult());
        } else if (firstResult.getType().equals("bool")) {
            return Boolean.parseBoolean(
                    firstResult.getResult()) == Boolean.parseBoolean(secondResult.getResult());
        }
        // Note: Currently, we do not have equality comparisons for class, method,
        // field, & file type
        return false;
    }
}