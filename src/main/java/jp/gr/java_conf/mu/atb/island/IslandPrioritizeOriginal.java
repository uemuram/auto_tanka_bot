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
			swapPhase(parentIdx1, parentIdx2, CommonUtil.random(5), CommonUtil.random(10));
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

	// 2�̒Z�̂̂����An�Ԗڂ̃t�F�[�Y��m�n�_�����ւ��Ď�����ɓ����
	// 0<=n<=4, 0<=m<=9
	private void swapPhase(int parentIdx1, int parentIdx2, int n, int m) {
		// �e(�f�B�[�v�R�s�[)
		Tanka tanka1 = this.currentGenerationTankaList.get(parentIdx1).clone();
		Tanka tanka2 = this.currentGenerationTankaList.get(parentIdx2).clone();

		// n�Ԗڂ̃t�F�[�Y������

		// n+1�`�Ō�܂ł�����
		for (int i = n + 1; i < 5; i++) {
			ArrayList<Word> phase1 = tanka1.getPhase(i);
			ArrayList<Word> phase2 = tanka2.getPhase(i);
			tanka1.updatePhase(i, phase2);
			tanka2.updatePhase(i, phase1);
		}

		// ������ɒǉ�
		this.nextGenerationTankaList.add(tanka1);
		this.nextGenerationTankaList.add(tanka2);
	}

}
