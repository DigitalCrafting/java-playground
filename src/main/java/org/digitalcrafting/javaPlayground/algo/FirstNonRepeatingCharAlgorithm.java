package org.digitalcrafting.javaPlayground.algo;

import java.util.HashMap;
import java.util.Map;

public class FirstNonRepeatingCharAlgorithm {
    public static void main(String[] args) {
        System.out.println(firstNonRepeatingCharacter("abcdcaf"));
        System.out.println(firstNonRepeatingCharacter("faadabcbbebdf"));
        System.out.println(firstNonRepeatingCharacter("a"));
        System.out.println(firstNonRepeatingCharacter(""));
    }

    public static int firstNonRepeatingCharacter(String string) {
        Map<Character, Integer> counts = new HashMap<>();

        for (Character c : string.toCharArray()) {
            counts.putIfAbsent(c, 0);

            int count = counts.get(c);
            count++;
            counts.put(c, count);
        }

        for (int index = 0; index < string.length(); index++) {
            if (counts.get(string.charAt(index)) == 1) {
                return index;
            }
        }

        return -1;
    }
}
