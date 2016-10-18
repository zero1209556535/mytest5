package com.lucene.LuceneT;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by lenovo on 2016/9/27.
 */
public class LuceneTest {
    public static void main(String[] args) throws IOException, ParseException {
        String fieldName = "text";
        //检索内容
		String text = "IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。";
        Analyzer analyzer=new IKAnalyzer();
        Directory dir= FSDirectory.open(new File("C:\\lucene"));
        IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_42,analyzer);
        IndexWriter writer=LuceneUtils.getIndexWriter(dir,config);
        Document doc=new Document();
        doc.add(new TextField(fieldName,text, Field.Store.YES));
        LuceneUtils.addIndex(writer,doc);
        System.out.println("创建索引success");


        IndexReader reader= DirectoryReader.open(dir);
        IndexSearcher searcher=new IndexSearcher(reader);
        QueryParser queryParser=new QueryParser(Version.LUCENE_42,fieldName,analyzer);
        Query query=queryParser.parse("IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法");
        List<Document> list= LuceneUtils.query(searcher,query);
        for (Document document:list){
            System.out.println(document.get(fieldName));
        }
    }
}
