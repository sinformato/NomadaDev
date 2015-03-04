package org.ALPHA.nomadadev;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALPHA on 23/10/2014.
 */
public class Day implements Serializable
{
    public int nPlaces = 0;
    public int nPaths = 0;
    public int nUnknownActivityPaths = 0;

    public int nItems = 0;
    public List<Character> typeOfItem = new ArrayList<Character>();

    public int day = 0;
    public int month = 0;
    public int year = 0;
    public String dateString = "";
    public String fileName = "";

    public List<Place> places = new ArrayList<Place>();
    public List<Path> paths = new ArrayList<Path>();
    public List<UnknownActivityPath> unknownActivityPaths = new ArrayList<UnknownActivityPath>();
    public Stats stats;

    public static class Stats implements Serializable
    {
        public Distances distances;
        public Times times;
        public Speeds speeds;
        public NDetections nDetections;
        public Pedometer pedometer;
        public GPS gps;
        
        public static class Distances implements Serializable
        {
            public float total = 0;
            public float walking = 0;
            public float running = 0;
            public float vehicle = 0;
            public double totalDistanceByAcceleration = 0;
        }
        
        public static class Times implements Serializable
        {
            public long Total = 0;
            public long TimeAverageInEveryPlace = 0;

            public Still still;
            public Walking walking;
            public Running running;
            public Vehicle vehicle;

            public static class Still implements Serializable
            {
                public long Total = 0;
                public long Absolutely=0;
                public long MovingSure=0;
                public long MovingProbably=0;
            }

            public static class Walking implements Serializable
            {
                public long Total = 0;
                public long Sure=0;
                public long Probably=0;
            }

            public static class Running implements Serializable
            {
                public long Total = 0;
                public long Sure=0;
                public long Probably=0;
            }

            public static class Vehicle implements Serializable
            {
                public long Total = 0;
                public long RunningSure=0;
                public long StillSure=0;
                public long NoGPS=0;
            }
        }

        public static class Speeds implements Serializable
        {
            public Min min;
            public Max max;
            public Average average;
            
            public static class Min implements Serializable
            {
                public float Walking = 0;
                public float Running = 0;
                public float Vehicle = 0;
            }
            public static class Max implements Serializable
            {
                public float Walking = 0;
                public float Running = 0;
                public float Vehicle = 0;
            }
            public static class Average implements Serializable
            {
                public float Walking = 0;
                public float Running = 0;
                public float Vehicle = 0;
            }
        }
        
        public static class NDetections implements Serializable
        {
            public int Total = 0;
            public int Still=0;
            public int Walking=0;
            public int Running=0;
            public int Vehicle=0;
            public int VehicleParked=0;
        }

        public static class Pedometer implements Serializable
        {
            public int nStepsTotal = 0;
            public int nStepsWalking = 0;
            public int nStepsRunning = 0;
        }

        public static class GPS implements Serializable
        {
            public int nTotalLocationPoints = 0;
        }
    }

    public static class Place implements Serializable
    {
        public double latitude = 0;
        public double longitude = 0;
        public double altitude = 0;

        public String address = "";
        public String placeName = "";

        public long timeArrival = 0;
        public long timeDeparture = 0;
        public long timeDuration = 0;
        public String timeArrivalString = "";
        public String timeDepartureString = "";
        public String timeDurationString = "";
    }

    public static class Path implements Serializable
    {
        public long timeStart = 0;
        public long timeEnd = 0;
        public long timeDuration = 0;
        public String timeStartString = "";
        public String timeEndString = "";
        public String timeDurationString = "";

        public Constants.MovStatus activityType = Constants.MovStatus.INITIAL;

        public int Color = 0;

        public float distance = 0;

        public float speedAverage = 0;
        public float speedMin = 0;
        public float speedMax = 0;

        public int nRoutePoints = 0;
        public List<Coordinates> routePoints = new ArrayList<Coordinates>();

        public int nSteps = 0;
    }

    public static class UnknownActivityPath implements Serializable
    {
        public long timeStart = 0;
        public long timeEnd = 0;
        public long timeDuration = 0;

        public int Color = 0;

        public float distance = 0;

        public Coordinates pointStart = new Coordinates();
        public Coordinates pointEnd = new Coordinates();
    }

    public static class Coordinates implements Serializable
    {
        public double longitude;
        public double latitude;
        public double altitude;
    }
}
