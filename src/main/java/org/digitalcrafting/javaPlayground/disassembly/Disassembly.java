package org.digitalcrafting.javaPlayground.disassembly;

import org.digitalcrafting.javaPlayground.utils.FileUtils;

import java.io.IOException;

public class Disassembly {
    public static void main(String[] args) {
        try {
            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_1");
            disassemble("org/digitalcrafting/javaPlayground/disassembly/mov_2");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void disassemble(String path) throws IOException {
        byte[] bytes = FileUtils.readBytes(path);
        String[] pathParts = path.split("/");
        String fileName = pathParts[pathParts.length - 1];

        System.out.println("Disassembling file " + fileName);
        for (int i = 0; i < bytes.length; i += 2) {
            byte first = bytes[i];
            byte second = bytes[i + 1];

            System.out.println(new AssemblyInstruction(first, second));
        }
    }

    private static class AssemblyInstruction {
        private String operation;
        private String destination;
        private String source;

        public AssemblyInstruction(byte first, byte second) {
            operation = AssemblyOpCodes.getName((byte) (first & AssemblySyntaxBits.OPCODE));
            boolean d_val = (first & AssemblySyntaxBits.D) == AssemblySyntaxBits.D;
            boolean w_val = (first & AssemblySyntaxBits.W) == AssemblySyntaxBits.W;
            ModEncoding modEncoding = ModEncoding.get((byte) (second & AssemblySyntaxBits.MOD));

            if (d_val) {
                destination = RegisterEncodings.getName((byte) (second & AssemblySyntaxBits.REG), w_val);
                if (ModEncoding.REGISTER_MODE.equals(modEncoding)) {
                    source = RegisterEncodings.getName((byte) (second & AssemblySyntaxBits.R_M), w_val);
                }
            } else {
                source = RegisterEncodings.getName((byte) (second & AssemblySyntaxBits.REG), w_val);
                if (ModEncoding.REGISTER_MODE.equals(modEncoding)) {
                    destination = RegisterEncodings.getName((byte) (second & AssemblySyntaxBits.R_M), w_val);
                }
            }
        }

        @Override
        public String toString() {
            return operation + " " + destination + "," + source;
        }
    }

    private enum RegisterEncodings {
        /* I populated bits 2-4 and 5-7 with the same values
        *  in order to reuse the enum. The correct bit range
        *  will be determined by mask.
        * */
        AL_AX((byte) 0b00_000_000, "AL", "AX"),
        CL_CX((byte) 0b00_001_001, "CL", "CX"),
        DL_DX((byte) 0b00_010_010, "DL", "DX"),
        BL_BX((byte) 0b00_011_011, "BL", "BX"),
        AH_SP((byte) 0b00_100_100, "AH", "SP"),
        CH_BP((byte) 0b00_101_101, "CH", "BP"),
        DH_SI((byte) 0b00_110_110, "DH", "SI"),
        BH_DI((byte) 0b00_111_111, "BH", "DI")
        ;
        private byte value;
        private String name_w0;
        private String name_w1;

        RegisterEncodings(byte value, String name_w0, String name_w1) {
            this.value = value;
            this.name_w0 = name_w0;
            this.name_w1 = name_w1;
        }

        public static String getName(byte mask, boolean w) {
            for (RegisterEncodings encoding : RegisterEncodings.values()) {
                if ((mask & encoding.value) == mask) {
                    if (w) {
                        return encoding.name_w1;
                    } else {
                        return encoding.name_w0;
                    }
                }
            }
            return "Unknown REG value";
        }
    }

    private enum AssemblyOpCodes {
        MOV((byte) 0b100010_00, "MOV");
        private byte value;
        private String name;

        AssemblyOpCodes(byte value, String name) {
            this.value = value;
            this.name = name;
        }

        public static String getName(byte opcode) {
            for (AssemblyOpCodes code : AssemblyOpCodes.values()) {
                if ((code.value & opcode) == opcode) {
                    return code.name;
                }
            }
            return "Unknown OPCODE";
        }
    }

    private enum ModEncoding {
        MEMORY_MODE_NO_DISP((byte) 0b00_000000),
        MEMORY_MODE_8_DISP((byte) 0b01_000000),
        MEMORY_MODE_16_DISP((byte) 0b10_000000),
        REGISTER_MODE((byte) 0b11_000000),
        ;
        private byte value;

        ModEncoding(byte value) {
            this.value = value;
        }

        public static ModEncoding get(byte mask) {
            for (ModEncoding encoding : ModEncoding.values()) {
                if ((encoding.value & mask) == mask) {
                    return encoding;
                }
            }
            return null;
        }
    }

    private static class AssemblySyntaxBits {
        public static final byte OPCODE = (byte) 0b11111100;
        public static final byte D = (byte) 0b00000010;
        public static final byte W = (byte) 0b00000001;

        public static final byte MOD = (byte) 0b11000000;
        public static final byte REG = (byte) 0b00111000;
        public static final byte R_M = (byte) 0b00000111;
    }

}
