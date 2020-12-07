package knock;

import weka.core.converters.CSVLoader;
import weka.core.Instances;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.MLPRegressor;
import weka.classifiers.Classifier;

import weka.classifiers.evaluation.Evaluation;

import weka.classifiers.functions.activation.Softplus;
import weka.classifiers.functions.loss.SquaredError;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class TestWeka {
    public static void main(String[] args) throws Exception{
        CSVLoader loaderCSV = new CSVLoader();
        loaderCSV.setSource(new File("knock\\data.csv"));
        Instances data = loaderCSV.getDataSet(); //train data

        data.setClassIndex(data.numAttributes() - 1);
        //use MultilayerPerceptron to train the data
        MultilayerPerceptron mlp = new MultilayerPerceptron ();
        Classifier cls = mlp;
        cls.buildClassifier(data);
        mlp.setHiddenLayers("15,15,15"); //- 3 hidden layers with 5 units for each layer
        // mlp.setHiddenLayers("3"); // one hidden layer with 3 units
        mlp.setLearningRate(0.3);
        mlp.setTrainingTime(1000000); //100000 epoches

        //use MLPRegressor to train the data
        MLPRegressor mlpr = new MLPRegressor();
        mlpr.setActivationFunction(new Softplus());
        mlpr.setLossFunction(new SquaredError());
        mlpr.buildClassifier(data);

        FileWriter file = new FileWriter("knock\\prediction_weka.txt");
        BufferedWriter write_prediction = new BufferedWriter(file);
        
        for(int i = 0; i < data.numInstances(); i++){
             //predict the value of xor
            double clsLabelMlp = cls.classifyInstance(data.instance(i));
            double clsLabelMlpr = mlpr.classifyInstance(data.instance(i));
            // System.out.println("instance: " + data.instance(i));
            //print out the prediction
            write_prediction.write(clsLabelMlp + ",");
            write_prediction.write(clsLabelMlpr + "");
            write_prediction.newLine();
            // System.out.println("prediction: " + clsLabel + " actual: " + data.instance(i).toDoubleArray()[data.numAttributes() - 1]);
            
        }

        System.out.println("Evaluation of mlp");
        System.out.println("One hidden layer with " + mlp.getHiddenLayers() + " units");
        System.out.println("Num epoch: " + mlp.getTrainingTime());
        write_prediction.close();

        Evaluation evalmlp = new Evaluation(data);
        evalmlp.evaluateModel(cls, data);
        System.out.println("Multilayer Perceptron Evaluation: ");
        System.out.println(evalmlp.toSummaryString(true));

        //print out the root mean squared error only
        System.out.println("Root mean square error: " + evalmlp.rootMeanSquaredError());



        System.out.println("Evaluation of mlpr");
        Evaluation evalmlpr = new Evaluation(data);
        evalmlpr.evaluateModel(mlpr, data);
        System.out.println("MLPRegressor: ");
        System.out.println(evalmlpr.toSummaryString(true));

    }
}