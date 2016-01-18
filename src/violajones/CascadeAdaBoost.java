package violajones;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

public class CascadeAdaBoost implements Serializable {

	private static final long serialVersionUID = -1171610810606395341L;
	private List<IntegralImage> posDatas;
	private List<IntegralImage> negDatas;
	private List<IntegralImage> datas;
	private List<HaarLikeFeature> features;

	public CascadeAdaBoost(List<IntegralImage> posDatas, List<IntegralImage> negDatas, List<HaarLikeFeature> features) {
		this.posDatas = posDatas;
		this.negDatas = negDatas;
		this.datas = new ArrayList<IntegralImage>();
		this.features = features;
	}

	public CascadeClassifier train(JavaSparkContext sc, int sparkCores,
			double eachDR, double eachFAR, double finalFAR, String misClassFile) {
		CascadeClassifier classifier = new CascadeClassifier();
		double curFAR = 1.0;
		while (curFAR > finalFAR) {
			this.datas.clear();
			this.datas.addAll(this.posDatas);
			this.datas.addAll(this.negDatas);
			Collections.shuffle(this.datas);
			System.out.println("Training Layer " + (classifier.getLayerSize() + 1));
			System.out.println("Training Data size: " + this.datas.size() + "(" + this.posDatas.size() + "/"
					+ this.negDatas.size() + ")");

			AdaClassifier ada = classifier.getNextClassifier();
			while (classifier.getCurrentLayerFAR() > eachFAR
					&& classifier.getClassifierFAR() > finalFAR) {
				this.trainStrongClassifier(sc, sparkCores, ada);
				classifier.computeCurrentLayerDRAndFAR(datas);
				while (classifier.getCurrentLayerDR() < eachDR) {
					ada.adjustingThreshold();
					classifier.computeCurrentLayerDRAndFAR(datas);
				}
				System.out.println("Layer: " + classifier.getLayerSize()+ ", AdaClassifier size: "
						+ ada.getClassifierSize() + ", DR: " + classifier.getClassifierDR()
						+ ", FAR: " + classifier.getClassifierFAR());
				exportMisClassificationDatas(misClassFile, classifier, datas);
			}
			curFAR = classifier.getCurrentLayerFAR() * curFAR;

			System.out.println("Training Layer " + classifier.getLayerSize() + " success, DR: "
					+ classifier.getClassifierDR() + ", FAR: " + classifier.getClassifierFAR() + "/" + curFAR);

			this.posDatas.clear();
			this.negDatas.clear();
			for (IntegralImage iim : this.datas) {
				if (iim.getLabel() == 0) {
					if (classifier.predict(iim) == 1) {
						this.negDatas.add(iim);
					}
				}
				if (iim.getLabel() == 1) {
					if (classifier.predict(iim) == 1) {
						this.posDatas.add(iim);
					}
				}
			}
		}
		return classifier;
	}

	// 训练强分类器
	private void trainStrongClassifier(JavaSparkContext sc, int sparkCores, AdaClassifier ada) {
		this.trainWeakClassifier(sc, sparkCores);
		HaarLikeFeature pickFea = this.pickWeakClassifier();
		double alpha = Math.log((1.0 - (pickFea.getError() + 0.0001)) / (pickFea.getError() + 0.0001));
		ada.addFeature(pickFea, alpha);
		this.updateImagesWeight(pickFea);
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
					scores.add(new IntegralImageScore(datas.get(i).getLabel(), datas.get(i).getWeight(),
							fea.getEigenvalue(datas.get(i))));
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
	private void updateImagesWeight(HaarLikeFeature pickFea) {
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
	
	// 导出误分类样本
	private void exportMisClassificationDatas(String misClassFile,
			CascadeClassifier classifier, List<IntegralImage> datas) {
		List<String> misClassDatas = new ArrayList<String>();
		for(IntegralImage iim : datas) {
			if(classifier.predict(iim) != iim.getLabel()) {
				misClassDatas.add(String.valueOf(iim.getId()));
			}
		}
		FileUtils.exportFile(misClassFile, misClassDatas);
	}

}
