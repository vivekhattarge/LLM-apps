package com.spring.ai.rag.spring_ai.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class IngestionService implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(IngestionService.class);

    private final VectorStore vectorStore;

    private final JdbcClient jdbcClient;

    @Value("classpath:/docs/article_thebeatoct2024.pdf")
    Resource marketPDF;

    public IngestionService(VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }


    @Override
    public void run(String... args) throws Exception {

        Integer count =
                jdbcClient.sql("select COUNT(*) from vector_store")
                        .query(Integer.class)
                        .single();
        LOGGER.info("No of Records in the PG Vector Store = " + count);

        if(count == 0) {
        var pdfReader = new ParagraphPdfDocumentReader(marketPDF);
        TextSplitter textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(pdfReader.get()));
        LOGGER.info("VectorStore Loaded with data!");
        }

    }
}
