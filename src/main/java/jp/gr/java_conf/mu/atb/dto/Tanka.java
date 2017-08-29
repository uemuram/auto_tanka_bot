package jp.gr.java_conf.mu.atb.dto;

import java.util.ArrayList;

import jp.gr.java_conf.mu.atb.util.CommonUtil;

public class Tanka {

	// 学習データ
	private static AppearenceRate appearenceRate;
	private static ArrayList<Word> blankWord;
	private final static int PHASE_COUNT = 5;

	// フェーズ別の配列
	private ArrayList<ArrayList<Word>> tanka;

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
	public void print() {
		int score = this.calcScore();
		System.out.println(score + "\t" + this.toString());
	}

	// 分解して画面表示する
	public void printWord() {
		this.print();
		for (int i = 0; i < PHASE_COUNT; i++) {
			ArrayList<Word> phase = this.tanka.get(i);
			for (Word word : phase) {
				word.print();
			}
		}
	}

	// スコア
	public int getScore() {
		return this.calcScore();
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
	private int calcScore() {
		// 初期スコア
		int score = 400;
		int[] phaseLength = { 5, 7, 5, 7, 7 };

		// 57577からずれていると減点
		for (int i = 0; i < 5; i++) {
			score -= ((Math.abs(this.getPhaseLength(i) - phaseLength[i])) * 10);
		}

		// 学習データ通りの連結ではない場合は減点
		ArrayList<Word> linkedWordList = this.getLinkedWordList();
		int size = linkedWordList.size();
		for (int i = 0; i < size - 1; i++) {
			Word current = linkedWordList.get(i);
			Word next = linkedWordList.get(i + 1);
			int c = appearenceRate.getCount1(current.getKey(), next.getKey());
			// System.out.println(current.getCharTerm() + "\t" +
			// next.getCharTerm() + "\t" + c);
			if (c == 0) {
				score -= 10;
			}
		}

		// マイナスにならないようにする
		if (score < 0) {
			score = 0;
		}
		return score;
	}

}
