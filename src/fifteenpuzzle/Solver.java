package fifteenpuzzle;

import java.io.*;

public class Solver {
	public static void main(String[] args) {
		try {
			FifteenPuzzle puzzle = new FifteenPuzzle(args[0]);
			FifteenPuzzle ret = puzzle.PDBSolve();
			File f = new File(args[1]);
			FileWriter fw = new FileWriter(f);
			fw.write(ret.instructions);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
