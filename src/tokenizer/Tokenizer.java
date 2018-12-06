package tokenizer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Beinhaltet die Methoden und Funktionen:
 *
 * 	getWords(String text) : List<String>
 * 	termfrequenz(List<String> worte) : TreeMap<String,Integer>
 *  mergeSort(List<String> whole) : List<String>
 * 
 * 
 * @author becksusanna
 *
 */
public class Tokenizer {
	
	//Woerter aus Dokument auslesen
	public static List<String> getWords(String text){
		text=text.trim();
		text=text.replaceAll("\\[[0-9]+\\]", "");
		text=text.replaceAll("[,\"():]", "");//Anfuehrungszeichen aus Text nehmen
		ArrayList<String> date = new ArrayList<String>();
		Pattern p = Pattern.compile("[0-9]{1,2}[.][ ][A-Za-zä]*[ ][0-9]{4}");
		Matcher m = p.matcher(text);
		int i=0;
		while(m.find()) {
			date.add(m.group(0));
		}
		
		text=text.replaceAll("[.]", "");
		text=text.replaceAll("([A-ZÄÖÜa-zäöüß ]+)(-)([A-ZÄÖÜa-zäöüß]+)", "$1$3");
		String[] words=text.split("[ ]");
		String[] alle=new String[words.length+date.size()];
		for(i=0;i<date.size();i++) {
			alle[i]=date.get(i);
		}
		for(i=0;i<words.length;i++) {
			alle[date.size()+i]=words[i];
		}
		return Arrays.asList(alle);
	}
	
	//Berechnung Termfrequenz
	public static TreeMap<String,Integer> termfrequenz(List<String> worte) {
		TreeMap<String,Integer> map = new TreeMap<String,Integer>();
		for(int i = 0;i<worte.size();i++) {
			final int x = i;
			map.put(worte.get(i), java.lang.Math.toIntExact(worte.stream().filter(p -> p.equals(worte.get(x))).count()));
		}
		return map;
	}

}