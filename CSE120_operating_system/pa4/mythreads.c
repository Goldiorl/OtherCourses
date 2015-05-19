//-----Headfiles------
#include <setjmp.h>
#include "aux.h"
#include "umix.h"
#include "mythreads.h"
//-----Macros------
#define STACKSIZE	65536		/* maximum size of thread stack */
#define MAINTHREAD 0
#define TRUE 1
#define FALSE 0
#define QUEUESIZE 1000
//-----Global&Statics---
static int MyInitThreadsCalled = 0;	/* 1 if MyInitThreads called, else 0 */
static int currentthread;
static int previousthread=-1;// previous & current both in scheduling,initially no previous thread
static int justspawned;
static int runningthreadcounter;
static int globaltransfer;
static int debug=1;

static struct thread {			/* thread table */
	int valid;			/* 1 if entry is valid, else 0 */
	jmp_buf starting_env;
	jmp_buf env;			/* current context */
	void (*currentfuc)();
	int currentparam;
	void (*funcf)();
	int paramp;
} thread[MAXTHREADS];
typedef struct{
		int intq[QUEUESIZE];
		int head; 
		int tail; // first element that pid can be inserted
}queue;
queue tq;
//----Function declaration----
void StackProgHandler(int t);
void MyInitThreads ();
int MySpawnThread (void (*func)(), int param);
int MyGetThread ();
int MyYieldThread (int t);
void MySchedThread ();
void MyExitThread ();
void initQueue_user(queue *);
int isFull(queue*);
int isEmpty(queue*);
int enqueue(queue*,int);
int dequeue(queue*q);
void shiftToHead(queue *,int);
//----Starting point for functions----

//StackProgHandler()
void StackProgHandler(int t){
	if(t==MAXTHREADS){
		longjmp(thread[MAINTHREAD].env,1); //Still in initialization, back to main
	}
	
	if(setjmp(thread[t].starting_env)==0){
		char s[STACKSIZE];
		if(((int) &s[STACKSIZE-1])-((int) &s[0])+1!=STACKSIZE){
			Printf ("Stack space reservation failed\n");
			Exit ();
		}
		
		StackProgHandler(t+1);//recursively initialization
	}
	
	//when referring the functions,MAINTHREAD might be meaningless TODO
	(*(thread[currentthread].funcf)) (thread[currentthread].paramp);
	
	MyExitThread();
	
}
//MyInitThreads () 
void MyInitThreads ()
{
	int i;

	if (MyInitThreadsCalled) {                /* run only once */
		Printf ("InitThreads: should be called only once\n");
		Exit ();
	}

	for (i = 0; i < MAXTHREADS; i++) {	/* initialize thread table */
		thread[i].valid = 0;
	}

	thread[0].valid = 1;			/* initialize thread 0 */
	MyInitThreadsCalled = 1;
	if(debug==0) Printf("MyInitiTrheads: current MyInitThreadsCalled value: %d\n",MyInitThreadsCalled);
	//----below is addedon
	initQueue_user(&tq);
	enqueue(&tq,0); //TODO whether to put current thread into the queue
	
	currentthread=MAINTHREAD;
	justspawned=MAINTHREAD;
	runningthreadcounter=1;
	
	if(setjmp(thread[MAINTHREAD].env)==0){
		StackProgHandler(MAINTHREAD);
	}
}



int MySpawnThread (func, param)
	void (*func)();		/* function to be executed */
	int param;		/* integer parameter */
{
	if (! MyInitThreadsCalled) {
	if(debug==0) Printf("MySpawnThread: current MyInitThreadsCalled value: %d\n",MyInitThreadsCalled);
		Printf ("SpawnThread: Must call InitThreads first\n");
		Exit ();
	}

	//------------Sanity check---------------
	if(runningthreadcounter>=MAXTHREADS){
		Printf("not stack room for more threads\n");
		return -1;
	}
	
	//------------main body------------------
	//find an entry, put it
	int loopindex=justspawned;
	int validentryindex=0;
//	if(thread[loopindex].valid==FALSE){
//		validentryindex=loopindex;
//		justspawned=loopindex;
//		thread[loopindex].valid=TRUE;
//		runningthreadcounter++;
//	} else{
	loopindex++;		
	while(loopindex!=justspawned){
		if(loopindex>MAXTHREADS-1) loopindex=0;
		if(thread[loopindex].valid==FALSE){
		validentryindex=loopindex;
		justspawned=loopindex;
		thread[loopindex].valid=TRUE;
		runningthreadcounter++;
		break;
		}
		loopindex++;
	}
//	}
	//put into the queue tail(at this time the item in queue iis contagious)
	int hasbeenputflag=0;
	if(enqueue(&tq,validentryindex)==1) hasbeenputflag=1;
	
	if(hasbeenputflag==0) Printf("Errors on putting into queue");
	
	//initializing the environment of the newly added thread so that can immediately call
	if(setjmp(thread[validentryindex].env)!=0){
		longjmp(thread[currentthread].starting_env,1);
	}
	//memcpy(thread[validentryindex].env, thread[validentryindex].starting_env, sizeof(jmp_buf));
	//to use global transfer or use memcpy. Global transfer is ok only depending on the fact that the thread[MAINTREAD] won't be reset. It is logical because Main THREADS stands for the process.
	
	thread[validentryindex].funcf=func;
	thread[validentryindex].paramp=param;
	
	if(debug==0) Printf(" %d just spawned\n", validentryindex);
	return validentryindex;
}
	
int MyGetThread ()
{
	if(debug==0) Printf("MyGetTrheads called!!\n");
//	if(debug==0) Printf("MyGetThread :current MyInitThreadsCalled value: %d\n",MyInitThreadsCalled);
	if (! MyInitThreadsCalled) {
	if(debug==0) Printf("MyGetThread: current MyInitThreadsCalled value: %d\n",MyInitThreadsCalled);
		Printf ("MyGetThread: Must call InitThreads first\n");
		Exit ();
	}
	return currentthread;

}

int MyYieldThread (t)
	int t;				/* thread being yielded to */
{

	//sanity check
	if (! MyInitThreadsCalled) {
		Printf ("YieldThread: Must call InitThreads first\n");
		Exit ();
	}

	if (t < 0 || t >= MAXTHREADS) {
		Printf ("YieldThread: %d is not a valid thread ID\n", t);
		return (-1);
	}
	
	if (! thread[t].valid) {
		Printf ("YieldThread: Thread %d does not exist\n", t);
		return (-1);
	}

	//queue movement
	
	
	int dequeuedthread;
	if(t!=tq.intq[tq.head]){
	dequeuedthread=dequeue(&tq);
	enqueue(&tq,dequeuedthread);
	int pointer;
	shiftToHead(&tq,t);
	previousthread=currentthread;
	currentthread=t;
	}else return currentthread;//else, yield to self
	
	if(debug==0) Printf("MyYieldThread: %d is yielding to %d \n",previousthread,currentthread);	
	if(setjmp(thread[previousthread].env)==0){
		longjmp(thread[currentthread].env,1);
	}
	return previousthread;
}


void MySchedThread ()
{
	//sanity check
	if (! MyInitThreadsCalled) {
		Printf ("SchedThread: Must call InitThreads first\n");
		Exit ();
	}
	if(runningthreadcounter==0 || isEmpty(&tq)){
		Printf("errors in SchedTread checking, gonna exit\n");
		Exit();
	}
	int yieldsto;
	if(runningthreadcounter==1) return ; //Only one thread running, sched to self
	yieldsto=tq.head+1;
	if(yieldsto==QUEUESIZE) yieldsto=0;
	
	if(debug==0) Printf("MySchedTreahd: next:: yielding from %d to %d\n",currentthread,tq.intq[yieldsto]);
	MyYieldThread(tq.intq[yieldsto]);
}



void MyExitThread ()
{
	if(debug==0)Printf("threads : %d called myexitthreads   ",currentthread);
	if (! MyInitThreadsCalled) {
		Printf ("ExitThread: Must call InitThreads first\n");
		Exit ();
	}
	
	runningthreadcounter--;
	if(runningthreadcounter==0){
	if(debug==0)	Printf("Last thread :%d exists, then exits\n",currentthread);
		Exit();
	}
	
	//make thread table entry valid=0
	thread[currentthread].valid=FALSE;
	//make current queue entry -1
	int exitthread=dequeue(&tq);
	if(isEmpty(&tq)){ Printf("Queue empty, bukexue!!"); Exit();}
	else { 
	previousthread=currentthread;
	currentthread=tq.intq[tq.head];
	if(debug==0) Printf("%d exiting so %d runs\n",previousthread,currentthread);
	longjmp(thread[currentthread].env,1);
	}
	
	//TODO previous zenme ban ? My sched??
}

//-----------queue operations-------------
void initQueue_user(queue *q){
	for(int i=0;i<QUEUESIZE;i++){
		q->intq[i]=-1;
	}
	q->head=0;
	q->tail=0;
}

int isFull(queue *q){
		if (q->intq[q->tail]!=-1) return (1);
		else return (0);
}

int isEmpty(queue *q){
	if(q->tail==q->head && q->intq[q->tail]==-1) return (1);
	else return (0);
}

int enqueue(queue *q,int pid){ // return 0 failur, 1 successful
	if(!isFull(q)){
		q->intq[q->tail++]=pid;
		if(q->tail==QUEUESIZE)q->tail=0;
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
				q->intq[q->head]=-1;
				q->head++;
				if(q->head==QUEUESIZE) q->head=0;
				return ret;
		}else{
			Printf("empty queue, nothing to dequeue");
			return 0;
		}
}
void shiftToHead(queue * q,int t){
	int pointer,temp,next,foundflag,oldpointer;
	oldpointer=q->head;
	pointer=q->head;
	temp=q->intq[pointer];
	next=temp;
	if(next==t) return; //alreadytohead
	else{
	
	for(int i=0;i<QUEUESIZE;i++) {if (q->intq[i]==t) foundflag=1;}
	if(foundflag==0) {Printf("t not found! return!!!"); return;}
	
	
	while(next!=t){
		pointer++;
		if(pointer==QUEUESIZE) pointer=0;
		next=q->intq[pointer];
		q->intq[pointer]=temp; //replace with old
		temp=next; //new one cached
	}
	
	q->intq[oldpointer]=next;
	}
}

void printQueue_user(queue *q){
	int iter=q->head;
	while(iter!=q->tail){
	Printf("location %d has value %d\n",iter,q->intq[iter]);
	iter++;
	if(iter==QUEUESIZE) iter=0;
	}

}
