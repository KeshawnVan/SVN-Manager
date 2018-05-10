package com.star.svn.parser;

import com.google.common.collect.Lists;
import com.star.svn.function.Predicate;
import com.star.svn.function.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class FileParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileParser.class);
    private static final String INDEX = "Index:";

    public void parseDiffFile(String path){

        List<List<String>> blocks = getContent(path);
        for (List<String> block : blocks) {
            String index = block.get(0);
            String filePath = "/"+index.substring(7,index.length());

            List<String> addLines = Streams.filter(block, new Predicate<String>() {
                @Override
                public Boolean is(String s) {
                    return s.startsWith("+") && !s.startsWith("+++");
                }
            });

            List<String> deleteLines = Streams.filter(block, new Predicate<String>() {
                @Override
                public Boolean is(String s) {
                    return s.startsWith("-") && !s.startsWith("---");
                }
            });
            System.out.println(deleteLines);
        }
        System.out.println(blocks);

    }

    private List<List<String>> getContent(String path) {
        List<List<String>> blocks = Lists.newLinkedList();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)))){
            String line = bufferedReader.readLine();
            while (line != null){
                if (line.startsWith(INDEX)){
                    List<String> block = Lists.newLinkedList();
                    block.add(line);
                    blocks.add(block);
                }else {
                    blocks.get(blocks.size() - 1).add(line);
                }
                line = bufferedReader.readLine();
            }
        } catch (Exception e){
            LOGGER.error("file parse error", e);
        }
        return blocks;
    }

    public static void main(String[] args) {
        new FileParser().parseDiffFile("/Users/fankaixiang/Downloads/新建文件夹/svn_diff_file_4_5_1525947332223.txt");
    }
}
