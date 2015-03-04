package org.ALPHA.nomadadev;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALPHA on 23/10/2014.
 */
public class TimeLine implements Serializable
{
    public int nDays = 0;

    public List<Item> item = new ArrayList<Item>();

    public static class Item implements Serializable
    {
        public int year = 0;
        public int month = 0;
        public int day = 0;

        public String fileName= "";
    }
}