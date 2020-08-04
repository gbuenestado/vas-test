package com.mcp.test.dto;

import java.io.Serializable;
import java.util.Comparator;

public class WordDto implements Serializable {

	private static final long serialVersionUID = 1912035260343928993L;

	private String word;
	private Integer occurrences;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Integer getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(Integer occurrences) {
		this.occurrences = occurrences;
	}
	
	public static final Comparator<WordDto> comparator = new Comparator<WordDto>() {
		public int compare(WordDto w1, WordDto w2) {
			return w1.getOccurrences().compareTo(w2.getOccurrences());
		}
	};
	
}
