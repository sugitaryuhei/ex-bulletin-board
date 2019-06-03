package com.example.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.Comment;

/**
 * commentsテーブルを操作するリポジトリ.
 * 
 * @author ryuheisugita
 */
@Repository
public class CommentRepository {
	
	@Autowired
	private NamedParameterJdbcTemplate template;
	
	private String tableName = "comments";
	
	private static final RowMapper<Comment> COMMENT_ROW_MAPPER =(rs,i) ->{
		Comment comment = new Comment();
		comment.setId(rs.getInt("id"));
		comment.setName(rs.getString("name"));
		comment.setContent(rs.getString("content"));
		comment.setArticleId(rs.getInt("article_id"));
		return comment;
	};
	
	/**
	 * 指定された投稿IDのコメントリストを取得.
	 * 
	 * @param articled 投稿ID
	 * @return コメントリスト
	 */
	public List<Comment> findByArticleId(int articleId){
		String sql ="select id,name,content,article_id from " + tableName + " where article_id=:articleId"
				          + " order by id desc" ; 
		SqlParameterSource param = new MapSqlParameterSource().addValue("articleId", articleId);
		List<Comment> commentList = template.query(sql, param, COMMENT_ROW_MAPPER);
		return commentList;
	}
	
	/**
	 * コメントをcomments	テーブルに追加する.
	 * 
	 * @param comment コメント
	 */
	public void insert(Comment comment) {
		String sql = "insert into " + tableName + " (name,content,article_id) values(:name,:content,:articleId)";
		SqlParameterSource param = new BeanPropertySqlParameterSource(comment);
		template.update(sql, param);
	}
	
	/**
	 * 指定された投稿IDのコメントを削除する.
	 * 
	 * @param articleId 投稿ID
	 */
	public void delete(int articleId) {
		String sql = "delete from " + tableName + " where article_id=:articleId";
		SqlParameterSource param = new MapSqlParameterSource().addValue("articleId", articleId);
		template.update(sql, param);
	}

}
