package hch.hooney.mysedyproject.DO;

public class WordDO {
    private String word;
    private String atr;
    private String definition;
    private String example_sentence;

    public WordDO(){
        this.word = null;
        this.atr = null;
        this.definition = null;
        this.example_sentence = null;
    }

    public WordDO(String w, String a, String d, String e){
        this.word = w;
        this.atr = a;
        this.definition = d;
        this.example_sentence = e;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getAtr() {
        return atr;
    }

    public void setAtr(String atr) {
        this.atr = atr;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExample_sentence() {
        return example_sentence;
    }

    public void setExample_sentence(String example_sentence) {
        this.example_sentence = example_sentence;
    }

    @Override
    public String toString() {
        return "WordDO{" +
                "word='" + word + '\'' +
                ", atr='" + atr + '\'' +
                ", definition='" + definition + '\'' +
                ", example_sentence='" + example_sentence + '\'' +
                '}';
    }
}
