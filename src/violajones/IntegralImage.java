package violajones;

import java.io.Serializable;

/**
 * 积分图
 */
public class IntegralImage implements Serializable {

	private static final long serialVersionUID = 6671192731069159415L;
	private double[][] image; // 原图像
	private double[][] sat; // 举行特征积分图
	private double[][] rsat; // 倾斜特征积分图
	private int label; // 图像类别（是否人脸）
	private double weight; // 图像权重

	public IntegralImage(double[][] im, int l, double w) {
		this.image = im;
		this.label = l;
		this.weight = w;
		this.computeIntegrogram();
	}

	// 计算积分图
	private void computeIntegrogram() {
		int m = this.image.length;
		int n = this.image[0].length;
		this.sat = new double[m + 1][n + 1];
		this.rsat = new double[m + 1][n + 2];
		for (int y = 0; y < m; y++) {
			for (int x = 0; x < n; x++) {
				this.sat[y + 1][x + 1] = this.sat[y][x + 1] + this.sat[y + 1][x] - this.sat[y][x] + this.image[y][x];
				if (y == 0) {
					this.rsat[y + 1][x + 1] = this.image[y][x];
				} else {
					this.rsat[y + 1][x + 1] = this.rsat[y][x] + this.rsat[y][x + 2] - this.rsat[y - 1][x + 1]
							+ this.image[y][x] + this.image[y - 1][x];
				}
			}
		}
	}

	// 获取举行图像积分和
	public double getAreaSum(int x, int y, int w, int h, int angle) {
		double sum = 0;
		if (angle == 0) {
			sum = this.sat[y][x] + this.sat[y + h][x + w] - this.sat[y][x + w] - this.sat[y + h][x];
		} else if (angle == 45) {
			sum = this.rsat[y][x + 1] + this.rsat[y + w + h][x - h + w + 1] - this.rsat[y + h][x - h + 1]
					- this.rsat[y + w][x + w + 1];
		}
		return sum;
	}

	public double[][] getImage() {
		return image;
	}

	public int getLabel() {
		return label;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	// public static void main(String[] args) {
	// float[][] image = new float[7][7];
	// for(int i = 0; i < 7; i++) {
	// for(int j = 0; j < 7; j++) {
	// image[i][j] = i * 7 + j + 1;
	// }
	// }
	//
	// IntegralImage im = new IntegralImage(image, 1, 0);
	//
	// for(int i = 0; i<im.image.length; i++) {
	// for(int j = 0; j<im.image[i].length; j++) {
	// System.out.print(im.image[i][j] + " ");
	// }
	// System.out.println();
	// }
	//
	// for(int i = 0; i<im.sat.length; i++) {
	// for(int j = 0; j<im.sat[i].length; j++) {
	// System.out.print(im.sat[i][j] + " ");
	// }
	// System.out.println();
	// }
	//
	// for(int i = 0; i<im.rsat.length; i++) {
	// for(int j = 0; j<im.rsat[i].length; j++) {
	// System.out.print(im.rsat[i][j] + " ");
	// }
	// System.out.println();
	// }
	//
	// System.out.println(im.getAreaSum(2, 1, 2, 2, 45));
	// }

}
