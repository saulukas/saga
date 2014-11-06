//-------------------------------------------------------------------------//
//                                                                         //
//    PROJECT:      Line count                                             //
//    FILE:         FileTypeCount.java                                     //
//    AUTHOR:       saulukas                                               //
//                                                                         //
//-------------------------------------------------------------------------//

package saga.linecount;

import java.io.*;
import java.util.*;

//-------------------------------------------------------------------------//
//                                                                         //
//    FileTypeCount                                                        //
//    =============                                                        //
//                                                                         //
//-------------------------------------------------------------------------//
public class FileTypeCount
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
        public int byteCount = 0;
	//-----------------------------------------------------------------
        public void add(Statistics s)
        {
            fileCount += s.fileCount;
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
                "FileTypeCount 1.02, (c) saga 2008"
              + "\n"
              + "\nParameters:"
              + "\n"
              + "\n    start-dir [-ignoreFileName ...]"
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
        
	TreeSet<String> ignore = new TreeSet<String>();
	for (int i = argIndex;  i < args.length;  i++)
	    if (args[i].startsWith("-ignore"))
	    {
	        String fileName = args[i].substring("-ignore".length());
		if (fileName.length() > 0)
		    ignore.add(fileName.toLowerCase());
	    }
	
        TreeMap<String, Statistics> map = 
	    new TreeMap<String, Statistics>();
	    
        String startDirName = startDir.getCanonicalPath();
        System.out.println(".");
        System.out.println(".   startDir : " + startDirName);
        
	DirStats dirStats = new DirStats();
        countDirectory(startDir, startDirName, map, dirStats, ignore);
	int fileTypeCount = map.size();

        System.out.println(".   dirCount : " + dirStats.dirCount);
        System.out.println(".   fileCount: " + dirStats.fileCount);
        System.out.println(".   byteCount: " + dirStats.byteCount);
        System.out.println(".   fileTypes: " + fileTypeCount);
        System.out.println(".");
	
	if (map.size() > 0)
            printStatistics(map);
    }
    //---------------------------------------------------------------------
    public static void countDirectory 
    (
        File                     directory,
        String                   startDirName,
        Map<String, Statistics>  map,
	DirStats                 dirStats,
	Set<String>              ignore
    )
        throws IOException
    {
        File[] files = directory.listFiles();
        for (File file : files)
	{
	    String fileName = file.getName().toLowerCase();
	    if (ignore.contains(fileName))
	        continue;
            if (file.isDirectory())
            {
	        dirStats.dirCount += 1;
                countDirectory(file, startDirName, map, dirStats, ignore);
            }
            else 
            {
	        dirStats.fileCount += 1;
	        dirStats.byteCount += file.length();
	        String fileType = getFileType(file).toLowerCase();
		if (!map.containsKey(fileType))
		     map.put(fileType, new Statistics());
		Statistics stats = map.get(fileType);
		stats.fileCount += 1;
		stats.byteCount += file.length();
            }    
	}
    }
    //---------------------------------------------------------------------
    public static String getFileType (File file)
    {
        String name      = file.getName();
	int    typeIndex = name.lastIndexOf('.');
	String fileType  = "";
	if (typeIndex >= 0)
	    fileType = name.substring(typeIndex);
	return fileType;
    }
    //---------------------------------------------------------------------
    public static void printStatistics(TreeMap<String, Statistics>  map)
    {
	String[][] table = new String [1 + map.size()][3];
	table[0][0] = "fileType";
	table[0][1] = "files";
	table[0][2] = "bytes";
	
        String[] fileTypes = map.keySet().toArray(new String[map.size()]);
	for (int row = 0;  row < fileTypes.length;  row++)
	{
	    Statistics stats = map.get(fileTypes[row]);
	    table[row+1][0] = fileTypes[row];
	    table[row+1][1] = add1000seps("" + stats.fileCount);
	    table[row+1][2] = add1000seps("" + stats.byteCount);
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
