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

		// �w�K�p�Z��
		ArrayList<String> tankaList = CommonUtil.readFile("tanka.txt");
		// �o�����i�[�N���X
		AppearenceRate appearenceRate = new AppearenceRate();

		// �S�Ă̒Z�̂��g���Ċw�K
		for (String tanka : tankaList) {
			System.out.println("\n�y" + tanka + "�z");

			// �ǂݍ��܂ꂽ�g�[�N���̈ꗗ
			ArrayList<HashMap<String, String>> tokenList = new ArrayList<HashMap<String, String>>();
			// ��؂�ʒu�̈ꗗ
			HashMap<String, Integer> blankPosition = new HashMap<String, Integer>();

			// ��d�J�E���g���������Ȃ���ǂݍ���(�u�����v�A�u�����X�J�C�c���[�v�A�u�X�J�C�c���[�v�[�ˁu�����X�J�C�c���[�v�݂̂ɂ���)
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
					// �|�W�V�������ړ�������(�ړ�������1)
					int positionIncrement = positionIncrementAttribute.getPositionIncrement();
					// ���̒P�ꂪ�A���������̒P��Əd�Ȃ��Ă��邩
					int positionLength = positionLengthAttribute.getPositionLength();
					// �|�W�V�����ړ����Ȃ���΁A�O��̓o�^���ʂ��̂Ă�(�O��:�u�����v�A����:�u�����X�J�C�c���[�v
					// �ł���΁A�u�����v)���̂Ă�
					if (positionIncrement == 0) {
						tokenList.remove(tokenList.size() - 1);
						count--;
					}
					HashMap<String, String> token = new HashMap<String, String>();
					// ���f�[�^
					token.put(Const.TOKEN_TERM, charTermAttribute.toString());
					// �ǂ�
					token.put(Const.TOKEN_READING, readingAttribute.getReading());
					// �i��
					token.put(Const.TOKEN_PART_OF_SPEECH, partOfSpeechAttribute.getPartOfSpeech());
					// ���p�n1(�A�p�` �Ȃ�)
					token.put(Const.TOKEN_INFLECTION_FORM, inflectionAttribute.getInflectionForm());
					// ���p�n2(�ܒi�E�J�s������ �Ȃ�)
					token.put(Const.TOKEN_INFLECTION_TYPE, inflectionAttribute.getInflectionType());

					// �P����L�^
					tokenList.add(token);
					// �󔒂������ꍇ�͏ꏊ���L�^
					if (token.get(Const.TOKEN_PART_OF_SPEECH).equals("�L��-��")) {
						blankCount++;
						blankPosition.put(count + "", blankCount);
					}
					count++;

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

			// �󔒂̌���4�ł͂Ȃ������ꍇ�͒��f
			if (blankPosition.size() != 4) {
				System.out.println("�󔒂� " + blankPosition.size() + " �������̂ŃX�L�b�v");
				continue;
			}

			// �o�������v�Z
			int size = tokenList.size();
			for (int i = 0; i < size; i++) {
				HashMap<String, String> currentToken;
				HashMap<String, String> before1Token;
				HashMap<String, String> before2Token;
				String currentKey = "";
				String before1Key = "";
				String before2Key = "";

				// ����
				currentToken = tokenList.get(i);
				currentKey = getKeyName(currentToken, blankPosition.get(i + ""));
				// 1�O
				if (i >= 1) {
					before1Token = tokenList.get(i - 1);
					before1Key = getKeyName(before1Token, blankPosition.get((i - 1) + ""));
				}
				// 2�O
				if (i >= 2) {
					before2Token = tokenList.get(i - 2);
					before2Key = getKeyName(before2Token, blankPosition.get((i - 2) + ""));
				}

				// �\��
				System.out.print(currentToken.get(Const.TOKEN_TERM) + '\t' + currentToken.get(Const.TOKEN_READING) + ','
						+ currentToken.get(Const.TOKEN_PART_OF_SPEECH) + ','
						+ currentToken.get(Const.TOKEN_INFLECTION_FORM) + ','
						+ currentToken.get(Const.TOKEN_INFLECTION_TYPE));
				System.out.println("\t<" + currentKey + ">");

				// �󔒈ȊO�œǂ݂��Ȃ��P�ꂪ�����������͑ł��؂�
				if (currentToken.get(Const.TOKEN_READING) == null
						&& !currentToken.get(Const.TOKEN_PART_OF_SPEECH).equals("�L��-��")) {
					System.out.println("���ǂ݂��Ȃ����ߑł��؂�");
					break;
				}
				// 1�ڂ̂Ƃ�
				if (i == 0) {
					// �擪�́A0�Ԗڂ̋󔒂̌�A�Ƃ������Ƃɂ���
					appearenceRate.incrementCount1("*��0", currentKey);
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

	// ���p�n�����l�������L�[����Ԃ�
	// ��) ����-����,�A�p�`,��i
	private static String getKeyName(HashMap<String, String> token, Integer blankPosition) {
		String key;
		if (blankPosition != null) {
			// �󔒂̏ꍇ
			key = "*" + "��" + blankPosition;
		} else {
			// �󔒂ł͂Ȃ��ꍇ
			key = token.get(Const.TOKEN_PART_OF_SPEECH) + "," + token.get(Const.TOKEN_INFLECTION_FORM) + ","
					+ token.get(Const.TOKEN_INFLECTION_TYPE);
		}
		return key;
	}

}
