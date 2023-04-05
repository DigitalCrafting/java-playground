package org.digitalcrafting.javaPlayground.disassembly;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static org.digitalcrafting.javaPlayground.disassembly.AssemblyConsts.*;

public class AssemblyDecompiler {
    public String decode(byte[] bytes) {
        List<AssemblyInstruction> assemblyInstructionList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        int i = 0;
        while (i < bytes.length - 1) {
            byte first = bytes[i];
            AssemblyInstruction current = new AssemblyInstruction();

            if (isOpcode(first, AssemblyOpCodes.MOV)) {
                current.operation = AssemblyOpCodes.MOV.name;
                i = decompileOperation(current, i, bytes);
            } else if (isOpcode(first, AssemblyOpCodes.MOV_IMM_TO_REG)) {
                if (isMaskSet(first, (byte) 0b00001000)) {
                    current = decompileMovImmediateToRegister(first, bytes[i + 1], bytes[i + 2]);
                    i += 3;
                } else {
                    current = decompileMovImmediateToRegister(first, bytes[i + 1], null);
                    i += 2;
                }
            } else if (isOpcode(first, AssemblyOpCodes.MOV_IMM_TO_REG_MEM)) {
                current.operation = AssemblyOpCodes.MOV_IMM_TO_REG_MEM.name;
                i = decompileOperationImmediateToRegMem(current, i, bytes);
            } else if (isOpcode(first, AssemblyOpCodes.MOV_MEM_TO_ACC)) {
                boolean w_val = ((first & BaseOperands.W) == BaseOperands.W);
                if (w_val) {
                    current = decompileMovMemoryToAccumulator(first, bytes[i + 1], bytes[i + 2]);
                    i += 3;
                } else {
                    current = decompileMovMemoryToAccumulator(first, bytes[i + 1], null);
                    i += 2;
                }
            } else if (isOpcode(first, AssemblyOpCodes.MOV_ACC_TO_MEM)) {
                boolean w_val = ((first & BaseOperands.W) == BaseOperands.W);
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
            } else if (isOpcode(first, AssemblyOpCodes.IMM_TO_REG_MEM)) {
                byte second = bytes[i + 1];
                if ((second & 0b00_111_000) == 0b00_101_000) {
                    current.operation = AssemblyOpCodes.SUB.name;
                } else if ((second & 0b00_111_000) == 0b00_000_000) {
                    current.operation = AssemblyOpCodes.ADD.name;
                } else if ((second & 0b00_111_000) == 0b00_111_000) {
                    current.operation = AssemblyOpCodes.CMP.name;
                }
                i = decompileOperationImmediateToRegMem(current, i, bytes);
            } else if (isOpcode(first, AssemblyOpCodes.ADD)) {
                current.operation = AssemblyOpCodes.ADD.name;
                i = decompileOperation(current, i, bytes);
            } else if (isOpcode(first, AssemblyOpCodes.ADD_IMM_TO_ACC)) {
                current.operation = AssemblyOpCodes.ADD_IMM_TO_ACC.name;
                i = decompileOperationImmediateToAccumulator(current, i, bytes);
            } else if (isOpcode(first, AssemblyOpCodes.SUB)) {
                current.operation = AssemblyOpCodes.SUB.name;
                i = decompileOperation(current, i, bytes);
            } else if (isOpcode(first, AssemblyOpCodes.SUB_IMM_TO_ACC)) {
                current.operation = AssemblyOpCodes.SUB_IMM_TO_ACC.name;
                i = decompileOperationImmediateToAccumulator(current, i, bytes);
            } else if (isOpcode(first, AssemblyOpCodes.CMP)) {
                current.operation = AssemblyOpCodes.CMP.name;
                i = decompileOperation(current, i, bytes);
            } else if (isOpcode(first, AssemblyOpCodes.CMP_IMM_TO_ACC)) {
                current.operation = AssemblyOpCodes.CMP_IMM_TO_ACC.name;
                i = decompileOperationImmediateToAccumulator(current, i, bytes);
            } else if (isOpcode(first, AssemblyOpCodes.JMP_VARIANTS)) {
                i = decompileJumpVariants(current, i, bytes);
            } else if (isOpcode(first, AssemblyOpCodes.LOOP_VARIANTS)) {
                i = decompileLoopVariants(current, i, bytes);
            } else if (isOpcode(first, AssemblyOpCodes.PUSH)) {
                current.operation = AssemblyOpCodes.PUSH.name;
                i = decompileStackOperation(current, i, bytes);
            } else if (isOpcode(first, AssemblyOpCodes.PUSH_REG)) {
                current.operation = AssemblyOpCodes.PUSH_REG.name;
                current.destination = RegisterEncodings.getName((byte) (first & 0b00000_111), (first & 0b00001000) == 0b00001000);
                i += 2;
            } else {
                String currentByte = Integer.toBinaryString(bytes[i]);
                assemblyInstructionList.forEach(builder::append);
                builder.append("Unknown operation: " + currentByte.substring(currentByte.length() - 8));
                return builder.toString();
            }

            assemblyInstructionList.add(current);
        }

        assemblyInstructionList.forEach(builder::append);
        return builder.toString();
    }

    private boolean isOpcode(byte first, AssemblyOpCodes opcode) {
        return (first & opcode.mask) == opcode.value;
    }

    private boolean isMaskSet(byte value, byte mask) {
        return (value & mask) == mask;
    }

    private AssemblyInstruction decompileMovImmediateToRegister(byte first, byte second, Byte third) {
        AssemblyInstruction instruction = new AssemblyInstruction();
        instruction.operation = AssemblyOpCodes.MOV_IMM_TO_REG.name;
        instruction.destination = RegisterEncodings.getName((byte) (first & 0b00000_111), (first & 0b00001000) == 0b00001000);
        instruction.source = String.valueOf(shortFromBytes(second, third));
        return instruction;
    }

    private AssemblyInstruction decompileMovMemoryToAccumulator(byte first, byte second, Byte third) {
        AssemblyInstruction instruction = new AssemblyInstruction();
        instruction.operation = AssemblyOpCodes.MOV_MEM_TO_ACC.name;
        instruction.destination = "ax";
        instruction.source = "[" + shortFromBytes(second, third) + "]";
        return instruction;
    }

    private AssemblyInstruction decompileMovAccumulatorToMemory(byte first, byte second, Byte third) {
        AssemblyInstruction instruction = new AssemblyInstruction();
        instruction.operation = AssemblyOpCodes.MOV_ACC_TO_MEM.name;
        instruction.source = "ax";
        instruction.destination = "[" + shortFromBytes(second, third) + "]";
        return instruction;
    }

    private AssemblyInstruction decompileMovRegMemToSegmentRegister(byte first, byte second, Byte third, Byte fourth) {
        AssemblyInstruction instruction = new AssemblyInstruction();
        instruction.operation = AssemblyOpCodes.MOV_REG_MEM_TO_SEG.name;
        System.out.println("Called decompileMovRegMemToSegmentRegister");
        return instruction;
    }

    private AssemblyInstruction decompileMovSegmentRegisterToRegMem(byte first, byte second, Byte third, Byte fourth) {
        AssemblyInstruction instruction = new AssemblyInstruction();
        instruction.operation = AssemblyOpCodes.MOV_SEG_TO_REG_MEM.name;
        System.out.println("Called decompileMovSegmentRegisterToRegMem");
        return instruction;
    }

    private int decompileStackOperation(AssemblyInstruction instruction, int index, byte[] bytes) {
        EffectiveAddressCalculation effectiveAddress = calculateEffectiveAddressWithoutData(instruction, index, bytes);
        String addressOrRegister = effectiveAddress.affectiveAddress;

        byte second = bytes[index + 1];
        ModEncoding modEncoding = ModEncoding.get((byte) (second & BaseOperands.MOD));
        if (!ModEncoding.REGISTER_MODE.equals(modEncoding) && !AssemblyOpCodes.MOV.name.equals(instruction.operation)) {
            addressOrRegister = "word " + addressOrRegister;
        }

        instruction.destination = addressOrRegister;
        return effectiveAddress.index;
    }

    /*
     * |-----------------------------------------------------|
     * | OPCODE D W | MOD REG R/M | DISP (low) | DISP (high) |
     * |-----------------------------------------------------|
     * */
    private int decompileOperation(AssemblyInstruction instruction, int index, byte[] bytes) {
        byte first = bytes[index];
        byte second = bytes[index + 1];

        boolean d_val = (first & BaseOperands.D) == BaseOperands.D;
        boolean w_val = (first & BaseOperands.W) == BaseOperands.W;
        String register = RegisterEncodings.getName((byte) (second & BaseOperands.REG), w_val);
        EffectiveAddressCalculation effectiveAddress = calculateEffectiveAddressWithoutData(instruction, index, bytes);
        String addressOrRegister = effectiveAddress.affectiveAddress;

        if (d_val) {
            instruction.destination = register;
            instruction.source = addressOrRegister;
        } else {
            instruction.source = register;
            instruction.destination = addressOrRegister;
        }

        return effectiveAddress.index;
    }

    /*
     * |------------------------------------------------------------------------|
     * | OPCODE S W | MOD 0 0 0 R/M | DISP (low) | DISP (high) | data8 | data16 |
     * |------------------------------------------------------------------------|
     * */
    private int decompileOperationImmediateToRegMem(AssemblyInstruction instruction, int index, byte[] bytes) {
        byte first = bytes[index];
        byte second = bytes[index + 1];

        boolean w_val = (first & BaseOperands.W) == BaseOperands.W;
        ModEncoding modEncoding = ModEncoding.get((byte) (second & BaseOperands.MOD));

        EffectiveAddressCalculation effectiveAddress = calculateEffectiveAddressWithData(instruction, index, bytes);
        String addressOrRegister = effectiveAddress.affectiveAddress;
        Byte data8 = effectiveAddress.date8;
        Byte data16 = effectiveAddress.date16;


        if (!ModEncoding.REGISTER_MODE.equals(modEncoding) && !AssemblyOpCodes.MOV.name.equals(instruction.operation)) {
            if (w_val) {
                addressOrRegister = "word " + addressOrRegister;
            } else {
                addressOrRegister = "byte " + addressOrRegister;
            }
        }

        short dataAsShort = shortFromBytes(data8, data16);
        String data = String.valueOf(dataAsShort);
        if (AssemblyOpCodes.MOV.name.equals(instruction.operation)) {
            if (data16 != null) {
                data = " word " + data;
            } else {
                data = " byte " + data;
            }
        }

        instruction.destination = addressOrRegister;
        instruction.source = data;

        return effectiveAddress.index;
    }

    /*
     * |------------------------------|
     * | OPCODE 0 W |  data8 | data16 |
     * |------------------------------|
     * */
    private int decompileOperationImmediateToAccumulator(AssemblyInstruction instruction, int index, byte[] bytes) {
        byte first = bytes[index];
        byte data8 = bytes[index + 1];
        Byte data16 = null;
        boolean w_val = (first & BaseOperands.W) == BaseOperands.W;
        String accumulator;

        if (w_val) {
            data16 = bytes[index + 2];
            index += 3;
            accumulator = "ax";
        } else {
            index += 2;
            accumulator = "al";
        }

        instruction.destination = accumulator;
        instruction.source = String.valueOf(shortFromBytes(data8, data16));

        return index;
    }

    private int decompileJumpVariants(AssemblyInstruction instruction, int index, byte[] bytes) {
        JumpVariants byBits = JumpVariants.getByBits(bytes[index]);
        if (byBits != null) {
            instruction.operation = byBits.name;
        }
        /* For some reason when decompiling the assembly, you have to add this offset.
         *  Unless you provide the nasm with actual labels, instead of the offsets, which I didn't yet.
         * */
        instruction.destination = "($+2)+" + String.valueOf(bytes[index + 1]);

        return index + 2;
    }

    private int decompileLoopVariants(AssemblyInstruction instruction, int index, byte[] bytes) {
        LoopVariants byBits = LoopVariants.getByBits(bytes[index]);
        if (byBits != null) {
            instruction.operation = byBits.name;
        }
        /* For some reason when decompiling the assembly, you have to add this offset.
         *  Unless you provide the nasm with actual labels, instead of the offsets, which I didn't yet.
         * */
        instruction.destination = "($+2)+" + String.valueOf(bytes[index + 1]);

        return index + 2;
    }

    private EffectiveAddressCalculation calculateEffectiveAddressWithoutData(AssemblyInstruction instruction, int index, byte[] bytes) {
        byte first = bytes[index];
        byte second = bytes[index + 1];
        Byte third = null;
        Byte fourth = null;

        boolean w_val = (first & BaseOperands.W) == BaseOperands.W;
        ModEncoding modEncoding = ModEncoding.get((byte) (second & BaseOperands.MOD));
        String addressOrRegister = "";

        if (ModEncoding.REGISTER_MODE.equals(modEncoding)) {
            addressOrRegister = RegisterEncodings.getName((byte) (second & BaseOperands.R_M), w_val);
            index += 2;
        } else {
            String effectiveAddressCalculation = EFFECTIVE_ADDRESS_CALCULATIONS.get((byte) (second & BaseOperands.R_M));

            if (ModEncoding.MEMORY_MODE_NO_DISP.equals(modEncoding)) {
                if ((byte) (second & BaseOperands.R_M) == 0b00000110) {
                    third = bytes[index + 2];
                    fourth = bytes[index + 3];
                    index += 4;

                    if ((byte) (second & BaseOperands.R_M) == 0b00000110) {
                        short shortVal = shortFromBytes(third, fourth);
                        effectiveAddressCalculation = String.valueOf(shortVal);
                    }
                } else {
                    index += 2;
                }
            } else if (ModEncoding.MEMORY_MODE_8_DISP.equals(modEncoding)) {
                third = bytes[index + 2];
                index += 3;

                if (third >= 0) {
                    effectiveAddressCalculation += " + " + third;
                } else if (third < 0) {
                    effectiveAddressCalculation += String.valueOf(third);
                }
            } else if (ModEncoding.MEMORY_MODE_16_DISP.equals(modEncoding)) {
                third = bytes[index + 2];
                fourth = bytes[index + 3];
                index += 4;

                short shortVal = shortFromBytes(third, fourth);
                if (shortVal >= 0) {
                    effectiveAddressCalculation += " + " + String.valueOf(shortVal);
                } else {
                    effectiveAddressCalculation += " " + shortVal;
                }
            }
            addressOrRegister = "[" + effectiveAddressCalculation + "]";
        }

        return new EffectiveAddressCalculation(index, addressOrRegister);
    }

    private EffectiveAddressCalculation calculateEffectiveAddressWithData(AssemblyInstruction instruction, int index, byte[] bytes) {
        byte first = bytes[index];
        byte second = bytes[index + 1];

        Byte dispLow = null;
        Byte dispHigh = null;
        Byte data8 = null;
        Byte data16 = null;

        ModEncoding modEncoding = ModEncoding.get((byte) (second & BaseOperands.MOD));
        boolean s_val = (first & BaseOperands.S) == BaseOperands.S;
        boolean w_val = (first & BaseOperands.W) == BaseOperands.W;

        boolean extended_data;
        if ("mov".equals(instruction.operation)) {
            extended_data = w_val;
        } else {
            extended_data = !s_val && w_val;
        }

        String addressOrRegister;

        /* Register Mode == No displacement */
        if (ModEncoding.REGISTER_MODE.equals(modEncoding)) {
            addressOrRegister = RegisterEncodings.getName((byte) (second & BaseOperands.R_M), w_val);
            data8 = bytes[index + 2];
            if (extended_data) {
                data16 = bytes[index + 3];
                index += 4;
            } else {
                index += 3;
            }
        } else {
            String effectiveAddressCalculation = EFFECTIVE_ADDRESS_CALCULATIONS.get((byte) (second & BaseOperands.R_M));
            if (ModEncoding.MEMORY_MODE_NO_DISP.equals(modEncoding)) {
                if ((byte) (second & BaseOperands.R_M) == 0b00000110) {
                    dispLow = bytes[index + 2];
                    dispHigh = bytes[index + 3];
                    data8 = bytes[index + 4];
                    if (extended_data) {
                        data16 = bytes[index + 5];
                        index += 6;
                    } else {
                        index += 5;
                    }
                    short shortVal = shortFromBytes(dispLow, dispHigh);
                    effectiveAddressCalculation = String.valueOf(shortVal);
                } else {
                    data8 = bytes[index + 2];
                    if (extended_data) {
                        data16 = bytes[index + 3];
                        index += 4;
                    } else {
                        index += 3;
                    }
                }
            } else if (ModEncoding.MEMORY_MODE_8_DISP.equals(modEncoding)) {
                dispLow = bytes[index + 2];
                data8 = bytes[index + 3];
                if (extended_data) {
                    data16 = bytes[index + 4];
                    index += 5;
                } else {
                    index += 4;
                }

                if (dispLow > 0) {
                    effectiveAddressCalculation += " + " + dispLow;
                } else if (dispLow < 0) {
                    effectiveAddressCalculation += dispLow;
                }
            } else if (ModEncoding.MEMORY_MODE_16_DISP.equals(modEncoding)) {
                dispLow = bytes[index + 2];
                dispHigh = bytes[index + 3];
                data8 = bytes[index + 4];
                if (extended_data) {
                    data16 = bytes[index + 5];
                    index += 6;
                } else {
                    index += 5;
                }

                short shortVal = shortFromBytes(dispLow, dispHigh);
                if (shortVal >= 0) {
                    effectiveAddressCalculation += " + " + String.valueOf(shortVal);
                } else {
                    effectiveAddressCalculation += " " + shortVal;
                }
            }
            addressOrRegister = "[" + effectiveAddressCalculation + "]";
        }

        return new EffectiveAddressCalculation(index, addressOrRegister, data8, data16);
    }

    private short shortFromBytes(byte low, Byte high) {
        if (high == null) {
            return low;
        } else {
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.put(low);
            bb.put(high);
            return bb.getShort(0);
        }
    }
}
