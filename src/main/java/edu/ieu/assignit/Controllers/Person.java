package edu.ieu.assignit.Controllers;

// Dummy class for table
public class Person {
    private final String id;
    private final String output;
    private final String result;

    public Person(String id, String output, String result) {
        this.id = id;
        this.output = output;
        this.result = result;
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
}
