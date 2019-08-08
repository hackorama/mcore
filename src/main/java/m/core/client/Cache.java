package m.core.client;

/**
 * Client cache interface
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public interface Cache {
    /**
     * Set the cache count and object size
     *
     * @param count
     *            Number of entries in the cache
     * @param size
     *            Maximum object size that can be cached
     */
    public void setCountAndSize(int count, long size);
}
