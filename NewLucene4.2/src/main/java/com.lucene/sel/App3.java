package com.lucene.sel;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;

/**
 * Created by lenovo on 2016/9/22.
 */
public class App3 {
    public static void main(String[] args) throws IOException {
        String filePath = "C:\\test\\13.txt";
        String news=new String();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
        String str;
        while ((str = in.readLine()) != null) {
            news+=str;
        }
        in.close();

        System.out.println(news);
        IKAnalyzer analyzer = new IKAnalyzer(true);
        StringReader reader = new StringReader(news);
        TokenStream ts = analyzer.tokenStream("", reader);
        CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
        while(ts.incrementToken()){
            System.out.print(term.toString()+"|");
        }
        analyzer.close();
        reader.close();

        System.out.println();
        StringReader re = new StringReader(news);
        IKSegmenter ik = new IKSegmenter(re,true);
        Lexeme lex = null;

        File  f = new File("C:\\test\\jiego.txt");
        f.delete();
        String path="C:\\test\\jiego.txt";  //%%%%%%


        while((lex=ik.next())!=null){
            System.out.print(lex.getLexemeText()+"|");

            try {
                FileWriter fw=new FileWriter(path,true);
                PrintWriter pw=new PrintWriter(fw);
                pw.print(lex.getLexemeText()+"|");
                pw.close();
                //bw.close();
                fw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace(); }
        }


    }
}
