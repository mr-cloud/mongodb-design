package homwork2_3;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MyMongoCRUD {
	public static void main(String[] args){
		//initial mongo client and connect to collection.
		MongoClient client = new MongoClient();
		MongoDatabase database = client.getDatabase("students");
		MongoCollection<Document> collection = database.getCollection("grades");
		//action
		List<Document> results = 
		collection.find(new Document()
				.append("type", "homework")).sort(new Document()
						.append("student_id", 1)
						.append("score", 1)).into(new ArrayList<Document>());
		int groupStudentId = -1;
		for(Document tmp: results){
			if(tmp.getInteger("student_id") != groupStudentId){
				groupStudentId = tmp.getInteger("student_id");
				collection.deleteOne(tmp);
			}
		}
	}

}
