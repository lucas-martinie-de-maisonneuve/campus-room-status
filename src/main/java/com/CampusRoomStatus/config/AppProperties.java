package com.CampusRoomStatus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.google")
@Component
public class AppProperties {

    private String customer = "my_customer";
    private String timeZone = "Europe/Paris";
    private int maxResultsPerPage = 100;
    private String oauthClientId;
    private String oauthClientSecret;
    private String oauthRefreshToken;

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public int getMaxResultsPerPage() {
        return maxResultsPerPage;
    }

    public void setMaxResultsPerPage(int maxResultsPerPage) {
        this.maxResultsPerPage = maxResultsPerPage;
    }

    public String getOauthClientId() {
        return oauthClientId;
    }

    public void setOauthClientId(String oauthClientId) {
        this.oauthClientId = oauthClientId;
    }

    public String getOauthClientSecret() {
        return oauthClientSecret;
    }

    public void setOauthClientSecret(String oauthClientSecret) {
        this.oauthClientSecret = oauthClientSecret;
    }

    public String getOauthRefreshToken() {
        return oauthRefreshToken;
    }

    public void setOauthRefreshToken(String oauthRefreshToken) {
        this.oauthRefreshToken = oauthRefreshToken;
    }
}