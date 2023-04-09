package edu.ieu.assignit;

import java.io.IOException;

public class CCompiler implements Compiler {
    @Override
    public int compile(String path, String[] args) throws IOException, InterruptedException {
        String[] cmdarray = new String[args.length + 1];
        System.arraycopy(args, 0, cmdarray, 1, args.length);
        cmdarray[0] = path;
        System.out.println(cmdarray[0] + cmdarray[1]);
        Process process = Runtime.getRuntime().exec(cmdarray);
        process.waitFor();
        return process.exitValue();
    }    
}
