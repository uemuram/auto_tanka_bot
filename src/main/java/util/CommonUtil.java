package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CommonUtil {

	// テスト
	public static void test() {
		System.out.println("test");
	}

	// 0〜n-1までの乱数を返す(n=3なら0,1,2のどれかを返す)
	public static int random(int n) {
		return (int) (Math.random() * n);
	}

	// from〜toまでの乱数を返す(from=2,to=5なら、2,3,4,5のどれかを返す)
	public static int random(int from, int to) {
		return (int) (from + Math.random() * (to - from + 1));
	}

	// 指定されたファイルを読み込んで配列に格納して返す
	public static ArrayList<String> readFile(String filename) {
		// "src/main/resources"からファイルを読み込む．
		InputStream is = ClassLoader.getSystemResourceAsStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		ArrayList<String> list = new ArrayList<String>();
		String l = null;
		try {
			while ((l = br.readLine()) != null) {
				// コメント行と空行をスキップ
				if (l.length() == 0 || l.startsWith("#")) {
					continue;
				}
				// System.out.println(l);
				list.add(l);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	// スリープする
	// スリープ
	public static void sleep(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException e) {
		}
	}

	// 文字列に絵文字が含まれているか判別する
	public static boolean isSurrogate(String text) {
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (Character.isHighSurrogate(c) || Character.isLowSurrogate(c)) {
				return true;
			}
		}
		return false;
	}
}
