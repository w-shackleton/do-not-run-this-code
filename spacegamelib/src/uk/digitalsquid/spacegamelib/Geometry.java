package uk.digitalsquid.spacegamelib;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * Geometry and shape helper functions
 * @author william
 *
 */
public final class Geometry {
	
	public static final int SHAPE_RESOLUTION = 18;
	
	static {
		// Make sure we have enough vertices
		Settings.maxPolygonVertices = SHAPE_RESOLUTION;
	}
	
	/**
	 * Creates an arc shape
	 * @param radius
	 * @param from The angle from in RADIANS
	 * @param to The angle to in RADIANS
	 * @return
	 */
	public static final Shape createArc(Body body, final float originX, final float originY, final float radius, final float from, final float to) {
		PolygonShape shape = new PolygonShape();
		Vec2[] vertices = new Vec2[SHAPE_RESOLUTION];
		
		vertices[0] = new Vec2(originX, originY);
		
		for(int i = 1; i < SHAPE_RESOLUTION; i++) {
			// Loop is inclusive to make sure final point is in arc
			float angle = from + ((to-from) * (float)(i-1) / (SHAPE_RESOLUTION-2));
			vertices[i] = new Vec2();
			vertices[i].x = (float)Math.cos(angle) * radius + originX;
			vertices[i].y = (float)Math.sin(angle) * radius + originY;
		}
		
		shape.set(vertices, SHAPE_RESOLUTION);
		return shape;
	}
	
	static final Shape createConcaveEdge(Vec2[] points) {
		ChainShape shape = new ChainShape();
		shape.createChain(points, points.length);
		return shape;
	}
	
	/**
	 * Creates a concave arc shape
	 * @param radius
	 * @param from The angle from in RADIANS
	 * @param to The angle to in RADIANS
	 * @return
	 */
	public static final Shape createConcaveArc(final float originX, final float originY, final float radius, final float from, final float to) {
		Vec2[] vertices = new Vec2[SHAPE_RESOLUTION];
		
		for(int i = 0; i < SHAPE_RESOLUTION; i++) {
			// Loop is inclusive to make sure final point is in arc
			float angle = from + ((to-from) * (float)i / (SHAPE_RESOLUTION-1));
			vertices[i] = new Vec2();
			vertices[i].x = (float)Math.cos(angle) * radius + originX;
			vertices[i].y = (float)Math.sin(angle) * radius + originY;
		}
		
		return createConcaveEdge(vertices);
	}
}
