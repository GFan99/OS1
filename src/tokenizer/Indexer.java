package tokenizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class Indexer {
/**
 * fkt. addDocument --> frequenzmap übergeben + pfad zum dokument
 * --> dann index erstellen -> jedes set aus map nehmen, in globale map schreiben; index = termID
 * bekommt termliste und gibt map zurück, in der alle wörter (keys) mit ihrer termfrequenz als values
 */
	//Map mit DokID als Key und Source als Value
	private ArrayList<String> doklexikon; 
	private ArrayList<String> termlexikon;
	private ArrayList<String> indexliste;
	private ArrayList<String> sortliste;
	private int partcounter;
	private int sortpartcounter;

	public Indexer() {
		this.doklexikon = new ArrayList<String>();
		this.termlexikon = new ArrayList<String>();
		this.indexliste = new ArrayList<String>();
		this.sortliste = new ArrayList<String>();
		this.partcounter = 0;
		this.sortpartcounter = 0;
		this.start();
	}
	
	public void start() {
		File ordner = pfadNachOS("","");
		File[] dateien = ordner.listFiles();
		StringBuilder sb = new StringBuilder();
		try {
			for (int i =0;i<dateien.length;i++) {
			
				BufferedReader br = new BufferedReader(new FileReader(dateien[i]));
				String line = br.readLine();
				while (line != null) {
					sb.append(line).append("\n");
					line = br.readLine();
				}
				br.close();
				String fileAsString = sb.toString();
				List<String> list = Tokenizer.getWords(fileAsString);
				System.out.println("\n");
				System.out.println("datei"+i);
				System.out.println(list);
				TreeMap<String,Integer> tf = Tokenizer.termfrequenz(list);
				System.out.println("\n");
				System.out.println("datei"+i+" termfrequenz");
				System.out.println(tf);
				this.addDocument(dateien[i].getAbsolutePath(), tf);
			}
		}
		catch (Exception e) {e.printStackTrace();}
		this.sortpartfiles();
	}
	
	public void sortpartfiles() {
		ArrayList<String> tmplist = new ArrayList<String>();
		File ordner = pfadNachOS("tmp","");
		File[] dateien = ordner.listFiles();
		try {
			for (int i =0;i<dateien.length;i++) {
				BufferedReader br = new BufferedReader(new FileReader(dateien[i]));
				String line = br.readLine();
				while (line != null) {
					tmplist.add(line);
					line = br.readLine();
				}
				br.close();
			}
		}
		catch (Exception e) {e.printStackTrace();}
		ArrayList<ArrayList<String>> tmparray = new ArrayList<ArrayList<String>>();
		for(int i=0;i<doklexikon.size();i++) {
			ArrayList<String> newlist = new ArrayList<String>();
			tmparray.add(newlist);
		}
		for(int i=0;i<tmplist.size();i++) {
			String[] tmp = tmplist.get(i).split(",");
			tmparray.get(Integer.parseInt(tmp[1])).add(tmp[0]+","+tmp[1]+","+tmp[2]);
		}
		for(int i=0;i<tmparray.size();i++) {
			sortliste.addAll(tmparray.get(i));
		}
		writeSortedFile();
	}
	
	public void addDocument(String source, TreeMap<String,Integer> frequenz) {
		doklexikon.add(source);
		int dokid = doklexikon.size()-1;
		for (String key:frequenz.descendingKeySet()) {
			if (indexliste.size() >= 150) {
				writePartFile();
			}
			if (!termlexikon.contains(key)) {
				termlexikon.add(key);
			}
			int termid = termlexikon.indexOf(key);
			indexliste.add(termid + "," + dokid + "," + frequenz.get(key));
		}		
	}
	
	public void writePartFile() {
		partcounter++;
		try {
			Files.write(Paths.get(pfadNachOS("tmp","").getPath(),"partfile"+partcounter+".txt"), indexliste);
			indexliste.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public void writeSortedFile() {
		sortpartcounter++;
		ArrayList<String> print = new ArrayList<String>();
		while (sortliste.size()!=0) {
			int linecounter = 0;
			while (linecounter<150 && sortliste.size()!=0) {
				print.add(sortliste.remove(linecounter));
			}
			try {
				Files.write(Paths.get(pfadNachOS("sorted","").getPath(),"sortedfile"+sortpartcounter+".txt"), print);
				print.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public void writeLexika() {
		try {
			Files.write(Paths.get(pfadNachOS("Lexika","doks.txt").getPath()), doklexikon);
			Files.write(Paths.get(pfadNachOS("Lexika","terme.txt").getPath()), termlexikon);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public File pfadNachOS(String dateiname, String ordnername) {
		String osName = System.getProperty("os.name");
		if (osName.indexOf("Windows") != -1) {
			ordnername=ordnername+"//";
			String pfad = System.getProperty("user.dir");//user.dir ist workspace
			if (dateiname!="") {
				dateiname="//"+dateiname;
			}
			return new File(pfad+"//Texte//"+ordnername+dateiname);
		}
		else {
			ordnername=ordnername+"/";
			if (dateiname!="") {
				dateiname="/"+dateiname;
			}
			return new File("./Texte/"+ordnername+dateiname);
		}		
	}
}
