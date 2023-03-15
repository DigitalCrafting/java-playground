package org.digitalcrafting.javaPlayground.disassembly;

import org.digitalcrafting.javaPlayground.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Disassembly {
    private final static AssemblyDecompiler DECODER = new AssemblyDecompiler();

    public static void main(String[] args) {
        try {
            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_1");
            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_2");
            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_3");
            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_4_challenge");
            disassemble("org/digitalcrafting/javaPlayground/disassembly/add_sub_cmp_jnz");
//            disassemble("org/digitalcrafting/javaPlayground/disassembly/completionist");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void disassemble(String path) throws IOException {
        byte[] bytes = FileUtils.readBytes(path);
        String[] pathParts = path.split("/");
        String fileName = pathParts[pathParts.length - 1];
        System.out.println("Disassembling file " + fileName);
        String decoded = DECODER.decode(bytes);
        System.out.println(decoded);

        Path source = Paths.get(Disassembly.class.getResource("/").getPath());
        Path newFile = Paths.get(source.toAbsolutePath() + "/" + path + "_disassembled.asm");
        if (!Files.exists(newFile)) {
            Files.createFile(newFile);
        }
        Files.write(newFile, decoded.getBytes());
    }
}
