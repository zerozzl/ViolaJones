package violajones;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CascadeAdaBoost implements Serializable {

	private static final long serialVersionUID = -1171610810606395341L;
	private List<IntegralImage> posDatas;
	private List<IntegralImage> negDatas;
	private List<HaarLikeFeature> features;
	
	public CascadeAdaBoost(List<IntegralImage> posDatas,
			List<IntegralImage> negDatas, List<HaarLikeFeature> features) {
		this.posDatas = posDatas;
		this.negDatas = negDatas;
		this.features = features;
	}
	
	public void train(double eachDR, double eachFAR, double finalFAR) {
		List<Map<HaarLikeFeature, Double>> model = new ArrayList<Map<HaarLikeFeature, Double>>();
		List<Double> DRs = new ArrayList<Double>();
		List<Double> FARs = new ArrayList<Double>();
		DRs.add(1.0);
		FARs.add(1.0);
		int i = 0;
		while(FARs.get(i) > finalFAR) {
			i += 1;
			double far = FARs.get(i - 1);
			Map<HaarLikeFeature, Double> ada = null;
			if(i == 1) {
				ada = new HashMap<HaarLikeFeature, Double>();
			} else {
				ada = model.get(i - 1);
			}
			while(far > finalFAR * FARs.get(i - 1)) {
				
			}
		}
	}

}
