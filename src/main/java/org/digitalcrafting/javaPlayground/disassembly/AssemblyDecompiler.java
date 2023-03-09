package org.digitalcrafting.javaPlayground.disassembly;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.digitalcrafting.javaPlayground.disassembly.AssemblyConsts.*;

public class AssemblyDecompiler {
    public String decode(byte[] bytes) {
        StringBuilder decodedAssembly = new StringBuilder();

        int i = 0;
        while (i < bytes.length - 1) {
            byte first = bytes[i];
            AssemblyInstruction current = new AssemblyInstruction();

            if (isOpcode(first, AssemblyOpCodes.MOV)) {
                byte second = bytes[i + 1];
                ModEncoding modEncoding = ModEncoding.get((byte) (second & MOV_RegMem_toFrom_Reg_Masks.MOD));
                if (ModEncoding.REGISTER_MODE.equals(modEncoding)) {
                    current = decompileMov(first, second);
                    i += 2;
                } else if (ModEncoding.MEMORY_MODE_NO_DISP.equals(modEncoding)) {
                    if ((byte) (second & MOV_RegMem_toFrom_Reg_Masks.R_M) == 0b00000110) {
                        current = decompileMov(first, second, bytes[i + 2], bytes[i + 3]);
                        i += 4;
                    } else {
                        current = decompileMov(first, second);
                        i += 2;
                    }
                } else if (ModEncoding.MEMORY_MODE_8_DISP.equals(modEncoding)) {
                    current = decompileMov(first, second, bytes[i + 2]);
                    i += 3;
                } else if (ModEncoding.MEMORY_MODE_16_DISP.equals(modEncoding)) {
                    current = decompileMov(first, second, bytes[i + 2], bytes[i + 3]);
                    i += 4;
                }
            } else if (isOpcode(first, AssemblyOpCodes.MOV_IMM_TO_REG)) {
                if (isMaskSet(first, (byte) 0b00001000)) {
                    current = decompileMovImmediateToRegister(first, bytes[i + 1], bytes[i + 2]);
                    i += 3;
                } else {
                    current = decompileMovImmediateToRegister(first, bytes[i + 1], null);
                    i += 2;
                }
            } else if (isOpcode(first, AssemblyOpCodes.MOV_IMM_TO_REG_MEM)) {
                boolean w_val = ((first & MOV_RegMem_toFrom_Reg_Masks.W) == MOV_RegMem_toFrom_Reg_Masks.W);
                byte second = bytes[i + 1];
                ModEncoding modEncoding = ModEncoding.get((byte) (second & MOV_RegMem_toFrom_Reg_Masks.MOD));
                if (ModEncoding.REGISTER_MODE.equals(modEncoding)) {
                    if (w_val) {
                        current = decompileMovImmediateToRegisterOrMemory(first, second, bytes[i + 2], bytes[i + 3], null, null);
                        i += 4;
                    } else {
                        current = decompileMovImmediateToRegisterOrMemory(first, second, bytes[i + 2], null, null, null);
                        i += 3;
                    }
                } else if (ModEncoding.MEMORY_MODE_NO_DISP.equals(modEncoding)) {
                    if ((byte) (second & MOV_RegMem_toFrom_Reg_Masks.R_M) == 0b00000110) {
                        if (w_val) {
                            current = decompileMovImmediateToRegisterOrMemory(first, second, bytes[i + 2], bytes[i + 3], bytes[i + 4], bytes[i + 5]);
                            i += 6;
                        } else {
                            current = decompileMovImmediateToRegisterOrMemory(first, second, bytes[i + 2], bytes[i + 3], bytes[i + 4], null);
                            i += 5;
                        }
                    } else {
                        if (w_val) {
                            current = decompileMovImmediateToRegisterOrMemory(first, second, bytes[i + 2], bytes[i + 3], null, null);
                            i += 4;
                        } else {
                            current = decompileMovImmediateToRegisterOrMemory(first, second, bytes[i + 2], null, null, null);
                            i += 3;
                        }
                    }
                } else if (ModEncoding.MEMORY_MODE_8_DISP.equals(modEncoding)) {
                    if (w_val) {
                        current = decompileMovImmediateToRegisterOrMemory(first, second, bytes[i + 2], bytes[i + 3], null, null);
                        i += 4;
                    } else {
                        current = decompileMovImmediateToRegisterOrMemory(first, second, bytes[i + 2], null, null, null);
                        i += 3;
                    }
                } else if (ModEncoding.MEMORY_MODE_16_DISP.equals(modEncoding)) {
                    if (w_val) {
                        current = decompileMovImmediateToRegisterOrMemory(first, second, bytes[i + 2], bytes[i + 3], bytes[i + 4], bytes[i + 5]);
                        i += 6;
                    } else {
                        current = decompileMovImmediateToRegisterOrMemory(first, second, bytes[i + 2], bytes[i + 3], bytes[i + 4], null);
                        i += 5;
                    }
                }
            } else if (isOpcode(first, AssemblyOpCodes.MOV_MEM_TO_ACC)) {
                boolean w_val = ((first & MOV_RegMem_toFrom_Reg_Masks.W) == MOV_RegMem_toFrom_Reg_Masks.W);
                if (w_val) {
                    current = decompileMovMemoryToAccumulator(first, bytes[i + 1], bytes[i + 2]);
                    i += 3;
                } else {
                    current = decompileMovMemoryToAccumulator(first, bytes[i + 1], null);
                    i += 2;
                }
            } else if (isOpcode(first, AssemblyOpCodes.MOV_ACC_TO_MEM)) {
                boolean w_val = ((first & MOV_RegMem_toFrom_Reg_Masks.W) == MOV_RegMem_toFrom_Reg_Masks.W);
                if (w_val) {
                    current = decompileMovAccumulatorToMemory(first, bytes[i + 1], bytes[i + 2]);
                    i += 3;
                } else {
                    current = decompileMovAccumulatorToMemory(first, bytes[i + 1], null);
                    i += 2;
                }
            } else if (isOpcode(first, AssemblyOpCodes.MOV_REG_MEM_TO_SEG)) {
                current = decompileMovRegMemToSegmentRegister(first, bytes[i + 1], bytes[i + 2], bytes[i + 3]);
                i += 4;
            } else if (isOpcode(first, AssemblyOpCodes.MOV_SEG_TO_REG_MEM)) {
                current = decompileMovSegmentRegisterToRegMem(first, bytes[i + 1], bytes[i + 2], bytes[i + 3]);
                i += 4;
            } else {
                i += 2;
            }

            decodedAssembly.append(current).append("\n");
        }

        return decodedAssembly.toString();
    }

    private boolean isOpcode(byte first, AssemblyOpCodes opcode) {
        return (first & opcode.mask) == opcode.value;
    }

    private boolean isMaskSet(byte value, byte mask) {
        return (value & mask) == mask;
    }

    private AssemblyInstruction decompileMov(byte first, byte second) {
        return decompileMov(first, second, null);
    }

    private AssemblyInstruction decompileMov(byte first, byte second, Byte third) {
        return decompileMov(first, second, third, null);
    }

    private AssemblyInstruction decompileMov(byte first, byte second, Byte third, Byte fourth) {
        AssemblyInstruction instruction = new AssemblyInstruction();
        instruction.operation = AssemblyOpCodes.MOV.name;
        boolean d_val = (first & MOV_RegMem_toFrom_Reg_Masks.D) == MOV_RegMem_toFrom_Reg_Masks.D;
        boolean w_val = (first & MOV_RegMem_toFrom_Reg_Masks.W) == MOV_RegMem_toFrom_Reg_Masks.W;
        ModEncoding modEncoding = ModEncoding.get((byte) (second & MOV_RegMem_toFrom_Reg_Masks.MOD));

        String register = RegisterEncodings.getName((byte) (second & MOV_RegMem_toFrom_Reg_Masks.REG), w_val);
        String address_or_register;

        if (ModEncoding.REGISTER_MODE.equals(modEncoding)) {
            address_or_register = RegisterEncodings.getName((byte) (second & MOV_RegMem_toFrom_Reg_Masks.R_M), w_val);
        } else {
            String effectiveAddressCalculation = EFFECTIVE_ADDRESS_CALCULATIONS.get((byte) (second & MOV_RegMem_toFrom_Reg_Masks.R_M));
            if (ModEncoding.MEMORY_MODE_NO_DISP.equals(modEncoding)) {
                if ((byte) (second & MOV_RegMem_toFrom_Reg_Masks.R_M) == 0b00000110) {
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    bb.put(third);
                    bb.put(fourth);
                    short shortVal = bb.getShort(0);
                    effectiveAddressCalculation = String.valueOf(shortVal);
                }
            } else if (ModEncoding.MEMORY_MODE_8_DISP.equals(modEncoding)) {
                if (third > 0) {
                    effectiveAddressCalculation += " + " + third;
                } else if (third < 0) {
                    effectiveAddressCalculation += String.valueOf(third);
                }
            } else if (ModEncoding.MEMORY_MODE_16_DISP.equals(modEncoding)) {
                ByteBuffer bb = ByteBuffer.allocate(2);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                bb.put(third);
                bb.put(fourth);
                short shortVal = bb.getShort(0);
                if (shortVal > 0) {
                    effectiveAddressCalculation += " + " + String.valueOf(shortVal);
                } else if (shortVal < 0) {
                    effectiveAddressCalculation += " " + shortVal;
                }
            }
            address_or_register = "[" + effectiveAddressCalculation + "]";
        }

        if (d_val) {
            instruction.destination = register;
            instruction.source = address_or_register;
        } else {
            instruction.source = register;
            instruction.destination = address_or_register;
        }

        return instruction;
    }

    private AssemblyInstruction decompileMovImmediateToRegister(byte first, byte second, Byte third) {
        AssemblyInstruction instruction = new AssemblyInstruction();
        instruction.operation = AssemblyOpCodes.MOV.name;
        instruction.destination = RegisterEncodings.getName((byte) (first & 0b00000_111), (first & 0b00001000) == 0b00001000);

        if (third == null) {
            instruction.source = String.valueOf(second);
        } else {
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.put(second);
            bb.put(third);
            short shortVal = bb.getShort(0);
            instruction.source = String.valueOf(shortVal);
        }

        return instruction;
    }

    private AssemblyInstruction decompileMovImmediateToRegisterOrMemory(byte first, byte second, Byte third, Byte fourth, Byte fifth, Byte sixth) {
        AssemblyInstruction instruction = new AssemblyInstruction();
        instruction.operation = AssemblyOpCodes.MOV_IMM_TO_REG_MEM.name;
        ModEncoding modEncoding = ModEncoding.get((byte) (second & MOV_RegMem_toFrom_Reg_Masks.MOD));

        Byte dispLow = null;
        Byte dispHigh = null;
        Byte data8 = null;
        Byte data16 = null;

        String address_or_register;
        if (ModEncoding.REGISTER_MODE.equals(modEncoding)) {
            address_or_register = RegisterEncodings.getName((byte) (second & MOV_RegMem_toFrom_Reg_Masks.R_M), sixth != null);
            data8 = third;
            data16 = fourth;
        } else {
            String effectiveAddressCalculation = EFFECTIVE_ADDRESS_CALCULATIONS.get((byte) (second & MOV_RegMem_toFrom_Reg_Masks.R_M));
            if (ModEncoding.MEMORY_MODE_NO_DISP.equals(modEncoding)) {
                if ((byte) (second & MOV_RegMem_toFrom_Reg_Masks.R_M) == 0b00000110) {
                    dispLow = third;
                    dispHigh = fourth;
                    data8 = fifth;
                    data16 = sixth;

                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    bb.put(dispLow);
                    bb.put(dispHigh);
                    short shortVal = bb.getShort(0);
                    effectiveAddressCalculation = String.valueOf(shortVal);
                } else {
                    data8 = third;
                    data16 = fourth;
                }
            } else if (ModEncoding.MEMORY_MODE_8_DISP.equals(modEncoding)) {
                dispLow = third;
                data8 = fourth;
                data16 = fifth;

                if (dispLow > 0) {
                    effectiveAddressCalculation += " + " + dispLow;
                } else if (dispLow < 0) {
                    effectiveAddressCalculation += dispLow;
                }
            } else if (ModEncoding.MEMORY_MODE_16_DISP.equals(modEncoding)) {
                dispLow = third;
                dispHigh = fourth;
                data8 = fifth;
                data16 = sixth;

                ByteBuffer bb = ByteBuffer.allocate(2);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                bb.put(dispLow);
                bb.put(dispHigh);
                short shortVal = bb.getShort(0);
                if (shortVal > 0) {
                    effectiveAddressCalculation += " + " + String.valueOf(shortVal);
                } else if (shortVal < 0) {
                    effectiveAddressCalculation += " " + shortVal;
                }
            }
            address_or_register = "[" + effectiveAddressCalculation + "]";
        }

        String data;
        if (data16 != null) {
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.put(data8);
            bb.put(data16);
            short shortVal = bb.getShort(0);
            data = " word " + shortVal;
        } else {
            data = " byte " + data8;
        }

        instruction.destination = address_or_register;
        instruction.source = data;

        return instruction;
    }

    private AssemblyInstruction decompileMovMemoryToAccumulator(byte first, byte second, Byte third) {
        AssemblyInstruction instruction = new AssemblyInstruction();
        instruction.operation = "MOV";
        instruction.destination = "AX";

        String memory;
        if (third == null) {
            memory = "[" + (int) second + "]";
        } else {
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.put(second);
            bb.put(third);
            short shortVal = bb.getShort(0);
            memory = "[" + shortVal + "]";
        }

        instruction.source = memory;
        return instruction;
    }

    private AssemblyInstruction decompileMovAccumulatorToMemory(byte first, byte second, Byte third) {
        AssemblyInstruction instruction = new AssemblyInstruction();
        instruction.operation = "MOV";
        instruction.source = "AX";

        String memory;
        if (third == null) {
            memory = "[" + (int) second + "]";
        } else {
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.put(second);
            bb.put(third);
            short shortVal = bb.getShort(0);
            memory = "[" + shortVal + "]";
        }

        instruction.destination = memory;

        return instruction;
    }

    private AssemblyInstruction decompileMovRegMemToSegmentRegister(byte first, byte second, Byte third, Byte fourth) {
        AssemblyInstruction instruction = new AssemblyInstruction();
        instruction.operation = "MOV";
        System.out.println("Called decompileMovRegMemToSegmentRegister");
        return instruction;
    }

    private AssemblyInstruction decompileMovSegmentRegisterToRegMem(byte first, byte second, Byte third, Byte fourth) {
        AssemblyInstruction instruction = new AssemblyInstruction();
        instruction.operation = "MOV";
        System.out.println("Called decompileMovSegmentRegisterToRegMem");
        return instruction;
    }
}
