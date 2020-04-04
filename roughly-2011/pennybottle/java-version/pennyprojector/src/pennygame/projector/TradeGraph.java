package pennygame.projector;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.LinkedList;

import pennygame.lib.msg.MTradesList;
import pennygame.lib.msg.data.ClosedQuote;
import pennygame.projector.queues.PSConn;

/**
 * A {@link Graph} showing the past trades from a set amount of time in the game.
 * @author william
 *
 */
public class TradeGraph extends Graph {

	private static final long serialVersionUID = 6637015494732464967L;
	
	protected static final long DEFAULT_FIRST_TIME = 1296208819 * 1000;
	protected static final long DEFAULT_LAST_TIME = 1296208919 * 1000;
	protected static final int DEFAULT_SMALLEST_PENNIES = 1;
	protected static final int DEFAULT_LARGEST_PENNIES = 200;
	
	protected static final Color TRADE_COLSTART = new Color(0, 0, 128);
	protected static final Stroke TRADE_COLSTART_WIDTH = new BasicStroke(1);
	
	protected static final Color TRADE_COLEND_CLOSED_B = new Color(0, 255, 0);
	protected static final Color TRADE_COLEND_CLOSED_S = new Color(255, 0, 0);
	protected static final Color TRADE_COLEND_TIMEOUTED = new Color(128, 128, 128);
	protected static final Color TRADE_COLEND_CANCELLED = new Color(255, 0, 0);
	
	protected static final Color TRADE_LINE_B = new Color(255, 0, 0);
	protected static final Color TRADE_LINE_S = new Color(0, 255, 0);
	protected static final Stroke TRADE_LINE_WIDTH = new BasicStroke(2);
	
	LinkedList<ClosedQuote> trades = new LinkedList<ClosedQuote>();
	
	public TradeGraph(PSConn serv) {
		super(serv);
		setGraphBounds(DEFAULT_FIRST_TIME, DEFAULT_SMALLEST_PENNIES, DEFAULT_LAST_TIME, DEFAULT_LARGEST_PENNIES);
	}

	@Override
	protected String getXAxisLabel() {
		return "Time";
	}

	@Override
	protected String getYAxisLabel() {
		return "Pennies";
	}

	@Override
	protected String getXAxisLineText(long pointOnLine) {
		Timestamp t = new Timestamp(pointOnLine);
		return DateFormat.getTimeInstance(DateFormat.SHORT).format(t);
	}

	@Override
	protected void drawData(Graphics2D g) {
		for(ClosedQuote q : trades) {
			float gy = (float) graphToUserY(q.getPennies());
			double x1 = graphToUserX(q.getTimeCreated ().getTime());
			double x2 = graphToUserX(q.getTimeAccepted().getTime());
			
			g.setPaint(q.getType() == ClosedQuote.TYPE_BUY ? TRADE_LINE_B : TRADE_LINE_S);
			g.setStroke(TRADE_LINE_WIDTH);
			g.drawLine((int)x1, (int)gy, (int)x2, (int)gy);
			
			g.setPaint(TRADE_COLSTART);
			g.setStroke(TRADE_COLSTART_WIDTH);
			int circleRadius = (int) (Math.sqrt(Math.abs(q.getBottles())) * 2 + 2);
			g.fillOval((int)x1 - circleRadius, (int)gy - circleRadius, circleRadius * 2, circleRadius * 2);
			
			switch(q.getFinishReason()) {
			case ClosedQuote.FINISH_CLOSED:
				g.setPaint(q.getType() == ClosedQuote.TYPE_BUY ? TRADE_COLEND_CLOSED_B : TRADE_COLEND_CLOSED_S);
				g.fillOval((int)x2 - 5, (int)gy - 5, 10, 10);
				break;
			case ClosedQuote.FINISH_CANCELLED:
				g.setPaint(q.getType() == ClosedQuote.TYPE_SELL ? TRADE_COLEND_CLOSED_B : TRADE_COLEND_CLOSED_S); // Other way round to show colour as the same
				g.fillOval((int)x2 - 5, (int)gy - 5, 10, 10);
				break;
			case ClosedQuote.FINISH_TIMEOUTED:
				g.setPaint(TRADE_COLEND_TIMEOUTED);
				g.fillOval((int)x2 - 5, (int)gy - 5, 10, 10);
				break;
			}
		}
	}
	
	/**
	 * Sets the data to display
	 * @param trades
	 */
	public void setData(MTradesList trades) {
		this.trades = trades.getTrades();
		
		long minTime, maxTime;
		int minPennies, maxPennies;
		
		if(trades.getTrades().size() == 0) {
			minTime = DEFAULT_FIRST_TIME;
			maxTime = DEFAULT_LAST_TIME;
			minPennies = DEFAULT_SMALLEST_PENNIES;
			maxPennies = DEFAULT_LARGEST_PENNIES;
		} else {
			minTime = trades.getMinTime();
			maxTime = trades.getMaxTime();
			minPennies = trades.getMinPennies();
			maxPennies = trades.getMaxPennies();
		}
		
		setGraphBounds(minTime, minPennies, maxTime, maxPennies);
		repaint();
	}
}
