//-------------------------------------------------------------------------//
//                                                                         //
//    PROJECT:      CVS log command output formatting                      //
//    FILE:         CVSLogItem.java                                        //
//    AUTHOR:       saulukas                                               //
//                                                                         //
//-------------------------------------------------------------------------//
package saga.tools.cvslog;

//-------------------------------------------------------------------------//
//                                                                         //
//    CVSLogItem                                                           //
//    ==========                                                           //
//                                                                         //
//-------------------------------------------------------------------------//
public class CVSLogItem implements Comparable<CVSLogItem>
{
    protected boolean  isTag;
    protected String   fileName;
    protected String   totalRevisions;
    protected String   revision;
    protected String   dateTime;
    protected String   author;
    protected String   lines;
    protected String   comment;

    //---------------------------------------------------------------------
    public CVSLogItem
    (
        String   fileName,
        String   totalRevisions,
        String   revision,
        String   dateTime,
        String   author,
        String   lines,
        String   comment
    )
    {
        this.isTag    = false;
        this.fileName = fileName;
        this.totalRevisions = totalRevisions;
        this.revision = revision;
        this.dateTime = dateTime;
        this.author = author;
        this.lines = lines;
        this.comment = comment;
    } 
    //---------------------------------------------------------------------
    public CVSLogItem
    (
        String   tagName,
        String   dateTime
    )
    {
        this.isTag    = true;
        this.comment  = tagName;
        this.dateTime = dateTime;
    } 
    //---------------------------------------------------------------------
    public boolean  getIsTag          () {return isTag;}
    public String   getFileName       () {return fileName;}
    public String   getTotalRevisions () {return totalRevisions;}
    public String   getRevision       () {return revision;}
    public String   getDateTime       () {return dateTime;}
    public String   getAuthor         () {return author;}
    public String   getLines          () {return lines;}
    public String   getComment        () {return comment;}
    //---------------------------------------------------------------------
    public void updateDateTime (String newDateTime)
    {
        if (dateTime == null  ||  newDateTime.compareTo(dateTime) > 0)
            dateTime = newDateTime;
    }
    //---------------------------------------------------------------------
    public void incrementTotalRevisions ()
    {
        int   count = 0;
        try { count = Integer.parseInt(totalRevisions); }
        catch (Throwable t) {}
        count          += 1;
        totalRevisions  = "" + count;
    }
    //---------------------------------------------------------------------//
    //                                                                     //
    //    Comparable                                                       //
    //    ----------                                                       //
    //                                                                     //
    //---------------------------------------------------------------------//
    public int compareTo (CVSLogItem b)
    {
        if (this == b) return 0;
        if (null == b) return 1;
        int sign = 0;
        sign = dateTime.compareTo(b.getDateTime());
        if (sign != 0) return sign;
        if (isTag != b.getIsTag())
            return isTag ? 1 : -1;
        if (isTag)
            return comment.compareTo(b.getComment());
        sign = author.compareTo(b.getAuthor());
        if (sign != 0) return sign;
        sign = comment.compareTo(b.getComment());
        return sign;
    }
    //---------------------------------------------------------------------//
    //                                                                     //
    //    Object                                                           //
    //    ------                                                           //
    //                                                                     //
    //---------------------------------------------------------------------//
    public String toString ()
    {
        return "[CVSLogItem: "
            + " isTag=" + isTag
            + " fileName=" + fileName
            + " totalRevisions=" + totalRevisions
            + " revision=" + revision
            + " dateTime=" + dateTime
            + " author=" + author
            + " lines=" + lines
            + " comment=" + comment
            + "]";
    }
    //---------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (o instanceof CVSLogItem)
            return compareTo((CVSLogItem) o) == 0;
        return false;
    }
}
//=========================================================================//
