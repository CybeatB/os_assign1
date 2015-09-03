
/**
 *
 * @author Josh Schwarz (a1645899), Jonathan Blieschke (a1646288)
 */
import java.util.*;
import java.io.*;

public class SchedulerDriver {

    private static int firstSeats = 10;
    private static int buisSeats = 20;
    private static int econSeats = 70;
    private final static PriorityQueue<Process> overFull = new PriorityQueue<>();
    private static int overFullSize = -1;

    //The Process class will sort each request in the given order
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

    //Prints out the information in each batch in order, as per spec
    private static void printBatch(ArrayList<Process> batch) {

        String temp = "";
        int tempCount = batch.size();
        int count = batch.size();
        int semaphore = 1;

        //Count seats +/- from total avaliable. Any that fall outside bounds will be added to the overflow queue.
        for (Process p : batch) {
            if (p.action().equals("R")) {
                switch (p.type()) {
                    case "F":
                        if (firstSeats - p.seats() >= 0) {
                            firstSeats = firstSeats - p.seats();
                        } else {
                            overFull.add(p);
                        }
                        break;
                    case "B":
                        if (buisSeats - p.seats() >= 0) {
                            buisSeats = buisSeats - p.seats();
                        } else {
                            overFull.add(p);
                        }
                        break;
                    case "E":
                        if (econSeats - p.seats() >= 0) {
                            econSeats = econSeats - p.seats();
                        } else {
                            overFull.add(p);
                        }
                        break;
                }
            } else {
                switch (p.type()) {
                    case "F":
                        firstSeats = firstSeats + p.seats();
                        break;
                    case "B":
                        buisSeats = buisSeats + p.seats();
                        break;
                    case "E":
                        econSeats = econSeats + p.seats();
                        break;
                }
            }
        }

        //Remove the un-processable requests from the origional batch
        for (Process p : overFull) {
            batch.remove(p);
            tempCount--;
        }

        //ADMIT A BATCH
        for (Process p : batch) {
            temp = temp + p.agentID();
            if (tempCount > 1) {
                temp = temp + ", ";
            }
            tempCount--;
        }

        if (!batch.isEmpty()) {
            System.out.println("Admit a batch (" + temp + ")");
        }

        //AGENT * WAITS & SEMAPHORE
        for (Process p : batch) {
            semaphore--;
            System.out.println(p.agentID() + " executes wait operation, semaphore = " + semaphore);
        }

        //AGENT ACTIVITY
        for (Process p : batch) {

            //CANCELATION
            if (p.action().equals("C")) {
                overFullSize = 0;
                System.out.println(p.agentID() + " enters the system");
                String resSeat = (p.agentID() + " cancels " + p.seats());
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
            } else {
                //RESERVATION
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

        PriorityQueue<Process> cancel = new PriorityQueue<>();
        PriorityQueue<Process> res = new PriorityQueue<>();
        HashMap<String, Integer> map = new HashMap<>();
        ArrayList<Process> batch = new ArrayList<>();
        FileInputStream fstream = null;
        File file = new File(args[0]);
        String strLine;

        //Parse File
        try {
            fstream = new FileInputStream(file);
        } catch (IOException e) {
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        //Create objects and push on appropriate queue
        try {
            while ((strLine = br.readLine()) != null) {
                //AGENT NEEDS SPACE BEFORE DIGIT
                String[] splitString = strLine.split(" ");
                String tempNum = splitString[0];
                int space = tempNum.length();
                tempNum = tempNum.substring(0, space - 1) + " " + tempNum.substring(space - 1, tempNum.length());

                String agentNum = tempNum;
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

        //Process requests until we run out, or cant process any more
        while (res.size() >= 0 && cancel.size() >= 0) {

            //If every queue is empty, we processed everything correctly.
            if (res.isEmpty() && cancel.isEmpty() && overFull.isEmpty()) {
                break;
            }

            int prev_time = -1;
            String agent = null;

            if (cancel.peek() != null) {
                agent = cancel.peek().agentID();
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
                    printBatch(batch);
                    batch.clear();
                }

            } else {
                //Reservation
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
                batch.stream().forEach((p) -> {
                    map.put(p.agentID(), p.time());
                });
                printBatch(batch);
                batch.clear();
            }

            //Put un-processable requests back into the main process queue
            if (!overFull.isEmpty()) {
                if (overFullSize == 0) {
                    overFull.stream().forEach((p) -> {
                        res.add(p);
                    });
                    overFull.clear();
                    overFullSize++;
                }
            }

            //Still couldnt be processed after multiple attempts
            if (overFullSize == 1) {
                for (Process p : overFull) {
                    System.out.println("Cannot admit a batch");
                    System.out.println(p.agentID() + " cannot reserve any seats in this system");
                }
                overFull.clear();
            }
        }
    }
}
