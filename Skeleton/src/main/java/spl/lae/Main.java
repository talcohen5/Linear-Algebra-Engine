package spl.lae;
import java.io.IOException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {

      if (args.length != 3) {
      System.err.println("Usage: <numThreads> <inputFile> <outputFile>");
      OutputWriter.write("Invalid number of arguments", "output.json");
      return;
      }
      int numThreads;
      String inputPath;
      String outputPath;

      try {
          numThreads = Integer.parseInt(args[0]);
          inputPath = args[1];
          outputPath = args[2];
      } catch (Exception e) {
          System.err.println("Invalid arguments");
          return;
      }
      InputParser parser = new InputParser();
      ComputationNode root;

      try {
          root = parser.parse(inputPath);
      } catch (Exception e) {
          String errorM = "Error parsing input file: " + e.getMessage();
          System.err.println(errorM);
          OutputWriter.write(errorM, outputPath);
          return;
      }
      
      LinearAlgebraEngine lae = new LinearAlgebraEngine(numThreads);
      try {
        ComputationNode resultNode = lae.run(root);
        double[][] result = resultNode.getMatrix();
        OutputWriter.write(result, outputPath);
        System.out.println(lae.getWorkerReport());
      } catch (Exception e) {
        try {
          OutputWriter.write(e.getMessage(), outputPath);
        } catch (Exception ignored) {}
      }

            
    }
}