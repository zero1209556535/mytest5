package com.lucene.sel;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;

/**
 * Created by lenovo on 2016/9/22.
 */
public class aPP {
    public static void main(String[] args) {
        String fieldName="test";
//        String text="IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法";

        String text="阿莱克斯塔萨";

        Analyzer analyzer=new IKAnalyzer();

        Directory dir=null;
        IndexWriter iwriter=null;
        IndexReader ireader=null;
        IndexSearcher isearcher=null;

        try {
            dir= new RAMDirectory();

            IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_42,analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            iwriter=new IndexWriter(dir,config);

            Document doc=new Document();
            doc.add(new Field("ID","10000",Field.Store.YES,Field.Index.NOT_ANALYZED));
            doc.add(new Field(fieldName,text,Field.Store.YES,Field.Index.ANALYZED));
            iwriter.addDocument(doc);
            iwriter.close();


            //搜索过程
            ireader=IndexReader.open(dir);
            isearcher=new IndexSearcher(ireader);

            String keyword="阿莱";

            QueryParser queryParser=new QueryParser(Version.LUCENE_42,fieldName,analyzer);
            queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query query=queryParser.parse(keyword);

            TopDocs topDocs=isearcher.search(query,5);
            System.out.println("找到"+topDocs.totalHits+"条记录");

            ScoreDoc[] scoreDoc=topDocs.scoreDocs;
            for(int i=0;i<topDocs.totalHits;i++){
                Document targertDoc=isearcher.doc(scoreDoc[i].doc);
                System.out.println("查找到的内容为"+targertDoc.toString());
            }

        }catch (Exception e){
            e.printStackTrace();

        }finally{
            if(ireader!=null){
                try {
                    ireader.close();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(dir!=null){
                try {
                    dir.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
