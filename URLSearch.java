


import java.io.FileNotFoundException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 * Class to create a search engine for
 * urls for pages that contain certain words.
 */
public final class URLSearch {

    /**Default constructor. */
    private URLSearch() { }

    /**
     * Initializes hashmap to contain all information
     * from file in map form.
     */
    private static Map<String, ArrayList<String>> makeMap(String fileName) {

        Map<String, ArrayList<String>> hashMap = new HashMap<>();
        try {
            File file = new File(fileName);

            Scanner rd = new Scanner(file);


            String[] keys = new String[0];
            String value = null;
            while (rd.hasNextLine()) {
                value = rd.nextLine();

                keys = rd.nextLine().split(" ");


                for (int i = 0; i < keys.length; i++) {
                    addToMap(keys[i], hashMap, value);
                }
            }

            return hashMap;
        } catch (FileNotFoundException e) {
            System.out.println("File not found");

            return null;
        }


    }

    /**
     * Adds the key into correct position
     * in newly constructed hashmap.
     */
    private static void addToMap(String key, Map<String,
            ArrayList<String>> hash, String value) {

        if (!hash.has(key)) {
            hash.insert(key, new ArrayList<>());
            hash.get(key).add(value);
        } else {
            hash.get(key).add(value);
        }



    }

    /**
     * Conduct the OR function on top
     * two words of the stack.
     */
    private static Stack<String> findOr(Stack<String> words, Map<String,
            ArrayList<String>> hash, Stack<String> url) {
        Stack<String> urlTest = new Stack<>();
        if (words.size() < 2) {
            System.err.println("Not enough arguments");
            return url;
        } else {
            String word1 = words.pop();

            String word2 = words.pop();

            ArrayList<String> list1 = new ArrayList<>();
            list1 = findWords(word1, hash);
            ArrayList<String> list2 = new ArrayList<>();
            list2 = findWords(word2, hash);

            for (int j = 0; j < list2.size(); j++) {
                urlTest.push(list2.get(j));
            }

            for (int i = 0; i < list1.size(); i++) {
                String check = list1.get(i);
                if (!urlTest.contains(check)) {
                    urlTest.push(check);
                }
            }
            return urlTest;
        }

    }

    /**
     * Finds urls that contain the key word.
     */
    private static ArrayList<String>
        findWords(String s, Map<String, ArrayList<String>> hash) {
        ArrayList<String> apply = new ArrayList<>();
        if (hash.has(s)) {
            apply = hash.get(s);
        }
        return apply;
    }

    /**
     * Checking that word exists as key in
     * hashmap.
     */
    private static boolean
        checkWord(String s, Map<String, ArrayList<String>> hash) {

        return hash.has(s);
    }

    /**
     * Print contents of stack or urls.
     */
    private static void printStack(Stack<String> url) {
        Stack<String> cur = new Stack<>();
        if (url.size() < 1) {
            System.out.print("");

        } else {

            while (!url.isEmpty()) {
                System.out.println(url.pop());
            }
        }

    }

    /**
     * Conduct AND function for top two words
     * in stack of user input.
     */
    private static Stack<String> findDup(Stack<String> words, Map<String,
            ArrayList<String>> hash, Stack<String> url) {
        Stack<String> urlTest = new Stack<>();

        if (words.size() < 2) {
            System.err.println("Not enough arguments");
            return url;
        } else {
            String word1 = words.pop();

            String word2 = words.pop();

            ArrayList<String> list1 = new ArrayList<>();
            list1 = findWords(word1, hash);
            ArrayList<String> list2 = new ArrayList<>();
            list2 = findWords(word2, hash);
            for (int i = 0; i < list1.size(); i++) {
                String check = list1.get(i);

                for (int j = 0; j < list2.size(); j++) {

                    if (check.equals(list2.get(j))) {
                        urlTest.push(check);

                    }
                }
            }
            return urlTest;
        }

    }

    /**
     * Find all urls that apply to word once
     * added to stack.
     */
    public static Stack<String> addLinks(String s, Map<String,
            ArrayList<String>> hash, Stack<String> url) {
        ArrayList<String> list1 = new ArrayList<>();
        list1 = findWords(s, hash);

        for (int i = 0; i < list1.size(); i++) {
            if (!url.contains(list1.get(i))) {
                url.push(list1.get(i));
            }
        }

        return url;
    }


    /**
     * Main driver method.
     */
    public static void main(String[] args) {

        Map<String, ArrayList<String>> hash = null;

        hash = makeMap(args[0]);
        System.out.println("Index Created");


        Scanner thru = new Scanner(System.in);
        Stack<String> words = new Stack<>();
        Stack<String> url = new Stack<>();
        System.out.print("> ");
        int execute = 0;
        while (thru.hasNext()) {
            String s = thru.next();

            switch (s) {
                case "?":
                    printStack(url);
                    execute++;
                    break;
                case "!":
                    System.exit(0);
                    execute++;
                    break;
                case "&&":
                    url = findDup(words, hash, url);
                    execute++;
                    break;
                case "||":
                    url = findOr(words, hash, url);
                    execute++;
                    break;
                default:
                    break;

            }
            if (execute == 0 && checkWord(s, hash)) {
                words.push(s);
                url = addLinks(s, hash, url);
            } else if (execute == 0 && !checkWord(s, hash)) {
                System.err.println("Invalid input");
            }

            execute = 0;
            System.out.print("> ");
        }

    }

}