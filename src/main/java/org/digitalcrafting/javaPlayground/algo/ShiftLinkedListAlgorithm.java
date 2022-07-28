package org.digitalcrafting.javaPlayground.algo;

import org.digitalcrafting.javaPlayground.utils.LoggingUtils;

public class ShiftLinkedListAlgorithm {
    public static void main(String[] args) {
        LinkedList el_5 = new LinkedList(5);
        LinkedList el_4 = new LinkedList(4);
        el_4.next = el_5;
        LinkedList el_3 = new LinkedList(3);
        el_3.next = el_4;
        LinkedList el_2 = new LinkedList(2);
        el_2.next = el_3;
        LinkedList el_1 = new LinkedList(1);
        el_1.next = el_2;
        LinkedList el_0 = new LinkedList(0);
        el_0.next = el_1;
        LoggingUtils.log(shiftLinkedList(el_0, 6));
    }

    public static LinkedList shiftLinkedList(LinkedList head, int k) {
        LinkedList previous = head;
        LinkedList current = head;

        LinkedList tail = getTail(head);
        LinkedList newHead = head;

        if (k > 0) {
            for (int i = 0; i < k; i++) {
                if (current.next == null) {
                    current = head;
                } else {
                    current = current.next;
                }
            }

            while (current.next != null) {
                previous = previous.next;
                current = current.next;
            }
        }

        if (k < 0) {
            current = previous.next;
            for (int i = 0; i > k + 1; i--) {
                if (current.next == null) {
                    current = head;
                } else {
                    current = current.next;
                }

                if (previous.next == null) {
                    previous = head;
                } else {
                    previous = previous.next;
                }
            }
        }

        if (k != 0 && previous.next != null) {
            newHead = previous.next;
            tail.next = head;
            previous.next = null;
        }

        return newHead;
    }

    private static LinkedList getTail(LinkedList head) {
        LinkedList current = head;
        while (current.next != null) {
            current = current.next;
        }
        return current;
    }

    static class LinkedList {
        public int value;
        public LinkedList next;

        public LinkedList(int value) {
            this.value = value;
            next = null;
        }
    }
}
