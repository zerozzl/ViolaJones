package violajones;

import java.util.ArrayList;
import java.util.List;

public class CascadeClassifier {

	private List<AdaClassifier> classifiers;
	private List<Double> DRs;
	private List<Double> FARs;

	public CascadeClassifier() {
		this.classifiers = new ArrayList<AdaClassifier>();
		this.DRs = new ArrayList<Double>();
		this.FARs = new ArrayList<Double>();
	}
	
	public AdaClassifier getNextClassifier() {
		AdaClassifier ada = null;
		double dr = 0, far = 1;
		if(classifiers.isEmpty()) {
			ada = new AdaClassifier();
		} else {
			ada = classifiers.get(classifiers.size() - 1).clone();
			dr = this.getCurrentDR();
			far = this.getCurrentFAR();
		}
		classifiers.add(ada);
		DRs.add(dr);
		FARs.add(far);
		return ada;
	}
	
	public int predict(IntegralImage iim) {
		for(AdaClassifier ada : classifiers) {
			if(ada.predict(iim) == 0) {
				return 0;
			}
		}
		return 1;
	}
	
	public void computeDRAndFAR(List<IntegralImage> datas) {
		int pos = 0, neg = 0, tp = 0, fp = 0;
		for(IntegralImage iim : datas) {
			int pred = this.predict(iim);
			if(iim.getLabel() == 1) {
				pos++;
				if(pred == 1) {
					tp++;
				}
			} else {
				neg++;
				if(pred == 1) {
					fp += 1;
				}
			}
		}
		
		DRs.set(DRs.size() - 1, (double)tp / pos);
		FARs.set(FARs.size() - 1, (double)fp / neg);
	}
	
	public double getCurrentDR() {
		return DRs.get(DRs.size() - 1);
	}
	
	public double getCurrentFAR() {
		return FARs.get(FARs.size() - 1);
	}
	
	
	
	
	
	
	
	
	
	
	
	
//	public void print() {
//		System.out.println("num: " + classifiers.size());
//		for(int i = 0; i< classifiers.size(); i++) {
//			AdaClassifier ada = classifiers.get(i);
//			System.out.println("theta: " + ada.getTheta()
//					+ ", fea num: " + ada.getClassifiers().size());
//		}
//	}
//	
//	public static void main(String[] args) {
//		CascadeClassifier classifier = new CascadeClassifier();
//		
//		AdaClassifier ada1 = classifier.getNextClassifier();
//		ada1.addFeature(new HaarLikeFeature("1s", new int[2], 2, 1), 0);
//		
//		AdaClassifier ada2 = classifier.getNextClassifier();
//		ada2.addFeature(new HaarLikeFeature("1s", new int[2], 2, 1), 0);
//		ada2.setTheta(1.0);
//		
//		AdaClassifier ada3 = classifier.getNextClassifier();
//		
//		ada2.addFeature(new HaarLikeFeature("1s", new int[2], 2, 1), 0);
//		ada3.setTheta(2.0);
//		
//		AdaClassifier ada4 = classifier.getNextClassifier();
//		
//		ada1.setTheta(3.0);
//		classifier.print();
//	}
	
}
