package org.ALPHA.nomadadev;

import java.io.Serializable;

/**
 * Created by ALPHA on 27/10/2014.
 */
public class Constants implements Serializable
{
    public Detection detection;
    public UpdateTimer updateTimer;
    public GPS gps;
    public NotificationService notificationService;

    public static class Detection implements Serializable
    {
        public MovQuantity movQuantity;
        public TimeForChangingTo timeForChangingTo;
        public Speed speed;
        public Accelerometer accelerometer;
        public Places places;

        public static class MovQuantity implements Serializable
        {
            public Near near;
            public Far far;

            public static class Near implements Serializable {
                public Min min;
                public Max max;

                public static class Min implements Serializable {
                    public double Walking = 0;
                    public double Running = 0;
                    public double VehicleStill = 0;
                    public double VehicleRunning = 0;
                }

                public static class Max implements Serializable {
                    public double StillAbsolutely = 0;
                    public double Still = 0;
                    public double Walking = 0;
                    public double Running = 0;
                    public double VehicleStill = 0;
                    public double VehicleRunning = 0;
                    public double VehicleHavingPark = 0;
                }
            }

            public static class Far implements Serializable {
                public Min min;
                public Max max;

                public static class Min implements Serializable {
                    public double Walking = 0;
                    public double Running = 0;
                    public double VehicleStill = 0;
                    public double VehicleRunning = 0;
                }

                public static class Max implements Serializable {
                    public double StillAbsolutely = 0;
                    public double Still = 0;
                    public double Walking = 0;
                    public double Running = 0;
                    public double VehicleStill = 0;
                    public double VehicleRunning = 0;
                    public double VehicleHavingPark = 0;
                }
            }
        }

        public static class TimeForChangingTo implements Serializable
        {
            public long Still = 0;
            public long Walking = 0;
            public long Running = 0;
            public long VehicleRunning = 0;
            public long VehicleHaveParked = 0;
        }

        public static class Speed implements Serializable
        {
            public Min min;
            public Max max;

            public static class Min implements Serializable
            {
                public double Walking = 0;
                public double Running = 0;
                public double VehicleRunning = 0;
            }

            public static class Max implements Serializable
            {
                public double Still = 0;
                public double Walking = 0;
                public double Running = 0;
                public double VehicleStill = 0;
                public double VehicleRunning = 0;
                public double Absolute = 0;
            }
        }

        public static class Accelerometer implements Serializable
        {
            public double minAcceleration = 0;
        }

        public static class Places implements Serializable
        {
            public long minTimeStillForNewPlace = 0;
        }
    }

    public static class UpdateTimer implements Serializable
    {
        public int Main = 0;
        public int Map = 0;
        public int Service = 0;
        public int currentActivity = 0;
        public int timeLine = 0;
        public int Settings = 0;
        public int Stats = 0;
    }

    public static class GPS implements Serializable
    {
        public int maxTimeWithoutGpsFix = 0;
        public int updateTime=0;
        public int minDistance=0;
    }

    public static class NotificationService implements Serializable
    {
        public int id = 17;
    }

    public enum MovStatus implements Serializable
    {
        STILL, WALKING, RUNNING, VEHICLE, UNKNOWN, INITIAL
    }

    public enum MovSubStatus implements Serializable
    {
        STILL_SURE, STILL_MOVING_SURE, STILL_MOVING_PROBABLY, WALKING_SURE, WALKING_PROBABLY, RUNNING_SURE, RUNNING_PROBABLY, VEHICLE_RUNNING_SURE, VEHICLE_RUNNING_NO_GPS_PROBABLY, VEHICLE_STILL_SURE, VEHICLE_STILL_PROBABLY, VEHICLE_PARKED_SURE, VEHICLE_PARKED_PROBABLY, UNKNOWN, INITIAL
    }

    public enum NotificationType implements Serializable
    {
        NOTIFICATION_STATUS_CHANGED, NOTIFICATION_UPDATE
    }
}
