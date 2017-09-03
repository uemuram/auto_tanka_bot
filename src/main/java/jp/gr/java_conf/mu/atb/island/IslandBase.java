package jp.gr.java_conf.mu.atb.island;

import java.util.ArrayList;
import java.util.Comparator;

import jp.gr.java_conf.mu.atb.dto.MaterialWord;
import jp.gr.java_conf.mu.atb.dto.Tanka;
import jp.gr.java_conf.mu.atb.dto.Word;
import jp.gr.java_conf.mu.atb.util.CommonUtil;

public class IslandBase {

	// �ێ�����Z�̂̐�
	protected int tankaNum = 40;

	// ������
	protected ArrayList<Tanka> currentGenerationTankaList;
	// ������
	protected ArrayList<Tanka> nextGenerationTankaList;
	// ���㐔
	protected int generation;
	// �f�ޏW
	protected MaterialWord materialWord;

	// �R���X�g���N�^
	public IslandBase() {
		this.currentGenerationTankaList = new ArrayList<Tanka>();
		this.nextGenerationTankaList = new ArrayList<Tanka>();
		this.generation = 1;
	}

	// n�Ԗڂ̒Z�̂�Ԃ�
	public Tanka getTanka(int n) {
		return currentGenerationTankaList.get(n);
	}

	// ��������𐶐�
	public void birth(MaterialWord materialWord) {
		for (int i = 0; i < this.tankaNum; i++) {
			Tanka tanka = createRandaomTanka(materialWord);
			this.currentGenerationTankaList.add(tanka);
		}
	}

	// ��������\�[�g����
	public void sort() {
		this.currentGenerationTankaList.sort(new TankaComparator());
	}

	// �����1�i�߂�
	public void incrementGeneration() {
		// �������������ɃR�s�[
		this.currentGenerationTankaList.clear();
		for (Tanka tanka : this.nextGenerationTankaList) {
			currentGenerationTankaList.add(tanka);
		}
		// ��������N���A
		this.nextGenerationTankaList.clear();
		// ���㐔���グ��
		this.generation++;
	}

	// �ꗗ��\��
	public void printCurrentGeneration() {
		int size = this.currentGenerationTankaList.size();
		System.out.println("---������(" + this.generation + ")---");
		for (int i = 0; i < size; i++) {
			System.out.print(i + "\t");
			currentGenerationTankaList.get(i).print(this.materialWord);
		}
	}

	// �ꗗ��\��
	public void printNextGeneration() {
		int size = this.nextGenerationTankaList.size();
		System.out.println("---������(" + this.generation + ")---");
		for (int i = 0; i < size; i++) {
			System.out.print(i + "\t");
			nextGenerationTankaList.get(i).print(this.materialWord);
		}
	}

	// �Z�̂������_����1����
	protected Tanka createRandaomTanka(MaterialWord materialWord) {
		Tanka tanka = new Tanka();
		for (int i = 0; i < 5; i++) {
			int n = CommonUtil.random(1, 4);
			for (int j = 0; j < n; j++) {
				Word tmpWord = materialWord.getRandomWord();
				tanka.putWord(i, tmpWord);
			}
		}
		return tanka;
	}

	// ���[���b�g�I���ŒZ�̂�1�I���index��Ԃ�
	protected int selectRoulette() {
		ArrayList<Double> rate1 = new ArrayList<Double>();
		ArrayList<Double> rate2 = new ArrayList<Double>();
		double sum = 0;
		for (int i = 0; i < this.tankaNum; i++) {
			sum += this.currentGenerationTankaList.get(i).getScore(this.materialWord);
			rate1.add(sum);
		}
		for (int i = 0; i < this.tankaNum; i++) {
			rate2.add(rate1.get(i) / sum);
		}
		double ran = Math.random();
		int index = 0;
		for (int i = 0; i < this.tankaNum; i++) {
			if (ran < rate2.get(i)) {
				index = i;
				break;
			}
		}
		return index;
	}

	// ��r�p�����N���X
	private class TankaComparator implements Comparator<Tanka> {
		@Override
		public int compare(Tanka p1, Tanka p2) {
			int p1Score = p1.getScore(IslandBase.this.materialWord);
			int p2Score = p2.getScore(IslandBase.this.materialWord);

			if (p1Score == p2Score) {
				return 0;
			} else {
				return p1.getScore(IslandBase.this.materialWord) < p2.getScore(IslandBase.this.materialWord) ? 1 : -1;
			}
		}
	}

}
