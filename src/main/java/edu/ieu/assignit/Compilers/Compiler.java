package edu.ieu.assignit.Compilers;

import edu.ieu.assignit.Result;

import java.io.*;

public abstract class Compiler {
    protected final File workingDirectory;

    public Compiler(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public Result compile(String path, String args) throws Exception {
        Process process = Runtime.getRuntime().exec(path + " " + args, null, workingDirectory);
        process.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder outputBuilder = new StringBuilder();
        StringBuilder errorBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            outputBuilder.append(line).append("\n");
        }
        while ((line = errorReader.readLine()) != null) {
            errorBuilder.append(line).append("\n");
        }
        Result result = new Result(outputBuilder.toString(), process.exitValue(), errorBuilder.toString());
        return result;
    }


    public File getWorkingDirectory() {
        return workingDirectory;
    }
}
