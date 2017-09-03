package jp.gr.java_conf.mu.atb.dto;

public class Word {

	private String charTerm;
	private String reading;
	private int readingLength;
	private String partOfSpeech;
	private String inflectionForm;
	private String inflectionType;
	private String key;

	public Word() {
	}

	public Word(String charTerm, String reading, String partOfSpeech, String inflectionForm, String inflectionType) {
		this.charTerm = charTerm;
		this.reading = reading;
		this.partOfSpeech = partOfSpeech;
		this.inflectionForm = inflectionForm;
		this.inflectionType = inflectionType;

		// �ǂ񂾎��̕�����
		if (reading == null) {
			this.readingLength = 0;
		} else {
			// �������v�Z
			String tmpReading = reading;
			// �ǂ񂾂Ƃ��̕�������m�肽���̂ŁA�����������͏��O���ĕ������v�Z
			tmpReading = tmpReading.replaceAll("�@", "");
			tmpReading = tmpReading.replaceAll("�B", "");
			tmpReading = tmpReading.replaceAll("�D", "");
			tmpReading = tmpReading.replaceAll("�F", "");
			tmpReading = tmpReading.replaceAll("�H", "");
			tmpReading = tmpReading.replaceAll("��", "");
			tmpReading = tmpReading.replaceAll("��", "");
			tmpReading = tmpReading.replaceAll("��", "");
			this.readingLength = tmpReading.length();
		}

		// �L�[
		if (partOfSpeech.startsWith("����-")) {
			this.key = this.partOfSpeech + "," + this.inflectionForm + "," + this.inflectionType + "�y" + charTerm + "�z";
		} else {
			this.key = this.partOfSpeech + "," + this.inflectionForm + "," + this.inflectionType;
		}

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

}
