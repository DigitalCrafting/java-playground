package org.digitalcrafting.javaPlayground.disassembly;

import org.apache.commons.lang3.StringUtils;

public class AssemblyInstruction {
    public String operation;
    public String destination;
    public String source;

    @Override
    public String toString() {
        if (StringUtils.isBlank(source)) {
            return operation + " " + destination + "\n";
        } else {
            return operation + " " + destination + ", " + source + "\n";
        }
    }
}
