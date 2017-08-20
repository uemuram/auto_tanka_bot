package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CommonUtil {

	// �e�X�g
	public static void test() {
		System.out.println("test");
	}

	// �w�肳�ꂽ�t�@�C����ǂݍ���Ŕz��Ɋi�[���ĕԂ�
	public static ArrayList<String> readFile(String filename) {
		// "src/main/resources"����t�@�C����ǂݍ��ށD
		InputStream is = ClassLoader.getSystemResourceAsStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		ArrayList<String> list = new ArrayList<String>();
		String l = null;
		try {
			while ((l = br.readLine()) != null) {
				// �R�����g�s�Ƌ�s���X�L�b�v
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
