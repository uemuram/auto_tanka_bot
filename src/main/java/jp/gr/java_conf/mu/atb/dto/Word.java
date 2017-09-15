package jp.gr.java_conf.mu.atb.dto;

public class Word {

	private String charTerm;
	private String reading;
	private int readingLength;
	private String partOfSpeech;
	private String inflectionForm;
	private String inflectionType;
	private String key;
	private String serializedString;

	public Word() {
	}

	public Word(String charTerm, String reading, String partOfSpeech, String inflectionForm, String inflectionType) {
		this.charTerm = charTerm;
		this.reading = reading;
		this.partOfSpeech = partOfSpeech;
		this.inflectionForm = inflectionForm;
		this.inflectionType = inflectionType;

		// 読んだ時の文字長
		if (reading == null) {
			this.readingLength = 0;
		} else {
			// 文字長計算
			String tmpReading = reading;
			// 読んだときの文字数を知りたいので、小さい文字は除外して文字帳計算
			tmpReading = tmpReading.replaceAll("ァ", "");
			tmpReading = tmpReading.replaceAll("ィ", "");
			tmpReading = tmpReading.replaceAll("ゥ", "");
			tmpReading = tmpReading.replaceAll("ェ", "");
			tmpReading = tmpReading.replaceAll("ォ", "");
			tmpReading = tmpReading.replaceAll("ャ", "");
			tmpReading = tmpReading.replaceAll("ュ", "");
			tmpReading = tmpReading.replaceAll("ョ", "");
			this.readingLength = tmpReading.length();
		}

		// キー
		if (partOfSpeech.startsWith("助詞-")) {
			this.key = this.partOfSpeech + "," + this.inflectionForm + "," + this.inflectionType + "【" + charTerm + "】";
		} else {
			this.key = this.partOfSpeech + "," + this.inflectionForm + "," + this.inflectionType;
		}

		// 直列化文字列
		this.serializedString = this.partOfSpeech + "," + this.inflectionForm + "," + this.inflectionType + "【"
				+ charTerm + "】";
	}

	public Word(String key) {
		this("", "", "", "", "");
		this.key = key;
	}

	public void print() {
		System.out.println(this.charTerm + "\t" + this.reading + "(" + this.readingLength + ")" + ","
				+ this.partOfSpeech + "," + this.inflectionForm + "," + this.inflectionType + "\t<" + this.key + ">");
	}

	public String toString() {
		return this.charTerm;
	}

	public String getKey() {
		return this.key;
	}

	public String getCharTerm() {
		return charTerm;
	}

	public String getReading() {
		return reading;
	}

	public int getReadingLength() {
		return readingLength;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public String getInflectionForm() {
		return inflectionForm;
	}

	public String getInflectionType() {
		return inflectionType;
	}

	public String getSerializedString() {
		return serializedString;
	}
}
