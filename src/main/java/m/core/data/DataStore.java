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
 * Stores of this data-store also allows duplicate keys so stores operate like
 * {@code Multimaps} (maps with more then one value for a key).
 * <p>
 * {@link DataStore#get(String, String)} and
 * {@link DataStore#put(String, String, String)} operates like a regular single
 * value per key {@code Map}.
 * <p>
 * <ul>
 * <li>{@code get} returns a single matching value
 * <li>{@code put} adds a new value for the key, replaces any existing value for
 * the key
 * </ul>
 * {@link DataStore#getMulti(String, String)} and
 * {@link DataStore#putMulti(String, String, String)} operates like a multiple
 * values per key {@code Multimap}.
 * <p>
 * <ul>
 * <li>{@code getMultiKey} returns a {@code List} of matching values
 * <li>{@code putMutiKey} adds a new value for the key, without replacing any
 * existing values for the key
 * </ul>
 * <p>
 * The stores get created on the first insertion of a key value.
 *
 * So {@code put} will initialize a regular single value per key store and
 * {@code putMuti} will initialize a multiple values per key {@code Multimap}
 * store.
 * <p>
 * After a store is initialized as a regular store using {@code put} any attempt
 * to do a {@code putMuti} on that store will cause a {@code RunTimeException}.
 *
 * After a store once initialized as a {@code Multimap} store using
 * {@code putMulti} any attempt to do a regular {@code put} on that store will
 * cause a {@code RunTimeException}.
 *
 * @see https://en.wikipedia.org/wiki/Multimap
 *
 * @implSpec Implementations of this data-store should provide the necessary
 *           access methods when leveraging an existing database system, and
 *           should clean up any resources in {@link DataStore#close()}.
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
     * <p>
     * Closes all open resources (file handles or network connections) of this data
     * store.
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
     * Returns all the values for the specified key of the specified
     * {@code Mutimap} store in this data-store.
     *
     * @param store the store name
     * @param key   the key name
     * @return the values as {@code List} if the store and key exists, could be
     *         empty
     */
    public List<String> getMulti(String store, String key);

    /**
     * Stores the specified key and value (replacing any existing value) to the
     * specified store in this data-store.
     * <p>
     * The specified store will be created if it does not exist.
     *
     * @param store the store name
     * @param key   the key name
     * @param value the value name
     */
    public void put(String store, String key, String value);

    /**
     * Stores the specified key and value to the specified store as a
     * {@code Multimap} in this data-store.
     * <p>
     * The value will be stored without replacing any existing values, allowing
     * multiple values for a key.
     * <p>
     * The specified store will be created as a new {@code Multimap} store if it
     * does not already exist.
     *
     * @param store the store name
     * @param key   the key name
     * @param value the value
     */
    public void putMulti(String store, String key, String value);

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
