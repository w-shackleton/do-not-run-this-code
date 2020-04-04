package uk.digitalsquid.spacegame.subviews;

import java.io.Serializable;

import org.jbox2d.common.Vec2;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View.BaseSavedState;

/**
 * Persistently stores the game's current state
 * @author william
 *
 */
public class GameState extends BaseSavedState {
	
	public Vec2 userPos, userVelocity;
	public Vec2 avgPos;
	public Vec2[] screenPos;
	
	public float userZoom;
	
	public GameState(Parcel parcel) {
		super(parcel);
		userPos = readVec2(parcel);
		userVelocity = readVec2(parcel);
		avgPos = readVec2(parcel);
		
		final int screenPosLength = parcel.readInt();
		screenPos = new Vec2[screenPosLength];
		for(int i = 0; i < screenPosLength; i++) {
			screenPos[i] = readVec2(parcel);
		}
		
		userZoom = parcel.readFloat();
	}
	
	public GameState(Parcelable parcelable) {
		super(parcelable);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		writeVec2(dest, userPos);
		writeVec2(dest, userVelocity);
		writeVec2(dest, avgPos);
		
		// Write array manually to avoid serialization
		if(screenPos == null) screenPos = new Vec2[0];
		dest.writeInt(screenPos.length);
		for(Vec2 vec : screenPos) {
			writeVec2(dest, vec);
		}
		
		dest.writeFloat(userZoom);
	}
	
	/**
	 * {@link Vec2} is only {@link Serializable}, so this is quicker
	 * @param dest
	 * @param write
	 */
	private void writeVec2(Parcel dest, Vec2 write) {
		if(write == null) {
			dest.writeFloat(0);
			dest.writeFloat(0);
		} else {
			dest.writeFloat(write.x);
			dest.writeFloat(write.y);
		}
	}
	private Vec2 readVec2(Parcel from) {
		return new Vec2(from.readFloat(), from.readFloat());
	}
	
	public static final Creator<GameState> CREATOR = new Creator<GameState>() {
		@Override
		public GameState createFromParcel(Parcel source) {
			return new GameState(source);
		}

		@Override
		public GameState[] newArray(int size) {
			return new GameState[size];
		}
		
	};
}
