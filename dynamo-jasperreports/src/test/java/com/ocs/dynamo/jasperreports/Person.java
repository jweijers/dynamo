package com.ocs.dynamo.jasperreports;

public class Person {
    Integer socialId;
    String name;

    public Integer getSocialId() {
        return socialId;
    }

    public void setSocialId(Integer socialId) {
        this.socialId = socialId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person(Integer socialId, String name) {
        super();
        this.socialId = socialId;
        this.name = name;
    }
}