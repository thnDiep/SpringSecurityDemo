package com.example.demo.configuration;

import lombok.AccessLevel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties
public class DataSourceProperties {
    String url;
    String driverClassName;
    String username;
    String password;
}
