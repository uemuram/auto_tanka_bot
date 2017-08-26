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

		// 学習データを読み込み
		System.out.println("----------");
		AppearenceRate appearenceRate = new AppearenceRate("AppearanceRate1.txt");
		appearenceRate.printRate1();

		// Twitter利用準備
		TwitterUtil twitterUtil = new TwitterUtil();

		// Twitterからキーワードで検索した結果のテキストを取得
		System.out.println("----------");
		ArrayList<String> tweetTextList = twitterUtil.searchTweetText("キュアホイップ", 30);

		// Twitterから取得したテキストを利用して、材料となる単語を整理
		System.out.println("----------");
		MaterialWord materialWord = new MaterialWord(tweetTextList);
		materialWord.print();

		// 短歌を生成
		System.out.println("----------");

		for (int i = 0; i < 10; i++) {
			Tanka tanka = createTanka(appearenceRate, materialWord);
			System.out.println(getScore(tanka) + "\t" + tanka.toString());
		}

		System.out.println("end");
	}

	// 適当に短歌を生成
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

	// 短歌をスコアリング
	private static int getScore(Tanka tanka) {
		// 減点法。初期スコア
		int score = 300;
		int[] phaseLength = { 5, 7, 5, 7, 7 };

		for (int i = 0; i < 5; i++) {
			// 57577からずれていると減点
			score -= ((Math.abs(tanka.getPhaseLength(i) - phaseLength[i])) * 10);
		}

		return score;
	}

}
