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
		tankaList.add("�Ԃ���� �����̂������� ������ ���肽���Ƃ���� ����Ă�����");
		tankaList.add("���݂����� ���Ȃ����݂��� �ނ炳���� ���鐢�E�� ���������痈��");
		tankaList.add("�Â₩�� �؂Ȃ����� ���邱�Ƃ� �l�H�m�\�� �m��̂��낤��");
		tankaList.add("�j������ ���������� �����悹�� ���y�L�̂₤�ɂ� �䂩�ʓ���");
		tankaList.add("�ǂɓ\�� �s�������� �L�X�ł��� �܂Ŕw��L�΂� �����̃��n�r��");
		tankaList.add("�ʂ炤�ۂ�������");
		tankaList.add("�c��123�R�c");

		for (String tanka : tankaList) {
			System.out.println("\n�y" + tanka + "�z");

			// ��d�J�E���g���������Ȃ���ǂݍ���(�u�����v�A�u�����X�J�C�c���[�v�A�u�X�J�C�c���[�v�[�ˁu�����X�J�C�c���[�v�݂̂ɂ���)
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
					// �|�W�V�������ړ�������(�ړ�������1)
					int positionIncrement = positionIncrementAttribute.getPositionIncrement();
					// ���̒P�ꂪ�A���������̒P��Əd�Ȃ��Ă��邩
					int positionLength = positionLengthAttribute.getPositionLength();
					// �|�W�V�����ړ����Ȃ���΁A�O��̓o�^���ʂ��̂Ă�(�O��:�u�����v�A����:�u�����X�J�C�c���[�v
					// �ł���΁A�u�����v)���̂Ă�
					if (positionIncrement == 0) {
						tokenList.remove(tokenList.size() - 1);
					}
					HashMap<String, String> token = new HashMap<String, String>();
					// ���f�[�^
					token.put("term", charTermAttribute.toString());
					// �ǂ�
					token.put("reading", readingAttribute.getReading());
					// �i��
					token.put("partOfSpeech", partOfSpeechAttribute.getPartOfSpeech());
					// ���p�n1(�A�p�` �Ȃ�)
					token.put("inflectionForm", inflectionAttribute.getInflectionForm());
					// ���p�n2(�ܒi�E�J�s������ �Ȃ�)
					token.put("inflectionType", inflectionAttribute.getInflectionType());

					// �P����L�^
					tokenList.add(token);
					// �P��̏d�Ȃ���X�L�b�v(����:�u�����X�J�C�c���[�v �A����u�X�J�C�c���[�v�ł���΁A�u�X�J�C�c���[�v)���̂Ă�
					for (int i = 0; i < positionLength - 1; i++) {
						tokenizer.incrementToken();
					}
				}
			} catch (Exception e) {
				// �G���[�I��
				System.out.println("�G���[:" + e.getMessage());
				return;
			}

			// �o�������v�Z
			int size = tokenList.size();
			for (int i = 0; i < size; i++) {
				HashMap<String, String> token = tokenList.get(i);

				System.out.println(token.get("term") + ',' + token.get("reading") + ',' + token.get("partOfSpeech")
						+ ',' + token.get("inflectionForm") + ',' + token.get("inflectionType"));
				// �󔒈ȊO�œǂ݂��Ȃ��P�ꂪ�����������͑ł��؂�
				if (token.get("reading") == null && !token.get("partOfSpeech").equals("�L��-��")) {
					System.out.println("���ǂ݂��Ȃ����ߑł��؂�");
					break;
				}
			}

		}

	}

}
