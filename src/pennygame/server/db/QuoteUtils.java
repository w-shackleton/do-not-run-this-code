package pennygame.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import pennygame.lib.msg.MMyInfo;
import pennygame.lib.msg.MOpenQuotesList;
import pennygame.lib.msg.MPutQuote;
import pennygame.lib.msg.data.OpenQuote;
import pennygame.lib.msg.data.PB;
import pennygame.lib.queues.LoopingThread;
import pennygame.server.client.CMulticaster;

/**
 * Utilities for processing quotes
 * @author william
 *
 */
public final class QuoteUtils {
	
	private final PreparedStatement putQuoteStatement, getOpenQuotesStatement, getUserClosedTradesStatement, getUserOpenQuotesStatement, getUserMoneyStatement;
	private final PreparedStatement getQuoteInfoStatement, requestQuoteLockStatement;
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
		getUserClosedTradesStatement = conn.prepareStatement(
				"SELECT * FROM " +
				"(SELECT type, idfrom, idto, bottles, pennies, timeaccepted FROM quotes WHERE " +
				"status='closed' AND (idfrom=? OR idto=?) " +
				"ORDER BY timeaccepted DESC LIMIT 40) as tbl " +
				"ORDER BY timeaccepted;"); // TODO: Optimise this's OR statement with an index?
		getUserOpenQuotesStatement = conn.prepareStatement(
				"SELECT * FROM quotes WHERE status='open' AND idfrom=? ORDER BY timecreated;");
		getUserMoneyStatement = conn.prepareStatement(
				"SELECT (bottles - q.pbottles) AS nbottles, " +
				"(pennies + q.pvalue) AS npennies " +
				"FROM users, " +
				"(SELECT 0 as pbottles, 0 as pvalue UNION ALL " +
				"SELECT COALESCE(SUM(quotes.bottles), 0) as pbottles, COALESCE(SUM(CAST(quotes.pennies AS SIGNED) * quotes.bottles), 0) AS pvalue FROM quotes WHERE idfrom=? AND status='open' UNION ALL " +
				"SELECT COALESCE(SUM(quotes.bottles), 0) as pbottles, COALESCE(SUM(CAST(quotes.pennies AS SIGNED) * quotes.bottles), 0) AS pvalue FROM quotes WHERE idfrom=? AND status='open' AND type='buy' UNION ALL " +
				"SELECT COALESCE(SUM(quotes.bottles), 0) as pbottles, COALESCE(SUM(CAST(quotes.pennies AS SIGNED) * quotes.bottles), 0) AS pvalue FROM quotes WHERE idfrom=? AND status='open' AND type='sell') " +
				"AS q WHERE users.id=?;"
				);
		
		getQuoteInfoStatement = conn.prepareStatement(
				"SELECT quotes.id, quotes.type, users.friendlyname AS fromname, quotes.pennies, " +
				"quotes.bottles, quotes.bottles * CAST(quotes.pennies AS SIGNED) AS value, quotes.timecreated AS time " +
				"FROM quotes, users WHERE quotes.idfrom = users.id AND quotes.id=?;");
		requestQuoteLockStatement = conn.prepareStatement(
				"UPDATE quotes SET lockid=?, timeaccepted=NOW() WHERE id=? AND status='open' AND lockid IS NULL;");
	}
	
	/**
	 * Puts a new quote into the list.
	 * @param type The type of quote, as defined in {@link MPutQuote}
	 * @param userId The ID of the user to put the quote under
	 * @param pennies
	 * @param bottles
	 * @throws SQLException If there is an error in the SQL syntax or connection
	 * @return true if succesful, false if not
	 */
	public synchronized boolean putQuote(int type, int userId, int pennies, int bottles) throws SQLException {
		LinkedList<PB> money = getUserMoney(userId, 1); // UserID doesn't matter here
		// We want items 2 and 3 (potential money)
		
		// Least possible pennies
		int lPennies = money.get(2).getPennies() - Math.abs(pennies * bottles);
		// Least possible bottles
		int lBottles = money.get(3).getBottles() - Math.abs(bottles);
		
		
		if(type == MPutQuote.TYPE_BUY)
		{
			if(lPennies < 0) return false; // Not enough pennies to buy
			putQuoteStatement.setString(1, "buy");
			putQuoteStatement.setInt(4, -Math.abs(bottles)); // Negative values for buy quotes; makes easier processing later
		}
		else
		{
			if(lBottles < 0) return false; // Not enough bottles to sell
			putQuoteStatement.setString(1, "sell");
			putQuoteStatement.setInt(4, Math.abs(bottles)); // Negative values for buy quotes; makes easier processing later
		}
		
		putQuoteStatement.setInt(2, userId);
		putQuoteStatement.setInt(3, Math.abs(pennies));
		
		putQuoteStatement.executeUpdate();
		
		pushOpenQuotes();
		
		return true;
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
	
	public synchronized OpenQuote getQuoteInfo(int id) throws SQLException {
		getQuoteInfoStatement.setInt(1, id);
		ResultSet rs = getQuoteInfoStatement.executeQuery();
		while(rs.next()) {
			return new OpenQuote(id, rs.getString("type").equals("buy") ? OpenQuote.TYPE_BUY : OpenQuote.TYPE_SELL, rs.getString("fromname"), rs.getInt("pennies"), rs.getInt("bottles"), rs.getTimestamp("time"));
		}
		return null;
	}
	
	/**
	 * Gets the user's current money
	 * @param id the user's ID
	 * @return a {@link LinkedList} containing: current money, potential money, potential worst money, potential most money
	 * @throws SQLException 
	 */
	public synchronized LinkedList<PB> getUserMoney(int id, int estimatedWorth) throws SQLException {
		LinkedList<PB> nums = new LinkedList<PB>();
		
		getUserMoneyStatement.setInt(1, id);
		getUserMoneyStatement.setInt(2, id);
		getUserMoneyStatement.setInt(3, id);
		getUserMoneyStatement.setInt(4, id);
		
		ResultSet rs = getUserMoneyStatement.executeQuery();
		
		while(rs.next()) {
			nums.add(new PB(rs.getInt("npennies"), rs.getInt("nbottles"), estimatedWorth));
		}
		
		return nums;
	}
	
	public MMyInfo getUserMoneyMessage(int id, int estimatedWorth) throws SQLException {
		LinkedList<PB> nums = getUserMoney(id, estimatedWorth);
		return new MMyInfo(nums.get(0), nums.get(1), nums.get(2), nums.get(3));
	}
	
	public synchronized void pushUserMoney(int id, int estimatedWorth) throws SQLException {
		LinkedList<PB> nums = getUserMoney(id, estimatedWorth);
		MMyInfo info = new MMyInfo(nums.get(0), nums.get(1), nums.get(2), nums.get(3));
		
		multicast.sendMessageToClient(id, info);
	}
	
	/**
	 * Tries to lock the quote for accepting
	 * @param userId
	 * @param quoteId
	 * @return true if sucessful, false if someone else got there first
	 * @throws SQLException 
	 */
	public synchronized boolean requestLockQuote(int userId, int quoteId) throws SQLException {
		requestQuoteLockStatement.setInt(1, userId);
		requestQuoteLockStatement.setInt(2, quoteId);
		
		int rows = requestQuoteLockStatement.executeUpdate(); // Will return 0 if row already grabbed
		return rows == 1;
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
		
		protected final PreparedStatement deleteOldQuotes, getOldQuoteUserIds;
		
		protected Object waitObject;

		protected QuotePurger(Connection conn) throws SQLException {
			super("Old quote purger");
			deleteOldQuotes = conn.prepareStatement(
					"UPDATE quotes SET status='timeout', timeaccepted=NOW() " +
					"WHERE status='open' AND timecreated < TIMESTAMPADD(SECOND, ?, NOW()) AND lockid IS NULL;");
			getOldQuoteUserIds = conn.prepareStatement(
					"SELECT idfrom AS userid FROM quotes " +
					"WHERE status='open' AND timecreated < TIMESTAMPADD(SECOND, ?, NOW()) AND lockid IS NULL GROUP BY idfrom;");
		}
		
		@Override
		protected void setup() {
			waitObject = new Object();
		}

		@Override
		protected void loop() {
			try {
				deleteOldQuotes.setInt(1, -quoteTimeout);
				getOldQuoteUserIds.setInt(1, -quoteTimeout);
				
				ResultSet usersToNotify = getOldQuoteUserIds.executeQuery(); // Get users to notify of change
				
				int numRows = deleteOldQuotes.executeUpdate();
				
				// Now list has changed, notify users
				while(usersToNotify.next()) {
					pushUserMoney(usersToNotify.getInt("userid"), 42);
				}
				
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
