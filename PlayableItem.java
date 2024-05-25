/**
 * <b>May not add any accessor/mutator for this class</b>
 */
public class PlayableItem {
    private int lastPlayedTime;
    private int totalPlayTime;
    private String endpoint;
    private String title;
    private String artist;
    private int popularity;
    private int playedCounts; // How many times this song has been played, initially to be 0

    public PlayableItem(int lastPlayedTime, int totalPlayTime, String endpoint,
                        String title, String artist, int popularity) {
        this.lastPlayedTime = lastPlayedTime;
        this.totalPlayTime = totalPlayTime;
        this.endpoint = endpoint;
        this.title = title;
        this.artist = artist;
        this.popularity = popularity;
        playedCounts = 0;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int pop) {
        popularity = pop;
    }

    public boolean playable() {
        return lastPlayedTime != totalPlayTime;
    }

    public boolean play() {
        if (playable()) {
            lastPlayedTime = lastPlayedTime + 1;
            return playable();
        } else {
            playedCounts = playedCounts + 1;
            lastPlayedTime = 0;
            return true;
        }
    }

    public boolean equals(PlayableItem another) {
        return another.getTitle().equals(title) && another.getArtist().equals(artist)
                && another.totalPlayTime == totalPlayTime && another.endpoint.equals(endpoint);
    }

    public String toString() {
        String str = "";
        str = title + "," + endpoint + "," + Integer.toString(lastPlayedTime) + ","
                + Integer.toString(totalPlayTime)
                + "," + artist + "," + Integer.toString(popularity)
                + "," + Integer.toString(playedCounts);
        return str;
    }

    public int compareTo(PlayableItem o) {
        return o.playedCounts - playedCounts;
    }
}
