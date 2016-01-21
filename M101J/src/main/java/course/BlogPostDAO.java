package course;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class BlogPostDAO {
    MongoCollection<Document> postsCollection;

    public BlogPostDAO(final MongoDatabase blogDatabase) {
        postsCollection = blogDatabase.getCollection("posts");
    }

    // Return a single post corresponding to a permalink
    public Document findByPermalink(String permalink) {

        // todo  XXX
        Document post = this.postsCollection.find(new Document("permalink", permalink)).first();



        return post;
    }

    // Return a list of posts in descending order. Limit determines
    // how many posts are returned.
    public List<Document> findByDateDescending(int limit) {

        // todo,  XXX
        // Return a list of Documents, each one a post from the posts collection
        List<Document> posts = this.postsCollection.find()
        		.sort(new Document("date", -1))
        		.limit(limit).into(new ArrayList<Document>());
        //testing
        System.out.println("get posts:");
        for(Document tmp: posts){
        	System.out.println(tmp.toString());
        }

        return posts;
    }


    public String addPost(String title, String body, List tags, String username) {

        System.out.println("inserting blog entry " + title + " " + body);

        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase();
        permalink = permalink+ (new Date()).getTime();


        // todo XXX
        // Remember that a valid post has the following keys:
        // author, body, permalink, tags, comments, date
        //
        // A few hints:
        // - Don't forget to create an empty list of comments
        // - for the value of the date key, today's datetime is fine.
        // - tags are already in list form that implements suitable interface.
        // - we created the permalink for you above.

        // Build the post object and insert it
        Document post = new Document();
        post.append("title", title)
        .append("author", username)
        .append("body", body)
        .append("permalink", permalink)
        .append("tags", tags)
        .append("comments", new ArrayList<Object>())
        .append("date", new Date());
        
        this.postsCollection.insertOne(post);


        return permalink;
    }




    // White space to protect the innocent








    // Append a comment to a blog post
    public void addPostComment(final String name, final String email, final String body,
                               final String permalink) {

        // todo  XXX
        // Hints:
        // - email is optional and may come in NULL. Check for that.
        // - best solution uses an update command to the database and a suitable
        //   operator to append the comment on to any existing list of comments
    	Document comment = new Document()
    			.append("author", name)
    			.append("email", email)
    			.append("body", body);
    	List<Document> comments = new ArrayList<Document>();
    	Document post = 
    	this.postsCollection.find((new Document("permalink", permalink))).first();
    	comments = (List<Document>)post.get("comments");
    	System.out.println("before add a comment:");
    	for(Document tmp: comments){
    		System.out.println(tmp.toString());
    	}
    	comments.add(comment);
    	//update the comments with respect to the responsive post
    	this.postsCollection.updateOne(new Document("permalink", permalink)
    			, new Document("$set", new Document("comments", comments)));
    	System.out.println("after add a comment:");
    	for(Document tmp: (List<Document>)this.postsCollection.find(new Document("permalink", permalink)).first().get("comments"))
    		System.out.println(tmp.toString());
    }
}
