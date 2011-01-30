package pennygame.projector;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JPanel;

import pennygame.projector.queues.PSConn;

public abstract class Graph extends JPanel {

	private static final long serialVersionUID = 2433949872026867217L;
	
	protected static final float SCALE = 1;
	protected static final int G_BORDER = 40;
	protected static final Stroke G_BORDER_WIDTH = new BasicStroke(1);
	
	protected static final Color BG = new Color(255, 255, 255);
	protected static final Color BORDER = new Color(0, 0, 0);
	protected static final Color LABEL = new Color(50, 50, 50);
	protected static final Color GRIDLINES= new Color(150, 150, 150);
	
	protected PSConn serv;

	public Graph(PSConn serv) {
		this.serv = serv;
		setMinimumSize(new Dimension(400, 300));
		setPreferredSize(new Dimension(500, 400));
	}
	
	private Dimension size = new Dimension(100, 100);
	
	@Override
	public void paint(Graphics g1) {
		super.paint(g1);
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		size = getSize();
		
		g.scale(SCALE, SCALE);
		size.width /= SCALE;
		size.height /= SCALE;
		
		g.setPaint(BG);
		g.fillRect(0, 0, size.width * 4, size.height * 4);
		
		{ // Graph borders
			g.setPaint(BORDER);
			g.setStroke(G_BORDER_WIDTH);
			g.drawLine(G_BORDER, G_BORDER, G_BORDER, size.height - G_BORDER);
			g.drawLine(G_BORDER, size.height - G_BORDER, size.width - G_BORDER, size.height - G_BORDER);
		}
		
		{ // Labels
			g.rotate(-Math.PI / 2, G_BORDER / 2, size.height / 2);
			g.setFont(g.getFont().deriveFont(14f));
			g.setPaint(LABEL);
			g.drawString(getYAxisLabel(), G_BORDER / 2, size.height / 2);
			g.rotate(Math.PI / 2, G_BORDER / 2, size.height / 2);
			
			g.drawString(getXAxisLabel(), size.width / 2, size.height - (G_BORDER / 4) + 5);
		}
		
		{ // Units
			// Y
			for(long i = top; i >= bottom; i += Math.min((bottom - top) / 4, -10)) {
				float gy = (float) graphToUserY(i);
				
				g.setPaint(LABEL);
				g.drawString("" + (int)i, G_BORDER / 3, gy + 5);
				
				g.setPaint(GRIDLINES);
				g.drawLine(G_BORDER, (int)gy, size.width - G_BORDER, (int)gy);
			}
			
			// X
			
			// Simply to only draw half of time values
			int count = 0;
			for(long j = left; j <= right; j += Math.max((long)(right - left) / 16, 10000)) {
				float gx = (float) graphToUserX(j);
				
				if(count == 0 || count > 10) {
					g.setPaint(LABEL);
					g.drawString(getXAxisLineText(j), gx - 15, size.height - (G_BORDER / 2));
				}
				
				g.setPaint(GRIDLINES);
				g.drawLine((int)gx, (int)size.height - G_BORDER, (int)gx, G_BORDER);
				
				count++;
			}
		}
		
		drawData(g);
	}
	
	/**
	 * Converts coords from the graph to coords as they should be in userspace. It uses values from {@link #setGraphBounds(float, float, float, float)}.
	 * @param d
	 * @return
	 */
	protected Dimension graphToUserCoords(Dimension d) {
		float x = (float)(d.width - left) / (float)(right - left); // From 0 to 1
		if(x < 0 || x > 1) return null;
		float y = (float)(d.height - top) / (float)(bottom - top); // From 0 to 1
		if(y > 0 || y < -1) return null;
		return new Dimension((int)(x * (size.width - (2 * G_BORDER)) + G_BORDER), (int)(y * (size.height - (2 * G_BORDER)) + G_BORDER));
	}
	
	/**
	 * Converts X coord from the graph to X coord as it should be in userspace. It uses values from {@link #setGraphBounds(float, float, float, float)}.
	 * @param d
	 * @return
	 */
	protected float graphToUserX(float d) {
		double x = (double)(d - left) / (float)(right - left); // From 0 to 1
		if(x < 0 || x > 1) return -5;
		x = Math.pow(10, x * 3); // Between 1 and 1000
		x -= 1;
		x /= 999; // 0 and 1
		return (float)x * (size.width - (2 * G_BORDER)) + G_BORDER;
	}
	
	/**
	 * Converts Y coord from the graph to Y coord as it should be in userspace. It uses values from {@link #setGraphBounds(float, float, float, float)}.
	 * @param d
	 * @return
	 */
	protected float graphToUserY(float d) {
		float y = (float)(d - top) / (float)(bottom - top); // From 0 to 1
		if(y < 0 || y > 1) return -5;
		return y * (size.height - (2 * G_BORDER)) + G_BORDER;
	}
	
	/**
	 * Converts X coord from the graph to X coord as it should be in userspace. It uses values from {@link #setGraphBounds(float, float, float, float)}.
	 * @param d
	 * @return
	 */
	protected double graphToUserX(long d) {
		double x = (double)(d - left) / (double)(right - left); // From 0 to 1
		if(x < 0 || x > 1) return -5;
		x = Math.pow(10, x * 3); // Between 1 and 1000
		x -= 1;
		x /= 999; // 0 and 1
		return x * (size.width - (2 * G_BORDER)) + G_BORDER;
	}
	
	/**
	 * Converts Y coord from the graph to Y coord as it should be in userspace. It uses values from {@link #setGraphBounds(float, float, float, float)}.
	 * @param d
	 * @return
	 */
	protected double graphToUserY(long d) {
		double y = (double)(d - top) / (double)(bottom - top); // From 0 to 1
		if(y < 0 || y > 1) return -5;
		return y * (size.height - (2 * G_BORDER)) + G_BORDER;
	}
	
	
	private long left = 100, top = 200, bottom = 100, right = 200;
	
	protected void setGraphBounds(long x1, long y1, long x2, long y2) {
		if(x1 > x2) {
			long n = x1;
			x1 = x2;
			x2 = n;
		}
		if(y1 > y2) {
			long n = y1;
			y1 = y2;
			y2 = n;
		}
		
		left = x1;
		top = y2;
		bottom = y1;
		right = x2;
	}
	
	protected abstract String getXAxisLabel();
	protected abstract String getYAxisLabel();
	
	/**
	 * Gets the text to label the X axis with. The point is between those specified in {@link #setGraphBounds(float, float, float, float)}.
	 * @param pointOnLine
	 * @return
	 */
	protected abstract String getXAxisLineText(long pointOnLine);
	
	protected abstract void drawData(Graphics2D g);
}
