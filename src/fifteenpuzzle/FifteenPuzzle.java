package fifteenpuzzle;

import java.io.*;
import java.util.*;

public class FifteenPuzzle {
    public final static int UP = 0;
    public final static int DOWN = 1;
    public final static int LEFT = 2;
    public final static int RIGHT = 3;

    public class TilePos {
        public int x;
        public int y;

        public TilePos(int y, int x) {
            this.x = x;
            this.y = y;
        }
    }

    public int SIZE;
    public int PDBSIZE;
    public int board[][];
    private TilePos blank;
    public String instructions = "";

    private void checkBoard() throws BadBoardException {
        int[] vals = new int[SIZE * SIZE];

        // check that the board contains all number 0...15
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] < 0 || board[i][j] >= SIZE * SIZE)
                    throw new BadBoardException("found tile " + board[i][j]);
                vals[board[i][j]] += 1;
            }
        }

        for (int i = 0; i < vals.length; i++)
            if (vals[i] != 1)
                throw new BadBoardException("tile " + i +
                        " appears " + vals[i] + "");

    }

    /**
     * @param fileName
     * @throws FileNotFoundException if file not found
     * @throws BadBoardException     if the board is incorrectly formatted Reads a
     *                               board from file and creates the board
     */
    public FifteenPuzzle(String fileName) throws IOException, BadBoardException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        SIZE = br.read() - '0';
        PDBSIZE = SIZE;
        br.read();

        board = new int[SIZE][SIZE];
        int c1, c2, s;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                c1 = br.read();
                c2 = br.read();
                s = br.read(); // skip the space
                if (s != ' ' && s != '\n') {
                    br.close();
                    throw new BadBoardException("error in line " + i);
                }
                if (c1 == ' ')
                    c1 = '0';
                if (c2 == ' ')
                    c2 = '0';
                int data = 10 * (c1 - '0') + (c2 - '0');
                board[i][j] = data;
                if (data == 0) {
                    blank = new TilePos(i, j);
                }
            }
        }
        checkBoard();

        br.close();
    }

    public FifteenPuzzle(int SIZE) {
        board = new int[SIZE][SIZE];
        this.SIZE = SIZE;
        this.PDBSIZE = SIZE;
        int cnt = 1;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = cnt;
                cnt++;
            }
        }
        // init blank
        blank = new TilePos(SIZE - 1, SIZE - 1);
        board[blank.y][blank.x] = 0;
    }

    public FifteenPuzzle(FifteenPuzzle toClone) {
        this(toClone.SIZE); // chain to basic init
        for (TilePos p : allTilePos()) {
            board[p.y][p.x] = toClone.tile(p);
        }
        instructions = toClone.instructions;
        blank = toClone.getBlank();
        PDBSIZE = toClone.PDBSIZE;
    }

    public List<TilePos> allTilePos() {
        ArrayList<TilePos> out = new ArrayList<TilePos>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                out.add(new TilePos(i, j));
            }
        }
        return out;
    }

    public int tile(TilePos p) {
        return board[p.y][p.x];
    }

    public TilePos getBlank() {
        return blank;
    }

    public TilePos whereIs(int x) {
        for (TilePos p : allTilePos()) {
            if (tile(p) == x) {
                return p;
            }
        }
        return null;
    }

    public TilePos correct(int x) {
        int cnt = 1;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (cnt == x) {
                    return new TilePos(i, j);
                }
                cnt++;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FifteenPuzzle) {
            for (TilePos p : allTilePos()) {
                if (this.tile(p) != ((FifteenPuzzle) o).tile(p)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int out = 0;
        for (TilePos p : allTilePos()) {
            out = (out * SIZE * SIZE) + this.tile(p);
        }
        return out;
    }

    public List<TilePos> allValidMoves() {
        ArrayList<TilePos> out = new ArrayList<TilePos>();
        int y = blank.y;
        int x = blank.x;
        Integer[] moves = new Integer[4];
        if (y < SIZE - 1) {
            moves[0] = board[y + 1][x];
        }
        if (y > 0) {
            moves[1] = board[y - 1][x];
        }
        if (x < SIZE - 1) {
            moves[2] = board[y][x + 1];
        }
        if (x > 0) {
            moves[3] = board[y][x - 1];
        }
        for (int dir = 0; dir < 4; dir++) {
            if ((moves[dir] != null) && isValidMove(moves[dir], dir)) {
                out.add(new TilePos(moves[dir], dir)); // represent tile, direction pair as a tile pos
            }
        }
        return out;
    }

    public boolean isValidMove(int tile, int direction) {
        TilePos p = whereIs(tile);
        boolean flag = true;
        if ((direction == UP) && (p.y <= 0)) {
            flag = false;
        }
        if ((direction == DOWN) && (p.y >= SIZE - 1)) {
            flag = false;
        }
        if ((direction == LEFT) && (p.x <= 0)) {
            flag = false;
        }
        if ((direction == RIGHT) && (p.x >= SIZE - 1)) {
            flag = false;
        }
        int dx = blank.x - p.x;
        int dy = blank.y - p.y;
        if ((Math.abs(dx) + Math.abs(dy) != 1) || (dx * dy != 0)) {
            flag = false;
        }
        return flag;
    }

    /**
     * Get the number of the tile, and moves it to the specified direction
     * 
     * @throws IllegalMoveException if the move is illegal
     */
    public void move(Integer tile, int direction) throws IllegalMoveException {
        instructions += tile.toString() + " ";

        TilePos p = whereIs(tile);
        if (p == null)
            throw new IllegalMoveException("tile " + tile + " not found");
        int i = p.y;
        int j = p.x;

        // the tile is in position [i][j]
        if (direction == UP) {
            if ((i > 0) && (board[i - 1][j] == 0)) {
                board[i - 1][j] = tile;
                board[i][j] = 0;
                instructions += "U\n";
            } else
                throw new IllegalMoveException("" + tile + "cannot move UP");
        } else if (direction == DOWN) {
            if ((i < SIZE - 1) && (board[i + 1][j] == 0)) {
                board[i + 1][j] = tile;
                board[i][j] = 0;
                instructions += "D\n";
            } else
                throw new IllegalMoveException("" + tile + "cannot move DOWN");
        } else if (direction == RIGHT) {
            if ((j < SIZE - 1) && (board[i][j + 1] == 0)) {
                board[i][j + 1] = tile;
                board[i][j] = 0;
                instructions += "R\n";
            } else
                throw new IllegalMoveException("" + tile + "cannot move LEFT");
        } else if (direction == LEFT) {
            if (j > 0 && board[i][j - 1] == 0) {
                board[i][j - 1] = tile;
                board[i][j] = 0;
                instructions += "L\n";
            } else
                throw new IllegalMoveException("" + tile + "cannot move LEFT");
        } else {
            throw new IllegalMoveException("Unexpected direction: " + direction);
        }
        blank = p;
    }

    /**
     * returns a new puzzle with the move applied
     * 
     * @param p
     * @return
     */
    public FifteenPuzzle moveClone(TilePos p) {
        FifteenPuzzle out = new FifteenPuzzle(this);

        try {
            out.move(p.y, p.x);
        } catch (Exception e) {
            System.out.println(e);
        }

        return out;
    }

    /**
     * A* heuristic.
     * Total manhattan distance (L1 norm) from each non-blank tile to its correct
     * position
     * 
     * @return
     */
    public int manhattanDistance() {
        int sum = 0;
        for (TilePos p : allTilePos()) {
            int val = tile(p);
            if (val > 0) {
                TilePos correct = correct(val);
                sum += Math.abs(correct.x - p.x);
                sum += Math.abs(correct.y - p.y);
            }
        }
        return sum;
    }

    public int linearConflict() {
        int sum = 0;
        for (TilePos p : allTilePos()) {
            int val = tile(p);
            TilePos correct = correct(val);
            if ((val > 0) && (val != board[correct.y][correct.x])) {
                int i;
                TilePos otherCorrect;
                int otherVal;
                if (p.y == correct.y) { // tile is in correct row
                    for (i = p.x + 1; i < SIZE; i++) { // check for linear conflicting values in rest of row
                        otherVal = board[p.y][i];
                        otherCorrect = correct(otherVal);
                        if ((otherVal != 0) && (val > otherVal) && (i != otherCorrect.x)) {
                            sum++;
                        }
                    }
                } else if (p.x == correct.x) { // tile is in correct col
                    for (i = p.y + 1; i < SIZE; i++) { // check for linear conflicting values in rest of col
                        otherVal = board[i][p.x];
                        otherCorrect = correct(otherVal);
                        if ((otherVal != 0) && (val > otherVal) && (i != otherCorrect.y)) {
                            sum++;
                        }
                    }
                }
            }
        }
        return 2 * sum;
    }

    public int PDBScore() {
        return manhattanDistance() + linearConflict();
    }

    public List<FifteenPuzzle> allAdjacentPuzzles() {
        ArrayList<FifteenPuzzle> out = new ArrayList<FifteenPuzzle>();
        for (TilePos move : allValidMoves()) {
            out.add(moveClone(move));
        }
        return out;
    }

    /**
     * returns a solved board if it was able to solve it, or else null
     * 
     * @return
     */
    public FifteenPuzzle aStarSolve() {
        HashMap<FifteenPuzzle, FifteenPuzzle> predecessor = new HashMap<FifteenPuzzle, FifteenPuzzle>();
        HashMap<FifteenPuzzle, Integer> depth = new HashMap<FifteenPuzzle, Integer>();
        final HashMap<FifteenPuzzle, Integer> score = new HashMap<FifteenPuzzle, Integer>();
        Comparator<FifteenPuzzle> comparator = new Comparator<FifteenPuzzle>() {
            @Override
            public int compare(FifteenPuzzle a, FifteenPuzzle b) {
                return score.get(a) - score.get(b);
            }
        };
        PriorityQueue<FifteenPuzzle> toVisit = new PriorityQueue<FifteenPuzzle>(10000, comparator);
        predecessor.put(this, null);
        depth.put(this, 0);
        score.put(this, this.PDBScore());
        toVisit.add(this);
        while (toVisit.size() > 0) {
            FifteenPuzzle candidate = toVisit.remove();
            if (candidate.isSolved()) {
                return candidate;
            }
            for (FifteenPuzzle fp : candidate.allAdjacentPuzzles()) {
                if (!predecessor.containsKey(fp)) {
                    predecessor.put(fp, candidate);
                    depth.put(fp, depth.get(candidate) + 1);
                    int estimate = fp.PDBScore();
                    score.put(fp, depth.get(candidate) + 1 + estimate);
                    // dont' add to p-queue until the metadata is in place that the comparator needs
                    toVisit.add(fp);
                }
            }
        }
        return null;
    }

    public FifteenPuzzle GreedyMoveToTile(int tile) {
        TilePos p = whereIs(tile);
        FifteenPuzzle curr = new FifteenPuzzle(this);
        int x = curr.blank.x;
        int y = curr.blank.y;
        int dx = x - p.x;
        int dy = y - p.y;
        while (Math.abs(dx) > 1) { // move until we are within 1 square of tile in x direction
            if (dx > 1) { // positive dx values mean we are to right of target tile
                curr = curr.moveClone(new TilePos(curr.board[y][x - 1], 3)); // move blank left
                dx--;
            } else {
                curr = curr.moveClone(new TilePos(curr.board[y][x + 1], 2)); // move blank right
                dx++;
            }
            x = curr.blank.x;
        }
        while (Math.abs(dy) > 1) { // move until we are within 1 square of tile in y direction
            if (dy > 1) { // positive dy value mean we are below target tile
                curr = curr.moveClone(new TilePos(curr.board[y - 1][x], 1)); // move blank up
                dy--;
            } else {
                curr = curr.moveClone(new TilePos(curr.board[y + 1][x], 0)); // move blank down
                dy++;
            }
            y = curr.blank.y;
        }
        if (dx != 0) {
            if (p.y < SIZE - 1) { // if tile is not in in last row
                while (dy < 1) { // while we are not below tile
                    curr = curr.moveClone(new TilePos(curr.board[y + 1][x], 0));
                    dy++;
                    y = curr.blank.y;
                }
            } else { // tile is in last row
                while (dy > -1) { // while we are not in the row above tile
                    curr = curr.moveClone(new TilePos(curr.board[y - 1][x], 1));
                    dy--;
                    y = curr.blank.y;
                }
            }
            if (dx == 1) { // positive dx values mean we are to right of target tile
                curr = curr.moveClone(new TilePos(curr.board[y][x - 1], 3)); // move blank left
                dx--;
            } else {
                curr = curr.moveClone(new TilePos(curr.board[y][x + 1], 2)); // move blank right
                dx++;
            }
            x = curr.blank.x;
        }
        if (dy == 1) {
            curr = curr.moveClone(new TilePos(curr.board[y - 1][x], 1)); // move blank up
        }
        return curr;
    }

    public FifteenPuzzle GreedyMoveLeft(int tile) {
        FifteenPuzzle curr = new FifteenPuzzle(this);
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x - 1], 3)); // move blank left
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
        curr = curr.moveClone(new TilePos(tile, 2)); // move target tile left
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y - 1][curr.blank.x], 1)); // move blank up
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x - 1], 3)); // move blank left
        return curr;
    }

    public FifteenPuzzle GreedyMoveRight(int tile) {
        FifteenPuzzle curr = new FifteenPuzzle(this);
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x + 1], 2)); // move blank right
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
        curr = curr.moveClone(new TilePos(tile, 3)); // move target tile right
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y - 1][curr.blank.x], 1)); // move blank up
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x + 1], 2)); // move blank right
        return curr;
    }

    public FifteenPuzzle GreedyMoveUp(int tile) {
        FifteenPuzzle curr = new FifteenPuzzle(this);
        curr = curr.moveClone(new TilePos(tile, 0)); // move target tile up
        // if blank is not in row below first row of fringe data base and has room to it
        // right
        if ((curr.blank.y > curr.SIZE - curr.PDBSIZE + 1) && (curr.blank.x < curr.SIZE - 1)) {
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x + 1], 2)); // move blank right
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y - 1][curr.blank.x], 1)); // move blank up
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y - 1][curr.blank.x], 1)); // move blank up
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x - 1], 3)); // move blank left
        } // blank is not in first row below fringe data base but doesnt have room to its
          // right (we assume it has room to its left)
        else if (curr.blank.y > curr.SIZE - curr.PDBSIZE + 1) {

            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x - 1], 3)); // move blank left
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y - 1][curr.blank.x], 1)); // move blank up
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y - 1][curr.blank.x], 1)); // move blank up
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x + 1], 2)); // move blank right
        }
        return curr;
    }

    public FifteenPuzzle GreedyMoveDown(int tile) {
        FifteenPuzzle curr = new FifteenPuzzle(this);
        // if blank has room to its right
        if (curr.blank.x < curr.SIZE - 1) {
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x + 1], 2)); // move blank right
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x - 1], 3)); // move blank left
        } // blank doesnt have room to its right
        else {
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x - 1], 3)); // move blank left
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x + 1], 2)); // move blank right
        }
        curr = curr.moveClone(new TilePos(tile, 1)); // move target tile down
        return curr;
    }

    FifteenPuzzle GreedyMoveToTargetInRow(int tile) {
        TilePos p = correct(tile);
        if (this.board[p.y][p.x] == tile) {
            return this;
        }
        FifteenPuzzle curr = this.GreedyMoveToTile(tile);
        p.y += 1; // want to be one slot below its target
        int x = curr.blank.x;
        int y = curr.blank.y + 1;
        int dx = x - p.x;
        int dy = y - p.y;
        while (dx != 0) { // move until we are within 1 square of target in x direction
            if (dx > 0) { // positive dx values mean we are to right of target
                curr = curr.GreedyMoveLeft(tile);
                dx--;
            } else {
                curr = curr.GreedyMoveRight(tile);
                dx++;
            }
        }
        while (dy > 0) { // Assume we will always be below target row
            curr = curr.GreedyMoveUp(tile);
            dy--;
        }
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
        return curr;
    }

    FifteenPuzzle GreedySlotLastPieceInRow(int tile) {
        TilePos p = correct(tile);
        if (this.board[p.y][p.x] == tile) {
            return this;
        }
        FifteenPuzzle curr = this.GreedyMoveToTile(tile);
        p.y += 2; // want to place last piece two slots below where it is supposed to be
        int x = curr.blank.x;
        int y = curr.blank.y + 1;
        int dx = x - p.x;
        int dy = y - p.y;
        while (dx < 0) { // move tile to last col
            curr = curr.GreedyMoveRight(tile);
            dx++;
        }
        while (dy != 0) { // move until tile is 2 squares below target dest
            if (dy > 0) {
                curr = curr.GreedyMoveUp(tile);
                dy--;
            } else {
                curr = curr.GreedyMoveDown(tile);
                dy++;
            }
        }
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
        int i;
        for (i = 0; i < PDBSIZE - 1; i++) {
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x - 1], 3)); // move blank left
        }
        for (i = 0; i < 2; i++) {
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y - 1][curr.blank.x], 1)); // move blank up
        }
        for (i = 0; i < PDBSIZE - 2; i++) {
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x + 1], 2)); // move blank right
        }
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x + 1], 2)); // move blank right
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x - 1], 3)); // move blank left
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y - 1][curr.blank.x], 1)); // move blank up
        for (i = 0; i < PDBSIZE - 2; i++) {
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x - 1], 3)); // move blank left
        }
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
        return curr;
    }

    FifteenPuzzle GreedyMoveToTargetInCol(int tile) {
        TilePos p = correct(tile);
        if (this.board[p.y][p.x] == tile) {
            return this;
        }
        FifteenPuzzle curr = this.GreedyMoveToTile(tile);
        p.y += 1; // want to place tile one below target
        int x = curr.blank.x;
        int y = curr.blank.y + 1;
        int dx = x - p.x;
        int dy = y - p.y;
        while (dy != 0) { // move until we are within 1 square of target in y direction
            if (dy > 0) { // positive dy values mean we are below target
                curr = curr.GreedyMoveUp(tile);
                dy--;
            } else {
                curr = curr.GreedyMoveDown(tile);
                dy++;
            }
        }
        while (dx > 0) { // Assume we will always be right of target col
            curr = curr.GreedyMoveLeft(tile);
            dx--;
        }
        if (dy == 0) {
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x + 1], 2)); // move blank right
        }
        return curr;
    }

    FifteenPuzzle GreedySlotLastPieceInCol(int tile) {
        TilePos p = correct(tile);
        if (this.board[p.y][p.x] == tile) {
            return this;
        }
        FifteenPuzzle curr = this.GreedyMoveToTile(tile);
        p.x += 2; // want to place last piece two slots to right where it is supposed to be
        int x = curr.blank.x;
        int y = curr.blank.y + 1;
        int dx = x - p.x;
        int dy = y - p.y;
        while (dy != 0) { // move until tile is on same level as target in y direction
            if (dy > 0) { // positive dy values mean we are below target
                curr = curr.GreedyMoveUp(tile);
                dy--;
            } else {
                curr = curr.GreedyMoveDown(tile);
                dy++;
            }
        }
        while (dx != 0) { // move until tile is right two squares of target in x direction
            if (dx > 0) { // positive dx values means we are to right of target
                curr = curr.GreedyMoveLeft(tile);
                dx--;
            } else {
                curr = curr.GreedyMoveRight(tile);
                dx++;
            }
        }
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x - 1], 3)); // move blank left
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x + 1], 2)); // move blank right
        int i;
        for (i = 0; i < PDBSIZE - 2; i++) {
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y - 1][curr.blank.x], 1)); // move blank up
        }
        for (i = 0; i < 2; i++) {
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x - 1], 3)); // move blank left
        }
        for (i = 0; i < PDBSIZE - 3; i++) {
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
        }
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y + 1][curr.blank.x], 0)); // move blank down
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x + 1], 2)); // move blank right
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y - 1][curr.blank.x], 1)); // move blank up
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x - 1], 3)); // move blank left
        for (i = 0; i < PDBSIZE - 3; i++) {
            curr = curr.moveClone(new TilePos(curr.board[curr.blank.y - 1][curr.blank.x], 1)); // move blank up
        }
        curr = curr.moveClone(new TilePos(curr.board[curr.blank.y][curr.blank.x + 1], 2)); // move blank right
        return curr;
    }

    /**
     * returns a board with fringe database solved up to size 4
     * 
     * @return
     */
    public FifteenPuzzle PDBGreedy() {
        int tile = 1;
        FifteenPuzzle ret = this.GreedyMoveToTile(tile);
        int diff = ret.SIZE - ret.PDBSIZE;
        int topLeft = (SIZE * diff + diff + 1) % (SIZE * SIZE);
        while (ret.PDBSIZE > 3) {
            diff = ret.SIZE - ret.PDBSIZE;
            topLeft = (SIZE * diff + diff + 1) % (SIZE * SIZE);
            for (tile = topLeft; tile < SIZE + SIZE * diff; tile++) {
                ret = ret.GreedyMoveToTargetInRow(tile);
            }
            ret = ret.GreedySlotLastPieceInRow(tile);
            for (tile = topLeft + SIZE; tile < SIZE * SIZE - ret.PDBSIZE + 1; tile += SIZE) {
                ret = ret.GreedyMoveToTargetInCol(tile);
            }
            ret = ret.GreedySlotLastPieceInCol(tile);
            ret.PDBSIZE--;
        }
        return ret;
    }

    public FifteenPuzzle PDBSolve() {
        FifteenPuzzle ret = this.PDBGreedy();
        ret = ret.aStarSolve();
        return ret;
    }

    public FifteenPuzzle performInstructions(String filename)
            throws IOException, BadBoardException, IllegalMoveException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();
        int tile;
        int direction;
        while (line != null) {
            String[] words = line.split(" ");
            if (words[0].length() == 2) {
                tile = (words[0].charAt(0) - '0') * 10 + (words[0].charAt(1)) - '0';
            } else {
                tile = (words[0].charAt(0) - '0');
            }
            if (words[1].equals("U")) {
                direction = 0;
            } else if (words[1].equals("D")) {
                direction = 1;
            } else if (words[1].equals("L")) {
                direction = 2;
            } else if (words[1].equals("R")) {
                direction = 3;
            } else {
                direction = 4;
            }
            this.move(tile, direction);
            line = br.readLine();
        }
        br.close();
        return this;
    }

    /**
     * @return true if and only if the board is solved, i.e., the board has all
     *         tiles in their correct positions
     */
    public boolean isSolved() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (board[i][j] != (SIZE * i + j + 1) % (SIZE * SIZE))
                    return false;
        return true;
    }

    private String num2str(int i) {
        if (i == 0)
            return "  ";
        else if (i < 10)
            return " " + Integer.toString(i);
        else
            return Integer.toString(i);
    }

    public String toString() {
        String ans = "";
        for (int i = 0; i < SIZE; i++) {
            ans += num2str(board[i][0]);
            for (int j = 1; j < SIZE; j++)
                ans += " " + num2str(board[i][j]);
            ans += "\n";
        }
        return ans;
    }
}
