package com.lucene.LuceneT;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lenovo on 2016/9/27.
 */
public class LuceneManager {
    public volatile static LuceneManager singlenton;
    public volatile static IndexWriter writer;
    public volatile static IndexReader reader;
    public volatile static IndexSearcher searcher;
    public final Lock writeLock=new ReentrantLock();

    public LuceneManager(){}

    public static LuceneManager getInstance(){
        if(singlenton==null){
            synchronized (LuceneManager.class){
                if(singlenton==null){
                    singlenton=new LuceneManager();
                }
            }
        }
        return singlenton;
    }

    /**
     * 获取IndexWtrite的单例对象
     * @param dir
     * @param config
     * @return
     */
    public IndexWriter getIndexWriter(Directory dir, IndexWriterConfig config){
        if(dir==null){
            throw new IllegalArgumentException("Directory can not be null");
        }
        if(config==null){
            throw new IllegalArgumentException("IndexWriterConfig can not be null");
        }

        try {
         writeLock.lock();
            if(writer==null){
                //如果索引目录被锁定抛异常
                if(IndexWriter.isLocked(dir)){
                    throw new LockObtainFailedException("Directory of index had been lock");
                }
                writer=new IndexWriter(dir,config);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            writeLock.unlock();
        }
        return writer;
    }


    /**
     * 获取IndexReader对象
     * @param dir
     * @param NRTReader 是否开启NRTReader 对象
     * @return
     */
    public IndexReader getIndexReader(Directory dir,boolean NRTReader){
        if(dir==null){
            throw new IllegalArgumentException("Directory can not be null");
        }
            try {
                if(reader==null) {
                    reader = DirectoryReader.open(dir);
                }else{
                    if(NRTReader&&reader instanceof IndexReader){
                        reader=DirectoryReader.openIfChanged((DirectoryReader)reader);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reader;
    }

    /**
     * 获取IndexReader对象 （默认不启用NRTReader）
     * @param dir
     * @return
     */
    public IndexReader getIndexReader(Directory dir){

        return getIndexReader(dir,false);
    }



    /**
     * 获取IndexSearch对象
     * @param reader
     * @param executor 如果要开启多线程查询就需要提供ExecutorService对象参数
     * @return
     */
    public IndexSearcher getIndexSearcher(IndexReader reader, ExecutorService executor){
        if(reader==null){
            throw new IllegalArgumentException("IndexReader can not be null");
        }

        if (searcher==null){
            searcher=new IndexSearcher(reader);
        }
        return searcher;
    }

    /**
     * 获取IndexSearch对象 默认不开启多线程查询
     * @param reader
     * @return
     */
    public IndexSearcher getIndexSearcher(IndexReader reader){
        return getIndexSearcher(reader,null);
    }

}
