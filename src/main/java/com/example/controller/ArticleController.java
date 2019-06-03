package com.example.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Article;
import com.example.domain.Comment;
import com.example.form.ArticleForm;
import com.example.form.CommentForm;
import com.example.repository.ArticleRepository;
import com.example.repository.CommentRepository;

/**
 * アプリケーションを動かすためのコントローラー.
 * 
 * @author ryuheisugita
 */
@Controller
@Transactional
@RequestMapping("/article")
public class ArticleController {
	
	@Autowired
	private ArticleRepository articleRepository;
	
	@Autowired
	private CommentRepository commentRepository;

	/**
	 * 掲示板画面を表示する.
	 * 
	 * @return 掲示板画面
	 */
	@RequestMapping("")
	public String index(Model model) {
		List<Article> aricleList = articleRepository.findAllExcludeComment();
		for(Article article:aricleList) {
			List<Comment> commentList = commentRepository.findByArticleId(article.getId());
			article.setCommentList(commentList);
		}
		model.addAttribute("articleList", aricleList);
		System.out.println(aricleList);
		return "article";
	}
	
	/**
	 * 掲示板画面を表示する.
	 * 
	 * @return 掲示板画面
	 */
	@RequestMapping("/1-time-sql")
	public String index2(Model model) {
		List<Article> articleList = articleRepository.findAll();
		model.addAttribute("articleList", articleList);
		System.out.println(articleList);
		return "article";
	}
	
	/**
	 * 記事を投稿して、掲示板画面を表示する.
	 * 
	 * @return 掲示板画面
	 */
	@RequestMapping("/insert-article")
	public String insertArticle(ArticleForm form) {
		Article article = new Article();
		BeanUtils.copyProperties(form, article);
		articleRepository.insert(article);
		return "redirect:/article";
	}

	/**
	 * コメントを追加して、掲示板画面を表示する.
	 * 
	 * @return 掲示板画面
	 */	
	@RequestMapping("/insert-comment")
	public String insertComment(CommentForm form) {
		System.out.println(form);
		Comment comment = new Comment();
		BeanUtils.copyProperties(form, comment);
		comment.setArticleId(Integer.parseInt(form.getArticleId()));
		System.out.println(comment);
		commentRepository.insert(comment);
		return "redirect:/article";
	}
	
	/**
	 * 記事を削除して、掲示板画面を表示する.
	 * 
	 * @return 掲示板画面
	 */	
	@RequestMapping("/delete-article")
	public String deleteArticle(int id) {
		System.out.println(id);
		commentRepository.delete(id);
		articleRepository.deleteById(id);
		return "redirect:/article";
	}
	
}
