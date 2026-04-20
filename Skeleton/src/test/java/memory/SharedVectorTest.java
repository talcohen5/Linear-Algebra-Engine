package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SharedVectorTest {

    @Test
    void testGetAndLength() {
        SharedVector v = new SharedVector(new double[]{1,2,3}, VectorOrientation.ROW_MAJOR);
        assertEquals(3, v.length());
        assertEquals(2, v.get(1));
    }

    @Test
    void testAddValid() {
        SharedVector a = new SharedVector(new double[]{1,2}, VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{3,4}, VectorOrientation.ROW_MAJOR);
        a.add(b);
        assertEquals(4, a.get(0));
        assertEquals(6, a.get(1));
    }

    @Test
    void testAddInvalidSize() {
        SharedVector a = new SharedVector(new double[]{1}, VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{1,2}, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> a.add(b));
    }

    @Test
    void testNegate() {
        SharedVector v = new SharedVector(new double[]{1,-2}, VectorOrientation.ROW_MAJOR);
        v.negate();
        assertEquals(-1, v.get(0));
        assertEquals(2, v.get(1));
    }

    @Test
    void testDotProductValid() {
        SharedVector r = new SharedVector(new double[]{1,2}, VectorOrientation.ROW_MAJOR);
        SharedVector c = new SharedVector(new double[]{3,4}, VectorOrientation.COLUMN_MAJOR);
        assertEquals(11, r.dot(c));
    }

    @Test
    void testDotProductSameOrientation() {
        SharedVector a = new SharedVector(new double[]{1}, VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{2}, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> a.dot(b));
    }

    @Test
    void testTranspose() {
        SharedVector v = new SharedVector(new double[]{1,2}, VectorOrientation.ROW_MAJOR);
        v.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());
    }

    @Test
    void testTransposeTwice() {
        SharedVector v = new SharedVector(new double[]{1,2}, VectorOrientation.ROW_MAJOR);
        v.transpose();
        v.transpose();
        assertEquals(VectorOrientation.ROW_MAJOR, v.getOrientation());
    }

    @Test
    void testTransposeEmptyVector() {
        SharedVector v = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        v.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());
    }
    @Test
    void testDotProductEmpty() {
        SharedVector a = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{}, VectorOrientation.COLUMN_MAJOR);
        assertEquals(0, a.dot(b));
    }

    @Test
    void testNegateEmptyVector() {
        SharedVector v = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        v.negate();
        assertEquals(0, v.length());
    }

    @Test
    void testAddEmptyVectors() {
        SharedVector a = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{}, VectorOrientation.COLUMN_MAJOR);
        a.add(b);
        assertEquals(0, a.length());
    }

    @Test
    void testVecMatMulValid() {
        SharedVector v = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);

        double[][] matrixData = {{3, 4}, {5, 6}};
        SharedMatrix m = new SharedMatrix();
        m.loadColumnMajor(matrixData); 

        v.vecMatMul(m);

        assertEquals(2, v.length());
        assertEquals(13.0, v.get(0));
        assertEquals(16.0, v.get(1));
    }

    @Test
    void testVecMatMulDimensionMismatch() {
       
        SharedVector v = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);

        double[][] matrixData = {{1}, {1}, {1}}; 
        SharedMatrix m = new SharedMatrix();
        m.loadColumnMajor(matrixData);
        assertThrows(IllegalArgumentException.class, () -> v.vecMatMul(m));
    }

    @Test
    void testVecMatMulWrongOrientation() {
        SharedVector v = new SharedVector(new double[]{1, 2}, VectorOrientation.COLUMN_MAJOR);
        SharedMatrix m = new SharedMatrix(new double[][]{{1, 2}, {3, 4}});

        assertThrows(IllegalArgumentException.class, () -> v.vecMatMul(m));
    }

    @Test
    void testConstructorDeepCopy() {
        double[] data = {1.0, 2.0};
        SharedVector v = new SharedVector(data, VectorOrientation.ROW_MAJOR);
        data[0] = 99.0; 
        
        assertEquals(1.0, v.get(0), "SharedVector should copy input array");
    }
}