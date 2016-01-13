package violajones;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 特征模版
 */
public class FeatureTemplate implements Serializable {
	
	private static final long serialVersionUID = 2974614978200644745L;
	private String type;
	private int angle;
	private int w;
	private int h;
	
	public FeatureTemplate(String t, int ang, int w, int h) {
		this.type = t;
		this.angle = ang;
		this.w = w;
		this.h = h;
	}

	public String getType() {
		return type;
	}

	public int getAngle() {
		return angle;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}
	
	// 初始化特征模板
	public static List<FeatureTemplate> initFeaTemplates() {
		/*
		 *  具体特征模板形状，请参考论文: 
		 *  An Extended Set of Haar-like Features for Rapid Object Detection
		 */
		List<FeatureTemplate> templates = new ArrayList<FeatureTemplate>();
		templates.add(new FeatureTemplate("1a", 0, 2, 1));
		templates.add(new FeatureTemplate("1b", 0, 1, 2));
		templates.add(new FeatureTemplate("1c", 45, 2, 1));
		templates.add(new FeatureTemplate("1d", 45, 1, 2));
		templates.add(new FeatureTemplate("2a", 0, 3, 1));
		templates.add(new FeatureTemplate("2b", 0, 4, 1));
		templates.add(new FeatureTemplate("2c", 0, 1, 3));
		templates.add(new FeatureTemplate("2d", 0, 1, 4));
		templates.add(new FeatureTemplate("2e", 45, 3, 1));
		templates.add(new FeatureTemplate("2f", 45, 4, 1));
		templates.add(new FeatureTemplate("2g", 45, 1, 3));
		templates.add(new FeatureTemplate("2h", 45, 1, 4));
		templates.add(new FeatureTemplate("3a", 0, 3, 3));
		templates.add(new FeatureTemplate("3b", 45, 3, 3));
		return templates;
	}
	
}
