//-------------------------------------------------------------------------//
//                                                                         //
//    PROJECT:      Line count                                             //
//    FILE:         ListFileTypesTool.java                                 //
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
import static saga.util.ExceptionUtils.ex;

//-------------------------------------------------------------------------//
//                                                                         //
//    ListFileTypesTool                                                    //
//    =================                                                    //
//                                                                         //
//-------------------------------------------------------------------------//
public class ListFileTypesTool extends Tool
{
    public static final String SPACE_CHARS = " \t\n\r\f";

    //---------------------------------------------------------------------
    public ListFileTypesTool()
    {
        super("list-file-types",
            "Finds different file name extensions/types and counts them");
    }
    //---------------------------------------------------------------------
    @Override
    public int run(String[] args) {
        if (args.length < 1)
        {
            println(name + " 1.02, (c) saga 2008");
            println("");
            println("    " + oneLineDescription);
            println("");
            println("Parameters:");
            println("");
            println("    start-dir [-ignoreFileName ...]");
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

	TreeSet<String> ignore = new TreeSet<>();
	for (int i = argIndex;  i < args.length;  i++)
	    if (args[i].startsWith("-ignore"))
	    {
	        String fileName = args[i].substring("-ignore".length());
		if (fileName.length() > 0)
		    ignore.add(fileName.toLowerCase());
	    }

        TreeMap<String, Statistics> map = new TreeMap<>();

        String startDirName = ex(startDir::getCanonicalPath);
        println(".");
        println(".   startDir : " + startDirName);

	DirStats dirStats = new DirStats();
        ex(() -> {countDirectory(startDir, startDirName, map, dirStats, ignore);});
	int fileTypeCount = map.size();

        println(".   dirCount : " + dirStats.dirCount);
        println(".   fileCount: " + dirStats.fileCount);
        println(".   byteCount: " + dirStats.byteCount);
        println(".   fileTypes: " + fileTypeCount);
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
        public int byteCount = 0;
	//-----------------------------------------------------------------
        public void add(Statistics s)
        {
            fileCount += s.fileCount;
            byteCount += s.byteCount;
        }
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
}
//=========================================================================//
