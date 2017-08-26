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

	// 0�`n-1�܂ł̗�����Ԃ�(n=3�Ȃ�0,1,2�̂ǂꂩ��Ԃ�)
	public static int random(int n) {
		return (int) (Math.random() * n);
	}

	// from�`to�܂ł̗�����Ԃ�(from=2,to=5�Ȃ�A2,3,4,5�̂ǂꂩ��Ԃ�)
	public static int random(int from, int to) {
		return (int) (from + Math.random() * (to - from + 1));
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
				// System.out.println(l);
				list.add(l);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	// �X���[�v����
	// �X���[�v
	public static void sleep(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException e) {
		}
	}

	// ������ɊG�������܂܂�Ă��邩���ʂ���
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
