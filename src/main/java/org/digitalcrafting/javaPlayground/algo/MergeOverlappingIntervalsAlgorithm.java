package org.digitalcrafting.javaPlayground.algo;

import org.digitalcrafting.javaPlayground.utils.LoggingUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MergeOverlappingIntervalsAlgorithm {
    public static void main(String[] args) {
        LoggingUtils.log(mergeOverlappingIntervals(new int[][]{
                {1, 2},
                {3, 5},
                {4, 7},
                {6, 8},
                {9, 10}
        })); // expected [[1,2],[3,8],[9,10]]

        LoggingUtils.log(mergeOverlappingIntervals(new int[][]{
                {1, 22},
                {-20, 30}
        })); // expected [[-20,30]]


    }

    public static int[][] mergeOverlappingIntervals(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(o -> o[0])); // This way, we ensure that intervals are in order
        List<int[]> merged = new ArrayList<>();

        int i = 0;
        while (i < intervals.length) {
            int increment = 1;
            int[] interval = intervals[i];
            int currentBound = interval[1];

            int[] overlaped = new int[] {interval[0], interval[1]};

            for (int j = i + 1; j < intervals.length; j++) {
                if (intervals[j][0] <= currentBound) {
                    if (currentBound < intervals[j][1]) {
                        currentBound = intervals[j][1];
                    }
                    increment++; // This only works, because we sorted the intervals at the beginning
                }
            }

            if (currentBound != interval[1]) {
                overlaped[1] = currentBound;
            }

            merged.add(overlaped);
            i += increment;
        }

        return merged.toArray(new int[][] {});
    }
}
