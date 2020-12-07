package util;

import players.ParamList;
import java.util.ArrayList;

import java.util.stream.DoubleStream;
/**
 * Just a little class for practicing working with streams.
 * 
 * @author Steven Bogaerts
 */
public class StreamPrac {

    public static double compute(double x, int a, int b) {
        return x + a + b;
    }

    public static double getFirstParamValue(ParamList params, int a, int b) {
        return params.get(0) + a + b;
    }

    public static void main(String[] args) {
        // Make array of doubles
        double[] result = DoubleStream.iterate(0, i -> i+0.02).limit(51).toArray();
        System.out.println("Inputs: " + java.util.Arrays.toString(result) + "\n");

        result = DoubleStream.iterate(0, i -> i+0.02)
                             .limit(51)
                             .parallel()
                             .map((x) -> {return compute(x, 2, 3);})
                             .toArray();
        System.out.println("Outputs: " + java.util.Arrays.toString(result) + "\n");

        ArrayList<ParamList> allParamList = new ArrayList<ParamList>();

        ///////////////////////////////////////////////////////////////////////
        System.out.println("----------\nInputs: ");
        int paramListCount = 1000;
        for(int i = 0; i < paramListCount; i++) {
            allParamList.add(new ParamList(new double[] {}));
            allParamList.get(i).set(0, (double) i / paramListCount);
            System.out.print(allParamList.get(i).get(0) + " ");
        }
        System.out.println();

        result = allParamList.stream()
                             .parallel()
                             .map((x) -> {return getFirstParamValue(x, 2, 3);})
                             .mapToDouble(x -> {return x;})
                             .toArray();
        System.out.println("Outputs: " + java.util.Arrays.toString(result) + "\n");

        // ArrayList<ArrayList<Double>> paramALList = new ArrayList<>();
        //System.out.println(paramALList.stream().parallel().map((x) -> playGame(x, gamesPerIndividual)).getClass());
    }

}