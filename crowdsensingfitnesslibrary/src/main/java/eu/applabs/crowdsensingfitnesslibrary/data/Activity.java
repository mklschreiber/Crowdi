package eu.applabs.crowdsensingfitnesslibrary.data;

public class Activity {
    public enum Type {
        In_Vehicle,
        Biking,
        On_Foot,
        Still,
        Unknown,
        Tilting,
        Walking,
        Running,
        Aerobic,
        Badminton,
        Baseball,
        Basketball,
        Biathlon,
        Handbiking,
        Mountain_Biking,
        Road_Biking,
        Spinning,
        Stationary_Biking,
        Utility_Biking,
        Boxing,
        Calisthenics,
        Circuit_Training,
        Cricket,
        Dancing,
        Elliptical,
        Fencing,
        American_Football,
        Australian_Football,
        Soccer_Football,
        Frisbee,
        Gardening,
        Golf,
        Gymnastics,
        Handball,
        Hiking,
        Hockey,
        Horseback_Riding,
        Housework,
        Jumping_Rope,
        Kayaking,
        Kettlebell_Training,
        Kickboxing,
        Kitesurfing,
        Material_Arts,
        Meditation,
        Mixed_Material_Arts,
        P90X_Exercises,
        Paragliding,
        Pilates,
        Polo,
        Racquetball,
        Rock_Climbing,
        Rowing,
        Rowing_Machine,
        Rugby,
        Jogging,
        Running_On_Sand,
        Running_Treadmill,
        Sailing,
        Scuba_Diving,
        Skateboarding,
        Skating,
        Cross_Skating,
        Inline_Skating,
        Skiing,
        Back_Country_Skiing,
        Cross_Country_Skiing,
        Downhill_Skiing,
        Kite_Skiing,
        Roller_Skiing,
        Sledding,
        Sleeping,
        Snowboarding,
        Snowmobile,
        Snowshoeing,
        Squash,
        Stair_Climbing,
        Stair_Climbing_Machine,
        Stand_Up_Paddleboarding,
        Strength_Training,
        Surfing,
        Swimming,
        Swimming_Open_Water,
        Swimming_Swimming_Pool,
        Table_Tennis,
        Team_Sports,
        Tennis,
        Treadmill,
        Volleyball,
        Volleyball_Beach,
        Volleyball_Indoor,
        Wakeboarding,
        Walking_Fitness,
        Nording_Walking,
        Walking_Treadmill,
        Waterpolo,
        Weightlifting,
        Wheelchair,
        Windsurfing,
        Yoga,
        Zumba,
        Diving,
        Ergometer,
        Ice_Skating,
        Indoor_Skating,
        Curling,
        PLACEHOLDER_NOT_DEFINED,
        Other,
        Light_Sleep,
        Deep_Sleep,
        REM_Sleep,
        Awake
    }

    public static String convertToString(Type type) {
        switch(type) {
            case In_Vehicle:
                return "In vehicle";
            case Biking:
                return "Biking";
            case On_Foot:
                return "On foot";
            case Still:
                return "Still";
            case Unknown:
                return "Unknown";
            case Tilting:
                return "Tilting";
            case Walking:
                return "Walking";
            case Running:
                return "Running";
            case Aerobic:
                return "Aerobic";
            case Badminton:
                return "Badminton";
            case Baseball:
                return "Baseball";
            case Basketball:
                return "Basketball";
            case Biathlon:
                return "Biathlon";
            case Handbiking:
                return "Handbiking";
            case Mountain_Biking:
                return "Mountain biking";
            case Road_Biking:
                return "Road biking";
            case Spinning:
                return "Spinning";
            case Stationary_Biking:
                return "Stationary biking";
            case Utility_Biking:
                return "Utility biking";
            case Boxing:
                return "Boxing";
            case Calisthenics:
                return "Calisthenics";
            case Circuit_Training:
                return "Circuit training";
            case Cricket:
                return "Cricket";
            case Dancing:
                return "Dancing";
            case Elliptical:
                return "Elliptical";
            case Fencing:
                return "Fencing";
            case American_Football:
                return "American football";
            case Australian_Football:
                return "Australian football";
            case Soccer_Football:
                return "Soccer football";
            case Frisbee:
                return "Frisbee";
            case Gardening:
                return "Gardening";
            case Golf:
                return "Golf";
            case Gymnastics:
                return "Gymnastic";
            case Handball:
                return "Handball";
            case Hiking:
                return "Hiking";
            case Hockey:
                return "Hockey";
            case Horseback_Riding:
                return "Horseback Riding";
            case Housework:
                return "Housework";
            case Jumping_Rope:
                return "Jumping rope";
            case Kayaking:
                return "Kayaking";
            case Kettlebell_Training:
                return "Kettleball training";
            case Kickboxing:
                return "Kickboxing";
            case Kitesurfing:
                return "Kitesurfing";
            case Material_Arts:
                return "Material arts";
            case Meditation:
                return "Meditation";
            case Mixed_Material_Arts:
                return "Mixed material arts";
            case P90X_Exercises:
                return "P90X exercises";
            case Paragliding:
                return "Paragliding";
            case Pilates:
                return "Pilates";
            case Polo:
                return "Polo";
            case Racquetball:
                return "Racquetball";
            case Rock_Climbing:
                return "Rock climbing";
            case Rowing:
                return "Rowing";
            case Rowing_Machine:
                return "Rowing machine";
            case Rugby:
                return "Rugby";
            case Jogging:
                return "Jogging";
            case Running_On_Sand:
                return "Running on sand";
            case Running_Treadmill:
                return "Running (Treadmill)";
            case Sailing:
                return "Sailing";
            case Scuba_Diving:
                return "Scuba diving";
            case Skateboarding:
                return "Sakteboarding";
            case Skating:
                return "Skating";
            case Cross_Skating:
                return "Cross skating";
            case Inline_Skating:
                return "Inline skating";
            case Skiing:
                return "Skiing";
            case Back_Country_Skiing:
                return "Back country skiing";
            case Cross_Country_Skiing:
                return "Cross country skiing";
            case Downhill_Skiing:
                return "Downhill skiing";
            case Kite_Skiing:
                return "Kite skiing";
            case Roller_Skiing:
                return "Roller skiing";
            case Sledding:
                return "Sledding";
            case Sleeping:
                return "Sleeping";
            case Snowboarding:
                return "Snowboarding";
            case Snowmobile:
                return "Snowmobile";
            case Snowshoeing:
                return "Snowshoeing";
            case Squash:
                return "Squash";
            case Stair_Climbing:
                return "Stair climbing";
            case Stair_Climbing_Machine:
                return "Stair climbing machine";
            case Stand_Up_Paddleboarding:
                return "Stand up paddleboarding";
            case Strength_Training:
                return "Strength training";
            case Surfing:
                return "Surfing";
            case Swimming:
                return "Swimming";
            case Swimming_Open_Water:
                return "Swimming open water";
            case Swimming_Swimming_Pool:
                return "Swimming swimming pool";
            case Table_Tennis:
                return "Table tennis";
            case Team_Sports:
                return "Team sports";
            case Tennis:
                return "Tennis";
            case Treadmill:
                return "Treadmill";
            case Volleyball:
                return "Volleyball";
            case Volleyball_Beach:
                return "Volleyball beach";
            case Volleyball_Indoor:
                return "Volleyball indoor";
            case Wakeboarding:
                return "Wakeboarding";
            case Walking_Fitness:
                return "Walking fitness";
            case Nording_Walking:
                return "Nording walking";
            case Walking_Treadmill:
                return "Walking (Treadmill)";
            case Waterpolo:
                return "Waterpolo";
            case Weightlifting:
                return "Weightlifiting";
            case Wheelchair:
                return "Wheelchair";
            case Windsurfing:
                return "Windsurfing";
            case Yoga:
                return "Yoga";
            case Zumba:
                return "Zumba";
            case Diving:
                return "Diving";
            case Ergometer:
                return "Ergometer";
            case Ice_Skating:
                return "Ice skating";
            case Indoor_Skating:
                return "Indoor skating";
            case Curling:
                return "Curling";
            case PLACEHOLDER_NOT_DEFINED:
                return "";
            case Other:
                return "Other";
            case Light_Sleep:
                return "Ligth sleep";
            case Deep_Sleep:
                return "Deep sleep";
            case REM_Sleep:
                return "REM sleep";
            case Awake:
                return "Awake";
            default:
                return "";
        }
    }
}
