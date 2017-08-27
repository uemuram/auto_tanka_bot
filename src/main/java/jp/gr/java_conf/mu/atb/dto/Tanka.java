package jp.gr.java_conf.mu.atb.dto;

import java.util.ArrayList;

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

	// �w�肳�ꂽ�ԍ��̃t�F�[�Y�ɒP���1�ǉ�����
	public void putWord(int phaseNum, Word word) {
		ArrayList<Word> phase = this.tanka.get(phaseNum);
		phase.add(word);
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

	// ������
	public String toString() {
		String tankaStr = "";
		for (int i = 0; i < PHASE_COUNT; i++) {
			ArrayList<Word> phase = this.tanka.get(i);
			for (Word word : phase) {
				tankaStr += word.getCharTerm();
			}
			if (i != PHASE_COUNT - 1) {
				tankaStr += " ";
			}
		}
		return tankaStr;
	}

	// ��ʕ\������
	public void print() {
		int score = this.calcScore();
		System.out.println(score + "\t" + this.toString());
	}

	// �X�R�A
	public int getScore() {
		return this.calcScore();
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
	private int calcScore() {
		// �����X�R�A
		int score = 200;
		int[] phaseLength = { 5, 7, 5, 7, 7 };

		// 57577���炸��Ă���ƌ��_
		for (int i = 0; i < 5; i++) {
			score -= ((Math.abs(this.getPhaseLength(i) - phaseLength[i])) * 10);
		}

		ArrayList<Word> linkedWordList = this.getLinkedWordList();
		// �w�K�f�[�^�ʂ�̘A���ł͂Ȃ��ꍇ�͌��_

		// �}�C�i�X�ɂȂ�Ȃ��悤�ɂ���
		if (score < 0) {
			score = 0;
		}
		return score;
	}

}
