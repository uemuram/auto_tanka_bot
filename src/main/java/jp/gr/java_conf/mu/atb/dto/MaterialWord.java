package jp.gr.java_conf.mu.atb.dto;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.InflectionAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.ReadingAttribute;

import jp.gr.java_conf.mu.atb.util.CommonUtil;

public class MaterialWord {

	// キーでマップした単語一覧
	private HashMap<String, ArrayList<Word>> materialWordMap;

	// 単純に並べた単語一覧
	private ArrayList<Word> materialWordList;

	// 単語と、その次に出現する単語のマッピング
	private HashMap<String, HashMap<String, Integer>> materialWordTransition;

	// 単語と、その次と、さらにその次に出現する単語のマッピング
	private HashMap<String, Integer> materialWordTransition2;

	// コンストラクタ(空データ)
	public MaterialWord() {
		this.materialWordMap = new HashMap<String, ArrayList<Word>>();
		this.materialWordList = new ArrayList<Word>();
		this.materialWordTransition2 = new HashMap<String, Integer>();
		this.materialWordTransition = new HashMap<String, HashMap<String, Integer>>();
	}

	// コンストラクタ(テキスト一覧を指定)
	public MaterialWord(ArrayList<String> textList) {
		this();

		// 全てのテキストを追加する
		for (String text : textList) {
			addMaterialWord(text);
		}
	}

	// word1->word2->word3の間の遷移の数を返す
	public int getTransitionCount2(Word word1, Word word2, Word word3) {
		if (word1 == null || word2 == null || word3 == null) {
			return 0;
		}

		String key = word1.getSerializedString() + "+" + word2.getSerializedString() + "+"
				+ word3.getSerializedString();
		Integer count = this.materialWordTransition2.get(key);
		if (count == null) {
			return 0;
		}
		return count;
	}

	// word1->word2の間の遷移の数を返す
	public int getTransitionCount(Word word1, Word word2) {
		if (word1 == null || word2 == null) {
			return 0;
		}

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

	// 素材を全て表示
	public void print1() {
		System.out.println("----------素材データ(単語一覧)----------");
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

	// 素材を全て表示
	public void print2() {
		System.out.println("----------素材データ(遷移一覧)----------");
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

	// 素材となった単語の個数を返す
	public int getCount() {
		return this.materialWordList.size();
	}

	// 単語をランダムで1つ返す
	public Word getRandomWord() {
		int size = this.materialWordList.size();
		return this.materialWordList.get(CommonUtil.random(size));
	}

	// 名詞(長さ2以上)をランダムで1つ返す。ただしexcludeで指定された単語は除外
	public Word getRandomNoum(String exclude) {
		ArrayList<Word> tmpWordList = new ArrayList<Word>();
		for (Word word : this.materialWordList) {
			String partOfSpeech = word.getPartOfSpeech();
			if (!word.getCharTerm().equals(exclude)
					&& (partOfSpeech.startsWith("名詞") || partOfSpeech.startsWith("固有名詞"))
					&& word.getReadingLength() >= 2 && !CommonUtil.isHankakuOnly(word.getCharTerm())) {
				tmpWordList.add(word);
			}
		}
		int size = tmpWordList.size();
		if (size == 0) {
			return null;
		}
		return tmpWordList.get(CommonUtil.random(size));
	}

	// n番目の単語を返す
	public Word getWord(int n) {
		int size = this.materialWordList.size();
		return this.materialWordList.get(n % size);
	}

	// テキストをもとにして素材を追加する
	private void addMaterialWord(String text) {
		System.out.println("【" + text + "】");
		// 読み込まれたトークンの一覧
		ArrayList<Word> wordList = new ArrayList<Word>();

		// ツイッターのユーザ名(@xx)を除外する
		text = text.replaceAll("@[a-zA-Z0-9_]+", " ");

		// 二重カウントを除去しながら読み込み(「東京」、「東京スカイツリー」、「スカイツリー」ー⇒「東京スカイツリー」のみにする)
		try (JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, false, JapaneseTokenizer.DEFAULT_MODE)) {
			tokenizer.setReader(new StringReader(text));
			PositionIncrementAttribute positionIncrementAttribute = tokenizer
					.addAttribute(PositionIncrementAttribute.class);
			PositionLengthAttribute positionLengthAttribute = tokenizer.addAttribute(PositionLengthAttribute.class);
			CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
			ReadingAttribute readingAttribute = tokenizer.addAttribute(ReadingAttribute.class);
			PartOfSpeechAttribute partOfSpeechAttribute = tokenizer.addAttribute(PartOfSpeechAttribute.class);
			InflectionAttribute inflectionAttribute = tokenizer.addAttribute(InflectionAttribute.class);
			tokenizer.reset();

			while (tokenizer.incrementToken()) {
				// ポジションが移動したか(移動したら1)
				int positionIncrement = positionIncrementAttribute.getPositionIncrement();
				// 今の単語が、次いくつ分の単語と重なっているか
				int positionLength = positionLengthAttribute.getPositionLength();
				// ポジション移動がなければ、前回の登録結果を捨てる(前回:「東京」、今回:「東京スカイツリー」
				// であれば、「東京」)を捨てる
				if (positionIncrement == 0) {
					wordList.remove(wordList.size() - 1);
				}

				Word word = new Word(charTermAttribute.toString(), readingAttribute.getReading(),
						partOfSpeechAttribute.getPartOfSpeech(), inflectionAttribute.getInflectionForm(),
						inflectionAttribute.getInflectionType());

				// 単語を記録
				wordList.add(word);

				// 単語の重なりをスキップ(今回:「東京スカイツリー」 、次回「スカイツリー」であれば、「スカイツリー」)を捨てる
				for (int i = 0; i < positionLength - 1; i++) {
					tokenizer.incrementToken();
				}
			}
		} catch (Exception e) {
			// エラー終了
			System.out.println("エラー:" + e.getMessage());
			return;
		}
		System.out.println("");

		// 素材を登録
		Word before2Word = null;
		Word beforeWord = null;
		Word currentWord = null;
		for (Word word : wordList) {
			before2Word = beforeWord;
			beforeWord = currentWord;
			currentWord = word;
			addMaterialWord(before2Word, beforeWord, currentWord);
		}

	}

	// 単語を素材として追加する
	private void addMaterialWord(Word before2Word, Word beforeWord, Word currentWord) {

		// ---------単語の前後関係を2つ登録---------
		if (before2Word != null && beforeWord != null) {
			String before2WordSerializedString = before2Word.getSerializedString();
			String beforeWordSerializedString = beforeWord.getSerializedString();
			String currentWordSerializedString = currentWord.getSerializedString();
			// 前後関係の数をインクリメント
			incrementMaterialWordTransition(before2WordSerializedString, beforeWordSerializedString,
					currentWordSerializedString);
		}

		// ---------単語の前後関係を1つ登録---------
		if (beforeWord != null) {
			String beforeWordSerializedString = beforeWord.getSerializedString();
			String currentWordSerializedString = currentWord.getSerializedString();
			// 前後関係の数をインクリメント
			incrementMaterialWordTransition(beforeWordSerializedString, currentWordSerializedString);
		}

		// ---------単語を1つ登録---------
		// 読みがない場合、絵文字の場合、記号の場合、「?」の場合はスキップ
		if (currentWord.getReading() == null || CommonUtil.isSurrogate(currentWord.getCharTerm())
				|| currentWord.getPartOfSpeech().startsWith("記号") || currentWord.getCharTerm().equals("?")) {
			return;
		}
		// 不適切な単語を除去
		if (currentWord.getCharTerm().equals("www")) {
			return;
		}
		// キーに紐づいた単語の一覧をとる
		ArrayList<Word> wordListWithKey = this.materialWordMap.get(currentWord.getKey());
		if (wordListWithKey == null) {
			wordListWithKey = new ArrayList<Word>();
		}
		currentWord.print();
		wordListWithKey.add(currentWord);
		this.materialWordMap.put(currentWord.getKey(), wordListWithKey);
		this.materialWordList.add(currentWord);
	}

	// 前後関係の数をインクリメントする
	private void incrementMaterialWordTransition(String before2WordSerializedString, String beforeWordSerializedString,
			String currentWordSerializedString) {

		String key = before2WordSerializedString + "+" + beforeWordSerializedString + "+" + currentWordSerializedString;
		Integer count = this.materialWordTransition2.get(key);
		if (count == null) {
			count = 0;
		}
		this.materialWordTransition2.put(key, count + 1);
	}

	// 前後関係の数をインクリメントする
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
