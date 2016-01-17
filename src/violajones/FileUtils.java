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

	public static Map<Integer, List<IntegralImage>> loadTrainDatasSeparate(
			String fileName, int width, int height) {
		List<IntegralImage> posImages = new ArrayList<IntegralImage>();
		List<IntegralImage> negImages = new ArrayList<IntegralImage>();
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String[] data = tempString.split(",");
				double[][] img = new double[width][height];
				int index = 0;
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						img[i][j] = Float.parseFloat(data[index++]);
					}
				}
				int label = Integer.parseInt(data[index]);
				if(label == 1) {
					posImages.add(new IntegralImage(img, label, 0));
				} else {
					negImages.add(new IntegralImage(img, label, 0));
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
		
		doMeanAndNormalization(posImages);
		doMeanAndNormalization(negImages);
		
		double posWeight = (double)1 / (2 * posImages.size()), negWeight = (double)1 / (2 * negImages.size());
		for(IntegralImage iim : posImages) {
			iim.setWeight(posWeight);
		}
		for(IntegralImage iim : negImages) {
			iim.setWeight(negWeight);
		}
		
		Map<Integer, List<IntegralImage>> resultMap = new HashMap<Integer, List<IntegralImage>>();
		resultMap.put(1, posImages);
		resultMap.put(0, negImages);
		return resultMap;
	}

	public static List<IntegralImage> loadTrainDatas(String fileName, int width, int height) {
		List<IntegralImage> images = new ArrayList<IntegralImage>();
		Map<Integer, List<IntegralImage>> dmap = loadTrainDatasSeparate(fileName, width, height);
		List<IntegralImage> posDatas = dmap.get(1);
		List<IntegralImage> negDatas = dmap.get(0);
		images.addAll(posDatas);
		images.addAll(negDatas);
		return images;
	}

	// 均值归一化
	private static void doMeanAndNormalization(List<IntegralImage> datas) {
		if (datas == null || datas.isEmpty()) {
			return;
		}
		Iterator<IntegralImage> it= datas.iterator();
		while (it.hasNext()) {
			IntegralImage iim = it.next();
			double mean = 0, var = 0;
			double[][] img = iim.getImage();
			double size = img.length * img[0].length;

			for (int i = 0; i < img.length; i++) {
				for (int j = 0; j < img[i].length; j++) {
					mean += img[i][j];
					var += img[i][j] * img[i][j];
				}
			}
			mean = mean / size;
			var = Math.sqrt((var / size) - (mean * mean));
			
			if(var == 0) {
				it.remove();
				continue;
			} else{
				for (int i = 0; i < img.length; i++) {
					for (int j = 0; j < img[i].length; j++) {
						img[i][j] = (img[i][j] - mean) / var;
					}
				}
			}
		}
	}
	
	// 导出模型
	public static void exportModel(String file, List<String> model) {
		if (model != null && !model.isEmpty()) {
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file)));
				for (String line : model) {
					out.write(line + "\n");
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
