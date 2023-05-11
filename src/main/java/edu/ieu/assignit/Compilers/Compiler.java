package edu.ieu.assignit.Compilers;

import edu.ieu.assignit.Result;

import java.io.File;

public abstract class Compiler {
    protected final File workingDirectory;

    public Compiler(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public abstract Result compile(String path, String args) throws Exception;

    public File getWorkingDirectory() {
        return workingDirectory;
    }
}