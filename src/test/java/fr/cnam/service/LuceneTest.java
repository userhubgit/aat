package fr.cnam.service;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.springframework.util.StringUtils;
import org.tartarus.snowball.ext.FrenchStemmer;

import fr.cnam.util.AATLuceneAnalyzerUtil;
import fr.cnam.util.Constante;

public class LuceneTest {

	/**
	 *
	 * @return {@link Analyzer}
	 * @throws IOException
	 */
	public static Analyzer getGeneriqueAnalyzer() {

		Analyzer generiqueAnalyzer = new Analyzer() {			

			@Override
			public TokenStream tokenStream(String fieldName, Reader reader) {
				
				Tokenizer generiqueTokenizer = new WhitespaceTokenizer(Version.LUCENE_36, reader);
				TokenStream filter = new StandardFilter(Version.LUCENE_36, generiqueTokenizer);
				filter = new LowerCaseFilter(Version.LUCENE_36, filter);
				filter = new ASCIIFoldingFilter(filter);				
				filter = new SnowballFilter(filter, new FrenchStemmer());
				return filter;
			}
		};
		return generiqueAnalyzer;
	}
	
	public static void main(String[] args) throws Exception {

		//  1 create the index
		Directory d = new RAMDirectory();

		Analyzer analyzer =  AATLuceneAnalyzerUtil.getSynonymeAnalyzer();

				// AATLuceneAnalyzerUtil.getAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
		
	

		IndexWriter w = new IndexWriter(d, config);
		addDoc(w, "amygdalite thyroïde", "193398817");
        addDoc(w, "syndrôme épaule main", "55320055Z");
        addDoc(w, "chirurgie", "55063554A");
        addDoc(w, "allergie", "9900333X");
        addDoc(w, "insuffisance", "9900333X");
        displayIndex(w);
        
        w.close();
        
        
        // 2 query
		String querystr = args.length > 0 ? args[0] : "insuf";		
		Query q = new QueryParser(Version.LUCENE_36, "libelle", analyzer).parse("(" + querystr+"*" + ") OR (" + querystr.trim() + ")" );
//		q = getLibelleWithApproximatifQuery(querystr);
		
		// 3. search
        int hitsPerPage = 10;
        IndexReader reader = IndexReader.open(d);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document doc = searcher.doc(docId);
            System.out.println(searcher.explain(q, docId));
            System.out.println((i + 1) + ". " + doc.get("synonyme") + "\t" + doc.get("libelle"));
        }

        // reader can only be closed when there
        // is no need to access the documents any more.
        searcher.close();
        reader.close();
	}
	
	
	public static void addDoc(IndexWriter w, String libelle, String synonyme) throws CorruptIndexException, IOException{
		
		Document doc = new Document();
		doc.add(new Field("libelle", libelle, Store.YES, Index.ANALYZED));
		doc.add(new Field("synonyme", synonyme, Store.YES, Index.ANALYZED));
		w.addDocument(doc);
	}
	
	public static Query keywordQuery(String value, Analyzer analyzer) throws Exception{
	 
	    if(StringUtils.isEmpty(value) || null==analyzer)
	        return null;
	    final QueryParser parser = getQueryParser(analyzer);
	    parser.setFuzzyMinSim(0.6f);
	    final Query query = parser.parse(value);
	    return query;
	}
	
	public static QueryParser getQueryParser(final Analyzer analyzer) {
	    QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36,
	        new String[]{
	            "libelle",
	            "synonyme"
	        },
	        analyzer);
	    return parser;
	}

	
	public static void displayIndex(IndexWriter w) throws IOException {

		// Affichage des tokens dans le libelle
		System.out.println("****** DEBUT : Affichage des tokens dans le libelle *******");

		TermEnum terms = w.getReader().terms(new Term("libelle"));
		if (null != terms.term()) {
			do {
				Term term = terms.term();
				if (term.field().endsWith("libelle"))
					System.out.println("[" + term.field() + "] == " + term.text());
			} while (terms.next());
		}

		System.out.println("****** FIN : Affichage des tokens dans le libelle *******");
	}
	
	public static Query getNearQuery(String input){
		String[] terms = input.split(" ");
		SpanQuery[] clauses = new SpanQuery[terms.length];
		
		for (int i = 0; i < terms.length; i++) {
			SpanTermQuery sq = new SpanTermQuery(new Term("libelle", terms[i]));
			FuzzyQuery firstNameQuery = new FuzzyQuery(new Term("libelle", terms[i]), 0.5f, 2, 10);

			clauses[i] = new SpanMultiTermQueryWrapper<MultiTermQuery>(firstNameQuery);
		}
		
		SpanNearQuery snq = new SpanNearQuery(clauses , 3, false);
		return snq;
	}
	
	public static BooleanQuery getLibelleWithApproximatifQuery(String userInput) {

		BooleanQuery approximativeRecherche = new BooleanQuery();
		String[] termSaisie = userInput.split(" ");

		for (int i = 0; i < termSaisie.length; i++) {
			FuzzyQuery fuzz = new FuzzyQuery(new Term("libelle", termSaisie[i]), 0.5f, 2, 100);
			approximativeRecherche.setBoost(Constante.LIBELLE_SCORE);
			approximativeRecherche.add(fuzz, Occur.MUST);
		}
		return approximativeRecherche;
	}
}
