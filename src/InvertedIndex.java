
import java.io.*;
import static java.lang.System.out;
import java.util.*;

//=====================================================================
class DictEntry2 {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    public HashSet<Integer> postingList;

    DictEntry2() {
        postingList = new HashSet<Integer>();
    }
}

//=====================================================================
class Index2 {

    //--------------------------------------------
    Map<Integer, String> sources;  // store the doc_id and the file name
    HashMap<String, DictEntry2> index; // THe inverted index
    //--------------------------------------------

    Index2() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry2>();
    }

    //---------------------------------------------
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry2 dd = (DictEntry2) pair.getValue();
            HashSet<Integer> hset = dd.postingList;// (HashSet<Integer>) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
            Iterator<Integer> it2 = hset.iterator();
            while (it2.hasNext()) {
                System.out.print(it2.next() + ", ");
            }
            System.out.println("");
            //it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry2());
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }
                //  printDictionary();
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
    }

    HashSet<Integer> intersect(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<Integer>();
        Iterator<Integer> p1 = pL1.iterator();
        Iterator<Integer> p2 = pL2.iterator();
        int docID1 = 0, docID2 = 0;
        if (p1.hasNext()) {
            docID1 = p1.next();
        }
        if (p2.hasNext()) {
            docID2 = p2.next();
        }
        while (p1.hasNext() && p2.hasNext()) {
            if (docID1 == docID2) {
                answer.add(docID1);
                docID1 = p1.next();
                docID2 = p2.next();
            } else if (docID1 < docID2) {
                if (p1.hasNext()) {
                    docID1 = p1.next();
                } else {
                    return answer;
                }
            } else {
                if (p2.hasNext()) {
                    docID2 = p2.next();
                } else {
                    return answer;
                }
            }
        }
        if (docID1 == docID2) {
            answer.add(docID1);
        }
        return answer;
    }

    String[] rearrangeWords(String[] words, int[] frequency, int lenght) {
        boolean isSorted = false;
        int hold;
        String sHold;
        for (int i = 0; i < lenght - 1; i++) {
            frequency[i] = index.get(words[i].toLowerCase()).doc_freq;
        }
        while (isSorted) {
            isSorted = true;
            for (int i = 0; i < lenght - 1; i++) {
                if (frequency[i] > frequency[i + 1]) {
                    hold = frequency[i];
                    sHold = words[i];
                    frequency[i] = frequency[i + 1];
                    words[i] = words[i + 1];
                    frequency[i + 1] = hold;
                    words[i + 1] = sHold;
                    isSorted = false;
                }
            }
        }
        return words;
    }

    public String find(String phrase) {

        String result = "";
        String[] words = phrase.split("\\W+");
        try {
            HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            for (String word : words) {
                res.retainAll(index.get(word).postingList);
            }
            for (int num : res) {
                result += sources.get(num) + "\n";
            }
        } catch (Exception e) {
            System.out.println("Not found");
        }
        return result;
    }

    public String find_04(String phrase) {
        String result = "";
        try {
            // write you code here
            String[] words = phrase.split("\\W+");
            int len = words.length;
            words = rearrangeWords(words, new int[len], len);
            HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            int i = 1;
            while (i < len) {
                res = intersect(res, index.get(words[i].toLowerCase()).postingList);
                i++;
            }
            for (int num : res) {
                result += "\t" + sources.get(num) + "\n";
            }
        } catch (Exception e) {
            System.out.println("Not found");
        }
        return result;
    }

}

public class InvertedIndex {

    static void findMissing(String a[], String b[],
            int n, int m) {
        for (int i = 0; i < n; i++) {
            int j;

            for (j = 0; j < m; j++) {
                if (a[i] == null ? b[j] == null : a[i].equals(b[j])) {
                    break;
                }
            }

            if (j == m) {
                System.out.print(a[i] + " ");
            }
        }
    }

    public static void main(String args[]) throws IOException {
        Scanner input = new Scanner(System.in);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Index2 index = new Index2();
        String phrase = "";
        String phrase2 = "";
        String part1 = "";
        String part2 = "";
        index.buildIndex(new String[]{
            "D:\\Second term- level four\\Information retreival\\MalekMohamedAmin_20180209\\tryTXT\\1.txt",
            "D:\\Second term- level four\\Information retreival\\MalekMohamedAmin_20180209\\tryTXT\\2.txt",
            "D:\\Second term- level four\\Information retreival\\MalekMohamedAmin_20180209\\tryTXT\\3.txt",});
        // String arrFiles[] = { "F:\\tryTXT\\1.txt" ,  "F:\\tryTXT\\2.txt",  "F:\\tryTXT\\3.txt"};
        System.out.println("Print search phrase: ");
        phrase = in.readLine();
        String[] words = phrase.split("\\s");;
        part1 = words[0];
        part2 = words[2];
       
        if (words[1].equals("and")) {
            phrase2 = part1 + " " + part2;
            out.println(index.find_04(phrase2));
        }

        if (words[1].equals("not")) {
            String findSecond = index.find(part2);
            String[] findSecondArr = findSecond.split("\\s");

            String findFirst = index.find(part1);
            String[] findFirstArr = findFirst.split("\\s");
             
            Set<String> Set1 = new HashSet<>();

            // Iteration using enhanced for loop
            for (String element : findFirstArr) {
                Set1.add(element);
            }

          Set<String> Set2 = new HashSet<>();

            // Iteration using enhanced for loop
            for (String element2 : findSecondArr) {
                Set2.add(element2);
            }
          Set1.removeAll(Set2);
          
          Iterator<String> _iterator = Set1.iterator();
        // Iterate the elements of Set
        while (_iterator.hasNext()) {
            // print the element of the Set
            System.out.print(_iterator.next() + "\n");
        }
    }
        
         if (words[1].equals("or")) {
            String findSecond = index.find(part2);
            String[] findSecondArr = findSecond.split("\\s");

            String findFirst = index.find(part1);
            String[] findFirstArr = findFirst.split("\\s");
             
            Set<String> Set1 = new HashSet<>();

            // Iteration using enhanced for loop
            for (String element : findFirstArr) {
                Set1.add(element);
            }

          Set<String> Set2 = new HashSet<>();

            // Iteration using enhanced for loop
            for (String element2 : findSecondArr) {
                Set2.add(element2);
            }
            
          Set1.addAll(Set2);
          Iterator<String> _iterator = Set1.iterator();
        // Iterate the elements of Set
        while (_iterator.hasNext()) {
            // print the element of the Set
            System.out.print(_iterator.next() + "\n");
        }
    }    
}
}
