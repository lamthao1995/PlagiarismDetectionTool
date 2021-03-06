package nlp1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class Measures {
	public static double cos(Sentence st1, Sentence st2)
	{
		double res = 0;
		double st1_length = 0;
		double st2_length = 0;
		for (String w: st1.words){
			st1_length = st1_length + Math.pow(st1.tf_isf.get(w), 2);
		}
		for (String w: st2.words){
			st2_length = st2_length + Math.pow(st2.tf_isf.get(w), 2);
		}
		st1_length = Math.sqrt(st1_length);
		st2_length = Math.sqrt(st2_length);
		for (String w: st1.words){
			if (st2.words.contains(w)){
				res = res + st1.tf_isf.get(w) * st2.tf_isf.get(w);
			}
		}
		res = res/st1_length/st2_length;
		return res;		
	}
	public static double dice(Sentence st1, Sentence st2)
	{
		double res = 0;
		for (String w: st1.words){
			if (st2.words.contains(w)){
				res = res + 2;
			}
		}
		res = res/(st1.length + st2.length);
		return res;
	}
	public static double similarity(Document d1, Document d2)
	{
		//System.out.println("KKKKKKKKK");
		double res = 0;
		double d1_length = 0;
		double d2_length = 0;
		for (String w: d1.words){
			System.out.println("H!");
			d1_length = d1_length + Math.pow(d1.tf_isf.get(w), 2);
		}
		for (String w: d2.words){
			d2_length = d2_length + Math.pow(d2.tf_isf.get(w), 2);
		}
		d1_length = Math.sqrt(d1_length);
		d2_length = Math.sqrt(d2_length);
		for (String w: d1.words){
			if (d2.words.contains(w)){
				res = res + d1.tf_isf.get(w) * d2.tf_isf.get(w);
			}
		}
		System.out.println("d1 length: " + d1_length);
		System.out.println("d2 length: " + d2_length);
		res = res/d1_length/d2_length;
		return res;
		
	}
	public static double usymSimilarity(Document suspDoc, Document srcDoc){
		int suspLength = suspDoc.sentences.size();
		double res = 0;
		for (Sentence srcSentence: srcDoc.sentences){
			double s = 0;
			double t;
			for (Sentence suspSentence: suspDoc.sentences){
				t = Measures.cos(srcSentence, suspSentence);
				if (t > s)
					s = t;
			}
			res = res + s;
		}
		res = res/suspLength;
		return res;
	}
}
