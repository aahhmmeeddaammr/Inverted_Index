package invertedIndex;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 *
 * @author ehab
 */
public class Index5 {

    //--------------------------------------------
    int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index
    //--------------------------------------------

    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    public void setN(int n) {
        N = n;
    }


    /**
     * Prints the posting list for a given term.
     * @param p The head of the posting list.
     */
     public void printPostingList(Posting p) {
        // Iterator<Integer> it2 = hset.iterator();
        System.out.print("[");
        while (p != null) {
            System.out.print(p.docId);   //print current element

            /// -4- **** complete here ****
             // fix get rid of the last comma

             //check If there's another element after this, print comma
            if (p.next != null) {
            System.out.print(",");
        }

            // System.out.print("" + p.docId + "," );  -->  add a comma even after the last element

            p = p.next;
        }
        System.out.println("]");
    }

    /**
     * Prints the entire dictionary along with document frequency and posting lists.
     */
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
            printPostingList(dd.pList);
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    /**
     * Builds the index by processing a set of files.
     * @param files Array of file paths.
     */
    public void buildIndex(String[] files) {  // from disk not from the internet
        int fid = 0; // First document id
        for (String fileName : files) {// looping over the files names
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {// reading each file content
                if (!sources.containsKey(fileName)) {// checking if the file isn't stored in sources
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));//adding the new file to sources
                }
                String ln;//declaring a variable to store each line in each document
                int flen = 0;//variable to store the length of words in each document
                while ((ln = file.readLine()) != null) {//exit the loop when the file content ends

                    flen+=indexOneLine(ln,fid);//sending each line in each file to be added to the index
                    // and updating the length of words in the document
                    /// -2- **** complete here ****
                    ///**** hint   flen +=  ________________(ln, fid);
                }
                sources.get(fid).length = flen;//setting the length of the document

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;//incrementing the document id
        }
        //   printDictionary();
    }

    /**
     * Indexes a single line from a document.
     * @param ln Line of text.
     * @param fid Document ID.
     * @return Number of words in the line.
     */
    public int indexOneLine(String ln, int fid) {
        int flen = 0;

        String[] words = ln.split("\\W+");//splitting the line of words and storing it into an array of strings
        //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;//adding the length of the words to the document length variable
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);//converting each word to the root of the word

            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }

   /**
     * Checks if a word is a stop word.
     * @param word Word to check.
     * @return True if it's a stop word, otherwise false.
     */
   public boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;

    }

    /**
     * Applies stemming to a word (currently a placeholder).
     * @param word Input word.
     * @return Stemmed word.
     */
    public String stemWord(String word) { //skip for now
        return word;
//        Stemmer s = new Stemmer();
//        s.addString(word);
//        s.stem();
//        return s.toString();
    }

    /**
     * Intersects two posting lists and returns a new posting list
     * containing document IDs that appear in both lists.
     *
     * @param pL1 The first posting list.
     * @param pL2 The second posting list.
     * @return A new posting list containing the intersection of pL1 and pL2.
     */
    public Posting intersect(Posting pL1, Posting pL2) {
        // Initialize the resulting posting list
        Posting answer = null;
        Posting last = null; // Pointer to track the last node in the result list

        // Traverse both lists until one of them reaches the end
        while (pL1 != null && pL2 != null) {
            // If both lists contain the same document ID, add it to the result
            if (pL1.docId == pL2.docId) {
                Posting new_node = new Posting(pL1.docId); // Create a new posting node

                if (answer == null) {
                    answer = new_node; // Initialize the result list
                } else {
                    last.next = new_node; // Append to the result list
                }
                last = new_node; // Update the last pointer

                // Move both pointers to the next node
                pL1 = pL1.next;
                pL2 = pL2.next;
            }
            // Move pL1 forward if its docId is smaller
            else if (pL1.docId < pL2.docId) {
                pL1 = pL1.next;
            }
            // Move pL2 forward if its docId is smaller
            else {
                pL2 = pL2.next;
            }
        }

        // Return the intersected posting list
        return answer;
    }

    /**
     * Finds documents containing all words in a phrase.
     * @param phrase Input phrase.
     * @return List of matching documents.
     */
    public String find_24_01(String phrase) { // any mumber of terms non-optimized search
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;

        //fix this if word is not in the hash table will crash...
        Posting posting = index.get(words[0].toLowerCase()).pList;
        int i = 1;
        while (i < len) {
            Boolean StopWord = stopWord(words[i]);
            if (StopWord) {
                i++;
                continue;
            }
            posting = intersect(posting, index.get(words[i].toLowerCase()).pList);
            i++;
        }
        while (posting != null) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
            posting = posting.next;
        }
        return result;
    }


    /**
     * Sorts an array of words in lexicographical order using Bubble Sort.
     * @param words Array of words to be sorted.
     * @return Sorted array of words.
     */
    public String[] sort(String[] words) {
        boolean sorted = false; // Flag to track if sorting is complete
        String sTmp; // Temporary variable for swapping

        // Bubble Sort algorithm
        while (!sorted) {
            sorted = true; // Assume sorted unless a swap is made
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]); // Compare adjacent words
                if (compare > 0) { // If words[i] is greater, swap
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false; // Set flag to false since a swap was made
                }
            }
        }
        return words; // Return the sorted array
    }

    /**
     * Stores the index to a file.
     * @param storageName Filename for storing the index.
     */
    public void store(String storageName) {
        try {
            String pathToStorage = "C:\\Users\\Ahmed\\Desktop\\My Projects\\Information Retrieval\\is322_HW_1\\is322_HW_1\\"+storageName+".txt";
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = " + entry.getValue().title + ", Value = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); //String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                //  System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    //    System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============EBD STORE=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether a storage file exists in the specified directory.
     *
     * @param storageName The name of the storage file to check.
     * @return true if the file exists and is not a directory, false otherwise.
     */
    public boolean storageFileExists(String storageName) {
        // Construct the file path by appending the storage name to the directory path
        java.io.File f = new java.io.File("/home/ehab/tmp11/rl/" + storageName);

        // Check if the file exists and is not a directory
        if (f.exists() && !f.isDirectory()) {
            return true; // File exists
        }
        return false; // File does not exist or is a directory
    }

    /**
     * Creates a new storage file with the specified name.
     *
     * @param storageName The name of the storage file to create.
     */
    public void createStore(String storageName) {
        try {
            // Define the full file path where the storage file will be created
            String pathToStorage = "/home/ehab/tmp11/" + storageName;

            // Create a FileWriter to write to the specified file
            Writer wr = new FileWriter(pathToStorage);

            // Write the string "end" followed by a newline character to the file
            wr.write("end" + "\n");

            // Close the writer to ensure data is saved and resources are released
            wr.close();
        } catch (Exception e) {
            // Print the stack trace in case of an error during file creation
            e.printStackTrace();
        }
    }

    /**
     * Loads an index from a storage file into memory.
     *
     * @param storageName The name of the storage file to load.
     * @return A HashMap representing the inverted index.
     */
    public HashMap<String, DictEntry> load(String storageName) {
        try {
            // Construct the full path to the storage file
            String pathToStorage = "/home/ehab/tmp11/rl/" + storageName;

            // Initialize sources and index maps
            sources = new HashMap<>();
            index = new HashMap<>();

            // Open the file for reading
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));

            String ln = "";
            int flen = 0;

            // Read the first section of the file (Source Records)
            while ((ln = file.readLine()) != null) {
                // Check for the section separator
                if (ln.equalsIgnoreCase("section2")) {
                    break;
                }

                // Split the line by commas to extract fields
                String[] ss = ln.split(",");
                int fid = Integer.parseInt(ss[0]);

                try {
                    // Print debug info (document details)
                    System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " [" + ss[4] + "]   " + ss[5].replace('~', ','));

                    // Create a SourceRecord object and store it in sources map
                    SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                    sources.put(fid, sr);
                } catch (Exception e) {
                    System.out.println(fid + "  ERROR  " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Read the second section of the file (Inverted Index)
            while ((ln = file.readLine()) != null) {
                // Check for the end of the file
                if (ln.equalsIgnoreCase("end")) {
                    break;
                }

                // Split the line into dictionary entry and postings list
                String[] ss1 = ln.split(";");
                String[] ss1a = ss1[0].split(","); // Dictionary entry
                String[] ss1b = ss1[1].split(":"); // Posting list

                // Create and store the dictionary entry
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));

                // Process the posting list
                String[] ss1bx;
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");

                    // Add a new posting to the list
                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }

            // Indicate that loading is complete
            System.out.println("============= END LOAD =============");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return the loaded index
        return index;
    }

}

//=====================================================================
