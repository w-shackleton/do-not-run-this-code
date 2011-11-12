package uk.digitalsquid.spacegame.spaceitem.blocks;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.items.BlockDef;
import uk.digitalsquid.spacegamelib.Constants;
import uk.digitalsquid.spacegamelib.Geometry;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.gl.Mesh;
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
	
	static final int LINES_PER_GRID_LENGTH = 1;
	
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
	 * The size in units of an angular block's circumference.
	 */
	protected float angularDistance;
	
	/**
	 * The maximum width of the arc
	 */
	protected float angularYmax;
	/**
	 * The maximum width of the arc
	 */
	protected float angularYmin;
	
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
		
		// tmpDraw = new RectMesh(center.x, center.y, size.x, size.y, 1, 1, 0, 1);
		// tmpDraw.setRotation(angle * RAD_TO_DEG);
		
		vortexLines = (int) (size.x / BlockDef.GRID_SIZE * LINES_PER_GRID_LENGTH);
		vortexTexCoordSize = size.x / BlockDef.GRID_SIZE_2;
		final short[] indices = new short[vortexLines * 6];
		for(int i = 0; i < vortexLines; i++) {
			// Define indices for quads. See RectMesh's indices for this.
			indices[i*6+0] = (short) (i*4 + 0);
			indices[i*6+1] = (short) (i*4 + 1);
			indices[i*6+2] = (short) (i*4 + 2);
			indices[i*6+3] = (short) (i*4 + 1);
			indices[i*6+4] = (short) (i*4 + 3);
			indices[i*6+5] = (short) (i*4 + 2);
		}
		lines = new Mesh(center.x, center.y, new float[vortexLines * 3 * 4], indices, new float[vortexLines * 2 * 4], R.drawable.blockvortexbg);
		lines.setRepeatingTexture(true);
		lines.setRotation(angle * RAD_TO_DEG);
		
		lineProgress = new float[vortexLines * 3];
		for(int i = 0; i < vortexLines; i++) {
			lineProgress[i*3+0] = RAND.nextFloat();
			lineProgress[i*3+1] = RAND.nextFloat() * 0.05f + 0.05f;
			lineProgress[i*3+2] = RAND.nextFloat();
		}
	}
	/**
	 * Constructs a arc based vortex. Note that there is no minimum value for the arc as this would make the shape concave, and anger Box2D
	 * 
	 * @param center Where the arc starts from
	 * @param angle The rotation in RADIANS from the start position
	 * @param angularSize The size in RADIANS of the arc
	 * @param size The radius of the arc
	 */
	public BlockVortex(Vec2 center, float angle, float angularSize, float size, float minSize) {
		type = VortexType.ANGULAR;
		pos = center;
		this.angularSize = angularSize;
		this.angle = angle;
		this.angularYmax = size;
		this.angularYmin = minSize;
		
		catchArea = Geometry.createArc(null, center.x, center.y, size, angle, angle+angularSize);
		
		// angle
		// ----- x 2PI r
		// 2PI
		angularDistance = angularSize * size;
		vortexLines = (int) (angularDistance / BlockDef.GRID_SIZE * LINES_PER_GRID_LENGTH);
		vortexTexCoordSize = angularDistance / BlockDef.GRID_SIZE_2;
		final short[] indices = new short[vortexLines * 6];
		for(int i = 0; i < vortexLines; i++) {
			// Define indices for quads. See RectMesh's indices for this.
			indices[i*6+0] = (short) (i*4 + 0);
			indices[i*6+1] = (short) (i*4 + 1);
			indices[i*6+2] = (short) (i*4 + 2);
			indices[i*6+3] = (short) (i*4 + 1);
			indices[i*6+4] = (short) (i*4 + 3);
			indices[i*6+5] = (short) (i*4 + 2);
		}
		lines = new Mesh(center.x, center.y, new float[vortexLines * 3 * 4], indices, new float[vortexLines * 2 * 4], R.drawable.blockvortexbg);
		lines.setRepeatingTexture(true);
		lines.setRotation(angle * RAD_TO_DEG);
		
		lineProgress = new float[vortexLines * 3];
		for(int i = 0; i < vortexLines; i++) {
			lineProgress[i*3+0] = RAND.nextFloat();
			lineProgress[i*3+1] = RAND.nextFloat() * 0.05f + 0.05f;
			lineProgress[i*3+2] = RAND.nextFloat();
		}
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
	
	/**
	 * The animated line - a mesh so that each line is a quad.
	 */
	protected Mesh lines;
	
	/**
	 * The progress of the lines, from which the vortex positions are calculated.<br />
	 * Format: {position 0-1, progress 0-1, size 0-1}.
	 */
	protected float[] lineProgress;
	protected float textureMovement;
	
	public void draw(GL10 gl) {
		if(lines != null) lines.draw(gl);
	}
	@Override public void move(float millistep, float speedScale) { }
	
	@Override
	public void drawMove(float millistep, float speedscale) {
		// Recalculate vortex positions.
		for(int i = 0; i < vortexLines; i++) {
			// Move step on
			lineProgress[i*3+1] += 0.001 * millistep;
			
			float progress = lineProgress[i*3+1];
			float step = lineProgress[i*3+2];
			if(progress + step > 1) { // Reset
				// Pos
				lineProgress[i*3+0] = RAND.nextFloat();
				step = RAND.nextFloat() * 0.05f + 0.05f;
				lineProgress[i*3+1] = Math.min(step, 1);
				lineProgress[i*3+2] = -step; // Put behind 0.
			}
		}
		textureMovement += 0.0006 * millistep;
		
		calculateLinesFromProgress();
	}
	
	private static final float VORTEX_LINE_WIDTH = 0.06f;
	private static final float VORTEX_CURVED_LINE_WIDTH = 0.18f;
	private static final float HALF_LINE = VORTEX_LINE_WIDTH / 2;
	private static final float HALF_LINE_2 = VORTEX_CURVED_LINE_WIDTH / 2;
	
	/**
	 * Transforms lineProgress into lines. Also modifies the texture coordinates, as this shape is non trivial
	 */
	private void calculateLinesFromProgress() {
		FloatBuffer vertices, texCoord;
		switch(type) {
		case LINEAR:
			vertices = lines.getVertices();
			texCoord = lines.getTextureCoordinates();
			for(int i = 0; i < vortexLines; i++) {
				final float position = lineProgress[i*3+0];
				final float progress = lineProgress[i*3+1];
				final float step = lineProgress[i*3+2];
				float newPosition1 = (position - 0.5f) - (HALF_LINE / linearSize.x);
				float newPosition2 = (position - 0.5f) + (HALF_LINE / linearSize.y);
				float newProgress1 = (float) (Math.cos(progress * Math.PI / 2) - 0.5f);
				float newProgress2 = (float) (Math.cos((progress+step) * Math.PI / 2) - 0.5f);
				vertices.put(i*3*4+0, newPosition1 * linearSize.x);
				vertices.put(i*3*4+1, newProgress1 * linearSize.y);
				vertices.put(i*3*4+3, newPosition2 * linearSize.x);
				vertices.put(i*3*4+4, newProgress1 * linearSize.y);
				vertices.put(i*3*4+6, newPosition1 * linearSize.x);
				vertices.put(i*3*4+7, newProgress2 * linearSize.y);
				vertices.put(i*3*4+9, newPosition2 * linearSize.x);
				vertices.put(i*3*4+10,newProgress2 * linearSize.y);
				
				// Images go positive down, opengl goes positive up
				texCoord.put(i*2*4+0, (+newPosition1 + 0.5f) * vortexTexCoordSize + textureMovement);
				texCoord.put(i*2*4+1, (-newProgress1 - 0.5f) * 1);
				texCoord.put(i*2*4+2, (+newPosition2 + 0.5f) * vortexTexCoordSize + textureMovement);
				texCoord.put(i*2*4+3, (-newProgress1 - 0.5f) * 1);
				texCoord.put(i*2*4+4, (+newPosition1 + 0.5f) * vortexTexCoordSize + textureMovement);
				texCoord.put(i*2*4+5, (-newProgress2 - 0.5f) * 1);
				texCoord.put(i*2*4+6, (+newPosition2 + 0.5f) * vortexTexCoordSize + textureMovement);
				texCoord.put(i*2*4+7, (-newProgress2 - 0.5f) * 1);
			}
			break;
		case ANGULAR:
			vertices = lines.getVertices();
			texCoord = lines.getTextureCoordinates();
			for(int i = 0; i < vortexLines; i++) {
				final float position= lineProgress[i*3+0];
				final float progress = lineProgress[i*3+1];
				final float step = lineProgress[i*3+2];
				float newProgress1 = (float) Math.cos(progress * Math.PI / 2);
				float newProgress2 = (float) Math.cos((progress+step) * Math.PI / 2);
				float newPosition1 = position - (HALF_LINE / angularDistance);
				float newPosition2 = position + (HALF_LINE / angularDistance);
				float newPosition1X = (float) Math.cos((position - (HALF_LINE_2 / angularDistance)) * angularSize);
				float newPosition1Y = (float) Math.sin((position - (HALF_LINE_2 / angularDistance)) * angularSize);
				float newPosition2X = (float) Math.cos((position + (HALF_LINE_2 / angularDistance)) * angularSize);
				float newPosition2Y = (float) Math.sin((position + (HALF_LINE_2 / angularDistance)) * angularSize);
				
				float newPosition3X = (float) Math.cos((position - (HALF_LINE_2 / angularDistance)) * angularSize);
				float newPosition3Y = (float) Math.sin((position - (HALF_LINE_2 / angularDistance)) * angularSize);
				float newPosition4X = (float) Math.cos((position + (HALF_LINE_2 / angularDistance)) * angularSize);
				float newPosition4Y = (float) Math.sin((position + (HALF_LINE_2 / angularDistance)) * angularSize);
				
				vertices.put(i*3*4+0, newPosition3X * (newProgress2 * (angularYmax - angularYmin) + angularYmin));
				vertices.put(i*3*4+1, newPosition3Y * (newProgress2 * (angularYmax - angularYmin) + angularYmin));
				vertices.put(i*3*4+3, newPosition4X * (newProgress2 * (angularYmax - angularYmin) + angularYmin));
				vertices.put(i*3*4+4, newPosition4Y * (newProgress2 * (angularYmax - angularYmin) + angularYmin));
				vertices.put(i*3*4+6, newPosition1X * (newProgress1 * (angularYmax - angularYmin) + angularYmin));
				vertices.put(i*3*4+7, newPosition1Y * (newProgress1 * (angularYmax - angularYmin) + angularYmin));
				vertices.put(i*3*4+9, newPosition2X * (newProgress1 * (angularYmax - angularYmin) + angularYmin));
				vertices.put(i*3*4+10,newPosition2Y * (newProgress1 * (angularYmax - angularYmin) + angularYmin));
				
				// Images go positive down, opengl goes positive up
				texCoord.put(i*2*4+0, +newPosition1 * vortexTexCoordSize - textureMovement);
				texCoord.put(i*2*4+1, -newProgress1 * 1);
				texCoord.put(i*2*4+2, +newPosition2 * vortexTexCoordSize - textureMovement);
				texCoord.put(i*2*4+3, -newProgress1 * 1);
				texCoord.put(i*2*4+4, +newPosition1 * vortexTexCoordSize - textureMovement);
				texCoord.put(i*2*4+5, -newProgress2 * 1);
				texCoord.put(i*2*4+6, +newPosition2 * vortexTexCoordSize - textureMovement);
				texCoord.put(i*2*4+7, -newProgress2 * 1);
			}
			break;
		}
	}
}
