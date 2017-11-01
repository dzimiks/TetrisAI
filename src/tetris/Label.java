package tetris;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class Label {
	
	public JLabel draw;
	
	public static final Color BLACK = Color.BLACK;
	public static final Color WHITE = Color.WHITE;

	// Default boje
	public static final Color DEFAULT_PEN_COLOR = BLACK;
	public static final Color DEFAULT_CLEAR_COLOR = WHITE;

	// Trenutna boja linija
	private static Color penColor;

	// Default velicina canvasa je SIZE x SIZE
	static final int SIZE = 512; 
	public int width  = SIZE;
	private int height = SIZE;

	// Default debljina linija
	private static final double DEFAULT_PEN_RADIUS = 0.002; // Perfektno se uklapa

	// Trenutna debljina linija
	private static double penRadius;

	// Canvas border
	public double BORDER = 0.00;
	
	private static final double DEFAULT_XMIN = 0.0;
	private static final double DEFAULT_XMAX = 1.0;
	private static final double DEFAULT_YMIN = 0.0;
	private static final double DEFAULT_YMAX = 1.0;
	public double xmin, ymin, xmax, ymax;

	// Default font
	private final Font DEFAULT_FONT = new Font("SansSerif", Font.BOLD, 35);

	// Trenutni font
	private static Font font;

	private BufferedImage offscreenImage, onscreenImage;
	protected Graphics2D offscreen, onscreen;
	
	public Label(int w, int h) {
		width = w;
		height = h;
		offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		onscreenImage  = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		offscreen = offscreenImage.createGraphics();
		onscreen  = onscreenImage.createGraphics();
		
		setXscale();
		setYscale();
		
		offscreen.setColor(DEFAULT_CLEAR_COLOR);
		offscreen.fillRect(0, 0, width, height);
		
		setPenColor();
		setPenRadius();
		setFont();
		clear();

		RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
												  RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		offscreen.addRenderingHints(hints);

		ImageIcon icon = new ImageIcon(onscreenImage);
		draw = new JLabel(icon);
	}
	
	// Menja koordinatni sistem
	public void setXscale() { 
		setXscale(DEFAULT_XMIN, DEFAULT_XMAX); 
	}
	
	public void setYscale() { 
		setYscale(DEFAULT_YMIN, DEFAULT_YMAX); 
	}
	
	public void setXscale(double min, double max) {
		double size = max - min;
		xmin = min - BORDER * size;
		xmax = max + BORDER * size;
	}

	public void setYscale(double min, double max) {
		double size = max - min;
		ymin = min - BORDER * size;
		ymax = max + BORDER * size;
	}

	// Pomocne metode koje skaliraju korisnikove koordinate u koordinate ekrana
	protected double scaleX (double x) { 
		return width  * (x - xmin) / (xmax - xmin); 
	}
	
	protected double scaleY (double y) { 
		return height * (ymax - y) / (ymax - ymin); 
	}
	
	// Cisti ekran odredjenom bojom
	public void clear() { 
		clear(DEFAULT_CLEAR_COLOR);
	}
	
	public void clear(Color color) {
		offscreen.setColor(color);
		offscreen.fillRect(0, 0, width, height);
		offscreen.setColor(penColor);
	}

	public void setPenRadius() { 
		setPenRadius(DEFAULT_PEN_RADIUS);
	}
	
	public void setPenRadius(double r) {
		penRadius = r * SIZE;
		BasicStroke stroke = new BasicStroke((float)penRadius);
		offscreen.setStroke(stroke);
	}

	public void setPenColor() {
		setPenColor(DEFAULT_PEN_COLOR); 
	}
	
	public void setPenColor(Color color) {
		penColor = color;
		offscreen.setColor(penColor);
	}

	public void setFont() {
		font = DEFAULT_FONT;
	}
	
	public void add(Container frame, String spot) {
		frame.add(draw, spot);
	}
	
	// Teska matematika...
	public void line(double x0, double y0, double x1, double y1) {
		offscreen.draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));
	}

	public void polygon(double[] x, double[] y) {
		int n = x.length;
		GeneralPath path = new GeneralPath();
		path.moveTo((float)scaleX(x[0]), (float)scaleY(y[0]));

		for (int i = 0; i < n; i++)
			path.lineTo((float)scaleX(x[i]), (float)scaleY(y[i]));

		path.closePath();
		offscreen.draw(path);
	}

	public void filledPolygon(double[] x, double[] y) {
		int n = x.length;
		GeneralPath path = new GeneralPath();
		path.moveTo((float)scaleX(x[0]), (float)scaleY(y[0]));
		
		for (int i = 0; i < n; i++)
			path.lineTo((float)scaleX(x[i]), (float)scaleY(y[i]));
		
		path.closePath();
		offscreen.fill(path);
	}

	public void rectangleLL(double x, double y, double w, double h) {
		double[] xarray = {x, x, x + w, x + w};
		double[] yarray = {y, y + h, y + h, y};
		polygon(xarray, yarray);
	}

	public void filledRectangleLL(double x, double y, double w, double h, Color c) {
		double[] xarray = {x, x, x + w, x + w};
		double[] yarray = {y, y + h, y + h, y};
		
		setPenColor(c);
		filledPolygon(xarray, yarray);
		setPenColor(DEFAULT_PEN_COLOR);
	}
	
	// Ispis teksta na sredini ekrana
	public void text(double x, double y, String s) {
		offscreen.setFont(font);
		FontMetrics metrics = offscreen.getFontMetrics();
		double xs = scaleX(x);
		double ys = scaleY(y);
		int ws = metrics.stringWidth(s);
		int hs = metrics.getDescent();
		offscreen.drawString(s, (float)(xs - ws / 2.0), (float)(ys + hs));
	}
	
	public void show() {
		onscreen.drawImage(offscreenImage, 0, 0, null);
		
		try {
			draw.repaint();
		}
		catch(NullPointerException e) {
			e.printStackTrace();
		}
	}
}