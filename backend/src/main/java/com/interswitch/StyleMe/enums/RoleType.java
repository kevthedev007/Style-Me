package com.interswitch.StyleMe.enums;

public enum RoleType {
    ROLE_USER("User");

    private final String value;

    RoleType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
