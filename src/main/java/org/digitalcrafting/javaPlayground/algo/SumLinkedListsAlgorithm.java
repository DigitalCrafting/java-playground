package org.digitalcrafting.javaPlayground.algo;

import org.digitalcrafting.javaPlayground.utils.LoggingUtils;

/*
* Basically, implement addition, but number is represented as linked list, and in reverse order.
*/
public class SumLinkedListsAlgorithm {
    public static void main(String[] args) {
        LinkedList list_1_1 = new LinkedList(2);
        LinkedList list_1_2 = new LinkedList(4);
        LinkedList list_1_3 = new LinkedList(7);
        LinkedList list_1_4 = new LinkedList(1);
        list_1_1.next = list_1_2;
        list_1_2.next = list_1_3;
        list_1_3.next = list_1_4;

        LinkedList list_2_1 = new LinkedList(9);
        LinkedList list_2_2 = new LinkedList(4);
        LinkedList list_2_3 = new LinkedList(5);
        list_2_1.next = list_2_2;
        list_2_2.next = list_2_3;

        LoggingUtils.log(sumOfLinkedLists(list_1_1, list_2_1));
    }

    public static LinkedList sumOfLinkedLists(LinkedList linkedListOne, LinkedList linkedListTwo) {
        LinkedList first = linkedListOne;
        LinkedList second = linkedListTwo;

        int carry = 0;
        LinkedList sum = null;
        LinkedList sumHead = null;

        while (first != null || second != null) {
            int currentSum = 0;
            if (first != null) {
                currentSum += first.value;
                first = first.next;
            }

            if (second != null) {
                currentSum += second.value;
                second = second.next;
            }

            currentSum += carry;

            int newNodeValue = currentSum % 10;
            carry = currentSum / 10;

            if (sum == null) {
                sum = new LinkedList(newNodeValue);
                sumHead = sum;
            } else {
                sum.next = new LinkedList(newNodeValue);
                sum = sum.next;
            }
        }

        if (carry != 0) {
            sum.next = new LinkedList(carry);
        }

        return sumHead;
    }

    public static class LinkedList {
        public int value;
        public LinkedList next;

        public LinkedList(int value) {
            this.value = value;
            this.next = null;
        }
    }
}
