package knock;

import org.tensorflow.DataType;
// import org.tensorflow.*;
import org.tensorflow.Graph;

import org.tensorflow.GraphOperation;
import org.tensorflow.Operation;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.SavedModelBundle;

import java.util.Scanner;
import java.io.File;

public class TestTensorFlow{
    public static void main(String[] args) throws Exception {
        Graph graph = new Graph();

        //load graph with operation

        //Add a const to our graph
        Operation x = graph.opBuilder("Const", "x")  //return OperationBuilder object
                      .setAttr("dtype", DataType.FLOAT) //data type
                      .setAttr("value", Tensor.create(3.0f)) //value
                      .build(); //OperationBuilder object adds the operations to our graph

        // System.out.println(x);
        //Placeholders: variable that dont have value at declaration - will be assigned later
        //Build graph without actual data
        Operation y = graph.opBuilder("Placeholder", "y")
                      .setAttr("dtype", DataType.FLOAT) 
                      .build();
        


      //Function - from + - * / to matrix multiplication
        Operation xy = graph.opBuilder("Mul", "xy")
                      .addInput(x.output(0))
                      .addInput(y.output(0)) //tensor can has more than 1 input
                      .build();
      
      //No graph visualization available


      //Sessions - driver for Graph's execution. Ecapsulate environment so Operation and Graph -> Tensor
      Session session = new Session(graph);

      //Calculation - multiply x y
      Tensor tensor = session.runner().fetch("xy").feed("x", Tensor.create(5.0f)).feed("y", Tensor.create(4.0f)).run().get(0);
      //fetch the xy operation
      //feed it the x and y values
      System.out.println("Here");
      System.out.println(tensor.floatValue());


      //read model into Java
      SavedModelBundle model = SavedModelBundle.load("knock\\model", "serve");
      // Tensor tensor2 = model.session().runner().run().get(0);
      System.out.println(model);

      }
}