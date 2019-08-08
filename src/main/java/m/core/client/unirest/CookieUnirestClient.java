package m.core.client.unirest;

import java.util.List;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;

import com.mashape.unirest.http.Unirest;

import m.core.common.Debug;

public class CookieUnirestClient extends UnirestClient {

    private BasicCookieStore cookieStore;

    public CookieUnirestClient() {
        cookieStore = new BasicCookieStore();
        Unirest.setHttpClient(HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .setDefaultCookieStore(cookieStore).build());
    }

    public void clearCookies() {
        cookieStore.clear();
    }

    public void debugLogCookies() {
        Debug.log(cookieStore);
    }

    public void debugPrintCookies() {
        Debug.print(cookieStore);
    }

    public Cookie getCookie(String name) {
        return getCookies().stream().filter(e -> name.equals(e.getName())).findAny().get();
    }

    public List<Cookie> getCookies() {
        return cookieStore.getCookies();
    }

}