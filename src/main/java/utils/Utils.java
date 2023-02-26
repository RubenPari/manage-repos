package utils;

import java.io.IOException;

import org.kohsuke.github.GHRepository;

public class Utils {
    public static GHRepository[] quickSort(GHRepository[] arr) throws IOException {
        if (arr == null || arr.length == 0) {
            return arr;
        }
        return quickSort(arr, 0, arr.length - 1);
    }

    private static GHRepository[] quickSort(GHRepository[] arr, int left, int right) throws IOException {
        if (left < right) {
            int pivotIndex = partition(arr, left, right);
            quickSort(arr, left, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, right);
        }
        return arr;
    }

    private static int partition(GHRepository[] arr, int left, int right) throws IOException {
        GHRepository pivot = arr[right];
        int i = left - 1;
        for (int j = left; j < right; j++) {
            if (arr[j].getCreatedAt().compareTo(pivot.getCreatedAt()) > 0) {
                i++;
                GHRepository temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        GHRepository temp = arr[i + 1];
        arr[i + 1] = arr[right];
        arr[right] = temp;
        return i + 1;
    }
}
