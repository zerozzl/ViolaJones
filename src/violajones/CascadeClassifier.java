package violajones;

import java.util.ArrayList;
import java.util.List;

public class CascadeClassifier {

	private List<AdaClassifier> classifiers;

	public CascadeClassifier() {
		classifiers = new ArrayList<AdaClassifier>();
	}
	
	public AdaClassifier getNextClassifier() {
		AdaClassifier ada = null;
		if(classifiers.isEmpty()) {
			ada = new AdaClassifier();
		} else {
			ada = (AdaClassifier)classifiers.get(classifiers.size() - 1).clone();
		}
		classifiers.add(ada);
		return ada;
	}
	
	public void print() {
		System.out.println("num: " + classifiers.size());
		for(int i = 0; i< classifiers.size(); i++) {
			AdaClassifier ada = classifiers.get(i);
			System.out.println("theta: " + ada.getTheta()
					+ ", fea num: " + ada.getClassifiers().size());
		}
	}
	
	public static void main(String[] args) {
		CascadeClassifier classifier = new CascadeClassifier();
		
		AdaClassifier ada1 = classifier.getNextClassifier();
		ada1.addFeature(new HaarLikeFeature("1s", new int[2], 2, 1), 0);
		
		AdaClassifier ada2 = classifier.getNextClassifier();
		ada2.addFeature(new HaarLikeFeature("1s", new int[2], 2, 1), 0);
		ada2.setTheta(1.0);
		
		AdaClassifier ada3 = classifier.getNextClassifier();
		
		ada2.addFeature(new HaarLikeFeature("1s", new int[2], 2, 1), 0);
		ada3.setTheta(2.0);
		
		AdaClassifier ada4 = classifier.getNextClassifier();
		
		ada1.setTheta(3.0);
		classifier.print();
	}
	
}
