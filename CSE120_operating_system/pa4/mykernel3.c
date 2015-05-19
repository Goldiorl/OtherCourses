/* mykernel.c: your portion of the kernel
 *
 *	Below are procedures that are called by other parts of the kernel. 
 *	Your ability to modify the kernel is via these procedures.  You may
 *	modify the bodies of these procedures any way you wish (however,
 *	you cannot change the interfaces).  
 */

#include "aux.h"
#include "sys.h"
#include "mykernel3.h"

#define FALSE 0
#define TRUE 1
#define MAXPROCS_PA3 1000


/*	A sample semaphore table.  You may change this any way you wish.  
 */
typedef struct{
		int intq[MAXPROCS_PA3-1];
		int head; 
		int tail; // first element that pid can be inserted
}queue;


static struct {
	int valid;	/* Is this a valid entry (was sem allocated)? */
	int value;	/* value of semaphore */
	queue waitlist;
} semtab[MAXSEMS];

int isFull(queue *q){
		if (q->intq[q->tail]!=0) return (1);
		else return (0);
}

int isEmpty(queue *q){
	if(q->tail==q->head && q->intq[q->tail]==0) return (1);
	else return (0);
}

int enqueue(queue *q,int pid){ // return 0 failur, 1 successful
	if(!isFull(q)){
		q->intq[q->tail++]=pid;
		if(q->tail==MAXPROCS_PA3-1)q->tail=0;
		return (1);
	}else {
			Printf("enqueue failure, full queue");
			return (0);
	}
}

int dequeue(queue *q){ //return pid number, 0 if failed
		int ret;
		if(!isEmpty(q)){
				ret=q->intq[q->head];
				q->intq[q->head]=0;
				q->head++;
				if(q->head==MAXPROCS_PA3-1) q->head=0;
				return ret;
		}else{
			Printf("empty queue, nothing to dequeue");
			return 0;
		}
}

					



/*	InitSem () is called when kernel starts up. Initialize data
 *	structures (such as the semaphore table) and call any initialization
 *	procedures here. 
 */

void InitSem ()
{
	int s;

	/* modify or add code any way you wish */

	for (s = 0; s < MAXSEMS; s++) {		/* mark all sems free */
		semtab[s].valid = FALSE;
	}
}

/*	MySeminit (p, v) is called by the kernel whenever the system
 *	call Seminit (v) is called.  The kernel passes the initial
 * 	value v, along with the process ID p of the process that called
 *	Seminit.  MySeminit should allocate a semaphore (find a free entry
 *	in semtab and allocate), initialize that semaphore's value to v,
 *	and then return the ID (i.e., index of the allocated entry).  
 */

int MySeminit (p, v)
	int p, v;
{
	int s;

	/* modify or add code any way you wish */

	for (s = 0; s < MAXSEMS; s++) {
		if (semtab[s].valid == FALSE) {
			break;
		}
	}
	if (s == MAXSEMS) {
		Printf ("No free semaphores\n");
		return (-1);
	}

	semtab[s].valid = TRUE;
	semtab[s].value = v;

	return (s);
}

/*	MyWait (p, s) is called by the kernel whenever the system call
 *	Wait (s) is called.  
 */

void MyWait (p, s)
	int p, s;
{
	/* modify or add code any way you wish */
	semtab[s].value--;
	if(semtab[s].value<0){
		enqueue(&(semtab[s].waitlist),p);
		Block(p);
	}
}

/*	MySignal (p, s) is called by the kernel whenever the system call
 *	Signal (s) is called. 
 */

void MySignal (p, s)
	int p, s;
{
	/* modify or add code any way you wish */

	semtab[s].value++;
	if(semtab[s].value<=0){
		int retpid=dequeue(&(semtab[s].waitlist));
		if(retpid==0) Printf("Warning!!! Internal error: nothing to unblock");
		Unblock(retpid);
	}
}

