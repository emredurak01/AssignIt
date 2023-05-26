package edu.ieu.assignit.Compilers;

import edu.ieu.assignit.Result;

import java.io.File;

// It is called compiler for convenience
// and uses GNU Guile for interpretation.
public class SchemeCompiler extends Compiler {
    public static final String COMPILER_PATH = "guile";
    public static final String ARGS = "main.scm";

    public SchemeCompiler(File workingDirectory) {
        super(workingDirectory);
    }

    @Override
    public Result compile(String path, String args) throws Exception {
        return super.compile(path, args);
    }
}
