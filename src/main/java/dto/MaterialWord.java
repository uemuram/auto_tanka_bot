package dto;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.InflectionAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.ReadingAttribute;

import jp.gr.java_conf.mu.atb.util.CommonUtil;

public class MaterialWord {

	// �L�[�Ń}�b�v�����P��ꗗ
	private HashMap<String, ArrayList<Word>> materialWordMap;

	// �P���ɕ��ׂ��P��ꗗ
	private ArrayList<Word> materialWordList;

	// �R���X�g���N�^(��f�[�^)
	public MaterialWord() {
		this.materialWordMap = new HashMap<String, ArrayList<Word>>();
	}

	// �R���X�g���N�^(�e�L�X�g�ꗗ���w��)
	public MaterialWord(ArrayList<String> textList) {
		this.materialWordMap = new HashMap<String, ArrayList<Word>>();
		this.materialWordList = new ArrayList<Word>();

		// �S�Ẵe�L�X�g��ǉ�����
		for (String text : textList) {
			addMaterialWord(text);
		}
	}

	// �f�ނ�S�ĕ\��
	public void print() {
		Object[] keys1 = this.materialWordMap.keySet().toArray();
		Arrays.sort(keys1);
		for (int i = 0; i < keys1.length; i++) {
			String key1 = (String) keys1[i];
			ArrayList<Word> materialWordList = materialWordMap.get(key1);
			for (Word tmpWord : materialWordList) {
				System.out.println(key1 + "\t" + tmpWord.getCharTerm() + "\t" + tmpWord.getReading() + "\t"
						+ tmpWord.getReadingLength());
			}
		}
	}

	// �P��������_����1�Ԃ�
	public Word getRandomWord() {
		int size = this.materialWordList.size();
		return this.materialWordList.get(CommonUtil.random(size));
	}

	// �e�L�X�g�����Ƃɂ��đf�ނ�ǉ�����
	private void addMaterialWord(String text) {
		System.out.println("�y" + text + "�z");

		// �c�C�b�^�[�̃��[�U��(@xx)�����O����
		text = text.replaceAll("@[a-zA-Z0-9_]", " ");

		// �e�L�X�g��i������
		try (JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, false, JapaneseTokenizer.DEFAULT_MODE)) {
			tokenizer.setReader(new StringReader(text));
			CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
			ReadingAttribute readingAttribute = tokenizer.addAttribute(ReadingAttribute.class);
			PartOfSpeechAttribute partOfSpeechAttribute = tokenizer.addAttribute(PartOfSpeechAttribute.class);
			InflectionAttribute inflectionAttribute = tokenizer.addAttribute(InflectionAttribute.class);
			tokenizer.reset();
			while (tokenizer.incrementToken()) {
				addMaterialWord(charTermAttribute.toString(), readingAttribute.getReading(),
						partOfSpeechAttribute.getPartOfSpeech(), inflectionAttribute.getInflectionForm(),
						inflectionAttribute.getInflectionType());
			}
		} catch (Exception e) {
			// �G���[�I��
			System.out.println("�G���[:" + e.getMessage());
			return;
		}
		System.out.println("");
	}

	// �P��ЂƂ�f�ނƂ��Ēǉ�����
	private void addMaterialWord(String charTerm, String reading, String partOfSpeech, String inflectionForm,
			String inflectionType) {
		// �ǂ݂��Ȃ��ꍇ�A�G�����̏ꍇ�A�L���̏ꍇ�̓X�L�b�v
		if (reading == null || CommonUtil.isSurrogate(charTerm) || partOfSpeech.startsWith("�L��")) {
			return;
		}
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
		int readingLength = tmpReading.length();

		String key = partOfSpeech + "," + inflectionForm + "," + inflectionType;
		System.out.println(charTerm + "\t" + reading + "(" + readingLength + ")" + "," + partOfSpeech + ","
				+ inflectionForm + "," + inflectionType + "\t<" + key + ">");

		// �L�[�ɕR�Â����P��̈ꗗ���Ƃ�
		ArrayList<Word> materialWordList = this.materialWordMap.get(key);
		if (materialWordList == null) {
			materialWordList = new ArrayList<Word>();
		}

		Word tmpWord = new Word();
		tmpWord.setCharTerm(charTerm);
		tmpWord.setReading(reading);
		tmpWord.setReadingLength(readingLength);
		tmpWord.setPartOfSpeech(partOfSpeech);
		tmpWord.setInflectionForm(inflectionForm);
		tmpWord.setInflectionType(inflectionType);

		materialWordList.add(tmpWord);

		this.materialWordList.add(tmpWord);
		this.materialWordMap.put(key, materialWordList);
	}

}
