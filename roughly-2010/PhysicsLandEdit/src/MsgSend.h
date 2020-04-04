#ifndef MSGSEND_H
#define MSGSEND_H
/* Very simple system to send a message back
 * to PhyLandFrame & PhyLandPEArea, normal event handlers
 * didn't seem to work under windows.
 */

class MsgSend
{
public:
	virtual void sendMsg(int what) = 0;
	enum
	{
		MSG_Sidebar,
		MSG_Enable,
		MSG_Disable,
	};
};

#endif
