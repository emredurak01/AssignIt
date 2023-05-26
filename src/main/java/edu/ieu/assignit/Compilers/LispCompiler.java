package edu.ieu.assignit.Compilers;

import edu.ieu.assignit.Result;

import java.io.File;

// It is called compiler for convenience
// and uses Emacs lisp for interpretation.
public class LispCompiler extends Compiler {
    public static final String COMPILER_PATH = "emacs";
    public static final String ARGS = "--script main.el";

    public LispCompiler(File workingDirectory) {
        super(workingDirectory);
    }

    @Override
    public Result compile(String path, String args) throws Exception {
        return super.compile(path, args);
    }
}
