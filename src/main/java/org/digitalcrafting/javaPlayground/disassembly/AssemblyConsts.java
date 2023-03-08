package org.digitalcrafting.javaPlayground.disassembly;

import java.util.HashMap;
import java.util.Map;

public class AssemblyConsts {
    public static final Map<Byte, String> EFFECTIVE_ADDRESS_CALCULATIONS = new HashMap<>();

    static {
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000000, "BX + SI");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000001, "BX + DI");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000010, "BP + SI");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000011, "BP + DI");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000100, "SI");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000101, "DI");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000110, "BP");
        EFFECTIVE_ADDRESS_CALCULATIONS.put((byte) 0b00000111, "BX");
    }

    public enum RegisterEncodings {
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
        BH_DI((byte) 0b00_111_111, "BH", "DI");
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
        MOV((byte) 0b100010_00, (byte) 0b11111100, "MOV"),
        MOV_IMM_TO_REG((byte) 0b1011_0000, (byte) 0b1111_0000,  "MOV"),
        MOV_IMM_TO_REG_MEM((byte) 0b11000110, (byte) 0b11111110,  "MOV"),
        ;
        public byte value;
        public byte mask;
        public String name;

        AssemblyOpCodes(byte value, byte mask, String name) {
            this.value = value;
            this.mask = mask;
            this.name = name;
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

    public static class MOV_RegMem_toFrom_Reg_Masks {
        public static final byte D = (byte) 0b00000010;
        public static final byte W = (byte) 0b00000001;

        public static final byte MOD = (byte) 0b11000000;
        public static final byte REG = (byte) 0b00111000;
        public static final byte R_M = (byte) 0b00000111;
    }
}
