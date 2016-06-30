package fr.info.antillesinfov2.business;

public final class Constant {
    //public static final String URL_DNS = "http://caisseproto-1/ws-stock";
    public static final String URL_DNS = "http://192.168.1.19/ws-stock";
    //public static final String URL_DNS = "http://localhost:8080/ws-stock";
    //public static final String URL_DNS = "http://192.168.2.37/ws-stock";
    public static final String URL_SESSION = URL_DNS + "/sessions";
    public static final String ACTION_RETRAIT = "RETRAIT";
    public static final String ACTION_DEPOT = "DEPOT";

    private Constant(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }

}
