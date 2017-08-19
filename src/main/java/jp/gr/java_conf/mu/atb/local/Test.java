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
		// "�������鎞�l����ς��āA��͂莿�̂����W���O�����O�������邱�Ƃɂ���B�����Ă��܂��̎��s������o�āA�V���b�N�X�{�[����APX�N���u�A�����O�Ȃǃq�b�g���i���Y�ݏo���A���ݐ��E�ł������̃V�F�A���ւ��ЂɂȂ����B���݂ł͊y��̐��Y�͂����A�W���O�����O����̂ݍ���Ă��邻���B";

		// String src = "�Ԃ���� �����̂������� ������ ���肽���Ƃ���� ����Ă�����";
		// String src = "���݂����� ���Ȃ����݂��� �ނ炳���� ���鐢�E�� ���������痈��";
		// String src = "�Â₩�� �؂Ȃ����� ���邱�Ƃ� �l�H�m�\�� �m��̂��낤��";
		// String src = "�j������ ���������� �����悹�� ���y�L�̂₤�ɂ� �䂩�ʓ���";
		String src = "�ǂɓ\�� �s�������� �L�X�ł��� �܂Ŕw��L�΂� �����̃��n�r��";
		//String src = "����A�o�ώY�ƏȂƁA�l�H�m�\�V���|�W�E���̂��߂Ɋ֐����ۋ�`�ƐV�������ۋ�`�Ɠ����X�J�C�c���[�ɍs���܂����B";

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
