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

	// キーでマップした単語一覧
	private HashMap<String, ArrayList<Word>> materialWordMap;

	// 単純に並べた単語一覧
	private ArrayList<Word> materialWordList;

	// コンストラクタ(空データ)
	public MaterialWord() {
		this.materialWordMap = new HashMap<String, ArrayList<Word>>();
	}

	// コンストラクタ(テキスト一覧を指定)
	public MaterialWord(ArrayList<String> textList) {
		this.materialWordMap = new HashMap<String, ArrayList<Word>>();
		this.materialWordList = new ArrayList<Word>();

		// 全てのテキストを追加する
		for (String text : textList) {
			addMaterialWord(text);
		}
	}

	// 素材を全て表示
	public void print() {
		System.out.println("----------素材データ----------");
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

	// 素材となった単語の個数を返す
	public int getCount() {
		return this.materialWordList.size();
	}

	// 単語をランダムで1つ返す
	public Word getRandomWord() {
		int size = this.materialWordList.size();
		return this.materialWordList.get(CommonUtil.random(size));
	}

	// テキストをもとにして素材を追加する
	private void addMaterialWord(String text) {
		System.out.println("【" + text + "】");

		// ツイッターのユーザ名(@xx)を除外する
		text = text.replaceAll("@[a-zA-Z0-9_]+", " ");

		Word beforeWord = null;
		Word currentWord = null;

		// テキストを品詞分解
		try (JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, false, JapaneseTokenizer.DEFAULT_MODE)) {
			tokenizer.setReader(new StringReader(text));
			CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
			ReadingAttribute readingAttribute = tokenizer.addAttribute(ReadingAttribute.class);
			PartOfSpeechAttribute partOfSpeechAttribute = tokenizer.addAttribute(PartOfSpeechAttribute.class);
			InflectionAttribute inflectionAttribute = tokenizer.addAttribute(InflectionAttribute.class);
			tokenizer.reset();
			while (tokenizer.incrementToken()) {
				// 1つ前の単語
				beforeWord = currentWord;
				// 今回の単語
				currentWord = new Word(charTermAttribute.toString(), readingAttribute.getReading(),
						partOfSpeechAttribute.getPartOfSpeech(), inflectionAttribute.getInflectionForm(),
						inflectionAttribute.getInflectionType());

				addMaterialWord(currentWord);

			}
		} catch (Exception e) {
			// エラー終了
			System.out.println("エラー:" + e.getMessage());
			return;
		}
		System.out.println("");
	}

	// 単語ひとつを素材として追加する
	private void addMaterialWord(Word word) {
		// 読みがない場合、絵文字の場合、記号の場合、「?」の場合はスキップ
		if (word.getReading() == null || CommonUtil.isSurrogate(word.getCharTerm())
				|| word.getPartOfSpeech().startsWith("記号") || word.getCharTerm().equals("?")) {
			return;
		}
		// 不適切な単語を除去
		if (word.getCharTerm().equals("www")) {
			return;
		}

		String key = word.getPartOfSpeech() + "," + word.getInflectionForm() + "," + word.getInflectionType();
		// キーに紐づいた単語の一覧をとる
		ArrayList<Word> wordListWithKey = this.materialWordMap.get(key);
		if (wordListWithKey == null) {
			wordListWithKey = new ArrayList<Word>();
		}
		word.print();
		wordListWithKey.add(word);
		this.materialWordMap.put(key, wordListWithKey);
		this.materialWordList.add(word);
	}

}
