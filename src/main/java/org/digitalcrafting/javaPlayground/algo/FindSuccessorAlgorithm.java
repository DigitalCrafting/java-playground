package org.digitalcrafting.javaPlayground.algo;

import java.util.ArrayList;
import java.util.List;

/* Successor to the "node" in BT is a node, that's next to visit using inOrderTraversal */
public class FindSuccessorAlgorithm {
    public static void main(String[] args) {
        BinaryTree bt_1 = new BinaryTree(1);
        BinaryTree bt_2 = new BinaryTree(2);
        BinaryTree bt_3 = new BinaryTree(3);
        BinaryTree bt_4 = new BinaryTree(4);
        BinaryTree bt_5 = new BinaryTree(5);
        BinaryTree bt_6 = new BinaryTree(6);

        bt_1.left = bt_2;
        bt_1.right = bt_3;

        bt_2.left = bt_4;
        bt_2.right = bt_5;
        bt_2.parent = bt_1;

        bt_3.parent = bt_1;

        bt_4.left = bt_6;
        bt_4.parent = bt_2;

        bt_5.parent = bt_2;

        bt_6.parent = bt_4;

        System.out.println(findSuccessor(bt_1, bt_5));
    }

    public static BinaryTree findSuccessor(BinaryTree tree, BinaryTree node) {
        List<BinaryTree> inOrder = new ArrayList<>();
        traverse(tree, inOrder);
        int i = inOrder.indexOf(node);
        if (i == inOrder.size() - 1) {
            return null;
        }
        return inOrder.get(i + 1);
    }

    private static void traverse(BinaryTree node, List<BinaryTree> inOrder) {
        if (node == null)
            return;

        traverse(node.left, inOrder);
        inOrder.add(node);
        traverse(node.right, inOrder);
    }

    static class BinaryTree {
        public int value;
        public BinaryTree left = null;
        public BinaryTree right = null;
        public BinaryTree parent = null;

        public BinaryTree(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "BinaryTree{" +
                    "value=" + value +
                    ", left=" + (left != null ? left.value + ""  : "none") +
                    ", right=" + (right != null ? right.value + ""  : "none") +
                    ", parent=" + (parent != null ? parent.value + ""  : "none") +
                    '}';
        }
    }
}
