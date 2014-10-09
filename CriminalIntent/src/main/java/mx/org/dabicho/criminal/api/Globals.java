package mx.org.dabicho.criminal.api;

/**
 * Created by dabicho on 10/8/14.
 */
public class Globals {
    private static Integer NaturalOrientation=null;

    public static Integer getNaturalOrientation() {
        return NaturalOrientation;
    }

    public static void setNaturalOrientation(Integer naturalOrientation) {
        NaturalOrientation = naturalOrientation;
    }
}
