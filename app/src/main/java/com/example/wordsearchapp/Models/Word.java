package com.example.wordsearchapp.Models;

public class Word {

    private int wordSize;
    private String mWord;

    public Word(int wordSize, String mWord){
        this.wordSize =  wordSize;
        this.mWord = mWord;
    }

    public int getWordSize() {
        return wordSize;
    }

    public void setWordSize(int wordSize) {
        this.wordSize = wordSize;
    }

    public String getGameWord() {
        return mWord;
    }

    public void setGameWord(String mWord) {
        this.mWord = mWord;
    }

    @Override
    public String toString() {
        return mWord.toUpperCase();
    }
}
