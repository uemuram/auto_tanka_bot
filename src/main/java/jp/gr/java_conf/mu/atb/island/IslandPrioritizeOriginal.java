package jp.gr.java_conf.mu.atb.island;

import java.util.ArrayList;

import jp.gr.java_conf.mu.atb.dto.MaterialWord;
import jp.gr.java_conf.mu.atb.dto.Tanka;
import jp.gr.java_conf.mu.atb.dto.Word;
import jp.gr.java_conf.mu.atb.util.CommonUtil;

public class IslandPrioritizeOriginal extends IslandBase {

	// �ˑR�ψق̊m��
	private double mutationProbability;

	// �R���X�g���N�^
	public IslandPrioritizeOriginal() {
		super();
	}

	public IslandPrioritizeOriginal(int tankaNum, MaterialWord materialWord, double mutationProbability) {
		this();
		this.mutationProbability = mutationProbability;
		if ((tankaNum % 2) != 0) {
			throw new RuntimeException("��`�q���͋����̕K�v����");
		}
		this.tankaNum = tankaNum;
		this.materialWord = materialWord;
	}

	@Override
	// ��������𐶐�
	public void birth(MaterialWord materialWord) {
		super.birthOrder(materialWord);
	}

	// ���̐���𐶐�
	public void createNextGeneration(MaterialWord materialWord) {
		// �X�R�Amax�͎�����Ɏc�����߁A�����̐���2����
		int n = (this.tankaNum - 2) / 2;
		for (int i = 0; i < n; i++) {
			// ���[���b�g�I���ň�`�q��2�I��
			int parentIdx1 = this.selectRoulette();
			int parentIdx2 = this.selectRoulette();
			// �����_���Ńt�F�[�Y�����ւ���
			swapPhase(parentIdx1, parentIdx2, CommonUtil.random(5));
		}

		// �ˑR�ψ�
		for (Tanka tanka : this.nextGenerationTankaList) {
			for (int i = 0; i < 5; i++) {
				if (Math.random() < this.mutationProbability) {
					int type = CommonUtil.random(5);
					if (type == 0) {
						// �ǉ�
						tanka.insertWord(i, materialWord.getRandomWord());
					} else if (type == 1) {
						// �폜
						tanka.deleteWord(i);
					} else {
						// �X�V
						tanka.updateWord(i, materialWord.getRandomWord());
					}
				}
			}
		}

		// �X�R�A������2�̒Z�̂�������Ɏc��
		this.sort();
		Tanka score1stTanka = this.currentGenerationTankaList.get(0).clone();
		Tanka score2ndTanka = this.currentGenerationTankaList.get(1).clone();
		this.nextGenerationTankaList.add(score1stTanka);
		this.nextGenerationTankaList.add(score2ndTanka);
	}

	// 2�̒Z�̂̂����An�Ԗڂ̃t�F�[�Y�����ւ��Ď�����ɓ����
	private void swapPhase(int parentIdx1, int parentIdx2, int n) {
		// �e(�f�B�[�v�R�s�[)
		Tanka tanka1 = this.currentGenerationTankaList.get(parentIdx1).clone();
		Tanka tanka2 = this.currentGenerationTankaList.get(parentIdx2).clone();
		ArrayList<Word> phase1 = tanka1.getPhase(n);
		ArrayList<Word> phase2 = tanka2.getPhase(n);

		// �����p�ɁA�Z������null��ǉ����Ē��������낦��
		int size1 = phase1.size();
		int size2 = phase2.size();
		if (size1 > size2) {
			for (int i = 0; i < size1 - size2; i++) {
				phase2.add(null);
			}
		} else if (size1 < size2) {
			for (int i = 0; i < size2 - size1; i++) {
				phase1.add(null);
			}
		}
		int size = phase1.size();

		// ����ւ��n�_�������_���Ɍ���
		int p1 = CommonUtil.random(size);
		int p2 = CommonUtil.random(size);
		int from = CommonUtil.min(p1, p2);
		int to = CommonUtil.max(p1, p2);

		// ����ւ����s
		for (int i = from; i <= to; i++) {
			Word word1 = phase1.get(i);
			Word word2 = phase2.get(i);
			phase1.remove(i);
			phase2.remove(i);
			phase1.add(i, word2);
			phase2.add(i, word1);
		}

		// null������
		for (int i = size - 1; i >= 0; i--) {
			if (phase1.get(i) == null) {
				phase1.remove(i);
			}
			if (phase2.get(i) == null) {
				phase2.remove(i);
			}
		}

		// ������ɒǉ�
		this.nextGenerationTankaList.add(tanka1);
		this.nextGenerationTankaList.add(tanka2);
	}

}
