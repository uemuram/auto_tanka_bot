package jp.gr.java_conf.mu.atb.local;

import java.io.StringReader;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.BytesTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.ReadingAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.BaseFormAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.InflectionAttribute;

public class Test {

	public static void main(String[] args) {

		// String src =
		// "だがある時考えを変えて、やはり質のいいジャグリング道具を作ることにする。そしてあまたの試行錯誤を経て、シリックスボールや、PXクラブ、リングなどヒット商品を産み出し、現在世界でも多くのシェアを誇る会社になった。現在では楽器の生産はせず、ジャグリング道具のみ作っているそう。";

		// String src = "ぶつかりて おしのけもして 白鳥は おりたいところに おりておちつく";
		// String src = "さみしい顔 しないさみしさ むらさきに 暮れる世界の 向こうから来る";
		// String src = "甘やかで 切ない風も あることを 人工知能は 知るのだろうか";
		// String src = "綱かけて 国来国来と 引きよせる 風土記のやうには ゆかぬ島島";
		String src = "壁に貼る 不動明王に キスできる まで背を伸ばす 今日のリハビリ";
		//String src = "昨日、経済産業省と、人工知能シンポジウムのために関西国際空港と新東京国際空港と東京スカイツリーに行きました。";

		try (JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, false, JapaneseTokenizer.DEFAULT_MODE)) {
			tokenizer.setReader(new StringReader(src));

			PositionIncrementAttribute positionIncrementAttribute = tokenizer
					.addAttribute(PositionIncrementAttribute.class);
			PositionLengthAttribute positionLengthAttribute = tokenizer.addAttribute(PositionLengthAttribute.class);
			FlagsAttribute flagsAttribute = tokenizer.addAttribute(FlagsAttribute.class);
			KeywordAttribute keywordAttribute = tokenizer.addAttribute(KeywordAttribute.class);
			OffsetAttribute offsetAttribute = tokenizer.addAttribute(OffsetAttribute.class);

			CharTermAttribute term = tokenizer.addAttribute(CharTermAttribute.class);
			ReadingAttribute reading = tokenizer.addAttribute(ReadingAttribute.class);
			PartOfSpeechAttribute partOfSpeech = tokenizer.addAttribute(PartOfSpeechAttribute.class);
			InflectionAttribute inflection = tokenizer.addAttribute(InflectionAttribute.class);

			BaseFormAttribute baseForm = tokenizer.addAttribute(BaseFormAttribute.class);
			PayloadAttribute payloadAttribute = tokenizer.addAttribute(PayloadAttribute.class);
			TypeAttribute typeAttribute = tokenizer.addAttribute(TypeAttribute.class);
			BytesTermAttribute bytesTermAttribute = tokenizer.addAttribute(BytesTermAttribute.class);
			TermToBytesRefAttribute termToBytesRefAttribute = tokenizer.addAttribute(TermToBytesRefAttribute.class);

			tokenizer.reset();

			while (tokenizer.incrementToken()) {
				System.out.println(positionIncrementAttribute.getPositionIncrement() + "-"
						+ positionLengthAttribute.getPositionLength() + "-" + flagsAttribute.getFlags() + "-"
						+ keywordAttribute.isKeyword() + "-" + offsetAttribute.startOffset() + "-"
						+ offsetAttribute.endOffset() + "\t" + term.toString() + "," + reading.getReading() + ","
						+ partOfSpeech.getPartOfSpeech() + "," + inflection.getInflectionForm() + ","
						+ inflection.getInflectionType());
			}

		} catch (Exception e) {

		}

	}

}
