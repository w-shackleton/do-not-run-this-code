package uk.digitalsquid.spacegame.spaceitem.items;

import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.blocks.BlockVortex;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.gl.Lines;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.spaceitem.Rectangular;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.TopDrawable;

public class Wall extends Rectangular implements Moveable, TopDrawable, Forceful
{
	protected static final int LINES = 10;
	protected static final int LINE_CIRCLES = 1;
	protected static final float GAP_WIDTH = 1.5f;
	protected static final Random rGen = new Random();
	
	protected static final float BOUNCINESS = 0.7f;
	
	private RectMesh walledge;
	
	BlockVortex vTop, vBottom;
	
	protected static final PaintDesc wallPaint = new PaintDesc(20, 100, 40);
	
	protected static final float WALL_WIDTH = BlockDef.GRID_SIZE_2;
	protected static final float WALL_MIN_X = 8.0f;
	protected static final float WALL_MAX_X = 100.0f;
	
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
		
		fixture.setFriction(0.2f);
		fixture.getFilterData().categoryBits = COLLISION_GROUP_PLAYER;
		fixture.getFilterData().maskBits = COLLISION_GROUP_PLAYER;
		
		for(int i = 0; i < lines.length; i++) {
			for(int j = 0; j < lines[i].length; j++) {
				lines[i][j] = new Lines(getPosX(), getPosY(), new float[4*3], GL10.GL_LINE_STRIP, 0.9f, 0.6f, 0.3f, 0.7f);
				lines[i][j].setRotation(rotation);
				FloatBuffer buffer = lines[i][j].getVertices();
				buffer.put(0, -this.size.x / 2 - BlockDef.GRID_SIZE);
				buffer.put(1, 0);
				buffer.put(2, 0);
				buffer.put(9, this.size.x / 2 + BlockDef.GRID_SIZE);
				buffer.put(10, 0);
				buffer.put(11, 0);
			}
		}
	}
	
	protected final Lines[][] lines = new Lines[LINE_CIRCLES][LINES];

	@Override
	public void draw(GL10 gl, float worldZoom) {
		walledge.draw(gl);
		if(vTop != null) vTop.draw(gl);
		if(vBottom != null) vBottom.draw(gl);
	}
	
	private float step = 0;

	@Override
	public void drawMove(float millistep, float speedScale) {
		step += 0.004f * millistep;
		boolean clockwise = true;
		for(int i = 0; i < lines.length; i++) {
			Lines[] list = lines[i];
			clockwise = !clockwise;
			float position01 = ((float)i+1f) / ((float)lines.length+1f);
			for(int j = 0; j < list.length; j++) {
				FloatBuffer vertices = list[j].getVertices();
				float rotation = (float)j / (float)list.length * (float)Math.PI * 2f + (step * (1-position01));
				if(clockwise) rotation = -rotation;
				float ypos = (float)Math.cos(rotation) * size.y / 2;
				ypos *= position01;
				float zpos = (float)(Math.sin(rotation)-1) * size.y / 2;
				zpos *= position01;
				vertices.put(3, -size.x / 2);
				vertices.put(4, ypos);
				vertices.put(5, zpos);
				vertices.put(6, size.x / 2);
				vertices.put(7, ypos);
				vertices.put(8, zpos);
			}
		}
		if(vTop != null)	vTop   .drawMove(millistep, speedScale);
		if(vBottom != null) vBottom.drawMove(millistep, speedScale);
	}

	@Override
	public void move(float millistep, float speedScale) {
		if(vTop != null)	vTop   .move(millistep, speedScale);
		if(vBottom != null) vBottom.move(millistep, speedScale);
	}

	@Override
	public void drawTop(GL10 gl, float worldZoom) {
	}

	@Override
	public void drawBelow(GL10 gl, float worldZoom) {
		gl.glEnable(GL10.GL_DEPTH_TEST);
		for(Lines[] list : lines) {
			for(Lines line : list) {
				line.draw(gl);
			}
		}
		gl.glDisable(GL10.GL_DEPTH_TEST);
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
