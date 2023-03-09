package org.digitalcrafting.javaPlayground.disassembly;

import org.digitalcrafting.javaPlayground.utils.FileUtils;

import java.io.IOException;

public class Disassembly {
    private final static AssemblyDecompiler DECODER = new AssemblyDecompiler();

    public static void main(String[] args) {
        try {
//            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_1");
//            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_2");
//            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_3");
            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_4_challenge");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void disassemble(String path) throws IOException {
        byte[] bytes = FileUtils.readBytes(path);
        String[] pathParts = path.split("/");
        String fileName = pathParts[pathParts.length - 1];

        System.out.println("Disassembling file " + fileName);
        System.out.println(DECODER.decode(bytes));
    }
}
