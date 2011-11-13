package uk.digitalsquid.spacegame.spaceitem.items;

import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.assistors.Spring;
import uk.digitalsquid.spacegame.spaceitem.blocks.BlockVortex;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.Constants;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.gl.Bezier;
import uk.digitalsquid.spacegamelib.gl.Lines;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.spaceitem.Rectangular;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.TopDrawable;

public class Wall extends Rectangular implements Moveable, TopDrawable, Forceful
{
	protected static final int DRAW_SECTIONS_PER_BLOCK = 8;
	protected static final int SPRING_SECTIONS = 6;
	protected static final float GAP_WIDTH = 1.5f;
	protected static final Random rGen = new Random();
	
	protected static final float BOUNCINESS = 0.7f;
	
	protected final int drawSections;
	
	private RectMesh walledge;
	private RectMesh walljoin1, walljoin2;
	
	BlockVortex vTop, vBottom;
	
	protected static final PaintDesc wallPaint = new PaintDesc(20, 100, 40);
	
	protected static final float WALL_WIDTH = BlockDef.GRID_SIZE_2;
	protected static final float WALL_MIN_X = 8.0f;
	protected static final float WALL_MAX_X = 100.0f;
	
	protected final boolean hasEnds;
	
	/**
	 * Construct a new {@link Wall}.
	 * @param context
	 * @param coord
	 * @param size The size of the wall
	 * @param rotation The rotation of this object, in DEGREES
	 * @param bounciness
	 */
	public Wall(SimulationContext context, Vec2 coord, float size, float rotation, boolean hasEnds, boolean hasVortex)
	{
		super(context, coord, new Vec2(CompuFuncs.trimMinMax(size, WALL_MIN_X, WALL_MAX_X), WALL_WIDTH), 1, rotation, BOUNCINESS, BodyType.STATIC);
		
		this.hasEnds = hasEnds;
		drawSections = (int) (size / (float)Constants.GRID_SIZE_2 * (float)DRAW_SECTIONS_PER_BLOCK);
		
		if(hasVortex) {
			Vec2 center1 = coord.add(new Vec2(0, GRID_SIZE * 2.5f));
			VecHelper.rotateLocal(center1, coord, rotation * DEG_TO_RAD);
			Vec2 center2 = coord.add(new Vec2(0, -GRID_SIZE * 2.5f));
			VecHelper.rotateLocal(center1, coord, rotation * DEG_TO_RAD);
			
			Vec2 vortexSize = new Vec2(this.size);
			vortexSize.y = GRID_SIZE * 3f;
			vTop = new BlockVortex(center1, vortexSize, rotation * DEG_TO_RAD);
			vBottom = new BlockVortex(center2, vortexSize, (rotation + 180) * DEG_TO_RAD);
		}
		
		walledge = new RectMesh(coord.x, coord.y, this.size.x, this.size.y, R.drawable.walledge);
		walledge.setRotation(rotation);
		walledge.setRepeatingTexture(true);
		
		if(hasEnds) {
			walljoin1 = new RectMesh(coord.x + this.size.x / 2 + this.size.y / 4,
					coord.y,
					this.size.y / 2,
					this.size.y,
					R.drawable.walljoiner);
			walljoin2 = new RectMesh(coord.x - this.size.x / 2 - this.size.y / 4,
					coord.y,
					this.size.y / 2,
					this.size.y,
					R.drawable.walljoiner);
			walljoin1.setRotation(rotation);
			walljoin2.setRotation(180+rotation);
		}
		
		fixture.setFriction(0.2f);
		fixture.getFilterData().categoryBits = COLLISION_GROUP_PLAYER;
		fixture.getFilterData().maskBits = COLLISION_GROUP_PLAYER;
		
		bezierPoints = new float[drawSections * 2];
		line = new Lines(getPosX(), getPosY(), new float[drawSections * 3], GL10.GL_LINE_STRIP, 1, 1, 1, 1);
		line.setRotation(rotation);
		spring = new Spring(SPRING_SECTIONS, 0, 0, 0, 0, 0.2f, 2f); // Don't initialise here, do so on next line
		if(hasEnds) {
			// Extra y/2 to add half distance on for end part.
			spring.setEnds(-this.size.x / 2 - this.size.y / 2, 0, this.size.x / 2 + this.size.y / 2, 0);
		} else {
			spring.setEnds(-this.size.x / 2, 0, this.size.x / 2, 0);
		}
	}
	
	// protected final Lines[][] lines = new Lines[LINE_CIRCLES][LINES];
	protected final Lines line;
	protected final Spring spring;
	protected final float[] bezierPoints;

	@Override
	public void draw(GL10 gl, float worldZoom) {
		walledge.draw(gl);
		if(vTop != null) vTop.draw(gl);
		if(vBottom != null) vBottom.draw(gl);
	}
	
	private float step = 0;

	@Override
	public void drawMove(float millistep, float speedScale) {
		spring.drawMove(millistep, speedScale);
		step += 0.004f * millistep;
		
		if(RAND.nextInt(60) == 42) {
			spring.getVelocities()[2] = -3f;
		}
		
		// Get bezier points from spring control points
		Bezier.bezier2D(spring.getSpringPoints(), drawSections, bezierPoints);
		FloatBuffer buffer = line.getVertices();
		for(int i = 0; i < drawSections; i++) {
			float bPoint = bezierPoints[i*2+0];
			float endScaleFactor = 1;
			if(Math.abs(bPoint) > size.x / 2) { // Start shrinking end
				endScaleFactor = CompuFuncs.trimMinMax((Math.abs(bPoint) - size.x / 2) / (size.y / 2), 1, 0);
			}
			buffer.put(i*3+0, bPoint);
			buffer.put(i*3+1, (float)Math.sin((float)i * 7f / DRAW_SECTIONS_PER_BLOCK - step) * size.y / 2 * 0.95f * endScaleFactor); // 5 = magic distance number
		}
		
		if(vTop != null)	vTop   .drawMove(millistep, speedScale);
		if(vBottom != null) vBottom.drawMove(millistep, speedScale);
	}

	@Override
	public void move(float millistep, float speedScale) {
		spring.move(millistep, speedScale);
		if(vTop != null)	vTop   .move(millistep, speedScale);
		if(vBottom != null) vBottom.move(millistep, speedScale);
	}

	@Override
	public void drawTop(GL10 gl, float worldZoom) {
	}

	@Override
	public void drawBelow(GL10 gl, float worldZoom) {
		line.draw(gl);
		
		if(walljoin1 != null) walljoin1.draw(gl);
		if(walljoin2 != null) walljoin2.draw(gl);
	}

	@Override
	public Vec2 calculateRF(Vec2 itemC, Vec2 itemV) {
		Vec2 rf = new Vec2();
		Vec2 f1 = null, f2 = null;
		if(vTop != null)    f1 = vTop.calculateRF(itemC, itemV);
		if(vBottom != null) f2 = vBottom.calculateRF(itemC, itemV);
		if(f1 != null) rf.addLocal(f1);
		if(f2 != null) rf.addLocal(f2);
		return rf;
	}

	@Override public boolean isForceExclusive() { return false; }
	@Override public Vec2 calculateVelocityImmutable(Vec2 itemPos, Vec2 itemV, float itemRadius) { return null; }
	@Override public void calculateVelocityMutable(Vec2 itemPos, Vec2 itemV, float itemRadius) { }
}
