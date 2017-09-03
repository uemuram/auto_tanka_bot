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

		// テキストを品詞分解
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
			// エラー終了
			System.out.println("エラー:" + e.getMessage());
			return;
		}
		System.out.println("");
	}

	// 単語ひとつを素材として追加する
	private void addMaterialWord(String charTerm, String reading, String partOfSpeech, String inflectionForm,
			String inflectionType) {
		// 読みがない場合、絵文字の場合、記号の場合、「?」の場合はスキップ
		if (reading == null || CommonUtil.isSurrogate(charTerm) || partOfSpeech.startsWith("記号")
				|| charTerm.equals("?")) {
			return;
		}
		// 不適切な単語を除去
		if (charTerm.equals("www")) {
			return;
		}

		String key = partOfSpeech + "," + inflectionForm + "," + inflectionType;
		// キーに紐づいた単語の一覧をとる
		ArrayList<Word> wordListWithKey = this.materialWordMap.get(key);
		if (wordListWithKey == null) {
			wordListWithKey = new ArrayList<Word>();
		}
		Word tmpWord = new Word(charTerm, reading, partOfSpeech, inflectionForm, inflectionType);
		tmpWord.print();
		wordListWithKey.add(tmpWord);
		this.materialWordMap.put(key, wordListWithKey);

		this.materialWordList.add(tmpWord);
	}

}
