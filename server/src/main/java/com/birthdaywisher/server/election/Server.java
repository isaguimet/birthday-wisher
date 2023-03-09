public class Server {
    private int serverId;
    public int serverIndex;
    private int[] serverId;
    private boolean active;
    private int predId;
    private int succId;
    private int predIndex;
    private int succIndex;
    private boolean running;

    Server(int index) {
        this.index = index;
        this.running = false;
        setSuccessor(this.index);
        setPedecessor(this.index);
    }

    private void setSuccessor() {

        int succ = this.index + 1;
        if(succ < serverId.length) {
            this.succId = serverId[succ];
        } else {
            this.succId = 0; //to back to begining
        }

    }

    private void setPedecessor() {
        int pred = this.index - 1;
        if (pred > 0) {
            this.predId = pred;
        } else {
            this.predId = serverId[serverId.length - 1];
        }

    }

    //check if it's server exists
    public int getServerId() {
        return this.serverId;
    }
    public int getSuccId() {
        return this.succId;
    }
    public int getPredId() {
        return this.predId;
    }
    public int getServerIndex() {
        return this.serverIndex;
    }
    public int getSuccIndex() {
        return this.succIndex;
    }
    public int getPredIndex() {
        return this.predIndex;
    }

    public boolean getRunning() {
        return this.running;
    }

    public void setRunning(boolean status) {
        this.running = status;
    }
}
