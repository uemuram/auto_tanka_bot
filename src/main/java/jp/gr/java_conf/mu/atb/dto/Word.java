package jp.gr.java_conf.mu.atb.dto;

public class Word {

	private String charTerm;
	private String reading;
	private int readingLength;
	private String partOfSpeech;
	private String inflectionForm;
	private String inflectionType;

	public Word() {
	}

	public void print() {
		String key = this.partOfSpeech + "," + this.inflectionForm + "," + this.inflectionType;
		System.out.println(this.charTerm + "\t" + this.reading + "(" + this.readingLength + ")" + ","
				+ this.partOfSpeech + "," + this.inflectionForm + "," + this.inflectionType + "\t<" + key + ">");
	}

	public String getKey() {
		return partOfSpeech + "," + inflectionForm + "," + inflectionType;
	}

	public String getCharTerm() {
		return charTerm;
	}

	public void setCharTerm(String charTerm) {
		this.charTerm = charTerm;
	}

	public String getReading() {
		return reading;
	}

	public void setReading(String reading) {
		this.reading = reading;
	}

	public int getReadingLength() {
		return readingLength;
	}

	public void setReadingLength(int readingLength) {
		this.readingLength = readingLength;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	public String getInflectionForm() {
		return inflectionForm;
	}

	public void setInflectionForm(String inflectionForm) {
		this.inflectionForm = inflectionForm;
	}

	public String getInflectionType() {
		return inflectionType;
	}

	public void setInflectionType(String inflectionType) {
		this.inflectionType = inflectionType;
	}

}
