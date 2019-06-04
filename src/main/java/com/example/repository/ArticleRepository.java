package com.example.repository;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.Article;
import com.example.domain.Comment;

/**
 * ariclesテーブルを操作するリポジトリ.
 * 
 * @author ryuheisugita
 */
@Repository
public class ArticleRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;

	private String tableName = "articles";

	private static final RowMapper<Article> ARTICLE_ROW_MAPPER = (rs, i) -> {
		Article article = new Article();
		article.setId(rs.getInt("id"));
		article.setName(rs.getString("name"));
		article.setContent(rs.getString("content"));
		return article;
	};

//	private static final RowMapper<Article> ARTICLE_ROW_MAPPER2 = (rs, i) -> {
//		Article article = new Article();
//
//		article.setId(rs.getInt("id"));
//		article.setName(rs.getString("name"));
//		article.setContent(rs.getString("content"));
//		Comment comment = new Comment();
//		comment.setId(rs.getInt("c_id"));
//		comment.setName(rs.getString("c_name"));
//		comment.setContent(rs.getString("c_content"));
//		comment.setArticleId(rs.getInt("id"));
//		List<Comment> commentList = new ArrayList<>();
//		commentList.add(comment);
//		if (comment.getName() == null) {
//			commentList = null;
//		}
//		article.setCommentList(commentList);
//		return article;
//	};

	private static final ResultSetExtractor<List<Article>> ARTICLE_RESULT_SET_EXTRACTOR = (rs) -> {
		List<Article> articleList = new LinkedList<>();
		int beforeArticleId = 0;

		List<Comment> commentList = null;

		while (rs.next()) {

			if (beforeArticleId != rs.getInt("id")) {
				Article article = new Article();
				article.setName(rs.getString("name"));
				article.setContent(rs.getString("content"));
				article.setId(rs.getInt("id"));
				articleList.add(article);
				commentList = new LinkedList<>();
				article.setCommentList(commentList);
			}

			if (rs.getInt("c_id") != 0) {
				Comment comment = new Comment();
				comment.setId(rs.getInt("c_id"));
				comment.setName(rs.getString("c_name"));
				comment.setContent(rs.getString("c_content"));
				comment.setArticleId(rs.getInt("id"));
				commentList.add(comment);
			}
			beforeArticleId = rs.getInt("id");
		}
		return articleList;
	};

	/**
	 * 投稿されているすべての記事を表示する(コメントを含まない).
	 * 
	 * @return 投稿されているすべての記事
	 */
	public List<Article> findAllExcludeComment() {
		String sql = "select id,name,content from " + tableName + " order by id desc";
		List<Article> articleList = template.query(sql, ARTICLE_ROW_MAPPER);
		return articleList;
	}

	/**
	 * 投稿されているすべての記事を表示する(一つのSQL文で).
	 * 
	 * @return 投稿されているすべての記事
	 */
//	public List<Article> findAll() {
//		// ArticleのcommentListに一つずつcomment を入れて、List<Article>を取得
//		String sql = "select a.id as id,a.name as name,a.content as content,c.id as c_id,c.name as c_name,c.content as c_content from "
//				+ tableName + " a full join comments c on a.id=c.article_id order by a.id desc,c.id desc";
//		List<Article> articleListNotCommentList = template.query(sql, ARTICLE_ROW_MAPPER2);
//		// 記事IDをLinkedHashMapのキーとして、同じキーのcommentListをまとめる
//		Map<Integer, Article> articleMap = new LinkedHashMap<>();
//		for (Article article : articleListNotCommentList) {
//			if (articleMap.get(article.getId()) == null) {
//				articleMap.put(article.getId(), article);
//			} else {
//				List<Comment> commentList = articleMap.get(article.getId()).getCommentList();
//				commentList.add(article.getCommentList().get(0));
//				article.setCommentList(commentList);
//				articleMap.put(article.getId(), article);
//			}
//		}
//		// LinkedHashMapからarticleListを生成
//		Set<Integer> set = articleMap.keySet();
//		List<Article> articleList = new LinkedList<Article>();
//		for (Integer id : set) {
//			articleList.add(articleMap.get(id));
//		}
//		return articleList;
//	}

	/**
	 * 投稿されているすべての記事を表示する(一つのSQL文で).
	 * 
	 * @return 投稿されているすべての記事
	 */
	public List<Article> findAll() {
		// ArticleのcommentListに一つずつcomment を入れて、List<Article>を取得
		String sql = "select a.id as id,a.name as name,a.content as content,c.id as c_id,c.name as c_name,c.content as c_content from "
				+ tableName + " a full join comments c on a.id=c.article_id order by a.id desc,c.id desc";

		return template.query(sql, ARTICLE_RESULT_SET_EXTRACTOR);
	}

	/**
	 * 投稿された記事をarticleテーブルに書き込む.
	 * 
	 * @param article 投稿記事
	 */
	public void insert(Article article) {
		String sql = "insert into " + tableName + " (name,content) values(:name,:content)";
		SqlParameterSource param = new BeanPropertySqlParameterSource(article);
		template.update(sql, param);
	}

	/**
	 * 渡された記事IDの投稿をarticlesテーブルから削除する.
	 * 
	 * @param id 記事ID
	 */
	public void deleteById(int id) {
		String sql = "delete from " + tableName + " where id=:id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		template.update(sql, param);
	}
	
	/**
	 * 一つのSQL文に渡された記事IDの投稿をarticlesテーブルから削除する.
	 * 
	 * @param id 記事ID
	 */
	public void deleteById1TimeSQL(int id) {
		String sql = "WITH deleted AS " + 
				"(DELETE FROM "+tableName+" WHERE id =:id" + 
				" RETURNING id)" + 
				" DELETE FROM comments " + 
				" WHERE article_id IN (SELECT id FROM deleted)";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		template.update(sql, param);
	}

}
