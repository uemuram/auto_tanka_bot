package dto;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import util.CommonUtil;

public class AppearenceRate {

	private HashMap<String, HashMap<String, Integer>> rate1;

	// �R���X�g���N�^(��f�[�^)
	public AppearenceRate() {
		// ������
		this.rate1 = new HashMap<String, HashMap<String, Integer>>();
	}

	// �R���X�g���N�^(�t�@�C������)
	public AppearenceRate(String fileName) {
		this.rate1 = new HashMap<String, HashMap<String, Integer>>();
		ArrayList<String> appearenceData = CommonUtil.readFile(fileName);

		for (String record : appearenceData) {
			String[] data = record.split("\t", -1);
			setCount1(data[0], data[1], Integer.parseInt(data[2]));
		}

	}

	// �w�肳�ꂽ�L�[�̎��̃L�[��Ԃ�
	public String getNextKey(String currentKey) {
		HashMap<String, Integer> rateMap = this.rate1.get(currentKey);
		if (rateMap == null) {
			return null;
		}

		
		
		return "";
	}

	// rate1�́Akey1�̌��key2���o�Ă������Ԃ�
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

	// rate1�́Akey1-key2�̌���1���₷
	public void incrementCount1(String key1, String key2) {
		int count = this.getCount1(key1, key2);
		setCount1(key1, key2, count + 1);
	}

	// rate1�́Akey1-key2�Ɏw�肵���l������
	private void setCount1(String key1, String key2, int count) {
		HashMap<String, Integer> tmpMap1 = this.rate1.get(key1);
		if (tmpMap1 == null) {
			tmpMap1 = new HashMap<String, Integer>();
		}
		tmpMap1.put(key2, count);
		this.rate1.put(key1, tmpMap1);
	}

	// rate1����ʂɕ\������
	public void printRate1() {
		try {
			outputRate1(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
