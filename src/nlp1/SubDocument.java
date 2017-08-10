package nlp1;

public class SubDocument extends Document {
	public SubDocument(int type, String s) {
		super(type, s);
		// TODO Auto-generated constructor stub
	}
	public int docType;
	/*public SubDocument(String s) {
		super(s);
		// TODO Auto-generated constructor stub
	}
	public SubDocument(String s, int t){
		super(s);
		this.docType = t;
	}*/
	public enum Type {
		docID, filePath, title, author, affliation, supervisor,
		date, keyword, abstractt, intro, chapter, conclusion, reference}
	
}
