public class Message {
    private int type; //0 if election, 1 if leader
    private Server s;
    private int currentIndex;

    message(int type, Server s) {
        this.type = type;
        this.s = s;
    }
    
    public Server getServer() {
        return this.s;
    }

    public Server getType(){
        return this.type;
    }

    public int getCurrentIndex(){
        return this.currentIndex;
    }

    public int setCurrentIndex(int index){
        this.currentIndex = index;
    }
}
