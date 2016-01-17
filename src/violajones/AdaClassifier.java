package violajones;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AdaClassifier {

	private double theta;
	private Map<HaarLikeFeature, Double> classifiers;

	public AdaClassifier() {
		this.theta = 0;
		this.classifiers = new HashMap<HaarLikeFeature, Double>();
	}

	public AdaClassifier(double th, Map<HaarLikeFeature, Double> cla) {
		this.theta = th;
		this.classifiers = cla;
	}

	public AdaClassifier clone() {
		return new AdaClassifier(this.theta, new HashMap<HaarLikeFeature, Double>(this.classifiers));
	}

	public void addFeature(HaarLikeFeature fea, double alpha) {
		this.theta += 0.5 * alpha;
		this.classifiers.put(fea, alpha);
	}

	public void adjustingThreshold() {
		this.theta -= 0.01;
		if(this.theta < 0) {
			this.theta = 0;
		}
	}
	
	public int predict(IntegralImage iim) {
		double score = 0;
		for (Entry<HaarLikeFeature, Double> item : classifiers.entrySet()) {
			score += item.getKey().getVote(iim) * item.getValue();
		}
		if (score >= theta) {
			return 1;
		} else {
			return 0;
		}
	}

}
