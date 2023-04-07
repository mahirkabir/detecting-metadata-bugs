package engine;

import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;

import models.DataResult;

public interface IEngineCache {
    /**
     * Add result of the function call in cache
     * 
     * @param functionCall
     * @param result
     */
    void addFunctionCall(String functionCall, DataResult result);

    /**
     * Fetch result of the function call from the cache
     * 
     * @param functionCall
     * @return
     */
    DataResult fetchFunctionCall(String functionCall);

    /**
     * Add loaded filename in the cache
     * 
     * @param filename
     */
    void addLoadedFilename(String filename);

    /**
     * Get all the laoded filenames from the cache
     * 
     * @return
     */
    Map<String, Boolean> getLoadedFilenames();

    /**
     * Get loaded AST generated from file - filepath
     * 
     * @param filepath
     * @return
     */
    CompilationUnit getLoadedAST(String filepath);

    /**
     * Set loaded AST generated for the file - filepath
     * 
     * @param filepath
     * @param cu
     */
    void setLoadedAST(String filepath, CompilationUnit cu);
}
