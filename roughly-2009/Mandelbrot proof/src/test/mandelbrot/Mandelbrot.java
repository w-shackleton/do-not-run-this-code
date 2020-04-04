package test.mandelbrot;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

import org.apache.commons.math.complex.Complex;

/**
 * A better written version of the Mandelbrot fractal in less than 50 lines.
 * @author Will Shackleton<w.shackleton@gmail.com>
 */
public class Mandelbrot extends JFrame {
	private static final long serialVersionUID = 8977784323704785899L;
	protected static final Complex TWO = new Complex(2, 0);
	
	public Mandelbrot() {
		setSize(900, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int width = getWidth(); int height = getHeight();
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				int iterations = mandelPoint((float)x / (float)(width / 3) - 2, (float)y / (float)(height / 2) - 1);
				g.setColor(new Color(Math.min(iterations * 10, 255), 0, 0));
				g.drawLine(x, y, x, y);
			}
		}
	}
	
	protected int mandelPoint(double x, double y) {
		Complex c = new Complex(x, y); // Parameter
		Complex z = new Complex(0, 0); // Iteration position
		int i = 0; // Iteration count
		for(; z.getReal() * z.getReal() + z.getImaginary() * z.getImaginary() < 4; i++) { // If abs(z) < 2
			if(i > 400) return 0; // Escape condition
			z = z.pow(TWO); // z[n+1] = z[n]^2 + c
			if(z.isNaN()) z = c; // 0 pow 2 results in NaN, so check for this case.
			else z = z.add(c);
		}
		return i; // Number of iterations.
	}
}
