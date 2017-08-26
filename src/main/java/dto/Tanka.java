package dto;

import java.util.ArrayList;

public class Tanka {

	private ArrayList<ArrayList<Word>> tanka;
	private final int PHASE_COUNT = 5;

	public Tanka() {
		this.tanka = new ArrayList<ArrayList<Word>>();
		for (int i = 0; i < this.PHASE_COUNT; i++) {
			ArrayList<Word> phase = new ArrayList<Word>();
			this.tanka.add(phase);
		}
	}

	// 指定された番号のフェーズに単語を1つ追加する
	public void putWord(int phaseNum, Word word) {
		ArrayList<Word> phase = this.tanka.get(phaseNum);
		phase.add(word);
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

	// 文字列化
	public String toString() {
		String tankaStr = "";
		for (int i = 0; i < this.PHASE_COUNT; i++) {
			ArrayList<Word> phase = this.tanka.get(i);
			for (Word word : phase) {
				tankaStr += word.getCharTerm();
			}
			if (i != this.PHASE_COUNT - 1) {
				tankaStr += " ";
			}
		}
		return tankaStr;
	}

	// 画面表示する
	public void print() {
		System.out.println(this.toString());
	}

}
