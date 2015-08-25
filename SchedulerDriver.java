import java.util.*;
import java.io.*;

public class SchedulerDriver{

	public static class Process {
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
	    if (!this._action.equals(proc.action()) && !this._id.equals(proc.agentID())) {
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
	    return this._id.compareTo(proc.agentID());
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

	public static void main(String args[]){

		PriorityQueue<Process> cancel = new PriorityQueue<Process>();
		PriorityQueue<Process> res = new PriorityQueue<Process>();

		HashMap<String, Integer> map = new HashMap<String, Integer>();

		ArrayList<Process> batch = new ArrayList<Process>();
		FileInputStream fstream = null;

		File file = new File(args[0]);

		//Parse File
		try {
			fstream = new FileInputStream(file);
		} catch(IOException e){
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		try{

		while ((strLine = br.readLine()) != null)   {
		  String[] splitString = strLine.split(" ");
		  String agentNum = splitString[0];
		  String act = splitString[1];
		  String type = splitString[2];
		  int seats = Integer.parseInt(splitString[3]);
		  int time = Integer.parseInt(splitString[4]);

		  if(act.equals("R")){
		  	res.add(new Process(time, act, type, seats, agentNum));
		  }
		  else{
		  	cancel.add(new Process(time, act, type, seats, agentNum));
		  }
		}

		br.close();	

		} catch(IOException e){
			e.printStackTrace();
		}

		

		//Output text
		while(res.size() > 0 && cancel.size() > 0){

			int prev_time = -1;

			String agent = cancel.peek().agentID();

			if(map.get(agent) != null){
				prev_time = map.get(agent);

				if(res.peek().time() == prev_time){
					//Reservation

					//Check for batch
					while(true){
						Process top = res.poll();
						batch.add(top);

						if(top.seats() != res.peek().seats()){
							break;
						}
						else if(top.type() != res.peek().type()){
							break;
						}
						else if(top.action() != res.peek().action()){
							break;
						}
					}

					//process the batch
					for (Process p: batch) {
						System.out.println("Agent " + p.agentID() + "Booked " + p.seats() + "seats.");
					}
					batch.clear();
				}
				else{
					//Cancellation

					//Check for batch
					while(true){
						Process top = cancel.poll();
						batch.add(top);

						if(top.seats() != cancel.peek().seats()){
							break;
						}
						else if(top.type() != cancel.peek().type()){
							break;
						}
						else if(top.action() != cancel.peek().action()){
							break;
						}
					}

					//process the batch
					for (Process p: batch) {
						System.out.println("Agent " + p.agentID() + "Booked " + p.seats() + "seats.");
					}
					batch.clear();
				}
			}
			else{
				//Reservation

				//Check for batch
				while(true){
					Process top = res.poll();
					batch.add(top);

					if(top.seats() != res.peek().seats()){
						break;
					}
					else if(top.type() != res.peek().type()){
						break;
					}
					else if(top.action() != res.peek().action()){
						break;
					}
				}

				//process the batch
				for (Process p: batch) {
					System.out.println("Agent " + p.agentID() + "Booked " + p.seats() + "seats.");
				}
				batch.clear();
			}


		}

	}

}