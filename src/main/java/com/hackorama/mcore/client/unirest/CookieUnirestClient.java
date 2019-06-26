package com.hackorama.mcore.client.unirest;

import java.util.List;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;

import com.mashape.unirest.http.Unirest;

public class CookieUnirestClient extends UnirestClient {

    private BasicCookieStore cookieStore;

    public CookieUnirestClient() {
        cookieStore =  new BasicCookieStore();
        Unirest.setHttpClient(HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .setDefaultCookieStore(cookieStore).build());
    }

    public void clearCookies() {
        cookieStore.clear();
    }

    public void debugCookies() {
        System.out.println("[COOKIE STORE");
        getCookies().forEach(e -> {
            System.out.println("NAME: " + e.getName());
            System.out.println(" VALUE: " + e.getValue());
            System.out.println(" PATH: " + e.getPath());
            System.out.println(" DOMAIN: " + e.getDomain());
            System.out.println(" EXPIRYDATE: " + e.getExpiryDate());
            System.out.println(" VERSION: " + e.getVersion());
            System.out.println(" SECURE: " + e.isSecure());
            System.out.println(" PERSISTENT: " + e.isPersistent());
            System.out.println(" COMMENT: " + e.getComment());
            System.out.println(" COMMENTURL: " + e.getCommentURL());
        });
        System.out.println("COOKIE STORE]");
    }

    public List<Cookie> getCookies() {
        return cookieStore.getCookies();
    }

}
