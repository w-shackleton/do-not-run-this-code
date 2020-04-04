package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.HashMap;
import java.util.Map;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import uk.digitalsquid.spacegame.spaceitem.blocks.BlockVortex;
import uk.digitalsquid.spacegamelib.Constants;
import uk.digitalsquid.spacegamelib.Geometry;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.VecHelper;


/**
 * Definitions of the different types of block.
 * @author william
 *
 */
public abstract class BlockDef implements Constants {
	
	/**
	 * The grid size according to images.
	 */
	public static final float IMAGE_GRID_SIZE = 32;
	
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
	
	public float getRestitution() {
		return 0.7f;
	}
	public float getFriction() {
		return 0.6f;
	}
	
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
	 * The resulting shape shouldn't be rotated.
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
			
			int pos = 0;
			// BLOCK_CENTER
			defs.put(pos++, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(100, 100);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					return null; // No shape - center should never be hit.
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_center;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(2, 2);
				}

				@Override // No vortexes for center
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					return null;
				}
			});
			// BLOCK_EDGE
			defs.put(pos++, new BlockDef() {
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
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_edge1;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(4, 2);
				}
				
				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					Vec2 vortexCenter = pos.add(new Vec2(0, GRID_SIZE * 2.5f));
					VecHelper.rotateLocal(vortexCenter, pos, angle);
					Vec2 vortexSize = new Vec2(size);
					vortexSize.y = GRID_SIZE * 3f;
					return new BlockVortex(vortexCenter, vortexSize, angle);
				}
			});
			// BLOCK_CORNER
			defs.put(pos++, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(1, 1);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					return Geometry.createArc(body, -size.x / 2, -size.y / 2, size.x, 0f, (float)Math.PI / 2);
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_corner1;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(2, 2);
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					Vec2 origin = new Vec2(pos.x - GRID_SIZE, pos.y - GRID_SIZE); // Bottom corner
					VecHelper.rotateLocal(origin, pos, angle);
					return new BlockVortex(origin, angle, (float)Math.PI / 2 /* 90 */, size.x + GRID_SIZE * 3f, size.x);
				}
			});
			// BLOCK_FADE
			defs.put(pos++, new BlockDef() {
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
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_fade1;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(4, 2);
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					return null;
				}
			});
			
			// BLOCK_WALLJOIN1
			defs.put(pos++, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(1, 1);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					PolygonShape shape = new PolygonShape();
					shape.setAsBox(size.x / 2, size.y / 2);
					return shape;
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_walljoin1;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(2, 2);
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					Vec2 vortexCenter = pos.add(new Vec2(0, GRID_SIZE * 2.5f));
					VecHelper.rotateLocal(vortexCenter, pos, angle);
					Vec2 vortexSize = new Vec2(size);
					vortexSize.y = GRID_SIZE * 3f;
					return new BlockVortex(vortexCenter, vortexSize, angle);
				}
			});
			// BLOCK_WALLJOIN2
			defs.put(pos++, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(1, 1);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					PolygonShape shape = new PolygonShape();
					shape.setAsBox(size.x / 2, size.y / 2);
					return shape;
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_walljoin2;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(2, 2);
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					Vec2 vortexCenter = pos.add(new Vec2(0, GRID_SIZE * 2.5f));
					VecHelper.rotateLocal(vortexCenter, pos, angle);
					Vec2 vortexSize = new Vec2(size);
					vortexSize.y = GRID_SIZE * 3f;
					return new BlockVortex(vortexCenter, vortexSize, angle);
				}
			});
			// BLOCK_WALLJOIN3
			defs.put(pos++, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(1, 1);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					PolygonShape shape = new PolygonShape();
					shape.setAsBox(size.x / 2, size.y / 2);
					return shape;
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_walljoin3;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(2, 2);
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					Vec2 vortexCenter = pos.add(new Vec2(0, GRID_SIZE * 2.5f));
					VecHelper.rotateLocal(vortexCenter, pos, angle);
					Vec2 vortexSize = new Vec2(size);
					vortexSize.y = GRID_SIZE * 3f;
					return new BlockVortex(vortexCenter, vortexSize, angle);
				}
			});
			// BLOCK_WALL_CORNER
			defs.put(pos++, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(1, 1);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					PolygonShape shape = new PolygonShape();
					shape.setAsBox(size.x / 2, size.y / 2);
					return shape;
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_wallcorner;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(2, 2);
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					return null;
				}
			});
			// BLOCK_WALL_CORNER2
			defs.put(pos++, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(1, 1);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					PolygonShape shape = new PolygonShape();
					shape.setAsBox(size.x / 2, size.y / 2);
					return shape;
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_wallcorner2;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(2, 2);
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					return null;
				}
			});
			// BLOCK_CONCAVE1
			defs.put(pos++, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(1, 1);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					return Geometry.createConcaveArc(-size.x / 2, size.y / 2, size.x, -Constants.PI / 2, 0);
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_concave1;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(2, 2);
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					return null;
				}
			});
			// BLOCK_CONCAVE2
			defs.put(pos++, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(2, 2);
				}
				@Override
				protected Vec2 getUnscaledMinSize() {
					return new Vec2(2, 2);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					return Geometry.createConcaveArc(-size.x / 2, size.y / 2, size.x, -Constants.PI / 2, 0);
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_concave2;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(4, 4);
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					return null;
				}
			});
			// BLOCK_CORNER2
			defs.put(pos++, new BlockDef() {
				@Override
				protected Vec2 getUnscaledMaxSize() {
					return new Vec2(2, 2);
				}
				@Override
				protected Vec2 getUnscaledMinSize() {
					return new Vec2(2, 2);
				}
				
				@Override
				public Shape getShape(Body body, Vec2 size) {
					return Geometry.createArc(body, -size.x / 2, -size.y / 2, size.x, 0f, (float)Math.PI / 2);
				}
				
				@Override
				public int getImageId() {
					return uk.digitalsquid.spacegame.R.drawable.block_corner2;
				}
				
				public Vec2 getImageRelativeSize() {
					return new Vec2(4, 4);
				}

				@Override
				public BlockVortex getVortex(Vec2 pos, Vec2 size, float angle) {
					Vec2 origin = new Vec2(pos.x - GRID_SIZE, pos.y - GRID_SIZE); // Bottom corner
					VecHelper.rotateLocal(origin, pos, angle);
					return new BlockVortex(origin, angle, (float)Math.PI / 2 /* 90 */, size.x + GRID_SIZE * 3f, size.x);
				}
			});
		}
		return defs.get(id);
	}
}
