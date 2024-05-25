import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Stack;

public class Playlist {

    private String name;
    private int playingMode = 0;
    private int playingIndex = 0;
    private ArrayList<PlayableItem> curList;
    private PlayableItem cur;
    private Stack<PlayableItem> history;
    private PriorityQueue<PlayableItem> freqListened;
    private ArrayList<PlayableItem> playlist;
    private Iterator<PlayableItem> iterator;
    private ArrayList<PlayableItem> freqList;
    private ArrayList<PlayableItem> recList;
    private int recIndex;
    private int previousMode;

    public Playlist() {
        name = "Default";
        playingMode = 0;
        playingIndex = 0;
        curList = new ArrayList<PlayableItem>();
        cur = null;
        history = new Stack<PlayableItem>();
        freqListened = new PriorityQueue<PlayableItem>(PlayableItem::compareTo);
        playlist = new ArrayList<PlayableItem>();
        freqList = new ArrayList<PlayableItem>();
        recList = new ArrayList<PlayableItem>();
        previousMode = 0;
    }

    public Playlist(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int size() {
        return playlist.size();
    }

    public String toString() {
        String str = "";
        str = str + name + "," + Integer.toString(size()) + " songs";
        return str;
    }

    private void generate() {
        freqListened = new PriorityQueue<PlayableItem>(PlayableItem::compareTo);
        for (PlayableItem item: playlist) {
            freqListened.offer(item);
        }
        freqList = new ArrayList<PlayableItem>();
        PlayableItem item = null;
        while (true) {
            item = freqListened.poll();
            if (item == null) {
                break;
            } else {
                freqList.add(item);
            }
        }
    }

    public void addPlayableItem(PlayableItem newItem) {
        playlist.add(newItem);
        generate();
    }

    public void addPlayableItem(ArrayList<PlayableItem> newItem) {
        playlist.addAll(newItem);
        generate();
    }

    public boolean removePlayableItem(int number) {
        if (cur != null) {
            if (cur.equals(playlist.get(number - 1))) {
                while (cur.playable()) {
                    cur.play();
                }
            }
            generate();
            if (playingMode == 2) {
                cur = getNextPlayable();
            }
            return false;
        }
        if (playingMode == 2) {
            playlist.remove(freqList.get(number - 1));
            freqList.remove(number - 1);
        } else {
            playlist.remove(number - 1);
            generate();
        }
        return true;
    }

    public void switchPlayingMode(int newMode) {
        previousMode = playingMode;
        playingMode = newMode;
        playingIndex = 0;
        if (playingMode == 2) {
            generate();
        }
        if (playingMode == 3) {
            MusicDatabase md = new MusicDatabase();
            File file = new File("songs.csv");
            md.addSongs(file);
            for (PlayableItem item: playlist) {
                freqListened.offer(item);
            }
            recList = md.getRecommendedSongs(getFiveMostPopular());
            playingIndex = playlist.size() - 1;
            playlist.addAll(recList);
            freqList.addAll(recList);
        }
    }

    private void sortByArtist(ArrayList<String> arr) {
        for (int i = 1; i < arr.size(); i++) {
            String x = arr.get(i);
            int j = i - 1;
            while (j > -1 && arr.get(j).compareTo(x) > 0) {
                arr.set(j + 1, arr.get(j));
                j--;
            }
            arr.set(j + 1, x);
        }
    }

    public ArrayList<String> getFiveMostPopular() {
        Iterator<PlayableItem> it = freqListened.iterator();
        ArrayList<String> artists = new ArrayList<String>();
        while (it.hasNext()) {
            artists.add(it.next().getArtist());
        }
        int num = 5;
        if (artists.size() > num) {
            sortByArtist(artists);
        } else {
            return artists;
        }
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < num; i++) {
            result.add(artists.get(i));
        }
        return result;
    }

    /**
     * Go to the last playing item
     */
    public void goBack() {
        if (history.isEmpty()) {
            System.out.println("No more step to go back");
        } else {
            cur = history.pop();
            playingIndex = playingIndex - 1;
        }
    }

    public void play(int seconds) {
        String str = "";
        if (seconds <= 0) {
            str = "Invalid seconds.";
            System.out.println(str);
            return;
        }
        if (cur == null) {
            cur = getNextPlayable();
        }
        int sec = 0;
        while (true) {
            if (sec == seconds) {
                break;
            }
            if (cur == null) {
                str = str + "No more music to play.";
                break;
            }
            str = str + "Seconds " + Integer.toString(sec) +  " : " + cur.getTitle() + " start.\n";
            if (!cur.playable()) {
                sec = sec + 1;
            } else {
                while (cur.playable()) {
                    cur.play();
                    sec = sec + 1;
                    if (sec == seconds) {
                        break;
                    }
                }
            }
            if (!cur.playable()) {
                if (sec == seconds) {
                    str = str + "Seconds " + Integer.toString(sec)
                            +  " : " + cur.getTitle() + " complete.";
                } else {
                    str = str + "Seconds " + Integer.toString(sec)
                            +  " : " + cur.getTitle() + " complete.\n";
                }
                cur.play();
                if (playingMode == 0 | playingMode == 2 | playingMode == 3) {
                    history.push(cur);
                }
                cur = getNextPlayable();
            }
        }
        System.out.println(str);
    }

    public String showPlaylistStatus() {
        String str = "";
        int i = 1;
        if (playingMode == 0 | playingMode == 1) {
            for (PlayableItem item: playlist) {
                if (cur != null) {
                    if (item.equals(cur)) {
                        str = str + Integer.toString(i)
                                + ". " + item.toString() + " - Currently play\n";
                    } else {
                        str = str + Integer.toString(i)
                                + ". " + item.toString() + "\n";
                    }
                } else {
                    str = str + Integer.toString(i)
                            + ". " + item.toString() + "\n";
                }
                i = i + 1;
            }
        } else if (playingMode == 2) {
            for (PlayableItem item: freqList) {
                if (cur != null) {
                    if (item.equals(cur)) {
                        str = str + Integer.toString(i) + ". "
                                + item.toString() + " - Currently play\n";
                    } else {
                        str = str + Integer.toString(i) + ". "
                                + item.toString() + "\n";
                    }
                } else {
                    str = str + Integer.toString(i) + ". "
                            + item.toString() + "\n";
                }
                i = i + 1;
            }
        }
        return str;
    }

    public PlayableItem getNextPlayable() {
        PlayableItem item = null;
        if (playingMode == 0) {
            if (playingIndex >= playlist.size()) {
                item = null;
            } else {
                item = playlist.get(playingIndex);
                playingIndex = playingIndex + 1;
            }
        } else if (playingMode == 1) {
            int index = (int) Math.random() * (playlist.size());
            item = playlist.get(index);
        } else if (playingMode == 2) {
            if (playingIndex >= freqList.size()) {
                item = null;
            } else {
                item = freqList.get(playingIndex);
                playingIndex = playingIndex + 1;
            }
        } else if (playingMode == 3) {
            if (playingIndex >= playlist.size()) {
                item = null;
            } else {
                item = playlist.get(playingIndex);
                playingIndex = playingIndex + 1;
            }
        }
        return item;
    }

}
