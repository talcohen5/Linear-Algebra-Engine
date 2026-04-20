package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SharedMatrixTest {

    @Test
    void testLoadRowMajorAndRead() {
        double[][] m = {{1,2},{3,4}};
        SharedMatrix sm = new SharedMatrix(m);
        double[][] out = sm.readRowMajor();
        assertArrayEquals(m, out);
    }

    @Test
    void testLoadColumnMajor() {
        double[][] m = {{1,2},{3,4}};
        SharedMatrix sm = new SharedMatrix();
        sm.loadColumnMajor(m);
        assertEquals(VectorOrientation.COLUMN_MAJOR, sm.getOrientation());
        double[][] out = sm.readRowMajor();
        assertArrayEquals(m, out);
    }

    @Test
    void testEmptyMatrix() {
        SharedMatrix sm = new SharedMatrix();
        assertEquals(0, sm.length());
        assertEquals(0, sm.readRowMajor().length);
    }

    @Test
    void testGetInvalidIndex() {
        SharedMatrix sm = new SharedMatrix(new double[][]{{1}});
        assertThrows(IndexOutOfBoundsException.class, () -> sm.get(2));
    }
     @Test
    void testNonSquareMatrix() {
        double[][] m = {{1,2,3},{4,5,6}};
        SharedMatrix sm = new SharedMatrix(m);
        assertEquals(2, sm.length());
        assertArrayEquals(m, sm.readRowMajor());
    }

    @Test
    void testLoadNullMatrix() {
        SharedMatrix sm = new SharedMatrix();
        sm.loadRowMajor(null);
        assertEquals(0, sm.length(), "Matrix should be empty when loading null");
    }

    @Test
    void testLoadSingleElement() {
        double[][] m = {{5.5}};
        SharedMatrix sm = new SharedMatrix(m);
        assertEquals(1, sm.length());
        assertEquals(1, sm.get(0).length());
        assertEquals(5.5, sm.readRowMajor()[0][0]);
    }

    @Test
    void testLargeThinMatrix() {
        double[][] m = new double[100][1];
        for(int i=0; i<100; i++) m[i][0] = i;
        
        SharedMatrix sm = new SharedMatrix(m);
        assertEquals(100, sm.length());
        assertArrayEquals(m, sm.readRowMajor());
    }

    @Test
    void testDataIsolation() {
        double[][] m = {{1, 1}, {1, 1}};
        SharedMatrix sm = new SharedMatrix(m);
        
        m[0][0] = 99;
        
        assertNotEquals(99, sm.readRowMajor()[0][0], "Matrix should maintain its own copy of data");
    }

    @Test
    void testLoadColumnMajorNonSquare() {
    
        double[][] m = {
            {1, 2, 3},
            {4, 5, 6}
        };
        SharedMatrix sm = new SharedMatrix();
        sm.loadColumnMajor(m);
        
        assertEquals(3, sm.length(), "Should have 3 vectors (columns)");
        assertEquals(VectorOrientation.COLUMN_MAJOR, sm.getOrientation());

        assertArrayEquals(m, sm.readRowMajor());
    }

    @Test
    void testConcurrentReadLocks() {
        double[][] m = {{1, 2}, {3, 4}};
        SharedMatrix sm = new SharedMatrix(m);

        assertDoesNotThrow(() -> {
            double[][] out1 = sm.readRowMajor();
            double[][] out2 = sm.readRowMajor();
            assertArrayEquals(out1, out2);
        });
    }

    @Test
    void testGetAndModifyVector() {
      
        double[][] m = {{10, 20}};
        SharedMatrix sm = new SharedMatrix(m);
        
        SharedVector v = sm.get(0);
        v.negate(); 
        
        assertEquals(-10.0, sm.readRowMajor()[0][0], "Changes to a vector should be reflected in the matrix");
    }

    @Test
    void testReloadMatrixWithDifferentSize() {
        SharedMatrix sm = new SharedMatrix(new double[][]{{1, 2}});
        assertEquals(1, sm.length()); 
        double[][] newMat = {{1, 2}, {3, 4}, {5, 6}};
        sm.loadRowMajor(newMat);

        assertEquals(3, sm.length());
        assertEquals(5.0, sm.readRowMajor()[2][0]);
    }
   

}