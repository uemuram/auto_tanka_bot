package jp.gr.java_conf.mu.atb.local;

import dto.AppearenceRate;

public class CleateTanka {
	public static void main(String[] args) {

		System.out.println("start");

		// 学習データを読み込み
		AppearenceRate appearenceRate = new AppearenceRate("AppearanceRate1.txt");
		appearenceRate.printRate1();

		System.out.println("end");
	}

}
