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

    public enum AssemblyOpCodes {
        MOV((byte) 0b100010_00, (byte) 0b11111100, "mov"),
        MOV_IMM_TO_REG((byte) 0b1011_0000, (byte) 0b11110000,  "mov"),
        MOV_IMM_TO_REG_MEM((byte) 0b11000110, (byte) 0b11111110,  "mov"),
        MOV_MEM_TO_ACC((byte) 0b10100000, (byte) 0b11111110,  "mov"),
        MOV_ACC_TO_MEM((byte) 0b10100010, (byte) 0b11111110,  "mov"),
        MOV_REG_MEM_TO_SEG((byte) 0b10001110, (byte) 0b11111111,  "mov"),
        MOV_SEG_TO_REG_MEM((byte) 0b10001100, (byte) 0b11111111,  "mov"),
        ADD((byte) 0b00000000, (byte) 0b11111100,  "add"),
        ADD_IMM_TO_REG_MEM((byte) 0b10000000, (byte) 0b11111100,  "add"),
        ADD_IMM_TO_ACC((byte) 0b00000100, (byte) 0b11111110,  "add"),
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
