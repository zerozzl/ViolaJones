package violajones;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HaarLikeFeature implements Serializable {

	private static final long serialVersionUID = 6535379986214547006L;
	private String type;
	private int[] pos;
	private int w;
	private int h;
	private double theta;
	private int p;
	private double error;

	public HaarLikeFeature(String type, int[] pos, int w, int h) {
		this.type = type;
		this.pos = pos;
		this.w = w;
		this.h = h;
		this.theta = 0;
		this.p = 1;
		this.error = 0;
	}

	public double getEigenvalue(IntegralImage intImg) {
		double eigenvalue = 0;
		if (type.equals("1a")) {
			int part = w / 2;
			double negative = intImg.getAreaSum(pos[1], pos[0], part, h, 0);
			double positive = intImg.getAreaSum(pos[1] + part, pos[0], part, h, 0);
			eigenvalue = positive - negative;
		} else if (type.equals("1b")) {
			int part = h / 2;
			double negative = intImg.getAreaSum(pos[1], pos[0], w, part, 0);
			double positive = intImg.getAreaSum(pos[1], pos[0] + part, w, part, 0);
			eigenvalue = positive - negative;
		} else if (type.equals("1c")) {
			int part = w / 2;
			double negative = intImg.getAreaSum(pos[1] + h, pos[0], part, h, 45);
			double positive = intImg.getAreaSum(pos[1] + h + part, pos[0] + part, part, h, 45);
			eigenvalue = positive - negative;
		} else if (type.equals("1d")) {
			int part = h / 2;
			double negative = intImg.getAreaSum(pos[1] + h, pos[0], w, part, 45);
			double positive = intImg.getAreaSum(pos[1] + part, pos[0] + part, w, part, 45);
			eigenvalue = positive - negative;
		} else if (type.equals("2a")) {
			int part = w / 3;
			double negative1 = intImg.getAreaSum(pos[1], pos[0], part, h, 0);
			double positive = intImg.getAreaSum(pos[1] + part, pos[0], part, h, 0);
			double negative2 = intImg.getAreaSum(pos[1] + 2 * part, pos[0], part, h, 0);
			eigenvalue = positive - negative1 - negative2;
		} else if (type.equals("2b")) {
			int part = w / 4;
			double negative1 = intImg.getAreaSum(pos[1], pos[0], part, h, 0);
			double positive = intImg.getAreaSum(pos[1] + part, pos[0], 2 * part, h, 0);
			double negative2 = intImg.getAreaSum(pos[1] + 3 * part, pos[0], part, h, 0);
			eigenvalue = positive - negative1 - negative2;
		} else if (type.equals("2c")) {
			int part = h / 3;
			double negative1 = intImg.getAreaSum(pos[1], pos[0], w, part, 0);
			double positive = intImg.getAreaSum(pos[1], pos[0] + part, w, part, 0);
			double negative2 = intImg.getAreaSum(pos[1], pos[0] + 2 * part, w, part, 0);
			eigenvalue = positive - negative1 - negative2;
		} else if (type.equals("2d")) {
			int part = h / 4;
			double negative1 = intImg.getAreaSum(pos[1], pos[0], w, part, 0);
			double positive = intImg.getAreaSum(pos[1], pos[0] + part, w, 2 * part, 0);
			double negative2 = intImg.getAreaSum(pos[1], pos[0] + 3 * part, w, part, 0);
			eigenvalue = positive - negative1 - negative2;
		} else if (type.equals("2e")) {
			int part = w / 3;
			double negative1 = intImg.getAreaSum(pos[1] + h, pos[0], part, h, 45);
			double positive = intImg.getAreaSum(pos[1] + h + part, pos[0] + part, part, h, 45);
			double negative2 = intImg.getAreaSum(pos[1] + h + 2 * part, pos[0] + 2 * part, part, h,
					45);
			eigenvalue = positive - negative1 - negative2;
		} else if (type.equals("2f")) {
			int part = w / 4;
			double negative1 = intImg.getAreaSum(pos[1] + h, pos[0], part, h, 45);
			double positive = intImg.getAreaSum(pos[1] + h + part, pos[0] + part, 2 * part, h, 45);
			double negative2 = intImg.getAreaSum(pos[1] + h + 3 * part, pos[0] + 3 * part, part, h,
					45);
			eigenvalue = positive - negative1 - negative2;
		} else if (type.equals("2g")) {
			int part = h / 3;
			double negative1 = intImg.getAreaSum(pos[1] + h, pos[0], w, part, 45);
			double positive = intImg.getAreaSum(pos[1] + 2 * part, pos[0] + part, w, part, 45);
			double negative2 = intImg.getAreaSum(pos[1] + part, pos[0] + 2 * part, w, part, 45);
			eigenvalue = positive - negative1 - negative2;
		} else if (type.equals("2h")) {
			int part = h / 4;
			double negative1 = intImg.getAreaSum(pos[1] + h, pos[0], w, part, 45);
			double positive = intImg.getAreaSum(pos[1] + 3 * part, pos[0] + part, w, 2 * part, 45);
			double negative2 = intImg.getAreaSum(pos[1] + part, pos[0] + 3 * part, w, part, 45);
			eigenvalue = positive - negative1 - negative2;
		} else if (type.equals("3a")) {
			int partw = w / 3;
			int parth = h / 3;
			double whole = intImg.getAreaSum(pos[1], pos[0], w, h, 0);
			double positive = intImg.getAreaSum(pos[1] + partw, pos[0] + parth, partw, parth, 0);
			eigenvalue = 2 * positive - whole;
		} else if (type.equals("3b")) {
			int partw = w / 3;
			int parth = h / 3;
			double whole = intImg.getAreaSum(pos[1] + h, pos[0], w, h, 45);
			double positive = intImg.getAreaSum(pos[1] + partw + 2 * parth, pos[0] + partw + parth, partw,
					parth, 45);
			eigenvalue = 2 * positive - whole;
		}
		return eigenvalue / (w * h);
	}
	
	// 投票
	public int getVote(IntegralImage intImg) {
		double score = this.getEigenvalue(intImg);
		if (this.p * score <= this.p * this.theta) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public void updateInfo(double theta, double err, int p) {
		this.theta = theta;
		this.error = err;
		this.p = p;
	}

	public double getError() {
		return error;
	}

	public String toString() {
		return "type:" + this.type + "|pos:" + this.pos[0] + "," + this.pos[1] + "|w:" + this.w + "|h:" + this.h
				+ "|theta:" + this.theta + "|p:" + this.p;
	}
	
	public String toStringWithWeight(double weight) {
		return "type:" + this.type + "|pos:" + this.pos[0] + "," + this.pos[1] + "|w:" + this.w + "|h:" + this.h
				+ "|theta:" + this.theta + "|p:" + this.p + "|weight:" + weight;
	}

	// 初始化特征
	public static List<HaarLikeFeature> initFeatures(int W, int H, List<FeatureTemplate> templates) {
		List<HaarLikeFeature> features = new ArrayList<HaarLikeFeature>();
		for (FeatureTemplate feaTemp : templates) {
			if (feaTemp.getAngle() == 0) {
				int wblock = W / feaTemp.getW();
				int hblock = H / feaTemp.getH();
				for (int i = 0; i < wblock; i++) {
					for (int j = 0; j < hblock; j++) {
						for (int y = 0; y < H - (j + 1) * feaTemp.getH() + 1; y++) {
							for (int x = 0; x < W - (i + 1) * feaTemp.getW() + 1; x++) {
								int[] pos = new int[2];
								pos[0] = y;
								pos[1] = x;
								features.add(new HaarLikeFeature(feaTemp.getType(), pos, (i + 1) * feaTemp.getW(),
										(j + 1) * feaTemp.getH()));
							}
						}
					}
				}
			} else if (feaTemp.getAngle() == 45) {
				for (int i = 0; i < W; i++) {
					for (int j = 0; j < H; j++) {
						int edge = (i + 1) * feaTemp.getW() + (j + 1) * feaTemp.getH();
						if (edge > H || edge > W) {
							break;
						}
						for (int y = 0; y < H - edge + 1; y++) {
							for (int x = 0; x < W - edge + 1; x++) {
								int[] pos = new int[2];
								pos[0] = y;
								pos[1] = x;
								features.add(new HaarLikeFeature(feaTemp.getType(), pos, (i + 1) * feaTemp.getW(),
										(j + 1) * feaTemp.getH()));
							}
						}
					}
				}
			}
		}
		return features;
	}
	
}
