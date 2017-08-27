package jp.gr.java_conf.mu.atb.island;

import jp.gr.java_conf.mu.atb.dto.MaterialWord;

public class IslandNormal extends IslandBase {

	// コンストラクタ
	public IslandNormal() {
		super();
	}

	public IslandNormal(int tankaNum) {
		this();
		if ((tankaNum % 2) != 0) {
			throw new RuntimeException("遺伝子数は偶数の必要あり");
		}
		this.tankaNum = tankaNum;
	}

	@Override
	// 初期世代を生成
	public void birth(MaterialWord materialWord) {
		super.birth(materialWord);
	}

	// 次の世代を生成
	public void createNextGeneration(MaterialWord materialWord) {
		// スコアmaxは次世代に残すため、交叉の数は2引く
		int n = (this.tankaNum - 2) / 2;
		for (int i = 0; i < n; i++) {
			// ルーレット選択で遺伝子を2つ選択
			int parentIdx1 = this.selectRoulette();
			int parentIdx2 = this.selectRoulette();

			System.out.println(parentIdx1);
			System.out.println(parentIdx2);
		}

	}

}
