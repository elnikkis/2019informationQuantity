package s4.T000003; // Please modify to s4.Bnnnnnn, where nnnnnn is your student ID.
import java.lang.*;
import java.util.Arrays;
import s4.specification.*;


class NaiveInformationEstimator implements InformationEstimatorInterface {
    byte[] myTarget;
    byte[] mySpace;
    FrequencerInterface myFrequencer;
    boolean targetReady = false;
    boolean spaceReady = false;

    @Override
    public void setTarget(byte[] target) {
        myTarget = target;
        if (myTarget.length > 0) {
            targetReady = true;
        }
        if (myFrequencer == null) {
            myFrequencer = new Frequencer();
        }
        myFrequencer.setTarget(target);
    }
    @Override
    public void setSpace(byte[] space) {
        mySpace = space;
        if (mySpace.length > 0) {
            spaceReady = true;
        }
        if (myFrequencer == null) {
            myFrequencer = new Frequencer();
        }
        myFrequencer.setSpace(space);
    }

    @Override
    public double estimation() {
        if (!targetReady) {
            return 0;
        }
        if (!spaceReady) {
            return Double.MAX_VALUE;
        }

        boolean[] partition = new boolean[myTarget.length+1];
        int np = 1<<(myTarget.length-1);
        // System.out.println("np="+np+" length="+myTarget.length);
        double value = Double.MAX_VALUE; // value = mininimum of each "value1".

        for(int p=0; p<np; p++) { // There are 2^(n-1) kinds of partitions.
            // binary representation of p forms partition.
            // for partition {"ab" "cde" "fg"}
            // a b c d e f g   : myTarget
            // T F T F F T F T : partition:
            partition[0] = true; // I know that this is not needed, but..
            for(int i=0; i<myTarget.length -1;i++) {
                partition[i+1] = (0 !=((1<<i) & p));
            }
            partition[myTarget.length] = true;

            // Compute Information Quantity for the partition, in "value1"
            // value1 = IQ(#"ab")+IQ(#"cde")+IQ(#"fg") for the above example
            double value1 = (double) 0.0;
            int end = 0;
            int start = 0;
            while(start<myTarget.length) {
                // System.out.write(myTarget[end]);
                end++;;
                while(partition[end] == false) {
                    // System.out.write(myTarget[end]);
                    end++;
                }
                // System.out.print("("+start+","+end+")");
                int freq = myFrequencer.subByteFrequency(start, end);
                value1 += iq(freq);
                start = end;
            }
            // System.out.println(" "+ value1);

            // Get the minimal value in "value"
            if(value1 < value) value = value1;
        }
        return value;
    }

    // IQ: information quantity for a count, -log2(count/sizeof(space))
    double iq(int freq) {
        return  - Math.log10((double) freq / (double) mySpace.length) / Math.log10((double) 2.0);
    }

}


public class InformationEstimator implements InformationEstimatorInterface {
    // Code to test, *warning: This code contains intentional problem*
    byte[] myTarget; // data to compute its information quantity
    byte[] mySpace;  // Sample space to compute the probability
    FrequencerInterface myFrequencer;  // Object for counting frequency
    boolean targetReady = false;
    boolean spaceReady = false;

    double[] memoIqs;

    // IQ: information quantity for a count, -log2(count/sizeof(space))
    double iq(int freq) {
        return  - Math.log10((double) freq / (double) mySpace.length) / Math.log10((double) 2.0);
    }

    /** Information quantity of a subbyte of target (target[:end])*/
    double iqs(int end) {
        // endはtarget.lengthが1以上のときにしか呼ばれないことが保証されている
        //System.out.printf("iqs end=%d, memoIqs=%f\n", end, memoIqs[end-1]);
        if (memoIqs[end-1] != -1) {
            return memoIqs[end-1];
        }
        double result = iq(myFrequencer.subByteFrequency(0, end));
        //for (int p=myTarget.length-1; p>=0; p--) {
        for (int p=end-1; p>0; p--) {
        //for (int p=1; p<myTarget.length; p++) {
            // I(target[0:p])
            double value1 = iqs(p);
            // f(target[p:end])
            int freq = myFrequencer.subByteFrequency(p, end);
            double value2 = iq(freq);

            double value = value1 + value2;
            // Get minimum value
            if (result > value) {
                result = value;
            }
            //System.out.printf("%f + %f = %f\n", value1, value2, value);
        }
        memoIqs[end-1] = result;
        return result;
    }

    void prepareMemo() {
        if (!targetReady || !spaceReady) {
            return;
        }
        // Initialize memoIqs
        memoIqs = new double[myTarget.length];
        Arrays.fill(memoIqs, -1);
        memoIqs[0] = iq(myFrequencer.subByteFrequency(0, 1));
    }

    @Override
    public void setTarget(byte[] target) {
        myTarget = target;
        if (myTarget.length > 0) {
            targetReady = true;
        }
        if (myFrequencer == null) {
            myFrequencer = new Frequencer();
        }
        myFrequencer.setTarget(target);
        prepareMemo();
    }

    @Override
    public void setSpace(byte[] space) {
        mySpace = space;
        if (mySpace.length > 0) {
            spaceReady = true;
        }
        if (myFrequencer == null) {
            myFrequencer = new Frequencer();
        }
        myFrequencer.setSpace(space);
        prepareMemo();
    }

    @Override
    public double estimation(){
        if (!targetReady) {
            return 0;
        }
        if (!spaceReady) {
            return Double.MAX_VALUE;
        }
        return iqs(myTarget.length);
        /*
        boolean[] partition = new boolean[myTarget.length+1];
        int np = 1<<(myTarget.length-1);
        // System.out.println("np="+np+" length="+myTarget.length);
        double value = Double.MAX_VALUE; // value = mininimum of each "value1".

        for(int p=0; p<np; p++) { // There are 2^(n-1) kinds of partitions.
            // binary representation of p forms partition.
            // for partition {"ab" "cde" "fg"}
            // a b c d e f g   : myTarget
            // T F T F F T F T : partition:
            partition[0] = true; // I know that this is not needed, but..
            for(int i=0; i<myTarget.length -1;i++) {
                partition[i+1] = (0 !=((1<<i) & p));
            }
            partition[myTarget.length] = true;

            // Compute Information Quantity for the partition, in "value1"
            // value1 = IQ(#"ab")+IQ(#"cde")+IQ(#"fg") for the above example
            double value1 = (double) 0.0;
            int end = 0;
            int start = 0;
            while(start<myTarget.length) {
                // System.out.write(myTarget[end]);
                end++;;
                while(partition[end] == false) {
                    // System.out.write(myTarget[end]);
                    end++;
                }
                // System.out.print("("+start+","+end+")");
                int freq = myFrequencer.subByteFrequency(start, end);
                value1 += iq(freq);
                start = end;
            }
            // System.out.println(" "+ value1);

            // Get the minimal value in "value"
            if(value1 < value) value = value1;
        }
        return value;
        */
    }

    public static void main(String[] args) {
        InformationEstimatorInterface naive = new NaiveInformationEstimator();
        InformationEstimatorInterface pro = new InformationEstimator();
        naive.setSpace("3210321001230123".getBytes());
        pro.setSpace("3210321001230123".getBytes());

        {
            naive.setTarget("0".getBytes());
            pro.setTarget("0".getBytes());
            double v0 = naive.estimation();
            double v1 = pro.estimation();
            System.out.printf(">0 naive: %f, pro: %f, %s\n", v0, v1, v0 == v1);
        }
        {
            naive.setTarget("01".getBytes());
            pro.setTarget("01".getBytes());
            double v0 = naive.estimation();
            double v1 = pro.estimation();
            System.out.printf(">01 naive: %f, pro: %f, %s\n", v0, v1, v0 == v1);
        }
        {
            naive.setTarget("0123".getBytes());
            pro.setTarget("0123".getBytes());
            double v0 = naive.estimation();
            double v1 = pro.estimation();
            System.out.printf(">0123 naive: %f, pro: %f, %s\n", v0, v1, v0 == v1);
        }
        {
            naive.setTarget("00".getBytes());
            pro.setTarget("00".getBytes());
            double v0 = naive.estimation();
            double v1 = pro.estimation();
            System.out.printf(">00 naive: %f, pro: %f, %s\n", v0, v1, v0 == v1);
        }
    }
}

