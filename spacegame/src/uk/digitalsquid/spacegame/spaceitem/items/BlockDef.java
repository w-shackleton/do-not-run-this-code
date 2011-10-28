package uk.digitalsquid.spacegame.spaceitem.items;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;


/**
 * Definitions of the different types of block.
 * @author william
 *
 */
public abstract class BlockDef {
	/**
	 * The size of grid items
	 */
	public static final float GRID_SIZE = 16;
	
	/**
	 * Gets the smallest size of the block, where 1 unit is a grid square. Should not cache its result.
	 * @return
	 */
	protected abstract Vec2 getUnscaledMinSize();
	
	/**
	 * Gets the largest size of the block, where 1 unit is a grid square. Should not cache its result.
	 * @return
	 */
	protected abstract Vec2 getUnscaledMaxSize();
	
	public Vec2 getMinSize() {
		return getUnscaledMinSize().mulLocal(GRID_SIZE);
	}
	public Vec2 getMaxSize() {
		return getUnscaledMaxSize().mulLocal(GRID_SIZE);
	}
	
	public abstract float getRestitution();
	public abstract float getFriction();
	
	/**
	 * Returns the shape of this definition, as it should appear to the physics engine. Should not cache the result.
	 * @return
	 */
	public abstract Shape getShape();
}
