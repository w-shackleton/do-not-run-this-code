package uk.digitalsquid.spacegame.spaceitem.interfaces;

/**
 * An interface for objects that affect the game (stars etc)
 * @author william
 *
 */
public interface LevelAffectable {
	
	public AffectData affectLevel();

	/**
	 * A simple class that contains data for affecting the overall level
	 * @author william
	 *
	 */
	public static class AffectData
	{
		public final int scoreAdd;
		
		public AffectData(int scoreAdd) {
			this.scoreAdd = scoreAdd;
		}
	}
}
