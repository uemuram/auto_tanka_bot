package jp.gr.java_conf.mu.atb.island;

import java.util.ArrayList;

import jp.gr.java_conf.mu.atb.dto.MaterialWord;
import jp.gr.java_conf.mu.atb.dto.Tanka;
import jp.gr.java_conf.mu.atb.dto.Word;
import jp.gr.java_conf.mu.atb.util.CommonUtil;

public class IslandBase {

	// 保持する短歌の数
	protected int tankaNum = 40;

	// 現世代
	protected ArrayList<Tanka> currentGenerationTankaList;
	// 次世代
	protected ArrayList<Tanka> nextGenerationTankaList;

	// コンストラクタ
	public IslandBase() {
		this.currentGenerationTankaList = new ArrayList<Tanka>();
		this.nextGenerationTankaList = new ArrayList<Tanka>();
	}

	// 初期世代を生成
	public void birth(MaterialWord materialWord) {
		for (int i = 0; i < this.tankaNum; i++) {
			Tanka tanka = createRandaomTanka(materialWord);
			this.currentGenerationTankaList.add(tanka);
		}
	}

	// 一覧を表示
	public void print() {
		int size = this.currentGenerationTankaList.size();
		for (int i = 0; i < size; i++) {
			currentGenerationTankaList.get(i).print();
		}
	}

	// 短歌をランダムで1つ生成
	protected Tanka createRandaomTanka(MaterialWord materialWord) {
		Tanka tanka = new Tanka();
		for (int i = 0; i < 5; i++) {
			int n = CommonUtil.random(1, 4);
			for (int j = 0; j < n; j++) {
				Word tmpWord = materialWord.getRandomWord();
				tanka.putWord(i, tmpWord);
			}
		}
		return tanka;
	}

	// ルーレット選択で短歌を1つ選んでindexを返す
	protected int selectRoulette() {
		ArrayList<Double> rate1 = new ArrayList<Double>();
		ArrayList<Double> rate2 = new ArrayList<Double>();
		double sum = 0;
		for (int i = 0; i < this.tankaNum; i++) {
			sum += this.currentGenerationTankaList.get(i).getScore();
			rate1.add(sum);
		}
		for (int i = 0; i < this.tankaNum; i++) {
			rate2.add(rate1.get(i) / sum);
		}
		double ran = Math.random();
		int index = 0;
		for (int i = 0; i < this.tankaNum; i++) {
			if (ran < rate2.get(i)) {
				index = i;
				break;
			}
		}
		return index;
	}

}
