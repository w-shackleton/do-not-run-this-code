package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.HashMap;
import java.util.Map;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import uk.digitalsquid.spacegame.levels.SaxLoader;
import uk.digitalsquid.spacegame.spaceitem.assistors.Geometry;
import uk.digitalsquid.spacegame.spaceitem.blocks.BlockVortex;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;


/**
 * Definitions of the different types of block.
 * @author william
 *
 */
public abstract class BlockDef {
	
	/**
	 * The grid size according to images.
	 */
	public static final float IMAGE_GRID_SIZE = 32;
	
	/**
	 * The positioning block size of grid items
	 */
	public static final float GRID_SIZE = 16 * SaxLoader.LOAD_SCALE;
	/**
	 * The size of grid items
	 */
	public static final float GRID_SIZE_2 = GRID_SIZE * 2;
	
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
		return getUnscaledMinSize().mulLocal(GRID_SIZE_2);
	}
	public Vec2 getMaxSize() {
		return getUnscaledMaxSize().mulLocal(GRID_SIZE_2);
	}
	
	public abstract float getRestitution();
	public abstract float getFriction();
	
	/**
	 * If the block has a picture, returns the image ID. Otherwise, return -1.
	 * @return
	 */
	public abstract int getImageId();
	
	/**
	 * Gets the size of the image at getImageId relative to {@link #IMAGE_GRID_SIZE}
	 * @return
	 */
	public abstract Vec2 getImageRelativeSize();
	
	/**
	 * Returns the shape of this definition, as it should appear to the physics engine. Should not cache the result.
	 * @param size The scaled size of the object - used to work out how big the shape should be. Shape should be centered over origin.
	 * @return
	 */
	public abstract Shape getShape(Body body, Vec2 size);
	
	/**
	 * 
	 * @param pos
	 * @param size
	 * @param angle in RADIANS
	 * @return
	 */
	public abstract BlockVortex getVortex(Vec2 pos, Vec2 size, float angle);
	
	/**
	 * Creates a block from this definition. Use this factory method, as returned type may be a child type in some cases.
	 * The default implementation creates a normal block with this def.
	 * @return
	 */
	public Block create(SimulationContext context, Vec2 pos, Vec2 size, float angle, boolean hasVortex) {
		return new Block(context, pos, size, angle, hasVortex, this);
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
				public Shape getShape(Body body, Vec2 size) {
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
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(2, 2);
				}
				
				@Override
				public float getFriction() {
					return 0;
				}

				@Override // No vortexes for center
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					return null;
				}
			});
			// BLOCK_EDGE
			defs.put(1, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(100, 1);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					PolygonShape shape = new PolygonShape();
					shape.setAsBox(size.x / 2, size.y / 2);
					return shape;
				}
				
				@Override
				public float getRestitution() {
					return 0.7f;
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_edge1;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(4, 2);
				}
				
				@Override
				public float getFriction() {
					return 0.6f;
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					Vec2 vortexCenter = size.sub(new Vec2(0, -GRID_SIZE_2));
					CompuFuncs.rotateLocal(vortexCenter, pos, angle);
					Vec2 vortexSize = new Vec2(size);
					vortexSize.x = GRID_SIZE_2;
					return new BlockVortex(vortexCenter, vortexSize, angle);
				}
			});
			// BLOCK_CORNER
			defs.put(2, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(1, 1);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					return Geometry.createArc(body, -size.x / 2, -size.y / 2, size.x, 0f, (float)Math.PI / 2);
				}
				
				@Override
				public float getRestitution() {
					return 0.7f;
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_corner1;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(2, 2);
				}
				
				@Override
				public float getFriction() {
					return 0.6f;
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					// TODO Write corner vortex
					return null;
				}
			});
			// BLOCK_FADE
			defs.put(3, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(100, 1);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					// TODO: Do something here?
					PolygonShape shape = new PolygonShape();
					shape.setAsBox(size.x / 2, size.y / 2);
					return shape;
				}
				
				@Override
				public float getRestitution() {
					return 0.1f;
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_fade1;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(4, 2);
				}
				
				@Override
				public float getFriction() {
					return 1f;
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					return null;
				}
			});
		}
		return defs.get(id);
	}
}
