package models;

public class DataResult<T> {
    private String type;
    private T result;

    public DataResult(String type, T result) {
        super();
        this.type = type;
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getResult() {
        return result;
    }
    
    public void setResult(T result) {
        this.result = result;
    }
}
