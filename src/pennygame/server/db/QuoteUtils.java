package pennygame.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import pennygame.lib.msg.MOpenQuotesList;
import pennygame.lib.msg.MPutQuote;
import pennygame.lib.msg.data.OpenQuote;
import pennygame.lib.queues.LoopingThread;
import pennygame.server.client.CMulticaster;

/**
 * Utilities for processing quotes
 * @author william
 *
 */
public final class QuoteUtils {
	
	private final PreparedStatement putQuoteStatement, getOpenQuotesStatement;
	private final CMulticaster multicast;
	
	protected final QuotePurger quotePurger;
	
	QuoteUtils(Connection conn, CMulticaster multicast) throws SQLException {
		this.multicast = multicast;
		this.quotePurger = new QuotePurger(conn);
		quotePurger.start();
		
		putQuoteStatement = conn.prepareStatement("INSERT INTO quotes(type, idfrom, pennies, bottles) VALUES (?, ?, ?, ?);");
		getOpenQuotesStatement = conn.prepareStatement(
				"SELECT * FROM " +
				"((SELECT quotes.id, quotes.type, users.friendlyname AS fromname, quotes.pennies, quotes.bottles, quotes.bottles * CAST(quotes.pennies AS SIGNED) AS value, quotes.timecreated AS time " +
				"FROM quotes, users WHERE status='open' AND type='sell' AND quotes.idfrom = users.id ORDER BY pennies LIMIT ?) " +
				"UNION ALL " +
				" (SELECT quotes.id, quotes.type, users.friendlyname AS fromname, quotes.pennies, quotes.bottles, quotes.bottles * CAST(quotes.pennies AS SIGNED) AS value, quotes.timecreated AS time " +
				"FROM quotes, users WHERE status='open' AND type='buy'  AND quotes.idfrom = users.id ORDER BY pennies DESC LIMIT ?)) " +
				"AS tbl ORDER BY tbl.pennies DESC;"); // This searches through all buy and sells, unions them and then sorts the result.
	}
	
	/**
	 * Puts a new quote into the list.
	 * @param type The type of quote, as defined in {@link MPutQuote}
	 * @param userId The ID of the user to put the quote under
	 * @param pennies
	 * @param bottles
	 * @throws SQLException 
	 */
	public synchronized void putQuote(int type, int userId, int pennies, int bottles) throws SQLException {
		if(type == MPutQuote.TYPE_BUY)
		{
			putQuoteStatement.setString(1, "buy");
			putQuoteStatement.setInt(4, -Math.abs(bottles)); // Negative values for buy quotes; makes easier processing later
		}
		else
		{
			putQuoteStatement.setString(1, "sell");
			putQuoteStatement.setInt(4, Math.abs(bottles)); // Negative values for buy quotes; makes easier processing later
		}
		
		putQuoteStatement.setInt(2, userId);
		putQuoteStatement.setInt(3, Math.abs(pennies));
		
		putQuoteStatement.executeUpdate();
		
		pushOpenQuotes();
	}
	
	private int numberOfQuotes = 15;
	
	/**
	 * Sets the number of EACH quote to show, ie 15 will show 30 in total
	 * @param number
	 */
	public synchronized void setNumQuotes(int number) {
		numberOfQuotes = number;
	}
	
	public int getNumQuotes() {
		return numberOfQuotes;
	}
	
	public int getTotalNumQuotes() {
		return numberOfQuotes * 2;
	}
	
	/**
	 * Gets the list of currently open quotes from the DB
	 * @return
	 * @throws SQLException 
	 */
	public synchronized LinkedList<OpenQuote> getOpenQuotes() throws SQLException {
		LinkedList<OpenQuote> list = new LinkedList<OpenQuote>();
		
		getOpenQuotesStatement.setInt(1, numberOfQuotes);
		getOpenQuotesStatement.setInt(2, numberOfQuotes);
		ResultSet rs = getOpenQuotesStatement.executeQuery();
		
		while(rs.next()) {
			String type = rs.getString("type");
			int t;
			if(type.equals("buy")) t = OpenQuote.TYPE_BUY;
			else t = OpenQuote.TYPE_SELL;
			list.add(new OpenQuote(rs.getInt("id"), t, rs.getString("fromname"), rs.getInt("pennies"), rs.getInt("bottles"), rs.getTimestamp("time")));
		}
		
		return list;
	}
	
	public synchronized void pushOpenQuotes() {
		try {
			multicast.multicastMessage(new MOpenQuotesList(getOpenQuotes(), getTotalNumQuotes()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized int getQuoteTimeout() {
		return quotePurger.getQuoteTimeout();
	}
	
	public synchronized void setQuoteTimeout(int secs) {
		quotePurger.setQuoteTimeout(secs);
	}
	
	/**
	 * Deletes old quotes (older than a specified timeout) from the database
	 * @author william
	 *
	 */
	public class QuotePurger extends LoopingThread {
		
		protected PreparedStatement deleteOldQuotes;
		
		protected Object waitObject;

		protected QuotePurger(Connection conn) throws SQLException {
			super("Old quote purger");
			deleteOldQuotes = conn.prepareStatement(
					"UPDATE quotes SET status='timeout' " +
					"WHERE status='open' AND timecreated < TIMESTAMPADD(SECOND, ?, NOW());");
		}
		
		@Override
		protected void setup() {
			waitObject = new Object();
		}

		@Override
		protected void loop() {
			try {
				deleteOldQuotes.setInt(1, -quoteTimeout);
				int numRows = deleteOldQuotes.executeUpdate();
				
				if(numRows > 0) { // List has changed, resend
					System.out.println("Timeouted " + numRows + " old quotes from the DB, pushing new list");
				
					pushOpenQuotes();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			synchronized(waitObject) {
				try {
					waitObject.wait(3000);
				} catch (InterruptedException e1) { }
			}
		}
		
		protected int quoteTimeout = 60;
		
		protected synchronized void setQuoteTimeout(int secs) {
			quoteTimeout = Math.abs(secs);
		}
		
		protected synchronized int getQuoteTimeout() {
			return quoteTimeout;
		}
		
		@Override
		public synchronized void beginStopping() {
			synchronized(waitObject) {
				waitObject.notify();
			}
			super.beginStopping();
		}
	}
	
	public synchronized void beginStopping() {
		quotePurger.beginStopping();
	}
}
