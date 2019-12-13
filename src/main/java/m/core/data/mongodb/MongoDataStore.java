package m.core.data.mongodb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import m.core.data.DataStore;

public class MongoDataStore implements DataStore {

    private static final Logger logger = LoggerFactory.getLogger(MongoDataStore.class);
    private String db = "test"; // MongoDB default test database name by convention
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private Set<String> multiKeyStores = new HashSet<String>();
    private Set<String> singleKeyStores = new HashSet<String>();

    public MongoDataStore() {
        mongoClient = new MongoClient();
        mongoDatabase = mongoClient.getDatabase(db);
    }

    public MongoDataStore(String db) {
        this.db = db;
        mongoClient = new MongoClient();
        mongoDatabase = mongoClient.getDatabase(db);
    }

    public MongoDataStore(String db, String host) {
        this.db = db;
        mongoClient = new MongoClient(host);
        mongoDatabase = mongoClient.getDatabase(db);
    }

    public MongoDataStore(String db, String host, int port) {
        mongoClient = new MongoClient(host, port);
    }

    private void checkStoreConflicts(String name, boolean isMulti) {
        if (isMulti) {
            if (singleKeyStores.contains(name)) {
                throw new RuntimeException("Another store already exists with the same name " + name);
            }
            multiKeyStores.add(name);
        } else {
            if (multiKeyStores.contains(name)) {
                throw new RuntimeException("Another store already exists with the same name " + name);
            }
            singleKeyStores.add(name);
        }
    }

    @Override
    public void clear() {
        mongoDatabase.drop();
        logger.info("Cleard data store {}", db);
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Override
    public boolean contains(String store, String key) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(store);
        FindIterable<Document> findIterable = mongoCollection.find(Filters.eq("K", key));
        return findIterable.iterator().hasNext();
    }

    @Override
    public List<String> get(String store) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(store);
        FindIterable<Document> findIterable = mongoCollection.find(Filters.exists("K"));
        MongoCursor<Document> cursor = findIterable.iterator();
        List<String> resultList = new ArrayList<>();
        while (cursor.hasNext()) {
            resultList.add(cursor.next().get("V").toString());
        }
        return resultList;
    }

    @Override
    public String get(String store, String key) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(store);
        FindIterable<Document> findIterable = mongoCollection.find(Filters.eq("K", key));
        MongoCursor<Document> cursor = findIterable.iterator();
        if (cursor.hasNext()) {
            return cursor.next().get("V").toString();
        }
        return null;
    }

    @Override
    public List<String> getByValue(String store, String value) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(store);
        FindIterable<Document> findIterable = mongoCollection.find(Filters.eq("V", value));
        MongoCursor<Document> cursor = findIterable.iterator();
        List<String> resultList = new ArrayList<>();
        while (cursor.hasNext()) {
            resultList.add(cursor.next().get("K").toString());
        }
        return resultList;
    }

    @Override
    public Set<String> getKeys(String store) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(store);
        FindIterable<Document> findIterable = mongoCollection.find(Filters.exists("K"));
        MongoCursor<Document> cursor = findIterable.iterator();
        Set<String> resultList = new HashSet<>();
        while (cursor.hasNext()) {
            resultList.add(cursor.next().get("K").toString());
        }
        return resultList;
    }

    @Override
    public List<String> getMulti(String store, String key) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(store);
        FindIterable<Document> findIterable = mongoCollection.find(Filters.eq("K", key));
        MongoCursor<Document> cursor = findIterable.iterator();
        List<String> resultList = new ArrayList<>();
        while (cursor.hasNext()) {
            resultList.add(cursor.next().get("V").toString());
        }
        return resultList;
    }

    @Override
    public void put(String store, String key, String value) {
        checkStoreConflicts(store, false);
        if (contains(store, key)) {
            MongoCollection<Document> collection = mongoDatabase.getCollection(store);
            collection.updateOne(Filters.eq("K", key), Updates.set("V", value));
        } else {
            MongoCollection<Document> collection = mongoDatabase.getCollection(store);
            Document document = new Document("K", key).append("V", value);
            collection.insertOne(document);
        }
    }

    @Override
    public void putMulti(String store, String key, String value) {
        checkStoreConflicts(store, true);
        MongoCollection<Document> collection = mongoDatabase.getCollection(store);
        Document document = new Document("K", key).append("V", value);
        collection.insertOne(document);
    }

    @Override
    public void remove(String store, String key) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(store);
        mongoCollection.deleteMany(Filters.eq("K", key));
    }

    @Override
    public void remove(String store, String key, String value) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(store);
        mongoCollection.deleteMany(Filters.and(Filters.eq("K", key), Filters.eq("V", value)));
    }

}
