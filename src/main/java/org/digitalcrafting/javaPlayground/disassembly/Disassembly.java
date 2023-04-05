package org.digitalcrafting.javaPlayground.disassembly;

import org.digitalcrafting.javaPlayground.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Disassembly {
    private final static AssemblyDecompiler DECODER = new AssemblyDecompiler();

    public static void main(String[] args) {
        try {
//            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_1", true);
//            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_2", true);
//            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_3", true);
//            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_4_challenge", true);
//            disassemble("org/digitalcrafting/javaPlayground/disassembly/add_sub_cmp_jnz", true);
//            disassemble("org/digitalcrafting/javaPlayground/disassembly/add_sub_cmp_jnz_disassembled", false);
            disassemble("org/digitalcrafting/javaPlayground/disassembly/completionist", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void disassemble(String path, boolean compile) throws IOException {
        byte[] bytes = FileUtils.readBytes(path);
        String[] pathParts = path.split("/");
        String fileName = pathParts[pathParts.length - 1];
        System.out.println("Disassembling file " + fileName);
        String decoded = DECODER.decode(bytes);
        if (decoded == null) {
            return;
        }

        System.out.println(decoded);

        String[] pathToResource = Arrays.copyOfRange(pathParts, 0, pathParts.length - 1);
        String pathToResourceAsString = String.join("/", pathToResource);

        Path source = Paths.get(Disassembly.class.getResource("/").getPath(), "/", pathToResourceAsString);
        Path newFile = Paths.get(source.toAbsolutePath() + "/" + fileName + "_disassembled.asm");
        if (!Files.exists(newFile)) {
            Files.createFile(newFile);
        }
        Files.write(newFile, decoded.getBytes());

        if (compile) {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(
                    "bash",
                    "-c",
                    "nasm " + fileName + "_disassembled.asm" + " & " + "diff " + fileName + " " + fileName + "_disassembled"
            );
            builder.directory(source.toFile());

            try {
                Process process = builder.start();
                StringBuilder output = new StringBuilder();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }

                int exitVal = process.waitFor();
                if (exitVal == 0) {
                    System.out.println("Difference between original and disassembled for " + fileName + ": ");
                    if (output.toString().isBlank()) {
                        System.out.println("None");
                    } else {
                        System.out.println(output);
                    }
                } else {
                    System.out.println(output);
                }
                System.out.println("=====================================================");
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
