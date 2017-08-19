package jp.gr.java_conf.mu.atb.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.InflectionAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.ReadingAttribute;

public class Learning {

	public static void main(String[] args) {

		System.out.println("start");

		// 学習用短歌
		ArrayList<String> tankaList = readTanka();

		// 出現率格納マップ
		HashMap<String, HashMap<String, Integer>> appearanceRate1 = new HashMap<String, HashMap<String, Integer>>();

		for (String tanka : tankaList) {
			System.out.println("\n【" + tanka + "】");

			// 読み込まれたトークンの一覧
			ArrayList<HashMap<String, String>> tokenList = new ArrayList<HashMap<String, String>>();
			// 区切り位置の一覧
			HashMap<String, Integer> blankPosition = new HashMap<String, Integer>();

			// 二重カウントを除去しながら読み込み(「東京」、「東京スカイツリー」、「スカイツリー」ー⇒「東京スカイツリー」のみにする)
			try (JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, false, JapaneseTokenizer.DEFAULT_MODE)) {
				tokenizer.setReader(new StringReader(tanka));
				PositionIncrementAttribute positionIncrementAttribute = tokenizer
						.addAttribute(PositionIncrementAttribute.class);
				PositionLengthAttribute positionLengthAttribute = tokenizer.addAttribute(PositionLengthAttribute.class);
				CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
				ReadingAttribute readingAttribute = tokenizer.addAttribute(ReadingAttribute.class);
				PartOfSpeechAttribute partOfSpeechAttribute = tokenizer.addAttribute(PartOfSpeechAttribute.class);
				InflectionAttribute inflectionAttribute = tokenizer.addAttribute(InflectionAttribute.class);
				tokenizer.reset();

				int count = 0;
				int blankCount = 0;
				while (tokenizer.incrementToken()) {
					// ポジションが移動したか(移動したら1)
					int positionIncrement = positionIncrementAttribute.getPositionIncrement();
					// 今の単語が、次いくつ分の単語と重なっているか
					int positionLength = positionLengthAttribute.getPositionLength();
					// ポジション移動がなければ、前回の登録結果を捨てる(前回:「東京」、今回:「東京スカイツリー」
					// であれば、「東京」)を捨てる
					if (positionIncrement == 0) {
						tokenList.remove(tokenList.size() - 1);
						count--;
					}
					HashMap<String, String> token = new HashMap<String, String>();
					// 生データ
					token.put("term", charTermAttribute.toString());
					// 読み
					token.put("reading", readingAttribute.getReading());
					// 品詞
					token.put("partOfSpeech", partOfSpeechAttribute.getPartOfSpeech());
					// 活用系1(連用形 など)
					token.put("inflectionForm", inflectionAttribute.getInflectionForm());
					// 活用系2(五段・カ行促音便 など)
					token.put("inflectionType", inflectionAttribute.getInflectionType());

					// 単語を記録
					tokenList.add(token);
					// 空白だった場合は場所を記録
					if (token.get("partOfSpeech").equals("記号-空白")) {
						blankCount++;
						blankPosition.put(count + "", blankCount);
					}
					count++;

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

			// 空白の個数が4つではなかった場合は中断
			if (blankPosition.size() != 4) {
				System.out.println("空白が " + blankPosition.size() + " 個だったのでスキップ");
				continue;
			}

			// 出現率を計算
			int size = tokenList.size();
			for (int i = 0; i < size; i++) {
				HashMap<String, String> currentToken;
				HashMap<String, String> before1Token;
				HashMap<String, String> before2Token;
				String currentKey = "";
				String before1Key = "";
				String before2Key = "";

				// 今の
				currentToken = tokenList.get(i);
				currentKey = getKeyName(currentToken, blankPosition.get(i + ""));
				// 1つ前
				if (i >= 1) {
					before1Token = tokenList.get(i - 1);
					before1Key = getKeyName(before1Token, blankPosition.get((i - 1) + ""));
				}
				// 2つ前
				if (i >= 2) {
					before2Token = tokenList.get(i - 2);
					before2Key = getKeyName(before2Token, blankPosition.get((i - 2) + ""));
				}

				// 表示
				System.out.print(currentToken.get("term") + '\t' + currentToken.get("reading") + ','
						+ currentToken.get("partOfSpeech") + ',' + currentToken.get("inflectionForm") + ','
						+ currentToken.get("inflectionType"));
				System.out.println("\t<" + currentKey + ">");

				// 空白以外で読みがない単語が見つかったら解析打ち切り
				if (currentToken.get("reading") == null && !currentToken.get("partOfSpeech").equals("記号-空白")) {
					System.out.println("※読みがないため打ち切り");
					break;
				}
				// 1つ目のとき
				if (i == 0) {
					// 先頭は、0番目の空白の後、ということにする
					incrementAppearanceRate1(appearanceRate1, "*空白0", currentKey);
				} else {
					incrementAppearanceRate1(appearanceRate1, before1Key, currentKey);
				}
			}
		}
		System.out.println("--------");
		printAppearanceRate1(appearanceRate1);
		System.out.println("end");
	}

	// 活用系等を考慮したキー名を返す
	// 例) 動詞-自立,連用形,一段
	private static String getKeyName(HashMap<String, String> token, Integer blankPosition) {
		String key;
		if (blankPosition != null) {
			// 空白の場合
			key = "*" + "空白" + blankPosition;
		} else {
			// 空白ではない場合
			key = token.get("partOfSpeech") + "," + token.get("inflectionForm") + "," + token.get("inflectionType");
		}
		return key;
	}

	// 出現率1を表示
	private static void printAppearanceRate1(HashMap<String, HashMap<String, Integer>> appearanceRate1) {
		// ソート用
		Object[] keys1 = appearanceRate1.keySet().toArray();
		Arrays.sort(keys1);
		for (int i = 0; i < keys1.length; i++) {
			String key1 = (String) keys1[i];
			HashMap<String, Integer> tmpMap = appearanceRate1.get(key1);
			Object[] keys2 = tmpMap.keySet().toArray();
			Arrays.sort(keys2);
			for (int j = 0; j < keys2.length; j++) {
				String key2 = (String) keys2[j];
				int count = tmpMap.get(key2);
				System.out.println(key1 + "\t" + key2 + "\t" + count);
			}
		}
	}

	// key1の後にkey2が出てくる個数を1増やす
	private static void incrementAppearanceRate1(HashMap<String, HashMap<String, Integer>> appearanceRate1, String key1,
			String key2) {
		int count = getAppearanceRate1(appearanceRate1, key1, key2);
		HashMap<String, Integer> tmpMap1 = appearanceRate1.get(key1);
		if (tmpMap1 == null) {
			tmpMap1 = new HashMap<String, Integer>();
		}
		tmpMap1.put(key2, count + 1);
		appearanceRate1.put(key1, tmpMap1);
	}

	// key1の後にkey2が出てくる個数を返す
	private static int getAppearanceRate1(HashMap<String, HashMap<String, Integer>> appearanceRate1, String key1,
			String key2) {
		HashMap<String, Integer> tmpMap1 = appearanceRate1.get(key1);
		if (tmpMap1 == null) {
			return 0;
		}
		Integer count = tmpMap1.get(key2);
		if (count == null) {
			return 0;
		}
		return count;
	}

	// 学習元の短歌を読み込む
	private static ArrayList<String> readTanka() {
		// "src/main/resources"からファイルを読み込む．
		InputStream is = ClassLoader.getSystemResourceAsStream("tanka.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		ArrayList<String> tankaList = new ArrayList<String>();
		String l = null;
		try {
			while ((l = br.readLine()) != null) {
				// コメント行と空行をスキップ
				if (l.length() == 0 || l.startsWith("#")) {
					continue;
				}
				System.out.println(l);
				tankaList.add(l);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tankaList;
	}

}
