package violajones;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

class Surface extends JPanel {

	private static final long serialVersionUID = 1L;
	private BufferedImage img;

	public Surface(int w, int h, int[] imgData) {
		img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		img.setRGB(0, 0, w, h, imgData, 0, w);
		
		Dimension d = new Dimension();
		d.width = img.getWidth(null);
		d.height = img.getHeight(null);
		setPreferredSize(d);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(img, 0, 0, null);
	}

}

public class DisplayImage extends JFrame {

	private static final long serialVersionUID = 1L;

	public DisplayImage(int w, int h, int[] drawImg) {
		add(new Surface(w, h, drawImg));
		pack();
		setTitle("Viola-Jones");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static int[] loadDatas(String fileName, int width, int height) {
		double[][] img = new double[width][height];
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			boolean firstRow = true;
			while ((tempString = reader.readLine()) != null) {
				if (firstRow) {
					firstRow = false;
				} else {
					String[] data = tempString.split(",");
					int index = 0;
					for (int i = 0; i < width; i++) {
						for (int j = 0; j < height; j++) {
							img[i][j] = Float.parseFloat(data[index++]);
						}
					}
					break;
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

		int[] drawImg = new int[576];
		int index = 0;
		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[i].length; j++) {
				drawImg[index++] = new Color((int) img[i][j], (int) img[i][j], (int) img[i][j]).getRGB();
			}
		}

		return drawImg;
	}

	public static void main(String[] args) {
		final int[] drawImg = loadDatas(
				"E:/TestDatas/MLStudy/FaceDection/MIT/MIT_DATA_DEV.txt", 24, 24);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				DisplayImage ex = new DisplayImage(24, 24, drawImg);
				ex.setVisible(true);
			}
		});
	}

}