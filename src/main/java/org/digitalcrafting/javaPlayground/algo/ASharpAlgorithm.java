package org.digitalcrafting.javaPlayground.algo;

import org.digitalcrafting.javaPlayground.utils.LoggingUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ASharpAlgorithm {
    public static void main(String[] args) {
//        LoggingUtils.log(aStarAlgorithm(0, 1, 4, 3, new int[][]{
//                {0, 0, 0, 0, 0},
//                {0, 1, 1, 1, 0},
//                {0, 0, 0, 0, 0},
//                {1, 0, 1, 1, 1},
//                {0, 0, 0, 0, 0}
//        }));
//
        LoggingUtils.log(aStarAlgorithm(1, 1, 8, 8, new int[][]{
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 0, 1, 1, 1, 1, 1, 0, 1},
                {1, 1, 0, 0, 0, 1, 0, 1, 0, 1},
                {1, 1, 0, 0, 0, 1, 0, 1, 0, 1},
                {1, 1, 0, 0, 0, 1, 0, 1, 0, 1},
                {1, 1, 0, 0, 0, 1, 0, 1, 0, 1},
                {1, 1, 0, 0, 0, 1, 0, 1, 0, 1},
                {1, 1, 1, 1, 1, 1, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        }));


    }


    /* Assumptions:
     *  - start and end are valid
     *  - we cannot go diagonally
     *  - there is 1 or 0 shortest paths
     */
    public static int[][] aStarAlgorithm(int startRow, int startCol, int endRow, int endCol, int[][] graph) {
        Map<String, Node> nodeMap = new HashMap<>();
        Node start = new Node(startRow, startCol);
        Node end = new Node(endRow, endCol);

        Queue<Node> nodesAvailableForVisit = new PriorityQueue<>();

        start.h = getHeuristic(start, end);
        start.g = 0;
        start.recalculateF();

        nodesAvailableForVisit.add(start);

        Node current;
        while (!nodesAvailableForVisit.isEmpty()) {
            current = nodesAvailableForVisit.poll();
            if (current.h == 0) {
                Stack<Node> pointStack = new Stack<>();
                while (current != null) {
                    pointStack.push(current);
                    current = current.from;
                }

                List<Node> path = new ArrayList<>();
                while (!pointStack.isEmpty()) {
                    path.add(pointStack.pop());
                }

                return path.stream().map(point -> new int[]{point.row, point.col}).collect(Collectors.toList()).toArray(new int[][]{});
            }

            List<Node> viableNeighbours = getNeighbours(current, graph);

            if (viableNeighbours.isEmpty()) {
                continue;
            }

            for (Node neighbour : viableNeighbours) {
                Node neighbourNode;
                if (nodeMap.containsKey(neighbour.id)) {
                    neighbourNode = nodeMap.get(neighbour.id);
                    if (neighbourNode.visited) {
                        continue;
                    }

                    if (current.g + 1 < neighbourNode.g) {
                        neighbourNode.g = current.g + 1;
                        neighbourNode.recalculateF();
                        neighbourNode.from = current;
                    }
                } else {
                    neighbourNode = new Node(neighbour.row, neighbour.col);
                    neighbourNode.g = current.g + 1;
                    neighbourNode.h = getHeuristic(neighbour, end);
                    neighbourNode.recalculateF();
                    neighbourNode.from = current;
                }
                nodesAvailableForVisit.add(neighbourNode);
                nodeMap.put(neighbour.id, neighbourNode);
                current.visited = true;
            }
        }

        return new int[][]{};
    }

    private static List<Node> getNeighbours(Node current, int[][] graph) {
        List<Node> neighbours = new ArrayList<>();

        // UP
        if (current.row - 1 >= 0 && graph[current.row - 1][current.col] == 0) {
            neighbours.add(new Node(current.row - 1, current.col));
        }

        // RIGHT
        if (current.col + 1 <= graph[0].length - 1 && graph[current.row][current.col + 1] == 0) {
            neighbours.add(new Node(current.row, current.col + 1));
        }

        // DOWN
        if (current.row + 1 <= graph.length - 1 && graph[current.row + 1][current.col] == 0) {
            neighbours.add(new Node(current.row + 1, current.col));
        }

        // LEFT
        if (current.col - 1 >= 0 && graph[current.row][current.col - 1] == 0) {
            neighbours.add(new Node(current.row, current.col - 1));
        }

        return neighbours;
    }

    private static int getHeuristic(Node start, Node end) {
        return Math.abs(end.row - start.row) + Math.abs(end.col - start.col);
    }

    static class Node implements Comparable<Node> {
        public int row;
        public int col;
        public String id;

        public int g;
        public int h;
        public int f;
        public boolean visited = false;
        public Node from;

        public Node(int row, int col) {
            this.row = row;
            this.col = col;
            this.id = row + "," + col;
        }

        public void recalculateF() {
            this.f = this.g + this.h;
        }

        @Override
        public int compareTo(Node o) {
            return f - o.f;
        }
    }
}
