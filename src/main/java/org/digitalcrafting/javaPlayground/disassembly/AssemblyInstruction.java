package org.digitalcrafting.javaPlayground.disassembly;

public class AssemblyInstruction {
    public String operation;
    public String destination;
    public String source;

    @Override
    public String toString() {
        return operation + " " + destination + "," + source;
    }
}
