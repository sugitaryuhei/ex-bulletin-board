package com.example.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 投稿された記事の内容を受け取るフォーム.
 * 
 * @author ryuheisugita
 */
public class ArticleForm {
	
	/** 記事名 */
	@NotBlank(message="名前を入力してください")
	@Size(max=50,message="名前は50文字以内で入力してください")
	private String name;
	/** 記事内容 */
	@NotBlank(message="内容を入力してください")
	private String content;
	
	@Override
	public String toString() {
		return "AtricleForm [name=" + name + ", content=" + content + "]";
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
