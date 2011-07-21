package com.onibuscerto.api.entities;

import java.util.Collection;

public interface Route {

    public String getId();

    public String getShortName();

    public void setShortname(String shortName);

    public String getLongName();

    public void setLongName(String longName);

    public Type getType();

    public void setType(Type type);

    public Collection<Trip> getTrips();

    public enum Type {

        STREETCAR(0),
        SUBWAY(1),
        RAIL(2),
        BUS(3),
        FERRY(4),
        CABLE_CAR(5),
        GONDOLA(6),
        FUNICULAR(7);
        private int code;

        private Type(int code) {
            this.code = code;
        }

        public int toInt() {
            return code;
        }

        public static Type fromInt(int code) {
            for (Type type : Type.values()) {
                if (type.code == code) {
                    return type;
                }
            }

            return null;
        }
    };
}
