package com.kritacademy.users.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@Data
public class UpdateUserInfo {
    @Size(min = 1)
    private String firstName;

    @Size(min = 1)
    private String lastName;

    private String password = "";
}
