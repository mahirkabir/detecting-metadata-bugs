package models;

public class EvalResult {
    private String type;
    private String result;

    public EvalResult() {
        super();
        this.type = "bool";
        this.result = "false";
    }

    public EvalResult(String type, String result) {
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
