import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class MusicDatabase {

    private Hashtable<String, ArrayList<PlayableItem>> data; 
    private TreeMap<String, ArrayList<PlayableItem>> artists;
    private Recommendation recommender;
    private int size;

    public MusicDatabase() {
        data = new Hashtable<>();
        artists = new TreeMap<>();
        recommender = new Recommendation("UserData.csv");
        size = 0;
    }

    public boolean addSongs(File inputFile) {
        try {
            Scanner sc = new Scanner(inputFile);
            ArrayList<List<String>> lst = new ArrayList<List<String>>();
            List<String> insideLst = new ArrayList<String>();
            String str;
            while (sc.hasNextLine()) {
                str = sc.nextLine();
                insideLst = Arrays.asList(str.split(","));
                lst.add(insideLst);
            }
            List<String> l = new ArrayList<String>();
            for (int i = 1; i < lst.size(); i++) {
                l = lst.get(i);
                addSongs(l.get(2), l.get(3), Integer.parseInt(l.get(4)),
                        Integer.parseInt(l.get(5)), l.get(7));
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public void addSongs(String name, String artist,
                         int duration, int popularity, String endpoint) {
        PlayableItem item = new PlayableItem(0, duration, endpoint, name, artist, popularity);
        ArrayList<PlayableItem> lst = new ArrayList<PlayableItem>();
        lst.add(item);
        if (data.containsKey(name)) {
            int count = 0;
            for (PlayableItem val: data.get(name)) {
                if (val.equals(item)) {
                    val.setPopularity(popularity);
                    count = 1;
                    break;
                }
            }
            if (count == 0) {
                data.get(name).add(item);
                size = size + 1;
            }
        } else {
            data.put(name, lst);
            size = size + 1;
        }
        if (artists.containsKey(artist)) {
            int count = 0;
            for (PlayableItem val: artists.get(artist)) {
                if (val.equals(item)) {
                    val.setPopularity(popularity);
                    count = 1;
                    break;
                }
            }
            if (count == 0) {
                artists.get(artist).add(item);
            }
        } else {
            artists.put(artist, lst);
        }
    }

    public ArrayList<PlayableItem> partialSearchBySongName(String name) {
        ArrayList<PlayableItem> lst = new ArrayList<PlayableItem>();
        Set<String> allKeys = data.keySet();
        for (String str: allKeys) {
            if (str.toLowerCase().contains(name.toLowerCase())) {
                lst.addAll(data.get(str));
            }
        }
        return lst;
    }

    public void sortPop(ArrayList<PlayableItem> lst) {
        for (int i = 1; i < lst.size(); i++) {
            PlayableItem x = lst.get(i);
            int j = i - 1;
            while (j > -1 && lst.get(j).getPopularity() < x.getPopularity()) {
                lst.set(j + 1, lst.get(j));
                j--;
            }
            lst.set(j + 1, x);
        }
    }

    public ArrayList<PlayableItem> partialSearchByArtistName(String name) {
        ArrayList<PlayableItem> lst = new ArrayList<PlayableItem>();
        Set<String> allKeys = artists.keySet();
        for (String str: allKeys) {
            if (str.toLowerCase().contains(name.toLowerCase())) {
                lst.addAll(artists.get(str));
            }
        }
        sortPop(lst);
        return lst;
    }

    public ArrayList<PlayableItem> searchHighestPopularity(int threshold) {
        ArrayList<PlayableItem> lst = new ArrayList<>();
        Set<String> allKeys = data.keySet();
        ArrayList<PlayableItem> l;
        for (String str: allKeys) {
            l = data.get(str);
            for (PlayableItem val: l) {
                if (val.getPopularity() >= threshold) {
                    lst.add(val);
                }
            }
        }
        sortPop(lst);
        return lst;
    }

    public ArrayList<PlayableItem> getRecommendedSongs(List<String> fiveArtists) {
        String[] artist = recommender.recommendNewArtists(fiveArtists);
        ArrayList<PlayableItem> lst = new ArrayList<PlayableItem>();
        ArrayList<PlayableItem> l;
        for (String str: artist) {
            l = artists.get(str);
            if (l != null) {
                for (PlayableItem item : l) {
                    lst.add(item);
                }
            }
        }
        sortPop(lst);
        ArrayList<PlayableItem> result = new ArrayList<PlayableItem>();
        if (lst.size() > 10) {
            for (int i = 0; i < 10; i++) {
                result.add(lst.get(i));
            }
        } else {
            result = lst;
        }
        return result;
    }

    public int size() {
        return size;
    }
}
