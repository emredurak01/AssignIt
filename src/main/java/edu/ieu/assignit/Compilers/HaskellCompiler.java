package edu.ieu.assignit.Compilers;

import edu.ieu.assignit.Result;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class HaskellCompiler extends Compiler {
    public static final String COMPILER_PATH = "ghc";
    public static final String ARGS = "main.hs";
    public static final String RUN_COMMAND = "./main";

    public HaskellCompiler(File workingDirectory) {
        super(workingDirectory);
    }

    @Override
    public Result compile(String path, String args) throws Exception {
        return super.compile(path, args);
    }
}
