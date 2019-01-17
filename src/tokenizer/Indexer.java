package tokenizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	
	/**
	 * Muster für FileFilter
	 * 
	 * File testDirectory = new File("C://rootDir//");
	 * File[] files = testDirectory.listFiles(new FileFilter() {
	 *     @Override
	 *     public boolean accept(File pathname) {
	 *     		String name = pathname.getName().toLowerCase();
	 *          return name.endsWith(".xml") && pathname.isFile();
	 *     }
	 * });
	 */
	
	public void start() {
		File ordner = new File("../Texte");
		File[] dateien = ordner.listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File pathname) {
		        return pathname.isFile();
		    }
		});		
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
		this.globalSort();
	}
	
	public void sortpartfiles() {
		ArrayList<String> tmplist = new ArrayList<String>();
		File ordner = new File("../Texte/tmp");
		File[] dateien = ordner.listFiles();
		partcounter=0;
		try {
			for (int i =0;i<dateien.length;i++) {
				BufferedReader br = new BufferedReader(new FileReader(dateien[i]));
				File datei = dateien[i];
				String line = br.readLine();
				while (line != null) {
					tmplist.add(line);
					line = br.readLine();
				}
				br.close();
				Collections.sort(tmplist, new Comparator<String>() {

					@Override
					public int compare(String o1, String o2) {
						Integer i1 = Integer.parseInt(o1.split(",")[0]);
						Integer i2 = Integer.parseInt(o2.split(",")[0]);
						
						return i1.compareTo(i2);
					}
					
				});
				writePartFile(tmplist, datei);
				partcounter++;
			}
			
		}
		catch(Exception e) {}
	}
	
	public void globalSort() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("../Texte/sorted/sortfile.txt")));
			ArrayList<BufferedReader> brlist = new ArrayList<BufferedReader>();
			File ordner = new File("../Texte/tmp");
			File[] dateien = ordner.listFiles();
			for (int i =0;i<dateien.length;i++) {
				BufferedReader br = new BufferedReader(new FileReader(dateien[i]));
				brlist.add(br);	
			}
			String zeile;
			for(int i=0;i<termlexikon.size()-1;i++) {
				for(int j=0;j<brlist.size();j++) {
					BufferedReader brx=brlist.get(j);
					brx.mark(7);
					zeile=brx.readLine();
					while(zeile!=null && Integer.parseInt(zeile.split(",")[0]) == i) {
						bw.write(zeile);
						bw.newLine();
						brx.mark(7);
						zeile=brx.readLine();
					}
					brlist.get(j).reset();
				}
			}
			bw.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addDocument(String source, TreeMap<String,Integer> frequenz) {
		doklexikon.add(source);
		int dokid = doklexikon.size()-1;
		for (String key:frequenz.descendingKeySet()) {
			if (indexliste.size() >= 150) {
				writePartFile(indexliste,new File("../Texte/tmp/partfile"+partcounter+".txt"));
				partcounter++;
			}
			if (!termlexikon.contains(key)) {
				termlexikon.add(key);
			}
			int termid = termlexikon.indexOf(key);
			indexliste.add(termid + "," + dokid + "," + frequenz.get(key));
		}		
	}
	
	public void writePartFile(ArrayList<String> liste, File datei) {
		
		try {
			Files.write(Paths.get(datei.getAbsolutePath()), liste);
			liste.clear();
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
				Files.write(Paths.get("../Texte/sorted/sortedfile"+sortpartcounter+".txt"), print);
				print.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public void writeLexika() {
		try {
			Files.write(Paths.get("../Texte/Lexika/doks.txt"), doklexikon);
			Files.write(Paths.get("../Texte/Lexika/terme.txt"), termlexikon);
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
