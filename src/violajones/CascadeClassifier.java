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
		if(classifiers.isEmpty()) {
			ada = new AdaClassifier();
		} else {
			ada = classifiers.get(classifiers.size() - 1).clone();
		}
		classifiers.add(ada);
		DRs.add((double) 0);
		FARs.add((double) 1);
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
	
	public void computeCurrentLayerDRAndFAR(List<IntegralImage> datas) {
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
	
	public double getCurrentLayerDR() {
		return DRs.get(DRs.size() - 1);
	}
	
	public double getCurrentLayerFAR() {
		return FARs.get(FARs.size() - 1);
	}
	
	public int getLayerSize() {
		return this.classifiers.size();
	}
	
	public double getClassifierDR() {
		double dr = 1;
		for(Double d : this.DRs) {
			dr *= d;
		}
		return dr;
	}
	
	public double getClassifierFAR() {
		double far = 1;
		for(Double fa : this.FARs) {
			far *= fa;
		}
		return far;
	}
	
	public List<String> exportModel() {
		List<String> model = new ArrayList<String>();
		for(int i = 0; i < this.classifiers.size(); i++) {
			model.addAll(this.classifiers.get(i).exportModel(i));
		}
		return model;
	}
	
}
