package org.ALPHA.nomadadev;

/**
 * Created by ALPHA on 12/11/2014.
 */
public class CurrentData
{
    public static Day day = new Day();
    public static TimeLine timeLine = new TimeLine();

    public static int nItemToShow = 0;
    public static int nPlaceToShow = 0;
    public static int nPathToShow = 0;
    public static int nUnknownPathToShow = 0;

    public void reset ()
    {
        nItemToShow = 0;
        nPlaceToShow = 0;
        nPathToShow = 0;
        nUnknownPathToShow = 0;
    }
}
