package final_exam;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ExamTerminator {
	private MongoClient client = new MongoClient();
	private MongoDatabase db = client.getDatabase("photo-sharing");
	private MongoCollection<Document> albumsCollection = db.getCollection("albums");
	private MongoCollection<Document> imagesCollection = db.getCollection("images");
	
	public static void main(String[] args){
		ExamTerminator test = new ExamTerminator();
		//遍历images collection，判断是否存在于album collection的images域值中
		ArrayList<Document> images = test.imagesCollection.find(
				new Document()).into(new ArrayList<Document>());
		for(Document tmp: images){
			int imageId = tmp.getInteger("_id");
			System.out.println("imageId: " + imageId);
			//通过查找包含该iamgeId的album document，若返回数量为零则从imagesCollection删除该image
			ArrayList<Document> eligibleAlbums = test.albumsCollection.find(
					new Document("images", imageId)).into(new ArrayList<Document>());

			if(eligibleAlbums == null || eligibleAlbums.size() == 0){
				//表明没找到符合条件的album
				System.out.println("没找到符合条件的album！");
				test.imagesCollection.deleteOne(new Document("_id", imageId));
			}
			else{
				System.out.println("找到符合条件的album,一共 " + eligibleAlbums.size() + " 条！");
				System.out.println("第一条是：" + eligibleAlbums.get(0).toString());
			}
		}

	}

}
