package com.lucene.LuceneT;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
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
 * Created by lenovo on 2016/9/30.
 */
public class LuceneIQ {
    private static IndexWriter writer;
    private static Analyzer analyzer;
    private static IndexSearcher searcher;
    private static Directory dir;

    private static  String docs="IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。";

    private static String docs2="中文分词开源工具包";

    private static String keyword="开源";

    /**
     * 创建索引
     */
    public static void createIndex()throws Exception{
        analyzer=new IKAnalyzer();
        dir= FSDirectory.open(new File("C:\\lucene"));
        IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_42,analyzer);
        writer=new IndexWriter(dir,config);
        Document doc=new Document();
        doc.add(new IntField("id",1,Field.Store.YES));
        doc.add(new TextField("content",docs, Field.Store.YES));

        writer.addDocument(doc);
        writer.commit();
        writer.close();
    }

    /**
     * 查询
     */
    public static  void queryIndex() throws Exception{
        analyzer=new IKAnalyzer();
        dir=FSDirectory.open(new File("C:\\lucene"));
        IndexReader reader= DirectoryReader.open(dir);
        searcher=new IndexSearcher(reader);

        QueryParser parser=new QueryParser(Version.LUCENE_42,"content",analyzer);
        Query query=parser.parse(keyword);

        TopDocs docs=searcher. search(query,10);
        ScoreDoc[] scoreDoc=docs.scoreDocs;

        //设置高亮
        QueryScorer scorer=new QueryScorer(query);
        Fragmenter fragmenter=new SimpleSpanFragmenter(scorer);

        //设置标签内部颜色
        SimpleHTMLFormatter simpleHTMLFormatter=new SimpleHTMLFormatter("<font color='red'>","</font>");

        //设置高亮
        Highlighter highlighter=new Highlighter(simpleHTMLFormatter,scorer);

        //设置片段
        highlighter.setTextFragmenter(fragmenter);
        //高亮设置完成
        for (int i=0;i<scoreDoc.length;i++){
            Document doc=searcher.doc(scoreDoc[i].doc);
            System.out.println("id:"+doc.get("id"));
            System.out.println("content:"+doc.get("content"));


            String content=doc.get("content");
            TokenStream tokenStream=analyzer.tokenStream("content",new StringReader(content));
            System.out.println(highlighter.getBestFragment(tokenStream,content));
        }

    }
    /**
     * 索引删除
     */
    public static void deleteIndex()throws Exception{
        dir=FSDirectory.open(new File("C:\\lucene"));
        analyzer=new IKAnalyzer();
        IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_42,analyzer);
        IndexWriter writer=new IndexWriter(dir,config);
        QueryParser parser=new QueryParser(Version.LUCENE_42,"content",analyzer);
        Query query=parser.parse(keyword);

        //删除索引
        writer.deleteDocuments(query);

        //更新索引
//        Document doc=new Document();
//        doc.add(new TextField("content",docs2, Field.Store.YES));
//        writer.updateDocument(new Term("content",docs),doc);


        writer.commit();
        writer.close();
        System.out.println("删除成功");

    }

    /**
     * 测试
     *
     */
    public static void main(String[] args)throws Exception {
//        LuceneIQ.createIndex();
//        LuceneIQ.deleteIndex();

        LuceneIQ.queryIndex();

    }

}
