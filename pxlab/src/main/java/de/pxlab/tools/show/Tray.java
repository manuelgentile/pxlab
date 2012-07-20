package de.pxlab.tools.show;

import java.io.*;
import java.util.*;
import de.pxlab.util.*;

/**
 * A list of files contained in a set of directory trees. Creates the list of
 * files while preserving the directories which contain the files. Also provides
 * an iterator-like method for sequentially retrieving files from the list.
 * Retrieval may be controlled by various interactive methods.
 * 
 * @version 0.1.1
 */
public class Tray extends ArrayList {
	private int currentDirectoryIndex;
	private int currentFileIndex;
	private int currentIndex;
	private int indexIncrement;
	private boolean directoryOverflow;
	private boolean stopFlag;
	private DirectoryPointer currentDirectoryPointer;
	private String currentDirectory;
	private String currentFile;
	private String currentPath;
	private ArrayList directoryList;
	private ArrayList randomDirectoryList;
	private ArrayList fileList;
	private ArrayList randomFileList;
	private ArrayList randomTray;
	private int debug;
	public static final int DBG_ALL = 63;
	public static final int DBG_FILE_NAME = 1 << 0;
	public static final int DBG_DIR_NAME = 1 << 1;
	public static final int DBG_FILE_LIST = 1 << 2;
	public static final int DBG_DIR_LIST = 1 << 3;
	public static final int DBG_INDEX = 1 << 4;
	private boolean randomDirectory = false;
	private boolean randomFile = false;
	private boolean randomAll = false;

	/**
	 * Create a tray of files. All files within the given root trees satisfying
	 * any of the given extensions are included.
	 * 
	 * @param cmdPath
	 *            a list of root directories from the command line.
	 * @param extList
	 *            a list of file extensions.
	 */
	public Tray(ArrayList cmdPath, ArrayList extList, int dbg) {
		super(2000);
		setDebug(dbg);
		String[] ext = StringExt.stringArrayOfList(extList);
		for (int i = 0; i < cmdPath.size(); i++) {
			try {
				addAll(new FileList((String) cmdPath.get(i), ext));
			} catch (FileNotFoundException fnx) {
				System.out.println("Tray() Error: Directory "
						+ (String) cmdPath.get(i) + " not found.");
			}
		}
		if ((debug & DBG_FILE_LIST) != 0)
			print();
		createDirectoryList();
		stopFlag = false;
		directoryOverflow = false;
		indexIncrement = 1;
		currentIndex = 0 - indexIncrement;
	}

	/**
	 * Set the randomization method for walking through the list of files.
	 * 
	 * @param rd
	 *            if true then the directory sequence is randomized.
	 * @param rf
	 *            if true then the file sequence within directories is
	 *            randomized.
	 * @param ra
	 *            if true then the file sequence is randomized across all
	 *            directories and files.
	 */
	public void setRandomization(boolean rd, boolean rf, boolean ra) {
		randomDirectory = rd;
		randomFile = rf;
		randomAll = ra;
	}

	/**
	 * Set the increment for the file index when walking through the list of
	 * files.
	 * 
	 * @param i
	 *            the file pointer increment. Should be 1 or -1.
	 */
	public void setIndexIncrement(int i) {
		indexIncrement = i;
	}

	/**
	 * Get the increment for the file index.
	 * 
	 * @return the index icrement value.
	 */
	public int getIndexIncrement() {
		return indexIncrement;
	}

	/**
	 * Set a debugging code.
	 * 
	 * @param d
	 *            the debugging code.
	 */
	public void setDebug(int d) {
		debug = debug | d;
		if (debug > 0)
			System.out.println("debug = " + debug);
	}

	/**
	 * Finds the directories contained in the tray and stores their pointers in
	 * the directoryList object.
	 */
	private void createDirectoryList() {
		directoryList = new ArrayList(100);
		String d = "";
		for (int i = 0; i < size(); i++) {
			File f = new File((String) get(i));
			String fp = f.getParent();
			if (!d.equals(fp)) {
				directoryList.add(new DirectoryPointer(fp, i));
				d = fp;
			}
		}
		randomDirectoryList = new ArrayList(directoryList);
		Collections.shuffle(randomDirectoryList);
		if ((debug & DBG_DIR_LIST) != 0)
			for (Iterator it = directoryList.iterator(); it.hasNext();) {
				System.out.println((DirectoryPointer) it.next());
			}
	}

	private DirectoryPointer nextDirectoryPointer(ArrayList list) {
		DirectoryPointer dp = null;
		int i = 0;
		if (currentDirectoryPointer == null) {
			i = (indexIncrement > 0) ? 0 : (list.size() - 1);
		} else {
			i = list.indexOf(currentDirectoryPointer);
			if (indexIncrement > 0) {
				i++;
				if (i >= list.size())
					i = 0;
			} else {
				i--;
				if (i < 0)
					i = list.size() - 1;
			}
		}
		dp = (DirectoryPointer) list.get(i);
		return dp;
	}

	private DirectoryPointer previousDirectoryPointer(ArrayList list) {
		DirectoryPointer dp = null;
		int i = 0;
		if (currentDirectoryPointer == null) {
			i = (indexIncrement > 0) ? 0 : (list.size() - 1);
		} else {
			i = list.indexOf(currentDirectoryPointer);
			if (indexIncrement < 0) {
				i++;
				if (i >= list.size())
					i = 0;
			} else {
				i--;
				if (i < 0)
					i = list.size() - 1;
			}
		}
		dp = (DirectoryPointer) list.get(i);
		return dp;
	}

	/** Get the list of file indices for the given directory. */
	private ArrayList getFileList(DirectoryPointer d) {
		int firstIndex = d.index;
		int lastIndex = size() - 1;
		int i = directoryList.indexOf(d);
		if (i < (directoryList.size() - 1)) {
			lastIndex = ((DirectoryPointer) directoryList.get(i + 1)).index - 1;
		}
		ArrayList f = new ArrayList(lastIndex - firstIndex + 1);
		for (int k = firstIndex; k <= lastIndex; k++) {
			Object x = get(k);
			f.add(x);
			// System.out.println("Item " + k + " = " + x);
		}
		return f;
	}

	private ArrayList getRandomFileList(DirectoryPointer d) {
		ArrayList f = new ArrayList(getFileList(d));
		Collections.shuffle(f);
		return f;
	}

	/**
	 * Move the file index such that files are skipped when walking through the
	 * list of files.
	 * 
	 * @param s
	 *            the number of files to be skipped.
	 */
	public void skip(int s) {
		for (int i = 0; (i < s) && hasNext(); i++)
			next();
	}

	/**
	 * Move the directory index such that the next file is from the next
	 * directory.
	 */
	public void gotoNextDirectory() {
		ArrayList dirList = randomDirectory ? randomDirectoryList
				: directoryList;
		currentDirectoryPointer = nextDirectoryPointer(dirList);
		fileList = null;
		if ((debug & DBG_DIR_NAME) != 0)
			System.out.println("New directory is " + currentDirectoryPointer);
	}

	/**
	 * Move the directory index such that the next file is from the previous
	 * directory.
	 */
	public void gotoPreviousDirectory() {
		ArrayList dirList = randomDirectory ? randomDirectoryList
				: directoryList;
		currentDirectoryPointer = previousDirectoryPointer(dirList);
		fileList = null;
		if ((debug & DBG_DIR_NAME) != 0)
			System.out.println("New directory is " + currentDirectoryPointer);
	}

	private int nextIndex(int currentIndex, int increment, int minIndex,
			int upperLimit) {
		int i = currentIndex + increment;
		if (i < minIndex)
			i = upperLimit - 1;
		else if (i >= upperLimit)
			i = minIndex;
		if ((debug & DBG_INDEX) != 0)
			System.out.println("currentIndex = " + currentIndex + " -> " + i);
		return i;
	}

	public String next() {
		String f = null;
		if (randomAll) {
			if (randomTray == null) {
				randomTray = new ArrayList(this);
				Collections.shuffle(randomTray);
			}
			currentIndex = nextIndex(currentIndex, indexIncrement, 0, size());
			f = (String) randomTray.get(currentIndex);
			stopFlag = (indexIncrement > 0) ? (currentIndex == (size() - 1))
					: (currentIndex == 0);
			currentDirectoryPointer = getDirectoryPointer(f);
			if ((debug & DBG_DIR_NAME) != 0)
				System.out.println("New directory is "
						+ currentDirectoryPointer);
		} else if (randomDirectory) {
			f = nextFile(randomDirectoryList);
		} else if (randomFile) {
			f = nextFile(directoryList);
		} else {
			f = nextFile(directoryList);
			stopFlag = (indexIncrement > 0) ? (currentIndex == (size() - 1))
					: (currentIndex == 0);
		}
		if ((debug & DBG_FILE_NAME) != 0)
			System.out.println("Next file is " + f + " [" + currentIndex + "]");
		if (((debug & DBG_FILE_NAME) != 0) && stopFlag)
			System.out.println(" -- stopFlag set!");
		return f;
	}

	private String nextFile(ArrayList dirList) {
		if (currentDirectoryPointer == null) {
			int i = (indexIncrement > 0) ? 0 : (dirList.size() - 1);
			currentDirectoryPointer = (DirectoryPointer) dirList.get(i);
			if ((debug & DBG_DIR_NAME) != 0)
				System.out.println("New directory is "
						+ currentDirectoryPointer);
		}
		if (fileList == null) {
			fileList = randomFile ? getRandomFileList(currentDirectoryPointer)
					: getFileList(currentDirectoryPointer);
			if ((debug & DBG_DIR_NAME) != 0)
				System.out.println("Directory " + currentDirectoryPointer
						+ " contains " + fileList.size() + " files.");
			currentFileIndex = (indexIncrement > 0) ? 0 : (fileList.size() - 1);
		} else {
			if ((indexIncrement > 0 && currentFileIndex == (fileList.size() - 1))
					|| (indexIncrement < 0 && currentFileIndex == 0)) {
				currentDirectoryPointer = nextDirectoryPointer(dirList);
				if ((debug & DBG_DIR_NAME) != 0)
					System.out.println("New directory is "
							+ currentDirectoryPointer);
				fileList = randomFile ? getRandomFileList(currentDirectoryPointer)
						: getFileList(currentDirectoryPointer);
				currentFileIndex = (indexIncrement > 0) ? 0
						: (fileList.size() - 1);
			} else {
				currentFileIndex = nextIndex(currentFileIndex, indexIncrement,
						0, fileList.size());
			}
		}
		currentIndex = currentDirectoryPointer.index + currentFileIndex;
		String f = (String) fileList.get(currentFileIndex);
		return f;
	}

	private DirectoryPointer getDirectoryPointer(String f) {
		int dpi = directoryList.size() - 1;
		int i = indexOf(f);
		for (int j = 1; j < directoryList.size(); j++) {
			DirectoryPointer dp = (DirectoryPointer) directoryList.get(j);
			if (i < dp.index) {
				dpi = j - 1;
				break;
			}
		}
		return (DirectoryPointer) directoryList.get(dpi);
	}

	/**
	 * Check whether this tray has another file name left.
	 * 
	 * @return true if it does and false if not.
	 */
	public boolean hasNext() {
		return !stopFlag;
	}

	/** Print the content of this tray. */
	public void print() {
		System.out.println("Tray contains " + size() + " files.");
		for (int i = 0; i < size(); i++)
			System.out.println(i + ": " + (String) get(i));
	}
	class DirectoryPointer {
		/** The name of the directory. */
		String name;
		/**
		 * The index of the first file of this directory in the Tray.
		 */
		int index;

		public DirectoryPointer(String n, int i) {
			name = n;
			index = i;
		}

		public String toString() {
			return name + " [" + index + "]";
		}
	}
}
