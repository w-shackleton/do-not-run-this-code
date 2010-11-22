package uk.digitalsquid.spacegame.spaceitem.interfaces;

import java.util.StringTokenizer;

public interface Messageable
{
	public MessageInfo sendMessage();
	
	public class MessageInfo
	{
		public boolean display;
		public String[] messages;
		
		public MessageInfo(String[] messages, boolean display)
		{
			this.display = display;
			this.messages = messages;
		}
		
		public MessageInfo(StringTokenizer tmessages, boolean display)
		{
			this.display = display;
			messages = new String[tmessages.countTokens()];
			int i = 0;
			while(tmessages.hasMoreTokens())
			{
				messages[i++] = tmessages.nextToken();
			}
		}
	}
}
