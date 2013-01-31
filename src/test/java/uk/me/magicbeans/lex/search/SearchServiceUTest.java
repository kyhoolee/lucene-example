package uk.me.magicbeans.lex.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;
import uk.me.magicbeans.lex.model.Product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

public class SearchServiceUTest {

    private SearchService testObj;

    private Set<Product> products;

    @Before
    public void setup() {
        testObj = new SearchService(new RAMDirectory());
        products = new HashSet<Product>();
        products.add(new Product("Clean code", "A handbook of agile software craftsmanship"));
        products.add(new Product("Rest in practice", "A book on how to do rest"));
        products.add(new Product("Effective unit testing", "Shows how to write good unit tests in java"));
        for(Product product: products) {
            testObj.index(product);
        }
    }

    @Test
    public void testSearchExpectOneResult() {
        List<Document> results = testObj.search("clean code");
        assertEquals("Expect one result returned", 1, results.size());
        System.out.println(results.toString());
    }

    @Test
    public void testSearchExpectOneResultAgain() {
        List<Document> results = testObj.search("computer book");
        assertEquals("Expect one result returned", 1, results.size());
        System.out.println(results.toString());
        assertEquals("Expect title is clean code", "Rest in practice", results.get(0).get("title"));
    }

    @Test
    public void testSearchExpectNoResults() {
        List<Document> results = testObj.search("Dog");
        assertEquals("Expect results returned", 0, results.size());
        System.out.println(results.toString());
    }

    @Test
    public void testSearchExpectTwoResults() {
        List<Document> results = testObj.search("unit testing book");
        assertEquals("Expect results returned", 2, results.size());
        System.out.println(results.toString());
    }

}
