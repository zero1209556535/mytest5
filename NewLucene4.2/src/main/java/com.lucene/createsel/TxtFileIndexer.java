package com.lucene.createsel;

import javafx.scene.control.TextFormatter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by lenovo on 2016/9/21.
 */
public class TxtFileIndexer {
    public static void main(String[] args)throws Exception {
        //indexdir是lucene程序索引文件存放目录
        File indexdir=new File("C:\\index");
        //datadir是对本地文本文件进行索引的目录
        File datadir=new File("C:\\test");

        //分词方法IK
        Analyzer luceneAnalyer=new IKAnalyzer();
//        Analyzer luceneAnalyer=new StandardAnalyzer(Version.LUCENE_42);

        //中文查询分词器
//        Analyzer luceneAnalyer=new SmartChineseAnalyzer(Version.LUCENE_42);

        IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_42,luceneAnalyer);

        if(true){
            //创建一个新的索引文件覆盖现有的索引文件
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        }else{
//            如果不存在索引文件创建新的索引文件
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        }

        //写入指定文件中的索引文档（文件储存索引）
        Directory directory= FSDirectory.open(indexdir);

        //创建索引
        //第一个参数为创建索引所存放的位置
        //第二个参数为指定的分词器对文档内容进行分词
        IndexWriter indexWriter=new IndexWriter(directory,config);

        //遍历test文件夹目录下的所有txt文档，并且为每个txt创建Document对象
        Long startTime=new Date().getTime();
        //获取数据源
        File[] dataFiles=datadir.listFiles();
        for (int i=0;i<dataFiles.length;i++){
            //把文本文档中的两个属性：内容和路径加载到2个Field对象中去，接着将这两个对象加载到Document对象中去
            //最后把这个文档用IndexWrite的add方法加入到索引中去
            //当前用例只支持文本文档
            if(dataFiles[i].isFile()&&dataFiles[i].getName().endsWith("txt")){
                System.out.println("索引文件："+dataFiles[i].getCanonicalPath());
                Document document=new Document();
                File file=dataFiles[i];

                //Field.Store 指定当前字段的索引方式
                //Field.Store.No 不要在索引中存储字段的值
                //Field.Store.YES 在索引中存储原始字段的值
                //TextField.TYPE_STORED 索引，分词，存储
                //TextField.TYPE_NO_STORED 索引，分词，不存储
                //存储文件路径
//                document.add(new StringField("id",i+"", Field.Store.YES));
//                document.add(new StringField("path",file.getCanonicalPath(),Field.Store.YES));

//                document.add(new Field("id",i+"",Field.Store.YES, Field.Index.ANALYZED));
//                document.add(new Field("path",file.getCanonicalPath(),Field.Store.YES,Field.Index.ANALYZED));

                document.add(new TextField("id",i+"",Field.Store.YES));
                document.add(new TextField("path",file.getCanonicalPath(),Field.Store.YES));

                //存储文件内容流
                FileInputStream fis=new FileInputStream(file);
                BufferedReader bTxtReader=new BufferedReader(new InputStreamReader(fis,"UTF-8"));
                document.add(new TextField("content",bTxtReader));

                //将文档对象写入到索引文件中
                if(indexWriter.getConfig().getOpenMode()== IndexWriterConfig.OpenMode.CREATE){
                    //没有旧的索引，所以向文件中添加新的索引
                    System.out.println("adding"+file);
                    indexWriter.addDocument(document);
                }else{
                    //如果存在旧的索引则用updatedocument代替就的 匹配新的索引路径
                    System.out.println("update"+file);
                    indexWriter.updateDocument(new Term("path",file.getPath()),document);
                }
            }
        }
        indexWriter.commit();
        indexWriter.close();
        long endTime=new Date().getTime();
        System.out.println("他需要"+(endTime-startTime)+"毫秒来创建目录中的文件索引"+datadir.getPath());

    }
}
