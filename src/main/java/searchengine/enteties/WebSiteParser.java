package searchengine.enteties;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import searchengine.models.HtmlInfo;
import searchengine.models.LemmaModel;
import searchengine.repositories.HtmlRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveAction;

//@Service
public class WebSiteParser extends RecursiveAction {
    public static Set<String> links = Collections.synchronizedSet(new HashSet<>());
    private final int webSiteId;
    private final String webSiteName;
    private final String suffix;
    private String htmlText;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private HtmlRepository htmlRepository;


    public WebSiteParser(int webSiteId, String webSiteName, String suffix) {
        this.webSiteId = webSiteId;
        this.webSiteName = webSiteName.replaceAll("//www\\.", "//");
        this.suffix = suffix;
    }

    @Override
    public void compute() {
        List<HtmlInfo> htmlInfoList = htmlRepository.findAll();
        try {
            for (HtmlInfo htmlInfo : htmlInfoList) {
                String text = htmlInfo.getHtmlText();
                saveLemmas(text);
            }

            List<LemmaModel> lemmaModelList = lemmaRepository.findAll();

            lemmaModelList.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        try {
//            List<WebSiteParser> taskList = getTaskList();
//
//            int taskListSize = taskList.size() - 1;
//            for (int i = taskListSize; i >= 0; i--) {
//                WebSiteParser parser = taskList.get(i);
//                parser.join();
//                PageModel pageModel = new PageModel(webSiteId, suffix, 200, parser.htmlText);
//                pageRepository.saveAndFlush(pageModel);
//            }
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
    }

    private Elements getLinkElements(Document document) {
        System.out.println(webSiteName.concat(suffix));
        return document.select("a");
    }

    private List<WebSiteParser> getTaskList() throws IOException {
        List<WebSiteParser> taskList = new ArrayList<>();

        Document document = getDocument();
        Elements elements = getLinkElements(document);
        String fullLink = webSiteName.concat(suffix);
        saveLemmas(getText());

        elements.stream()
                .map(element -> changeReservedCharacters(element.attr("href")))
                .filter(suffix -> linkSuffixFilter(suffix) && !links.contains(fullLink))
                .forEach(currSuffix -> {
                    links.add(fullLink);

                    WebSiteParser parser = new WebSiteParser(webSiteId, webSiteName, currSuffix);
                    parser.fork();
                    taskList.add(parser);
                });

        return taskList;
    }

    private void saveLemmas(String text) throws IOException {
        LuceneMorphology luceneMorphRus = new RussianLuceneMorphology();
        LuceneMorphology luceneMorphEng = new RussianLuceneMorphology();

        String[] words = text.replaceAll("[^a-zA-Z\\-\\s]", "").split("\\s+");

        HashSet<String> lemmasSet = new HashSet<>();
        for (String word : words) {
            if (luceneMorphEng.checkString(word)) {
                List<String> lemmas = luceneMorphEng.getNormalForms(word);
                lemmasSet.addAll(lemmas);
            } else if (luceneMorphRus.checkString(word)) {
                List<String> lemmas = luceneMorphRus.getNormalForms(word);
                lemmasSet.addAll(lemmas);
            }
        }

        for (String lemma : lemmasSet) {
            Optional<LemmaModel> lemmaModelOptional = lemmaRepository.findByLemma(lemma);
            if (lemmaModelOptional.isPresent()) {
                lemmaRepository.updateFrequencyById(lemmaModelOptional.get().getId());
            } else {
                lemmaRepository.saveAndFlush(new LemmaModel(webSiteId, lemma, 1));
            }
        }
    }

    private String getText() throws IOException {
        return getDocument().text();
    }

    private Document getDocument() throws IOException {
        return Jsoup.connect(webSiteName.concat(suffix))
                .ignoreHttpErrors(true)
                .followRedirects(true)
                .ignoreContentType(true)
                .timeout(5_000)
                .get();
    }

    private boolean linkSuffixFilter(String suffix) {
        return suffix.startsWith("/") && !suffix.equals("/") && !links.contains(suffix);
    }

    private String changeReservedCharacters(String link) {
        return link
                .replaceAll("%20", " ")
                .replaceAll("%21", "!")
                .replaceAll("%22", "\"")
                .replaceAll("%23", "#")
                .replaceAll("%24", "$")
                .replaceAll("%25", "%")
                .replaceAll("%26", "&")
                .replaceAll("%27", "'")
                .replaceAll("%28", "(")
                .replaceAll("%29", ")")
                .replaceAll("%2A", "*")
                .replaceAll("%2B", "+")
                .replaceAll("%2C", ",")
                .replaceAll("%2F", "/")
                .replaceAll("%3A", ":")
                .replaceAll("%3B", ";")
                .replaceAll("%3D", "=")
                .replaceAll("%3F", "?")
                .replaceAll("%40", "@")
                .replaceAll("%5B", "[")
                .replaceAll("%5D", "]");
    }
}
