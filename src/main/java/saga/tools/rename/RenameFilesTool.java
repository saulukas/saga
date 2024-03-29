//-------------------------------------------------------------------------//
//                                                                         //
//    PROJECT:      Enumerate Files                                        //
//    FILE:         RenameFilesTool.java                                   //
//    AUTHOR:       saulukas                                               //
//                                                                         //
//-------------------------------------------------------------------------//

package saga.tools.rename;

import java.io.*;
import java.util.*;
import saga.tools.Tool;
import static saga.util.SystemOut.print;
import static saga.util.SystemOut.println;

//-------------------------------------------------------------------------//
//                                                                         //
//    RenameFilesTool                                                      //
//    ===============                                                      //
//                                                                         //
//-------------------------------------------------------------------------//
public class RenameFilesTool extends Tool
{
    public static final String  TMP_FILE_PREFIX = "enum.files.tmp-";

    //---------------------------------------------------------------------
    public RenameFilesTool ()
    {
        super("rename-files", "Renames files by adding sequential numbers to their names");
    }

    //---------------------------------------------------------------------
    @Override
    public int run(String[] args)
    {
         if (args.length < 1) {
             println(name + " 1.0, (c) saga 2006");
             println("");
             println("    " + oneLineDescription);
             println("");
             println("Parameters:");
             println("");
             println("    [-descending] [-startNr n] prefix directory [accepted-extensions]");
             println("");
             return 0;
         }
         if (args.length < 2) {
             printInvalidArguments();
             return 2;
         }

         int      argIndex   = 0;
         boolean  ascending  = true;
         int      startIndex = 1;
         String   prefix     = "";
         String   directory  = "";

         if (args[argIndex].equals("-descending"))
         {
             argIndex += 1;
             ascending = false;
         }
         if (args.length < 2)
             printInvalidArguments();

         if (args[argIndex].equals("-startNr"))
         {
             argIndex     += 1;
             startIndex    = Integer.parseInt(args[argIndex]);
             argIndex     += 1;
             if (startIndex < 1)
                 startIndex = 1;
         }
         if (args.length < 2)
             printInvalidArguments();

         prefix    = args[argIndex++];
         directory = args[argIndex++];

         int extCount = (args.length - argIndex);
         if (extCount < 0)
             extCount = 0;
         String[]  validExtensions = new String [extCount];
         for (int i = 0;  i < extCount;  i++)
             validExtensions[i] = args[argIndex++].toUpperCase();

         File[] directoryContent = new File(directory).listFiles();
         if (directoryContent == null)
             directoryContent = new File [0];

         println("--------------------------------------------");

         TreeMap<String, File> fileMap = new TreeMap<String, File>();

         for (int i = 0;  i < directoryContent.length;   i++)
             if (directoryContent[i].isFile())
             {
                 String fileName = "" + directoryContent[i];
                 fileName = fileName.toUpperCase();
                 if (isExtensionValid(fileName, validExtensions))
                     fileMap.put(fileName, directoryContent[i]);
             }

         int  fileNr = startIndex;
         if (!ascending)
             fileNr = startIndex + fileMap.size() - 1;
         Iterator iterator = fileMap.keySet().iterator();

         File[] tmpFiles = new File [fileMap.size()];
         while (iterator.hasNext())
         {
             String oldName = (String) iterator.next();
             String newName =
                 oldName.substring(0, getDirectoryLength(oldName))
                 + TMP_FILE_PREFIX + prefix
                 + intToString(fileNr, 4)
                 + getFileNameExtension(oldName);
             int tmpFileIndex = (fileNr - startIndex);
             tmpFiles[tmpFileIndex] = new File(newName);
             File file = fileMap.get (oldName);
             if (!file.renameTo(tmpFiles[tmpFileIndex]))
                 println("\n    ERROR: " + newName + "  <-  " + oldName);
             else
                 print(" " + fileNr);
             fileNr += (ascending ? 1 : -1);
         }
         println("");
         println("--------------------------------------------");
         for (int i = 0;  i < tmpFiles.length;  i++)
         {
             fileNr  = startIndex + i;
             String oldName = tmpFiles[i].getName();
             String newName =
                 oldName.substring(0, getDirectoryLength(oldName))
                 + prefix
                 + intToString(fileNr, 4)
                 + getFileNameExtension(oldName);
             File file = fileMap.get (oldName);
             if (!tmpFiles[i].renameTo(new File(newName)))
                 println("\n    ERROR: " + newName + "  <-  " + oldName);
             else
                 print(" " + fileNr);
         }
         println("");
         println("--------------------------------------------");
         println("File count: " + fileMap.size());

         return 0;
    }

    //---------------------------------------------------------------------
    public static void printInvalidArguments() {
        println("ERROR: invalid command-line arguments");
    }

    //---------------------------------------------------------------------
    public static String intToString (int value, int width)
    {
        String result = "" + value;
        for (int i = result.length();  i < width;  i++)
            result = "0" + result;
        return result;
    }

    //---------------------------------------------------------------------
    public static int getDirectoryLength (String fileName)
    {
        if (fileName == null)
            return 0;
        int win  = fileName.lastIndexOf('\\');
        int unix = fileName.lastIndexOf('/');
        if (win  < 0)  win  = 0;
        if (unix < 0)  unix = 0;
        int result = (win > unix ? win : unix);
        if (fileName.charAt(result) == '\\'  ||  fileName.charAt(result) == '/')
            result++;
        return result;
    }

    //---------------------------------------------------------------------
    public static String getFileNameExtension (String fileName)
    {
        if (fileName == null)
            return "";
        int ext  = fileName.lastIndexOf('.');
        if (ext <= 0)
            return "";
        int win  = fileName.lastIndexOf('\\');
        int unix = fileName.lastIndexOf('/');
        if (win  < 0)  win  = 0;
        if (unix < 0)  unix = 0;
        if (ext <= win  ||  ext <= unix)
            return "";
        return fileName.substring(ext);
    }

    //---------------------------------------------------------------------
    public static boolean isExtensionValid (String fileName, String[] ext)
    {
        if (fileName == null)
            return false;
        if (ext == null  ||  ext.length <= 0)
            return true;
        fileName = fileName.toUpperCase();
        for (int i = 0;  i < ext.length;  i++)
            if (fileName.endsWith(ext[i]))
                return true;
        return false;
    }


} //....EnumerateFiles....//
//=========================================================================//
