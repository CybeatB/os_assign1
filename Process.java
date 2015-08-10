public class Process {
  // Constructor
  public Process() {}

  // Methods
  public int compareTo(Process proc) {
    // this <(-1) =(0) >(+1)
    if (!this._type.equals(proc.type())) {
      return this._type.equals("C") ? -1 : 1;
    }
    if (this._time != proc.time()) {
      return this._time - proc.time();
    }
  }
  public int time() {
    return this._time;
  }
  public String type() {
    return this._type;
  }
  public int seats() {
    return this._seats;
  }
  public String agentID() {
    return this._id;
  }
//TODO: differentiate between action & type
  // Variables
  private int _time;
  private String _type;
  private int _seats;
  private String _id;
}

