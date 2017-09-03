package jp.gr.java_conf.mu.atb.island;

import java.util.ArrayList;
import java.util.Comparator;

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
	// 世代数
	protected int generation;
	// 素材集
	protected MaterialWord materialWord;

	// コンストラクタ
	public IslandBase() {
		this.currentGenerationTankaList = new ArrayList<Tanka>();
		this.nextGenerationTankaList = new ArrayList<Tanka>();
		this.generation = 1;
	}

	// n番目の短歌を返す
	public Tanka getTanka(int n) {
		return currentGenerationTankaList.get(n);
	}

	// 初期世代を生成
	public void birth(MaterialWord materialWord) {
		for (int i = 0; i < this.tankaNum; i++) {
			Tanka tanka = createRandaomTanka(materialWord);
			this.currentGenerationTankaList.add(tanka);
		}
	}

	// 現世代をソートする
	public void sort() {
		this.currentGenerationTankaList.sort(new TankaComparator());
	}

	// 世代を1つ進める
	public void incrementGeneration() {
		// 次世代を現世代にコピー
		this.currentGenerationTankaList.clear();
		for (Tanka tanka : this.nextGenerationTankaList) {
			currentGenerationTankaList.add(tanka);
		}
		// 次世代をクリア
		this.nextGenerationTankaList.clear();
		// 世代数を上げる
		this.generation++;
	}

	// 一覧を表示
	public void printCurrentGeneration() {
		int size = this.currentGenerationTankaList.size();
		System.out.println("---現世代(" + this.generation + ")---");
		for (int i = 0; i < size; i++) {
			System.out.print(i + "\t");
			currentGenerationTankaList.get(i).print(this.materialWord);
		}
	}

	// 一覧を表示
	public void printNextGeneration() {
		int size = this.nextGenerationTankaList.size();
		System.out.println("---次世代(" + this.generation + ")---");
		for (int i = 0; i < size; i++) {
			System.out.print(i + "\t");
			nextGenerationTankaList.get(i).print(this.materialWord);
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
			sum += this.currentGenerationTankaList.get(i).getScore(this.materialWord);
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

	// 比較用内部クラス
	private class TankaComparator implements Comparator<Tanka> {
		@Override
		public int compare(Tanka p1, Tanka p2) {
			int p1Score = p1.getScore(IslandBase.this.materialWord);
			int p2Score = p2.getScore(IslandBase.this.materialWord);

			if (p1Score == p2Score) {
				return 0;
			} else {
				return p1.getScore(IslandBase.this.materialWord) < p2.getScore(IslandBase.this.materialWord) ? 1 : -1;
			}
		}
	}

}
