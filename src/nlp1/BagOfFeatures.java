package nlp1;

import java.util.ArrayList;
import java.util.HashMap;

public class BagOfFeatures {
	public HashMap<Double, ListOfSentences> NUMBERS = new HashMap<>();
	public HashMap<String, ListOfSentences> EMAILS = new HashMap<>();
	public BagOfFeatures(){
		
	}
	public void addEmail(String s, int t){
		if (EMAILS.containsKey(s)){
			ListOfSentences los = EMAILS.get(s);
			los.addSentence(t);
		}
		else{
			ListOfSentences l = new ListOfSentences();
			l.addSentence(t);
			EMAILS.put(s, l);
		}
	}
	public void addNumber(double s, int t){
		if (NUMBERS.containsKey(s)){
			ListOfSentences los = NUMBERS.get(s);
			los.addSentence(t);
		}
		else{
			ListOfSentences l = new ListOfSentences();
			l.addSentence(t);
			NUMBERS.put(s, l);
		}
	}
	public void print(){
		/*System.out.println("number: ");
		for (int i = 0; i < NUMBERS.size(); i++){
			System.out.print(NUMBERS.get(i).content + " ");
		}
		System.out.println("email: ");
		for (int i = 0; i < EMAILS.size(); i++){
			System.out.print(EMAILS.get(i).content + " ");
		}*/
	}
}
