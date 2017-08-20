package dto;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;

public class AppearenceRate {

	private HashMap<String, HashMap<String, Integer>> rate1;

	// �R���X�g���N�^
	public AppearenceRate() {
		// ������
		this.rate1 = new HashMap<String, HashMap<String, Integer>>();
	}

	// key1�̌��key2���o�Ă������Ԃ�
	public int getCount1(String key1, String key2) {
		HashMap<String, Integer> tmpMap1 = this.rate1.get(key1);
		if (tmpMap1 == null) {
			return 0;
		}
		Integer count = tmpMap1.get(key2);
		if (count == null) {
			return 0;
		}
		return count;
	}

	// rate1��key1-key2�̌��𑝂₷
	public void incrementCount1(String key1, String key2) {
		int count = this.getCount1(key1, key2);
		HashMap<String, Integer> tmpMap1 = this.rate1.get(key1);
		if (tmpMap1 == null) {
			tmpMap1 = new HashMap<String, Integer>();
		}
		tmpMap1.put(key2, count + 1);
		this.rate1.put(key1, tmpMap1);
	}

	// rate1����ʂɕ\������
	public void printRate1() throws IOException {
		outputRate1(null);
	}

	// rate1���t�@�C���o�͂���
	public void fileOutRate1(String fileName) throws IOException {
		outputRate1(fileName);
	}

	// rate1����ʏo�� or ��ʂƃt�@�C���ɏo��
	private void outputRate1(String fileName) throws IOException {
		// �t�@�C�������w�肳���΃t�@�C���o��
		boolean fileOutput = fileName != null ? true : false;
		PrintWriter pw = null;
		if (fileOutput) {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
		}
		// �\�[�g�p
		Object[] keys1 = this.rate1.keySet().toArray();
		Arrays.sort(keys1);
		for (int i = 0; i < keys1.length; i++) {
			String key1 = (String) keys1[i];
			HashMap<String, Integer> tmpMap = this.rate1.get(key1);
			// �\�[�g�p
			Object[] keys2 = tmpMap.keySet().toArray();
			Arrays.sort(keys2);
			for (int j = 0; j < keys2.length; j++) {
				String key2 = (String) keys2[j];
				int count = tmpMap.get(key2);
				System.out.println(key1 + "\t" + key2 + "\t" + count);
				if (fileOutput) {
					pw.println(key1 + "\t" + key2 + "\t" + count);
				}
			}
		}
		if (fileOutput) {
			pw.close();
		}
	}

}
