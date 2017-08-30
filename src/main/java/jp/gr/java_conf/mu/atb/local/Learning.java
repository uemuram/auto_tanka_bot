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

import jp.gr.java_conf.mu.atb.dto.AppearenceRate;
import jp.gr.java_conf.mu.atb.dto.Word;
import jp.gr.java_conf.mu.atb.util.CommonUtil;

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
			int count;
			// �ǂݍ��܂ꂽ�g�[�N���̈ꗗ
			ArrayList<Word> wordList = new ArrayList<Word>();
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

				count = 0;
				int blankCount = 0;
				while (tokenizer.incrementToken()) {
					// �|�W�V�������ړ�������(�ړ�������1)
					int positionIncrement = positionIncrementAttribute.getPositionIncrement();
					// ���̒P�ꂪ�A���������̒P��Əd�Ȃ��Ă��邩
					int positionLength = positionLengthAttribute.getPositionLength();
					// �|�W�V�����ړ����Ȃ���΁A�O��̓o�^���ʂ��̂Ă�(�O��:�u�����v�A����:�u�����X�J�C�c���[�v
					// �ł���΁A�u�����v)���̂Ă�
					if (positionIncrement == 0) {
						wordList.remove(wordList.size() - 1);
						count--;
					}

					Word word = new Word(charTermAttribute.toString(), readingAttribute.getReading(), 0,
							partOfSpeechAttribute.getPartOfSpeech(), inflectionAttribute.getInflectionForm(),
							inflectionAttribute.getInflectionType());

					// �P����L�^
					wordList.add(word);
					// �󔒂������ꍇ�͏ꏊ���L�^
					if (word.getPartOfSpeech().equals("�L��-��")) {
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

			// �Ō�ɋ󔒂�1�ǉ�����
			Word word = new Word(" ", null, 0, "�L��-��", null, null);
			wordList.add(word);
			blankPosition.put(count + "", 5);

			// �o�������v�Z
			int size = wordList.size();
			for (int i = 0; i < size; i++) {
				Word currentWord;
				Word before1Word;
				Word before2Word;

				String currentKey = "";
				String before1Key = "";
				String before2Key = "";

				// ����
				currentWord = wordList.get(i);
				currentKey = getKeyName(currentWord, blankPosition.get(i + ""));
				// 1�O
				if (i >= 1) {
					before1Word = wordList.get(i - 1);
					before1Key = getKeyName(before1Word, blankPosition.get((i - 1) + ""));
				}
				// 2�O
				if (i >= 2) {
					before2Word = wordList.get(i - 2);
					before2Key = getKeyName(before2Word, blankPosition.get((i - 2) + ""));
				}

				// �\��
				System.out.print(currentWord.getCharTerm() + '\t' + currentWord.getReading() + ','
						+ currentWord.getPartOfSpeech() + ',' + currentWord.getInflectionForm() + ','
						+ currentWord.getInflectionType());

				System.out.println("\t<" + currentKey + ">");

				// �󔒈ȊO�œǂ݂��Ȃ��P�ꂪ�����������͑ł��؂�
				if (currentWord.getReading() == null && !currentWord.getPartOfSpeech().equals("�L��-��")) {
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
	private static String getKeyName(Word word, Integer blankPosition) {
		String key;
		if (blankPosition != null) {
			// �󔒂̏ꍇ
			key = "*��" + blankPosition;
		} else if (word.getPartOfSpeech().startsWith("����-")) {
			// �����̏ꍇ
			key = word.getPartOfSpeech() + "," + word.getInflectionForm() + "," + word.getInflectionType();
		} else {
			// �󔒂ł͂Ȃ��ꍇ
			key = word.getPartOfSpeech() + "," + word.getInflectionForm() + "," + word.getInflectionType();
		}
		return key;
	}

}
