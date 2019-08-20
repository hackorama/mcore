package m.core.client.unirest;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;

import com.mashape.unirest.http.Unirest;

import m.core.client.Cache;
import m.core.config.Configuration;

/**
 * An HTTP client based on Unirest, that supports caching.
 *
 * @see <a href="http://unirest.io">unirest.io</a>
 */
public class CachingUnirestClient extends UnirestClient implements Cache {

    private CacheConfig cacheConfig;

    /**
     * Constructs a Unirest based HTTP client with caching support.
     */
    public CachingUnirestClient() {
        super();
        setCountAndSize(Configuration.clientCacheEntriesCount(), Configuration.clientCacheMaxObjectSizeBytes());
    }

    @Override
    public void setCountAndSize(int entryCount, long maxObjectSize) {
        cacheConfig = CacheConfig.custom().setMaxCacheEntries(entryCount).setMaxObjectSize(maxObjectSize)
                .setSharedCache(false).build();
        CloseableHttpClient cachingClient = CachingHttpClients.custom().setCacheConfig(cacheConfig)
                .setDefaultRequestConfig(requestConfig).build();
        Unirest.setHttpClient(cachingClient);
    }

}
