package org.digitalcrafting.javaPlayground.algo;

import org.digitalcrafting.javaPlayground.utils.LoggingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinCharactersForWords {
    public static void main(String[] args) {
        LoggingUtils.log(minimumCharactersForWords(new String[] {"this", "that", "did", "deed", "them!", "a"}));
    }

    /* TODO Think of better space/time complexity */
    public static char[] minimumCharactersForWords(String[] words) {
        Map<Character, Integer> availableChars = new HashMap<>();

        for (String word : words) {
            Map<Character, Integer> requiredCharacters = new HashMap<>();
            for (Character c : word.toCharArray()) {
                requiredCharacters.putIfAbsent(c, 0);
                int amount = requiredCharacters.get(c);
                requiredCharacters.put(c, amount + 1);
            }

            for (Map.Entry<Character, Integer> entry : requiredCharacters.entrySet()) {
                availableChars.putIfAbsent(entry.getKey(), 0);
                Integer available = availableChars.get(entry.getKey());
                Integer required = requiredCharacters.get(entry.getKey());

                if (required > available) {
                    availableChars.put(entry.getKey(), required);
                }
            }
        }

        List<Character> charsList = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : availableChars.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                charsList.add(entry.getKey());
            }
        }

        return toArray(charsList);
    }

    public static char[] toArray(List<Character> list){
        char[] toReturn = new char[list.size()];
        int i = 0;
        for(char c : list)
            toReturn[i ++] = c;
        return toReturn;
    }
}
