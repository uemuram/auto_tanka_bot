package jp.gr.java_conf.mu.atb.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class CleateTanka {
	public static void main(String[] args) {

		System.out.println("start");

		// �w�K�f�[�^��ǂݍ���
		readAppearanceRate1();

		System.out.println("end");
	}

	// �o����1��ǂݍ���
	private static HashMap<String, HashMap<String, Integer>> readAppearanceRate1() {

		// "src/main/resources"����t�@�C����ǂݍ��ށD
		InputStream is = ClassLoader.getSystemResourceAsStream("AppearanceRate1.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		// ArrayList<String> tankaList = new ArrayList<String>();
		String l = null;
		try {
			while ((l = br.readLine()) != null) {
				// �R�����g�s�Ƌ�s���X�L�b�v
				if (l.length() == 0 || l.startsWith("#")) {
					continue;
				}
				System.out.println(l);
				// tankaList.add(l);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return tankaList;
		return null;
	}

}
