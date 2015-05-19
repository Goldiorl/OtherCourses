Execution
Program takes four parameters at most. First parameters determines if program runs with or without GUI .
-c: run without GUI
-g: run with GUI
Console Application
Without GUI, program takes four parameters. Second parameter determines algorithm type (Brute force: -b, Graham: -g, Jarvis: -j). Third parameter gives input file name. Fourth parameter gives output file name.
java Experiment [console/GUI param] [Algorithm param] [in filename] [out filename]
Example:
java Experiment –c –b coordinates.txt bruteforceresult.txt
java Experiment –c –g xycoordinates.txt graham.txt
java Experiment –c –j crds.txt jarvis.txt
GUI
In GUI, user selects algorithm from a dropdown box and draws points and lines to a pane. No need to give algorithm type and output filename parameters.
java Experiment [console/GUI param] [in filename]
Example:
java Experiment –g coordinates.txt
java Experiment –g crds.txt
Input File Format
Input files consist of comma seperated lines. Each line has three elements: point name, point X coordinate, point Y coordinate.
Example:
p1,10,10 p2,85,65 p3,15,60 p4,75,110 px,130,50 mp12,85,65
cm,62,155 pnt_p,100,15
2.2.4. Output File Format
After application of convex hull algorithms, found points’ name will be written in output file (one point per line).
Example:
p1 pnt_p px p4 p3