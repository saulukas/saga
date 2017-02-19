//-------------------------------------------------------------------------//
//                                                                         //
//    PROJECT:      CVS log command output formatting                      //
//    FILE:         CVSLogFormatterTool.java                               //
//    AUTHOR:       saulukas                                               //
//                                                                         //
//-------------------------------------------------------------------------//
package saga.cvslog;

import java.io.*;
import java.util.*;
import saga.Tool;

import saga.util.Version;

//-------------------------------------------------------------------------//
//                                                                         //
//    CVSLogFormatterTool                                                  //
//    ===================                                                  //
//                                                                         //
//-------------------------------------------------------------------------//
public class CVSLogFormatterTool extends Tool
{
    public static final Version VERSION = new Version (1, 2, 0);
    //---------------------------------------------------------------------
    // 1.2    no CVS tag ...
    //        Added Version class.
    //---------------------------------------------------------------------

    public static final int MIN_LINE_WIDTH     = 39;
    public static final int DEFAULT_LINE_WIDTH = 79;

    @SuppressWarnings("serial")
    //---------------------------------------------------------------------//
    //                                                                     //
    //    LineStartnotFoundException                                       //
    //    ==========================                                       //
    //                                                                     //
    //---------------------------------------------------------------------//
    public static class LineStartNotFoundException extends Exception
    {
        LineStartNotFoundException(String msg) {super(msg);}
    }
    //---------------------------------------------------------------------//
    public CVSLogFormatterTool()
    {
        super("format-cvs-log", "Reads 'cvs log' output from stdin and writes formatted to stdout");
    }
    //---------------------------------------------------------------------//
    @Override
    public int run(String[] args) throws Exception {
        PrintStream      writer = System.out;

        int lineWidth = DEFAULT_LINE_WIDTH;

        if (args.length < 1)
        {
            writer.println(name + VERSION + " (c) saga 2009");
            writer.println("");
            writer.println("    " + oneLineDescription);
            writer.println("");
            writer.println("Usage:");
            writer.println("");
            writer.println("   CVSLogFormatter  lineWidth    - minLineWidth=" + MIN_LINE_WIDTH);
            writer.println("");
            return 0;
        }

        lineWidth = Integer.parseInt(args[0]);
        if (lineWidth < MIN_LINE_WIDTH)
            lineWidth = MIN_LINE_WIDTH;

        LineNumberReader reader =
            new LineNumberReader(new InputStreamReader(System.in));

        TreeMap<String, CVSLogItem> tagMap = new TreeMap<String, CVSLogItem>();

        CVSLogItem[] items = readItems(reader, tagMap);

        Arrays.sort(items);
/*
        writer.println("Item count = " + items.length);
        for (int i = 0;  i < items.length;  i++)
        {
            writer.println(i + ": " + items[i]);
        }
*/
        CVSLogItem prev = null;
        for (int i = 0;  i < items.length;  i++)
        {
            CVSLogItem item = items[i];
            if (item.getIsTag())
            {
                printTagItem(writer, item, lineWidth);
            }
            else
            {
                if (item.compareTo(prev) != 0)
                    printItemHeader(writer, item, lineWidth);
                printItemFile(writer, item, lineWidth);
                CVSLogItem next = (i+1 < items.length ? items[i+1] : null);
                if (item.compareTo(next) != 0)
                    printItemFooter(writer, item, lineWidth);
            }
            prev = item;
        }

        return 0;
    }
    //---------------------------------------------------------------------
    public static void printTagItem
    (
        PrintStream  writer,
        CVSLogItem   item,
        int          lineWidth
    )
    {
        writer.println();
        writer.println();
        String line = "T==" + item.getDateTime() + "===" + item.getComment()
                   + "===[" + item.getTotalRevisions()   + "]===";
        for (int i = line.length();  i < lineWidth;  i++)
            line += "=";
        writer.println(line);
    }
    //---------------------------------------------------------------------
    public static void printItemHeader
    (
        PrintStream  writer,
        CVSLogItem   item,
        int          lineWidth
    )
    {
        writer.println();
        writer.println();
        String line    = "+--" + item.getDateTime() + "--" + item.getAuthor();
        String lineEnd = "--+";
        for (int i = line.length();  i < lineWidth-lineEnd.length();  i++)
            line += "-";
        line += lineEnd;
        writer.println(line);
        String comment = item.getComment();
        String end = "  |";
        while (comment.length() > 0)
        {
            int n = comment.indexOf("\n");
            line = "|  ";
            if (n < 0)
            {
                line   += comment;
                comment = "";
            }
            else
            {
                line   += comment.substring(0, n);
                comment = comment.substring(n+1);
            }
            for (int i = line.length();  i < lineWidth-end.length();  i++)
                line += " ";
            line += end;
            writer.println(line);
        }
        printSeparator(writer, lineWidth);
    }
    //---------------------------------------------------------------------
    public static void printItemFile
    (
        PrintStream  writer,
        CVSLogItem   item,
        int          lineWidth
    )
    {
        String revisions = item.getRevision() + "/" + item.getTotalRevisions();
        String line      = "|  " + item.getFileName();
        String end       = " " + item.getLines() + " " + revisions + "  |";
        for (int i = line.length();  i < lineWidth-end.length();  i++)
             line += " ";
        line += end;
        writer.println(line);
    }
    //---------------------------------------------------------------------
    public static void printItemFooter
    (
        PrintStream  writer,
        CVSLogItem   item,
        int          lineWidth
    )
    {
        printSeparator(writer, lineWidth);
    }
    //---------------------------------------------------------------------
    public static void printSeparator (PrintStream  writer, int lineWidth)
    {
        writer.print('+');
        for (int i = 0;  i < lineWidth-2;  i++)
            writer.print('-');
        writer.print('+');
        writer.println();
    }
    //---------------------------------------------------------------------
    public static CVSLogItem[] readItems (LineNumberReader             reader,
                                          TreeMap<String, CVSLogItem>  tagMap)
        throws Exception
    {
        final String RCS_FILE = "RCS file: ";

        LinkedList<CVSLogItem> items = new LinkedList<CVSLogItem>();

        for (;;)
        {
            try
            {
                findLineStart(reader, RCS_FILE);
            }
            catch (LineStartNotFoundException ex)
            {
                items.addAll(tagMap.values());
                return items.toArray(new CVSLogItem [items.size()]);
            }
            readFileItems(reader, items, tagMap);
        }
    }
    //---------------------------------------------------------------------
    public static void readFileItems (LineNumberReader             reader,
                                      LinkedList<CVSLogItem>       fileItems,
                                      TreeMap<String, CVSLogItem>  tagMap)
        throws Exception
    {
        final String WORKING_FILE    = "Working file: ";
        final String SYMBOLIC_NAMES  = "symbolic names:";
        final String TOTAL_REVISIONS = "total revisions: ";
        final String REVISION        = "revision ";
        final String DATE_TIME       = "date: ";
        final String AUTHOR          = "author: ";
        final String LINES           = "lines: ";
        final String COMMENT_END     = "----------------------------";
        final String FILE_END        = "=============================================================================";

        TreeMap<String, LinkedList<String>> revTags   = null;

        String   fileName       = null;
        String   totalRevisions = null;
        String   revision       = null;
        String   dateTime       = null;
        String   author         = null;
        String   lines          = null;
        String   comment        = "";

        String   line  = null;

        fileName = findLineStart(reader, WORKING_FILE);

        line = findLineStart(reader, SYMBOLIC_NAMES);
        line = reader.readLine();
        while (line.startsWith("\t"))
        {
            if (revTags == null)
                revTags = new TreeMap<String, LinkedList<String>>();
            line = line.substring(1);
            String tag = getBeforeToken(line, ": ");
            String rev = getAfterToken (line, ": ");
            if (!revTags.containsKey(rev))
                 revTags.put(rev, new LinkedList<String>());
            revTags.get(rev).add(tag);
            line = reader.readLine();
        }

        line           = findLineStart(reader, TOTAL_REVISIONS);
        if (line.endsWith("selected revisions: 0"))
            return;
        totalRevisions = getBeforeToken(line, ";");

        do
        {
            revision       = findLineStart(reader, REVISION);
            line           = findLineStart(reader, DATE_TIME);
            dateTime       = getBeforeToken(line, ";");
            line           = getAfterToken (line, AUTHOR);
            author         = getBeforeToken(line, ";");
            lines          = getAfterToken (line, LINES, "");
            lines          = getBeforeToken(lines, ";");

            comment = "";
            line = reader.readLine();
            while (!line.startsWith(COMMENT_END) && !line.startsWith(FILE_END))
            {
                comment += (comment.length() > 0 ? "\n" : "") + line;
                line = reader.readLine();
            }
            if (revTags != null  &&  revTags.containsKey(revision))
            {
                for (String tag : revTags.get(revision))
                {
                    CVSLogItem tagItem = tagMap.get(tag);
                    if (tagItem == null)
                        tagMap.put(tag, new CVSLogItem(tag, dateTime));
                    tagMap.get(tag).updateDateTime(dateTime);
                    tagMap.get(tag).incrementTotalRevisions();
                }
            }
            fileItems.add(new CVSLogItem
            (
                fileName,
                totalRevisions,
                revision,
                dateTime,
                author,
                lines,
                comment
            ));
        }
        while (!line.startsWith(FILE_END));
    }
    //---------------------------------------------------------------------
    public static String findLineStart (LineNumberReader reader,
                                        String           lineStart)
        throws Exception
    {
        String line = reader.readLine();
        while (line != null)
        {
            if (line.startsWith(lineStart))
                return line.substring(lineStart.length());
            line = reader.readLine();
        }
        throw new LineStartNotFoundException("" + lineStart);
    }
    //---------------------------------------------------------------------
    public static String getBeforeToken (String line, String token)
       throws LineStartNotFoundException
    {
        int i = line.indexOf(token);
        if (i < 0)
            return line;
        return line.substring(0, i);
    }
    //---------------------------------------------------------------------
    public static String getAfterToken (String line, String token)
       throws LineStartNotFoundException
    {
        return getAfterToken(line, token, null);
    }
    //---------------------------------------------------------------------
    public static String getAfterToken (String line, String token, String def)
       throws LineStartNotFoundException
    {
        int i = line.indexOf(token);
        if (i < 0)
        {
            if (def != null)
                return def;
            throw new LineStartNotFoundException(token + ": " + line);
        }
        return line.substring(i + token.length());
    }
}
//=========================================================================//
