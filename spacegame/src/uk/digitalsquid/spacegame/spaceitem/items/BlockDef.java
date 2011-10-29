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
	 * Default is (1, 1)
	 * @return
	 */
	protected Vec2 getUnscaledMinSize() {
		return new Vec2(1, 1);
	}
	
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
	 * If the block has a picture, returns the image ID. Otherwise, return -1.
	 * @return
	 */
	public abstract int getImageId();
	
	/**
	 * Returns the shape of this definition, as it should appear to the physics engine. Should not cache the result.
	 * @param size The scaled size of the object - used to work out how big the shape should be. Shape should be centered over origin.
	 * @return
	 */
	public abstract Shape getShape(Vec2 size);
	
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
			
			// BLOCK_CENTER
			defs.put(0, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(100, 100);
				}
				
				@Override
				public Shape getShape(Vec2 size) {
					return null; // No shape - center should never be hit.
				}
				
				@Override
				public float getRestitution() {
					return 0;
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_center;
				}
				
				@Override
				public float getFriction() {
					return 0;
				}
			});
		}
		return defs.get(id);
	}
}
