#!/bin/env python

import os
returnL = []
for i in range(1, 15):
	returnV = os.system("./nanoml.byte tests/t"+str(i)+".ml")	
	#print(returnV)
	returnL.append(returnV)

