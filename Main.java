import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        MusicDatabase db = new MusicDatabase();
        File file = new File("src/songs.csv");
        db.addSongs(file);
        Recommendation r = new Recommendation("src/UserData.csv");
        Playlist l = new Playlist();
        ArrayList<String> test = new ArrayList<String>();
        test.add("a");
        test.add("c");
        test.add("b");
        test.add("a");
        test.remove("a");
        System.out.println(test.toString());
        String str1 = "assf";
        String str2 = "fsd";
        System.out.println(str1.compareTo(str2));
    }
}