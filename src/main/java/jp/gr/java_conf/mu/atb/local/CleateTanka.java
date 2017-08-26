package jp.gr.java_conf.mu.atb.local;

import java.util.ArrayList;

import jp.gr.java_conf.mu.atb.dto.AppearenceRate;
import jp.gr.java_conf.mu.atb.dto.MaterialWord;
import jp.gr.java_conf.mu.atb.dto.Tanka;
import jp.gr.java_conf.mu.atb.dto.Word;
import jp.gr.java_conf.mu.atb.util.CommonUtil;
import jp.gr.java_conf.mu.atb.util.TwitterUtil;

public class CleateTanka {
	public static void main(String[] args) {

		System.out.println("start");

		// �w�K�f�[�^��ǂݍ���
		System.out.println("----------");
		AppearenceRate appearenceRate = new AppearenceRate("AppearanceRate1.txt");
		appearenceRate.printRate1();

		// Twitter���p����
		TwitterUtil twitterUtil = new TwitterUtil();

		// Twitter����L�[���[�h�Ō����������ʂ̃e�L�X�g���擾
		System.out.println("----------");
		ArrayList<String> tweetTextList = twitterUtil.searchTweetText("�L���A�z�C�b�v", 30);

		// Twitter����擾�����e�L�X�g�𗘗p���āA�ޗ��ƂȂ�P��𐮗�
		System.out.println("----------");
		MaterialWord materialWord = new MaterialWord(tweetTextList);
		materialWord.print();

		// �Z�̂𐶐�
		System.out.println("----------");

		for (int i = 0; i < 10; i++) {
			Tanka tanka = createTanka(appearenceRate, materialWord);
			System.out.println(getScore(tanka) + "\t" + tanka.toString());
		}

		System.out.println("end");
	}

	// �K���ɒZ�̂𐶐�
	private static Tanka createTanka(AppearenceRate appearenceRate, MaterialWord materialWord) {
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

	// �Z�̂��X�R�A�����O
	private static int getScore(Tanka tanka) {
		// ���_�@�B�����X�R�A
		int score = 300;
		int[] phaseLength = { 5, 7, 5, 7, 7 };

		for (int i = 0; i < 5; i++) {
			// 57577���炸��Ă���ƌ��_
			score -= ((Math.abs(tanka.getPhaseLength(i) - phaseLength[i])) * 10);
		}

		return score;
	}

}
