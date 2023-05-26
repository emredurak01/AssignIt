package edu.ieu.assignit.Compilers;

import java.io.File;

public class HaskellCompiler extends Compiler {
    public static final String COMPILER_PATH = "ghc";
    public static final String ARGS = "main.hs";
    public static final String RUN_COMMAND = "./main";

    public HaskellCompiler(File workingDirectory) {
        super(workingDirectory);
    }
}
