package uk.digitalsquid.spacegame.spaceitem.interfaces;

public interface Moveable
{
	public void move(float millistep, float speedScale);
	
	/**
	 * This version of move only gets called once per draw
	 * @param millistep
	 * @param speedscale
	 */
	public void drawMove(float millistep, float speedscale);
}
