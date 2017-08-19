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

		// �w�K�p�Z��
		ArrayList<String> tankaList = readTanka();

		// �o�����i�[�}�b�v
		HashMap<String, HashMap<String, Integer>> appearanceRate1 = new HashMap<String, HashMap<String, Integer>>();

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
					// �󔒂������ꍇ�͏ꏊ���L�^
					if (token.get("partOfSpeech").equals("�L��-��")) {
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
				System.out.print(currentToken.get("term") + '\t' + currentToken.get("reading") + ','
						+ currentToken.get("partOfSpeech") + ',' + currentToken.get("inflectionForm") + ','
						+ currentToken.get("inflectionType"));
				System.out.println("\t<" + currentKey + ">");

				// �󔒈ȊO�œǂ݂��Ȃ��P�ꂪ�����������͑ł��؂�
				if (currentToken.get("reading") == null && !currentToken.get("partOfSpeech").equals("�L��-��")) {
					System.out.println("���ǂ݂��Ȃ����ߑł��؂�");
					break;
				}
				// 1�ڂ̂Ƃ�
				if (i == 0) {
					// �擪�́A0�Ԗڂ̋󔒂̌�A�Ƃ������Ƃɂ���
					incrementAppearanceRate1(appearanceRate1, "*��0", currentKey);
				} else {
					incrementAppearanceRate1(appearanceRate1, before1Key, currentKey);
				}
			}
		}
		System.out.println("--------");
		printAppearanceRate1(appearanceRate1);
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
			key = token.get("partOfSpeech") + "," + token.get("inflectionForm") + "," + token.get("inflectionType");
		}
		return key;
	}

	// �o����1��\��
	private static void printAppearanceRate1(HashMap<String, HashMap<String, Integer>> appearanceRate1) {
		// �\�[�g�p
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

	// key1�̌��key2���o�Ă������1���₷
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

	// key1�̌��key2���o�Ă������Ԃ�
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

	// �w�K���̒Z�̂�ǂݍ���
	private static ArrayList<String> readTanka() {
		// "src/main/resources"����t�@�C����ǂݍ��ށD
		InputStream is = ClassLoader.getSystemResourceAsStream("tanka.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		ArrayList<String> tankaList = new ArrayList<String>();
		String l = null;
		try {
			while ((l = br.readLine()) != null) {
				// �R�����g�s�Ƌ�s���X�L�b�v
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
