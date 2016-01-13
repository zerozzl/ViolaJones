package violajones;

import java.io.Serializable;

public class IntegralImageScore implements Serializable, Comparable<IntegralImageScore> {

	private static final long serialVersionUID = -3280156399672896204L;
	private int label;
	private double weight;
	private double score;

	public IntegralImageScore(int label, double weight, double score) {
		this.label = label;
		this.weight = weight;
		this.score = score;
	}

	public int getLabel() {
		return label;
	}

	public double getWeight() {
		return weight;
	}

	public double getScore() {
		return score;
	}
	
	@Override
	public int compareTo(IntegralImageScore o) {
		if (this.getScore() > o.getScore()) {
			return 1;
		} else if (this.getScore() == o.getScore()) {
			return 0;
		} else {
			return -1;
		}
	}
	
//	public static void main(String[] args) throws Exception {
//		String sparkAppName = "Viola-Jones Train";
//		String sparkMaster = "spark://localhost:7077";
//		int sparkCores = 60;
//		String sparkJars = "/home/hadoop/violajones/violajones.jar";
//		
//		SparkConf sparkConf = new SparkConf().setMaster(sparkMaster).setAppName(sparkAppName);
//		JavaSparkContext sc = new JavaSparkContext(sparkConf);
//		sc.addJar("/home/hadoop/violajones/violajones.jar");
//		
//		JavaRDD<String> scoresRDD = sc.textFile("hdfs://localhost:9000/datas/violajones/scores.txt", sparkCores);
//		JavaRDD<String[]> scoresArrRDD = scoresRDD.map(new Function<String, String[]>() {
//			@Override
//			public String[] call(String arg0) throws Exception {
//				return arg0.trim().split("\\|");
//			}
//		});
//		JavaRDD<List<IntegralImageScore>> iimsRDD = scoresArrRDD.map(new Function<String[], List<IntegralImageScore>>() {
//			@Override
//			public List<IntegralImageScore> call(String[] lineArr) throws Exception {
//				List<IntegralImageScore> scoreList = new ArrayList<IntegralImageScore>();
//				String[] feaInfo = lineArr[0].split(":");
//				int feaId = Integer.parseInt(feaInfo[1]);
//				int imIndex = 0;
//				double score = 0;
//				for(int i = 1; i<lineArr.length; i++) {
//					String[] scoreInfo = lineArr[i].split(",");
//					for(String info : scoreInfo) {
//						String[] tmp = info.split(":");
//						if(tmp[0].equals("i")) {
//							imIndex = Integer.parseInt(tmp[1]);
//						} else if(tmp[0].equals("s")) {
//							score = Double.parseDouble(tmp[1]);
//						}
//					}
//					scoreList.add(new IntegralImageScore(feaId, imIndex, score));
//				}
//				return scoreList;
//			}
//		});
//		
//		List<List<IntegralImageScore>> iims = iimsRDD.collect();
//		System.out.println(iims.size());
//	}
	
//	public static void main(String[] args) throws Exception {
//		Map<Integer, List<IntegralImageScore>> scoreMap = new HashMap<Integer, List<IntegralImageScore>>();
//		try {
//			BufferedInputStream bis = new BufferedInputStream(
//					new FileInputStream(new File("/home/hadoop/workdatas/FaceDection/scores.txt")));
//			BufferedReader in = new BufferedReader(new InputStreamReader(
//					bis, "utf-8"), 10 * 1024 * 1024);// 10M缓存
//			int num = 1;
//			while (in.ready()) {
//				System.out.println("reading info of feature " + num++);
//				List<IntegralImageScore> iims = new ArrayList<IntegralImageScore>();
//				String line = in.readLine();
//				String[] lineArr = line.trim().split("\\|");
//				String[] feaInfo = lineArr[0].split(":");
//				if(feaInfo[0].equals("f")) {
//					int feaId = Integer.parseInt(feaInfo[1]);
//					int imIndex = 0;
//					double score = 0;
//					for(int i = 1; i<lineArr.length; i++) {
//						String[] scoreInfo = lineArr[i].split(",");
//						for(String info : scoreInfo) {
//							String[] tmp = info.split(":");
//							if(tmp[0].equals("i")) {
//								imIndex = Integer.parseInt(tmp[1]);
//							} else if(tmp[0].equals("s")) {
//								score = Double.parseDouble(tmp[1]);
//							}
//						}
//						iims.add(new IntegralImageScore(feaId, imIndex, score));
//					}
//					scoreMap.put(feaId, iims);
//				} else {
//					throw new Exception("data model is not collect");
//				}
//			}
//			in.close();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//	}
	
}
