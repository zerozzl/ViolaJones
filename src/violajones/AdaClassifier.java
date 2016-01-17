package violajones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AdaClassifier {

	private double alpha;
	private Map<HaarLikeFeature, Double> classifiers;

	public AdaClassifier() {
		this.alpha = 0;
		this.classifiers = new HashMap<HaarLikeFeature, Double>();
	}

	public AdaClassifier(double alp, Map<HaarLikeFeature, Double> cla) {
		this.alpha = alp;
		this.classifiers = cla;
	}

	public AdaClassifier clone() {
		return new AdaClassifier(this.alpha, new HashMap<HaarLikeFeature, Double>(this.classifiers));
	}

	public void addFeature(HaarLikeFeature fea, double alp) {
		this.alpha += 0.5 * alp;
		this.classifiers.put(fea, alp);
	}

	public void adjustingThreshold() {
		this.alpha -= 0.01;
		if(this.alpha < 0) {
			this.alpha = 0;
		}
	}
	
	public int predict(IntegralImage iim) {
		double score = 0;
		for (Entry<HaarLikeFeature, Double> item : classifiers.entrySet()) {
			score += item.getKey().getVote(iim) * item.getValue();
		}
		if (score >= alpha) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public int getClassifierSize() {
		return this.classifiers.size();
	}
	
	public List<String> exportModel(int layer) {
		List<String> model = new ArrayList<String>();
		model.add("layer:" + layer + "|alpha:" + this.alpha);
		for (Entry<HaarLikeFeature, Double> item : classifiers.entrySet()) {
			model.add(item.getKey().toStringWithWeight(item.getValue()));
		}
		return model;
	}

}
