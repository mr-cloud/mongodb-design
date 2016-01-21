package homework3_1;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class FindAndRemoveEmbeddedDoc {
	public static void main(String[] args) {
		// initial mongo client and connect to collection.
		MongoClient client = new MongoClient();
		MongoDatabase database = client.getDatabase("school");
		MongoCollection<Document> collection = database.getCollection("students");
		// action
		List<Document> results = collection.find().into(new ArrayList<Document>());
		// 遍历每个student的scores，得到最小的homework的index，从该Document中删除
		// ，再根据findOneAndReplace替换原来的document
		for (Document tmp : results) {
			int curStudentId = tmp.getInteger("_id", -1);
			double curLowScore = Double.POSITIVE_INFINITY;
			int curLowIndex = -1;
			List<Document> tmpScores = (ArrayList<Document>) tmp.get("scores");
			//域scores并没有建立索引，不能够使用scores.INDEX来做索引值
			/*
			 * for(int i = 0; i < 4; i++){ if(tmp.getString("scores." + i +
			 * ".type").equals("homework")){//域scores并没有建立索引，不能够使用scores.
			 * INDEX来做索引值 double tmpLowScore = tmp.getDouble("scores." + i +
			 * ".score"); if(tmpLowScore < curLowScore){ curLowScore =
			 * tmpLowScore; curLowIndex = i; }
			 * 
			 * }
			 * 
			 * }
			 */
			for(int i = 0; i < tmpScores.size(); i++){
				if(tmpScores.get(i).getString("type").equals("homework")){
					double tmpLowScore = tmpScores.get(i).getDouble("score");
					if(tmpLowScore < curLowScore){ 
						curLowScore = tmpLowScore; 
						curLowIndex = i; 
					}
				}
			}
			List<Document> prunedScores = new ArrayList<Document>();
			for(int i = 0; i < tmpScores.size(); i++){
				if(i != curLowIndex)
					prunedScores.add(tmpScores.get(i));
			}
			System.out.println("before pruned:\n" + tmp.toString());
			//Document pruned = (Document)tmp.remove("scores." + curLowIndex + ".score");
			//只能看到最高层的key，不能嵌套探测。
/*			for(String key: tmp.keySet()){
				System.out.println("key: " + key);
			}
*/			
			System.out.println("after pruned scores:\n");
			for(Document prunedScore: prunedScores){
				System.out.println(prunedScore.toString());
			}			
			collection.updateOne(new Document("_id", curStudentId), new Document("$set"
					, new Document("scores", prunedScores)));
			System.out.println("after update student:\n" + collection.find(new Document("_id", curStudentId)).first().toString());
		}
	}
}
