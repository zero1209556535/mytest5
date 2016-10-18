package com.lucene.sel;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;


import java.io.File;

/**
 * Created by lenovo on 2016/9/21.
 */
public class TxtFileSearcher {
    public static void main(String[] args)throws Exception {
        String queryStr="阿莱克斯塔萨玛里苟斯";

        //lucene程序搜索的索引目录
        File indexDir=new File("C:\\index");

        IndexReader reader= DirectoryReader.open(FSDirectory.open(indexDir));

        IndexSearcher searcher=new IndexSearcher(reader);


        if(!indexDir.exists()){
            System.out.println("Lucene索引不存在");
            return;
        }

        //StandardAnalyzer
//        Term term=new Term("content",queryStr.toLowerCase());
//        TermQuery luceneQuery=new TermQuery(term);

        //IK
        QueryParser parser=new QueryParser(Version.LUCENE_42,"content",new IKAnalyzer());
        parser.setDefaultOperator(QueryParser.AND_OPERATOR);


        Query luceneQuery=parser.parse(queryStr.toLowerCase());

        TopDocs results=searcher.search(luceneQuery,1000);
        int numTotalHits=results.totalHits;
        System.out.println("共"+numTotalHits+"条结果与搜索匹配");


        ScoreDoc[] hit=results.scoreDocs;

        for (int i=0;i<hit.length;i++){
            Document document=searcher.doc(hit[i].doc);
            String path=document.get("path");
            System.out.println("id:"+document.get("id"));
            System.out.println("File:"+path);

        }
    }
}
