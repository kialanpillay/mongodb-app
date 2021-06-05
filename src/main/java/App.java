import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Arrays;


public class App {
    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("nobel");

        MongoCollection<Document> prize = database.getCollection("prize");
        MongoCollection<Document> laureate = database.getCollection("laureate");
        // Operations

        prize.aggregate(Arrays.asList(
                Aggregates.match(Filters.eq("category", "medicine")),
                Aggregates.unwind("$laureates"),
                Aggregates.group(null, Accumulators.sum("count", 1)))
        ).forEach(document -> System.out.println("Medicine Nobel Laureate Count: " + document.get("count")));

        //Insaaf Query
        FindIterable<Document> iterable = laureate.find(Filters.eq("bornCountry", "South Africa"));
        try (MongoCursor<Document> iterator = iterable.iterator()) {
            int count = 0;
            while (iterator.hasNext()) {
                iterator.next();
                count += 1;
            }
            System.out.println("South African Nobel Laureate Count: " + count);
        }

    }
}
