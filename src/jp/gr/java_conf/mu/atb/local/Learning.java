package jp.gr.java_conf.mu.atb.local;

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

public class Learning {

	public static void main(String[] args) {

		ArrayList<String> tankaList = new ArrayList<String>();
		tankaList.add("ぶつかりて おしのけもして 白鳥は おりたいところに おりておちつく");
		tankaList.add("さみしい顔 しないさみしさ むらさきに 暮れる世界の 向こうから来る");
		tankaList.add("甘やかで 切ない風も あることを 人工知能は 知るのだろうか");
		tankaList.add("綱かけて 国来国来と 引きよせる 風土記のやうには ゆかぬ島島");
		tankaList.add("壁に貼る 不動明王に キスできる まで背を伸ばす 今日のリハビリ");
		tankaList.add("ぬらうぽだｓｓｄ");
		tankaList.add("田中123山田");

		for (String tanka : tankaList) {
			System.out.println("\n【" + tanka + "】");

			// 二重カウントを除去しながら読み込み(「東京」、「東京スカイツリー」、「スカイツリー」ー⇒「東京スカイツリー」のみにする)
			ArrayList<HashMap<String, String>> tokenList = new ArrayList<HashMap<String, String>>();
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
				while (tokenizer.incrementToken()) {
					// ポジションが移動したか(移動したら1)
					int positionIncrement = positionIncrementAttribute.getPositionIncrement();
					// 今の単語が、次いくつ分の単語と重なっているか
					int positionLength = positionLengthAttribute.getPositionLength();
					// ポジション移動がなければ、前回の登録結果を捨てる(前回:「東京」、今回:「東京スカイツリー」
					// であれば、「東京」)を捨てる
					if (positionIncrement == 0) {
						tokenList.remove(tokenList.size() - 1);
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

			// 出現率を計算
			int size = tokenList.size();
			for (int i = 0; i < size; i++) {
				HashMap<String, String> token = tokenList.get(i);

				System.out.println(token.get("term") + ',' + token.get("reading") + ',' + token.get("partOfSpeech")
						+ ',' + token.get("inflectionForm") + ',' + token.get("inflectionType"));
				// 空白以外で読みがない単語が見つかったら解析打ち切り
				if (token.get("reading") == null && !token.get("partOfSpeech").equals("記号-空白")) {
					System.out.println("※読みがないため打ち切り");
					break;
				}
			}

		}

	}

}
