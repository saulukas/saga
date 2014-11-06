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

//-------------------------------------------------------------------------//
//                                                                         //
//    LineCount                                                            //
//    =========                                                            //
//                                                                         //
//-------------------------------------------------------------------------//
public class LineCount
{
    public static final String SPACE_CHARS = " \t\n\r\f";

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
    public static void main (String[] args)
    {
        try
        {
            mainX(args);
        }
        catch (Throwable t)
        {
            System.out.println(".");
            System.out.println(".   ERROR: " + t);
            System.out.println(".");
            t.printStackTrace();
        }
    }
    //---------------------------------------------------------------------
    public static void mainX (String[] args) throws Exception
    {
        if (args.length < 1)
        {
            System.out.println
	    (
                "LineCount 1.16 (c) saga 2009"
              + "\n"
              + "\nParameters:"
              + "\n"
              + "\n    start-dir file-name-end ..."
	      + "\n"
            );
            System.exit(1);
        }

        int  argIndex = 0;
        File startDir = new File(args[argIndex++]);

        if (!startDir.isDirectory())
        {
            System.out.println("Directory not found: " + startDir);
            System.exit(2);
        }

        TreeMap<String, Statistics> map =
	    new TreeMap<String, Statistics>();

        for (;  argIndex < args.length;  argIndex++)
            map.put(args[argIndex].toLowerCase(), new Statistics());

        String startDirName = startDir.getCanonicalPath();
        System.out.println(".");
        System.out.println(".   startDir : " + startDirName);

	DirStats dirStats = new DirStats();
        countDirectory(startDir, startDirName, map, dirStats);

	if (map.size() > 1)
	{
            Statistics total = new Statistics ();
	    for (Statistics statistics : map.values())
	        total.add(statistics);
	    map.put("", total);
	}

        System.out.println(".   dirCount : " + dirStats.dirCount);
        System.out.println(".   fileCount: " + dirStats.fileCount);
        System.out.println(".   byteCount: " + dirStats.byteCount);
        System.out.println(".");

	if (map.size() > 0)
            printStatistics(map);
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
	        String fileType = null;
                for (int i = 0;  i < fileTypes.length;  i++)
                    if (name.endsWith(fileTypes[i]))
		    {
		        fileType = fileTypes[i];
			break;
	            }
		if (fileType != null)
                    countFile(file, startDirName, map.get(fileType));
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
        System.out.println
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
    //---------------------------------------------------------------------
    public static int[] getColWidths (String[][] table)
    {
        int[] colWidths = new int[ table[0].length ];
        for (int col = 0;  col < colWidths.length;  col++)
	    colWidths[col] = 0;
        for (int col = 0;  col < colWidths.length;  col++)
	{
	    int maxWidth = colWidths[col];
	    for (int row = 0;  row < table.length;  row++)
	        if (maxWidth < table[row][col].length())
		    maxWidth = table[row][col].length();
	    colWidths[col] = maxWidth;
	}
	return colWidths;
    }
    //---------------------------------------------------------------------
    public static void printSeparatorLine(int[] colWidths)
    {
	for (int col = 0;  col < colWidths.length;  col++)
            System.out.print("+" + fillChar('-', colWidths[col] + 4));
	System.out.println("+");
    }
    //---------------------------------------------------------------------
    public static void printRow(int[] colWidths, String[] values)
    {
	for (int col = 0;  col < colWidths.length;  col++)
            System.out.print(
	        "|"
	      + (col > 0 ? alignRight("  "+values[col]+"  ", colWidths[col]+4)
	                 : alignLeft ("  "+values[col]+"  ", colWidths[col]+4)
	      ));
	System.out.println("|");
    }
    //---------------------------------------------------------------------
    public static String fillChar (char c, int count)
    {
        String result = "";
	for (int i = 0;  i < count;  i++)
	    result += c;
	return result;
    }
    //---------------------------------------------------------------------
    public static String alignRight (String s, int width)
    {
        String result = "" + s;
	for (int i = result.length();  i < width;  i++)
	    result = " " + result;
	return result;
    }
    //---------------------------------------------------------------------
    public static String alignLeft (String s, int width)
    {
        String result = "" + s;
	for (int i = result.length();  i < width;  i++)
	    result = result + " ";
	return result;
    }
    //---------------------------------------------------------------------
    public static String add1000seps (Object number)
    {
        if (number == null)
            return "";
        String str    = number.toString();
        String result = "";
        for (int i = 0;  i < str.length();  i++)
        {
            if (i > 0  &&  i % 3 == 0)
                result = "," + result;
            result = str.charAt(str.length()-1-i) + result;
        }
        return result;
    }
}
//=========================================================================//
