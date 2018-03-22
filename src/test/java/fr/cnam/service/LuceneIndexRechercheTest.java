package fr.cnam.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tartarus.snowball.ext.FrenchStemmer;

import fr.cnam.SpringBootWebApplication;
import fr.cnam.model.Motif;
import fr.cnam.util.AATLuceneAnalyzerUtil;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=SpringBootWebApplication.class)
public class LuceneIndexRechercheTest {

	@Autowired
	private LuceneIndexRecherche service;
	
	@Test
	public void testSaisieExacte() {
		List<Motif> resultat = service.rechercher("frac");
		Assert.assertTrue(resultat.size() == 1);
		Assert.assertTrue(resultat.get(0).getLibelle().equalsIgnoreCase("Angine"));
		
	}
	
	@Test
	public void test2(){
		
//		String input ="Même";
//		TokenStream tokenStream = new StandardTokenizer(
//                Version.LUCENE_36, new StringReader(input ));
//		
//		Set<String> normaliserListe = AATLuceneAnalyzerUtil.normaliserListe(AATLuceneAnalyzerUtil.etendreFrenchStopWordSet());
//		tokenStream = new LowerCaseFilter(Version.LUCENE_36, tokenStream);
//		tokenStream = new ASCIIFoldingFilter(tokenStream);
//        tokenStream = new StopFilter(Version.LUCENE_36, tokenStream, normaliserListe);
//        tokenStream = new SnowballFilter(tokenStream, new FrenchStemmer());
// 
//        StringBuilder sb = new StringBuilder();
//        CharTermAttribute charTermAttr = tokenStream.getAttribute(CharTermAttribute.class);
//        
//        try{
//            while (tokenStream.incrementToken()) {
//                if (sb.length() > 0) {
//                    sb.append(" ");
//                }
//                sb.append(charTermAttr.toString());
//            }
//        }
//        catch (IOException e){
//            System.out.println(e.getMessage());
//        }
//        System.out.println(sb.toString());
	}
	
}
