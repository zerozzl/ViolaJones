package violajones;

import java.io.Serializable;

public class IntegralImageScore implements Serializable, Comparable<IntegralImageScore> {

	private static final long serialVersionUID = -3280156399672896204L;
	private int label;
	private double weight;
	private double score;

	public IntegralImageScore(int label, double weight, double score) {
		this.label = label;
		this.weight = weight;
		this.score = score;
	}

	public int getLabel() {
		return label;
	}

	public double getWeight() {
		return weight;
	}

	public double getScore() {
		return score;
	}
	
	@Override
	public int compareTo(IntegralImageScore o) {
		if (this.getScore() > o.getScore()) {
			return 1;
		} else if (this.getScore() == o.getScore()) {
			return 0;
		} else {
			return -1;
		}
	}
	
}
