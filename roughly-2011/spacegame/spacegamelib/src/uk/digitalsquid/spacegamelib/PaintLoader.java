package uk.digitalsquid.spacegamelib;

import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

/**
 * This class allows for dynamic changing of paints, ie changing of antialiasing.
 * Each paint can only be used once, since the same object is recycled for all drawing.
 * This is mainly to reduce GC cleanups needed.
 * @author william
 *
 */
public final class PaintLoader
{
	private static final Paint p = new Paint();
	public static final synchronized Paint load(PaintDesc desc)
	{
		//Paint p = new Paint();
		
		p.setARGB(desc.a, desc.r, desc.g, desc.b);
		p.setAntiAlias(StaticInfo.Antialiasing);
		p.setStrokeWidth(desc.stroke);
		p.setTextSize(desc.textSize);
		p.setStyle(desc.style);
		p.setTextAlign(Align.CENTER);
		
		return p;
	}
	public static final class PaintDesc
	{
		public int a, r, g, b;
		public float stroke;
		public float textSize;
		public Style style;
		
		public PaintDesc(int r, int g, int b)
		{
			this.a = 255;
			this.r = r;
			this.g = g;
			this.b = b;
			this.stroke = 1;
			this.textSize = 16;
			this.style = Style.FILL;
		}
		
		public PaintDesc(int a, int r, int g, int b)
		{
			this.a = 255;
			this.r = r;
			this.g = g;
			this.b = b;
			this.stroke = 1;
			this.textSize = 16;
			this.style = Style.FILL;
		}
		
		public PaintDesc(int a, int r, int g, int b, float stroke)
		{
			this.a = a;
			this.r = r;
			this.g = g;
			this.b = b;
			this.stroke = stroke;
			this.textSize = 16;
			this.style = Style.FILL;
		}
		
		public PaintDesc(int a, int r, int g, int b, float stroke, Style style)
		{
			this.a = a;
			this.r = r;
			this.g = g;
			this.b = b;
			this.stroke = stroke;
			this.textSize = 16;
			this.style = style;
		}
		
		public PaintDesc(int a, int r, int g, int b, float stroke, float textSize)
		{
			this.a = a;
			this.r = r;
			this.g = g;
			this.b = b;
			this.stroke = stroke;
			this.textSize = textSize;
			this.style = Style.FILL;
		}
	}
}
