//-------------------------------------------------------------------------//
//                                                                         //
//    FILE:         PrintJhatInstanceCountDiff.java                        //
//    AUTHOR:       saulukas                                               //
//                                                                         //
//    Reads two plain-text files, containing query result of JHAT          //
//    instance count query, and outputs difference to System.out.          //
//                                                                         //
//-------------------------------------------------------------------------//
package saga.jhat;

import java.io.*;
import java.util.*;
import saga.Tool;

//-------------------------------------------------------------------------//
//                                                                         //
//    PrintJhatInstanceCountDiff                                           //
//    ==========================                                           //
//                                                                         //
//-------------------------------------------------------------------------//
public class PrintJhatInstanceCountDiff extends Tool
{
    static final String[] MARKERS = new String []
    {
        " instance of class ",
        " instances of class ",
    };

    public static final String  NEW_CLASSES    = "C+";
    public static final String  LOST_CLASSES   = "C-";
    public static final String  NEW_OBJECTS    = "O+";
    public static final String  LOST_OBJECTS   = "O-";

    public static final String  SEPARATOR_LINE =
    "----------------------------------------------------------------------";

    //---------------------------------------------------------------------
    static class DiffCount
    {
        public String   type;
        public long     count;
        public String   className;
        public DiffCount (String t, long c, String n)
        {type = t;  count = c;  className = n;}
    }
    //---------------------------------------------------------------------
    public PrintJhatInstanceCountDiff() 
    {
        super("jhat-diff", "Finds diffs in two JHAT instance count outputs.");
    }
    //---------------------------------------------------------------------
    public void showUsage ()
    {
        System.out.println
        (
            name + " 1.01, (c) saga 2009"
        + "\n"
        + "\n    " + oneLineDescription
        + "\n"
        + "\nParameters:"
        + "\n"
        + "\n    html2txt-old.txt   html2txt-new.txt"
        + "\n"
        );
    }
    //---------------------------------------------------------------------
    @Override
    public int run(String[] args) throws Exception {
    
        if (args.length < 2)
        {
            showUsage();
            return 1;
        }

        System.out.println(SEPARATOR_LINE);

        String  fileNameOld = args[0];
        String  fileNameNew = args[1];

        System.out.println("Reading old file: " + fileNameOld);
        Map<String, Long> oldCounts = readObjectCounts(fileNameOld);
        System.out.println("    classes: " + oldCounts.size());
        System.out.println("    objects: " + getTotalObjectCount(oldCounts));

        System.out.println("Reading new file: " + fileNameNew);
        Map<String, Long> newCounts = readObjectCounts(fileNameNew);
        System.out.println("    classes: " + newCounts.size());
        System.out.println("    objects: " + getTotalObjectCount(newCounts));

        System.out.println("Different classes (objects):");
        List<DiffCount> diffCounts  = getDiffCounts (oldCounts   , newCounts);
        List<DiffCount> newClasses  = getListForType(NEW_CLASSES , diffCounts);
        List<DiffCount> lostClasses = getListForType(LOST_CLASSES, diffCounts);
        List<DiffCount> newObjects  = getListForType(NEW_OBJECTS , diffCounts);
        List<DiffCount> lostObjects = getListForType(LOST_OBJECTS, diffCounts);

        printSummary("    new  classes: " + NEW_CLASSES , newClasses);
        printSummary("    lost classes: " + LOST_CLASSES, lostClasses);
        printSummary("    new  objects: " + NEW_OBJECTS , newObjects);
        printSummary("    lost objects: " + LOST_OBJECTS, lostObjects);

        System.out.println(SEPARATOR_LINE);

        if (diffCounts.size() <= 0)
            return 0;

        printDiffCounts (newClasses);
        printDiffCounts (lostClasses);
        printDiffCounts (newObjects);
        printDiffCounts (lostObjects);

        System.out.println(SEPARATOR_LINE);
        System.out.println("Classes touched by differences: " + diffCounts.size());
        
        return 0;
    }
    //---------------------------------------------------------------------
    public static Map<String, Long> readObjectCounts (String fileName)
        throws IOException
    {
        Map<String, Long> objCountMap = new TreeMap<String, Long>();
        BufferedReader file = new BufferedReader(new FileReader(fileName));
        for (;;)
        {
            String line = file.readLine();
            if (line == null)
                break;
            int markerPos = -1;
            int namePos   = -1;
            for (int i = 0;  i < MARKERS.length;  i++)
            {
                markerPos = line.indexOf(MARKERS[i]);
                if (markerPos > 0)
                {
                    namePos = markerPos + MARKERS[i].length();
                    break;
                }
            }
            if (markerPos <= 0)
                continue;
            String  countStr  = line.substring(0, markerPos).trim();
            long    count     = Long.parseLong(countStr);
            String  className = line.substring(namePos).trim();
            if (count > 0)
                objCountMap.put(className, new Long(count));
        }
        file.close();
        return objCountMap;
    }
    //---------------------------------------------------------------------
    public static long getTotalObjectCount (Map<String, Long> objCountMap)
    {
        long totalCount = 0;
        for (Long count : objCountMap.values())
            totalCount += count;
        return totalCount;
    }
    //---------------------------------------------------------------------
    static List<DiffCount> getDiffCounts (Map<String, Long> oldCounts,
                                          Map<String, Long> newCounts)
    {
        Map<String, Long> leftCounts  = new TreeMap<String, Long>(oldCounts);
        List<DiffCount>  diffCounts = new LinkedList<DiffCount>();

        for (String className : newCounts.keySet())
        {
            long newCount = newCounts.get(className);
            if (!leftCounts.containsKey(className))
            {
                diffCounts.add(new DiffCount(
                    NEW_CLASSES, newCount, className));
            }
            else
            {
                long oldCount  = oldCounts.get(className);
                long countDiff = newCount - oldCount;
                if (countDiff != 0)
                {
                    if (countDiff > 0)
                        diffCounts.add(new DiffCount(
                            NEW_OBJECTS,   countDiff, className));
                    else
                        diffCounts.add(new DiffCount(
                            LOST_OBJECTS, -countDiff, className));
                }
            }
            leftCounts.remove(className);
        }

        for (String className : leftCounts.keySet())
            diffCounts.add(new DiffCount(
                LOST_CLASSES, leftCounts.get(className), className));

        return diffCounts;
    }
    //---------------------------------------------------------------------
    static List<DiffCount> getListForType (String type, List<DiffCount> counts)
    {
        List<DiffCount> set = new LinkedList<DiffCount>();
        for (DiffCount toc : counts)
            if (type.equals(toc.type))
                set.add(toc);
        return set;
    }
    //---------------------------------------------------------------------
    static long getTotalObjectCount (Collection<DiffCount> counts)
    {
        long totalCount = 0;
        for (DiffCount toc : counts)
            totalCount += toc.count;
        return totalCount;
    }
    //---------------------------------------------------------------------
    static void printSummary (String prefix, Collection<DiffCount> counts)
    {
        System.out.print(prefix + " =\t");
        if (counts.size() <= 0)
            System.out.println("-");
        else
            System.out.println(
                counts.size() + "\t("  + getTotalObjectCount(counts) + ")");
    }
    //---------------------------------------------------------------------
    public static void printDiffCounts (List<DiffCount> counts)
    {
        DiffCount[] sorted = counts.toArray(new DiffCount[0]);
        Arrays.sort(sorted, new Comparator<DiffCount>()
        {
            public int compare (DiffCount a, DiffCount b)
            {
                int sign = (int)(a.count - b.count);
                if (sign != 0)
                   return -sign;
                sign = a.className.compareTo(b.className);
                return sign;
            }
        });
        for (DiffCount d : sorted)
            System.out.println(d.type + "\t" + d.count + "\t" + d.className);
    }
}
//=========================================================================//
