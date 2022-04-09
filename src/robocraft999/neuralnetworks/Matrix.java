package robocraft999.neuralnetworks;

import java.util.Random;

import processing.core.PApplet;
import processing.data.JSONArray;

public class Matrix implements Cloneable {

	private int rows;
	private int cols;

	private float[][] data;

	public Matrix(int rows_, int cols_) {
		this.rows = rows_;
		this.cols = cols_;
		if (rows_ == 0 || cols_ == 0)
			System.out.println("WARNING: rows and cols should not be zero");
		this.data = new float[this.rows][this.cols];
	}

	/*
	 * ##################### Operation-Functions ########################
	 */

	// scalar product
	public void mult(float n) {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				this.data[i][j] *= n;
			}
		}
	}

	public void mult(Matrix n) {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				this.data[i][j] *= n.data[i][j];
			}
		}
	}

	// matrix product
	public static Matrix mult(Matrix m1, Matrix m2) {
		Matrix matrix = new Matrix(m1.rows, m2.cols);
		if (m1.cols != m2.rows) {
			PApplet.println("ERROR: Matrix.mult(): Cols of m1 must match rows of m2");
			m1.printMatrix();
			m2.printMatrix();
			return matrix;
		}

		for (int i = 0; i < matrix.rows; i++) {
			for (int j = 0; j < matrix.cols; j++) {
				float sum = 0;
				for (int k = 0; k < m1.cols; k++) {
					sum += m1.data[i][k] * m2.data[k][j];
				}
				matrix.data[i][j] = sum;
			}
		}

		return matrix;
	}

	public void add(float n) {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				this.data[i][j] += n;
			}
		}
	}

	public void add(Matrix n) {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				this.data[i][j] += n.data[i][j];
			}
		}
	}

	public static Matrix add(Matrix m1, Matrix m2) {
		Matrix matrix = new Matrix(m1.rows, m1.cols);
		if (m1.rows != m2.rows || m1.cols != m2.cols) {
			PApplet.println("ERROR: Matrix.add(): Matrices require same size to be added");
			return matrix;
		}

		for (int i = 0; i < m1.rows; i++) {
			for (int j = 0; j < m1.cols; j++) {
				matrix.data[i][j] = m1.data[i][j] + m2.data[i][j];
			}
		}

		return matrix;
	}

	public static Matrix sub(Matrix m1, Matrix m2) {
		Matrix matrix = new Matrix(m1.rows, m1.cols);
		if (m1.rows != m2.rows || m1.cols != m2.cols) {
			PApplet.println("ERROR: Matrix.add(): Matrices require same size to be subtracted");
			return matrix;
		}

		for (int i = 0; i < matrix.rows; i++) {
			for (int j = 0; j < matrix.cols; j++) {
				matrix.data[i][j] = m1.data[i][j] - m2.data[i][j];
			}
		}

		return matrix;
	}

	/*
	 * ##################### Misc-Functions ########################
	 */

	// randomizes data
	public void randomize() {
		Random r = new Random();
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				this.data[i][j] = r.nextFloat() * 2 - 1;
			}
		}
	}

	// transposes the data
	public static Matrix transpose(Matrix m1) {
		Matrix matrix = new Matrix(m1.cols, m1.rows);
		for (int i = 0; i < m1.rows; i++) {
			for (int j = 0; j < m1.cols; j++) {
				matrix.data[j][i] = m1.data[i][j];
			}
		}
		return matrix;
	}

	// prints the data to the console
	public void printMatrix() {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				PApplet.print(PApplet.nfs(this.data[i][j], 1, 3) + " ");
			}
			PApplet.println();
		}
		// println();
	}

	public static Matrix fromArray(float[] data) {
		Matrix matrix = new Matrix(data.length, 1);
		for (int i = 0; i < data.length; i++) {
			matrix.data[i][0] = data[i];
		}
		return matrix;
	}

	public float[] toArray() {
		float[] arr = new float[this.rows * this.cols];
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				arr[i * cols + j] = this.data[i][j];
			}
		}
		return arr;
	}

	public void map(Func func) {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				this.data[i][j] = func.f(this.data[i][j]);
			}
		}
	}

	public static Matrix map(Matrix m1, Func func) {
		Matrix matrix = new Matrix(m1.rows, m1.cols);
		for (int i = 0; i < matrix.rows; i++) {
			for (int j = 0; j < matrix.cols; j++) {
				matrix.data[i][j] = func.f(m1.data[i][j]);
			}
		}
		return matrix;
	}

	public String toString() {
		String matrix = "";
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				matrix += PApplet.nfs(this.data[i][j], 1, 3) + " ";
			}
			matrix += "\n";
		}
		// matrix += "\n";
		return "Matrix:\n" + matrix;
	}

	public JSONArray toJSON() {
		JSONArray rows = new JSONArray();
		for (int i = 0; i < this.rows; i++) {
			JSONArray column = new JSONArray();
			for (int j = 0; j < this.cols; j++) {
				column.append(this.data[i][j]);
			}
			rows.append(column);
		}
		return rows;
	}

	public static Matrix fromJSON(JSONArray jsonMatrix) {
		Matrix matrix = new Matrix(jsonMatrix.size(), jsonMatrix.getJSONArray(0).size());

		JSONArray rows = jsonMatrix;
		for (int i = 0; i < matrix.rows; i++) {
			JSONArray column = rows.getJSONArray(i);
			for (int j = 0; j < matrix.cols; j++) {
				matrix.data[i][j] = column.getFloat(j);
			}
		}

		// println(matrix);

		return matrix;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}

/*
 * How to use:
 * 
 * Option1: Func doubleIt = new Func(){ public float f(float x){ return x * 2; }
 * };
 * 
 * Option 2: Func test = (x) -> {return x * 3;}; //easier but not availible in
 * this java version
 */
interface Func {
	float f(float x);
}
