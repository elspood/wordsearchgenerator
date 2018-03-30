import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class WordSearchGenerator {

	private static final int MINLENGTH = 4;
	private static final int MINWORDLIST = 5;
	private static Slot[][] grid;
	private static HashMap<String, Object> used = new HashMap<String, Object>();

	public static final String USAGE = "Arguments: [filename]\n"
			+ "[filename]: a file containing the grid layout, consecutive rows of characters and spaces\n"
			+ "            representing the grid design, one row per line, followed by a line containing\n"
			+ "            the phrase WORD LIST on its own line, followed by the list of words to be\n"
			+ "            hidden in the puzzle, line by line (minimum word length: " + MINLENGTH + ",\n"
			+ "            minimum word list length: " + MINWORDLIST + ")";

	public static void main(String[] args) {
		File f = new File(args[0]);
		Vector<String> wordlist = new Vector<String>();
		Vector<String> row = new Vector<String>();
		int maxrowlength = 0;
		try {
			BufferedReader r = new BufferedReader(new FileReader(f));
			String line;
			boolean griddone = false;
			while ((line = r.readLine()) != null) {
				if ("word list".equals(line.toLowerCase())) {
					griddone = true;
					continue;
				}
				int len = line.length();
				if (griddone) {
					line = line.trim();
					len = line.length();
					if (len == 0) continue;
					if (len < MINLENGTH) {
						System.err.println("Invalid word in word list: " + line + "\n" + USAGE);
						return;
					}
					wordlist.add(line.toUpperCase());
					continue;
				}
				row.add(line);
				if (len > maxrowlength) maxrowlength = len;
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + args[0]);
			return;
		} catch (IOException e) {
			System.err.println("Error reading input file\n" + e);
			return;
		}

		int rows = row.size();
		if ((wordlist.size() < MINWORDLIST) || (rows == 0)) {
			System.out.println("Invalid input file " + args[0] + "\n" + USAGE);
			return;
		}

		grid = new Slot[rows][maxrowlength];
		for (int i=0; i < rows; i++) {
			String w = row.get(i);
			int len = w.length();
			for (int j=0; j < len; j++) {
				grid[i][j] = (w.charAt(j) != ' ') ? new Slot() : null;
				System.out.print((grid[i][j] != null) ? '*' : ' ');
			}
			System.out.println();
		}
		// randomize word list
		Random r = new Random();
		Vector<String> randomlist = new Vector<String>();
		while (wordlist.size() > 0) {
			int i = r.nextInt(wordlist.size());
			String s = wordlist.remove(i);
			randomlist.add(s);
		}
		for (String w : randomlist) System.out.println(w);

		// generate horizontal row slots
		Vector<Slot[]> horz = new Vector<Slot[]>();
		for (int i=0; i < rows; i++) {
			int start = 0;
			while (start <= maxrowlength - MINLENGTH) {
				while ((start <= maxrowlength - MINLENGTH) && (grid[i][start] == null)) start++;
				int end = start + 1;
				while ((end < maxrowlength) && (grid[i][end] != null)) end++;
				int len = end - start;
				if (len >= MINLENGTH) {
					Slot[] s = new Slot[len];
					for (int j = start; j < end; j++) s[j - start] = grid[i][j];
					horz.add(s);
				}
				start = end + 1;
			}
		}

		// generate diagonal down (grave) slots
		Vector<Slot[]> diagd = new Vector<Slot[]>();
		for (int i=0; i <= rows - MINLENGTH; i++) {
			int start = 0;
			while ((start <= maxrowlength - MINLENGTH) && (start + i <= rows - MINLENGTH)) {
				while ((start <= maxrowlength - MINLENGTH) && (start + i <= rows - MINLENGTH) &&
						(grid[start + i][start] == null)) start++;
				int end = start + 1;
				while ((end < maxrowlength) && (end + i < rows) && (grid[end + i][end] != null)) end++;
				int len = end - start;
				if (len >= MINLENGTH) {
					Slot[] s = new Slot[len];
					for (int j = start; j < end; j++) s[j - start] = grid[i+j][j];
					diagd.add(s);
				}
				start = end + 1;
			}
		}

		for (int i=1; i <= maxrowlength - MINLENGTH; i++) {
			int start = 0;
			while ((start <= rows - MINLENGTH) && (start + i <= maxrowlength - MINLENGTH)) {
				while ((start <= rows - MINLENGTH) && (start + i <= maxrowlength - MINLENGTH) &&
						(grid[start][start + i] == null)) start++;
				int end = start + 1;
				while ((end < rows) && (end + i < maxrowlength) && (grid[end][end + i] != null)) end++;
				int len = end - start;
				if (len >= MINLENGTH) {
					Slot[] s = new Slot[len];
					for (int j = start; j < end; j++) s[j - start] = grid[j][i+j];
					diagd.add(s);
				}
				start = end + 1;
			}
		}

		// generate diagonal up (egu) slots
		Vector<Slot[]> diagu = new Vector<Slot[]>();
		for (int i=0; i <= maxrowlength - MINLENGTH; i++) {
			int start = 0;
			while ((start <= rows - MINLENGTH) && (start + i <= maxrowlength - MINLENGTH)) {
				while ((start <= rows - MINLENGTH) && (start + i <= maxrowlength - MINLENGTH) &&
						(grid[rows - 1 - start][start + i] == null)) start++;
				int end = start + 1;
				while ((end < rows) && (end + i < maxrowlength) && (grid[rows - 1 - end][end + i] != null)) end++;
				int len = end - start;
				if (len >= MINLENGTH) {
					Slot[] s = new Slot[len];
					for (int j = start; j < end; j++) s[j - start] = grid[rows - 1 - j][j + i];
					diagu.add(s);
				}
				start = end + 1;
			}
		}

		for (int i=1; i <= rows - MINLENGTH; i++) {
			int start = 0;
			while ((start <= maxrowlength - MINLENGTH) && (start + i <= rows - MINLENGTH)) {
				while ((start <= maxrowlength - MINLENGTH) && (start + i <= rows - MINLENGTH) &&
						(grid[rows - 1 - i - start][start] == null)) start++;
				int end = start + 1;
				while ((end < maxrowlength) && (end + i < rows) && (grid[rows - 1 - i - end][end] != null)) end++;
				int len = end - start;
				if (len >= MINLENGTH) {
					Slot[] s = new Slot[len];
					for (int j = start; j < end; j++) s[j - start] = grid[rows - 1 - i - j][j];
					diagu.add(s);
				}
				start = end + 1;
			}
		}

		// generate vertical slots
		Vector<Slot[]> vert = new Vector<Slot[]>();
		for (int i=0; i < maxrowlength; i++) {
			int start = 0;
			while (start <= rows - MINLENGTH) {
				while ((start <= rows - MINLENGTH) && (grid[start][i] == null)) start++;
				int end = start + 1;
				while ((end < rows) && (grid[end][i] != null)) end++;
				int len = end - start;
				if (len >= MINLENGTH) {
					Slot[] s = new Slot[len];
					for (int j = start; j < end; j++) s[j - start] = grid[j][i];
					vert.add(s);
				}
				start = end + 1;
			}
		}

		Vector<Slot[]> slots = new Vector<Slot[]>();
		for (Slot[] s : diagd) slots.add(s);
		for (Slot[] s : vert) slots.add(s);
		for (Slot[] s : horz) slots.add(s);
		for (Slot[] s : diagu) slots.add(s);

		System.out.println(horz.size() + " horz slots");
		System.out.println(diagd.size() + " diagd slots");
		System.out.println(diagu.size() + " diagu slots");
		System.out.println(vert.size() + " vert slots");
		for (int i=0; i < slots.size(); i++) {
			Slot[] s = slots.get(i);
			for (int j=0; j < s.length; j++) if (s[j] == null) {
				System.err.println("Null slot found! [" + i + "," + j + "]");
				return;
			}
		}

		makeGrid(randomlist, slots);
	}

	private static void makeGrid(Vector<String> wordlist, Vector<Slot[]> slots) {
		String word = wordlist.remove(0);
		int wordlen = word.length();
		for (int s=0; s < slots.size(); s++) {
			Slot[] slot = slots.get(s);
			int max = slot.length - wordlen;
			int i = 0;
			while (i <= max) {
				for (int j=0; j < wordlen; j++)
					slot[i + j].letter = word.charAt(j);
				makeGridCrossing(wordlist, slots);
				for (int j=0; j < wordlen; j++)
					slot[i + j].letter = word.charAt(wordlen - j - 1);
				makeGridCrossing(wordlist, slots);
				slot[i].letter = Slot.EMPTY;
				i++;
			}
			while (i < slot.length) slot[i++].letter = Slot.EMPTY;
		}
	}

	private static boolean balanced(Vector<Slot[]> slots) {
		for (Slot[] slot : slots) {
			int empty = 0;
			for (int i=0; i < slot.length; i++) {
				if (slot[i].letter == Slot.EMPTY) {
					empty++;
					if (empty == 4) return false;
				} else empty = 0;
			}
		}
		return true;
	}

	private static String hashGrid() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i < grid.length; i++)
			for (int j=0; j < grid[i].length; j++)
				if (grid[i][j] != null) sb.append(grid[i][j].letter);
		return sb.toString();
	}

	private static void makeGridCrossing(Vector<String> wordlist, Vector<Slot[]> slots) {
		//String hash = hashGrid();
		//if (used.containsKey(hash)) return;
		//used.put(hash, null);

		int size = wordlist.size();
		if (size == 0) {
			if (balanced(slots)) printGrid();
			return;
		}
		for (int i=0; i < size; i++) {
			String word = wordlist.remove(0);
			int wordlen = word.length();
			for (int s=0; s < slots.size(); s++) {
				Slot[] slot = slots.get(s);
				int max = slot.length - wordlen;
				for (int w=0; w <= max; w++) {
					boolean[] crossed = placeWord(word, slot, w, false);
					if (crossed != null) {
						for (int j=0; j < wordlen; j++) if (!crossed[j])
							slot[w + j].letter = word.charAt(j);
						makeGridCrossing(wordlist, slots);
						for (int j=0; j < wordlen; j++) if (!crossed[j])
							slot[w + j].letter = Slot.EMPTY;
					}

					crossed = placeWord(word, slot, w, true);
					if (crossed != null) {
						for (int j=0; j < wordlen; j++) if (!crossed[j])
							slot[w + j].letter = word.charAt(wordlen - 1 - j);
						makeGridCrossing(wordlist, slots);
						for (int j=0; j < wordlen; j++) if (!crossed[j])
							slot[w + j].letter = Slot.EMPTY;
					}
				}
			}
			wordlist.add(word);
		}
	}

	// finds all the matching crossed letters for the word in the slot
	// returns null if crossing letters don't match, or NO crossing letters are found at all
	private static boolean[] placeWord(String word, Slot[] slot, int start, boolean reverse) {
		int wordlen = word.length();
		boolean[] crossed = new boolean[wordlen];
		boolean found = false;
		for (int i=0; i < wordlen; i++) {
			char let = slot[start + i].letter;
			if (let == Slot.EMPTY) continue;

			int idx = (reverse) ? wordlen - i - 1 : i;
			if (let != word.charAt(idx)) return null;

			found = true;
			crossed[i] = true;
		}
		if (!found) return null;
		return crossed;
	}

	private static void printGrid() {
		StringBuilder sb = new StringBuilder();
		int empty = 0;;
		for (int i=0; i < grid.length; i++) {
			for (int j=0; j < grid[i].length; j++) {
				if (grid[i][j] == null) sb.append(" ");
				else {
					if (grid[i][j].letter == Slot.EMPTY) empty++;
					sb.append(grid[i][j].letter);
				}
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
		if (empty > 0) System.out.println(empty + " empty spaces");
	}
}
