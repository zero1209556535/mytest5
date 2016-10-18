package com.lucene.LuceneT;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.StringReader;

/**
 * Created by lenovo on 2016/10/8.
 */
public class LuceneIUDQH {
    private static IndexWriter writer;
    private static Analyzer analyzer;
    private static Directory dir;
    private static IndexSearcher searcher;

    private static String index="IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。";
    private static String keyword="开源工具包";

    //创建索引
    public static void createIndex() throws Exception{
        analyzer=new IKAnalyzer();
        dir= FSDirectory.open(new File("C:\\lucene1"));
        IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_42,analyzer);
        writer=new IndexWriter(dir,config);

        Document doc=new Document();
        doc.add(new IntField("id",1, Field.Store.YES));
        doc.add(new StringField("content",index,Field.Store.YES));

        writer.addDocument(doc);
        writer.commit();
        writer.close();

        System.out.println("索引创建成功");
    }


    //查询
    public static void queryIndex() throws Exception{
        analyzer=new IKAnalyzer();
        dir=FSDirectory.open(new File("C:\\lucene1"));
        IndexReader reader= DirectoryReader.open(dir);
        searcher=new IndexSearcher(reader);

        QueryParser parser=new QueryParser(Version.LUCENE_42,"content",analyzer);
        Query query=parser.parse(keyword);

        TopDocs docs=searcher.search(query,10);
        ScoreDoc[] scoreDocs=docs.scoreDocs;

        //设置高亮Start
        QueryScorer queryScorer=new QueryScorer(query);
        Fragmenter fragmenter=new SimpleSpanFragmenter(queryScorer);
        SimpleHTMLFormatter simpleHTMLFormatter=new SimpleHTMLFormatter("<font color='red'>","</font>");
        Highlighter highlighter=new Highlighter(simpleHTMLFormatter,queryScorer);
        highlighter.setTextFragmenter(fragmenter);

        for(int i=0;i<scoreDocs.length;i++){
            Document doc=searcher.doc(scoreDocs[i].doc);
            System.out.println("id:"+doc.get("id"));
            System.out.println("content:"+doc.get("content"));

            String content=doc.get("content");
            TokenStream tokenStream=analyzer.tokenStream("content",new StringReader(content));
            System.out.println(highlighter.getBestFragment(tokenStream,content));
        }

        System.out.println("查询成功");

    }


    public static void main(String[] args)throws Exception {
        LuceneIUDQH.createIndex();
        LuceneIUDQH.queryIndex();
    }
}
