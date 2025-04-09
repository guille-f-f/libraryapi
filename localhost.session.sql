SELECT book.book_title
FROM book
JOIN author ON book.id_author = author.id_author
WHERE author.author_name = 'Miguel de Cervates';

TABLE author;
TABLE book;
TABLE editorial;
