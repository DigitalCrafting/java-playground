package org.digitalcrafting.javaPlayground.algo;

import org.digitalcrafting.javaPlayground.utils.LoggingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneNumberMnemonicsAlgorithm {

    public static void main(String[] args) {
        LoggingUtils.log(phoneNumberMnemonics("1905"));
    }

    private static Map<Character, String> mnemonics = new HashMap<>();

    public static ArrayList<String> phoneNumberMnemonics(String phoneNumber) {
        initialize();
        List<String> combinations = new ArrayList<>();
        getCombination(0, phoneNumber, new StringBuffer(), combinations);
        return new ArrayList<>(combinations);
    }

    public static void initialize() {
        mnemonics.put('2', "abc");
        mnemonics.put('3', "def");
        mnemonics.put('4', "ghi");
        mnemonics.put('5', "jkl");
        mnemonics.put('6', "mno");
        mnemonics.put('7', "pqrs");
        mnemonics.put('8', "tuv");
        mnemonics.put('9', "xyz");
    }

    public static void getCombination(int idx, String str, StringBuffer currentCombination, List<String> combinations) {
        if (idx == str.length()) {
            String combination = currentCombination.toString();
            combinations.add(combination);
            return;
        }

        Character currentChar = str.charAt(idx);
        if(!mnemonics.containsKey(currentChar)) {
            generateCombination(currentCombination, currentChar, str, idx, combinations);
        } else {
            String mnemonic = mnemonics.get(currentChar);
            for (Character mnemonicChar : mnemonic.toCharArray()) {
                generateCombination(currentCombination, mnemonicChar, str, idx, combinations);
            }
        }
    }

    public static void generateCombination(StringBuffer currentCombination, Character currentChar, String str, int idx, List<String> combinations) {
        currentCombination.append(currentChar);
        getCombination(idx + 1, str, currentCombination, combinations);
        currentCombination.deleteCharAt(currentCombination.length() - 1);
    }
}
