//-------------------------------------------------------------------------//
//                                                                         //
//    FILE:         Version.java                                           //
//    AUTHOR:       saulukas                                               //
//                                                                         //
//-------------------------------------------------------------------------//
package saga.util;

//-------------------------------------------------------------------------//
//                                                                         //
//    Version                                                              //
//    =======                                                              //
//                                                                         //
//-------------------------------------------------------------------------//
public class Version implements Comparable<Version>
{
    protected int  majorVersion   = 0; // compatibility
    protected int  releaseVersion = 0; // release
    protected int  featureVersion = 0; // some features added
    protected int  branchVersion  = 0; // not zero if changing older releases
    // only one branch is allowed from any given major.release.feature
    //---------------------------------------------------------------------
    public Version ()
    {
        this (0, 0, 0, 0);
    }
    //---------------------------------------------------------------------
    public Version (int majorVersion)
    {
        this (majorVersion, 0, 0, 0);
    }
    //---------------------------------------------------------------------
    public Version (int majorVersion, int releaseVersion)
    {
        this (majorVersion, releaseVersion, 0, 0);
    }
    //---------------------------------------------------------------------
    public Version (int majorVersion, int releaseVersion, int featureVersion)
    {
        this (majorVersion, releaseVersion, featureVersion, 0);
    }
    //---------------------------------------------------------------------
    public Version (int majorVersion, 
                    int releaseVersion, 
                    int featureVersion, 
                    int branchVersion)
    {
        this.majorVersion   = majorVersion;
        this.releaseVersion = releaseVersion;
        this.featureVersion = featureVersion;
        this.branchVersion  = branchVersion;
    }
    //---------------------------------------------------------------------
    public int  getMajorVersion   () {return majorVersion;}
    public int  getReleaseVersion () {return releaseVersion;}
    public int  getFeatureVersion () {return featureVersion;}
    public int  getBranchVersion  () {return branchVersion;}
    //---------------------------------------------------------------------//
    //                                                                     //
    //    Object                                                           //
    //    ------                                                           //
    //                                                                     //
    //---------------------------------------------------------------------//
    public String toString ()
    {
        String result = majorVersion + "." + releaseVersion;
        if (featureVersion > 0  ||  branchVersion > 0)
            result += "." + featureVersion;
        if (branchVersion > 0)
            result += "." + branchVersion;
        return result;    
    }
    //---------------------------------------------------------------------
    public boolean equals (Object obj)
    {
        if (!(obj instanceof Version))  
            return false;
        Version version = (Version) obj;
        return compareTo((Version)obj) == 0;
    }
    //---------------------------------------------------------------------//
    //                                                                     //
    //    Comparable                                                       //
    //    ----------                                                       //
    //                                                                     //
    //---------------------------------------------------------------------//
    public int compareTo (Version version)
    {
        if (version == null)
            return 1;
        if (majorVersion != version.majorVersion)  
            return majorVersion - version.majorVersion;
        if (releaseVersion != version.releaseVersion)
            return releaseVersion - version.releaseVersion;
        if (featureVersion != version.featureVersion)
            return featureVersion - version.featureVersion;
        return branchVersion - version.branchVersion;
    }
}
//=========================================================================//
