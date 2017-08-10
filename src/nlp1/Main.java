package nlp1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {
	public static String srcFileName;
	public static String suspFileName;
	public static String srcDirPath = "srcDoc\\";
	public static String suspDirPath = "suspDoc\\";
	public static ArrayList<Document> srcDocument = new ArrayList<Document>();
	public static ArrayList<Document> suspDocument = new ArrayList<Document>();
	public static ArrayList<SeedingPair> seeds = new ArrayList<SeedingPair>();
	public static ArrayList<FragmentPair> Fragments = new ArrayList<FragmentPair>();
	public static ArrayList<FragmentPair> Fragmentss = new ArrayList<FragmentPair>();
	public static HashSet<String> global_words = new HashSet<String>();
	public static HashMap<String, Integer> global_wordFreq = new HashMap<String, Integer>();
	public static HashMap<String, Integer> global_nSentence = new HashMap<String, Integer>();
	public static HashMap<String, Integer> global_nDocument = new HashMap<String, Integer>();
	public static HashMap<String, Double> global_tfidf = new HashMap<String, Double>();
	public static HashSet<String> stoplist = new HashSet<String>();
	public static Document[] documents = new Document[5000];
	public static BagOfFeatures srcFeatures = new BagOfFeatures();
	public static BagOfFeatures suspFeatures = new BagOfFeatures();
	public static int nwords;
	public static int parseTime;
	public static int seedTime;
	public static int extendTime;
	public static int removeOverlapTime;
	//public static BagOfFeatures bag = new BagOfFeatures();
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		
		long time1 = System.currentTimeMillis();
		
		parse();
		/*Document d1 = new Document(0, "Me nau com toi roi vien.");
		d1.calculateThings();
		Document d2 = new Document(1, "Me di choi roi vien.");
		d2.calculateThings();
		//System.out.println(Measures.similarity(d1, d2));
		LevenshteinDistance ld = new LevenshteinDistance();
		System.out.println(ld.levenshteinDistanceImprovement(d2.wordered, d1.wordered));*/
		
		/*System.out.println("parse: " + parseTime);
		System.out.println("seed: " + seedTime);
		System.out.println("extend: " + extendTime);
		System.out.println("rmOverlap: " + removeOverlapTime);*/
	}
	public static void removeOverlap() throws FileNotFoundException, UnsupportedEncodingException{
		int[] mark = new int[Fragments.size()];
		for (int i = 0; i < Fragments.size(); i++){
			if (mark[i] == 1) continue;
			int IsrcBegin = Fragments.get(i).srcBegin;
			int IsrcEnd = Fragments.get(i).srcEnd;
			int IsuspBegin = Fragments.get(i).suspBegin;
			int IsuspEnd = Fragments.get(i).suspEnd;
			Document srcI = srcDocument.get(0).getDocument(IsrcBegin, IsrcEnd);
			double Qmax = 0;
			int argMax = i;
			for (int j = i + 1; j < Fragments.size(); j++){
				int JsrcBegin = Fragments.get(j).srcBegin;
				int JsrcEnd = Fragments.get(j).srcEnd;
				int JsuspBegin = Fragments.get(j).suspBegin;
				int JsuspEnd = Fragments.get(j).suspEnd;
				Document srcJ = srcDocument.get(0).getDocument(JsrcBegin, JsrcEnd);
				if ((JsuspBegin <= IsuspEnd && IsuspBegin < JsuspEnd)|| (IsuspBegin <= JsuspEnd && JsuspBegin < IsuspEnd)){
					//Overlap!
					mark[j] = 1;
					Document overlapDocumentIJ;
					Document frontNonOverlapIJ;
					Document rearNonOverlapIJ;
					Document nonOverlapIJ;
					if (JsuspBegin <= IsuspEnd){
						//System.out.println("Holo");
						overlapDocumentIJ = suspDocument.get(0).getDocument(JsuspBegin, IsuspEnd);
						frontNonOverlapIJ = suspDocument.get(0).getDocument(IsuspBegin, JsuspBegin);
						rearNonOverlapIJ = suspDocument.get(0).getDocument(IsuspEnd, JsuspEnd);
						nonOverlapIJ = Document.merge(frontNonOverlapIJ, rearNonOverlapIJ);
					}
					else{
						overlapDocumentIJ = suspDocument.get(0).getDocument(IsuspBegin, JsuspEnd);
						frontNonOverlapIJ = suspDocument.get(0).getDocument(JsuspBegin, IsuspBegin);
						rearNonOverlapIJ = suspDocument.get(0).getDocument(JsuspEnd, IsuspEnd);
						nonOverlapIJ = Document.merge(frontNonOverlapIJ, rearNonOverlapIJ);
					}
					srcI.calculateThings();
					overlapDocumentIJ.calculateThings();
					nonOverlapIJ.calculateThings();
					double simOverlapIJ = Measures.similarity(srcI, overlapDocumentIJ);
					double simNonOverlapIJ = Measures.similarity(srcI, nonOverlapIJ);
					double Qij = simOverlapIJ + (1 - simOverlapIJ) * simNonOverlapIJ;
					if (Qij > Qmax){
						Qmax = Qij;
						argMax = i;
					}
					srcJ.calculateThings();
					double simOverlapJI = Measures.similarity(srcJ, overlapDocumentIJ);
					double simNonOverlapJI = Measures.similarity(srcJ, nonOverlapIJ);
					double Qji = simOverlapJI + (1 - simOverlapJI) * simNonOverlapJI;
					if (Qji > Qmax){
						Qmax = Qji;
						argMax = j;
					}
				}
			}
			Fragmentss.add(Fragments.get(argMax));
			//Collect O(P)
		}
		int i = 0;
		for (FragmentPair fp: Fragmentss){
			i++;
			String s = String.valueOf(i); 
			//System.out.println("srcBegin: " + fp.srcBegin + "srcEnd: " + fp.srcEnd);
			int suspOffset, srcOffset, suspLength, srcLength;
			suspOffset = suspDocument.get(0).sentences.get(fp.suspBegin).offset;
			srcOffset = srcDocument.get(0).sentences.get(fp.srcBegin).offset;
			if (fp.suspEnd < suspDocument.get(0).sentences.size() - 1)
				suspLength = suspDocument.get(0).sentences.get(fp.suspEnd + 1).offset - suspOffset;
			else suspLength = suspDocument.get(0).endOfDocument - suspOffset + 1;
			if (fp.srcEnd < srcDocument.get(0).sentences.size() - 1)
				srcLength = srcDocument.get(0).sentences.get(fp.srcEnd + 1).offset - srcOffset;
			else srcLength = srcDocument.get(0).endOfDocument - srcOffset + 1;
			if (srcLength < Constants.MinimumFragmentLength || suspLength < Constants.MinimumFragmentLength) continue;
			writeToFile(suspOffset, srcOffset, suspLength - 1, srcLength - 1, fp.sim);
		}
	}
	public static void writeToFile(int suspOffset, int srcOffset, int suspLength, int srcLength, double sim) throws UnsupportedEncodingException, FileNotFoundException{
		boolean folder = new File("detected").mkdirs();
		String fileName = "detected\\" + suspFileName + "-" + srcFileName + ".xml";
		PrintWriter pw = new PrintWriter(fileName, "UTF-8");
		System.out.println("Hi!");
		pw.println("<document reference=\"" + suspFileName + "\".txt>");
		pw.println("<feature");
		pw.println("name=\"detected-plagiarism\"");
		pw.println("this-offset=\"" + suspOffset + "\"");
		pw.println("this-length=\"" + suspLength + "\"");
		pw.println("source-reference=\"" + srcFileName + ".txt\"");
		pw.println("source-offset=\"" + srcOffset + "\"");
		pw.println("source-length=\"" + srcLength + "\"");
		pw.println("/>");
		pw.println("</document>");
		pw.println("sim = " + sim);
		pw.close();
	}
	public static void parse() throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		File stoplist = new File("stoplist.txt");
		Scanner sc_stoplist = new Scanner(stoplist);
		while (sc_stoplist.hasNext()){
			String w = sc_stoplist.next();
			Main.stoplist.add(w);
			System.out.println(w);
		}
		sc_stoplist.close();
		PrintWriter pw = new PrintWriter("kqsrc.txt", "UTF-8");
		for (int i = 1; i <= 3230; i++){
			String fileNumber = String.valueOf(i);
			while (fileNumber.length() < 5) fileNumber = "0" + fileNumber;
			String filePath = srcDirPath + "source-document" + fileNumber + ".txt";
			//System.out.println(filePath);
			File srcFile = new File(filePath);
			Scanner sc = new Scanner(srcFile);
			String ss = "";
			while (sc.hasNextLine()){
				ss = ss + sc.nextLine();
				ss = ss + '\n';
			}
			sc.close();
			Document d = new Document(0, ss);
			documents[i] = d;
			d.calculateThings();
			for (String w: d.words){
				//System.out.print(w +".");
				pw.print(w);
				pw.print(" ");
			}
			pw.println();
			System.out.println("Done the " + i + "-th document!");
		}
		pw.flush();
		pw.close();
		pw = new PrintWriter("kqsusp.txt", "UTF-8");
		  for (int i = 1; i <= 1827; i++){
			String fileNumber = String.valueOf(i);
			while (fileNumber.length() < 5) fileNumber = "0" + fileNumber;
			String filePath = suspDirPath + "suspicious-document" + fileNumber + ".txt";
			//System.out.println(filePath);
			File srcFile = new File(filePath);
			Scanner sc = new Scanner(srcFile);
			String ss = "";
			while (sc.hasNextLine()){
				ss = ss + sc.nextLine();
				ss = ss + '\n';
			}
			sc.close();
			Document d = new Document(0, ss);
			d.calculateThings();
			for (String w: d.words){
				System.out.print("Content:.........." + w +".");
				pw.print(w);
				pw.print(" ");
			}
			pw.println();
			System.out.println("Done the " + i + "-th document!" );
		}
		pw.flush();
		pw.close();
		pw = new PrintWriter("kqsrc2.txt");
		int k = 0;
		for (int i = 1; i <= 3230; i++){
			for (String w: documents[i].words){
				int tf = documents[i].wordFreq.get(w);
				double idf = 1/(1 + Math.log(global_nDocument.get(w)));
				if (Main.stoplist.contains(w)) continue;
				if (tf*idf > Constants.tfidfThreshold){ 
					k++;
					pw.print(w + " ");
				}
			}
			pw.println();
			System.out.println("There are " + k + " words with tf_idf >= threshold and not a stop word.");
			System.out.println("There are " + documents[i].words.size() + " words in this document.");
		}
		
		/*File fpair = new File("pairs");
		Scanner scPair = new Scanner(fpair);
		while (scPair.hasNextLine()){
			long time = System.currentTimeMillis();
			String s = scPair.nextLine();
			suspFileName = s.substring(0, 28);
			srcFileName = s.substring(29, 53);
			File suspFile = new File(suspDirPath + suspFileName);
			Scanner sc = new Scanner(suspFile);
			String ss = "";
			while (sc.hasNextLine()){
				ss = ss + sc.nextLine();
				ss = ss + '\n';
			}
			sc.close();
			suspDocument.add(new Document(0, ss));
			
			File srcFile = new File(srcDirPath + srcFileName);
			ss = "";
			sc = new Scanner(srcFile);
			while (sc.hasNextLine()){
				ss = ss + sc.nextLine();
				ss = ss + '\n';
			}
			srcDocument.add(new Document(1, ss));
			sc.close();
			long ttime = System.currentTimeMillis();
			parseTime += (ttime - time);
			seed2();
			time = System.currentTimeMillis();
			seedTime += (time - ttime);
			extend(seeds, 2);
			ttime = System.currentTimeMillis();
			extendTime += (ttime - time);
			removeOverlap();
			time = System.currentTimeMillis();
			removeOverlapTime += (time - ttime);
			reset();
		}
		scPair.close();*/
	}
	public static void reset(){
		srcDocument.clear();
		suspDocument.clear();
		seeds.clear();
		Fragments.clear();
		Fragmentss.clear();
	    global_nSentence = new HashMap<String, Integer>();
	    global_wordFreq = new HashMap<String, Integer>();
	    global_words = new HashSet<String>();
	    nwords = 0;
	}
	public static void extend(ArrayList<SeedingPair> seeds, int maxGap){
		ArrayList<SeedingPair> srcBlock = new ArrayList<SeedingPair>();
		ArrayList<ArrayList<SeedingPair>> blockOfSrcBlock = 
				new ArrayList<ArrayList<SeedingPair>>();
		Collections.sort(seeds, new Comparator<SeedingPair>(){
			@Override
			public int compare(SeedingPair o1, SeedingPair o2) {
				// TODO Auto-generated method stub
				if (o1.src > o2.src) return 1;
				if (o1.src == o2.src) return 0;
				return -1;
			}
		});
		for (SeedingPair sp: seeds){
			if (srcBlock.isEmpty()){
				srcBlock.add(sp);
			}
			else{
				SeedingPair spEnd = srcBlock.get(srcBlock.size() - 1);
				if (sp.src - spEnd.src <= maxGap){
					srcBlock.add(sp);
				}
				else{
					if (srcBlock.size() >= Constants.SrcSize){
						blockOfSrcBlock.add((ArrayList<SeedingPair>) srcBlock.clone());
					}
					srcBlock.clear();
					srcBlock.add(sp);
				}
			}
		}
		if (srcBlock.size() >= Constants.SrcSize){
			blockOfSrcBlock.add(srcBlock);
		}
		ArrayList<ArrayList<SeedingPair>> finalBlock = new ArrayList<ArrayList<SeedingPair>>();
		for (ArrayList<SeedingPair> sBlock: blockOfSrcBlock){
			Collections.sort(sBlock, new Comparator<SeedingPair>(){
				@Override
				public int compare(SeedingPair o1, SeedingPair o2) {
					// TODO Auto-generated method stub
					if (o1.susp > o2.susp) return 1;
					if (o1.susp == o2.susp) return 0;
					return -1;
				}
			});
			ArrayList<SeedingPair> suspBlock = new ArrayList<SeedingPair>();
			for (SeedingPair sp: sBlock){
				if (suspBlock.isEmpty()){
					suspBlock.add(sp);
				}
				else{
					SeedingPair spEnd = suspBlock.get(suspBlock.size() - 1);
					if (sp.susp - spEnd.susp <= maxGap){
						suspBlock.add(sp);
					}
					else{
						if (suspBlock.size() >= Constants.SuspSize){
							finalBlock.add((ArrayList<SeedingPair>) suspBlock.clone());
						}
						suspBlock.clear();
						suspBlock.add(sp);
					}
				}
			}
			if (suspBlock.size() >= Constants.SuspSize){
				finalBlock.add(suspBlock);
			}
		}
		for (ArrayList<SeedingPair> sBlock: finalBlock){
			//System.out.println("sBlock size: " + sBlock.size());
			/*for (int i = 0; i < sBlock.size(); i++){
				System.out.println(sBlock.get(i).src + " " + sBlock.get(i).susp);
			}*/
			int MinSrc = 1000000;
			int MaxSrc = 0;
			for (int i = 0; i < sBlock.size(); i++){
				if (sBlock.get(i).src < MinSrc)
					MinSrc = sBlock.get(i).src;
				if (sBlock.get(i).src > MaxSrc){
					MaxSrc = sBlock.get(i).src;
				}
			}
			//System.out.println("MinSrc = " + MinSrc + " MaxSrc = " + MaxSrc);
			int MinSusp = sBlock.get(0).susp;
			int MaxSusp = sBlock.get(sBlock.size() - 1).susp;
			Document src = srcDocument.get(0).getDocument(MinSrc, MaxSrc);
			Document susp = suspDocument.get(0).getDocument(MinSusp, MaxSusp);
			src.calculateThings();
			susp.calculateThings();
			//System.out.println(Measures.similarity(src, susp));
			double sim = Measures.similarity(src, susp);
			if (Measures.similarity(src, susp) < Constants.SimThreshold){
				if (maxGap > Constants.MaxGapLeast){
					extend(sBlock, maxGap - 1);
				}
			}
			else{
				Fragments.add(new FragmentPair(MinSrc, MaxSrc, MinSusp, MaxSusp, sim));
			}
		}
	}
	public static void seed(){
		for (int k = 5; k < 6; k++){
			for (int i = 0; i < srcDocument.get(k).sentences.size(); i++){
				for (int j = 0; j < suspDocument.get(k).sentences.size(); j++){
					double cos = Measures.cos(srcDocument.get(k).sentences.get(i),
							suspDocument.get(k).sentences.get(j));
					double dice = Measures.dice(srcDocument.get(k).sentences.get(i),
							suspDocument.get(k).sentences.get(j));
					if (cos > Constants.CosineThreshold && dice > Constants.DiceThreshold)
						seeds.add(new SeedingPair(i, j, k));
					
				}
			}
		}
	}
	public static void seed2(){
		boolean[] flag = new boolean[5000];
		for (int i = 0; i < suspDocument.get(0).sentences.size(); i++){
			Sentence suspSentence = suspDocument.get(0).sentences.get(i);
			for (int j = 0; j < srcDocument.get(0).sentences.size(); j++){
				Sentence srcSentence = srcDocument.get(0).sentences.get(j);
				if (isTwoSentenceSimilar(suspSentence, srcSentence))
					seeds.add(new SeedingPair(j, i, 1));
			}
		}
	}
	public static boolean isTwoSentenceSimilar(Sentence i, Sentence j){
		double dice = Measures.dice(i, j);
		if (dice <= Constants.DiceThreshold) return false;
		double cos = Measures.cos(i, j);
		if (cos > Constants.CosineThreshold)
			return true;
		return false;
	}
}
