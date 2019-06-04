package com.example.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	
	@ModelAttribute
	public ArticleForm SetUpArticleForm() {
		return new ArticleForm();
	}
	
	@ModelAttribute
	public CommentForm SetUpCommentForm() {
		return new CommentForm();
	}
	
	@Autowired
	private ArticleRepository articleRepository;
	
	@Autowired
	private CommentRepository commentRepository;

	/**
	 * 2つのSQL文で掲示板画面を表示する.
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
		return "article";
	}
	
	/**
	 * １つのSQL文で掲示板画面を表示する.
	 * 
	 * @return 掲示板画面
	 */
	@RequestMapping("/1-time-sql")
	public String index2(Model model) {
		List<Article> articleList = articleRepository.findAll();
		model.addAttribute("articleList", articleList);
		return "article";
	}
	
	/**
	 * 記事を投稿して、掲示板画面を表示する.
	 * 
	 * @return 掲示板画面
	 */
	@RequestMapping("/insert-article")
	public String insertArticle(@Validated ArticleForm form, BindingResult result,Model model) {
		if(result.hasErrors()) {
			return index(model);
		}
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
	public String insertComment(@Validated CommentForm form,
			                                        BindingResult result,
			                                        Model model) {
		if(result.hasErrors()) {
			model.addAttribute("selectedId", Integer.parseInt(form.getArticleId()));
			return index2(model);
		}
		Comment comment = new Comment();
		BeanUtils.copyProperties(form, comment);
		comment.setArticleId(Integer.parseInt(form.getArticleId()));
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
		articleRepository.deleteById1TimeSQL(id);
//		commentRepository.delete(id);
//		articleRepository.deleteById(id);
		return "redirect:/article";
	}
	
}
