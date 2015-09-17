package com.compiler.util;

import java.io.*;


/**
 * Created by compiler on 31/08/15.
 */
public class FileReader implements Closeable {

    private  Reader fileBuffer;
    private int actualLine;
    private int actualColumn;

    public FileReader() {
        this.actualLine = 1;
    }

    public void close() throws IOException {
        if (this.fileBuffer != null){
            this.fileBuffer.close();
            this.fileBuffer = null;
        }
        else{
            throw new  IOException("fileReader is already closed");
        }
    }

    public int getActualLine() {
        return actualLine;
    }

    public int getActualColumn() {
        return actualColumn ;
    }


    public boolean isEndOfFile(char character){
        if(character == '\0'){
            return true;
        }
        return  false;
    }

    public void open(String path) throws IOException {
        File file = new File(path);
        InputStream in = new FileInputStream(file);
        Reader reader = new InputStreamReader(in);
        fileBuffer = new BufferedReader(reader);
    }

    public char getNextChar() throws IOException , IllegalStateException{
        if(fileBuffer == null){
            throw  new IllegalStateException("the file is closed");
        }
        int charCode = fileBuffer.read();
        if(charCode == -1){
            return  '\0';
        }
        else {
            char character =  (char)charCode;
            updateLineAndColumn(character);
            return character;
        }
    }

    private void updateLineAndColumn(char character){
        if(character == '\n' || character == '\r'){
            this.actualColumn = 0;
            this.actualLine++;
        }
        else if(character == '\t'){
            this.actualColumn += 4;
        }
        else {
            this.actualColumn++;
        }
    }


}
