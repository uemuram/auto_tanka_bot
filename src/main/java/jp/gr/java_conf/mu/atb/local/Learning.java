package jp.gr.java_conf.mu.atb.local;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.InflectionAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.ReadingAttribute;

import dto.AppearenceRate;
import jp.gr.java_conf.mu.atb.util.CommonUtil;
import jp.gr.java_conf.mu.atb.util.Const;

public class Learning {

	public static void main(String[] args) {

		System.out.println("start");

		// 学習用短歌
		ArrayList<String> tankaList = CommonUtil.readFile("tanka.txt");
		// 出現率格納クラス
		AppearenceRate appearenceRate = new AppearenceRate();

		// 全ての短歌を使って学習
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
					token.put(Const.TOKEN_TERM, charTermAttribute.toString());
					// 読み
					token.put(Const.TOKEN_READING, readingAttribute.getReading());
					// 品詞
					token.put(Const.TOKEN_PART_OF_SPEECH, partOfSpeechAttribute.getPartOfSpeech());
					// 活用系1(連用形 など)
					token.put(Const.TOKEN_INFLECTION_FORM, inflectionAttribute.getInflectionForm());
					// 活用系2(五段・カ行促音便 など)
					token.put(Const.TOKEN_INFLECTION_TYPE, inflectionAttribute.getInflectionType());

					// 単語を記録
					tokenList.add(token);
					// 空白だった場合は場所を記録
					if (token.get(Const.TOKEN_PART_OF_SPEECH).equals("記号-空白")) {
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
				System.out.print(currentToken.get(Const.TOKEN_TERM) + '\t' + currentToken.get(Const.TOKEN_READING) + ','
						+ currentToken.get(Const.TOKEN_PART_OF_SPEECH) + ','
						+ currentToken.get(Const.TOKEN_INFLECTION_FORM) + ','
						+ currentToken.get(Const.TOKEN_INFLECTION_TYPE));
				System.out.println("\t<" + currentKey + ">");

				// 空白以外で読みがない単語が見つかったら解析打ち切り
				if (currentToken.get(Const.TOKEN_READING) == null
						&& !currentToken.get(Const.TOKEN_PART_OF_SPEECH).equals("記号-空白")) {
					System.out.println("※読みがないため打ち切り");
					break;
				}
				// 1つ目のとき
				if (i == 0) {
					// 先頭は、0番目の空白の後、ということにする
					appearenceRate.incrementCount1("*空白0", currentKey);
				} else {
					appearenceRate.incrementCount1(before1Key, currentKey);
				}
			}
		}
		System.out.println("--------");

		try {
			appearenceRate.fileOutRate1("src\\main\\resources\\AppearanceRate1.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

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
			key = token.get(Const.TOKEN_PART_OF_SPEECH) + "," + token.get(Const.TOKEN_INFLECTION_FORM) + ","
					+ token.get(Const.TOKEN_INFLECTION_TYPE);
		}
		return key;
	}

}
