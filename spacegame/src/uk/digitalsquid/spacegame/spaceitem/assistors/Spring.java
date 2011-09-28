package uk.digitalsquid.spacegame.spaceitem.assistors;

import org.jbox2d.common.Vec2;

import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;

public final class Spring implements Moveable {
	
	private float[] springPoints;
	private float[] velocities;
	private float[] forces;
	private float[] masses;
	private float springPower;
	
	private final int numPoints;
	
	private float springConstant;
	
	private boolean startLocked = true, endLocked = true;
	
	public Spring(int pointsInSpring, float startX, float startY, float finishX, float finishY, float springConstant, float springPower) {
		springPoints = new float[pointsInSpring * 2];
		velocities = new float[pointsInSpring * 2];
		forces = new float[pointsInSpring * 2];
		masses = new float[pointsInSpring];
		for(int i = 0; i < masses.length; i++) {
			masses[i] = 1;
		}
		this.springPower = springPower;
		numPoints = springPoints.length / 2;
		this.springConstant = springConstant;
		setPosition(startX, startY, finishX, finishY);
		
	}
	
	public Spring(int pointsInSpring, float startX, float startY, float finishX, float finishY, float springConstant) {
		springPoints = new float[pointsInSpring * 2];
		velocities = new float[pointsInSpring * 2];
		forces = new float[pointsInSpring * 2];
		masses = new float[pointsInSpring];
		for(int i = 0; i < masses.length; i++) {
			masses[i] = 1;
		}
		springPower = 3;
		numPoints = springPoints.length / 2;
		this.springConstant = springConstant;
		setPosition(startX, startY, finishX, finishY);
		
	}
	public Spring(int pointsInSpring, Vec2 start, Vec2 fin, float springConstant) {
		this(pointsInSpring, (float)start.x, (float)start.y, (float)fin.x, (float)fin.y, springConstant);
	}
	
	@Override public void move(float millistep, float speedScale) { }

	@Override
	public void drawMove(float millistep, float speedscale) {
		float x1, y1, x2, y2, x, y;
		
		// Calculate ALL forces first
		final int end = numPoints - 1;
		for(int i = 0; i < numPoints; i++) {
			int pointStart = i * 2;
			x = springPoints[pointStart];
			y = springPoints[pointStart + 1];
			x1 = i == 0   ? x : springPoints[pointStart - 2]; // 0 if first
			y1 = i == 0   ? y : springPoints[pointStart - 1]; // 0 if first
			x2 = i == end ? x : springPoints[pointStart + 2];
			y2 = i == end ? y : springPoints[pointStart + 3];
			
			// Each spring distance * stiffness + dampener
			forces[pointStart  ] = ((x1 - x) * springPower + (x2 - x) * springPower - (velocities[pointStart  ] * springConstant));
			forces[pointStart+1] = ((y1 - y) * springPower + (y2 - y) * springPower - (velocities[pointStart+1] * springConstant));
		}
		
		// All points except sometimes first and last
		
		// Figure out how many points to do calc for.
		final int start2 = startLocked ? 1 : 0;
		final int end2 = endLocked ? numPoints - 1 : numPoints;
		for(int i = start2; i < end2; i++) {
			int pointStart = i * 2;
			
			velocities[pointStart  ] += forces[pointStart  ] * millistep / 1000f * 10;
			velocities[pointStart+1] += forces[pointStart+1] * millistep / 1000f * 10;
			
			springPoints[pointStart  ] += velocities[pointStart  ] * millistep / 1000f * 10;
			springPoints[pointStart+1] += velocities[pointStart+1] * millistep / 1000f * 10;
		}
	}
	
	/**
	 * Sets the spring in a straight line.
	 * @param startX
	 * @param startY
	 * @param finishX
	 * @param finishY
	 */
	public void setPosition(float startX, float startY, float finishX, float finishY) {
		float stepX = (finishX - startX) / numPoints;
		float stepY = (finishY - startY) / numPoints;
		
		// Set incremental points
		for(int i = 0; i < numPoints * 2; i += 2) {
			springPoints[i  ] = stepX * i + startX;
			springPoints[i+1] = stepY * i + startY;
		}
	}
	
	/**
	 * Force sets the position of both ends of the spring. Calling this locks both ends.
	 * @param startX
	 * @param startY
	 * @param finishX
	 * @param finishY
	 */
	public void setEnds(double startX, double startY, double finishX, double finishY) {
		setEnds((float)startX, (float)startY, (float)finishX, (float)finishY);
	}
	public void setEnds(float startX, float startY, float finishX, float finishY) {
		springPoints[0] = startX;
		springPoints[1] = startY;
		springPoints[springPoints.length-2] = finishX;
		springPoints[springPoints.length-1] = finishY;
		
		startLocked = true;
		endLocked = true;
	}
	
	/**
	 * Force sets the position of only one end of the spring. Calling this locks the end.
	 * @param finishX
	 * @param finishY
	 */
	public void setEnd(double finishX, double finishY) {
		setEnd((float)finishX, (float)finishY);
	}
	public void setEnd(float finishX, float finishY) {
		springPoints[springPoints.length-2] = finishX;
		springPoints[springPoints.length-1] = finishY;
		
		startLocked = false;
		endLocked = true;
	}
	
	public float[] getSpringPoints() {
		return springPoints;
	}
	
	private Vec2 endForceCoord = new Vec2();
	
	public Vec2 calculateEndForce() {
		endForceCoord.x = forces[forces.length-2];
		endForceCoord.y = forces[forces.length-1];
		return endForceCoord;
	}

	public float getSpringConstant() {
		return springConstant;
	}

	public void setSpringConstant(float springConstant) {
		this.springConstant = springConstant;
	}
	
	public float getSpringPower() {
		return springPower;
	}

	public void setSpringPower(float springPower) {
		this.springPower = springPower;
	}
}
