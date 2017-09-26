package jp.gr.java_conf.mu.atb.local;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import jp.gr.java_conf.mu.atb.dto.MaterialWord;
import jp.gr.java_conf.mu.atb.dto.Tanka;
import jp.gr.java_conf.mu.atb.dto.Word;
import jp.gr.java_conf.mu.atb.island.IslandNormal;
import jp.gr.java_conf.mu.atb.island.IslandPrioritizeOriginal;
import jp.gr.java_conf.mu.atb.util.CommonUtil;
import jp.gr.java_conf.mu.atb.util.TwitterUtil;

public class CleateTanka {
	public static void main(String[] args) {

		System.out.println("start");

		// テーマを決定
		String theme = getTheme();
		System.out.println("テーマ: " + theme);

		// Twitter利用準備
		TwitterUtil twitterUtil = new TwitterUtil();

		// Twitterからキーワードで検索した結果のテキストを取得
		System.out.println("----------");
		ArrayList<String> tweetTextList = twitterUtil.searchTweetText(theme, 30);
		// 検索失敗した場合はテーマを変えてリトライ
		if (tweetTextList == null) {
			System.out.println("検索失敗したため標準テーマでリトライ");
			theme = getStaticTheme();
			System.out.println("テーマ: " + theme);
			tweetTextList = twitterUtil.searchTweetText(theme, 30);
			if (tweetTextList == null) {
				System.out.println("検索失敗したため終了");
				return;
			}
		}

		// Twitterから取得したテキストを利用して、材料となる単語を整理
		System.out.println("----------");
		MaterialWord materialWord = new MaterialWord(tweetTextList);
		if (materialWord.getCount() == 0) {
			System.out.println("素材となるツイートを1件も取得できなかったため標準テーマでリトライ");
			theme = getStaticTheme();
			System.out.println("テーマ: " + theme);
			tweetTextList = twitterUtil.searchTweetText(theme, 30);
			System.out.println("----------");
			materialWord = new MaterialWord(tweetTextList);
			if (materialWord.getCount() == 0) {
				System.out.println("素材となるツイートを1件も取得できなかったため終了");
				return;
			}
		}

		// 短歌を生成
		String createdTanka = createTanka(materialWord, theme);
		System.out.println("----------");

		String tweetStr = "";
		tweetStr += "テーマ:【" + theme + "】\n";
		tweetStr += createdTanka;
		System.out.println(tweetStr);

		// 次のテーマを選択
		Word nextThemeWord = materialWord.getRandomNoum(theme);
		String nextTheme = nextThemeWord == null ? "" : nextThemeWord.getCharTerm();
		System.out.println("");
		System.out.println("次のテーマ:" + nextTheme);
		saveTheme(nextTheme);

		System.out.println("end");
	}

	// テーマをファイルに保存する
	private static void saveTheme(String theme) {
		// もとのテーマファイルが存在すれば削除する
		String themeFilePath = System.getenv("themeFilePath");
		File file = new File(themeFilePath);
		if (file.exists()) {
			if (!file.delete()) {
				System.out.println("ファイル:[" + themeFilePath + "]の削除に失敗しました");
				return;
			}
		}
		// テーマをファイルに保存する
		try {
			File newFile = new File(themeFilePath);
			FileWriter fw = new FileWriter(newFile, true);
			fw.write(theme);
			fw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// テーマを返す
	private static String getTheme() {
		// テーマを読み込む
		String themeFilePath = System.getenv("themeFilePath");
		System.out.println("テーマを取得: " + themeFilePath);
		ArrayList<String> themeList = CommonUtil.readFileWithFullPath(themeFilePath, false);

		String theme;
		// 読み込めない場合は固定のテーマを返す
		if (themeList == null || themeList.size() == 0) {
			theme = getStaticTheme();
		} else {
			theme = themeList.get(0);
			if (theme.length() == 0) {
				theme = getStaticTheme();
			}
		}
		return theme;
	}

	private static String getStaticTheme() {
		return "ツイッター";
	}

	// テーマをもとにして短歌を生成する
	private static String createTanka(MaterialWord materialWord, String theme) {
		materialWord.print1();
		materialWord.print2();

		// 短歌を生成
		System.out.println("----------");

		// // test
		// if (false) {
		// IslandPrioritizeOriginal islandTest = new
		// IslandPrioritizeOriginal(20, materialWord, 0.05);
		// islandTest.birth(materialWord);
		// islandTest.sort();
		// islandTest.printCurrentGeneration();
		//
		// islandTest.createNextGeneration(materialWord);
		// islandTest.incrementGeneration();
		// islandTest.sort();
		// islandTest.printCurrentGeneration();
		//
		// islandTest.createNextGeneration(materialWord);
		// islandTest.incrementGeneration();
		// islandTest.sort();
		// islandTest.printCurrentGeneration();
		//
		// islandTest.createNextGeneration(materialWord);
		// islandTest.incrementGeneration();
		// islandTest.sort();
		// islandTest.printCurrentGeneration();
		//
		// return;
		// }

		// GA用の島を生成
		IslandNormal islandNormal = new IslandNormal(20, materialWord, 0.01);
		islandNormal.birth(materialWord);
		islandNormal.sort();
		islandNormal.printCurrentGeneration();

		IslandNormal islandNormal2 = new IslandNormal(20, materialWord, 0.05);
		islandNormal2.birth(materialWord);
		islandNormal2.sort();
		islandNormal2.printCurrentGeneration();

		IslandPrioritizeOriginal islandNormal3 = new IslandPrioritizeOriginal(20, materialWord, 0.05);
		islandNormal3.birth(materialWord);
		islandNormal3.sort();
		islandNormal3.printCurrentGeneration();

		boolean emigrate = true;
		int maxGeneration = 1000;
		int emigrateInterval = 300;
		int a = maxGeneration / emigrateInterval;

		if (emigrate) {
			for (int i = 0; i < a; i++) {
				for (int j = 0; j < emigrateInterval; j++) {
					islandNormal.createNextGeneration(materialWord);
					islandNormal.incrementGeneration();

					islandNormal2.createNextGeneration(materialWord);
					islandNormal2.incrementGeneration();

					islandNormal3.createNextGeneration(materialWord);
					islandNormal3.incrementGeneration();
				}
				islandNormal.emigrateTo(islandNormal2, 1);
				islandNormal2.emigrateTo(islandNormal3, 1);
				islandNormal3.emigrateTo(islandNormal, 1);
			}
		} else {
			for (int i = 0; i < maxGeneration; i++) {
				islandNormal.createNextGeneration(materialWord);
				islandNormal.incrementGeneration();

				islandNormal2.createNextGeneration(materialWord);
				islandNormal2.incrementGeneration();

				islandNormal3.createNextGeneration(materialWord);
				islandNormal3.incrementGeneration();
			}
		}

		islandNormal.sort();
		islandNormal.printCurrentGeneration();

		islandNormal2.sort();
		islandNormal2.printCurrentGeneration();

		islandNormal3.sort();
		islandNormal3.printCurrentGeneration();

		System.out.println("----------");
		islandNormal.getTanka(0).printWord(materialWord);
		System.out.println("----------");
		islandNormal2.getTanka(0).printWord(materialWord);
		System.out.println("----------");
		islandNormal3.getTanka(0).printWord(materialWord);

		// 最もスコアの高い短歌を返す
		ArrayList<Tanka> tankaList = new ArrayList<Tanka>();
		tankaList.add(islandNormal.getTanka(0));
		tankaList.add(islandNormal2.getTanka(0));
		tankaList.add(islandNormal3.getTanka(0));

		int maxScore = -1;
		Tanka maxScoreTanka = tankaList.get(0);
		for (int i = 0; i < tankaList.size(); i++) {
			int tmpScore = tankaList.get(i).getScore(materialWord);
			if (tmpScore > maxScore) {
				maxScore = tmpScore;
				maxScoreTanka = tankaList.get(i);
			}
		}
		return maxScoreTanka.toString();
	}

}
