package violajones;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class ViolaJones {

	private static void checkMemoryInfo() {
		Runtime currRuntime = Runtime.getRuntime();
		int nFreeMemory = (int) (currRuntime.freeMemory() / 1024 / 1024);
		int nTotalMemory = (int) (currRuntime.totalMemory() / 1024 / 1024);
		System.out.println(nFreeMemory + "M/" + nTotalMemory + "M(free/total)");
	}

	private static void trainAdaBoost() {
		int imgSize = 24;
		int T = 300;
		// String root = "/home/hadoop/ProgramDatas/MLStudy/FaceDection/";
		String root = "E:/TestDatas/MLStudy/FaceDection/";
		String posFile = root + "pos_data.txt";
		String negFile = root + "neg_data.txt";
		String modelFile = root + "adaboost_model.txt";
		String sparkAppName = "Viola-Jones Train";
		String sparkMaster = "spark://localhost:7077";
		int sparkCores = 60;
		String sparkJars = "/home/hadoop/violajones/violajones.jar";

		checkMemoryInfo();

		System.out.println("initing feature templates...");
		List<FeatureTemplate> templates = FeatureTemplate.initFeaTemplates();

		System.out.println("initing features...");
		List<HaarLikeFeature> features = HaarLikeFeature.initFeatures(imgSize,
				imgSize, templates);

		System.out.println("loading train datas...");
		List<IntegralImage> trainDatas = FileUtils.loadAdaBoostDatas(posFile,
				negFile, imgSize, imgSize);
		Collections.shuffle(trainDatas);

		SparkConf sparkConf = new SparkConf().setMaster(sparkMaster)
				.setAppName(sparkAppName).set("spark.executor.memory", "2g");
		JavaSparkContext sc = new JavaSparkContext(sparkConf);
		sc.addJar(sparkJars);
		sc.setLogLevel("WARN");

		System.out.println("training adaboost...");
		AdaBoost adaBoost = new AdaBoost(trainDatas, features);
		Map<HaarLikeFeature, Double> model = adaBoost.train(sc, sparkCores, T);

		System.out.println("exporting model...");
		FileUtils.exportModel(modelFile, model);

		System.out.println("viola jones training success!");
		sc.close();
	}

	private static void trainCascadeAdaBoost() {
		int imgSize = 24;
		double eachDR = 0.99, eachFAR = 0.3, finalFAR = 0.0006;

		// String root = "/home/hadoop/ProgramDatas/MLStudy/FaceDection/";
		String root = "E:/TestDatas/MLStudy/FaceDection/";
		String dataFile = root + "MIT/MIT_DATA_DEV.txt";
		// String modelFile = root + "adaboost_model.txt";
		String sparkAppName = "Viola-Jones Train";
		String sparkMaster = "spark://localhost:7077";
		int sparkCores = 60;
		String sparkJars = "/home/hadoop/violajones/violajones.jar";

		checkMemoryInfo();

		System.out.println("initing feature templates...");
		List<FeatureTemplate> templates = FeatureTemplate.initFeaTemplates();

		System.out.println("initing features...");
		List<HaarLikeFeature> features = HaarLikeFeature.initFeatures(imgSize,
				imgSize, templates);

		System.out.println("loading train datas...");
		Map<Integer, List<IntegralImage>> trainDatas = FileUtils
				.loadCascadeAdaBoostDatas(dataFile, imgSize, imgSize);
		List<IntegralImage> posDatas = trainDatas.get(1);
		List<IntegralImage> negDatas = trainDatas.get(0);

		SparkConf sparkConf = new SparkConf().setMaster(sparkMaster)
				.setAppName(sparkAppName).set("spark.executor.memory", "2g");
		JavaSparkContext sc = new JavaSparkContext(sparkConf);
		sc.addJar(sparkJars);
		sc.setLogLevel("WARN");

		System.out.println("training cascade adaboost...");
		CascadeAdaBoost cascade = new CascadeAdaBoost(posDatas, negDatas,
				features);
		cascade.train(sc, sparkCores, eachDR, eachFAR, finalFAR);

		// System.out.println("exporting model...");
		// FileUtils.exportModel(modelFile, model);

		sc.close();
	}

	public static void main(String[] args) {
		Date begin = new Date();
		// trainAdaBoost();
		trainCascadeAdaBoost();
		Date end = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out
				.println("--------------------------------------------------");
		System.out.println("程序开始时间: " + df.format(begin));
		System.out.println("程序开始时间: " + df.format(end));
	}

}
