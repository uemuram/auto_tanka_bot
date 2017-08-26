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

	// 単語をランダムで1つ返す
	public Word getRandomWord() {
		int size = this.materialWordList.size();
		return this.materialWordList.get(CommonUtil.random(size));
	}

	// テキストをもとにして素材を追加する
	private void addMaterialWord(String text) {
		System.out.println("【" + text + "】");

		// ツイッターのユーザ名(@xx)を除外する
		text = text.replaceAll("@[a-zA-Z0-9_]", " ");

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
		// 読みがない場合、絵文字の場合、記号の場合はスキップ
		if (reading == null || CommonUtil.isSurrogate(charTerm) || partOfSpeech.startsWith("記号")) {
			return;
		}
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
		int readingLength = tmpReading.length();

		String key = partOfSpeech + "," + inflectionForm + "," + inflectionType;
		System.out.println(charTerm + "\t" + reading + "(" + readingLength + ")" + "," + partOfSpeech + ","
				+ inflectionForm + "," + inflectionType + "\t<" + key + ">");

		// キーに紐づいた単語の一覧をとる
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
