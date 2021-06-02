import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class App {
    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("nobel");

        MongoCollection<Document> prize = database.getCollection("prize");
        MongoCollection<Document> laureate = database.getCollection("laureate");
        // Operations
    }
}
