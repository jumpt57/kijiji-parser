package com.github.jumpt57.kijiji.models;

import java.util.Collection;
import java.util.HashSet;

public class Offer {
    public String price;
    public String title;
    public String url;
    public String date;
    public String address;
    public double lat;
    public double lng;
    public String aptId;
    public Collection<String> pictures = new HashSet<>();
}
