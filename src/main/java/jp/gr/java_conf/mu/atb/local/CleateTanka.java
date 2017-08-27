package jp.gr.java_conf.mu.atb.local;

import java.util.ArrayList;

import jp.gr.java_conf.mu.atb.dto.MaterialWord;
import jp.gr.java_conf.mu.atb.island.IslandNormal;
import jp.gr.java_conf.mu.atb.util.TwitterUtil;

public class CleateTanka {
	public static void main(String[] args) {

		System.out.println("start");

		// Twitter利用準備
		TwitterUtil twitterUtil = new TwitterUtil();

		// Twitterからキーワードで検索した結果のテキストを取得
		System.out.println("----------");
		ArrayList<String> tweetTextList = twitterUtil.searchTweetText("お茶漬け", 30);

		// Twitterから取得したテキストを利用して、材料となる単語を整理
		System.out.println("----------");
		MaterialWord materialWord = new MaterialWord(tweetTextList);
		materialWord.print();

		// 短歌を生成
		System.out.println("----------");

		// GA用の島を生成
		IslandNormal islandNormal = new IslandNormal(4);

		// 初期世代を生成
		islandNormal.birth(materialWord);
		islandNormal.print();

		// 次世代を生成
		islandNormal.createNextGeneration(materialWord);

		// 世代交代

		System.out.println("end");
	}
}
