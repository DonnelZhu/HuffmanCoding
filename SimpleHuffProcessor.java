/*  Student information for assignment:
 *
 *  On <MY|OUR> honor, <NAME1> and <NAME2), this programming assignment is <MY|OUR> own work
 *  and <I|WE> have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1 (Student whose turnin account is being used)
 *  UTEID:
 *  email address:
 *  Grader name:
 *
 *  Student 2
 *  UTEID:
 *  email address:
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private HuffmanTree tree;
    private int[] freq;

    /**
     * Preprocess data so that compression is possible ---
     * count characters/create tree/store state so that
     * a subsequent call to compress will work. The InputStream
     * is <em>not</em> a BitInputStream, so wrap it into one as needed.
     * @param in is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what kind of
     * header to use, standard count format, standard tree format, or
     * possibly some format added in the future.
     * @return number of bits saved by compression or some other measure
     * Note, to determine the number of
     * bits saved, the number of bits written includes
     * ALL bits that will be written including the
     * magic number, the header format number, the header to
     * reproduce the tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
        // store frequency in int array, has size of 256
        freq = new int[ALPH_SIZE];
        BitInputStream bin = new BitInputStream(in);
        int originalBits = 0;
        // read in the first 8 bits 
        int bit = bin.readBits(BITS_PER_WORD);
        
        while (bit != -1) {
            // orignalBits incremented by 8 to count total # of bits in original file
            originalBits += BITS_PER_WORD;
            // The int at index bit is the frequency of the occurence of bit
            freq[bit]++;
            // goes to the next 8 bits
            bit = bin.readBits(BITS_PER_WORD);
        }

        PriorityQueue<TreeNode> q = new PriorityQueue<>();
        // fills q with treeNodes made from freqb
        createQueue(freq, q);

        // creates huffman tree, takes in priority queue q as parameter
        tree = new HuffmanTree(q);

        // tracks the size of the newly compressed file  
        int compressedSize = findCompressedSize(freq, tree, headerFormat);
        
        System.out.println(originalBits - compressedSize);
        bin.close();
        // return the amount of bits the compression has saved us
        return originalBits - compressedSize;
    }

    private void createQueue(int[] freq, PriorityQueue<TreeNode> q){
        for (int i = 0; i < freq.length; i ++) {
            // if the frequency of a character is > 0, it is added to q as a TreeNode
            if (freq[i] != 0) {
                TreeNode treeNode = new TreeNode(i, freq[i]);
                q.enqueue(treeNode);
            }
        }
        q.enqueue(new TreeNode(PSEUDO_EOF, 1));
    }

    private int findCompressedSize(int[] freq, HuffmanTree tree, int headerFormat){
        int compressedSize = 0;
        compressedSize += 2*BITS_PER_INT;
        if (headerFormat == IHuffConstants.STORE_COUNTS) {
            compressedSize += BITS_PER_INT * ALPH_SIZE;
        }
        // gets map with value and its path in the huffman tree
        HashMap<Integer, String> huffMap = tree.getMap();
        // finishes calculating total # of bits in compressed file
        for (int val: huffMap.keySet()) {
            if (val != PSEUDO_EOF) {
                compressedSize += huffMap.get(val).length() * freq[val];
            } else {
                compressedSize += huffMap.get(val).length();
            }
        } 
        return compressedSize;
    }

    /**
     * Compresses input to output, where the same InputStream has
     * previously been pre-processed via <code>preprocessCompress</code>
     * storing state used by this call.
     * <br> pre: <code>preprocessCompress</code> must be called before this method
     * @param in is the stream being compressed (NOT a BitInputStream)
     * @param out is bound to a file/stream to which bits are written
     * for the compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is larger than the input file.
     * If this is false do not create the output file if it is larger than the input file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        throw new IOException("compress is not implemented");
        //return 0;
    }

    /**
     * Uncompress a previously compressed stream in, writing the
     * uncompressed bits/data to out.
     * @param in is the previously compressed data (not a BitInputStream)
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
        throw new IOException("uncompress not implemented");
        //return 0;
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }



    private void showString(String s){
        if(myViewer != null)
            myViewer.update(s);
    }
}
