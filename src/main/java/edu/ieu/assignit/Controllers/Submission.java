package edu.ieu.assignit.Controllers;

// Dummy class for table
public class Submission {
    private final String id;
    private final String output;
    private final String result;
    private final int status;
    private final String error;
    private final String expectedValue;

    public Submission(String id, String output, String result, int status, String error, String expectedValue) {
        this.id = id;
        this.output = output;
        this.result = result;
        this.status = status;
        this.error = error;
        this.expectedValue = expectedValue;
    }

    public String getId() {
        return id;
    }

    public String getOutput() {
        return output;
    }

    public String getResult() {
        return result;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getExpectedValue() {
        return expectedValue;
    }
}
