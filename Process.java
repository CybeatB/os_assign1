public class Process {
  // Constructor
  public Process(int time, String act, String type, int seats, String id) {
    this._time = time;
    this._action = act;
    this._type = type;
    this._seats = seats;
    this._id = id;
  }

  // Methods
  public int compareTo(Process proc) {
    // this <(-1) =(0) >(+1) (lower value -> higher priority)
    if (!this._action.equals(proc.action())) {
      return this._action.equals("C") ? -1 : 1;
    }
    if (this._time != proc.time()) {
      return this._time - proc.time();
    }
    if (!this._type.equals(proc.type())) {
      return this.typeComp(this._type, proc.type());
    }
    if (this._seats != proc.seats()) {
      return this._seats - proc.seats();
    }
    return this._id.compareTo(proc.id());
  }
  public int time() {
    return this._time;
  }
  public String action() {
    return this._action;
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

  // Variables
  private int _time;
  private String _action;
  private String _type;
  private int _seats;
  private String _id;

  // Helpers
  /* Returns:
   * <0 when a > b
   * =0 when a = b
   * >0 when a < b */
  private int typeComp(String a, String b) {
    if (a.equals(b)) {
      return 0;
    }
    if (a.equals('F')) {
      return -1;
    }
    if (b.equals('F')) {
      return 1;
    }
    if (a.equals('B')) {
      return -1;
    }
    if (b.equals('B')) {
      return 1;
    }
    // Should Never Happen
    return 0;
  }
}

