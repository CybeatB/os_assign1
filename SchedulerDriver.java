/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulerdriver;

/**
 *
 * @author Josh
 */
import java.util.*;
import java.io.*;

public class SchedulerDriver {

    private static class Process implements Comparable<Process> {

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
                return this._seats + proc.seats();
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

    public static void main(String args[]) {

        PriorityQueue<Process> cancel = new PriorityQueue<>();
        PriorityQueue<Process> res = new PriorityQueue<>();

        HashMap<String, Integer> map = new HashMap<>();

        ArrayList<Process> batch = new ArrayList<>();
        FileInputStream fstream = null;

        File file = new File(args[0]);

        //Parse File
        try {
            fstream = new FileInputStream(file);
        } catch (IOException e) {
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

        try {

            while ((strLine = br.readLine()) != null) {
                String[] splitString = strLine.split(" ");
                String agentNum = splitString[0];
                String act = splitString[1];
                String type = splitString[2];
                int seats = Integer.parseInt(splitString[3]);
                int time = Integer.parseInt(splitString[4]);

                if (act.equals("R")) {
                    res.add(new Process(time, act, type, seats, agentNum));
                } else {
                    cancel.add(new Process(time, act, type, seats, agentNum));
                }
            }

            br.close();

        } catch (IOException e) {

        }

        //TEST res && cancel contents
//        res.stream().forEach((p) -> {
//            System.out.println(p.type() + " " + p.agentID() + " " + p.seats() + " " + p.action());
//        });
//
//        cancel.stream().forEach((p) -> {
//            System.out.println(p.type() + " " + p.agentID() + " " + p.seats() + " " + p.action());
//        });

        //Output text
        while (res.size() > 0 && cancel.size() > 0) {

            int prev_time = -1;

            String agent = null;

            if (cancel.peek() != null) {
                agent = cancel.peek().agentID();
//                System.out.println("Agent " + agent);
            }

            if (map.get(agent) != null) {
                prev_time = map.get(agent);

                if (res.peek().time() == prev_time) {
                    //Reservation
                    //Check for batch
                    while (true) {
                        Process top = res.poll();
                        if (top != null) {
                            batch.add(top);
                            if (res.peek() != null) {
                                if (top.seats() != res.peek().seats()) {
                                    break;
                                } else if (!top.type().equals(res.peek().type())) {
                                    break;
                                } else if (!top.action().equals(res.peek().action())) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    //process the batch
                    for (Process p : batch) {
                        System.out.println("Agent " + p.agentID() + "Booked " + p.seats() + "seats.");
                        map.put(agent, p.time());
                        System.out.println("Putting agent " + agent + " at " + p.time());
                    }
                    batch.clear();
                } else {
                    //Cancellation
                    //Check for batch
                    while (true) {
                        Process top = cancel.poll();
                        if (top != null) {
                            batch.add(top);
                            if (cancel.peek() != null) {
                                if (top.seats() != cancel.peek().seats()) {
                                    break;
                                } else if (!top.type().equals(cancel.peek().type())) {
                                    break;
                                } else if (!top.action().equals(cancel.peek().action())) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    //process the batch
                    for (Process p : batch) {
                        System.out.println("Agent " + p.agentID() + "Cancelled " + p.seats() + "seats.");
                    }
                    batch.clear();
                }
                
            } else {
                //Reservation
                //Check for batch
                while (true) {
                    Process top = res.poll();
                    if (top != null) {
                        batch.add(top);
                        if (res.peek() != null) {
                            if (top.seats() != res.peek().seats()) {
                                break;
                            } else if (!top.type().equals(res.peek().type())) {
                                break;
                            } else if (!top.action().equals(res.peek().action())) {
                                break;
                            }
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }

                //process the batch
                int count = 0;
                for (Process p : batch) {
                    System.out.println("Agent " + p.agentID() + "Booked " + p.seats() + "seats. In loop " + count);
                    System.out.println("Putting agent " + agent + " at " + p.time());
                    map.put(agent, p.time());
                    count++;
                }
                batch.clear();
            }

        }

    }

}
