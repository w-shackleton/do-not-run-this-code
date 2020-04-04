/* Prototypes for functions defined in movetree.c  */

void movetree(ubyte *nums, int move, int pots, ubyte *cScore, ubyte *hScore, bool c, bool *cleared);

bool checkmove(ubyte *nums, bool c, int pots);

void printtree(ubyte *nums, int pots, ubyte cScore, ubyte hScore);
