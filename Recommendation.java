import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
//import java.util.stream.Collectors;

public class Recommendation {

    Map<Long, HashMap<String, Integer>> userData;

    public Recommendation(String filePath) {
        userData = new HashMap<>();
        parseCsvFile(filePath);
        sortMin();
    }

    private void sortMin() {
        int num = 5;
        ArrayList<String> str;
        ArrayList<Integer> mins;
        Set<Long> allKeys = userData.keySet();
        Set<String> keys;
        HashMap<String, Integer> hm;
        HashMap<String, Integer> value;
        for (Long key: allKeys) {
            hm = userData.get(key);
            keys = hm.keySet();
            str = new ArrayList<String>();
            mins = new ArrayList<Integer>();
            value = new HashMap<String, Integer>();
            for (String k: keys) {
                str.add(k);
                mins.add(hm.get(k));
            }
            sortMins(str, mins);
            if (str.size() > num) {
                for (int i = 0; i < num; i++) {
                    value.put(str.get(i), mins.get(i));
                }
                userData.put(key, value);
            } else {
                for (int i = 0; i < str.size(); i++) {
                    value.put(str.get(i), mins.get(i));
                }
                userData.put(key, value);
            }
        }
    }

    private void sortMins(ArrayList<String> str, ArrayList<Integer> arr) {
        for (int i = 1; i < arr.size(); i++) {
            int x = arr.get(i);
            String s = str.get(i);
            int j = i - 1;
            while (j > -1 && arr.get(j) <= x) {
                arr.set(j + 1, arr.get(j));
                str.set(j + 1, str.get(j));
                j--;
            }
            arr.set(j + 1, x);
            str.set(j + 1, s);
        }
    }

    private void parseCsvFile(String csvFilePath) {
        ArrayList<List<String>> lst = new ArrayList<List<String>>();
        try {
            File file = new File(csvFilePath);
            Scanner sc = new Scanner(file);
            sc.nextLine();
            while (sc.hasNextLine()) {
                lst.add(Arrays.asList(sc.nextLine().split(",")));
            }
        } catch (IOException e) {
            return;
        }
        HashMap<String, Integer> value;
        Long key;
        int n;
        for (List<String> l: lst) {
            key = Long.valueOf(l.get(0));
            value = new HashMap<String, Integer>();
            if (userData.containsKey(key)) {
                if (userData.get(key).containsKey(l.get(1))) {
                    n = userData.get(key).get(l.get(1)) + Integer.parseInt(l.get(3));
                    userData.get(key).put(l.get(1), n);
                } else {
                    userData.get(key).put(l.get(1), Integer.parseInt(l.get(3)));
                }
            } else {
                value.put(l.get(1), Integer.parseInt(l.get(3)));
                userData.put(key, value);
            }
        }
    }

    public double compute(Set<String> str1, List<String> str2) {
        double score;
        double a = 0;
        double b = 0;
        for (String str: str1) {
            if (str2.contains(str)) {
                a = a + 1;
            }
        }
        b = str1.size() + str2.size() - a;
        score = (double) a / b;
        return score;
    }

    private void sortScore(ArrayList<Long> id, ArrayList<Double> arr) {
        for (int i = 1; i < arr.size(); i++) {
            double x = arr.get(i);
            Long s = id.get(i);
            int j = i - 1;
            while (j > -1 && arr.get(j) < x) {
                arr.set(j + 1, arr.get(j));
                id.set(j + 1, id.get(j));
                j--;
            }
            arr.set(j + 1, x);
            id.set(j + 1, s);
        }
    }

    public String[] recommendNewArtists(List<String> artistList) {
        ArrayList<Long> id = new ArrayList<Long>();
        ArrayList<Double> score = new ArrayList<Double>();
        Set<Long> allKeys = userData.keySet();
        for (Long key: allKeys) {
            id.add(key);
            score.add(compute(userData.get(key).keySet(), artistList));
        }
        //System.out.println(id.toString());
        //System.out.println(score.toString());
        sortScore(id, score);
        //System.out.println(id.toString());
        //System.out.println(score.toString());
        ArrayList<String> artists = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            for (String str: userData.get(id.get(i)).keySet()) {
                artists.add(str);
            }
        }
        ArrayList<String> recommend = new ArrayList<String>();
        for (int i = 0; i < artists.size(); i++) {
            if (!artistList.contains(artists.get(i)) && !recommend.contains(artists.get(i))) {
                recommend.add(artists.get(i));
            }
        }
        String[] result = new String[recommend.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = recommend.get(i);
        }
        return result;
    }

}
