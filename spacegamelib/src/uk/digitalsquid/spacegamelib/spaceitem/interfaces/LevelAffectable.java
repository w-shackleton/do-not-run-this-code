package uk.digitalsquid.spacegamelib.spaceitem.interfaces;

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
		public final boolean incScore;
		public final boolean incDisplayedScore;
		
		public AffectData(boolean incScore, boolean incDisplayedScore) {
			this.incScore = incScore;
			this.incDisplayedScore = incDisplayedScore;
		}
	}
}
