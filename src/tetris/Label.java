package tetris;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.*;


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
	private static final double DEFAULT_PEN_RADIUS = 0.002;

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
	
	public double factorX(double w) { 
		return w * width  / Math.abs(xmax - xmin);  
	}
	
	public double factorY(double h) {
		return h * height / Math.abs(ymax - ymin); 
	}
	
	public double userX(double x) { 
		return xmin + x * (xmax - xmin) / width; 
	}
	
	public double userY(double y) {
		return ymax - y * (ymax - ymin) / height; 
	}

	public void showInFrame() {
		JFrame j = new JFrame();
		j.setTitle("Konfiguracija");
		j.setContentPane(this.draw);
		j.setVisible(true);
		j.pack();
		show();
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

	public void clear(double x1, double x2, double y1, double y2) {
		clear(DEFAULT_CLEAR_COLOR, x1, x2, y1, y2);  
	}
	
	public void clear(Color color, double x1, double x2, double y1, double y2) {
		int ix1 = (int) scaleX(x1);
		int ix2 = (int) scaleX(x2);
		int iy1 = (int) scaleY(y1);
		int iy2 = (int) scaleY(y2);

		offscreen.setColor(color);
		offscreen.fillRect(ix1, iy1, ix2, iy2);
		offscreen.setColor(penColor);
		//show();
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
		setFont(DEFAULT_FONT); 
	}
	
	public void setFont(Font f) { 
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		double x = toolkit.getScreenSize().getWidth();
		double y = toolkit.getScreenSize().getHeight();
		double xscale = x / 1400.0;
		double yscale = y / 1050.0;
		double scale = Math.sqrt((xscale * xscale + yscale * yscale) / 2);

		font = f.deriveFont((float)(f.getSize() * scale));
	}
	
	public void add(Container frame, String spot) {
		frame.add(draw, spot);
	}
	
	public void addML(MouseListener frame) {
		draw.addMouseListener(frame);
	}
	
	public void addMML(MouseMotionListener frame) {
		draw.addMouseMotionListener(frame);
	}
	
	public void addKL(KeyListener frame) {
		draw.addKeyListener(frame);
	}
	
	public void addMWL(MouseWheelListener frame) {
		draw.addMouseWheelListener(frame);
	}
	
	public void remML(MouseListener frame) {
		draw.removeMouseListener(frame);
	}
	
	public void remMML(MouseMotionListener frame) {
		draw.removeMouseMotionListener(frame);
	}
	
	public void remKL(KeyListener frame) {
		draw.removeKeyListener(frame);
	}
	
	public void remMWL(MouseWheelListener frame) {
		draw.removeMouseWheelListener(frame);
	}
	

	// Teska matematika...
	public void line(double x0, double y0, double x1, double y1) {
		offscreen.draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));
	}

	public void pixel(double x, double y) {
		offscreen.fillRect((int)Math.round(scaleX(x)), (int)Math.round(scaleY(y)), 1, 1);
	}
	
	public void pixelP(double x, double y) {
		offscreen.fillRect((int)Math.round(x), (int)Math.round(y), 1, 1);
	}
	
	public void pixelP(double x, double y, Color c) {
		setPenColor(c);
		offscreen.fillRect((int)Math.round(x), (int)Math.round(y), 1, 1);
	}

	public void point(double x, double y) {
		double xs = scaleX(x);
		double ys = scaleY(y);
		double r = penRadius;

		if (r <= 1)
			pixel(x, y);
		else 
			offscreen.fill(new Ellipse2D.Double(xs - r / 2, ys - r / 2, r, r));
	}

	public void arc(double x, double y, double r, double startAngle, double arcRange) {
		double xs = scaleX(x);
		double ys = scaleY(y);
		double ws = factorX(2 * r);
		double hs = factorY(2 * r);
		
		if (ws <= 1 && hs <= 1) 
			pixel(x, y);
		else 
			offscreen.draw(new Arc2D.Double(xs - ws / 2, ys - hs / 2, ws, hs, startAngle, arcRange, Arc2D.OPEN));
	}
	
	public void circle(double x, double y, double r) {
		double xs = scaleX(x);
		double ys = scaleY(y);
		double ws = factorX(2 * r);
		double hs = factorY(2 * r);
		
		if (ws <= 1 && hs <= 1) 
			pixel(x, y);
		else 
			offscreen.draw(new Ellipse2D.Double(xs - ws / 2, ys - hs / 2, ws, hs));
	}
	
	public void circleP(double x, double y, double r, Color col) {
		setPenColor(col);
		circleP(x, y, r);
	}
	
	public void circleP(double x, double y, double r) {
		double ws = 2 * r;
		double hs = 2 * r;
		double xs = scaleX(x);
		double ys = scaleY(y);

		if (ws <= 1 && hs <= 1) 
			pixel(x, y);
		else 
			offscreen.draw(new Ellipse2D.Double(xs - ws / 2, ys - hs /2 , ws, hs));
	}
	
	public void circle(double x, double y, double r, Color color) {
		setPenColor(color);
		circle(x,y,r);
	}
	public void filledCircle(double x, double y, double r) {
		double xs = scaleX(x);
		double ys = scaleY(y);
		double ws = factorX(2 * r);
		double hs = factorY(2 * r);
		
		if (ws <= 1 && hs <= 1)
			pixel(x, y);
		else 
			offscreen.fill(new Ellipse2D.Double(xs - ws / 2, ys - hs / 2, ws, hs));
	}
	
	public void filledCircleP(double x, double y, double r) {
		double ws = 2 * r;
		double hs = 2 * r;
		
		if (ws <= 1 && hs <= 1) 
			pixel(x, y);
		else 
			offscreen.fill(new Ellipse2D.Double(x - ws / 2, y - hs / 2, ws, hs));
	}

	public void square(double x, double y, double r) {
		double xs = scaleX(x);
		double ys = scaleY(y);
		double ws = factorX(2 * r);
		double hs = factorY(2 * r);
		
		if (ws <= 1 && hs <= 1) 
			pixel(x, y);
		else
			offscreen.draw(new Rectangle2D.Double(xs - ws / 2, ys - hs / 2, ws, hs));
	}

	public void filledSquare(double x, double y, double r) {
		double xs = scaleX(x);
		double ys = scaleY(y);
		double ws = factorX(2 * r);
		double hs = factorY(2 * r);
		
		if (ws <= 1 && hs <= 1) 
			pixel(x, y);
		else 
			offscreen.fill(new Rectangle2D.Double(xs - ws / 2, ys - hs / 2, ws, hs));
	}

	public void Arrow(double x, double y, double w, double h, double Scale, Color Color) {
		rectangle(x, y, w, h, Color);
		double[] xarray = {x + w / 2 - w / 10 + Scale * 1 / 10 * Math.sqrt(h * h * 25 + w * w) * 
						  Math.sqrt(3) / 3, x + w / 2 - w / 10, x + w / 2 - w / 10};
		double[] yarray = {y, y + Scale * 1 / 10 * Math.sqrt(h * h * 25 + w * w) * Math.sqrt(2) / 2,
						   y - Scale * 1 / 10 * Math.sqrt(h * h * 25 + w * w) * Math.sqrt(2) / 2};
		
		setPenColor(Color);
		filledPolygon(xarray, yarray);
		setPenColor();
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

	public void polygonP(double[] x, double[] y) {
		int n = x.length;
		GeneralPath path = new GeneralPath();
		path.moveTo((float)x[0], (float)y[0]);

		for (int i = 0; i < n; i++)
			path.lineTo((float)x[i], (float)y[i]);

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

	public void filledPolygonP(double[] x, double[] y) {
		int n = x.length;
		GeneralPath path = new GeneralPath();
		path.moveTo((float)x[0], (float)y[0]);
		
		for (int i = 0; i < n; i++)
			path.lineTo((float)x[i], (float)y[i]);
		
		path.closePath();
		offscreen.fill(path);
	}

	public void rectangle(double x, double y, double w, double h) {
		double[] xarray = {x - w / 2, x - w / 2, x + w / 2, x + w / 2};
		double[] yarray = {y - h / 2, y + h / 2, y + h / 2, y - h / 2};
		polygon(xarray, yarray);
	}
	
	public void rectangleLL(double x, double y, double w, double h) {
		double[] xarray = {x, x ,x + w, x + w};
		double[] yarray = {y, y + h, y + h, y};
		polygon(xarray, yarray);
	}
	
	public void rectangleP(double x, double y, double w, double h) {
		double[] xarray = {x, x, x + w, x + w};
		double[] yarray = {y, y + h, y + h, y};
		polygonP(xarray, yarray);
	}

	public void rectangle(double x, double y, double w, double h, Color c) {
		double[] xarray = {x - w / 2, x - w / 2, x + w / 2, x + w / 2};
		double[] yarray = {y - h / 2, y + h / 2, y + h / 2, y - h / 2};
		
		setPenColor(c);
		filledPolygon(xarray, yarray);
		setPenColor(DEFAULT_PEN_COLOR);
	}
	
	public void rectangleC(double x, double y, double w, double h, Color c) {
		double[] xarray = {x, x, x + w, x + w};
		double[] yarray = {y, y + h, y + h, y};
		
		setPenColor(c);
		filledPolygon(xarray, yarray);
		setPenColor(DEFAULT_PEN_COLOR);
	}

	public void filledRectangleP(double x, double y, double w, double h, Color c) {
		double[] xarray = {x, x, x + w, x + w};
		double[] yarray = {y, y + h, y + h, y};
		
		setPenColor(c);
		filledPolygonP(xarray, yarray);
		setPenColor(DEFAULT_PEN_COLOR);
	}
	
	public void filledRectangleLL(double x, double y, double w, double h, Color c) {
		double[] xarray = {x, x, x + w, x + w};
		double[] yarray = {y, y + h, y + h, y};
		
		setPenColor(c);
		filledPolygon(xarray, yarray);
		setPenColor(DEFAULT_PEN_COLOR);
	}

	public void rectangle(double x, double y, double w, double h, Color c,boolean Border, Color BorderColor) {
		double[] xarray = {x - w / 2, x - w / 2, x + w / 2, x + w / 2};
		double[] yarray = {y - h / 2, y + h / 2, y + h / 2, y - h / 2};
		
		if (c != null) {
			setPenColor(c);
			filledPolygon(xarray, yarray);
		}
		
		setPenColor(BorderColor);
		
		if (Border) 
			polygon(xarray, yarray);
		
		setPenColor();
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