package searchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);




//        String text = "qwe,  sad-ds\tq";
//        String[] words = text.replaceAll("[^a-zA-Z\\-\\s]", "").split("\\s+");
//        System.out.println(Arrays.toString(words));

//        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
//        System.out.println(luceneMorph.checkString("привет - , asd"));
//        List<String> wordBaseForms = luceneMorph.getNormalForms("пёс");
//        wordBaseForms.forEach(System.out::println);
    }
}
