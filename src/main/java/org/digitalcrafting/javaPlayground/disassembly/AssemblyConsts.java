package org.digitalcrafting.javaPlayground.disassembly;

import java.util.HashMap;
import java.util.Map;

public class AssemblyConsts {
    public static final Map<Byte, String> EFFECTIVE_ADDRESS_CALCULATIONS = new HashMap<>();

    static {
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000000, "bx + si");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000001, "bx + di");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000010, "bp + si");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000011, "bp + di");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000100, "si");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000101, "di");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000110, "bp");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000111, "bx");
    }

    public enum RegisterEncodings {
        /* I populated bits 2-4 and 5-7 with the same values
         *  in order to reuse the enum. The correct bit range
         *  will be determined by mask.
         * */
        AL_AX((byte) 0b00_000_000, "al", "ax"),
        CL_CX((byte) 0b00_001_001, "cl", "cx"),
        DL_DX((byte) 0b00_010_010, "dl", "dx"),
        BL_BX((byte) 0b00_011_011, "bl", "bx"),
        AH_SP((byte) 0b00_100_100, "ah", "sp"),
        CH_BP((byte) 0b00_101_101, "ch", "bp"),
        DH_SI((byte) 0b00_110_110, "dh", "si"),
        BH_DI((byte) 0b00_111_111, "bh", "di");
        public byte value;
        public String name_w0;
        public String name_w1;

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

    public enum JumpVariants {
//        JNZ((byte) 0b00000101, "jnz"),
        JE((byte) 0b00000100, "je"),
        JL((byte) 0b00001100, "jl"),
        JLE((byte) 0b00001110, "jle"),
        JB((byte) 0b00000010, "jb"),
        JBE((byte) 0b00000110, "jbe"),
        JP((byte) 0b00001010, "jp"),
        JO((byte) 0b00000000, "jo"),
        JS((byte) 0b00001000, "js"),
        JNE((byte) 0b00000101, "jne"),
        JNL((byte) 0b00001101, "jnl"),
        JG((byte) 0b00001111, "jg"),
        JNB((byte) 0b00000011, "jnb"),
        JA((byte) 0b00000111, "ja"),
        JNP((byte) 0b00001011, "jnp"),
        JNO((byte) 0b00000001, "jno"),
        JNS((byte) 0b00001001, "jns");
        public byte value;
        public String name;

        JumpVariants(byte value, String name) {
            this.value = value;
            this.name = name;
        }

        public static JumpVariants getByBits(byte check) {
            for (JumpVariants variant : JumpVariants.values()) {
                if ((check & 0b00001111) == variant.value) {
                    return variant;
                }
            }
            return null;
        }
    }

    public enum LoopVariants {
        LOOP((byte) 0b00000010, "loop"),
        LOOPZ((byte) 0b00000001, "loopz"),
        LOOPNZ((byte) 0b00000000, "loopnz"),
        JCXZ((byte) 0b00000011, "jcxz");
        public byte value;
        public String name;

        LoopVariants(byte value, String name) {
            this.value = value;
            this.name = name;
        }

        public static LoopVariants getByBits(byte check) {
            for (LoopVariants variant : LoopVariants.values()) {
                if ((check & 0b00001111) == variant.value) {
                    return variant;
                }
            }
            return null;
        }
    }

    public enum AssemblyOpCodes {
        MOV((byte) 0b100010_00, (byte) 0b11111100, "mov"),
        MOV_IMM_TO_REG((byte) 0b1011_0000, (byte) 0b11110000, "mov"),
        MOV_IMM_TO_REG_MEM((byte) 0b11000110, (byte) 0b11111110, "mov"),
        MOV_MEM_TO_ACC((byte) 0b10100000, (byte) 0b11111110, "mov"),
        MOV_ACC_TO_MEM((byte) 0b10100010, (byte) 0b11111110, "mov"),
        MOV_REG_MEM_TO_SEG((byte) 0b10001110, (byte) 0b11111111, "mov"),
        MOV_SEG_TO_REG_MEM((byte) 0b10001100, (byte) 0b11111111, "mov"),
        ADD((byte) 0b00000000, (byte) 0b11111100, "add"),
        ADD_IMM_TO_ACC((byte) 0b00000100, (byte) 0b11111110, "add"),
        SUB((byte) 0b00101000, (byte) 0b11111100, "sub"),
        SUB_IMM_TO_ACC((byte) 0b00101100, (byte) 0b11111110, "sub"),
        CMP((byte) 0b00111000, (byte) 0b11111100, "cmp"),
        CMP_IMM_TO_ACC((byte) 0b00111100, (byte) 0b11111110, "cmp"),
        IMM_TO_REG_MEM((byte) 0b10000000, (byte) 0b11111100, "DETERMINE BY REG"),
        JMP_VARIANTS((byte) 0b01110000, (byte) 0b11110000, "DETERMINE BY LOWER BITS"),
        LOOP_VARIANTS((byte) 0b11100000, (byte) 0b11110000, "DETERMINE BY LOWER BITS")
        ;
        public byte value;
        public byte mask;
        public String name;

        AssemblyOpCodes(byte value, byte mask, String name) {
            this.value = value;
            this.mask = mask;
            this.name = name;
        }

        public static AssemblyOpCodes get(byte check) {
            for (AssemblyOpCodes code : AssemblyOpCodes.values()) {
                if ((check & code.mask) == code.value) {
                    return code;
                }
            }
            return null;
        }
    }

    public enum ModEncoding {
        MEMORY_MODE_NO_DISP((byte) 0b00_000000),
        MEMORY_MODE_8_DISP((byte) 0b01_000000),
        MEMORY_MODE_16_DISP((byte) 0b10_000000),
        REGISTER_MODE((byte) 0b11_000000),
        ;
        public byte value;

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

    public static class BaseOperands {
        public static final byte D = (byte) 0b00000010;
        public static final byte S = (byte) 0b00000010;
        public static final byte W = (byte) 0b00000001;

        public static final byte MOD = (byte) 0b11000000;
        public static final byte REG = (byte) 0b00111000;
        public static final byte R_M = (byte) 0b00000111;
    }
}
