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
				System.out.println(l);
				list.add(l);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

}
