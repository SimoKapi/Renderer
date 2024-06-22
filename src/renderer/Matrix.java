package renderer;

public class Matrix {
    double[][] values;
    Matrix(double[][] values) {
        this.values = values;
    }
    
    public Matrix multiply(Matrix other) {
    	int rowCount = this.values.length;
    	int colCount = other.values[0].length;
    	double[][] returnList = new double[rowCount][colCount];
    	for (int row = 0; row < rowCount; row++) {
    		for (int col = 0; col < colCount; col++) {
    			for (int i = 0; i < rowCount; i++) {
	    			returnList[row][col] += this.values[row][i] * other.values[i][col];
    			}
    		}
    	}
    	return new Matrix(returnList);
    }
    public Vector3 transform(Vector3 in) {
    	return new Vector3(
	    		in.x * values[0][0] + in.y * values[1][0] + in.z * values[2][0],
	    		in.x * values[0][1] + in.y * values[1][1] + in.z * values[2][1],
	    		in.x * values[0][2] + in.y * values[1][2] + in.z * values[2][2]
		    	);
    }
    
    public static Matrix zero = new Matrix(new double[][] {
    	new double[] {0, 0, 0},
    	new double[] {0, 0, 0},
    	new double[] {0, 0, 0}
    });
}