package jp.gr.java_conf.mu.atb.island;

import jp.gr.java_conf.mu.atb.dto.MaterialWord;

public class IslandNormal extends IslandBase {

	// �R���X�g���N�^
	public IslandNormal() {
		super();
	}

	public IslandNormal(int tankaNum) {
		this();
		if ((tankaNum % 2) != 0) {
			throw new RuntimeException("��`�q���͋����̕K�v����");
		}
		this.tankaNum = tankaNum;
	}

	@Override
	// ��������𐶐�
	public void birth(MaterialWord materialWord) {
		super.birth(materialWord);
	}

	// ���̐���𐶐�
	public void createNextGeneration(MaterialWord materialWord) {
		// �X�R�Amax�͎�����Ɏc�����߁A�����̐���2����
		int n = (this.tankaNum - 2) / 2;
		for (int i = 0; i < n; i++) {
			// ���[���b�g�I���ň�`�q��2�I��
			int parentIdx1 = this.selectRoulette();
			int parentIdx2 = this.selectRoulette();

			System.out.println(parentIdx1);
			System.out.println(parentIdx2);
		}

	}

}
