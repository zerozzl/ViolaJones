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
			int line = 0;
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
				if (label == 1) {
					posImages.add(new IntegralImage(line++, img, label, 0));
				} else {
					negImages.add(new IntegralImage(line++, img, label, 0));
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

		double posWeight = (double) 1 / (2 * posImages.size()), negWeight = (double) 1
				/ (2 * negImages.size());
		for (IntegralImage iim : posImages) {
			iim.setWeight(posWeight);
		}
		for (IntegralImage iim : negImages) {
			iim.setWeight(negWeight);
		}

		Map<Integer, List<IntegralImage>> resultMap = new HashMap<Integer, List<IntegralImage>>();
		resultMap.put(1, posImages);
		resultMap.put(0, negImages);
		return resultMap;
	}

	public static List<IntegralImage> loadTrainDatas(String fileName,
			int width, int height) {
		List<IntegralImage> images = new ArrayList<IntegralImage>();
		Map<Integer, List<IntegralImage>> dmap = loadTrainDatasSeparate(
				fileName, width, height);
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
		Iterator<IntegralImage> it = datas.iterator();
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

			if (var == 0) {
				it.remove();
				continue;
			} else {
				for (int i = 0; i < img.length; i++) {
					for (int j = 0; j < img[i].length; j++) {
						img[i][j] = (img[i][j] - mean) / var;
					}
				}
			}
		}
	}

	// 导出模型
	public static void exportFile(String file, List<String> contents) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file)));
			if (contents != null && !contents.isEmpty()) {
				for (String line : contents) {
					out.write(line + "\n");
				}
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
