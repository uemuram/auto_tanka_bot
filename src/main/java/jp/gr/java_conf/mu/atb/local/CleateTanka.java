package jp.gr.java_conf.mu.atb.local;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import jp.gr.java_conf.mu.atb.dto.MaterialWord;
import jp.gr.java_conf.mu.atb.dto.Tanka;
import jp.gr.java_conf.mu.atb.dto.Word;
import jp.gr.java_conf.mu.atb.island.IslandPrioritizeOriginal;
import jp.gr.java_conf.mu.atb.util.CommonUtil;
import jp.gr.java_conf.mu.atb.util.TwitterUtil;

public class CleateTanka {
	public static void main(String[] args) {

		System.out.println("start");

		// テーマを決定
		String searchKey = getTheme();
		System.out.println("検索キー: " + searchKey);

		// Twitter利用準備
		TwitterUtil twitterUtil = new TwitterUtil();

		// Twitterからキーワードで検索した結果のテキストを取得
		System.out.println("----------");
		ArrayList<String> tweetTextList = twitterUtil.searchTweetText(searchKey, 30);
		// 検索失敗した場合はテーマを変えてリトライ
		if (tweetTextList == null) {
			System.out.println("検索失敗したため標準テーマでリトライ");
			searchKey = getStaticTheme();
			System.out.println("検索キー: " + searchKey);
			tweetTextList = twitterUtil.searchTweetText(searchKey, 30);
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
			searchKey = getStaticTheme();
			System.out.println("検索キー: " + searchKey);
			tweetTextList = twitterUtil.searchTweetText(searchKey, 30);
			System.out.println("----------");
			materialWord = new MaterialWord(tweetTextList);
			if (materialWord.getCount() == 0) {
				System.out.println("素材となるツイートを1件も取得できなかったため終了");
				return;
			}
		}

		// 短歌を生成
		String createdTanka = createTanka(materialWord);
		System.out.println("----------");

		// 検索キーが複数ある場合に1つ目をテーマにする
		String[] themes = searchKey.split(" ");
		String theme = themes[0];
		System.out.println("検索キー:" + searchKey + "\n");

		// ツイート実行
		String tweetStr = "";
		tweetStr += "テーマ:【" + theme + "】\n";
		tweetStr += createdTanka;
		System.out.println(tweetStr);

		// テーマを読み込む
		boolean tweet = false;
		String doTweet = System.getenv("doTweet");
		if (doTweet != null && doTweet.equals("true")) {
			tweet = true;
		}
		if (tweet) {
			twitterUtil.tweet(tweetStr);
		}

		// 次のテーマを選択
		Word nextThemeWord = materialWord.getRandomNoum(theme);
		Word nextThemeWord2 = materialWord.getRandomNoum(theme);

		String nextSearchKey = "";
		nextSearchKey += (nextThemeWord == null ? "" : nextThemeWord.getCharTerm());
		nextSearchKey += " ";
		nextSearchKey += (nextThemeWord2 == null ? "" : nextThemeWord2.getCharTerm());

		System.out.println("");
		System.out.println("次の検索キー:" + nextSearchKey);
		saveTheme(nextSearchKey);

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
			theme = theme.trim();
			if (theme.length() == 0) {
				theme = getStaticTheme();
			}
		}
		return theme;
	}

	private static String getStaticTheme() {
		ArrayList<String> themes = new ArrayList<String>();
		themes.add("りんご");
		themes.add("みかん");
		themes.add("いちご");
		themes.add("バナナ");
		themes.add("スイカ");
		themes.add("メロン");
		themes.add("ぶどう");
		themes.add("パイナップル");
		themes.add("レモン");
		int i = CommonUtil.random(themes.size());
		return themes.get(i);
	}

	// テーマをもとにして短歌を生成する
	private static String createTanka(MaterialWord materialWord) {
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
		IslandPrioritizeOriginal island1 = new IslandPrioritizeOriginal(20, materialWord, 0.01);
		island1.birth(materialWord);
		island1.sort();
		island1.printCurrentGeneration();

		IslandPrioritizeOriginal island2 = new IslandPrioritizeOriginal(20, materialWord, 0.05);
		island2.birth(materialWord);
		island2.sort();
		island2.printCurrentGeneration();

		IslandPrioritizeOriginal island3 = new IslandPrioritizeOriginal(20, materialWord, 0.05);
		island3.birth(materialWord);
		island3.sort();
		island3.printCurrentGeneration();

		boolean emigrate = true;
		int maxGeneration = 1000;
		int emigrateInterval = 300;

		for (int i = 0; i < maxGeneration; i++) {
			island1.createNextGeneration(materialWord);
			island1.incrementGeneration();

			island2.createNextGeneration(materialWord);
			island2.incrementGeneration();

			island3.createNextGeneration(materialWord);
			island3.incrementGeneration();

			island1.sort();
			island2.sort();
			island3.sort();
			System.out.println(island1.getGeneration() + ":" + island1.getTanka(0).getScoreStr(materialWord) + "\t"
					+ island1.getTanka(0).toString());
			System.out.println(island2.getGeneration() + ":" + island2.getTanka(0).getScoreStr(materialWord) + "\t"
					+ island2.getTanka(0).toString());
			System.out.println(island3.getGeneration() + ":" + island3.getTanka(0).getScoreStr(materialWord) + "\t"
					+ island3.getTanka(0).toString());
			System.out.println("");

			if (emigrate && i % emigrateInterval == 0 && i > 0) {
				System.out.println("--移住--");
				island1.emigrateTo(island2, 1);
				island2.emigrateTo(island3, 1);
				island3.emigrateTo(island1, 1);
			}
		}

		island1.sort();
		island1.printCurrentGeneration();

		island2.sort();
		island2.printCurrentGeneration();

		island3.sort();
		island3.printCurrentGeneration();

		System.out.println("----------");
		island1.getTanka(0).printDetail(materialWord);
		System.out.println("----------");
		island2.getTanka(0).printDetail(materialWord);
		System.out.println("----------");
		island3.getTanka(0).printDetail(materialWord);

		// 最もスコアの高い短歌を返す
		ArrayList<Tanka> tankaList = new ArrayList<Tanka>();
		tankaList.add(island1.getTanka(0));
		tankaList.add(island2.getTanka(0));
		tankaList.add(island3.getTanka(0));

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
