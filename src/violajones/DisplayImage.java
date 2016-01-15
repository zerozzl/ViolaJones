package violajones;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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

	public static void display(IntegralImage iim) {
		double[][] img = iim.getImage();
		int[] imgL = new int[img.length * img[0].length];
		int index = 0;
		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[i].length; j++) {
				imgL[index++] = new Color((int) img[i][j], (int) img[i][j], (int) img[i][j]).getRGB();
			}
		}
		
		final int[] drawImg = imgL;
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				DisplayImage ex = new DisplayImage(24, 24, drawImg);
				ex.setVisible(true);
			}
		});
	}

}