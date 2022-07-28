package org.digitalcrafting.javaPlayground.algo;

import org.digitalcrafting.javaPlayground.utils.LoggingUtils;

import java.util.ArrayList;
import java.util.List;

/*
* This algorithm must sort a stack in-place.
* We must treat this array as a stack.
* We can only access the last element of the array.
* We can only push the element to the array.
* We can peek at the last element.
* The algorithm must be recursive.
* We cannot use auxiliary data structures;
*/
public class SortStackAlgorithm {
    public static void main(String[] args) {
        LoggingUtils.log(sortStack(new ArrayList<>(List.of(-5, 2, -2, 4, 3, 1))));
    }

    public static ArrayList<Integer> sortStack(ArrayList<Integer> stack) {
        if (!stack.isEmpty()) {
            Integer lastEl = stack.remove(stack.size() - 1);
            sortStack(stack);
            insert(stack, lastEl);
        }
        return stack;
    }

    public static void insert(ArrayList<Integer> stack, Integer toBeInserted) {
        if (stack.isEmpty() || stack.get(stack.size() - 1) <= toBeInserted) {
            stack.add(toBeInserted);
        } else if (stack.get(stack.size() - 1) > toBeInserted) {
            Integer currentLast = stack.remove(stack.size() - 1);
            insert(stack, toBeInserted);
            stack.add(currentLast);
        }
    }
}
