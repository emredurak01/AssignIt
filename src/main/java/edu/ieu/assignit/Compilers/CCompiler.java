package edu.ieu.assignit.Compilers;

import edu.ieu.assignit.Result;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CCompiler extends Compiler {
    public static final String COMPILER_PATH = "gcc";
    public static final String ARGS = "main.c";
    public static final String RUN_COMMAND = "./main";

    public CCompiler(File workingDirectory) {
        super(workingDirectory);
    }

    @Override
    public Result compile(String path, String args) throws Exception {
        return super.compile(path, args);
    }
}
