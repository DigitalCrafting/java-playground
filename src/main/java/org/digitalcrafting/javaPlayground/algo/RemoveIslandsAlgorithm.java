package org.digitalcrafting.javaPlayground.algo;

import org.digitalcrafting.javaPlayground.utils.LoggingUtils;

import java.util.ArrayList;
import java.util.List;

/*
 * Island is defined as any number of vertically/horizontally adjusted 1, which can bend, that are not touching the borders
 *
 */
public class RemoveIslandsAlgorithm {
    public static void main(String[] args) {
        LoggingUtils.log(removeIslands(new int[][]{
                {1, 0, 0, 0, 0, 0},
                {0, 1, 0, 1, 1, 1},
                {0, 0, 1, 0, 1, 0},
                {1, 1, 0, 0, 1, 0},
                {1, 0, 1, 1, 0, 0},
                {1, 0, 0, 0, 0, 1}
        }));
    }

    public static int[][] removeIslands(int[][] matrix) {
        List<String> alreadyChecked = new ArrayList<>();
        List<String> islands = new ArrayList<>();
        List<String> currentLand = new ArrayList<>();


        for (int row = 1; row < matrix.length - 1; row++) {
            for (int col = 1; col < matrix[row].length - 1; col++) {
                if (matrix[row][col] == 0 || alreadyChecked.contains(row + "," + col)) {
                    continue;
                }

                mapLand(matrix, row, col, alreadyChecked, currentLand);

                if (isIsland(currentLand, matrix.length, matrix[row].length)) {
                    islands.addAll(currentLand);
                }

                currentLand.clear();
            }
        }

        for (String island : islands) {
            String[] coords = island.split(",");
            matrix[Integer.parseInt(coords[0])][Integer.parseInt(coords[1])] = 0;
        }

        return matrix;
    }

    private static void mapLand(int[][] matrix, int row, int col, List<String> alreadyChecked, List<String> currentLand) {
        if (row < 0 || row >= matrix.length || col < 0 || col >= matrix[row].length ) {
            return;
        }

        if (matrix[row][col] == 0 || alreadyChecked.contains(row + "," + col)) {
            return;
        }

        currentLand.add(row + "," + col);
        alreadyChecked.add(row + "," + col);

        mapLand(matrix, row, col + 1, alreadyChecked, currentLand);
        mapLand(matrix, row + 1, col, alreadyChecked, currentLand);
        mapLand(matrix, row - 1, col, alreadyChecked, currentLand);
        mapLand(matrix, row, col - 1, alreadyChecked, currentLand);
    }

    private static boolean isIsland(List<String> currentLand, int rows, int cols) {
        for (String s : currentLand) {
            String[] coords = s.split(",");
            if (Integer.parseInt(coords[0]) == 0 || Integer.parseInt(coords[0]) == rows - 1 || Integer.parseInt(coords[1]) == 0 || Integer.parseInt(coords[1]) == cols - 1) {
                return false;
            }
        }

        return true;
    }
}