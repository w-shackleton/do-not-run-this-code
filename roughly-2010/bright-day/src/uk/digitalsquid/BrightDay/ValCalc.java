/*
 * This file is part of Bright Day.
 * 
 * Bright Day is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Bright Day is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Bright Day.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.digitalsquid.BrightDay;


public class ValCalc
{
	//private float minHeight;
	//private float maxHeight;
	private float shift;
	private float gamma;
	private float stretch;
	//private float stretchY;
	private float outOfX;
	//private float outOfY;
	public ValCalc(
			float minHeight,
			float maxHeight,
			float shift,
			float gamma,
			float stretch,
			//float stretchY,
			float outOfX,
			float outOfY
			)
	{
		stretch += 20;
		this.gamma = gamma;
		//this.maxHeight = maxHeight;
		//this.minHeight = minHeight;
		this.outOfX = outOfX;
		//this.outOfY = outOfY;
		this.shift = shift;
		this.stretch = stretch;
		//this.stretchY = stretchY;
		
		curveHeight = (float)(maxHeight - minHeight) / 255f * (float)outOfY / 2f;
		curveHeightShift = outOfY - ((float)minHeight / 255f * (float)outOfY) - curveHeight;
		
		//posT = ((shift + (stretch / 2) + 100) % 100) * outOfX / 100;
		//posB = ((shift - (stretch / 2) + 100) % 100) * outOfX / 100;
	}
	float tempCosb, tempCos2b;
	int tempSigb;
	float curveHeight, curveHeightShift;
	//float posT, posB;
	boolean inner;
	public float getPos(float i) // This somehow works!
	{
		i = (i / outOfX * 2 - 1 + (shift / 50 - 1) + 3) % 2 - 1;
		i = (float) (Math.signum(i) * Math.pow(Math.abs(i),stretch / 50));
		i = (i + 1 - (shift / 50 - 1)) * outOfX / 2;
		tempCosb = (float)Math.cos(((float)i) / outOfX * Math.PI * 2 +
				((float)shift - 50f) / 50f * (float)Math.PI);
    	tempSigb = (int) Math.signum(tempCosb);
    	tempCos2b = (float) Math.pow(Math.abs(tempCosb), 1 / (gamma / 100f + 1)) * tempSigb;
    	
		/*tempCos2b++;
		tempCos2b /= 2;
    	tempCos2b = (float) Math.pow(tempCos2b, stretchY / 50);
		tempCos2b *= 2;
		tempCos2b--;*/
    	
    	//Log.v("BrightDay", "VC: In:  " + tempCos2b + ", " + stretchPos);
		
    	tempCos2b *= curveHeight;
    	tempCos2b += curveHeightShift;
    	return tempCos2b;
	}
}
