(ns kit.articles.service.article-source.protocol)

(defprotocol ArticleSource
  (search [this params] "Выполняет поиск статей в указанном источнике"))