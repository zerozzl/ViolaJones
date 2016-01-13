package violajones;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

/**
 * AdaBoost
 */
public class AdaBoost implements Serializable {

	private static final long serialVersionUID = 1184302568424159637L;

	private List<IntegralImage> datas;
	private List<HaarLikeFeature> features;

	public AdaBoost(List<IntegralImage> datas, List<HaarLikeFeature> features) {
		this.datas = datas;
		this.features = features;
	}

	public Map<HaarLikeFeature, Double> train(JavaSparkContext sc, int sparkCores, int T) {
		Map<HaarLikeFeature, Double> model = new HashMap<HaarLikeFeature, Double>();
		for (int i = 0; i < T; i++) {
			System.out.println("training classifer: " + (i + 1) + "/" + T);
			this.trainWeakClassifier(sc, sparkCores);
			HaarLikeFeature pickFea = this.pickWeakClassifier();
			double alpha = Math.log((1.0 - (pickFea.getError() + 0.0001)) / (pickFea.getError() + 0.0001));
			model.put(pickFea, alpha);
			this.updateSamplesWeight(pickFea);
		}
		return model;
	}

	// 训练弱分类器
	private void trainWeakClassifier(JavaSparkContext sc, int sparkCores) {
		JavaRDD<HaarLikeFeature> feaRDD = sc.parallelize(this.features, sparkCores);
		JavaRDD<HaarLikeFeature> feaTrainedRDD = feaRDD.map(new Function<HaarLikeFeature, HaarLikeFeature>() {
			private static final long serialVersionUID = 1L;
			@Override
			public HaarLikeFeature call(HaarLikeFeature fea) throws Exception {
				float wpos = 0, wneg = 0;
				List<IntegralImageScore> scores = new ArrayList<IntegralImageScore>();
				for (int i = 0; i < datas.size(); i++) {
					scores.add(new IntegralImageScore(datas.get(i).getLabel(),
							datas.get(i).getWeight(), fea.getEigenvalue(datas.get(i))));
					if (datas.get(i).getLabel() == 1) {
						wpos += datas.get(i).getWeight();
					} else {
						wneg += datas.get(i).getWeight();
					}
				}
				Collections.sort(scores);

				float spos = 0, sneg = 0;
				double bestSplit = 0, bestErr = 1;
				int polarity = 1;

				for (IntegralImageScore iims : scores) {
					float err = Math.min((spos + wneg - sneg), (sneg + wpos - spos));
					if (err < bestErr) {
						bestErr = err;
						bestSplit = iims.getScore();
						if ((spos + wneg - sneg) < (sneg + wpos - spos)) {
							polarity = -1;
						} else {
							polarity = 1;
						}
					}

					if (iims.getLabel() == 1) {
						spos += iims.getWeight();
					} else {
						sneg += iims.getWeight();
					}
				}

				fea.updateInfo(bestSplit, bestErr, polarity);
				return fea;
			}
		});
		this.features = new ArrayList<HaarLikeFeature>(feaTrainedRDD.collect());
	}

	// 选择弱分类器
	private HaarLikeFeature pickWeakClassifier() {
		HaarLikeFeature bestFea = null;
		double bestErr = 1;
		int bestIndex = 0;
		for (int i = 0; i < this.features.size(); i++) {
			HaarLikeFeature fea = this.features.get(i);
			if (fea.getError() < bestErr) {
				bestIndex = i;
				bestErr = fea.getError();
			}
		}

		bestFea = this.features.remove(bestIndex);
		return bestFea;
	}

	// 更新样本权重
	public void updateSamplesWeight(HaarLikeFeature pickFea) {
		double beta = (pickFea.getError() + 0.0001) / (1.0 - (pickFea.getError() + 0.0001));
		double z = 0.0;

		for (IntegralImage data : this.datas) {
			if (pickFea.getVote(data) == data.getLabel()) {
				z += data.getWeight() * beta;
			} else {
				z += data.getWeight();
			}
		}

		for (IntegralImage data : this.datas) {
			if (pickFea.getVote(data) == data.getLabel()) {
				data.setWeight(data.getWeight() * beta / z);
			} else {
				data.setWeight(data.getWeight() / z);
			}
		}
	}

}
