package com.example.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 入力されたコメントを受け取るフォーム.
 * 
 * @author ryuheisugita
 */
public class CommentForm {

	/** 投稿ID */
	private String articleId;
	/** コメント名 */
	@NotBlank(message="名前を入力してください")
	@Size(max=50,message="名前は50文字以内で入力してください")
	private String name;
	/** コメント内容 */
	@NotBlank(message="内容を入力してください")
	private String content;
	
	@Override
	public String toString() {
		return "CommentForm [articleId=" + articleId + ", name=" + name + ", content=" + content + "]";
	}
	
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
