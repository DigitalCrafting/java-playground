package org.digitalcrafting.javaPlayground.algo;

import java.util.HashMap;
import java.util.Map;

/* Simple algorithm, to check if I can construct a 'document' out of provided 'characters' */
public class CanGenerateDocumentAlgorithm {
    public static void main(String[] args) {
        System.out.println(generateDocument("aheaollabbhb", "hello"));
        System.out.println(generateDocument("aheaolabbhb", "hello"));
    }

    public static boolean generateDocument(String characters, String document) {
        if (characters == null || document == null) {
            return false;
        }

        if ("".equals(document)) {
            return true;
        } else {
            if ("".equals(characters)) {
                return false;
            }
        }

        /*
        * For optimal space complexity, we only store 1 set of characters in additional memory (additional to already provided strings).
        * We store how many of each unique character we have to construct the document.
        */
        Map<Character, Integer> uniqueProvidedCharacters = new HashMap<>();

        for (Character c : characters.toCharArray()) {
            if (uniqueProvidedCharacters.containsKey(c)) {
                int nmbr = uniqueProvidedCharacters.get(c);
                uniqueProvidedCharacters.put(c, nmbr + 1);
            } else {
                uniqueProvidedCharacters.put(c, 1);
            }
        }

        /*
        * To check if we can construct a document:
        * 1. We iterate over characters in the document,
        * 2. If the character is not in the provided chars, return false.
        * 3. If the character is provided, we 'reserve' it by decreasing the available amount, once the amount reaches 0,
        *    we completely remove it, so that next time, we will hit step #2.
        * 4. If we finish checking required characters, and never hit #2, then we can safely construct the document.
        */
        for (Character c : document.toCharArray()) {
            if (!uniqueProvidedCharacters.containsKey(c)) {
                return false;
            } else {
                int nmbr = uniqueProvidedCharacters.get(c);
                nmbr--;

                if (nmbr == 0) {
                    uniqueProvidedCharacters.remove(c);
                }
            }
        }


        return true;
    }
}
