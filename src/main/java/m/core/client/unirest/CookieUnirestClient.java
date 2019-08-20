package m.core.client.unirest;

import java.util.List;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;

import com.mashape.unirest.http.Unirest;

/**
 * An HTTP client based on Unirest with a cookie store.
 *
 */
public class CookieUnirestClient extends UnirestClient {

    private BasicCookieStore cookieStore;

    /**
     * Constructs a Unirest based HTTP client with cookie store.
     */
    public CookieUnirestClient() {
        cookieStore = new BasicCookieStore();
        Unirest.setHttpClient(HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .setDefaultCookieStore(cookieStore).build());
    }

    /**
     * Removes all cookies from the cookie store of this client.
     */
    public void clearCookies() {
        cookieStore.clear();
    }

    /**
     * Returns the specified cookie from the cookie store of this client.
     *
     * @param name the name of the cookie
     * @return the matching cookie, otherwise null
     */
    public Cookie getCookie(String name) {
        return getCookies().stream().filter(e -> name.equals(e.getName())).findAny().get();
    }

    /**
     * Returns all the cookies from the cookie store of this client.
     *
     * @return the {@code List} of cookies, could be empty
     */
    public List<Cookie> getCookies() {
        return cookieStore.getCookies();
    }

    /**
     * Returns the cookie store of this client.
     *
     * @return the cookie store
     */
    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }

}
