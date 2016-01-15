package violajones;

import java.util.HashMap;
import java.util.Map;

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
		this.classifiers.put(fea, alpha);
	}

	
	
	
	
	
	
	
	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public Map<HaarLikeFeature, Double> getClassifiers() {
		return classifiers;
	}
	
}
