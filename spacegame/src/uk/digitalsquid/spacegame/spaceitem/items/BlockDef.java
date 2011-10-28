package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.HashMap;
import java.util.Map;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import uk.digitalsquid.spacegamelib.SimulationContext;


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
	
	/**
	 * Creates a block from this definition. Use this factory method, as returned type may be a child type in some cases.
	 * The default implementation creates a normal block with this def.
	 * @return
	 */
	public Block create(SimulationContext context, Vec2 pos, Vec2 size, float angle) {
		return new Block(context, pos, size, angle, this);
	}
	
	private static Map<Integer, BlockDef> defs;
	
	/**
	 * Gets the definition for a block with the given ID. Also initialises the internal data, so should be called from loading screen.
	 * @param id
	 * @return <code>null</code> if no def found.
	 */
	public static BlockDef getBlockDef(int id) {
		if(defs == null) {
			defs = new HashMap<Integer, BlockDef>();
			
			// TODO: Define blocks!
		}
		return defs.get(id);
	}
}
