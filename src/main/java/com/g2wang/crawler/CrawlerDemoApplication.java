package com.g2wang.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@SpringBootApplication
public class CrawlerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerDemoApplication.class, args);
        crawl("https://sedna.com/", "");
    }

    private static Set<String> visited = Collections.synchronizedSet(new HashSet<>());
    private static final Pattern SEDNA_PATTERN = Pattern.compile("^https://sedna\\.com/.*$");
	private static final String INDENT = "  ";

	private static void crawl(String url, String parentUrl) {
        if (!visited.contains(url) && SEDNA_PATTERN.matcher(url).matches()) {
            try {
                visited.add(url);
				System.out.println("-----------------------------------");
				System.out.println(url + ":");
				System.out.println(INDENT + "referer: " + parentUrl);

                Document document = Jsoup.connect(url).get();

				System.out.println(INDENT + "css: ");
				Elements cssOnPage = document.select("link[href]")
						.select("link[rel=stylesheet]");
				for (Element css : cssOnPage) {
					System.out.println(INDENT + INDENT + css.attr("href"));
				}

				System.out.println(INDENT + "scripts: ");
				Elements scriptsOnPage = document.select("script[src]");
				for (Element script: scriptsOnPage) {
					System.out.println(INDENT + INDENT + script.attr("src"));
				}

				System.out.println(INDENT + "images: ");
				Elements imagesOnPage = document.select("img[src]");
				for (Element img: imagesOnPage) {
					System.out.println(INDENT + INDENT + img.attr("src"));
				}

				Elements linksOnPage = document.select("a[href]");

				// use depth first search to crawl
				for (Element link : linksOnPage) {
                    crawl(link.attr("abs:href"), url);
                }

            } catch (IOException e) {
                System.err.println("error handling '" + url + "': " + e.getMessage());
            }
        }
    }
}
