package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.BounceableRect;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import android.content.Context;

public class Wall extends BounceableRect
{
	protected static final int LINES = 10;
	protected static final float RAND_MIN_SIZE = 10 * ITEM_SCALE;
	protected static final float RAND_MAX_SIZE = 20 * ITEM_SCALE;
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
		
		wallside1 = new RectMesh(0, (float)-(this.size.x / 2 - this.size.y / 2), (float)this.size.y, (float)this.size.y, R.drawable.wallside);
		wallside2 = new RectMesh(0, (float)+(this.size.x / 2 - this.size.y / 2), (float)this.size.y, (float)this.size.y, R.drawable.wallside);
		wallside2.setRotation(180);
	}

	@Override
	public void draw(GL10 gl, float worldZoom)
	{
		gl.glPushMatrix();
		gl.glTranslatef((float)-pos.x, (float)-pos.y, 0);
		gl.glRotatef(rotation, 0, 0, 1);
		
		wallside1.draw(gl);
		wallside2.draw(gl);
		
		/* final Coord start = new Coord(pos.x - (size.x / 2) + size.y, pos.y);
		final Coord fin   = new Coord(pos.x + (size.x / 2) - size.y, pos.y);
		for(int i = 0; i < LINES; i++)
		{
			int currPos = 0;
			double prevPosX = start.x;
			double prevPosY = start.y;
			while(currPos + RAND_MAX_SIZE < fin.x - start.x)
			{
				final float posHeight = (float) (rGen.nextFloat() * size.y);
				final float posWidth = rGen.nextFloat() * (RAND_MAX_SIZE - RAND_MIN_SIZE) + RAND_MIN_SIZE;
				
				currPos += posWidth;
				
				c.drawLine(
						(float)(prevPosX) * worldZoom,
						(float)(prevPosY) * worldZoom,
						(float)(prevPosX + posWidth) * worldZoom,
						(float)(pos.y - (size.y / 2) + posHeight) * worldZoom,
						PaintLoader.load(wallPaint));
				prevPosX = prevPosX + posWidth;
				prevPosY = pos.y - (size.y / 2) + posHeight;
			}
			c.drawLine(
					(float)prevPosX * worldZoom,
					(float)prevPosY * worldZoom,
					(float)fin.x * worldZoom,
					(float)fin.y * worldZoom,
					PaintLoader.load(wallPaint));
		}
		
		c.rotate(-rotation, (float)pos.x * worldZoom, (float)pos.y * worldZoom); */
		gl.glPopMatrix();
	}
}
