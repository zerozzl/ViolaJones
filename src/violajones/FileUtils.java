package violajones;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FileUtils {

	// 读取AdaBoost训练数据
	public static List<IntegralImage> loadAdaBoostDatas(String posFile,
			String negFile, int width, int height) {
		List<IntegralImage> posImages = loadDatas(posFile, width, height, 1);
		List<IntegralImage> negImages = loadDatas(negFile, width, height, 0);
		List<IntegralImage> images = new ArrayList<IntegralImage>();
		images.addAll(posImages);
		images.addAll(negImages);
		return images;
	}

	// 读取训练数据
	public static List<IntegralImage> loadDatas(String fileName, int width,
			int height, int label) {
		List<IntegralImage> images = new ArrayList<IntegralImage>();
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String[] data = tempString.split(",");
				double[][] img = new double[height][width];
				int index = 0;
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						img[i][j] = Float.parseFloat(data[index++]);
					}
				}
				images.add(new IntegralImage(img, label, 0));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		double weight = 1.0 / (2 * images.size());
		for (IntegralImage iim : images) {
			iim.setWeight(weight);
		}

		doMeanAndNormalization(images);
		return images;
	}

	public static Map<Integer, List<IntegralImage>> loadCascadeAdaBoostDatas(
			String fileName, int width, int height) {
		List<IntegralImage> datas = loadAdaBoostDatas(fileName, width, height);
		List<IntegralImage> posDatas = new ArrayList<IntegralImage>();
		List<IntegralImage> negDatas = new ArrayList<IntegralImage>();
		for (IntegralImage iim : datas) {
			if (iim.getLabel() == 1) {
				posDatas.add(iim);
			} else {
				negDatas.add(iim);
			}
		}
		Map<Integer, List<IntegralImage>> resultMap = new HashMap<Integer, List<IntegralImage>>();
		resultMap.put(1, posDatas);
		resultMap.put(0, negDatas);
		return resultMap;
	}
	
	// 读取AdaBoost训练数据
	public static List<IntegralImage> loadAdaBoostDatas(String fileName,
			int width, int height) {
		double posWeight = 0, negWeight = 0;
		List<IntegralImage> images = new ArrayList<IntegralImage>();
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			boolean firstRow = true;
			while ((tempString = reader.readLine()) != null) {
				if (firstRow) {
					String[] num = tempString.split(",");
					posWeight = 1 / (2 * Float.parseFloat(num[0]));
					negWeight = 1 / (2 * Float.parseFloat(num[1]));
					firstRow = false;
				} else {
					String[] data = tempString.split(",");
					double[][] img = new double[width][height];
					int index = 0;
					for (int i = 0; i < width; i++) {
						for (int j = 0; j < height; j++) {
							img[i][j] = Float.parseFloat(data[index++]);
						}
					}
					int label = Integer.parseInt(data[index]);
					images.add(new IntegralImage(img, label,
							label == 1 ? posWeight : negWeight));
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		doMeanAndNormalization(images);
		return images;
	}

	// 导出模型
	public static void exportModel(String file,
			Map<HaarLikeFeature, Double> model) {
		if (model != null && !model.isEmpty()) {
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file)));
				Iterator<HaarLikeFeature> key = model.keySet().iterator();
				while (key.hasNext()) {
					HaarLikeFeature fea = key.next();
					double weight = model.get(fea);
					out.write(fea.toString() + "|weight:" + weight + "\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 均值归一化
	private static void doMeanAndNormalization(List<IntegralImage> orig) {
		if (orig == null || orig.isEmpty()) {
			return;
		}
		for (IntegralImage iim : orig) {
			double mean = 0;
			double[][] img = iim.getImage();
			double size = img.length * img[0].length;

			for (int i = 0; i < img.length; i++) {
				for (int j = 0; j < img[i].length; j++) {
					mean += img[i][j];
				}
			}
			mean = mean / size;

			for (int i = 0; i < img.length; i++) {
				for (int j = 0; j < img[i].length; j++) {
					img[i][j] = (img[i][j] - mean) / 255;
				}
			}
		}
	}

	// public static void main(String[] args) throws IOException {
	// String srcFile = "E:/TestDatas/MLStudy/FaceDection/MIT/MIT_DATA.txt";
	// int width = 24, height = 24;
	// List<IntegralImage> images = loadAdaBoostDatas(srcFile, width, height);
	// System.out.println(images.size());
	// System.out.println(images.get(0).getLabel() + ", "
	// + images.get(0).getWeight());
	// System.out.println(images.get(5000).getLabel() + ", "
	// + images.get(5000).getWeight());
	// System.out.println(images.get(0).getAreaSum(0, 0, 2, 1, 0));
	//
	// float[][] img = images.get(0).image;
	// for (int i = 0; i < img.length; i++) {
	// for (int j = 0; j < img[i].length; j++) {
	// System.out.print(img[i][j] + ", ");
	// }
	// System.out.println();
	// }
	// }

	// public static void main(String[] args) {
	// HaarLikeFeature fea1 = new HaarLikeFeature("2a", new int[2], 1, 1);
	// HaarLikeFeature fea2 = new HaarLikeFeature("3b", new int[2], 1, 1);
	// Map<HaarLikeFeature, Double> model = new HashMap<HaarLikeFeature,
	// Double>();
	// model.put(fea1, (double) 5);
	// model.put(fea2, (double) 5);
	// exportModel("/home/hadoop/test.txt", model);
	// }

	// public static void main(String[] args) {
	// double[][] im = new double[7][7];
	// for(int i = 0; i< 7; i++) {
	// for(int j = 0; j < 7; j++) {
	// im[i][j] = i * 7 + j + 1;
	// }
	// }
	//
	// IntegralImage iim = new IntegralImage(im, 0, 0);
	//
	// List<IntegralImage> iimList = new ArrayList<IntegralImage>();
	// iimList.add(iim);
	//
	// double[][] img = iimList.get(0).getImage();
	// for (int i = 0; i < img.length; i++) {
	// for (int j = 0; j < img[i].length; j++) {
	// System.out.print(img[i][j] + ", ");
	// }
	// System.out.println();
	// }
	//
	// doMeanAndNormalization(iimList);
	//
	// img = iimList.get(0).getImage();
	// for (int i = 0; i < img.length; i++) {
	// for (int j = 0; j < img[i].length; j++) {
	// System.out.print(img[i][j] + ", ");
	// }
	// System.out.println();
	// }
	// }

}
