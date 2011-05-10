package uk.digitalsquid.spacegame.spaceitem.items;

import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.misc.Lines;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.BounceableRect;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import android.content.Context;

public class Wall extends BounceableRect implements Moveable
{
	protected static final int LINES = 10;
	protected static final float GAP_WIDTH = 15;
	protected static final Random rGen = new Random();
	
	protected static final float BOUNCINESS = 0.7f;
	
	private RectMesh wallside1, wallside2;
	
	protected static final PaintDesc wallPaint = new PaintDesc(20, 100, 40);
	
	protected static final int WALL_WIDTH = 16;
	protected static final int WALL_MIN_X = 80;
	protected static final int WALL_MAX_X = 1000;
	
	/**
	 * Construct a new {@link Wall}.
	 * @param context
	 * @param coord
	 * @param size The size of the wall
	 * @param rotation The rotation of this object, in DEGREES
	 * @param bounciness
	 */
	public Wall(Context context, Coord coord, float size, float rotation)
	{
		super(context, coord, new Coord(CompuFuncs.TrimMinMax(size, WALL_MIN_X, WALL_MAX_X), WALL_WIDTH), rotation, BOUNCINESS);
		
		wallside1 = new RectMesh((float)-(this.size.x / 2 - this.size.y / 2), 0, (float)this.size.y, (float)this.size.y, R.drawable.wallside);
		wallside2 = new RectMesh((float)+(this.size.x / 2 - this.size.y / 2), 0, (float)this.size.y, (float)this.size.y, R.drawable.wallside);
		wallside2.setRotation(180);
		
		for(int i = 0; i < lines.length; i++) {
			lines[i] = new Lines(0, 0, new float[(int)((this.size.x - this.size.y * 2) / GAP_WIDTH) * 3 + 6], GL10.GL_LINE_STRIP, 0.1f, 0.5f, 0.2f, 1);
		}
	}
	
	protected final Lines[] lines = new Lines[LINES];

	@Override
	public void draw(GL10 gl, float worldZoom)
	{
		gl.glPushMatrix();
		gl.glTranslatef((float)pos.x, (float)pos.y, 0);
		gl.glRotatef(rotation, 0, 0, 1);
		
		wallside1.draw(gl);
		wallside2.draw(gl);
		
		for(Lines line : lines) {
			line.draw(gl);
		}
		
		gl.glPopMatrix();
	}
	
	private final float[] tmp = {0, 0, 0};

	@Override
	public void drawMove(float millistep, float speedScale) {
		final float startx = (float) (- (size.x / 2) + size.y);
		final float finx   = (float) (+ (size.x / 2) - size.y);
		final float starty = 0;
		
		for(Lines line : lines)
		{
			FloatBuffer buf = line.getVertices();
			
			tmp[0] = startx;
			tmp[1] = starty;
			buf.put(tmp);
			
			float currPos = startx;
			while(currPos + GAP_WIDTH < finx)
			{
				//final float posHeight = (float) (rGen.nextFloat() * size.y);
				currPos += GAP_WIDTH;
				
				// Set vals
				tmp[0] = currPos;
				tmp[1] = (float) ((rGen.nextFloat() * size.y) - size.y / 2);
				buf.put(tmp);
			}
			tmp[0] = finx;
			tmp[1] = starty;
			buf.put(tmp);
			
			buf.position(0);
		}
		
	}

	@Override
	public void move(float millistep, float speedScale) { }
}
