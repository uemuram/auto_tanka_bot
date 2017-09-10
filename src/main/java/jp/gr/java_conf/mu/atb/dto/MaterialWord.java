package jp.gr.java_conf.mu.atb.dto;

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

	// �P��ƁA���̎��ɏo������P��̃}�b�s���O
	private HashMap<String, HashMap<String, Integer>> materialWordTransition;

	// �R���X�g���N�^(��f�[�^)
	public MaterialWord() {
		this.materialWordMap = new HashMap<String, ArrayList<Word>>();
		this.materialWordList = new ArrayList<Word>();
		this.materialWordTransition = new HashMap<String, HashMap<String, Integer>>();
	}

	// �R���X�g���N�^(�e�L�X�g�ꗗ���w��)
	public MaterialWord(ArrayList<String> textList) {
		this();

		// �S�Ẵe�L�X�g��ǉ�����
		for (String text : textList) {
			addMaterialWord(text);
		}
	}

	// word1->word2�̊Ԃ̑J�ڂ̐���Ԃ�
	public int getTransitionCount(Word word1, Word word2) {
		HashMap<String, Integer> tmpHash1 = this.materialWordTransition.get(word1.getSerializedString());
		if (tmpHash1 == null) {
			return 0;
		}
		Integer count = tmpHash1.get(word2.getSerializedString());
		if (count == null) {
			return 0;
		}
		return count;
	}

	// �f�ނ�S�ĕ\��
	public void print1() {
		System.out.println("----------�f�ރf�[�^(�P��ꗗ)----------");
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

	// �f�ނ�S�ĕ\��
	public void print2() {
		System.out.println("----------�f�ރf�[�^(�J�ڈꗗ)----------");
		Object[] keys1 = this.materialWordTransition.keySet().toArray();
		Arrays.sort(keys1);
		for (int i = 0; i < keys1.length; i++) {
			String key1 = (String) keys1[i];
			HashMap<String, Integer> materialWordTransition = this.materialWordTransition.get(key1);
			Object[] keys2 = materialWordTransition.keySet().toArray();
			Arrays.sort(keys2);
			for (int j = 0; j < keys2.length; j++) {
				String key2 = (String) keys2[j];
				int count = materialWordTransition.get(key2);
				System.out.println(key1 + "\t->\t" + key2 + "\t" + count);
			}
		}
	}

	// �f�ނƂȂ����P��̌���Ԃ�
	public int getCount() {
		return this.materialWordList.size();
	}

	// �P��������_����1�Ԃ�
	public Word getRandomWord() {
		int size = this.materialWordList.size();
		return this.materialWordList.get(CommonUtil.random(size));
	}

	// n�Ԗڂ̒P���Ԃ�
	public Word getWord(int n) {
		int size = this.materialWordList.size();
		return this.materialWordList.get(n % size);
	}

	// �e�L�X�g�����Ƃɂ��đf�ނ�ǉ�����
	private void addMaterialWord(String text) {
		System.out.println("�y" + text + "�z");

		// �c�C�b�^�[�̃��[�U��(@xx)�����O����
		text = text.replaceAll("@[a-zA-Z0-9_]+", " ");

		Word beforeWord = null;
		Word currentWord = null;

		// �e�L�X�g��i������
		try (JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, false, JapaneseTokenizer.DEFAULT_MODE)) {
			tokenizer.setReader(new StringReader(text));
			CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
			ReadingAttribute readingAttribute = tokenizer.addAttribute(ReadingAttribute.class);
			PartOfSpeechAttribute partOfSpeechAttribute = tokenizer.addAttribute(PartOfSpeechAttribute.class);
			InflectionAttribute inflectionAttribute = tokenizer.addAttribute(InflectionAttribute.class);
			tokenizer.reset();
			while (tokenizer.incrementToken()) {
				// 1�O�̒P��
				beforeWord = currentWord;
				// ����̒P��
				currentWord = new Word(charTermAttribute.toString(), readingAttribute.getReading(),
						partOfSpeechAttribute.getPartOfSpeech(), inflectionAttribute.getInflectionForm(),
						inflectionAttribute.getInflectionType());
				// �f�ނ�o�^
				addMaterialWord(beforeWord, currentWord);
			}
		} catch (Exception e) {
			// �G���[�I��
			System.out.println("�G���[:" + e.getMessage());
			return;
		}
		System.out.println("");
	}

	// �P��ЂƂ�f�ނƂ��Ēǉ�����
	private void addMaterialWord(Word beforeWord, Word currentWord) {

		// ---------�P��̑O��֌W��o�^---------
		if (beforeWord != null) {
			String beforeWordSerializedString = beforeWord.getSerializedString();
			String currentWordSerializedString = currentWord.getSerializedString();
			// �O��֌W�̐����C���N�������g
			incrementMaterialWordTransition(beforeWordSerializedString, currentWordSerializedString);
		}

		// ---------�P���1�o�^---------
		// �ǂ݂��Ȃ��ꍇ�A�G�����̏ꍇ�A�L���̏ꍇ�A�u?�v�̏ꍇ�̓X�L�b�v
		if (currentWord.getReading() == null || CommonUtil.isSurrogate(currentWord.getCharTerm())
				|| currentWord.getPartOfSpeech().startsWith("�L��") || currentWord.getCharTerm().equals("?")) {
			return;
		}
		// �s�K�؂ȒP�������
		if (currentWord.getCharTerm().equals("www")) {
			return;
		}
		// �L�[�ɕR�Â����P��̈ꗗ���Ƃ�
		ArrayList<Word> wordListWithKey = this.materialWordMap.get(currentWord.getKey());
		if (wordListWithKey == null) {
			wordListWithKey = new ArrayList<Word>();
		}
		currentWord.print();
		wordListWithKey.add(currentWord);
		this.materialWordMap.put(currentWord.getKey(), wordListWithKey);
		this.materialWordList.add(currentWord);
	}

	// �O��֌W�̐����C���N�������g����
	private void incrementMaterialWordTransition(String beforeWordSerializedString,
			String currentWordSerializedString) {

		HashMap<String, Integer> tmpHash1 = this.materialWordTransition.get(beforeWordSerializedString);
		if (tmpHash1 == null) {
			tmpHash1 = new HashMap<String, Integer>();
			this.materialWordTransition.put(beforeWordSerializedString, tmpHash1);
		}

		Integer count = tmpHash1.get(currentWordSerializedString);
		if (count == null) {
			count = 0;
		}
		tmpHash1.put(currentWordSerializedString, count + 1);
	}

}
