-- Place your queries here. Docs available https://www.hugsql.org/

-- :name get-article-by-id :? :1
-- :doc returns a article object by id, or nil if not present
SELECT *
FROM article
WHERE id = :id

-- :name get-article-by-doi :? :1
-- :doc returns a article object by id, or nil if not present
SELECT *
FROM article
WHERE doi = :doi

-- :name add-article! :n
-- :doc insert articles
insert into article
(id, title, author, "date", doi)
values (null, :title, :author, :date, :doi)
returning *

-- :name update-article! :? :1
-- :doc update article
update article
set title = :title, "date" = :date, author = :author
where id = :id
returning *

-- :name delete-article! :? :1
-- :doc delete article
delete 
from article
where id = :id

-- :name search-articles :n
-- :doc returns articles
SELECT *
FROM article
WHERE 
  (:query IS NULL OR 
   title LIKE '%' || :query || '%' OR 
   author LIKE '%' || :query || '%' or 
   "date" LIKE '%' || :query || '%')
order by title
limit :count
OFFSET :offset