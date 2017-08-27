package jp.gr.java_conf.mu.atb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterUtil {

	private Twitter twitter;

	// �R���X�g���N�^
	public TwitterUtil() {
		// Twitter���p����
		// ���ϐ�����e��L�[��ǂݍ���
		String consumerKey = System.getenv("twitter4j_oauth_consumerKey");
		String consumerSecret = System.getenv("twitter4j_oauth_consumerSecret");
		String accessToken = System.getenv("twitter4j_oauth_accessToken");
		String accessTokenSecret = System.getenv("twitter4j_oauth_accessTokenSecret");

		System.out.println("consumerKey:\t" + consumerKey);
		System.out.println("consumerSecret:\t" + consumerSecret);
		System.out.println("accessToken:\t" + accessToken);
		System.out.println("accessTokenSecret:\t" + accessTokenSecret);

		// Twitter�ڑ��p�I�u�W�F�N�g
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);
		Configuration configuration = cb.build();
		TwitterFactory tf = new TwitterFactory(configuration);
		this.twitter = tf.getInstance();
	}

	// �w�肳�ꂽ�L�[���[�h�ŁA�w�肳�ꂽ������Twitter���������A���̃e�L�X�g�v�f��Ԃ�
	public ArrayList<String> searchTweetText(String word, int count) {
		List<Status> searchResultList = searchTweet(word, count);
		ArrayList<String> tweetTextList = new ArrayList<String>();
		for (Status status : searchResultList) {
			tweetTextList.add(status.getText());
		}
		return tweetTextList;
	}

	// �w�肳�ꂽ�L�[���[�h�ŁA�w�肳�ꂽ������Twitter����������
	private List<Status> searchTweet(String word, int count) {
		// ������
		List<Status> searchResultList;
		List<Status> tmpSearchResultList;
		long maxId = 0L;
		// �I���t���O
		boolean endFlag = false;
		// ���������B�w�肳�ꂽ�����Ō����A���������c�C�[�g������
		Query query = new Query();
		query.setQuery(word + " exclude:retweets");
		QueryResult result;

		// �܂�1�y�[�W�ڂ��擾
		try {
			result = twitter.search(query);
			searchResultList = result.getTweets();
			System.out.println("�������ʎ擾 " + searchResultList.size() + " ��");
		} catch (TwitterException e1) {
			System.out.println("�������ʎ擾���s : " + e1.getErrorMessage());
			throw new RuntimeException(e1);
		}
		// 1�����擾�ł��Ȃ������ꍇ�͏I��
		if (searchResultList.size() == 0) {
			endFlag = true;
		} else {
			// �Ō��ID���擾���Ă���
			maxId = searchResultList.get(searchResultList.size() - 1).getId() - 1;
			// ���ʃ`�F�b�N
			checkSearchResult(searchResultList);
			// �ړI�̌����ȏ�Ɏ擾�ł����ꍇ���I��
			if (searchResultList.size() >= count) {
				endFlag = true;
			}
		}
		// 1�y�[�W�ڂ��擾�ł����ꍇ��2�y�[�W�ڈȍ~���擾
		while (!endFlag) {
			query.setMaxId(maxId);
			// �A�����ă��N�G�X�g�𓊂��Ȃ��悤�ɂ��邽�߂ɏ����҂�
			CommonUtil.sleep(1000);
			try {
				result = twitter.search(query);
				tmpSearchResultList = result.getTweets();
				System.out.println("�������ʎ擾 " + tmpSearchResultList.size() + " �� (maxId= " + maxId + " )");
			} catch (TwitterException e1) {
				System.out.println("�������ʎ擾���s : " + e1.getErrorMessage());
				throw new RuntimeException(e1);
			}
			// 1�����擾�ł��Ȃ������ꍇ�͏I��
			if (tmpSearchResultList.size() == 0) {
				endFlag = true;
			} else {
				// �Ō��ID���擾���Ă���
				maxId = tmpSearchResultList.get(tmpSearchResultList.size() - 1).getId() - 1;
				// ���ʃ`�F�b�N
				checkSearchResult(tmpSearchResultList);
				// �擾���ʂ�ǉ�
				searchResultList.addAll(tmpSearchResultList);
				// �ړI�̌����ȏ�Ɏ擾�ł����ꍇ���I��
				if (searchResultList.size() >= count) {
					endFlag = true;
				}
			}
		}
		// �]�v�Ɏ擾���ꂽ����؂�̂Ă�
		if (searchResultList.size() > count) {
			int lastIndex = searchResultList.size() - 1;
			for (int i = lastIndex; i >= count; i--) {
				searchResultList.remove(i);
			}
		}
		return searchResultList;
	}

	// �������ʂ��`�F�b�N�A���p�ł��Ȃ����̂�����Ώ��O����
	private void checkSearchResult(List<Status> list) {
		HashMap<String, Boolean> tmpHash = new HashMap<String, Boolean>();
		int lastIndex = list.size() - 1;
		for (int i = lastIndex; i >= 0; i--) {
			String logStr = "";
			Status status = list.get(i);
			String text = status.getText();
			if (text.startsWith("RT @")) {
				// ��������c�C�[�g�����O
				list.remove(i);
				logStr += "�y�폜�z";
			} else if (text.contains("[���") || text.contains("(���") || text.contains("�y���") || text.contains("[����")
					|| text.contains("(����") || text.contains("�y����")) {
				// ����c�C�[�g�A�����c�C�[�g�Ǝv������̂����O
				list.remove(i);
				logStr += "�y�폜�z";
			} else if (text.contains("http://") || text.contains("https://")) {
				// URL���܂܂�Ă���c�C�[�g�����O
				list.remove(i);
				logStr += "�y�폜�z";
			} else if (text.contains("FF�O����") || text.contains("�g�U��]") || text.contains("���݊�]")) {
				// �]�v�Ȍ��܂蕶��(FF�O���玸�炵�܂� ��)�������Ă���c�C�[�g�����O
				list.remove(i);
				logStr += "�y�폜�z";
			} else {
				// �������e�̃c�C�[�g��2�񗘗p���Ȃ��悤�ɁA�n�b�V�����g���ă`�F�b�N
				Boolean check = tmpHash.get(text);
				if (check != null) {
					list.remove(i);
					logStr += "�y�폜�z";
				} else {
					tmpHash.put(text, true);
				}
			}
			logStr += (i + ":" + status.getCreatedAt() + ":" + status.getId() + ":" + text + "\n");
			System.out.print(logStr);
		}
	}

}
