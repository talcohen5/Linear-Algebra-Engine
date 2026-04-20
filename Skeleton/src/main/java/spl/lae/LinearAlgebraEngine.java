package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        executor = new TiredExecutor(numThreads);

    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        try{
             computationRoot.associativeNesting();
            while (computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
                ComputationNode node = computationRoot.findResolvable();
                if(node == null) {
                    throw new IllegalStateException("No resolvable nodes found, but computation not complete.");
                }
                loadAndCompute(node);
            }
           
        }finally {
            try {
                executor.shutdown();
            } catch (InterruptedException e) {
               
            }
        }
       
        
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        ComputationNode leftChild ;
        ComputationNode rightChild ;
    
        switch (node.getNodeType()) {
            case ADD:
                if(node.getChildren().size() != 2) {
                    throw new IllegalArgumentException("Node mast have two children");
                }

                leftChild = node.getChildren().get(0);
                rightChild = node.getChildren().get(1);
                leftMatrix.loadRowMajor(leftChild.getMatrix());
                rightMatrix.loadRowMajor(rightChild.getMatrix());
                if(leftMatrix.length() != rightMatrix.length() ||leftMatrix.get(0).length() != rightMatrix.get(0).length()) {
                    throw new IllegalArgumentException("Matrices must have the same non-zero number of rows");
                }
                executor.submitAll(createAddTasks());
                break;
            case MULTIPLY:
                if(node.getChildren().size() != 2) {
                    throw new IllegalArgumentException("Node mast have two children");
                }
                leftChild = node.getChildren().get(0);
                rightChild = node.getChildren().get(1);
                leftMatrix.loadRowMajor(leftChild.getMatrix());
                rightMatrix.loadColumnMajor(rightChild.getMatrix());
                if(leftMatrix.length() != rightMatrix.length()) {
                    throw new IllegalArgumentException("Matrices dimensions mismatch for multiplication");
                }
                executor.submitAll(createMultiplyTasks());
                break;
            case NEGATE:
                if(node.getChildren().size() != 1) {
                    throw new IllegalArgumentException("Node mast have one child");}
                leftChild = node.getChildren().get(0);
                leftMatrix.loadRowMajor(leftChild.getMatrix());
                executor.submitAll(createNegateTasks());
                break;
                
            case TRANSPOSE:
                if(node.getChildren().size() != 1) {
                        throw new IllegalArgumentException("Node mast have one child");}
                leftChild = node.getChildren().get(0);
                leftMatrix.loadRowMajor(leftChild.getMatrix());
                executor.submitAll(createTransposeTasks());
                break;
               
            default:
                throw new IllegalArgumentException("Unknown operator: " + node.getNodeType());
        }
        node.resolve(leftMatrix.readRowMajor());
        
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        if(leftMatrix.length() != rightMatrix.length() || leftMatrix.get(0).length() != rightMatrix.get(0).length()) {
            throw new IllegalArgumentException("Matrices must have the same non-zero number of rows");
        }
         List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < leftMatrix.length(); i++) {
            int row = i;
            tasks.add(() -> {
                leftMatrix.get(row).add(rightMatrix.get(row));
            });
        }
        return tasks;
       
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        int leftCols = leftMatrix.get(0).length();  
        int rightRows = rightMatrix.get(0).length(); 

        if (leftCols != rightRows) {
            throw new IllegalArgumentException(
                "Dimensions mismatch: left columns (" + leftCols +
                ") must equal right rows (" + rightRows + ")"
            );
        }
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            int row = i;
            tasks.add(() -> {
                leftMatrix.get(row).vecMatMul(rightMatrix);
            });
        }
        return tasks;
        
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            int row = i;
            tasks.add(() -> {
                leftMatrix.get(row).negate();
            });
        }
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            int row = i;
            tasks.add(() -> {
                leftMatrix.get(row).transpose();
            });
        }
        return tasks;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}
