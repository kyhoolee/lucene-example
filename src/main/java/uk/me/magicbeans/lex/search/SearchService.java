package uk.me.magicbeans.lex.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import uk.me.magicbeans.lex.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * For next step... http://www.javacodegeeks.com/2010/05/did-you-mean-feature-lucene-spell.html
 */
public class SearchService {

    private final Directory directory;
    private final Analyzer analyzer;

    public SearchService(Directory directory) {
        this.directory = directory;
        analyzer = new StandardAnalyzer(Version.LUCENE_41);
    }

    public void index(Product product) {
        try {
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_41, analyzer);
            IndexWriter iwriter = new IndexWriter(directory, config);
            Document doc = new Document();
            doc.add(new Field("title", product.getTitle(), TextField.TYPE_STORED));
            doc.add(new Field("description", product.getDescription(), TextField.TYPE_STORED));
            iwriter.addDocument(doc);
            iwriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Document> search(String searchTerm) {
        List<Document> matches = new ArrayList<Document>();
        try {
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
            MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_41, new String[]{"title", "description"}, analyzer);
            Query query = parser.parse(searchTerm);
            ScoreDoc[] hits = isearcher.search(query, null, 20).scoreDocs;
            for (ScoreDoc hit : hits) {
                matches.add(isearcher.doc(hit.doc));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return matches;
    }
}
