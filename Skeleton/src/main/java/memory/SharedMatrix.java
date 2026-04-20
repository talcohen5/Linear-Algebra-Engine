package memory;

import java.util.Arrays;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors
    
    public SharedMatrix() {
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        loadRowMajor(matrix);
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        if (matrix == null || matrix.length == 0) {
            this.vectors = new SharedVector[0];
            return;
        }
        SharedVector newVectors[] = new SharedVector[matrix.length];
       
        for (int i = 0; i < matrix.length; i++) {
           newVectors[i] = new SharedVector(Arrays.copyOf(matrix[i], matrix[i].length),VectorOrientation.ROW_MAJOR); 
        }
        this.vectors = newVectors;
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            loadRowMajor(matrix);
            return;
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        
        SharedVector[] newVectors = new SharedVector[cols];

        for (int j = 0; j < cols; j++) {
            double[] col = new double[rows];
            for (int i = 0; i < rows; i++) {
                col[i] = matrix[i][j];
            }
            newVectors[j] = new SharedVector(col, VectorOrientation.COLUMN_MAJOR);
        }
        this.vectors = newVectors;
    }

    public double[][] readRowMajor() {
       if (vectors.length == 0) {
        return new double[0][0];
    }

    VectorOrientation currentOrientation = getOrientation(); 
    
    int primaryLength = vectors.length; 
    int secondaryLength = vectors[0].length(); 
    
    int rows;
    int cols;

    if (currentOrientation == VectorOrientation.ROW_MAJOR) {
        rows = primaryLength;
        cols = secondaryLength;
    } else { 
        rows = secondaryLength;
        cols = primaryLength;
    }
    
    double[][] result = new double[rows][cols];
    acquireAllVectorReadLocks(vectors); 
    
    try {
        if (currentOrientation == VectorOrientation.ROW_MAJOR) {
          
            for (int i = 0; i < rows; i++) {
                SharedVector rowVector = vectors[i];
                for (int j = 0; j < cols; j++) {
                    result[i][j] = rowVector.get(j); 
                }
            }
        } else { 
            for (int j = 0; j < cols; j++) {
                SharedVector colVector = vectors[j];
                for (int i = 0; i < rows; i++) {
                    result[i][j] = colVector.get(i);
                }
            }
        }
    } finally {
        
        releaseAllVectorReadLocks(vectors);
    }
    return result;
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        if (index < 0 || index >= vectors.length) {
            throw new IndexOutOfBoundsException("Vector index out of bounds: " + index);
        }
        return vectors[index];
    }

    public int length() {
        // TODO: return number of stored vectors
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
       if (vectors.length == 0) {
        // Default to ROW_MAJOR if empty
            return VectorOrientation.ROW_MAJOR; 
        }
        return vectors[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for (SharedVector vec : vecs) {
            vec.readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for (SharedVector vec : vecs) {
            vec.readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for (SharedVector vec : vecs) {
            vec.writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        for (SharedVector vec : vecs) {
            vec.writeUnlock();
        }
    }
}
