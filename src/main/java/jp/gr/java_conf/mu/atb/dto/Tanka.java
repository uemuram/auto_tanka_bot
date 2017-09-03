package jp.gr.java_conf.mu.atb.dto;

import java.util.ArrayList;
import java.util.HashMap;

import jp.gr.java_conf.mu.atb.util.CommonUtil;

public class Tanka {

	// �w�K�f�[�^
	private static AppearenceRate appearenceRate;
	private static ArrayList<Word> blankWord;
	private final static int PHASE_COUNT = 5;

	// �t�F�[�Y�ʂ̔z��
	private ArrayList<ArrayList<Word>> tanka;

	// �ÓI������
	static {
		// �w�K�f�[�^��ێ�
		appearenceRate = new AppearenceRate("AppearanceRate1.txt");
		appearenceRate.printRate1();
		blankWord = new ArrayList<Word>();
		for (int i = 0; i < PHASE_COUNT + 1; i++) {
			blankWord.add(new Word("*��" + i));
		}
	}

	// �R���X�g���N�^
	public Tanka() {
		this.tanka = new ArrayList<ArrayList<Word>>();
		for (int i = 0; i < PHASE_COUNT; i++) {
			ArrayList<Word> phase = new ArrayList<Word>();
			this.tanka.add(phase);
		}
	}

	// ����̃f�B�[�v�R�s�[��Ԃ�
	public Tanka clone() {
		Tanka cloneTanka = new Tanka();
		for (int i = 0; i < PHASE_COUNT; i++) {
			ArrayList<Word> phase = this.tanka.get(i);
			for (Word word : phase) {
				cloneTanka.putWord(i, word);
			}
		}
		return cloneTanka;
	}

	// �w�肳�ꂽ�ԍ��̃t�F�[�Y�ɒP���1�ǉ�����
	public void putWord(int phaseNum, Word word) {
		ArrayList<Word> phase = this.tanka.get(phaseNum);
		phase.add(word);
	}

	// �w�肳�ꂽ�ԍ��̃t�F�[�Y�̃����_���ȉӏ��̒P����X�V����
	public void updateWord(int phaseNum, Word word) {
		ArrayList<Word> phase = this.tanka.get(phaseNum);
		int size = phase.size();
		int p = CommonUtil.random(size);
		phase.remove(p);
		phase.add(p, word);
	}

	// �w�肳�ꂽ�ԍ��̃t�F�[�Y�̃����_���ȉӏ��ɁA�P���}������
	public void insertWord(int phaseNum, Word word) {
		ArrayList<Word> phase = this.tanka.get(phaseNum);
		int size = phase.size();
		phase.add(CommonUtil.random(size), word);
	}

	// �w�肳�ꂽ�ԍ��̃t�F�[�Y����A�P���1�����_���ō폜����
	public void deleteWord(int phaseNum) {
		ArrayList<Word> phase = this.tanka.get(phaseNum);
		int size = phase.size();
		// �T�C�Y��2�ȏ�̂Ƃ��̂ݍ폜
		if (size >= 2) {
			int p = CommonUtil.random(size);
			phase.remove(p);
		}
	}

	// �w�肳�ꂽ�ԍ��̃t�F�[�Y�̒�����Ԃ�
	public int getPhaseLength(int phaseNum) {
		ArrayList<Word> phase = this.tanka.get(phaseNum);
		int length = 0;
		for (Word word : phase) {
			length += word.getReadingLength();
		}
		return length;
	}

	// �w�肳�ꂽ�ԍ��̃t�F�[�Y��Ԃ�
	public ArrayList<Word> getPhase(int n) {
		return this.tanka.get(n);
	}

	// ������
	public String toString() {
		String tankaStr = "";
		for (int i = 0; i < PHASE_COUNT; i++) {
			ArrayList<Word> phase = this.tanka.get(i);
			for (Word word : phase) {
				if (word == null) {
					tankaStr += "[null]";
				} else {
					tankaStr += word.getCharTerm();
				}
			}
			if (i != PHASE_COUNT - 1) {
				tankaStr += " ";
			}
		}
		return tankaStr;
	}

	// ��ʕ\������
	public void print(MaterialWord materialWord) {
		int score = this.calcScore(materialWord);
		System.out.println(score + "\t" + this.toString());
	}

	// �������ĉ�ʕ\������
	public void printWord(MaterialWord materialWord) {
		this.print(materialWord);
		for (int i = 0; i < PHASE_COUNT; i++) {
			ArrayList<Word> phase = this.tanka.get(i);
			for (Word word : phase) {
				word.print();
			}
		}
	}

	// �X�R�A
	public int getScore(MaterialWord materialWord) {
		return this.calcScore(materialWord);
	}

	// �S�Ă̒P���(�󔒍��݂�)���ׂ����X�g��Ԃ�
	private ArrayList<Word> getLinkedWordList() {
		ArrayList<Word> list = new ArrayList<Word>();
		for (int i = 0; i < PHASE_COUNT; i++) {
			list.add(blankWord.get(i));
			list.addAll(this.tanka.get(i));
		}
		list.add(blankWord.get(PHASE_COUNT));
		return list;
	}

	// �X�R�A���v�Z����
	private int calcScore(MaterialWord materialWord) {
		// �����X�R�A
		int score = 400;
		int[] phaseLength = { 5, 7, 5, 7, 7 };

		// 57577���炸��Ă���ƌ��_
		for (int i = 0; i < 5; i++) {
			score -= ((Math.abs(this.getPhaseLength(i) - phaseLength[i])) * 10);
		}

		// �����P�ꂪ�o�Ă����񐔂��`�F�b�N
		HashMap<String, Integer> duplicateWord = new HashMap<String, Integer>();

		ArrayList<Word> linkedWordList = this.getLinkedWordList();
		int size = linkedWordList.size();
		for (int i = 0; i < size; i++) {

			Word current = linkedWordList.get(i);

			// �P��Ԃ̑J�ڂɂ��`�F�b�N
			if (i < size - 1) {
				Word next = linkedWordList.get(i + 1);

				// �w�K�f�[�^�ɂ��X�R�A�����O
				int c = appearenceRate.getCount1(current.getKey(), next.getKey());
				// �A�����Ȃ��ꍇ�͑傫�����_
				if (c == 0) {
					score -= 10;
				}

				// �f�ރf�[�^�ɂ��X�R�A�����O
				int d = materialWord.getTransitionCount(current, next);
				// �����ȊO�őf�ނƓ����A��������΃{�[�i�X���_
				if (!current.getPartOfSpeech().startsWith("����-") && !next.getPartOfSpeech().startsWith("����-")) {
					score += d;
				}

				// �A���̓x�����ɉ����ă{�[�i�X���_
				double r = appearenceRate.getRatio1(current.getKey(), next.getKey());
				score += (r * 10);
			}

			// �������������x���o�Ă���ꍇ�͌��_
			if (current.getPartOfSpeech().startsWith("����-")) {
				String duplicateKey = current.getCharTerm() + ":" + current.getKey();
				Integer count = duplicateWord.get(duplicateKey);
				if (count == null) {
					// ����͌��_���Ȃ�
					duplicateWord.put(duplicateKey, 1);
				} else {
					// �����P�ꂪ2��ڈȍ~�o�Ē�����s�x���_
					duplicateWord.put(duplicateKey, count + 1);
					score -= 5;
				}
			}
		}

		// �}�C�i�X�ɂȂ�Ȃ��悤�ɂ���
		if (score < 0) {
			score = 0;
		}
		return score;
	}

}
