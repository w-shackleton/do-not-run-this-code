package uk.digitalsquid.spacegame.spaceitem.assistors;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;

public final class Spring implements Moveable {
	
	private float[] springPoints;
	private float[] velocities;
	private float[] masses;
	private float[] springStiffness;
	
	private final int numPoints;
	
	private final float springConstant;
	
	public Spring(int pointsInSpring, float startX, float startY, float finishX, float finishY, float springConstant) {
		springPoints = new float[pointsInSpring * 2];
		velocities = new float[pointsInSpring * 2];
		masses = new float[pointsInSpring];
		springStiffness = new float[pointsInSpring];
		for(int i = 0; i < masses.length; i++) {
			masses[i] = 1;
			springStiffness[i] = 1;
		}
		numPoints = springPoints.length / 2;
		this.springConstant = springConstant;
		setPosition(startX, startY, finishX, finishY);
		
	}
	public Spring(int pointsInSpring, Coord start, Coord fin, float springConstant) {
		this(pointsInSpring, (float)start.x, (float)start.y, (float)fin.x, (float)fin.y, springConstant);
	}
	
	@Override public void move(float millistep, float speedScale) { }

	@Override
	public void drawMove(float millistep, float speedscale) {
		// All points except first and last
		for(int i = 1; i < numPoints - 1; i++) {
			int pointStart = i * 2;
			float x1 = springPoints[pointStart - 2];
			float y1 = springPoints[pointStart - 1];
			float x2 = springPoints[pointStart + 2];
			float y2 = springPoints[pointStart + 3];
			float x = springPoints[pointStart];
			float y = springPoints[pointStart + 1];
			
			// Each spring distance * stiffness + dampener
			velocities[pointStart  ] -= ((x1 - x) * springStiffness[i] + (x2 - x) * springStiffness[i] + (velocities[i] * springConstant)) *
				millistep / 1000f;
			velocities[pointStart+1] -= ((y1 - y) * springStiffness[i] + (y2 - y) * springStiffness[i] + (velocities[i] * springConstant)) *
				millistep / 1000f;
			
			springPoints[pointStart  ] += velocities[pointStart  ] * millistep / 1000f;
			springPoints[pointStart+1] += velocities[pointStart+1] * millistep / 1000f;
		}
	}
	
	public void setPosition(float startX, float startY, float finishX, float finishY) {
		float stepX = (finishX - startX) / numPoints;
		float stepY = (finishY - startY) / numPoints;
		
		// Set incremental points
		for(int i = 0; i < numPoints * 2; i += 2) {
			springPoints[i  ] = stepX * i + startX;
			springPoints[i+1] = stepY * i + startY;
		}
	}
	
	public void setEnds(double startX, double startY, double finishX, double finishY) {
		setEnds((float)startX, (float)startY, (float)finishX, (float)finishY);
	}
	public void setEnds(float startX, float startY, float finishX, float finishY) {
		springPoints[0] = startX;
		springPoints[1] = startY;
		springPoints[springPoints.length-2] = finishX;
		springPoints[springPoints.length-1] = finishY;
	}
	
	public float[] getSpringPoints() {
		return springPoints;
	}
}
