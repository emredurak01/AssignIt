package edu.ieu.assignit.Compilers;

import edu.ieu.assignit.Result;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

// It is called compiler for convenience
// and uses GNU Guile for interpretation.
public class SchemeCompiler extends Compiler {
    public static final String COMPILER_PATH = "guile";
    public static final String ARGS = "main.scm";

    public SchemeCompiler(File workingDirectory) {
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
