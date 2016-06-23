package fr.info.antillesinfov2.business;

public final class Constant {
    public static final String URL_DNS = "http://caisseproto/ws-stock";

    private Constant(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }

}
