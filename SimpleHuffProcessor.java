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
 *  Grader name: Noah Beal
 *
 *  Student 2
 *  UTEID: asa3676
 *  email address: akif.abidi@utexas.edu
 *
 */


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private HuffmanTree tree;
    private int[] freq;
    private int headerType;
    private boolean hasPreCompression;
    private int diffInBits;
    private HashMap<Integer, String> huffMap;
    private int compressedSize;

    private final int INTERNAL_NODE_VAL = 0;
    private final int LEAF_NODE_VAL = 1;

    /**
     * Preprocess data so that compression is possible --- count characters/create
     * tree/store state so that a subsequent call to compress will work. The
     * InputStream is <em>not</em> a BitInputStream, so wrap it into one as needed.
     * 
     * @param in           is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what kind
     *                     of header to use, standard count format, standard tree
     *                     format, or possibly some format added in the future.
     * @return number of bits saved by compression or some other measure Note, to
     *         determine the number of bits saved, the number of bits written
     *         includes ALL bits that will be written including the magic number,
     *         the header format number, the header to reproduce the tree, AND the
     *         actual data.
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
        compressedSize = findCompressedSize(freq, tree, headerFormat);
        headerType = headerFormat;

        bin.close();
        hasPreCompression = true;
        // return the amount of bits the compression has saved us
        diffInBits = originalBits - compressedSize;
        return diffInBits;
    }

    // fills up PriorityQueue with treenodes made from uncompressed file
    // pre: q != null
    private void createQueue(int[] freq, PriorityQueue<TreeNode> q) {
        if (q == null) {
            throw new IllegalArgumentException("PriorityQueue q cannot be null");
        }
        for (int i = 0; i < freq.length; i++) {
            // if the frequency of a character is > 0, it is added to q as a TreeNode
            if (freq[i] != 0) {
                TreeNode treeNode = new TreeNode(i, freq[i]);
                q.enqueue(treeNode);
            }
        }
        // enqeue TreeNode with PSEUDO_EOF value last
        q.enqueue(new TreeNode(PSEUDO_EOF, 1));
    }

    // calculates the size of the compressed file
    // pre: tree != null
    // post: returns int of the total size of compressed file
    private int findCompressedSize(int[] freq, HuffmanTree tree, int headerFormat) {
        if (tree == null) {
            throw new IllegalArgumentException("HuffmanTree tree cannot be null");
        }
        int compressedSize = 0;
        // gets map with value and its path in the huffman tree
        huffMap = tree.getMap();
        compressedSize += 2 * BITS_PER_INT;
        if (headerFormat == STORE_COUNTS) {
            compressedSize += BITS_PER_INT * ALPH_SIZE;
        } else if (headerFormat == STORE_TREE) {
            compressedSize += BITS_PER_INT;
            for (TreeNode node : tree.getAllInOrder()) {
                // all nodes are one bit
                compressedSize += 1;
                //if a node is a leaf, then it requires 9 bits for the value
                if (node.isLeaf()) {
                    compressedSize += BITS_PER_WORD + 1;
                }
            }
        }
        // finishes calculating total # of bits in compressed file
        for (int val : huffMap.keySet()) {
            if (val != PSEUDO_EOF) {
                compressedSize += huffMap.get(val).length() * freq[val];
            } else {
                compressedSize += huffMap.get(val).length();
            }
        }
        return compressedSize;
    }

    static double timeForWrite = 0;
    static double timeForRead = 0;
    static Stopwatch sp = new Stopwatch();

    /**
     * Compresses input to output, where the same InputStream has previously been
     * pre-processed via <code>preprocessCompress</code> storing state used by this
     * call. <br>
     * pre: <code>preprocessCompress</code> must be called before this method
     * 
     * @param in    is the stream being compressed (NOT a BitInputStream)
     * @param out   is bound to a file/stream to which bits are written for the
     *              compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is larger than
     *              the input file. If this is false do not create the output file
     *              if it is larger than the input file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file or
     *                     writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        timeForWrite = 0;
        timeForRead = 0;
        if (!hasPreCompression) {
            throw new IllegalStateException("File has not been precompressed");
        }

        if (!force && diffInBits <= 0) {
            return 0;
        }


        BitInputStream bin = new BitInputStream(in);
        BitOutputStream bout = new BitOutputStream(out);
        createHeader(bout);
        // write out the actual compressed file
        HashMap<Integer, String> huffMap = tree.getMap();
        sp.start();
        int bit = bin.readBits(BITS_PER_WORD);
        sp.stop();
        timeForRead+=sp.time();
        while (bit != -1) {
            writeStringAsBits(bout, huffMap.get(bit));
            // goes to the next 8 bits
            sp.start();
            bit = bin.readBits(BITS_PER_WORD);
            sp.stop();
            timeForRead+=sp.time();
        }
        // PSEUDO_EOF value at the end of the file
        writeStringAsBits(bout, huffMap.get(PSEUDO_EOF));

        // fill out remaining bits
        bout.flush();

        bin.close();
        bout.close();
        System.out.println(timeForWrite + timeForRead + " " + timeForWrite);
        return compressedSize;
    }

    // Writes out string as bits
    // pre:
    // post: string written out in terms of bits
    private void writeStringAsBits(BitOutputStream out, String string) {
        for (int i = 0; i < string.length(); i ++) {
            sp.start();
            if (string.charAt(i) == '0') {
                out.writeBits(1, 0);
            } else {
                out.writeBits(1, 1);
            }
            sp.stop();
            timeForWrite+=sp.time();
        }
    }

    // Creates header depending on headerType
    // pre: sizeOfFile >= 0
    // post: writes out header
    private void createHeader(BitOutputStream out) {
        sp.start();
        // magic number
        out.writeBits(BITS_PER_INT, MAGIC_NUMBER);
        // header type
        out.writeBits(BITS_PER_INT, headerType);
        sp.stop();
        timeForWrite+=sp.time();
        if (headerType == STORE_COUNTS) { // all counts of "characters"
            countHeader(out);
        } else if (headerType == STORE_TREE) {
            treeHeader(out);
        }

    }

    // Writes out header for Standard Count Format
    private void countHeader(BitOutputStream out){
        for (int frequency: freq) {
            sp.start();
            out.writeBits(BITS_PER_INT, frequency);
            sp.stop();
            timeForWrite+=sp.time();
        }
    }

    // Writes out header for Standard Tree Format
    private void treeHeader(BitOutputStream out) {
        // find the size of the tree header
        sp.start();
        out.writeBits(BITS_PER_INT, tree.getNumInternalNodes() + tree.getNumLeafNodes() + tree.getNumLeafNodes() * (BITS_PER_WORD + 1));
        sp.stop();
        timeForWrite+=sp.time();

        // add the data for the tree
        for (TreeNode node : tree.getAllPreOrder()) {
            if (node.isLeaf()) {
                sp.start();
                out.writeBits(1, 1);
                out.writeBits(BITS_PER_WORD + 1, node.getValue());
                sp.stop();
                timeForWrite+=sp.time();
            } else {
                sp.start();
                out.writeBits(1, 0);
                sp.stop();
                timeForWrite+=sp.time();
            }
        }
    }

    /**
     * Uncompress a previously compressed stream in, writing the uncompressed
     * bits/data to out.
     * 
     * @param in  is the previously compressed data (not a BitInputStream)
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file or
     *                     writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
        final int LEFT = 0;
        BitInputStream bin = new BitInputStream(in);
        BitOutputStream bout = new BitOutputStream(out);
        int decompressedSize = 0;
        if (bin.readBits(BITS_PER_INT) != MAGIC_NUMBER) { // check if file has been compressed
            myViewer.showError("File did not start with huff magic number");
            bout.close();
            bin.close();
            return -1;
        }
        // creates huffman tree for decompression depending on how file was compressed
        int headerType = bin.readBits(BITS_PER_INT);
        HuffmanTree uncompressingTree = headerType == STORE_COUNTS ? createTreeFromCount(bin) : createTreeFromTree(bin);

        int bit = bin.readBits(1);
        TreeNode currentNode = uncompressingTree.getValue(null, bit);

        while (currentNode != null) {
            // reached leaf node with value
            if (currentNode.isLeaf()) {
                int val = currentNode.getValue();
                if (val != PSEUDO_EOF) {
                    bout.write(val);
                    decompressedSize += BITS_PER_WORD;
                    currentNode = uncompressingTree.getValue(null, bit);
                } else {
                    currentNode = null;
                }
            } else {
                if (bit == LEFT) {
                    currentNode = currentNode.getLeft();
                } else {
                    currentNode = currentNode.getRight();
                }
                bit = bin.readBits(1);
            }
        }
        bout.close();
        bin.close();

        bin.close();
        bout.close();
        return decompressedSize;
    }

    // creates HuffmanTree from SCF
    private HuffmanTree createTreeFromCount(BitInputStream in) throws IOException {
        int [] freq = new int[ALPH_SIZE];
        int index = 0;
        while (index < ALPH_SIZE) {
            int bit = in.readBits(BITS_PER_INT);
            if (bit > 0) {
                freq[index] += bit;
            }
            index ++;
        }

        // create priority queue from freq array from header
        PriorityQueue<TreeNode> q = new PriorityQueue<>();
        createQueue(freq, q);

        // return new instance of huffman tree created from the queue
        return new HuffmanTree(q);
    }

    // creates HuffmanTree from SCF
    private HuffmanTree createTreeFromTree(BitInputStream in) throws IOException {
        HuffmanTree result = new HuffmanTree();
        final int INTERNAL_NODE = -1;
        // reads in a size of the tree
        int size = in.readBits(BITS_PER_INT); 
        int bitsProcessed = 0;
        while (bitsProcessed < size) {
            int bit = in.readBits(1);
            bitsProcessed += 1;
            // is internal node
            if (bit == INTERNAL_NODE_VAL) { 
                result.add(INTERNAL_NODE);
            // else is leaf node
            } else if (bit == LEAF_NODE_VAL) {
                int val = in.readBits(BITS_PER_WORD + 1);
                bitsProcessed += (BITS_PER_WORD + 1);
                result.add(val);
            }
        }

        return result;
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    private void showString(String s) {
        if (myViewer != null)
            myViewer.update(s);
    }
}
