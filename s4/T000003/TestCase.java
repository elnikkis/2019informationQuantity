package s4.T000003; // Please modify to s4.Bnnnnnn, where nnnnnn is your student ID. 
import java.lang.*;
import s4.specification.*;


public class TestCase {
    private static int testCount = 0;

    /** Test and print message.
     *
     * @param expr : boolean value for assertion
     * @param message : Message printed when failed
     */
    static void assertTrue(boolean expr, String message) {
        testCount++;
        System.out.printf("Test%2d ", testCount);
        if(!expr) {
            System.out.println("Failed: " + message);
        }
        else {
            System.out.println("Passed: " + message);
        }
    }

    /** Test for Frequencer.frequency()
     *
     * */
    private static void testFrequencer_frequency() {
        {
            // basic test
            FrequencerInterface frequencer = new Frequencer();
            frequencer.setSpace("Hi Ho Hi Ho".getBytes());
            frequencer.setTarget("H".getBytes());
            int count = frequencer.frequency();
            assertTrue(count == 4, "H appears 4 times");
        }
        // Specification test
        {
            FrequencerInterface frequencer = new Frequencer();
            int count = frequencer.frequency();
            assertTrue(count == -1, "Should returns -1 when Target is not set");
        }
        {
            FrequencerInterface frequencer = new Frequencer();
            frequencer.setTarget("".getBytes());
            int count = frequencer.frequency();
            assertTrue(count == -1, "Should returns -1 when Target length is zero");
        }
        {
            FrequencerInterface frequencer = new Frequencer();
            frequencer.setTarget("hello".getBytes());
            int count = frequencer.frequency();
            assertTrue(count == 0, "Should returns 0 when Space is not set");
        }
        {
            FrequencerInterface frequencer = new Frequencer();
            frequencer.setTarget("hello".getBytes());
            frequencer.setSpace("".getBytes());
            int count = frequencer.frequency();
            assertTrue(count == 0, "Should returns 0 when Space length is zero");
        }
        {
            // overlap
            FrequencerInterface frequencer = new Frequencer();
            frequencer.setSpace("aaaaaa".getBytes());
            frequencer.setTarget("aaa".getBytes());
            int count = frequencer.frequency();
            assertTrue(count == 4, "overlap frequency == 4, got " + count);
        }
        {
            // same
            FrequencerInterface frequencer = new Frequencer();
            frequencer.setSpace("abcde".getBytes());
            frequencer.setTarget("abcde".getBytes());
            int count = frequencer.frequency();
            assertTrue(count == 1, "same frequency == 1, got " + count);
        }
    }
    private static void testFrequencer_subByteFrequency() {
        {
            FrequencerInterface frequencer = new Frequencer();
            frequencer.setSpace("abcabcabca".getBytes());
            frequencer.setTarget("abc".getBytes());
            int count = frequencer.subByteFrequency(0, 3);
            assertTrue(count == 3, "subbyte all, got " + count);
            count = frequencer.subByteFrequency(0, 2);
            assertTrue(count == 3, "subbyte 2, got " + count);
            count = frequencer.subByteFrequency(0, 1);
            assertTrue(count == 4, "subbyte 1, got " + count);
        }
    }

    /** Test for InformationEstimator.estimation()
     *
     * */
    private static void testInformationEstimator_estimation() {
        // Use block{} to define scope for each test
        {
            InformationEstimatorInterface estimator = new InformationEstimator();
            estimator.setSpace("3210321001230123".getBytes());

            estimator.setTarget("0".getBytes());
            double value = estimator.estimation();
            System.out.println(">0 "+value);
        }
    }

    public static void main(String[] args) {
        try {
            FrequencerInterface  myObject;
            System.out.println("checking s4.T000003.Frequencer");
            myObject = new s4.T000003.Frequencer();
            myObject.setSpace("Hi Ho Hi Ho".getBytes());
            myObject.setTarget("H".getBytes());
            int freq = myObject.frequency();
            System.out.print("\"H\" in \"Hi Ho Hi Ho\" appears "+freq+" times. ");
            if(4 == freq) { System.out.println("OK"); } else {System.out.println("WRONG"); }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred: STOP");
        }

        try {
            InformationEstimatorInterface myObject;
            double value;
            System.out.println("checking s4.T000003.InformationEstimator");
            myObject = new s4.T000003.InformationEstimator();
            myObject.setSpace("3210321001230123".getBytes());
            myObject.setTarget("0".getBytes());
            value = myObject.estimation();
            System.out.println(">0 "+value);
            myObject.setTarget("01".getBytes());
            value = myObject.estimation();
            System.out.println(">01 "+value);
            myObject.setTarget("0123".getBytes());
            value = myObject.estimation();
            System.out.println(">0123 "+value);
            myObject.setTarget("00".getBytes());
            value = myObject.estimation();
            System.out.println(">00 "+value);
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred: STOP");
        }

        try {
            // Call test methods
            testFrequencer_frequency();
            testFrequencer_subByteFrequency();
            //testInformationEstimator_estimation();
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred: STOP");
        }
    }
}
