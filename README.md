# Dynamic 15 Puzzle Solver
The 15-puzzle has been an object of great mathemat-
ical interest since its invention in the 1860s. The puzzle has 16
square slots on a square board. The first 15 slots have square
pieces; the 16th slot is empty. The object of the puzzle is to slide
orthogonally the square pieces into the empty spot, thus rearrang-
ing the pieces and changing which slot is vacant, until the desired
configuration is obtained.

My goal for this project was to find non-optimal solutions to solvable puzzles for various board sizes. 
From now on I will describe an $N^2-1$ Puzzle as a board of dimensions $N*N$ with tiles numbered $1$ - $N^2-1$
For example, the standard 15-puzzle is an $N^2-1$ puzzle with $N=4$. I created code inspired by my professor, Igor Shinkar, at Simon Fraser University to create puzzle objects from text file representations.

In order to solve small instance of the puzzle ($N<=4$), I used an implementation of the AStar algorithm inspired by https://gist.github.com/leopd/5992493.
Modifying this implementation by adding a linear conflict heuristic, I was able to solve all puzzles with $N<=4$ in under one second.

Puzzles with $N>=5$ are classified as an NP-complete problem and thus A-star is unable to solve puzzles of this size due to the extreme magnitude increase in number of solutions. Inspired by https://ianparberry.com/pubs/saml.pdf, I created an algorithm that finds a tile, and manually inserts it into place using designated combinations of moves. The strategy is to complete the top row and leftmost column which reduces the problem to a board of size $N-1$. The problem size will continually be reduced until A-Star is able to solve it (Shown below is the top row and leftmost column solved).

![image](https://user-images.githubusercontent.com/108145727/232346125-bf57ef0f-7c9d-437f-8e89-166f2eb6664d.png)


