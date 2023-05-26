package edu.ieu.assignit.Compilers;

import java.io.File;

public class JavaCompiler extends Compiler {
    public static final String COMPILER_PATH = "javac";
    public static final String ARGS = "-sourcepath . Main.java";
    public static final String RUN_COMMAND = "java Main";

    public JavaCompiler(File workingDirectory) {
        super(workingDirectory);
    }
}
