package jp.gr.java_conf.mu.atb.dto;

import java.util.ArrayList;
import java.util.HashMap;

import jp.gr.java_conf.mu.atb.util.CommonUtil;

public class Tanka {

	// 学習データ
	private static AppearenceRate appearenceRate;
	private static ArrayList<Word> blankWord;
	private final static int PHASE_COUNT = 5;

	// フェーズ別の配列
	private ArrayList<ArrayList<Word>> tanka;

	// スコア
	private int score = -1;
	private int sBase = 400;
	private int s1;
	private int s2;
	private int s3;
	private int s4;
	private int s5;

	// 静的初期化
	static {
		// 学習データを保持
		appearenceRate = new AppearenceRate("AppearanceRate1.txt");
		appearenceRate.printRate1();
		blankWord = new ArrayList<Word>();
		for (int i = 0; i < PHASE_COUNT + 1; i++) {
			blankWord.add(new Word("*空白" + i));
		}
	}

	// コンストラクタ
	public Tanka() {
		this.tanka = new ArrayList<ArrayList<Word>>();
		for (int i = 0; i < PHASE_COUNT; i++) {
			ArrayList<Word> phase = new ArrayList<Word>();
			this.tanka.add(phase);
		}
	}

	// 自らのディープコピーを返す
	public Tanka clone() {
		Tanka cloneTanka = new Tanka();
		for (int i = 0; i < PHASE_COUNT; i++) {
			ArrayList<Word> phase = this.tanka.get(i);
			for (Word word : phase) {
				cloneTanka.putWord(i, word);
			}
		}
		return cloneTanka;
	}

	// 指定された番号のフェーズに単語を1つ追加する
	public void putWord(int phaseNum, Word word) {
		ArrayList<Word> phase = this.tanka.get(phaseNum);
		phase.add(word);
	}

	// 指定された番号のフェーズのランダムな箇所の単語を更新する
	public void updateWord(int phaseNum, Word word) {
		ArrayList<Word> phase = this.tanka.get(phaseNum);
		int size = phase.size();
		int p = CommonUtil.random(size);
		phase.remove(p);
		phase.add(p, word);
	}

	// 指定された番号のフェーズを差し替える
	public void updatePhase(int phaseNum, ArrayList<Word> phase) {
		this.tanka.set(phaseNum, phase);
	}

	// 指定された番号のフェーズのランダムな箇所に、単語を挿入する
	public void insertWord(int phaseNum, Word word) {
		ArrayList<Word> phase = this.tanka.get(phaseNum);
		int size = phase.size();
		phase.add(CommonUtil.random(size), word);
	}

	// 指定された番号のフェーズから、単語を1つランダムで削除する
	public void deleteWord(int phaseNum) {
		ArrayList<Word> phase = this.tanka.get(phaseNum);
		int size = phase.size();
		// サイズが2以上のときのみ削除
		if (size >= 2) {
			int p = CommonUtil.random(size);
			phase.remove(p);
		}
	}

	// 指定された番号のフェーズの長さを返す
	public int getPhaseLength(int phaseNum) {
		ArrayList<Word> phase = this.tanka.get(phaseNum);
		int length = 0;
		for (Word word : phase) {
			length += word.getReadingLength();
		}
		return length;
	}

	// 指定された番号のフェーズを返す
	public ArrayList<Word> getPhase(int n) {
		return this.tanka.get(n);
	}

	// 文字列化
	public String toString() {
		String tankaStr = "";
		for (int i = 0; i < PHASE_COUNT; i++) {
			ArrayList<Word> phase = this.tanka.get(i);
			for (Word word : phase) {
				if (word == null) {
					tankaStr += "[null]";
				} else {
					tankaStr += word.getCharTerm();
				}
			}
			if (i != PHASE_COUNT - 1) {
				tankaStr += " ";
			}
		}
		return tankaStr;
	}

	// 画面表示する
	public void print(MaterialWord materialWord) {
		System.out.println(getScoreStr(materialWord) + "\t" + this.toString());
	}

	// 分解して画面表示する
	public void printWord(MaterialWord materialWord) {
		this.print(materialWord);
		for (int i = 0; i < PHASE_COUNT; i++) {
			ArrayList<Word> phase = this.tanka.get(i);
			for (Word word : phase) {
				word.print();
			}
		}
	}

	// スコア
	public int getScore(MaterialWord materialWord) {
		this.calcScore(materialWord);
		return this.score;
	}

	// スコア文字列
	public String getScoreStr(MaterialWord materialWord) {
		this.calcScore(materialWord);
		return "" + this.score + "\t(" + this.sBase + "\t" + this.s1 + "\t" + this.s2 + "\t" + this.s3 + "\t" + this.s4
				+ "\t" + this.s5 + ")";
	}

	// 全ての単語を(空白込みで)並べたリストを返す
	private ArrayList<Word> getLinkedWordList() {
		ArrayList<Word> list = new ArrayList<Word>();
		for (int i = 0; i < PHASE_COUNT; i++) {
			list.add(blankWord.get(i));
			list.addAll(this.tanka.get(i));
		}
		list.add(blankWord.get(PHASE_COUNT));
		return list;
	}

	// スコアを計算する
	private void calcScore(MaterialWord materialWord) {

		// 部分スコア
		this.s1 = 0; // 文字数が57577から外れていたら減点
		this.s2 = 0; // 学習データにない連結があったら減点
		this.s3 = 0; // 学習データにある連結の度合に応じて加点
		this.s4 = 0; // 素材データにある連結の度合いに応じて加点
		this.s5 = 0; // 同じ単語が連続していたら減点

		// 重み
		int b1 = 1;
		int b2 = 1;
		int b3 = 1;
		int b4 = 1;
		int b5 = 1;

		// 57577からずれていると減点
		int[] phaseLength = { 5, 7, 5, 7, 7 };
		for (int i = 0; i < 5; i++) {
			this.s1 -= ((Math.abs(this.getPhaseLength(i) - phaseLength[i])) * 5);
		}

		// 同じ単語が出てきた回数をチェック
		HashMap<String, Integer> duplicateWord = new HashMap<String, Integer>();

		ArrayList<Word> linkedWordList = this.getLinkedWordList();
		int size = linkedWordList.size();
		for (int i = 0; i < size; i++) {

			Word current = linkedWordList.get(i);

			// 単語間の遷移によるチェック
			if (i < size - 1) {
				Word next = linkedWordList.get(i + 1);

				// 学習データによるスコアリング
				int c = appearenceRate.getCount1(current.getKey(), next.getKey());
				// 連結がない場合は大きく減点
				if (c == 0) {
					this.s2 -= 10;
				}

				// 連結の度合いに応じてボーナス加点
				double r = appearenceRate.getRatio1(current.getKey(), next.getKey());
				this.s3 += (r * 10);

				// 素材データによるスコアリング
				int d = materialWord.getTransitionCount(current, next);
				if (d > 0) {
					this.s4 += 5;
				}
				// 助詞以外で素材と同じ連結があればボーナス加点
				// score += (d * 3);
				// if (!current.getPartOfSpeech().startsWith("助詞-") &&
				// !next.getPartOfSpeech().startsWith("助詞-")) {
				// score += (d);
				// }

			}

			// 同じ単語が何度も出てくる場合は減点
			if (current.getPartOfSpeech().startsWith("名詞-") || current.getPartOfSpeech().startsWith("助詞-")) {
				String duplicateKey = current.getCharTerm() + ":" + current.getKey();
				Integer count = duplicateWord.get(duplicateKey);
				if (count == null) {
					// 初回は減点しない
					duplicateWord.put(duplicateKey, 1);
				} else {
					// 同じ単語が2回目以降出て着たら都度減点
					count++;
					duplicateWord.put(duplicateKey, count);
					if (current.getPartOfSpeech().startsWith("名詞-") && count > 1) {
						this.s5 -= 10;
					}
					if (current.getPartOfSpeech().startsWith("助詞-") && count > 2) {
						this.s5 -= 10;
					}
					if (current.getPartOfSpeech().startsWith("接頭詞-名詞接続") && count > 1) {
						this.s5 -= 5;
					}
				}
			}

		}

		// スコアを計算
		this.score = this.sBase + (b1 * this.s1) + (b2 * this.s2) + (b3 * this.s3) + (b4 * this.s4) + (b5 * this.s5);

		// マイナスにならないようにする
		if (this.score < 0) {
			this.score = 0;
		}
	}

}
