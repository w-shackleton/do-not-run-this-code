package pennygame.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import pennygame.lib.GlobalPreferences;
import pennygame.lib.msg.MMyInfo;
import pennygame.lib.msg.MMyQuotesList;
import pennygame.lib.msg.MOpenQuotesList;
import pennygame.lib.msg.MPutQuote;
import pennygame.lib.msg.MTradesList;
import pennygame.lib.msg.data.ClosedQuote;
import pennygame.lib.msg.data.OpenQuote;
import pennygame.lib.msg.data.PB;
import pennygame.lib.msg.tr.MTAcceptResponse;
import pennygame.lib.msg.tr.MTCancelResponse;
import pennygame.lib.queues.LoopingThread;
import pennygame.server.client.CMulticaster;

/**
 * Utilities for processing quotes
 * @author william
 *
 */
public final class QuoteUtils {
	
	private final PreparedStatement putQuoteStatement, getOpenQuotesStatement, getUserClosedTradesStatement, getUserOpenQuotesStatement, getUserMoneyStatement;
	private final PreparedStatement getQuoteInfoStatement, requestQuoteLockStatement, acceptLockedQuoteStatement, declineLockedQuoteStatement, acceptLockQuoteUpdateMyMoney, acceptLockQuoteUpdateOtherMoney, cancelQuoteStatement;
	private final PreparedStatement getTradeHistoryStatement, getTradeHistoryRangeStatement;
	private final CMulticaster multicast;
	
	protected final QuotePurger quotePurger;
	
	private final Connection quoteAcceptingConn;
	
	private final GameUtils gameUtils;
	
	/**
	 * A map of users' guesses at the bottle's worth. This is also stored in the DB, and is stored here for ease of access
	 */
	protected final ConcurrentHashMap<Integer, Integer> userWorthGuesses;
	
	/**
	 * 
	 * @param conn
	 * @param quoteAcceptingConn
	 * @param miscDataGetConn
	 * @param connPool A pool of 4 connections, just to spread server load
	 * @param multicast
	 * @throws SQLException
	 */
	QuoteUtils(Connection conn, Connection quoteAcceptingConn, Connection miscDataGetConn, Connection[] connPool, CMulticaster multicast, GameUtils gameUtils) throws SQLException {
		this.multicast = multicast;
		this.quotePurger = new QuotePurger(conn);
		this.quoteAcceptingConn = quoteAcceptingConn;
		this.gameUtils = gameUtils;
		
		{
			userWorthGuesses = new ConcurrentHashMap<Integer, Integer>();
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM worthguess ORDER BY id;");
			while(rs.next()) {
				synchronized(userWorthGuesses) {
					userWorthGuesses.put(rs.getInt("userid"), rs.getInt("guess")); // Load each item into the list (load cache)
				}
			}
		}
		
		quotePurger.start();
		
		// TODO: Switch a lot of these to views?
		putQuoteStatement = connPool[0].prepareStatement("INSERT INTO quotes(type, idfrom, pennies, bottles) VALUES (?, ?, ?, ?);");
		getOpenQuotesStatement = conn.prepareStatement( // TODO: get rid of 'value', as I think it isn't used
				"SELECT * FROM " +
				"((SELECT quotes.id, quotes.type, users.friendlyname AS fromname, quotes.idfrom, quotes.pennies, quotes.bottles, quotes.bottles * CAST(quotes.pennies AS SIGNED) AS value, quotes.timecreated AS time " +
				"FROM quotes, users WHERE status='open' AND type='sell' AND quotes.idfrom = users.id ORDER BY pennies LIMIT ?) " +
				"UNION ALL " +
				" (SELECT quotes.id, quotes.type, users.friendlyname AS fromname, quotes.idfrom, quotes.pennies, quotes.bottles, quotes.bottles * CAST(quotes.pennies AS SIGNED) AS value, quotes.timecreated AS time " +
				"FROM quotes, users WHERE status='open' AND type='buy'  AND quotes.idfrom = users.id ORDER BY pennies DESC LIMIT ?)) " +
				"AS tbl ORDER BY tbl.pennies DESC;"); // This searches through all buy and sells, unions them and then sorts the result.
		getUserClosedTradesStatement = connPool[3].prepareStatement(
				"SELECT * FROM " +
				"(SELECT quotes.id, type, u1.friendlyname AS fromname, u2.friendlyname AS toname, quotes.bottles, quotes.pennies, timeaccepted AS time " +
				"FROM quotes " +
				"JOIN users AS u1 ON quotes.idfrom = u1.id " +
				"JOIN users AS u2 ON quotes.idto = u2.id " +
				"WHERE status='closed' AND (idfrom=? OR idto=?) " +
				"ORDER BY time DESC LIMIT 40) " +
				"as tbl ORDER BY time;");
		getUserOpenQuotesStatement = conn.prepareStatement(
				"SELECT id, type, bottles, pennies, timecreated AS time FROM quotes WHERE status='open' AND idfrom=? ORDER BY timecreated;");
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
		
		getQuoteInfoStatement = connPool[2].prepareStatement(
				"SELECT quotes.id, quotes.type, users.friendlyname AS fromname, quotes.idfrom, quotes.pennies, " +
				"quotes.bottles, quotes.bottles * CAST(quotes.pennies AS SIGNED) AS value, quotes.timecreated AS time " +
				"FROM quotes, users WHERE quotes.idfrom = users.id AND quotes.id=?;");
		requestQuoteLockStatement = connPool[2].prepareStatement(
				"UPDATE quotes SET lockid=?, timeaccepted=NOW() WHERE id=? AND status='open' AND lockid IS NULL;");
		
		acceptLockedQuoteStatement = quoteAcceptingConn.prepareStatement(
				"UPDATE quotes SET status='closed', lockid=NULL, idto=? WHERE id=? AND lockid=?;"); // No status=open because it could have timed out somehow
		acceptLockQuoteUpdateMyMoney = quoteAcceptingConn.prepareStatement(
				"UPDATE users SET bottles=bottles + ?, pennies=pennies - ? WHERE id=?;");
		acceptLockQuoteUpdateOtherMoney = quoteAcceptingConn.prepareStatement(
				"UPDATE users SET bottles=bottles - ?, pennies=pennies + ? WHERE id=?;");
		declineLockedQuoteStatement = quoteAcceptingConn.prepareStatement(
				"UPDATE quotes SET lockid=NULL, timeaccepted=NULL WHERE id=? AND lockid=?;");
		cancelQuoteStatement = conn.prepareStatement(
				"UPDATE quotes SET status='cancelled', timeaccepted=NOW() WHERE id=? AND status='open' AND idfrom=? AND lockid IS NULL;");
		
		getTradeHistoryStatement = miscDataGetConn.prepareStatement("SELECT * FROM tradehistory WHERE timecreated > TIMESTAMPADD(MINUTE, ?, NOW());");
		getTradeHistoryRangeStatement = miscDataGetConn.prepareStatement(
				"SELECT MIN(timecreated) AS mintime, MAX(timeaccepted) AS maxtime, MIN(pennies) AS minpennies, MAX(pennies) AS maxpennies " +
				"FROM quotes WHERE timecreated > TIMESTAMPADD(MINUTE, ?, NOW());");
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
		if(gameUtils.isGamePaused()) return false;
		LinkedList<PB> money = getUserMoney(userId); // Estimated worth doesn't matter here
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
			list.add(new OpenQuote(rs.getInt("id"), t, rs.getString("fromname"), rs.getInt("idfrom"), rs.getInt("pennies"), rs.getInt("bottles"), rs.getTimestamp("time")));
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
	
	/**
	 * @param id
	 * @return An {@link OpenQuote} describing the given id, or null
	 * @throws SQLException
	 */
	public synchronized OpenQuote getQuoteInfo(int id) throws SQLException {
		getQuoteInfoStatement.setInt(1, id);
		ResultSet rs = getQuoteInfoStatement.executeQuery();
		while(rs.next()) {
			return new OpenQuote(id, rs.getString("type").equals("buy") ? OpenQuote.TYPE_BUY : OpenQuote.TYPE_SELL, rs.getString("fromname"), rs.getInt("idfrom"), rs.getInt("pennies"), rs.getInt("bottles"), rs.getTimestamp("time"));
		}
		return null;
	}
	
	/**
	 * Gets the user's current money
	 * @param id the user's ID
	 * @return a {@link LinkedList} containing: current money, potential money, potential worst money, potential most money
	 * @throws SQLException 
	 */
	public synchronized LinkedList<PB> getUserMoney(int id) throws SQLException {
		LinkedList<PB> nums = new LinkedList<PB>();
		
		getUserMoneyStatement.setInt(1, id);
		getUserMoneyStatement.setInt(2, id);
		getUserMoneyStatement.setInt(3, id);
		getUserMoneyStatement.setInt(4, id);
		
		Integer estimatedWorth = userWorthGuesses.get(id);
		if(estimatedWorth == null) estimatedWorth = 1;
		
		ResultSet rs = getUserMoneyStatement.executeQuery();
		
		while(rs.next()) {
			nums.add(new PB(rs.getInt("npennies"), rs.getInt("nbottles"), estimatedWorth));
		}
		
		return nums;
	}
	
	public MMyInfo getUserMoneyMessage(int id) throws SQLException {
		LinkedList<PB> nums = getUserMoney(id);
		
		Integer estWorth = userWorthGuesses.get(id);
		if(estWorth == null) estWorth = 1;
		
		return new MMyInfo(nums.get(0), nums.get(1), nums.get(2), nums.get(3), estWorth);
	}
	
	public synchronized void pushUserMoney(int id) throws SQLException {
		LinkedList<PB> nums = getUserMoney(id);
		
		Integer estWorth = userWorthGuesses.get(id);
		if(estWorth == null) estWorth = 1;
		
		MMyInfo info = new MMyInfo(nums.get(0), nums.get(1), nums.get(2), nums.get(3), estWorth);
		
		multicast.sendMessageToClient(id, info);
	}
	
	public synchronized LinkedList<OpenQuote> getUserOpenQuotes(int id) throws SQLException {
		LinkedList<OpenQuote> quotes = new LinkedList<OpenQuote>();
		
		getUserOpenQuotesStatement.setInt(1, id);
		ResultSet rs = getUserOpenQuotesStatement.executeQuery();
		
		while(rs.next()) {
			String type = rs.getString("type");
			int t;
			if(type.equals("buy")) t = OpenQuote.TYPE_BUY;
			else t = OpenQuote.TYPE_SELL;
			quotes.add(new OpenQuote(rs.getInt("id"), t, "Me", id, rs.getInt("pennies"), rs.getInt("bottles"), rs.getTimestamp("time")));
		}
		
		return quotes;
	}
	
	public synchronized LinkedList<ClosedQuote> getUserClosedQuotes(int id) throws SQLException {
		LinkedList<ClosedQuote> quotes = new LinkedList<ClosedQuote>();
		
		getUserClosedTradesStatement.setInt(1, id);
		getUserClosedTradesStatement.setInt(2, id);
		ResultSet rs = getUserClosedTradesStatement.executeQuery();
		
		while(rs.next()) {
			String type = rs.getString("type");
			int t;
			if(type.equals("buy")) t = ClosedQuote.TYPE_BUY;
			else t = ClosedQuote.TYPE_SELL;
			quotes.add(new ClosedQuote(rs.getInt("id"), t, rs.getString("fromname"), rs.getString("toname"), rs.getInt("pennies"), rs.getInt("bottles"), rs.getTimestamp("time")));
		}
		
		return quotes;
	}
	
	public MMyQuotesList getUserQuotes(int id) throws SQLException {
		return new MMyQuotesList(getUserOpenQuotes(id), getUserClosedQuotes(id));
	}
	
	public void pushUserQuotes(int id) throws SQLException {
		multicast.sendMessageToClient(id, getUserQuotes(id));
	}
	
	/**
	 * Tries to lock the quote for accepting
	 * @param userId
	 * @param quoteId
	 * @return true if successful, false if someone else got there first
	 * @throws SQLException 
	 */
	public synchronized boolean requestLockQuote(int userId, int quoteId) throws SQLException {
		requestQuoteLockStatement.setInt(1, userId);
		requestQuoteLockStatement.setInt(2, quoteId);
		
		int rows = requestQuoteLockStatement.executeUpdate(); // Will return 0 if row already grabbed
		return rows == 1;
	}
	
	/**
	 * Counts when to push the data to the projector, and pushes every x times a quote is accepted.
	 */
	private int projectorGraphPush = 0;
	private static final int PROJECTOR_GRAPH_PUSH_AFTER_COUNT = 3;
	
	/**
	 * Accepts a quote which has been locked by the user, making it either 'closed' or 'open'
	 * @param userId
	 * @param quoteId
	 * @param accept
	 * @return
	 * @throws SQLException
	 */
	public synchronized int acceptLockedQuote(int userId, int quoteId, boolean accept) throws SQLException {
		if(accept) {
			if(gameUtils.isGamePaused()) return MTAcceptResponse.ACCEPT_QUOTE_GAME_PAUSED;
			OpenQuote q = getQuoteInfo(quoteId);
			if(q == null) return MTAcceptResponse.ACCEPT_QUOTE_FAIL; // Get info about quote
			
			LinkedList<PB> money = getUserMoney(userId);
			// Check against 0, 2, 3 (0 because of some weird logic I haven't figured out)
			money.remove(1); // Don't need
			for(PB testMoney : money) {
				if(testMoney.getBottles() + q.getBottles() < 0) return MTAcceptResponse.ACCEPT_QUOTE_NOMONEY; // If exceeds bottles
				if(testMoney.getPennies() - q.getValue() < 0) return MTAcceptResponse.ACCEPT_QUOTE_NOMONEY; // If exceeds pennies
			}
		
			acceptLockedQuoteStatement.setInt(1, userId);
			acceptLockedQuoteStatement.setInt(2, quoteId);
			acceptLockedQuoteStatement.setInt(3, userId);
			if(acceptLockedQuoteStatement.executeUpdate() != 1) {
				quoteAcceptingConn.rollback();
				return MTAcceptResponse.ACCEPT_QUOTE_FAIL;
			}
			
			acceptLockQuoteUpdateMyMoney.setInt(1, q.getBottles());
			acceptLockQuoteUpdateMyMoney.setInt(2, q.getValue());
			acceptLockQuoteUpdateMyMoney.setInt(3, userId);
			if(acceptLockQuoteUpdateMyMoney.executeUpdate() != 1) {
				quoteAcceptingConn.rollback();
				return MTAcceptResponse.ACCEPT_QUOTE_FAIL;
			}
			
			acceptLockQuoteUpdateOtherMoney.setInt(1, q.getBottles());
			acceptLockQuoteUpdateOtherMoney.setInt(2, q.getValue());
			acceptLockQuoteUpdateOtherMoney.setInt(3, q.getIdFrom());
			if(acceptLockQuoteUpdateOtherMoney.executeUpdate() != 1) {
				quoteAcceptingConn.rollback();
				return MTAcceptResponse.ACCEPT_QUOTE_FAIL;
			}
			
			quoteAcceptingConn.commit();
			
			pushOpenQuotes();
			pushUserMoney(userId);
			pushUserMoney(q.getIdFrom());
			pushUserQuotes(userId);
			pushUserQuotes(q.getIdFrom());
			
			projectorGraphPush++; // Something has happened
		} else { // Reset quote
			declineLockedQuoteStatement.setInt(1, quoteId);
			declineLockedQuoteStatement.setInt(2, userId);
			declineLockedQuoteStatement.executeUpdate();
			quoteAcceptingConn.commit();
		}
		return MTAcceptResponse.ACCEPT_QUOTE_SUCCESS;
	}
	
	public synchronized int cancelOpenQuote(int userId, int quoteId) throws SQLException {
		if(gameUtils.isGamePaused()) return MTCancelResponse.RESPONSE_GAME_PAUSED;
		
		cancelQuoteStatement.setInt(1, quoteId);
		cancelQuoteStatement.setInt(2, userId);
		
		int num = cancelQuoteStatement.executeUpdate();
		
		if(num != 1) return MTCancelResponse.RESPONSE_ALREADY_TAKEN;
		
		pushOpenQuotes();
		projectorGraphPush++;
		
		return MTCancelResponse.RESPONSE_OK;
	}
	
	private int tradeHistoryMinutes = 120;
	
	/**
	 * Gets the history of trades for the projector graph
	 * @return An {@link MTradesList} containing the complete list of trades
	 * @throws SQLException
	 */
	public synchronized MTradesList getTradeHistory() throws SQLException {
		
		long minTime, maxTime;
		int minPennies, maxPennies;
		getTradeHistoryRangeStatement.setInt(1, -tradeHistoryMinutes);
		ResultSet range = getTradeHistoryRangeStatement.executeQuery();
		if(!range.next()) {
			minTime = 1296208819 * 1000; // Just blank values to stop graph going weird.
			maxTime = 1296208919 * 1000;
			minPennies = 100;
			maxPennies = 200;
		} else {
			minPennies = range.getInt("minpennies");
			maxPennies = range.getInt("maxpennies");
			try {
				minTime = range.getTimestamp("mintime").getTime();
				maxTime = range.getTimestamp("maxtime").getTime();
			} catch(NullPointerException e) { // No rows
				minTime = 1296208819 * 1000; // Just blank values to stop graph going weird.
				maxTime = 1296208919 * 1000;
				minPennies = 100;
				maxPennies = 200;
			}
		}
		
		if(maxPennies - minPennies < 10) { // This causes graph problems
			maxPennies = minPennies + 10;
		}
		if(maxTime - minTime < 10000) {
			maxTime = minTime + 10000;
		}
		
		LinkedList<ClosedQuote> trades = new LinkedList<ClosedQuote>();
		getTradeHistoryStatement.setInt(1, -tradeHistoryMinutes);
		ResultSet rs = getTradeHistoryStatement.executeQuery();
		while(rs.next()) {
			int type = rs.getString("type").equals("buy") ? ClosedQuote.TYPE_BUY : ClosedQuote.TYPE_SELL;
			
			int finishReason = ClosedQuote.FINISH_CLOSED;
			String status = rs.getString("status");
			if(status.equals("closed")) finishReason = ClosedQuote.FINISH_CLOSED;
			if(status.equals("cancelled")) finishReason = ClosedQuote.FINISH_CANCELLED;
			if(status.equals("timeout")) finishReason = ClosedQuote.FINISH_TIMEOUTED;
			trades.add(new ClosedQuote(rs.getInt("id"), type, "", "", rs.getInt("pennies"), rs.getInt("bottles"), rs.getTimestamp("timeaccepted"), rs.getTimestamp("timecreated"), finishReason));
		}
		
		return new MTradesList(trades, minTime, maxTime, minPennies, maxPennies);
	}
	
	public void pushTradeHistory() throws SQLException {
		multicast.refreshProjectorTradeGraph(); // This is the one part of the game that doesn't need to be snappy, so send it from another thread!
	}
	
	public void setTradeHistoryMinutes(int minutes) throws SQLException {
		tradeHistoryMinutes = minutes;
		pushTradeHistory();
	}
	
	public int getTradeHistoryMinutes() {
		return tradeHistoryMinutes;
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
		
		protected final PreparedStatement deleteOldQuotes, getOldQuoteUserIds, resetTimeoutedLockedQuotes;
		
		protected Object waitObject;
		protected final int lockWaitTimeout = GlobalPreferences.getQuoteAcceptTimeout() + 2; // 2 to give a bit of a gap

		protected QuotePurger(Connection conn) throws SQLException {
			super("Old quote purger");
			deleteOldQuotes = conn.prepareStatement(
					"UPDATE quotes SET status='timeout', timeaccepted=NOW() " +
					"WHERE status='open' AND timecreated < TIMESTAMPADD(SECOND, ?, NOW()) AND lockid IS NULL;");
			getOldQuoteUserIds = conn.prepareStatement(
					"SELECT idfrom AS userid FROM quotes " +
					"WHERE status='open' AND timecreated < TIMESTAMPADD(SECOND, ?, NOW()) AND lockid IS NULL GROUP BY idfrom;");
			
			resetTimeoutedLockedQuotes = conn.prepareStatement(
					"UPDATE quotes SET lockid=NULL, timeaccepted=NULL " +
					"WHERE status='open' AND timeaccepted < TIMESTAMPADD(SECOND, ?, NOW()) AND lockid IS NOT NULL;");
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
					pushUserMoney(usersToNotify.getInt("userid"));
					pushUserQuotes(usersToNotify.getInt("userid"));
					projectorGraphPush++;
				}
				
				if(numRows > 0) { // List has changed, resend
					System.out.println("Timeouted " + numRows + " old quotes from the DB, pushing new list");
				
					pushOpenQuotes();
				}
				
				// Expire old locked quotes (so they don't become stuck)
				resetTimeoutedLockedQuotes.setInt(1, -lockWaitTimeout);
				resetTimeoutedLockedQuotes.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if(projectorGraphPush >= PROJECTOR_GRAPH_PUSH_AFTER_COUNT) {
				try {
					pushTradeHistory();
					projectorGraphPush -= PROJECTOR_GRAPH_PUSH_AFTER_COUNT;
				} catch (SQLException e) {
					e.printStackTrace();
				}
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
