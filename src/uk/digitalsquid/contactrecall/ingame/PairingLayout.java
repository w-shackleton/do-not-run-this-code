package uk.digitalsquid.contactrecall.ingame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import uk.digitalsquid.contactrecall.misc.Config;
import uk.digitalsquid.contactrecall.misc.ListUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TableLayout;

/**
 * Shows pairs of questions and answers on-screen and allows the user to draw
 * lines between them.
 * @author william
 *
 */
public class PairingLayout extends TableLayout implements Config {
	
	Context context;
	
	View[] lefts = new View[0];
	View[] rights = new View[0];
	/**
	 * A combined array of lefts and rights.
	 */
	View[] views = new View[0];
	
	/**
	 * The relative position of the lefts. This could become stale, so
	 * getRelativePositions() should be called before use of these.
	 */
	private RectF[] leftsRelativePosition = new RectF[0];
	private RectF[] rightsRelativePosition = new RectF[0];
	private RectF[] viewsRelativePosition = new RectF[0];
	
	/**
	 * The user-selected pairings. A value of -1 indicates no current pairing.
	 */
	private int[] pairings;
	
	Paint linePaint = new Paint();
	Paint finalisedLinePaint = new Paint();

	public PairingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		
		setWillNotDraw(false);
		
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		linePaint.setColor(Color.argb(180, 50, 50, 220));
		linePaint.setAntiAlias(true);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth(10 * metrics.density);
		linePaint.setStrokeCap(Cap.ROUND);
		finalisedLinePaint.setColor(Color.argb(255, 50, 50, 220));
		finalisedLinePaint.setAntiAlias(true);
		finalisedLinePaint.setStyle(Style.STROKE);
		finalisedLinePaint.setStrokeWidth(10 * metrics.density);
		finalisedLinePaint.setStrokeCap(Cap.ROUND);
	}
	
	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
		lefts = new View[getChildCount()];
		rights = new View[getChildCount()];
		pairings = new int[getChildCount()];
		Arrays.fill(pairings, -1);

		leftsRelativePosition = new RectF[getChildCount()];
		rightsRelativePosition = new RectF[getChildCount()];

		for(int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if(child instanceof ViewGroup) {
				ViewGroup row = (ViewGroup) child;
				// Assuming row has children left, space, right
				lefts[i] = row.getChildAt(0);
				rights[i] = row.getChildAt(2);
			}
		}
		views = ListUtils.concat(lefts, rights);
		viewsRelativePosition = new RectF[views.length];
	}
	
	private static final class CurrentLineData {
		float startX, startY;
		float endX, endY;
		boolean valid = true;
		public PointF getStart() {
			return new PointF(startX, startY);
		}
		public PointF getEnd() {
			return new PointF(endX, endY);
		}
	}
	
	/**
	 * Holds information about lines currently being drawn.
	 */
	@SuppressLint("UseSparseArrays")
	private Map<Integer, CurrentLineData> currentLineData =
			new HashMap<Integer, PairingLayout.CurrentLineData>();
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int action = event.getActionMasked();
		int index = event.getActionIndex();
		switch(action) {
		case MotionEvent.ACTION_DOWN:
			// Reset all motion data
			Log.d(TAG, "PairingLayout: Down");
			currentLineData.clear();
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.d(TAG, "PairingLayout: Pointer Down");
			CurrentLineData data = new CurrentLineData();
			currentLineData.put(event.getPointerId(index), data);
			PointF correctedStart = getCorrectedChoicePosition(
					new PointF(event.getX(index),
							event.getY(index)));
			if(correctedStart == null) {
				Log.d(TAG, "Invalid pointer location start");
				data.valid = false;
				break;
			}
			// Set start point as corrected pos, end as actual pos.
			data.startX = correctedStart.x;
			data.startY = correctedStart.y;
			data.endX = event.getX(index);
			data.endY = event.getY(index);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			for(int i = 0; i < event.getPointerCount(); i++) {
				int id = event.getPointerId(i);
				CurrentLineData pointerData = currentLineData.get(id);
				pointerData.endX = event.getX(i);
				pointerData.endY = event.getY(i);
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			Log.d(TAG, "PairingLayout: Up");
		case MotionEvent.ACTION_POINTER_UP:
			Log.d(TAG, "PairingLayout: Pointer Up");
			int id = event.getPointerId(index);
			CurrentLineData finishedData = currentLineData.remove(id);
			PointF correctedFinish = getCorrectedChoicePosition(
					new PointF(event.getX(index),
							event.getY(index)));
			if(correctedFinish == null && finishedData.valid) {
				Log.d(TAG, "Invalid pointer location end");
				finishedData.valid = false;
				break;
			}
			finishedData.endX = correctedFinish.x;
			finishedData.endY = correctedFinish.y;
			processNewPairing(finishedData);
			invalidate();
			break;
		}
		return true;
	}
	
	/**
	 * Gets the position of the given {@link View} relative to this
	 * {@link PairingLayout}.
	 * @param view
	 * @return
	 */
	protected final RectF getViewRelativePosition(View view) {
		RectF out = new RectF();
		float width = view.getWidth();
		float height = view.getHeight();
		float left = 0;
		float top = 0;
		
		// The current view we are inspecting as we go through the stack
		View current = view;
		while(true) {
			left += current.getLeft();
			top += current.getTop();
			ViewParent parent = current.getParent();
			if(parent == this) break;
			if(parent == getRootView()) break;
			if(!(parent instanceof View)) break;
			current = (View) parent;
		}
		
		out.left = left;
		out.top = top;
		out.right = left + width;
		out.bottom = top + height;
		
		return out;
	}
	
	protected final void loadRelativePositions() {
		int i = 0;
		for(View view : lefts) {
			leftsRelativePosition[i++] = getViewRelativePosition(view);
		}
		i = 0;
		for(View view : rights) {
			rightsRelativePosition[i++] = getViewRelativePosition(view);
		}
		i = 0;
		for(View view : views) {
			viewsRelativePosition[i++] = getViewRelativePosition(view);
		}
	}
	
	/**
	 * Gets the point on-screen where a line should start or finish, based
	 * upon the user's finger-based estimate.
	 * @param userPosition
	 * @return A corrected point within a question or answer, or
	 * <code>null</code> if the userPosition isn't in a {@link View}.
	 */
	protected PointF getCorrectedChoicePosition(PointF userPosition) {
		loadRelativePositions();
		for(RectF bounds : viewsRelativePosition) {
			if(bounds.contains(userPosition.x, userPosition.y))
				return new PointF(bounds.centerX(), bounds.centerY());
		}
		return null;
	}
	
	protected void processNewPairing(CurrentLineData datum) {
		// Re-process positions
		loadRelativePositions();

		// One of start or end should be in lefts, other in rights.
		PointF start = datum.getStart();
		PointF end = datum.getEnd();
		int leftIdx = -1, rightIdx = -1;
		// Find left and right views that contain the start and end points
		for(int i = 0; i < lefts.length; i++) {
			RectF bounds = leftsRelativePosition[i];
			if(bounds.contains(start.x, start.y) ||
					bounds.contains(end.x, end.y))
				leftIdx = i;
		}
		for(int i = 0; i < rights.length; i++) {
			RectF bounds = rightsRelativePosition[i];
			if(bounds.contains(start.x, start.y) ||
					bounds.contains(end.x, end.y))
				rightIdx = i;
		}
		if(leftIdx == -1 || rightIdx == -1) return;

		// Remove any existing pairings to this rightIdx
		for(int i = 0; i < pairings.length; i++) {
			if(pairings[i] == rightIdx)
				pairings[i] = -1;
		}
		pairings[leftIdx] = rightIdx;
	}
	
	/**
	 * We always want to get motion events here.
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		super.onInterceptTouchEvent(event);
		return true;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.d(TAG, "PairingLayout: Drawing " + currentLineData.size() + " lines");
		for(CurrentLineData line : currentLineData.values()) {
			if(!line.valid) continue; 
			canvas.drawLine(
					line.startX,
					line.startY,
					line.endX,
					line.endY,
					linePaint);
		}
		for(int left = 0; left < pairings.length; left++) {
			int right = pairings[left];
			if(right == -1) continue;
			if(leftsRelativePosition[left] == null ||
					rightsRelativePosition[right] == null)
				loadRelativePositions();
			float lx = leftsRelativePosition[left].centerX();
			float ly = leftsRelativePosition[left].centerY();
			float rx = rightsRelativePosition[right].centerX();
			float ry = rightsRelativePosition[right].centerY();
			canvas.drawLine(lx, ly, rx, ry, finalisedLinePaint);
		}
	}

	/**
	 * Gets the current pairings. A value of -1 indicates no pairing.
	 * @return
	 */
	public int[] getPairings() {
		return pairings;
	}

	public void setPairings(int[] pairings) {
		this.pairings = pairings;
	}
}
