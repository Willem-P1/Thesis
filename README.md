# Thesis
Repo for thesis, making upper bound heuristic for calculating TBR distance

#How to use
To use the code compile the java files in the "code" folder using "javac code/*.java". Then to run the code, use "java code.Main" with some command line arguments: <br>
 - use "-p [path]"to pass the file with the trees to run the algorithms on. NOTE: the code expects the trees in the newick format with one line per tree in the file.<br>
 - use "-t [time]" to pass the timelimit, what this timelimit means depends on which algorithm is used <br>
 - To select an algorithm use "-mcts" to use the MCTS algorithm, use "-r" to use the iterative random algorithm, use "-n" to use the naive MCTS algorithm.<br>
 - if the tree pairs contain a degree 2 vertex which acts like a root in the tree, due to the way the trees are created, use "-deg2" to remove this before calculation starts<br>
