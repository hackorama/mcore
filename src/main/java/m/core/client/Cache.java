package m.core.client;

/**
 *  An HTTP client cache.
 */
public interface Cache {

    /**
     * Sets the specified count and size of this client cache.
     *
     * @param maxEntryCount the maximum entries this cache can hold
     * @param maxObjectSize  the maximum sized object in bytes this cache will accept
     */
    public void setCountAndSize(int maxEntryCount, long maxObjectSize);
}
