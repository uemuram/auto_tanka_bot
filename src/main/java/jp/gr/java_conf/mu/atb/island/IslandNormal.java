package jp.gr.java_conf.mu.atb.island;

import java.util.ArrayList;

import jp.gr.java_conf.mu.atb.dto.MaterialWord;
import jp.gr.java_conf.mu.atb.dto.Tanka;
import jp.gr.java_conf.mu.atb.dto.Word;
import jp.gr.java_conf.mu.atb.util.CommonUtil;

public class IslandNormal extends IslandBase {

	// 突然変異の確率
	private double mutationProbability;

	// コンストラクタ
	public IslandNormal() {
		super();
	}

	public IslandNormal(int tankaNum, MaterialWord materialWord, double mutationProbability) {
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
		super.birthRandom(materialWord);
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
			swapPhase(parentIdx1, parentIdx2, CommonUtil.random(5));
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

	// 2つの短歌のうち、n番目のフェーズを入れ替えて次世代に入れる
	private void swapPhase(int parentIdx1, int parentIdx2, int n) {
		// 親(ディープコピー)
		Tanka tanka1 = this.currentGenerationTankaList.get(parentIdx1).clone();
		Tanka tanka2 = this.currentGenerationTankaList.get(parentIdx2).clone();
		ArrayList<Word> phase1 = tanka1.getPhase(n);
		ArrayList<Word> phase2 = tanka2.getPhase(n);

		// 交換用に、短い方にnullを追加して長さをそろえる
		int size1 = phase1.size();
		int size2 = phase2.size();
		if (size1 > size2) {
			for (int i = 0; i < size1 - size2; i++) {
				phase2.add(null);
			}
		} else if (size1 < size2) {
			for (int i = 0; i < size2 - size1; i++) {
				phase1.add(null);
			}
		}
		int size = phase1.size();

		// 入れ替え地点をランダムに決定
		int p1 = CommonUtil.random(size);
		int p2 = CommonUtil.random(size);
		int from = CommonUtil.min(p1, p2);
		int to = CommonUtil.max(p1, p2);

		// 入れ替え実行
		for (int i = from; i <= to; i++) {
			Word word1 = phase1.get(i);
			Word word2 = phase2.get(i);
			phase1.remove(i);
			phase2.remove(i);
			phase1.add(i, word2);
			phase2.add(i, word1);
		}

		// nullを消す
		for (int i = size - 1; i >= 0; i--) {
			if (phase1.get(i) == null) {
				phase1.remove(i);
			}
			if (phase2.get(i) == null) {
				phase2.remove(i);
			}
		}

		// 次世代に追加
		this.nextGenerationTankaList.add(tanka1);
		this.nextGenerationTankaList.add(tanka2);
	}

}
