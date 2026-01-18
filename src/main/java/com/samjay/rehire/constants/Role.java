package com.samjay.rehire.constants;

import lombok.Getter;

@Getter
public enum Role {

    ORGANIZATION("ORGANIZATION");
    private final String roleName;

    Role(String roleName) {

        this.roleName = roleName;

    }
}