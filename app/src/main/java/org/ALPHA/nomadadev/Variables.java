package org.ALPHA.nomadadev;

import android.location.Location;

/**
 * Created by ALPHA on 27/10/2014.
 */
public class Variables
{
    public SensorData sensorData;
    public MovStatus movStatus;
    public ServiceStatus serviceStatus;
    public ServiceRequest serviceRequest;
    public ServiceData serviceData;
    public Detection detection;

    public static class SensorData
    {
        public Accelerometer accelerometer;
        public Proximity proximity;
        public Light light;
        public GPS gps;
        public Pedometer pedometer;
        public Rotation rotation;

        public static class Accelerometer
        {
            public float movQuantity = 0;
            public float movQuantityAbs = 0;
            public float movQuantityAbsLast = 0;

            public Current current;
            public AveragePerTimeStep averagePerTimeStep;
            public Compute compute;
            public SpeedPerTimeStep speedPerTimeStep;
            public DistancePerTimeStep distancePerTimeStep;

            public static class Current
            {
                public float x=0;
                public float y=0;
                public float z=0;
            }

            public static class AveragePerTimeStep
            {
                public Signed signed;
                public Abs abs;

                public static class Signed
                {
                    public float x=0;
                    public float y=0;
                    public float z=0;
                }

                public static class Abs
                {
                    public float x=0;
                    public float y=0;
                    public float z=0;
                }
            }

            public static class Compute
            {
                public int nSamples = 0;
                public float Ts = 0;

                public Average average;

                public static class Average
                {
                    public Signed signed;
                    public Abs abs;

                    public static class Signed
                    {
                        public float x=0;
                        public float y=0;
                        public float z=0;
                    }

                    public static class Abs
                    {
                        public float x=0;
                        public float y=0;
                        public float z=0;
                    }
                }
            }

            public static class SpeedPerTimeStep
            {
                public double x = 0;
                public double y = 0;
                public double z = 0;
                public double speed = 0;
                public double speedKm = 0;
            }

            public static class DistancePerTimeStep
            {
                public double x = 0;
                public double y = 0;
                public double z = 0;
                public double distance = 0;
            }
        }

        public static class Proximity
        {
            public float value = 0;
        }

        public static class Light
        {
            public float value = 0;
        }

        public static class GPS
        {
            public boolean trackingOn = false;
            public long timeNoTracking = 0;
            public long currentTimeStep = 0;
            public long lastTime=0;
            public long currentTime=0;

            public int nTotalLocations = 0;
            public int Status=0;

            public boolean firstTime=true;
            
            public CurrentLocation currentLocation;
            
            public static class CurrentLocation
            {
                public double latitude=0;
                public double longitude=0;
                public double altitude=0;
                public float accuracy=0;
                public float speed=0;
                public float speedKm=0;
                public float distanceBetweenLocations = 0;
                public Location location;
                public Location locationLast;
            }
        }

        public static class Pedometer
        {
            public int nSteps = 0;
        }

        public static class Rotation
        {
            public float x = 0;
            public float y = 0;
            public float z = 0;
        }

    }

    public static class MovStatus
    {
        public Constants.MovStatus mov;
        public Constants.MovStatus movLast;
        public Constants.MovSubStatus movSub;
        public Constants.MovSubStatus movSubLast;
        public boolean Changed=false;
        public boolean changedFromStill=false;
    }

    public static class ServiceStatus
    {
        public boolean logRecordOn = false;
    }

    public static class ServiceData
    {
        public long runTime = 0;
        public long currentTimeStep = 0;
        public long startTime = 0;
        public long currentTime = 0;
        public long lastTime=0;
    }

    public static class ServiceRequest
    {
        public boolean newLogFile = false;
        public boolean logFileClose = false;
        public boolean mapUpdate = false;
        public boolean distanceUpdate = false;
    }

    public static class Detection
    {
        public MovQuantityThresholds movQuantityThresholds;
        public TimeForChangingTo timeForChangingTo;
        public Locations locations;
        public Times times;
        public Pedometer pedometer;

        public static class MovQuantityThresholds
        {
            public Min min;
            public Max max;
            
            public static class Min
            {
                public double Walking = 0;
                public double Running = 0;
                public double VehicleStill = 0;
                public double VehicleRunning = 0;
            }
            public static class Max
            {
                public double StillAbsolutely = 0;
                public double Still = 0;
                public double Walking = 0;
                public double Running = 0;
                public double VehicleStill = 0;
                public double VehicleRunning = 0;
                public double VehicleHavingPark = 0;
            }
        }

        public static class TimeForChangingTo
        {
            public long Still=0;
            public long Walking=0;
            public long Running=0;
            public long Vehicle=0;
            public long VehicleParked=0;
        }

        public static class Locations
        {
            public Path path;
            public Place place;

            public boolean currentSession=false;
            public boolean needToSetUnknownActivityPathEnding=false;

            public static class Path
            {
                public boolean firstPath=true;
                public boolean trackingOn=false;

                public ComputeCurrentSpeed computeCurrentSpeed;

                public static class ComputeCurrentSpeed
                {
                    public float speedSum = 0;
                }
            }
            public static class Place
            {
                public boolean firstPlace=true;
                public long timeForNewPlace = 0;
                public boolean trackingOn=false;

                public ComputeCurrentPlace computeCurrentPlace;

                public static class ComputeCurrentPlace
                {
                    public double latitudeSum = 0;
                    public double longitudeSum = 0;
                    public double altitudeSum = 0;
                    public int nLocations = 0;
                }
            }
        }

        public static class Times
        {
            public long timeWhileDetectingToAdd=0;
        }

        public static class Pedometer
        {
            public int detectionStatus = 0;
            public int currentStepsWhenPathStarted = 0;
        }
    }
}
