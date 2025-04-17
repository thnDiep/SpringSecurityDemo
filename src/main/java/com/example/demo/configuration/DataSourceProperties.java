package com.example.demo.configuration;

import lombok.AccessLevel;
import lombok.Data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataSourceProperties {
    String url;
    String driverClassName;
    String username;
    String password;
}
