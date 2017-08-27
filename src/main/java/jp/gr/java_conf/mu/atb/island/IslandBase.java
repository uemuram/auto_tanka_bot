package jp.gr.java_conf.mu.atb.island;

import java.util.ArrayList;

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

	// �R���X�g���N�^
	public IslandBase() {
		this.currentGenerationTankaList = new ArrayList<Tanka>();
		this.nextGenerationTankaList = new ArrayList<Tanka>();
	}

	// ��������𐶐�
	public void birth(MaterialWord materialWord) {
		for (int i = 0; i < this.tankaNum; i++) {
			Tanka tanka = createRandaomTanka(materialWord);
			this.currentGenerationTankaList.add(tanka);
		}
	}

	// �ꗗ��\��
	public void print() {
		int size = this.currentGenerationTankaList.size();
		for (int i = 0; i < size; i++) {
			currentGenerationTankaList.get(i).print();
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
			sum += this.currentGenerationTankaList.get(i).getScore();
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

}
