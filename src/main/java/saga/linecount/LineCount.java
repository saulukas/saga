//-------------------------------------------------------------------------//
//                                                                         //
//    PROJECT:      Line count                                             //
//    FILE:         LineCount.java                                         //
//    AUTHOR:       saulukas                                               //
//                                                                         //
//-------------------------------------------------------------------------//

package saga.linecount;

import java.io.*;
import java.util.*;
import saga.Tool;
import static saga.util.StringTable.getColWidths;
import static saga.util.StringTable.printRow;
import static saga.util.StringTable.printSeparatorLine;
import static saga.util.SystemOut.println;
import static saga.util.TextUtils.add1000seps;

//-------------------------------------------------------------------------//
//                                                                         //
//    LineCount                                                            //
//    =========                                                            //
//                                                                         //
//-------------------------------------------------------------------------//
public class LineCount extends Tool
{
    public static final String SPACE_CHARS = " \t\n\r\f";

    //---------------------------------------------------------------------
    public LineCount()
    {
        super("line-count",
                "Counts new lines in files with given file name ends.");
    }
    //---------------------------------------------------------------------
    @Override
    public int run(String[] args) throws Exception
    {
        if (args.length < 2)
        {
            println(name + " (c) saga 2009");
            println("");
            println("    " + oneLineDescription);
            println("");
            println("Parameters:");
            println("");
            println("    start-dir file-name-end ...");
            println("");
            return 0;
        }

        int  argIndex = 0;
        File startDir = new File(args[argIndex++]);

        if (!startDir.isDirectory())
        {
            println("Directory not found: " + startDir);
            return 2;
        }

        TreeMap<String, Statistics> map = new TreeMap<>();

        for (;  argIndex < args.length;  argIndex++)
            map.put(args[argIndex].toLowerCase(), new Statistics());

        String startDirName = startDir.getCanonicalPath();
        println(".");
        println(".   startDir : " + startDirName);

	DirStats dirStats = new DirStats();
        countDirectory(startDir, startDirName, map, dirStats);

	if (map.size() > 1)
	{
            Statistics total = new Statistics ();
	    for (Statistics statistics : map.values())
	        total.add(statistics);
	    map.put("", total);
	}

        println(".   dirCount : " + dirStats.dirCount);
        println(".   fileCount: " + dirStats.fileCount);
        println(".   byteCount: " + dirStats.byteCount);
        println(".");

	if (map.size() > 0)
            printStatistics(map);

        return 0;
    }
    //---------------------------------------------------------------------
    public static class DirStats
    {
        public int dirCount  = 0;
        public int fileCount = 0;
        public int byteCount = 0;
    }
    //---------------------------------------------------------------------
    public static class Statistics
    {
        public int fileCount = 0;
        public int lineCount = 0;
        public int wordCount = 0;
        public int byteCount = 0;
	//-----------------------------------------------------------------
        public void add(Statistics s)
        {
            fileCount += s.fileCount;
            lineCount += s.lineCount;
            wordCount += s.wordCount;
            byteCount += s.byteCount;
        }
    }
    //---------------------------------------------------------------------
    public static void mainX (String[] args) throws Exception
    {
    }
    //---------------------------------------------------------------------
    public static void countDirectory
    (
        File                         directory,
        String                       startDirName,
        TreeMap<String, Statistics>  map,
	DirStats                     dirStats
    )
        throws IOException
    {
        String[] fileTypes = map.keySet().toArray(new String[map.size()]);
        File  [] files     = directory.listFiles();
        for (File file : files)
            if (file.isDirectory())
            {
	        dirStats.dirCount += 1;
                countDirectory(file, startDirName, map, dirStats);
            }
            else
            {
	        dirStats.fileCount += 1;
		dirStats.byteCount += file.length();
	        String name     = file.getName().toLowerCase();
                for (int i = 0;  i < fileTypes.length;  i++)
                    if (name.endsWith(fileTypes[i]))
		    {
		        countFile(file, startDirName, map.get(fileTypes[i]));
	            }
            }
    }
    //---------------------------------------------------------------------
    public static void countFile
    (
        File        file,
        String      startDirName,
	Statistics  statistics
    )
        throws IOException
    {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        int      lineCount   = 0;
        int      wordCount   = 0;
        int      byteCount   = 0;
        boolean  isInWord    = false;
	boolean  wasNewLine  = true;
        int      symbol      = in.read();
        while (symbol != -1)
        {
            byteCount += 1;
            if (wasNewLine)
                lineCount += 1;
            boolean isSpace = (SPACE_CHARS.indexOf(symbol) >= 0);
            if (!isSpace   &&  !isInWord)
                wordCount += 1;
            isInWord   = !isSpace;
	    wasNewLine = (symbol == '\n');
            symbol     = in.read();
	}
        if (isInWord)
            wordCount += 1;
        in.close();
        statistics.fileCount += 1;
        statistics.lineCount += lineCount;
        statistics.wordCount += wordCount;
        statistics.byteCount += byteCount;
/*
        println
	(
            statistics.fileCount + ":"
            + "\t" + lineCount
            + "\t" + wordCount
            + "\t" + byteCount
            + "\t" + file.getCanonicalPath().substring(
	        startDirName.length() + 1)
        );
*/
    }
    //---------------------------------------------------------------------
    public static void printStatistics(TreeMap<String, Statistics>  map)
    {
	String[][] table = new String [1 + map.size()][5];
	table[0][0] = "";
	table[0][1] = "files";
	table[0][2] = "lines";
	table[0][3] = "words";
	table[0][4] = "bytes";

        String[] fileTypes = map.keySet().toArray(new String[map.size()]);
	for (int row = 0;  row < fileTypes.length;  row++)
	{
	    Statistics stats = map.get(fileTypes[row]);
	    table[row+1][0] = fileTypes[row];
	    table[row+1][1] = add1000seps("" + stats.fileCount);
	    table[row+1][2] = add1000seps("" + stats.lineCount);
	    table[row+1][3] = add1000seps("" + stats.wordCount);
	    table[row+1][4] = add1000seps("" + stats.byteCount);
	}

	int[] colWidths = getColWidths(table);
	printSeparatorLine(colWidths);
	printRow          (colWidths, table[0]);
	printSeparatorLine(colWidths);
	for (int row = 1;  row < table.length;  row++)
	    printRow      (colWidths, table[row]);
	printSeparatorLine(colWidths);
    }
}
//=========================================================================//
