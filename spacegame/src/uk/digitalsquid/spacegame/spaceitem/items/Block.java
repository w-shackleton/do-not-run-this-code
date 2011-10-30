package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.spaceitem.SpaceItem;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.IsClickable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;

/**
 * Block pieces fit together and are aligned to a grid.
 * @author william
 *
 */
public class Block extends SpaceItem implements Moveable, Forceful, IsClickable {
	/**
	 * The size of this block
	 */
	protected Vec2 size;
	
	protected Fixture fixture;
	
	protected RectMesh mesh;

	/**
	 * Protected constructor. Use BlockDef.create
	 * @param context
	 * @param pos
	 * @param size
	 * @param angle
	 * @param def
	 */
	Block(SimulationContext context, Vec2 pos, Vec2 size, float angle, BlockDef def) {
		super(context, pos, angle, BodyType.STATIC);
		
		setSize(size, def.getMinSize(), def.getMaxSize());
		
		Shape shape = def.getShape(size);
		if(shape != null) {
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.density = 1;
			fixtureDef.friction = def.getFriction();
			fixtureDef.restitution = def.getRestitution();
			fixture = body.createFixture(fixtureDef);
			fixture.setUserData(this);
		}
		
		if(def.getImageId() != -1) {
			mesh = new RectMesh(pos.x, pos.y, size.x, size.y, def.getImageId());
			
			// Since this is a repeating texture, custom tex coords must be set.
			//*
			Vec2 imageRelativeSize = def.getImageRelativeSize();
			final float coordSizeX = size.x / BlockDef.GRID_SIZE / imageRelativeSize.x;
			final float coordSizeY = size.y / BlockDef.GRID_SIZE / imageRelativeSize.y;
			final float[] texCoords = 
					{0,			coordSizeY,
					coordSizeX,	coordSizeY,
					0,			0,
                    coordSizeX,	0};
			mesh.setRepeatingTexture(true);
			mesh.setTextureCoordinates(texCoords);
			//*/
		}
	}
	
	/**
	 * This contstructor doesn't set up the block's data. This one should probably be overridden.
	 * @param context
	 * @param pos
	 * @param size
	 * @param angle
	 */
	Block(SimulationContext context, Vec2 pos, Vec2 size, float angle) {
		super(context, pos, angle, BodyType.DYNAMIC);
		this.size = size;
	}
	
	/**
	 * Sets the size within an area
	 * @param size
	 * @param min
	 * @param max
	 */
	void setSize(Vec2 size, Vec2 min, Vec2 max) {
		this.size = size;
		this.size.x = CompuFuncs.TrimMinMax(size.x, min.x, max.x);
		this.size.y = CompuFuncs.TrimMinMax(size.y, min.y, max.y);
	}

	@Override
	public boolean isClicked(float x, float y) {
		return false;
	}

	@Override
	public Vec2 calculateRF(Vec2 itemC, Vec2 itemV) {
		return null;
	}

	@Override
	public boolean isForceExclusive() {
		return false;
	}

	@Override
	public Vec2 calculateVelocityImmutable(Vec2 itemPos, Vec2 itemV,
			float itemRadius) {
		return null;
	}

	@Override
	public void calculateVelocityMutable(Vec2 itemPos, Vec2 itemV,
			float itemRadius) {
	}

	@Override
	public void move(float millistep, float speedScale) {
	}

	@Override
	public void drawMove(float millistep, float speedscale) {
	}

	@Override
	public void draw(GL10 gl, float worldZoom) {
		if(mesh != null) {
			mesh.setXY(getPosX(), getPosY());
			mesh.setRotation(getRotation());
			mesh.draw(gl);
		}
	}
}
