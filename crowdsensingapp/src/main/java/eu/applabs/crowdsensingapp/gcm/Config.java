package eu.applabs.crowdsensingapp.gcm;

public interface Config {

    // used to share GCM regId with application server - using php app server
    //static final String APP_SERVER_URL = "https://www.applabs.eu/gcm/gcm.php?shareRegId=1";
    static final String APP_SERVER_URL = "http://as.applabs.eu:8080/mygcm/GCMNotification";

    // GCM server using java
    // static final String APP_SERVER_URL =
    // "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";

    // Google Project Number
    static final String GOOGLE_PROJECT_ID = "crowdsensingtv";
    static final String MESSAGE_KEY = "message";

}
