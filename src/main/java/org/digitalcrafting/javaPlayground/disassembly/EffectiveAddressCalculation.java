package org.digitalcrafting.javaPlayground.disassembly;

public class EffectiveAddressCalculation {
    public int index;
    public String affectiveAddress;
    public Byte date8;
    public Byte date16;

    public EffectiveAddressCalculation(int index, String affectiveAddress) {
        this(index, affectiveAddress, null);
    }

    public EffectiveAddressCalculation(int index, String affectiveAddress, Byte date8) {
        this(index, affectiveAddress, date8, null);
    }

    public EffectiveAddressCalculation(int index, String affectiveAddress, Byte date8, Byte date16) {
        this.index = index;
        this.affectiveAddress = affectiveAddress;
        this.date8 = date8;
        this.date16 = date16;
    }
}
