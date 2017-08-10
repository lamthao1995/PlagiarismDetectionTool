package nlp1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

public class Document {
	public int docType;
	public BagOfFeatures features;
	public ArrayList<Sentence> sentences;
	public ArrayList<String> wordered = new ArrayList<String>();
	public HashSet<String> words = new HashSet<String>();
	public HashMap<String, Integer> wordFreq = new HashMap<String, Integer>();
	public HashMap<String, Double> tf_isf = new HashMap<String, Double>();
	public int endOfDocument;
	public double isf(String s){
		double res = Math.log(sentences.size());
		int stcFreq = 0;
		for (Sentence stc: sentences)
		{
			if (stc.words.contains(s))
			{
				stcFreq++;
			}
		}
		res = res - Math.log(stcFreq);
		return res;
	}
	private boolean isEndingOfSentence(char c){
		if (c == '.' || c == '?' || c == '!')
			return true;
		return false;
	}
	public Document(int type, String s){
		int lastChar = 0;
		StringTokenizer st = new StringTokenizer(s);
		while (st.hasMoreTokens()){
			wordered.add(st.nextToken());
		}
		this.docType = type;
		sentences = new ArrayList<>();
		int i = 0;
		int beginOfSentence = 0;
		boolean flag = true;
		s = s.toLowerCase();
		while (i < s.length()){
			if (isEndingOfSentence(s.charAt(i))){
				if (i - beginOfSentence < Constants.MinSentenceLength);
				else{
					String str = s.substring(beginOfSentence, lastChar + 1);
					Sentence sentence = new Sentence(str, beginOfSentence, this.docType);
					sentences.add(sentence);
					flag = true;
				}
			}
			if (Character.isLetterOrDigit(s.charAt(i))){
				lastChar = i;
			}
			if (flag){
				if (Character.isLetterOrDigit(s.charAt(i))){
					beginOfSentence = i;
					flag = false;
				}
			}
			i++;
		}
		endOfDocument = s.length();
	}
	public Document(ArrayList<Sentence> sentences){
		this.sentences = sentences;
	}
	public Document getDocument(int begin, int end){
		ArrayList<Sentence> sentences = new ArrayList<Sentence>();
		for (int i = begin; i <= end; i++){
			sentences.add(this.sentences.get(i));
		}
		return new Document(sentences);
	}
	public static Document merge(Document front, Document rear){
		ArrayList<Sentence> frontSentences = front.sentences;
		ArrayList<Sentence> rearSentences = rear.sentences;
		frontSentences.addAll(rearSentences);
		return new Document(frontSentences);
	}
	public void calculateThings(){
		for (int i = 0; i < sentences.size(); i++){
			for (String w: sentences.get(i).words){
				if (!this.words.contains(w)){
					this.words.add(w);
					this.wordFreq.put(w, 1);
				}
				else{
					int d = this.wordFreq.get(w);
					this.wordFreq.put(w, d + 1);
				}
			}
		}
		for (String w: words){
			tf_isf.put(w, tf_isf(w));
		}
		for (String w: this.words){
			if (!Main.global_nDocument.containsKey(w))
				Main.global_nDocument.put(w, 1);
			else{
				int d = Main.global_nDocument.get(w);
				Main.global_nDocument.put(w, d + 1);
			}
		}
	}
	public double tf_isf(String s){
		//Call after initiate all sentences!
		if (!this.words.contains(s))
			return 0;
		int tf = this.wordFreq.get(s);
		double isf = Math.log(Main.nwords/Main.global_nSentence.get(s));
		return (tf * isf);
	}
}
