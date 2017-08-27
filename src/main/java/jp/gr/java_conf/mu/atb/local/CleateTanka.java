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
		ArrayList<String> tweetTextList = twitterUtil.searchTweetText("�����Ђ�", 30);

		// Twitter����擾�����e�L�X�g�𗘗p���āA�ޗ��ƂȂ�P��𐮗�
		System.out.println("----------");
		MaterialWord materialWord = new MaterialWord(tweetTextList);
		materialWord.print();

		// �Z�̂𐶐�
		System.out.println("----------");

		// GA�p�̓��𐶐�
		IslandNormal islandNormal = new IslandNormal(4);

		// ��������𐶐�
		islandNormal.birth(materialWord);
		islandNormal.print();

		// ������𐶐�
		islandNormal.createNextGeneration(materialWord);

		// ������

		System.out.println("end");
	}
}
