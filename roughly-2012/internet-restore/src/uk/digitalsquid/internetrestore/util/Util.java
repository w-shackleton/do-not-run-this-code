package uk.digitalsquid.internetrestore.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

/**
 * The 'Util' class. Every project has one, right? I'll try to keep the size of
 * this one down
 *
 */
public class Util {
	
	/**
	 * A {@link String} that is {@link Parcelable}
	 *
	 */
	public static final class StringP implements Parcelable {
		public final String val;
		public StringP(String val) {
			this.val = val;
		}
		public StringP(Parcel source) {
			val = source.readString();
		}
		
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(val);
		}
		
		public static Creator<StringP> CREATOR = new Creator<Util.StringP>() {
			@Override
			public StringP[] newArray(int size) {
				return new StringP[size];
			}
			@Override
			public StringP createFromParcel(Parcel source) {
				return new StringP(source);
			}
		};
	}
	
	/**
	 * Makes a {@link SparseArray} of {@link String}, {@link Parcelable}.
	 * @param input
	 */
	public static final SparseArray<StringP> stringSparseArrayToParcelable(SparseArray<String> input) {
		SparseArray<StringP> ret = new SparseArray<Util.StringP>();
		if(input == null) return ret;
		for(int i = 0; i < input.size(); i++) {
			int key = input.keyAt(i);
			String val = input.valueAt(i);
			ret.append(key, new StringP(val));
		}
		return ret;
	}
	
	/**
	 * Makes a {@link SparseArray} of {@link String}, {@link Parcelable}.
	 * @param input
	 */
	public static final SparseArray<String> stringPSparseArrayToSparseArray(SparseArray<StringP> input) {
		SparseArray<String> ret = new SparseArray<String>();
		if(input == null) return ret;
		for(int i = 0; i < input.size(); i++) {
			int key = input.keyAt(i);
			String val = input.valueAt(i).val;
			ret.append(key, val);
		}
		return ret;
	}
}
