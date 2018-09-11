The files containing the graphs are structured as follows:

First line is a header that contains information regarding the maze. The information is as follows:

name of maze
start root for Ms Pac-Man
root that corresponds to the lair
start root for the ghosts
number of nodes in the maze
number of pills in the maze
number of power pills in the maze
number of junctions in the maze

All other lines corresponds to individual nodes on the graph. Each root has the following information:

root index
x-coordinate	
y-coordinate
neighbouring root in UP direction (-1 if none)
neighbouring root in RIGHT direction (-1 if none)
neighbouring root in DOWN direction (-1 if none)
neighbouring root in LEFT direction (-1 if none)
pill-index of the root (-1 if none)
power-pill index of the root (-1 if none)