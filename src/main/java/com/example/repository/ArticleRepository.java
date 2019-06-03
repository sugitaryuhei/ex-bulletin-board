package com.example.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	private static final RowMapper<Article> ARTICLE_ROW_MAPPER = (rs,i) -> {
		Article article = new Article();
		article.setId(rs.getInt("id"));
		article.setName(rs.getString("name"));
		article.setContent(rs.getString("content"));
		return article;
	};

	
	private static final RowMapper<Article> ARTICLE_ROW_MAPPER2 = (rs,i) -> {
		Article article = new Article();
		
		article.setId(rs.getInt("id"));
		article.setName(rs.getString("name"));
		article.setContent(rs.getString("content"));
		Comment comment = new Comment();
		comment.setId(rs.getInt("c_id"));
		comment.setName(rs.getString("c_name"));
		comment.setContent(rs.getString("c_content"));
		comment.setArticleId(rs.getInt("id"));
		List<Comment> commentList = new ArrayList<>();
		commentList.add(comment);
		article.setCommentList(commentList);
		return article;
	};
	
	
	/**
	 * 投稿されているすべての記事を表示する(コメントを含まない).
	 * 
	 * @return 投稿されているすべての記事
	 */
	public List<Article> findAllExcludeComment(){
		String sql = "select id,name,content from " + tableName
				            + " order by id desc";
		List<Article> articleList = template.query(sql, ARTICLE_ROW_MAPPER);
		return articleList;
	}
	
	/**
	 * 投稿されているすべての記事を表示する(コメントを含む).
	 * 
	 * @return 投稿されているすべての記事
	 */
	public List<Article> findAll(){
		String sql = "select a.id as id,a.name as name,a.content as content,c.id as c_id,c.name as c_name,c.content as c_content from " + tableName + 
				           " a full join comments c on a.id=c.article_id order by a.id desc,c.id desc";
		List<Article> articleListNotCommentList = template.query(sql, ARTICLE_ROW_MAPPER2);
		Map<String, Article> articleMap = new LinkedHashMap<>();
		for(Article article:articleListNotCommentList) {
			if(articleMap.get(article.getName()) == null ){
				articleMap.put(article.getName(), article);
			}else {
				List<Comment> commentList = articleMap.get(article.getName()).getCommentList();
				commentList.add(article.getCommentList().get(0));
				article.setCommentList(commentList);
				articleMap.put(article.getName(), article);
			}
		}
		Set<String> set = articleMap.keySet();
		List<Article> articleList = new LinkedList<Article>();
		for(String name:set) {
			articleList.add(articleMap.get(name));
		}
		return articleList;
	}
	
	/**
	 * 投稿された記事をarticleテーブルに書き込む.
	 * 
	 * @param article 投稿記事
	 */
	public void insert(Article article) {
		String sql ="insert into " + tableName + " (name,content) values(:name,:content)";
        SqlParameterSource param = new BeanPropertySqlParameterSource(article);
        template.update(sql, param);
	}
	
	/**
	 * 渡された記事IDの投稿をarticlesテーブルから削除する.
	 * 
	 * @param id　記事ID
	 */
	public void deleteById(int id) {
		String sql ="delete from " + tableName + " where id=:id";
        SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
        template.update(sql, param);
	}

}
