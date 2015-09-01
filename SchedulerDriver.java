/**
 *
 * @author Josh, Jonathan
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
        @Override
        public int compareTo(Process proc) {
//            System.out.println("this: " + this.toString());
//            System.out.println("proc: " + proc.toString());
//            System.out.println(" --- ");
            // this <(-1) =(0) >(+1) (lower value -> higher priority)
            if ((!this._action.equals(proc.action())) && (!this._id.equals(proc.agentID()))) {
                return this._action.equals("C") ? -1 : 1;
            }
            if (Integer.compare(this._time, proc.time()) != 0) {
                return this._time - proc.time();
            }
            if (!this._type.equals(proc.type())) {
                return this.typeCompAlt(this._type, proc.type());
                //return this.typeComp(this._type, proc.type());
            }
            if (Integer.compare(this._seats, proc.seats()) != 0) {
                return proc.seats() - this._seats;
            }
            return this._id.compareTo(proc.agentID());
        }

        @Override
        public String toString() {
            return this._id + " " + this._action + " " + this._type + " " + this._seats + " " + this._time;
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
        private final int _time;
        private final String _action;
        private final String _type;
        private final int _seats;
        private final String _id;

        // Helpers
    	  /* Returns:
         * <0 when a > b
         * =0 when a = b
         * >0 when a < b */
        private int typeCompAlt(String a, String b) {
            String cmp = a + b;
            switch (cmp) {
                case "FB":
                    return -1;
                case "FE":
                    return -1;
                case "BF":
                    return 1;
                case "EF":
                    return 1;
                case "BE":
                    return -1;
                case "EB":
                    return 1;
            }
            return 0;
        }

        private int typeComp(String a, String b) {
//            System.out.println("Comparing " + a + " to " + b);
            if (a.equals(b)) {
                return 0;
            }
            if (a.equals("F")) {
                return -1;
            }
            if (b.equals("F")) {
                return 1;
            }
            if (a.equals("B")) {
                return -1;
            }
            if (b.equals("B")) {
                return 1;
            }
            // Should Never Happen
            return 0;
        }
    }

    private static void printBatch(ArrayList<Process> batch) {

        String temp = "";
//       System.out.println("Admit a batch (" + temp + ")" );
        int tempCount = batch.size();
        int count = batch.size();
        int semaphore = 1;

        
        
        //ADMIT A BATCH
        for (Process p : batch) {
            temp = temp + p.agentID();
            if (tempCount > 1) {
                temp = temp + ", ";
            }
            tempCount--;
        }
        System.out.println("Admit a batch (" + temp + ")");

        //AGENT * WAITS
        for (Process p : batch) {
            semaphore--;
            System.out.println(p.agentID() + " executes wait operation, semaphore = " + semaphore);
        }

        //AGENT ACTIVITY
        for (Process p : batch) {

            if (p.action().equals("C")) {
                System.out.println("CANCEL");
                semaphore++;
                System.out.println(p.agentID() + " executes signal operation, semaphore = " + semaphore);
                System.out.println(p.agentID() + " exits the system");
            } else {
                System.out.println(p.agentID() + " enters the system");
                String resSeat = (p.agentID() + " reserves " + p.seats());
                if (p.seats() > 1) {
                    resSeat = resSeat + " seats in";
                } else {
                    resSeat = resSeat + " seat in";
                }

                switch (p.type()) {
                    case "F":
                        resSeat = resSeat + " First-class";
                        break;
                    case "B":
                        resSeat = resSeat + " Business-class";
                        break;
                    case "E":
                        resSeat = resSeat + " Economy-class";
                        break;
                }
                System.out.println(resSeat);

                semaphore++;
                System.out.println(p.agentID() + " executes signal operation, semaphore = " + semaphore);
                System.out.println(p.agentID() + " exits the system");
            }
        }

    }

    public static void main(String args[]) {

//        public static int firstSeats = 10;
//        public static int buisSeats = 20;
//        public static int econSeats = 70;

        PriorityQueue<Process> cancel = new PriorityQueue<>();
        PriorityQueue<Process> res = new PriorityQueue<>();
        PriorityQueue<Process> overFull = new PriorityQueue<>();

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
        while (res.size() >= 0 && cancel.size() >= 0) {

//                System.out.println("Current Reservation Queue");
//                System.out.println("-------------------------------------------------------");
//                res.stream().forEach((p) -> {
//                    System.out.println(p.toString());
//                });
//                cancel.stream().forEach((p) -> {
//                    System.out.println(p.toString());
//                });
//                System.out.println("-------------------------------------------------------");
            if (res.isEmpty() && cancel.isEmpty()) {
                break;
            }

            int prev_time = -1;

            String agent = null;

            if (cancel.peek() != null) {
                agent = cancel.peek().agentID();
//                System.out.println("Agent " + agent);
            }

            if (map.get(agent) != null) {
                prev_time = map.get(agent);

//                System.out.println();
//                System.out.println("Current Reservation Queue");
//                System.out.println("-------------------------------------------------------");
//                res.stream().forEach((p) -> {
//                    System.out.println(p.toString());
//                });
//                System.out.println("-------------------------------------------------------");
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
//                    for (Process p : batch) {
//                        System.out.println("BLOCK 1: Agent " + p.agentID() + "Booked " + p.seats() + "seats.");
//                        map.put(p.agentID(), p.time());
////                        System.out.println("Putting agent " + agent + " at " + p.time());
//                    }
                    batch.stream().forEach((p) -> {
                        map.put(p.agentID(), p.time());
                    });
                    printBatch(batch);
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
//                    for (Process p : batch) {
//                        System.out.println("Agent " + p.agentID() + "Cancelled " + p.seats() + "seats.");
//                    }
                    printBatch(batch);

                    batch.clear();
                }

            } else {
                //Reservation
                //Check for batch

//                System.out.println();
//                System.out.println("Current Reservation Queue");
//                System.out.println("-------------------------------------------------------");
//                res.stream().forEach((p) -> {
//                    System.out.println(p.toString());
//                });
//                System.out.println("-------------------------------------------------------");
                while (true) {

                    Process top = res.poll();
//                  System.out.println("Top = " + top.agentID());
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
//                int count = 0;
//                for (Process p : batch) {
//                    System.out.println("                        " + p.agentID() + " Booked " + p.seats() + "seats. In loop " + count);
//                    System.out.println("Putting agent " + agent + " at " + p.time());
//                    map.put(p.agentID(), p.time());
//                    count++;
//                }
                batch.stream().forEach((p) -> {
                    map.put(p.agentID(), p.time());
                });

                printBatch(batch);
                batch.clear();
            }

        }

    }

}
