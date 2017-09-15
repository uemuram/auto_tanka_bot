package jp.gr.java_conf.mu.atb.island;

import java.util.ArrayList;

import jp.gr.java_conf.mu.atb.dto.MaterialWord;
import jp.gr.java_conf.mu.atb.dto.Tanka;
import jp.gr.java_conf.mu.atb.dto.Word;
import jp.gr.java_conf.mu.atb.util.CommonUtil;

public class IslandPrioritizeOriginal extends IslandBase {

	// 突然変異の確率
	private double mutationProbability;

	// コンストラクタ
	public IslandPrioritizeOriginal() {
		super();
	}

	public IslandPrioritizeOriginal(int tankaNum, MaterialWord materialWord, double mutationProbability) {
		this();
		this.mutationProbability = mutationProbability;
		if ((tankaNum % 2) != 0) {
			throw new RuntimeException("遺伝子数は偶数の必要あり");
		}
		this.tankaNum = tankaNum;
		this.materialWord = materialWord;
	}

	@Override
	// 初期世代を生成
	public void birth(MaterialWord materialWord) {
		super.birthOrder(materialWord);
	}

	// 次の世代を生成
	public void createNextGeneration(MaterialWord materialWord) {
		// スコアmaxは次世代に残すため、交叉の数は2引く
		int n = (this.tankaNum - 2) / 2;
		for (int i = 0; i < n; i++) {
			// ルーレット選択で遺伝子を2つ選択
			int parentIdx1 = this.selectRoulette();
			int parentIdx2 = this.selectRoulette();
			// ランダムでフェーズを入れ替える
			swapPhase(parentIdx1, parentIdx2, CommonUtil.random(5), CommonUtil.random(10));
		}

		// 突然変異
		for (Tanka tanka : this.nextGenerationTankaList) {
			for (int i = 0; i < 5; i++) {
				if (Math.random() < this.mutationProbability) {
					int type = CommonUtil.random(5);
					if (type == 0) {
						// 追加
						tanka.insertWord(i, materialWord.getRandomWord());
					} else if (type == 1) {
						// 削除
						tanka.deleteWord(i);
					} else {
						// 更新
						tanka.updateWord(i, materialWord.getRandomWord());
					}
				}
			}
		}

		// スコアが高い2つの短歌を次世代に残す
		this.sort();
		Tanka score1stTanka = this.currentGenerationTankaList.get(0).clone();
		Tanka score2ndTanka = this.currentGenerationTankaList.get(1).clone();
		this.nextGenerationTankaList.add(score1stTanka);
		this.nextGenerationTankaList.add(score2ndTanka);
	}

	// 2つの短歌のうち、n番目のフェーズのm地点を入れ替えて次世代に入れる
	// 0<=n<=4, 0<=m<=9
	private void swapPhase(int parentIdx1, int parentIdx2, int n, int m) {
		// 親(ディープコピー)
		Tanka tanka1 = this.currentGenerationTankaList.get(parentIdx1).clone();
		Tanka tanka2 = this.currentGenerationTankaList.get(parentIdx2).clone();

		// n番目のフェーズを交換

		// n+1～最後までを交換
		for (int i = n + 1; i < 5; i++) {
			ArrayList<Word> phase1 = tanka1.getPhase(i);
			ArrayList<Word> phase2 = tanka2.getPhase(i);
			tanka1.updatePhase(i, phase2);
			tanka2.updatePhase(i, phase1);
		}

		// 次世代に追加
		this.nextGenerationTankaList.add(tanka1);
		this.nextGenerationTankaList.add(tanka2);
	}

}
