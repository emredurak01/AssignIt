package edu.ieu.assignit;

import java.io.IOException;

public class CCompiler implements Compiler {
    @Override
    public int compile(String path, String args) throws IOException, InterruptedException {

        Process process = Runtime.getRuntime().exec(path + " " + args);
        process.waitFor();
        return process.exitValue();
    }    
}
