package edu.ieu.assignit.Compilers;

import edu.ieu.assignit.Result;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavaCompiler extends Compiler {
    public static final String COMPILER_PATH = "javac";
    public static final String ARGS = "*.java";
    public static final String RUN_COMMAND = "java Main.java";

    public JavaCompiler(File workingDirectory) {
        super(workingDirectory);
    }

    @Override
    public Result compile(String path, String args) throws Exception {
        super.compile(path, args);
        return super.run(RUN_COMMAND);
    }
}
