package jp.gr.java_conf.mu.atb.local;

import java.util.ArrayList;

import jp.gr.java_conf.mu.atb.dto.MaterialWord;
import jp.gr.java_conf.mu.atb.island.IslandNormal;
import jp.gr.java_conf.mu.atb.util.TwitterUtil;

public class CleateTanka {
	public static void main(String[] args) {

		System.out.println("start");

		// Twitter���p����
		TwitterUtil twitterUtil = new TwitterUtil();

		// Twitter����L�[���[�h�Ō����������ʂ̃e�L�X�g���擾
		System.out.println("----------");
		ArrayList<String> tweetTextList = twitterUtil.searchTweetText("�v���L���A", 30);

		// Twitter����擾�����e�L�X�g�𗘗p���āA�ޗ��ƂȂ�P��𐮗�
		System.out.println("----------");
		MaterialWord materialWord = new MaterialWord(tweetTextList);
		if (materialWord.getCount() == 0) {
			System.out.println("�f�ނƂȂ�c�C�[�g��1�����擾�ł��Ȃ��������ߏI��");
			return;
		}

		materialWord.print1();
		materialWord.print2();

		// �Z�̂𐶐�
		System.out.println("----------");

		// GA�p�̓��𐶐�
		IslandNormal islandNormal = new IslandNormal(20);
		islandNormal.birth(materialWord);
		islandNormal.sort();
		islandNormal.printCurrentGeneration();

		IslandNormal islandNormal2 = new IslandNormal(20);
		islandNormal2.birth(materialWord);
		islandNormal2.sort();
		islandNormal2.printCurrentGeneration();

		IslandNormal islandNormal3 = new IslandNormal(20);
		islandNormal3.birth(materialWord);
		islandNormal3.sort();
		islandNormal3.printCurrentGeneration();

		for (int i = 0; i < 1000; i++) {
			islandNormal.createNextGeneration(materialWord);
			islandNormal.incrementGeneration();

			islandNormal2.createNextGeneration(materialWord);
			islandNormal2.incrementGeneration();

			islandNormal3.createNextGeneration(materialWord);
			islandNormal3.incrementGeneration();
		}
		islandNormal.sort();
		islandNormal.printCurrentGeneration();

		islandNormal2.sort();
		islandNormal2.printCurrentGeneration();

		islandNormal3.sort();
		islandNormal3.printCurrentGeneration();

		System.out.println("----------");
		islandNormal.getTanka(0).printWord();
		System.out.println("----------");
		islandNormal2.getTanka(0).printWord();
		System.out.println("----------");
		islandNormal3.getTanka(0).printWord();

		System.out.println("end");
	}
}
