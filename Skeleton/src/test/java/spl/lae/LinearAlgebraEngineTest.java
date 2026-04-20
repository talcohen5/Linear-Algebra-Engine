package spl.lae;

import memory.*;
import parser.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LinearAlgebraEngineTest {

    LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
    LinearAlgebraEngine lae2 = new LinearAlgebraEngine(50);
    @Test
    void testAddition() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2},{3,4}});
        ComputationNode b = new ComputationNode(new double[][]{{5,6},{7,8}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.ADD, List.of(a,b)
        );

        
        double[][] res = lae.run(root).getMatrix();

        assertArrayEquals(new double[][]{{6,8},{10,12}}, res);
    }

    @Test
    void testNegate() {
        ComputationNode a = new ComputationNode(new double[][]{{1,-2}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.NEGATE, List.of(a)
        );

        double[][] res = lae.run(root).getMatrix();
        assertArrayEquals(new double[][]{{-1,2}}, res);
    }

    @Test
    void testMultiplyDimensionsMismatch() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2}});
        ComputationNode b = new ComputationNode(new double[][]{{1,2}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.MULTIPLY, List.of(a,b)
        );


        assertThrows(IllegalArgumentException.class, () -> lae.run(root));
    }

    @Test
    void testTranspose() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.TRANSPOSE, List.of(a)
        );


        double[][] res = lae.run(root).getMatrix();
        assertArrayEquals(new double[][]{{1},{2}}, res);
    }

    @Test
    void testAddInvalidChildren() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.ADD, List.of(a)
        );

        LinearAlgebraEngine lae = new LinearAlgebraEngine(1);
        assertThrows(IllegalArgumentException.class, () -> lae.run(root));
    }    

    @Test
    void testMultiplyInvalidChildren() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.MULTIPLY, List.of(a)
        );

        LinearAlgebraEngine lae = new LinearAlgebraEngine(1);
        assertThrows(IllegalArgumentException.class, () -> lae.run(root));
    }
    @Test
    void testNegateInvalidChildren() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2}});
        ComputationNode b = new ComputationNode(new double[][]{{3,4}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.NEGATE, List.of(a,b)
        );

        LinearAlgebraEngine lae = new LinearAlgebraEngine(1);
        assertThrows(IllegalArgumentException.class, () -> lae.run(root));
    }

    @Test
    void testTransposeInvalidChildren() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2}});
        ComputationNode b = new ComputationNode(new double[][]{{3,4}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.TRANSPOSE, List.of(a,b)
        );

        LinearAlgebraEngine lae = new LinearAlgebraEngine(1);
        assertThrows(IllegalArgumentException.class, () -> lae.run(root));
    }

    @Test
    void testAddDifferentSizes() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2}});
        ComputationNode b = new ComputationNode(new double[][]{{3,4},{5,6}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.ADD, List.of(a,b)
        );


        assertThrows(IllegalArgumentException.class, () -> lae.run(root));
    }

    @Test
    void testMultiplyValid() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2,3},{4,5,6}});
        ComputationNode b = new ComputationNode(new double[][]{{7,8},{9,10},{11,12}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.MULTIPLY, List.of(a,b)
        );
        double[][] res = lae.run(root).getMatrix();

        assertArrayEquals(new double[][]{{58,64},{139,154}}, res);
    }

    @Test
    void testTransposeSquareMatrix() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2},{3,4}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.TRANSPOSE, List.of(a)
        );

        double[][] res = lae.run(root).getMatrix();
        assertArrayEquals(new double[][]{{1,3},{2,4}}, res);
    }

    @Test
    void testTransposeRectangularMatrix() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2,3},{4,5,6}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.TRANSPOSE, List.of(a)
        );

        double[][] res = lae.run(root).getMatrix();
        assertArrayEquals(new double[][]{{1,4},{2,5},{3,6}}, res);
    }

    @Test
    void testAdditionSingleThreaded() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2},{3,4}});
        ComputationNode b = new ComputationNode(new double[][]{{5,6},{7,8}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.ADD, List.of(a,b)
        );

        double[][] res = lae.run(root).getMatrix();

        assertArrayEquals(new double[][]{{6,8},{10,12}}, res);
    }

    @Test
    void testMultiplySingleThreaded() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2,3},{4,5,6}});
        ComputationNode b = new ComputationNode(new double[][]{{7,8},{9,10},{11,12}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.MULTIPLY, List.of(a,b)
        );

        double[][] res = lae.run(root).getMatrix();

        assertArrayEquals(new double[][]{{58,64},{139,154}}, res);
    }

    @Test
    void testNegateSingleThreaded() {
        ComputationNode a = new ComputationNode(new double[][]{{1,-2}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.NEGATE, List.of(a)
        );

        double[][] res = lae.run(root).getMatrix();
        assertArrayEquals(new double[][]{{-1,2}}, res);
    }
    @Test
    void testTransposeSingleThreaded() {
        ComputationNode a = new ComputationNode(new double[][]{{1,2}});
        ComputationNode root = new ComputationNode(
                ComputationNodeType.TRANSPOSE, List.of(a)
        );

        double[][] res = lae.run(root).getMatrix();
        assertArrayEquals(new double[][]{{1},{2}}, res);
    }

    @Test
    void testAdditionLargeMatrices() {
        int size =50;
        double[][] matA = new double[size][size];
        double[][] matB = new double[size][size];
        double[][] expected = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matA[i][j] = i + j;
                matB[i][j] = (i + j) * 2;
                expected[i][j] = matA[i][j] + matB[i][j];
            }
        }

        ComputationNode a = new ComputationNode(matA);
        ComputationNode b = new ComputationNode(matB);
        ComputationNode root = new ComputationNode(
                ComputationNodeType.ADD, List.of(a,b)
        );


        double[][] res2 = lae2.run(root).getMatrix();

        assertArrayEquals(expected, res2);
    }

    @Test
    void testMultiplyLargeMatrices() {
        int size = 100;
        double[][] matA = new double[size][size];
        double[][] matB = new double[size][size];
        double[][] expected = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matA[i][j] = i + 1;
                matB[i][j] = j + 1;
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                expected[i][j] = 0;
                for (int k = 0; k < size; k++) {
                    expected[i][j] += matA[i][k] * matB[k][j];
                }
            }
        }

        ComputationNode a = new ComputationNode(matA);
        ComputationNode b = new ComputationNode(matB);
        ComputationNode root = new ComputationNode(
                ComputationNodeType.MULTIPLY, List.of(a,b)
        );


        double[][] res = lae2.run(root).getMatrix();

        assertArrayEquals(expected, res);
    }
 
    @Test
    void testAddMultipleChildren() {
        ComputationNode a = new ComputationNode(new double[][]{{1}});
        ComputationNode b = new ComputationNode(new double[][]{{2}});
        ComputationNode c = new ComputationNode(new double[][]{{3}});
        List<ComputationNode> children = new ArrayList<>();
        children.add(a);
        children.add(b);
        children.add(c);
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, children);
        assertArrayEquals(new double[][]{{6}}, lae.run(root).getMatrix());
    }
    
    

}