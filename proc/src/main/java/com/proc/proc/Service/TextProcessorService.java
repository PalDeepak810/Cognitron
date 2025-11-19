package com.proc.proc.Service;

import org.springframework.stereotype.Service;

@Service
public class TextProcessorService {

    public String cleanText(String htmlContent){
        if(htmlContent==null) return " ";

        String text = htmlContent.replaceAll("<[^>]*>"," ");
        text = text.replaceAll("&[^;]+;"," ");
        text = text.replaceAll("[\\s]+"," ").trim();
        return text;
    }
}
