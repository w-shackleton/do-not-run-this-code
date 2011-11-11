package uk.digitalsquid.spacegame.spaceitem.blocks;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

import uk.digitalsquid.spacegame.spaceitem.items.BlockDef;
import uk.digitalsquid.spacegamelib.Constants;
import uk.digitalsquid.spacegamelib.Geometry;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.gl.Lines;
import uk.digitalsquid.spacegamelib.gl.Mesh;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;

/**
 * The vortex over a block
 * @author william
 *
 */
public class BlockVortex implements Constants, Forceful, Moveable {
	public static enum VortexType {
		LINEAR,
		ANGULAR
	}
	
	static final int LINES_PER_GRID_LENGTH = 20;
	
	final int vortexLines;
	
	/**
	 * The size to use for the tex coords, calculated from the total size of this.
	 */
	final float vortexTexCoordSize;
	
	/**
	 * Identity transform
	 */
	private static final Transform transform = new Transform(new Vec2(), new Mat22(1, 0, 0, 1));
	
	protected final Shape catchArea;
	
	protected final Vec2 pos;
	/**
	 * The angle in RADIANS. 0 is pulling left.
	 */
	protected final float angle;
	
	protected final VortexType type;
	
	protected Vec2 linearSize;
	
	/**
	 * The size in RADIANS of an angular block
	 */
	protected float angularSize;
	
	/**
	 * The maximum width of the arc
	 */
	protected float angularYmax;
	
	/**
	 * Constructs a rectangular vortex
	 * @param center
	 * @param size
	 * @param angle The rotation in RADIANS
	 */
	public BlockVortex(Vec2 center, Vec2 size, float angle) {
		type = VortexType.LINEAR;
		pos = center;
		linearSize = size;
		this.angle = angle;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(size.x / 2, size.y / 2, center, angle);
		catchArea = shape;
		
		tmpDraw = new RectMesh(center.x, center.y, size.x, size.y, 1, 1, 0, 1);
		tmpDraw.setRotation(angle * RAD_TO_DEG);
		
		vortexLines = (int) (size.x / BlockDef.GRID_SIZE * LINES_PER_GRID_LENGTH);
		vortexTexCoordSize = size.x / BlockDef.GRID_SIZE_2;
		final float[] texCoords = {
				0, 1,
				vortexTexCoordSize, 1,
				0, 0,
				vortexTexCoordSize, 0
		};
		lines = new Lines(center.x, center.y, vortexLines * 3 * 2, texCoords, GL10.GL_LINES, uk.digitalsquid.spacegame.R.drawable.blockvortexbg);
		lines.setRotation(angle * RAD_TO_DEG);
		
		lineProgress = new float[vortexLines * 3];
	}
	/**
	 * Constructs a arc based vortex. Note that there is no minimum value for the arc as this would make the shape concave, and anger Box2D
	 * 
	 * @param center Where the arc starts from
	 * @param angle The rotation in RADIANS from the start position
	 * @param angularSize The size in RADIANS of the arc
	 * @param size The radius of the arc
	 */
	public BlockVortex(Vec2 center, float angle, float angularSize, float size) {
		type = VortexType.ANGULAR;
		vortexLines = 0;
		vortexTexCoordSize = 1; // TODO: This?
		pos = center;
		this.angularSize = angularSize;
		this.angle = angle;
		
		catchArea = Geometry.createArc(null, center.x, center.y, size, angle, angle+angularSize);
		Vec2[] vec2s = ((PolygonShape)catchArea).m_vertices;
		float vertices[] = new float[vec2s.length * 3];
		for(int i = 0; i < vec2s.length; i++) {
			vertices[i*3+0] = vec2s[i].x;
			vertices[i*3+1] = vec2s[i].y;
			vertices[i*3+2] = 0;
		}
		tmpDraw = new Mesh(0, 0, vertices, new short[0], 0, 1, 0, 1);
		tmpDraw.setDrawMode(GL10.GL_TRIANGLE_FAN);
	}
	
	private Vec2 force = new Vec2();
	private static final float FORCE_MAGNITUDE = 12f;
	private static final Vec2 LINEAR_FORCE = new Vec2(0, -FORCE_MAGNITUDE);
	
	@Override
	public Vec2 calculateRF(Vec2 itemC, Vec2 itemV) {
		if(catchArea.testPoint(transform, itemC)) {
			switch(type) {
			case LINEAR:
				force.set(LINEAR_FORCE);
				VecHelper.rotateLocal(force, null, angle);
				return force;
			case ANGULAR:
				float angle = VecHelper.angleFromRad(pos, itemC);
				VecHelper.vecFromPolar(force, angle, FORCE_MAGNITUDE);
				return force;
			}
		}
		return null;
	}

	@Override
	public boolean isForceExclusive() {
		return false;
	}

	@Override
	public Vec2 calculateVelocityImmutable(Vec2 itemPos, Vec2 itemV, float itemRadius) {
		return null;
	}
	@Override
	public void calculateVelocityMutable(Vec2 itemPos, Vec2 itemV, float itemRadius) {
	}
	
	protected Mesh tmpDraw;
	protected Lines lines;
	
	/**
	 * The progress of the lines, from which the vortex positions are calculated.<br />
	 * Format: {position 0-1, progress 0-1, size 0-1}.
	 */
	protected float[] lineProgress;
	
	public void draw(GL10 gl) {
		if(tmpDraw != null) tmpDraw.draw(gl);
	}
	@Override public void move(float millistep, float speedScale) { }
	
	@Override
	public void drawMove(float millistep, float speedscale) {
		// Recalculate vortex positions.
		for(int i = 0; i < vortexLines; i++) {
			// Move step on
			// lineProgress[i*3+1] += 0.001 * 
		}
	}
}
