package com.hackorama.mcore.data;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.bson.Document;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;

/**
 *
 * Basic MongoDB connection tests, requires MongoDB server on localhost
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class MongoDBTest {

    private static MongoDatabase db;

    public static void put(String store, String id, String json) {
        MongoCollection<Document> table = db.getCollection(store);
        table.createIndex(new BasicDBObject("id", 1), new IndexOptions().unique(true));
        table.replaceOne(Filters.eq("id", id), Document.parse(json), new ReplaceOptions().upsert(true));
    }

    public static String get(String store, String id) {
        MongoCollection<Document> table = db.getCollection(store);
        Document doc = table.find(Filters.eq("id", id)).first();
        return doc == null ? "" : doc.toJson();
    }

    public static void main(String[] args) throws SQLException, UnirestException {
        MongoClient mongo = new MongoClient("localhost", 27017);
        db = mongo.getDatabase("test");
        put("store", "one", "{'id' : 'one','name' : 'UNO'}");
        put("store", "one", "{'id' : 'one','name' : 'UNO UNO'}");
        put("store", "two", "{'id' : 'two','name' : 'DOS'}");
        for (Document doc : db.getCollection("store").find()) {
            System.out.println(doc.toJson());
        }
        assertTrue(get("store", "one").contains("UNO UNO"));
        assertTrue(get("store", "two").contains("DOS"));
        mongo.close();
    }

}
