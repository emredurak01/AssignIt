package edu.ieu.assignit.Compilers;

import edu.ieu.assignit.Result;

import java.io.File;

// It is called compiler for convenience
// and uses Python 3 for interpretation.
public class PythonCompiler extends Compiler {
    public static final String COMPILER_PATH = "python3";
    public static final String ARGS = "main.py";

    public PythonCompiler(File workingDirectory) {
        super(workingDirectory);
    }

    @Override
    public Result compile(String path, String args) throws Exception {
        return super.compile(path, args);
    }
}
