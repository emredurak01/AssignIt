package edu.ieu.assignit;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;

// It is called compiler for convenience
// and uses Python 3 for interpretation.
public class PythonCompiler extends Compiler {
    public static final String COMPILER_PATH = "python3";
    public static final String ARGS = "main.py";
    
    public PythonCompiler(File workingDirectory) {
        super(workingDirectory);
    }
    
    @Override
    public Result compile(String path, String args) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(path + " " + args, null, workingDirectory);
        process.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output = reader.readLine();
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String error = reader2.readLine();
        Result result = new Result(output, process.exitValue(), error);
        return result;
    }
}
