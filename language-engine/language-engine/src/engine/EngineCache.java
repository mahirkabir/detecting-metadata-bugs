package engine;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;

import models.DataResult;

public class EngineCache implements IEngineCache {
    private Map<String, DataResult> mapResultCache;
    private Map<String, Boolean> mapLoadedFilenameCache;
    private Map<String, CompilationUnit> mapLoadedASTCache;

    public EngineCache() {
        super();
        this.mapResultCache = new HashMap<>();
        this.mapLoadedASTCache = new HashMap<String, CompilationUnit>();
    }

    @Override
    public void addFunctionCall(String functionCall, DataResult result) {
        this.mapResultCache.put(functionCall, result);
    }

    @Override
    public DataResult fetchFunctionCall(String functionCall) {
        DataResult result = null;
        if (this.mapResultCache.containsKey(functionCall))
            result = this.mapResultCache.get(functionCall);
        return result;
    }

    @Override
    public void addLoadedFilename(String filename) {
        if (this.mapLoadedFilenameCache == null)
            this.mapLoadedFilenameCache = new HashMap<String, Boolean>();
        this.mapLoadedFilenameCache.put(filename, true);
    }

    @Override
    public Map<String, Boolean> getLoadedFilenames() {
        return this.mapLoadedFilenameCache;
    }

    @Override
    public CompilationUnit getLoadedAST(String filename) {

        if (this.mapLoadedASTCache.containsKey(filename))
            return this.mapLoadedASTCache.get(filename);
        return null;
    }

    @Override
    public void setLoadedAST(String filename, CompilationUnit cu) {
        this.mapLoadedASTCache.put(filename, cu);
    }
}
