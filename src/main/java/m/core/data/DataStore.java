package m.core.data;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;

import m.core.data.cache.DataCache;
import m.core.data.queue.DataQueue;

/**
 * A key value data-store.
 * <p>
 *
 * Both key and value are of {@code String} type.
 *
 * Each store (table) in this data-store (database) is identified by a unique
 * name.
 *
 * Stores of this data-store allow duplicate keys so stores operate like
 * multimaps - maps with more then one value for a key.
 * <p>
 * {@link DataStore#get(String, String)} and
 * {@link DataStore#put(String, String, String)} operates like regular
 * {@code Map}.
 * <p>
 * <ul>
 * <li>{@code get} returns a single matching value
 * <li>{@code put} adds a new value, replaces any existing value
 * </ul>
 * {@link DataStore#getMultiKey(String, String)} and
 * {@link DataStore#putMultiKey(String, String, String)} operates like a
 * {@code MultiMap}.
 * <p>
 * <ul>
 * <li>{@code getMultiKey} returns list of matching values
 * <li>{@code putMutiKey} adds a new value, without replacing any existing values
 * </ul>
 * <p>
 */
public interface DataStore {

    /**
     * Returns a data cache using this data-store.
     *
     * @return data cache
     */
    public default DataCache asCache() {
        throw new NotImplementedException("This data store do not support data cache");
    }

    /**
     * Returns a data queue using this data-store.
     *
     * @return data queue
     */
    public default DataQueue asQueue() {
        throw new NotImplementedException("This data store do not support data queue");
    }

    /**
     * Removes all elements from all stores in this data-store.
     */
    public void clear();

    /**
     * Closes this data-store.
     */
    public void close();

    /**
     * Checks if the specified key exists in the specified store in this data-store.
     *
     * @param store the store name
     * @param key   the key name
     * @return true if specified key exists in the store, false otherwise
     */
    public boolean contains(String store, String key);

    /**
     * Returns all values from the specified store in the data-store
     * <p>
     * Will return empty {@code List}, if the specified store is invalid or empty.
     *
     * @param store the store name
     * @return all values from the specified store as a {@code List}, could be empty
     */
    public List<String> get(String store);

    /**
     * Returns the value for the specified key of the specified store in this
     * data-store.
     *
     * @param store the store name
     * @param key   the key name
     * @return the value if the key exists, otherwise {@code null}
     */
    public String get(String store, String key);

    /**
     * Returns all the keys for a specified value from the specified store in this
     * data-store.
     *
     * @param store the store name
     * @param value the value
     * @return the matching keys as {@code List} if the store and value exists,
     *         could be empty
     */
    public List<String> getByValue(String store, String value);

    /**
     * Returns all the keys from the specified store in in this data-store.
     *
     * @param store the store name
     * @return the keys as {@code List} if store exists, could be empty
     */
    public Set<String> getKeys(String store);

    /**
     * Returns all the values for the specified key of the specified store in this
     * data-store.
     * <p>
     * This is the same as a multimap get operation.
     *
     * @param store the store name
     * @param key   the key name
     * @return the values as {@code List} if the store and key exists, could be
     *         empty
     */
    public List<String> getMultiKey(String store, String key);

    /**
     * Stores the specified key and value (replacing any existing value) to the
     * specified store in this data-store.
     *
     * @param store the store name
     * @param key   the key name
     * @param value the value name
     */
    public void put(String store, String key, String value);

    /**
     * Stores the specified key and value (without replacing any existing values) to
     * the specified store in this data-store.
     * <p>
     * This is the same as a multimap put operation.
     *
     * @param store the store name
     * @param key   the key name
     * @param value the value
     */
    public void putMultiKey(String store, String key, String value);

    /**
     * Removes the specified key and all matching values from the specified store in
     * this data-store.
     *
     * @param store the store name
     * @param key   the key name
     */
    public void remove(String store, String key);

    /**
     * Removes the specified key and only the matching value from the specified
     * store in this data-store.
     *
     * @param store the store name
     * @param key   the key name
     * @param value the value store
     */
    public void remove(String store, String key, String value);

}
