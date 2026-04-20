package memory;

import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        this.vector = Arrays.copyOf(vector, vector.length);
        this.orientation = orientation;
    }

    public double get(int index) {
        readLock();
        try {
            return vector[index];
        } finally {
            readUnlock();
        }
    }

    public int length() {
        readLock();
        try {
            return vector.length;
        } finally {
            readUnlock();
        }
    }

    public VectorOrientation getOrientation() {
        readLock();
        try {
            return orientation;
        } finally {
            readUnlock();
        }
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        lock.writeLock().unlock();
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnlock() {
        lock.readLock().unlock();
    }


    public void transpose() {

       writeLock(); 
        try {
    
            if (this.orientation == VectorOrientation.ROW_MAJOR) {
                this.orientation = VectorOrientation.COLUMN_MAJOR;
            } else {
                this.orientation = VectorOrientation.ROW_MAJOR;
            }
        } finally {
            writeUnlock();
        }
    }

    public void add(SharedVector other) {
        if (other == null) {
            throw new IllegalArgumentException("other == null");
        }
        if (this.length() != other.length()) {
            throw new IllegalArgumentException("Vector dimensions must match for addition.");
        }
       
        writeLock(); 
        other.readLock(); 
        
        try {
                
            for (int i = 0; i < vector.length; i++) {
                this.vector[i] += other.vector[i];
            }
        } finally {
            other.readUnlock();
            writeUnlock();
        }
    }

    public void negate() {
        writeLock();
        try {
            for (int i = 0; i < vector.length; i++) {
                this.vector[i] = -this.vector[i];
            }
        } finally {
            writeUnlock();
        }
    }

    public double dot(SharedVector other) {
        double result = 0.0;
       if (other == null) {
            throw new IllegalArgumentException("other == null");
        }
        if (this.length() != other.length()) {
            throw new IllegalArgumentException("Vector dimensions must match for dot product.");
        }
        if (this.getOrientation() == other.getOrientation()) {
            throw new IllegalArgumentException("Dot product requires opposite orientations (ROW_MAJOR and COLUMN_MAJOR).");
        }
        readLock();
        other.readLock();

        try {
            
            for (int i = 0; i < vector.length; i++) {
                
                result += this.vector[i] * other.vector[i]; 
            }
            return result;
        } finally {
          
            other.readUnlock();
            readUnlock(); 
        }
        
    }

    public void vecMatMul(SharedMatrix matrix) {
       
        if (this.orientation != VectorOrientation.ROW_MAJOR) {
            throw new IllegalArgumentException("Vector must be ROW_MAJOR for vecMatMul.");
        }
        if (matrix == null) {
            throw new IllegalArgumentException("matrix == null");
        }
        int vectorColumns = this.length();
        int matrixRows;

        if (matrix.length() == 0) {
            matrixRows = 0;
        } else {
            matrixRows = matrix.get(0).length(); 
        }
        
        if (vectorColumns != matrixRows) {
            throw new IllegalArgumentException("Dimensions mismatch: Row length (" + vectorColumns + ") must equal Matrix rows (" + matrixRows + ").");
        }
        writeLock(); 
        
        try {
            int resultCols = matrix.length();
            double[] resultVectorData = new double[resultCols];
            
            for (int j = 0; j < resultCols; j++) {
                SharedVector column = matrix.get(j); 
                double dotProduct = 0;

                column.readLock();
                try {
                    for (int i = 0; i < this.length(); i++) {
                        dotProduct += this.vector[i] * column.get(i);
                    }
                } finally {
                    column.readUnlock();
                }
                
                resultVectorData[j] = dotProduct; 
            }

           
            
            if (this.vector.length != resultVectorData.length) {
                //if sizes differ, replace internal array
                this.vector = resultVectorData;
            } else {
                //if same size, copy data into existing array
                System.arraycopy(resultVectorData, 0, this.vector, 0, resultVectorData.length);
            }

        } finally {
        
            writeUnlock();
        }
    }
}
