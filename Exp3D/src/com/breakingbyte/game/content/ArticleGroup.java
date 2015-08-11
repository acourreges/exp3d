package com.breakingbyte.game.content;

import java.util.ArrayList;

public class ArticleGroup {

    public String name;
    
    public ArrayList<Article> articles = new ArrayList<Article>();
    
    public void addArticle(Article article) {
        articles.add(article);
    }
    
    public void updateAllPanelsFromArticles() {
        for (int i = 0; i < articles.size(); i++) {
            articles.get(i).updatePanelContent();
        }
    }
    
    public void applyAllToEngineState() {
        for (int i = 0; i < articles.size(); i++) {
            articles.get(i).applyToEngineState();
        }
    }
    
}
