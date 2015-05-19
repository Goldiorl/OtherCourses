#include "aux.h"
#include "sys.h"
#include "mykernel2.h"

//#define TIMERINTERVAL 200000	// in ticks (tick = 10 msec) 
#define TIMERINTERVAL 1

int fifohead;
int fifotail;
int lifocounter;
int rrpointer; //pointer for RoundRobin general
int rrpp_pointer;//pointer for RoundRobin in Proportional
double cpu_total;

static struct {
	int valid;		
	int pid;	
	int utilcounter; //increment once chosen on Sched()
	int	timecounter; //increment every preemptive scheduling
	double ratio; //  utilcounter/timecounter /expectedratio
	double expectedratio;
} proctab[MAXPROCS];

//InitSched()
void InitSched ()
{
	int i;
	if (GetSchedPolicy () == NOSCHEDPOLICY) {	// leave as is 
	//	SetSchedPolicy (ARBITRARY);	
	//	SetSchedPolicy(LIFO);
	//	SetSchedPolicy(FIFO);		
	//	SetSchedPolicy(ROUNDROBIN);	
		SetSchedPolicy(PROPORTIONAL);	
	}
	
	//Initialize global variables
	fifohead=0;
	fifotail=0;
	lifocounter=0;
	rrpointer=0;
	rrpp_pointer=0;
	cpu_total=0;
	
	// Initialize data structures 
	for (i = 0; i < MAXPROCS; i++) {
		proctab[i].valid = 0;
		proctab[i].pid = 0;
		proctab[i].utilcounter=0;
		proctab[i].timecounter=0;
		proctab[i].expectedratio=0;
		proctab[i].ratio=0;
	}

	// Set the timer last 
	SetTimer (TIMERINTERVAL);
}

//StartingProc()
int StartingProc (pid)
	int pid;
{
	int i;
//	Printf("I'm starting program %d\n",pid);
	//FIFO's pid storage is different
	if (GetSchedPolicy() == FIFO){
		if(proctab[fifotail].valid != 0) return (0); //full
		else {
		proctab[fifotail].valid = 1;
		proctab[fifotail].pid = pid;
		fifotail++;
		if(fifotail >= MAXPROCS) fifotail = 0;
		return (1);
		}
	}
	// ROUNDROBIN | PROPORTIONAL| LIFO
	for (i = 0; i < MAXPROCS; i++) {
		if (! proctab[i].valid) {
			proctab[i].valid = 1;
			proctab[i].pid = pid;
			proctab[i].utilcounter=0;
			proctab[i].timecounter=0;
			proctab[i].expectedratio=0;
			proctab[i].ratio=0;
			if(GetSchedPolicy()==LIFO){
					lifocounter=i;
					DoSched();
			}
			
			return (1);
		}
	}
	
	Printf ("Error in StartingProc: no free table entries\n");
	return (0);
}
		
//EndingProc();		
int EndingProc (pid)
	int pid;
{
	int i;
	//Ending FIFO
	if(GetSchedPolicy() == FIFO) {
		proctab[fifohead].valid=0;
		fifohead++;
		if(fifohead >= MAXPROCS) fifohead = 0;
		return (1);
	}
	
	//Ending LIFO
	if(GetSchedPolicy() == LIFO) {
		proctab[lifocounter].valid=0;
		lifocounter--;
		return (1);
	}
	
	
	//Ending ARBITRARY && ROUNDROBIN &&PROPORTIONAL
	for (i = 0; i < MAXPROCS; i++) {
		if (proctab[i].valid && proctab[i].pid == pid) {
		if(GetSchedPolicy() ==PROPORTIONAL) {
				cpu_total-=proctab[i].expectedratio;
//				Printf("program %d exiting, distract %f now the total cpu is %f\n",proctab[i].pid,proctab[i].expectedratio,cpu_total);
		}
			proctab[i].valid = 0;
			proctab[i].pid = 0;
			proctab[i].utilcounter=0;
			proctab[i].timecounter=0;
			proctab[i].expectedratio=0;
			proctab[i].ratio=0;
			return (1);
		}
	}

	
	Printf ("Error in EndingProc: can't find process %d\n", pid);
	return (0);
}

//SchedProc()
int SchedProc ()
{
	int i;
	int previous; //ROUNDROBIN
	int lowpid; //PROPORTIONAL
	double lowratio;
	
	switch (GetSchedPolicy ()) {

	case ARBITRARY:

		for (i = 0; i < MAXPROCS; i++) {
			if (proctab[i].valid) {
//				Printf("I have schedule %d\n in ARBITRARY",proctab[i].pid);
				return (proctab[i].pid);
			}
		}
		break;

	case FIFO:
	
		if(proctab[fifohead].valid){
//			Printf("I have schedule %d\n in FIFO",proctab[fifohead].pid);
			return (proctab[fifohead].pid);
		}
		break;

	case LIFO:

		if (proctab[lifocounter].valid){
//			Printf("I have schedule %d\n in LIFO",proctab[lifocounter].pid);
			return (proctab[lifocounter].pid);
		}
		break;

	case ROUNDROBIN:
	
		//ROUNDROBIN
		previous=rrpointer;
		rrpointer++;
		if(rrpointer >= MAXPROCS) rrpointer=0;
		while(rrpointer!=previous) {
			if(proctab[rrpointer].valid)  return proctab[rrpointer].pid;
			rrpointer++;
			if(rrpointer >= MAXPROCS) rrpointer=0;
		}
		if(proctab[rrpointer].valid) return proctab[rrpointer].pid;
		break;

	case PROPORTIONAL:
		lowratio=-1;
		lowpid=-1;
		//scan all
		for (i = 0; i < MAXPROCS; i++) {
			if (proctab[i].valid) {
				//Has set expected ratio
				if(proctab[i].expectedratio != 0){
				if(proctab[i].timecounter !=0)
				proctab[i].ratio= ( (double) proctab[i].utilcounter /(double) proctab[i].timecounter)
									/(double) proctab[i].expectedratio ;
//				Printf("program %d 's expected ratio is %f, current ration is %f\n",proctab[i].pid,proctab[i].expectedratio,proctab[i].ratio);
					if(lowpid==-1){
						lowpid=i;
						lowratio=proctab[i].ratio;
						}
					else if(proctab[i].ratio<lowratio){
						lowpid=i;
						lowratio=proctab[i].ratio;
					}
				} 
			}
		}
		//if <1 pick lowest 
		if(lowratio<1 && lowratio != -1) { // there is set and has <1
			proctab[lowpid].utilcounter++; 
//			Printf("there is set program,so pic %d\n",proctab[lowpid].pid);
			return proctab[lowpid].pid;
		}
		
		//ROUNDROBIN NEXT
		previous=rrpp_pointer;
		rrpp_pointer++;
		if(rrpp_pointer >= MAXPROCS) rrpp_pointer=0;
		while(rrpp_pointer!=previous) {
			if(proctab[rrpp_pointer].valid && proctab[rrpp_pointer].expectedratio==0) {
				proctab[rrpp_pointer].utilcounter++; //increment, return rrpp_pointer
//				Printf("in ROUNDROBIN, choose %d\n",proctab[rrpp_pointer].pid);
				return proctab[rrpp_pointer].pid;
			}
			rrpp_pointer++;
			if(rrpp_pointer >= MAXPROCS) rrpp_pointer=0;
		}
		//check "previous", increment
		if(proctab[rrpp_pointer].valid && proctab[rrpp_pointer].expectedratio==0){
			proctab[rrpp_pointer].utilcounter++;
//			Printf("in ROUNDROBIN, choose %d\n",proctab[rrpp_pointer].pid);
			return proctab[rrpp_pointer].pid;
		} // else, give the lowest pid whose ratio >1
		else  {
			proctab[lowpid].utilcounter++; //increment
//			Printf("there is set program, but no roundrobin, so set for %d\n",proctab[lowpid].pid);
			return proctab[lowpid].pid;
		}
		break;

	}
	
	return (0);
}

void HandleTimerIntr ()
{
	SetTimer (TIMERINTERVAL);
	int i;
	for (i = 0; i < MAXPROCS; i++) {
		if(proctab[i].valid) proctab[i].timecounter++;  //increment timecounter for alive processes
	}
	switch (GetSchedPolicy ()) {	/* is policy preemptive? */

	case ROUNDROBIN:		/* ROUNDROBIN is preemptive */
	case PROPORTIONAL:		/* PROPORTIONAL is preemptive */

		DoSched ();		/* make scheduling decision */
		break;

	default:			/* if non-preemptive, do nothing */
		break;
	}
}

int MyRequestCPUrate (pid, m, n)
	int pid;
	int m;
	int n;
{
	double temp;
	double alreadyset;
	int setflag;
	if ( m < 1 || n < 1 || m > n ){
		return (-1);
	}
	
	temp = (double) m / (double) n;	
	
	for (int i = 0; i < MAXPROCS; i++){
	if (proctab[i].pid == pid){
		if(proctab[i].expectedratio>0){
			alreadyset= proctab[i].expectedratio;
			setflag=1;
		}
	}
	}	

	//checking conditions
	if( setflag==1) cpu_total-=alreadyset;
	
	if(cpu_total+temp>1.001){
		if(setflag==1) cpu_total+=alreadyset;
//		Printf("Set FAIL!! Expectedratio for PID %d as %f, now the CPUTOTAL is %f\n",pid,temp,cpu_total);
		return (-1);
	}
	else {
		int i;
		for (i = 0; i < MAXPROCS; i++){
			if (proctab[i].pid == pid){
				proctab[i].expectedratio = temp;
				cpu_total += temp;
//				Printf("Set Expectedratio for PID %d as %f, now the CPUTOTAL is %f\n",pid,temp,cpu_total);
			}
			//set 0 for all utility and time counter
			proctab[i].utilcounter=0;
			proctab[i].timecounter=0;
			proctab[i].ratio=0;
		}
		if(GetSchedPolicy() == PROPORTIONAL){
			DoSched();
		}
	}
	return (0);
}
