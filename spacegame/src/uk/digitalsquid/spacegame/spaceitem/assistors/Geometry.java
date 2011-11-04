package uk.digitalsquid.spacegame.spaceitem.assistors;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * Geometry and shape helper functions
 * @author william
 *
 */
public final class Geometry {
	
	static final int SHAPE_RESOLUTION = 16;
	
	/**
	 * Creates an arc shape
	 * @param radius
	 * @param from The angle from in RADIANS
	 * @param to The angle to in RADIANS
	 * @return
	 */
	public static final Shape createArc(Body body, final float originX, final float originY, final float radius, final float from, final float to) {
		PolygonShape shape = new PolygonShape();
		Vec2[] vertices = new Vec2[SHAPE_RESOLUTION+1+1];
		
		for(int i = 0; i <= SHAPE_RESOLUTION; i++) {
			// Loop is inclusive to make sure final point is in arc
			float angle = from + (to * (float)i / SHAPE_RESOLUTION);
			vertices[i] = new Vec2();
			vertices[i].x = (float)Math.cos(angle) * radius + originX;
			vertices[i].y = (float)Math.sin(angle) * radius + originY;
		}
		vertices[SHAPE_RESOLUTION+1] = new Vec2(originX, originY);
		
		shape.set(vertices, 8);
		return shape;
	}
}
