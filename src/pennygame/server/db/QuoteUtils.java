package pennygame.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import pennygame.lib.msg.MOpenQuotesList;
import pennygame.lib.msg.MPutQuote;
import pennygame.lib.msg.data.OpenQuote;
import pennygame.server.client.CMulticaster;

/**
 * Utilities for processing quotes
 * @author william
 *
 */
public final class QuoteUtils {
	
	private final PreparedStatement putQuoteStatement, getOpenQuotesStatement;
	private final CMulticaster multicast;
	
	QuoteUtils(Connection conn, CMulticaster multicast) throws SQLException {
		this.multicast = multicast;
		
		putQuoteStatement = conn.prepareStatement("INSERT INTO quotes(type, idfrom, pennies, bottles) VALUES (?, ?, ?, ?);");
		getOpenQuotesStatement = conn.prepareStatement(
				"SELECT * FROM " +
				"((SELECT quotes.id, quotes.type, users.friendlyname AS fromname, quotes.pennies, quotes.bottles, quotes.bottles * CAST(quotes.pennies AS SIGNED) AS value " +
				"FROM quotes, users WHERE status='open' AND type='sell' AND quotes.idfrom = users.id ORDER BY pennies LIMIT ?) " +
				"UNION ALL " +
				" (SELECT quotes.id, quotes.type, users.friendlyname AS fromname, quotes.pennies, quotes.bottles, quotes.bottles * CAST(quotes.pennies AS SIGNED) AS value " +
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
	
	public synchronized void setNumQuotes(int number) {
		numberOfQuotes = number;
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
			if(type == "buy") t = OpenQuote.TYPE_BUY;
			else t = OpenQuote.TYPE_SELL;
			list.add(new OpenQuote(rs.getInt("id"), t, rs.getString("fromname"), rs.getInt("pennies"), rs.getInt("bottles"), rs.getTimestamp("time")));
		}
		
		return list;
	}
	
	public synchronized void pushOpenQuotes() {
		try {
			multicast.multicastMessage(new MOpenQuotesList(getOpenQuotes()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
