package nlp1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Sentence {
	public HashMap<String, Integer> wordFreq;
	public HashSet<String> words;
	public HashMap<String, Double> tf_isf = new HashMap<String, Double>();
	public int offset;
	public int length;
	public Sentence(String s, int offset, int docType){
		//System.out.println(s);
		this.offset = offset;
		words = new HashSet<>();
		wordFreq = new HashMap<>();
		//StringTokenizer st = new StringTokenizer(s);
		String[] ss = s.split(",? +|\\/ ?| ?- ?| ?\\\\ ?|[^a-zA-Z0-9]|\n");
		Pattern email = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]");
		Pattern number = Pattern.compile("[0-9]+[.]*[0-9]+");
		Matcher m;
		for (int i = 0; i < ss.length; i++){
			/*m = number.matcher(ss[i]);
			if (m.matches()){
				if (docType == 0)
					Main.suspFeatures.addNumber(Double.valueOf(ss[i]), offset);
				else
					Main.srcFeatures.addNumber(Double.valueOf(ss[i]), offset);
			}
			m = email.matcher(ss[i]);
			if (m.matches()){
				if (docType == 0)
					Main.suspFeatures.addEmail(ss[i], offset);
				else
					Main.srcFeatures.addEmail(ss[i], offset);
			}*/
			if (ss[i].length() < 3) continue;
			if (Main.global_words.contains(ss[i])){
				int d = Main.global_wordFreq.get(ss[i]);
				Main.global_wordFreq.put(ss[i], d + 1);
			}
			else{
				Main.global_words.add(ss[i]);
				Main.global_wordFreq.put(ss[i], 1);
			}
			if (words.contains(ss[i])){
				int d = wordFreq.get(ss[i]);
				wordFreq.put(ss[i], d + 1);
			}
			else{
				words.add(ss[i]);
				wordFreq.put(ss[i], 1);
				if (!Main.global_nSentence.containsKey(ss[i]))
					Main.global_nSentence.put(ss[i], 0);
				int d = Main.global_nSentence.get(ss[i]);
				Main.global_nSentence.put(ss[i], d + 1);
			}
		}
		this.length = ss.length;
		Main.nwords += this.length;
		for (String w: words){
			tf_isf.put(w, tf_isf(w));
		}
	}
	public double tf_isf(String s){
		//Call after initiate all sentences!
		if (!wordFreq.containsKey(s))
			return 0;
		int tf = wordFreq.get(s);
		double isf = Math.log(Main.nwords/Main.global_nSentence.get(s));
		return (tf * isf);
	}
	public int getFreqByString(String s){
		for (String w: words)
		{
			if (w.contains(s))
			{
				return wordFreq.get(s);
			}
		}
		return 0;
	}
	public double vectorLength(){
		int sum = 0;
		for (String s: words){
			sum = sum + wordFreq.get(s)*wordFreq.get(s);
		}
		return Math.sqrt(sum);
	}
	public void print(){
		for (String s: words)
		{
			System.out.print(s.length() + "          ");
			System.out.println(s);
		}
		System.out.print("\n");
	}
}
