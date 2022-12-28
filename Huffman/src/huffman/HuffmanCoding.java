package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);
	    /* Your code goes here */
        sortedCharFreqList = new ArrayList<CharFreq>();
        int totalCharacters = 0; //helps us to to each char in input, ends up being the total number of characters inputed
        StdIn.setFile(fileName);
        String inputString = StdIn.readAll(); //reads the entire string input
        char[] inputCharArr = inputString.toCharArray(); //turns that string input into a char array
        
        //Character in = inputCharArr[0];
        //CharFreq firstCh = new CharFreq(in, 1/inputCharArr.length);
        //sortedCharFreqList.add(firstCh);

        //goes through the char array, add new elements that didn't exist to sortedCharFreqList, and updating the prob for those already in the arrayList
        while(totalCharacters < inputCharArr.length){
            boolean doesExist = false;
            
            for(int i = 0; i < sortedCharFreqList.size(); i++){
                //if the element alreay exists in the array, increase its probability 
                Character current = inputCharArr[totalCharacters];
                Character existing = sortedCharFreqList.get(i).getCharacter();
             
                if(current.equals(existing)){
                    double prob = sortedCharFreqList.get(i).getProbOcc() + 1;
                    sortedCharFreqList.get(i).setProbOcc(prob);
                    doesExist = true;
                }
            } 
            
            if(doesExist == false){
                //if the elemetn doen't exist create it with its new probablity.
                CharFreq newChar = new CharFreq(inputCharArr[totalCharacters], 1);
                sortedCharFreqList.add(newChar);
            }
          
            totalCharacters++;
            
       
        }
        if(sortedCharFreqList.size() == 1){
            int newChar = (int)sortedCharFreqList.get(0).getCharacter() + 1;
            if(newChar == 127){
                newChar = 0;
            }
            Character sus = (char) newChar;
            CharFreq onlyOne = new CharFreq(sus, 0.0);
            sortedCharFreqList.add(onlyOne);
        }

        for(int i = 0; i < sortedCharFreqList.size(); i++){
            double update = (double)sortedCharFreqList.get(i).getProbOcc()/inputCharArr.length;
            sortedCharFreqList.get(i).setProbOcc(update);
            //System.out.println(sortedCharFreqList.get(i).getProbOcc());
        }


        Collections.sort(sortedCharFreqList);
       
    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {
	        /* Your code goes here */
            //STEP 1
            Queue<TreeNode> Source = new Queue<>();
            Queue<TreeNode> Target = new Queue<>();
    
            //STEP 2 and 3
            //Starts with all the nodes inside of source and Target queue is empty
            for(int i = 0; i < sortedCharFreqList.size(); i++){
                TreeNode newNode = new TreeNode(sortedCharFreqList.get(i), null, null);
                Source.enqueue(newNode);
            }
    
            TreeNode dqOne = null;
            TreeNode dqTwo = null;

            while(Source.size() + Target.size() != 1){
                
                //Gets the first removed into dqOne
                if(Source.isEmpty() && !Target.isEmpty()){
                    dqOne = Target.peek();
                    Target.dequeue();
                }else{
                    if((Target.isEmpty() && !Source.isEmpty()) || Source.peek().getData().getProbOcc() <= Target.peek().getData().getProbOcc()){
                        dqOne = Source.peek();
                        Source.dequeue();
                    }else{
                        dqOne = Target.peek();
                        Target.dequeue();
                    }
                }

                //Gets the second removed into dqTwo
                if(Source.isEmpty() && !Target.isEmpty()){
                    dqTwo = Target.peek();
                    Target.dequeue();
                }else{
                    if((Target.isEmpty() && !Source.isEmpty()) || Source.peek().getData().getProbOcc() <= Target.peek().getData().getProbOcc()){
                        dqTwo = Source.peek();
                        Source.dequeue();
                    }else{
                        dqTwo = Target.peek();
                        Target.dequeue();
                    }
                }

                CharFreq insertIntoNewlyCreatedNode = new CharFreq(null, dqOne.getData().getProbOcc() + dqTwo.getData().getProbOcc());
                TreeNode newlyCreatedNode = new TreeNode(insertIntoNewlyCreatedNode, dqOne, dqTwo);
                Target.enqueue(newlyCreatedNode);


            }
            //System.out.println(Target.size());
            //System.out.println(Source.size());
    
            huffmanRoot = Target.peek();
        }

    
    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    public void makeEncodings() {
        /* Your code goes here */
        encodings = new String[128];
        boolean itemIsPresent = false;
        Character[] ASCII = new Character[128];

        for(int i = 0; i <ASCII.length; i++){
            ASCII[i] = (char)i;
        }

        //Creates the string array with the elements are found
        for(int i = 0; i < ASCII.length; i ++){
            itemIsPresent = false;
            for(int j = 0; j < sortedCharFreqList.size(); j++){
                if( ASCII[i].equals(sortedCharFreqList.get(j).getCharacter())){
                itemIsPresent = true;
                }
            }        
            if(!itemIsPresent){
                ASCII[i] = null;
            }
        }

        for(int i = 0; i < encodings.length; i++){
            if(ASCII[i] != null){
            encodings[i] = ASCII[i].toString();
            }
        }
    
        //System.out.println("---------------------------");
        TreeNode temp = huffmanRoot;
        search(temp, "");
}


private void search(TreeNode x, String item){
    if(x.getLeft() != null){
        search(x.getLeft(), item + "0");
    }
    if(x.getRight() != null){
        search(x.getRight(), item + "1");
    }
    if(x.getLeft() == null && x.getRight() == null){
        for(int i = 0; i < encodings.length; i++){
            if(x.getData().getCharacter().toString().equals(encodings[i])){
                encodings[i] =  item;
                item = "";
            }
        }
    }

}

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        /* Your code goes here */
        String ended = "";
        while(StdIn.hasNextChar()){
            Character a = StdIn.readChar();

            for(int i = 0; i < encodings.length; i ++){
                if(a.equals((char)i) && encodings[i] != null){
                    //System.out.println(encodings[i]);
                    //System.out.println("---------");
                    ended = ended + encodings[i];
                }
            }

        }
       // System.out.println(ended);
       
        writeBitString(encodedFile, ended);

    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);
	    /* Your code goes here */
        String recieved = readBitString(encodedFile);
        String finalAmogus = "";
        char[] recievedSeperated = recieved.toCharArray();
       
        int i = 0;
        String temp = "";
        TreeNode original = huffmanRoot;

        int[] comparing = new int[recievedSeperated.length];
        for(int t = 0; t < comparing.length; t++){
            if(recievedSeperated[t] ==  (char)48 ){
                comparing[t] = 0;
            }else{
                comparing[t] = 1;
            }
        }


        while(i < recievedSeperated.length){
            
            huffmanRoot = original;
            while(huffmanRoot.getLeft() != null && huffmanRoot.getRight() != null){
                
                if(comparing[i] == 0){
                    temp += "0";
                    huffmanRoot = huffmanRoot.getLeft();
                    }else{
                    if(comparing[i] == 1){
                        temp += "1";
                        huffmanRoot = huffmanRoot.getRight();
                    }
                }
                
                i++;

            }
            
            for(int k = 0; k < encodings.length; k++){
                if(encodings[k] != null && encodings[k].equals(temp)){
                    finalAmogus += (char)k;
                }
            }
            
            temp = "";
            }
            
        

        //System.out.println("FINAL");
        //System.out.println(finalAmogus);
        StdOut.print(finalAmogus);

        //writeBitString(decodedFile, readBitString(temp));
    
    }
    
    

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
