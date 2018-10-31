package com.virtuallymachined.countrydb.model;

import java.util.Objects;

public class Country {

    private String name;
    private String alpha2Code;
    private String capital;
    private String region;
    private String flag;

    public Country() {
        // default
    }

    public Country(String name, String alpha2Code, String capital, String region, String flagUrl) {
        this.name = name;
        this.alpha2Code = alpha2Code;
        this.capital = capital;
        this.region = region;
        this.flag = flagUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlpha2Code() {
        return alpha2Code;
    }

    public void setAlpha2Code(String alpha2Code) {
        this.alpha2Code = alpha2Code;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(name, country.name) &&
                Objects.equals(alpha2Code, country.alpha2Code) &&
                Objects.equals(capital, country.capital) &&
                Objects.equals(region, country.region) &&
                Objects.equals(flag, country.flag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, alpha2Code, capital, region, flag);
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", alpha2Code='" + alpha2Code + '\'' +
                ", capital='" + capital + '\'' +
                ", region='" + region + '\'' +
                ", flag='" + flag + '\'' +
                '}';
    }
}
