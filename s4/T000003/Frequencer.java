package s4.T000003; // Please modify to s4.Bnnnnnn, where nnnnnn is your student ID. 
import java.lang.*;
import s4.specification.*;


class SimpleFrequencer implements FrequencerInterface {
    // Code to Test, *warning: This code contains intentional problem*
    byte[] myTarget;
    byte[] mySpace;

    @Override
    public void setTarget(byte[] target) {
        myTarget = target;
    }
    @Override
    public void setSpace(byte[] space) {
        mySpace = space;
    }

    @Override
    public int frequency() {
        int targetLength = myTarget.length;
        int spaceLength = mySpace.length;
        int count = 0;
        for(int start = 0; start<spaceLength; start++) { // Is it OK?
            boolean abort = false;
            for(int i = 0; i<targetLength; i++) {
                if(myTarget[i] != mySpace[start+i]) { abort = true; break; }
            }
            if(abort == false) { count++; }
        }
        return count;
    }

    // I know that here is a potential problem in the declaration.
    @Override
    public int subByteFrequency(int start, int length) {
        // Not yet implemented, but it is not currently used by anyone.
        return -1;
    }
}

public class Frequencer implements FrequencerInterface {
    byte[] myTarget;
    byte[] mySpace;
    int[] suffixArray;
    boolean targetReady = false;
    boolean spaceReady = false;

    @Override
    public void setTarget(byte[] target) {
        myTarget = target;
        if (myTarget.length > 0) {
            targetReady = true;
        }
    }
    @Override
    public void setSpace(byte[] space) {
        mySpace = space;
        if (mySpace.length > 0) {
            spaceReady = true;
        }
        makeSuffixArray();
    }

    @Override
    public int frequency() {
        if (!targetReady) {
            return -1;
        }
        return subByteFrequency(0, myTarget.length);
    }

    @Override
    public int subByteFrequency(int start, int end) {
        if (!targetReady) {
            return -1;
        }
        if (!spaceReady) {
            return 0;
        }
        int first = findStartIndex(start, end);
        int last = findEndIndex(start, end);
        //System.out.printf("(%d, %d)\n", first, last);
        return last - first;
    }

    private void printSuffixArray() {
        if (!spaceReady) {
            return;
        }
        for (int i=0; i<mySpace.length; i++) {
            var s = suffixArray[i];
            System.out.printf("suffixArray[%2d]=%2d: ", i, s);
            for(int j=s; j<mySpace.length; j++) {
                System.out.write(mySpace[j]);
            }
            System.out.write('\n');
        }
    }

    // suffixArrayのsuffix_iとsuffix_jとを比べて大小関係を返す
    int compareSuffix(int left, int right) {
        if (left == right) {
            // same suffix
            return 0;
        }
        int idx_left = suffixArray[left];
        int idx_right = suffixArray[right];
        for (int i=0; i<suffixArray.length; i++) {
            // bound check
            if (idx_left + i >= mySpace.length) {
                // right is longer
                return -1;
            }
            else if (idx_right + i >= mySpace.length) {
                // left is longer
                return 1;
            }
            // greater or less
            if (mySpace[idx_left + i] > mySpace[idx_right + i]) {
                return 1;
            }
            else if (mySpace[idx_left + i] < mySpace[idx_right + i]) {
                return -1;
            }
        }
        return 0;
    }

    /** Make a suffix array from mySpace */
    void makeSuffixArray() {
        suffixArray = new int[mySpace.length];
        for (int i=0; i<mySpace.length; i++) {
            suffixArray[i] = i;
        }
        // Sort suffixArray
        /*
        for (int i=0; i<suffixArray.length; i++) {
            for (int j=i+1; j<suffixArray.length; j++) {
                if (compareSuffix(i, j) > 0) {
                    int tmp = suffixArray[i];
                    suffixArray[i] = suffixArray[j];
                    suffixArray[j] = tmp;
                }
            }
        }
        */
        // merge sort
        var tmp = new int[mySpace.length];
        merge_sort(suffixArray, tmp, 0, suffixArray.length);
    }
    void merge_sort(int[] A, int[] B, int left, int right) {
        if (left == right || left == right - 1) {
            return;
        }
        int mid = (left + right) / 2;
        merge_sort(A, B, left, mid);
        merge_sort(A, B, mid, right);
        merge(A, B, left, mid, right);
    }
    void merge(int[] A, int[] B, int left, int mid, int right) {
        int i = left;
        int j = mid;
        int k = 0;
        while(i < mid && j < right) {
            if (compareSuffix(i, j) < 0) {
                B[k++] = A[i++];
            }
            else {
                B[k++] = A[j++];
            }
        }
        if(i == mid) {
            while(j < right) {
                B[k++] = A[j++];
            }
        }
        else {
            while(i < mid) {
                B[k++] = A[i++];
            }
        }
        for(int l=0; l<k; l++) {
            A[left + l] = B[l];
        }
    }

    /** Returns the first index in the suffix array that appears myTarget[start:end] */
    int findStartIndex(int start, int end) {
        int startIdx = binarySearch(0, suffixArray.length, start, end);
        // 見つからなかった
        if (startIdx == suffixArray.length) {
            return startIdx;
        }
        //System.out.printf("start find at %d (%d)\n", startIdx, compareSuffixAndTarget(startIdx, start, end));
        // 0の始まりをみつける
        while (startIdx >= 0 && compareSuffixAndTarget(startIdx, start, end) == 0) {
            startIdx--;
        }
        return startIdx + 1; // 0が起きる場所を示すので+1が必要
    }

    /** Returns the last index + 1 in the suffix array that appears myTarget[start:end] */
    int findEndIndex(int start, int end) {
        int endIdx = binarySearch(0, suffixArray.length, start, end);
        // 見つからなかった
        if (endIdx == suffixArray.length) {
            return endIdx;
        }
        //System.out.printf("end: found at %d (%d)\n", endIdx, compareSuffixAndTarget(endIdx, start, end));
        // 0の終わりをみつける
        while (endIdx < suffixArray.length && compareSuffixAndTarget(endIdx, start, end) == 0) {
            endIdx++;
        }
        return endIdx;
    }

    /*
    int binarySearch(int left, int right, int start, int end) {
        if (left > right || left >= suffixArray.length) {
            // failed to search
            return suffixArray.length;
        }
        int mid = (left + right) / 2;
        int ret = compareSuffixAndTarget(mid, start, end);
        //System.out.printf("left=%d, right=%d, [%d] = %d\n", left, right, mid, ret);
        if (ret == 0) {
            return mid;
        }
        else if (ret < 0) {
            // midより右側
            return binarySearch(mid+1, right, start, end);
        }
        else {
            // midより左側
            return binarySearch(left, mid-1, start, end);
        }
    }
    */

    int binarySearch(int left, int right, final int start, final int end) {
        while (left <= right) {
            int mid = (left + right) / 2;
            if (mid < 0 || mid >= suffixArray.length) {
                break;
            }
            int ret = compareSuffixAndTarget(mid, start, end);
            if (ret < 0) {
                left = mid + 1;
            }
            else if (ret > 0) {
                right = mid - 1;
            }
            else {
                return mid;
            }
        }
        return suffixArray.length;
    }

    /** 文字列suffix: mySpace[suffix_i:]と 文字列subTarget: myTarget[start:end]とを比較して、
     * subTargetがsuffixに部分一致していれば一致(0)を返し、
     * そうでなければ辞書順に比較してsuffixがsubTargetより大きければ1、
     * suffixがsubTargetより小さければ-1を返す。 */
    int compareSuffixAndTarget(int suffix_i, int start, int end) {
        int spaceIdx = suffixArray[suffix_i];
        int suffixLength = mySpace.length - spaceIdx;
        int targetLength = end - start;
        for (int i=0; i<targetLength; i++) {
            //System.out.printf("space[%d]=%s, target[%d]=%s\n", spaceIdx, (char)mySpace[spaceIdx+i], start, (char)myTarget[start+i]);
            // check bound
            if (spaceIdx + i >= mySpace.length) {
                // target is longer than suffix
                return -1;
            }
            // compare
            if (mySpace[spaceIdx + i] > myTarget[start + i]) {
                return 1;
            }
            else if (mySpace[spaceIdx + i] < myTarget[start + i]) {
                return -1;
            }
        }

        if (suffixLength == targetLength) {
            return 0;
        }
        else if (suffixLength > targetLength) {
            //return 1;
            // suffixがtargetより長いが、targetと一致している場合はok
            return 0;
        }
        else {
            // suffixはtargetより短い
            return -1;
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Checking my Frequencer");
            var frequencer = new Frequencer();
            //frequencer.setSpace("Hi Ho Hi Ho".getBytes());
            frequencer.setSpace("abcabcabc".getBytes());
            frequencer.setTarget("abc".getBytes());
            frequencer.printSuffixArray();

            for (int i=0; i<frequencer.mySpace.length; i++) {
                System.out.println("idx=" + i + " res=" + frequencer.compareSuffixAndTarget(i, 0, frequencer.myTarget.length));
            }
            //System.out.println(frequencer.binarySearch(0, 10, 0, 1));

            int freq = frequencer.subByteFrequency(0, 3);
            System.out.print("appears "+freq+" times. ");
            if(4 == freq) { System.out.println("OK"); } else {System.out.println("WRONG"); }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred: STOP");
        }
        /*
        try {
            System.out.println("Checking my Frequencer");
            var frequencer = new Frequencer();
            frequencer.setSpace("apple blue sky apple blue".getBytes());
            frequencer.setTarget("H".getBytes());
            frequencer.printSuffixArray();
            int freq = frequencer.frequency();
            System.out.print("\"H\" in \"Hi Ho Hi Ho\" appears "+freq+" times. ");
            if(4 == freq) { System.out.println("OK"); } else {System.out.println("WRONG"); }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred: STOP");
        }
        */
    }
}

