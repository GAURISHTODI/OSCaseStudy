import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class SchedulingSimulator extends JFrame {
    private JTextArea inputArea;
    private JTextField quantumField;
    private JComboBox<String> algoCombo;
    private JButton runBtn, runAllBtn;
    private GanttPanel ganttPanel;
    private JTextArea outputArea;

    public SchedulingSimulator() {
        setTitle("CPU Scheduling Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left panel - Input and Controls
        JPanel leftPanel = createLeftPanel();
        
        // Right panel - Output and Gantt Chart
        JPanel rightPanel = createRightPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.35);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
        
        // Event listeners
        runBtn.addActionListener(e -> runSelected());
        runAllBtn.addActionListener(e -> runAll());
        quantumField.addActionListener(e -> runSelected());
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(new TitledBorder("Input Configuration"));

        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(new TitledBorder("Process Input"));
        
        inputArea = new JTextArea(12, 20);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputArea.setText(
            "// Format: pid arrival burst priority\n" +
            "P1 0 4 3\n" +
            "P2 1 5 1\n" +
            "P3 2 2 4\n" +
            "P4 3 3 2\n"
        );
        
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setPreferredSize(new Dimension(300, 200));
        inputPanel.add(inputScroll, BorderLayout.CENTER);

        // Controls panel
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBorder(new TitledBorder("Algorithm Settings"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Algorithm selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        controlsPanel.add(new JLabel("Algorithm:"), gbc);
        
        gbc.gridy = 1;
        String[] algos = {"FCFS", "SJF (non-preemptive)", "Round Robin (preemptive)", 
                         "Priority (non-preemptive)", "Adaptive Round Robin"};
        algoCombo = new JComboBox<>(algos);
        controlsPanel.add(algoCombo, gbc);

        // Quantum input
        gbc.gridy = 2;
        controlsPanel.add(new JLabel("Time Quantum:"), gbc);
        
        gbc.gridy = 3;
        quantumField = new JTextField("2");
        controlsPanel.add(quantumField, gbc);

        // Buttons
        gbc.gridy = 4;
        runBtn = new JButton("Run Algorithm");
        runBtn.setBackground(new Color(70, 130, 180));
        runBtn.setForeground(Color.WHITE);
        controlsPanel.add(runBtn, gbc);

        gbc.gridy = 5;
        runAllBtn = new JButton("Compare All Algorithms");
        runAllBtn.setBackground(new Color(34, 139, 34));
        runAllBtn.setForeground(Color.WHITE);
        controlsPanel.add(runAllBtn, gbc);

        // Add components to left panel
        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(controlsPanel, BorderLayout.CENTER);
        
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));

        // Gantt Chart panel
        JPanel ganttContainer = new JPanel(new BorderLayout());
        ganttContainer.setBorder(new TitledBorder("Gantt Chart Visualization"));
        ganttContainer.setPreferredSize(new Dimension(700, 200));
        
        ganttPanel = new GanttPanel();
        ganttPanel.setPreferredSize(new Dimension(700, 180));
        ganttContainer.add(ganttPanel, BorderLayout.CENTER);

        // Output area
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("Results"));
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setPreferredSize(new Dimension(700, 300));
        outputPanel.add(outputScroll, BorderLayout.CENTER);

        rightPanel.add(ganttContainer, BorderLayout.NORTH);
        rightPanel.add(outputPanel, BorderLayout.CENTER);
        
        return rightPanel;
    }

    // All the remaining methods stay exactly the same...
    private List<Process> parseInput() {
        List<Process> list = new ArrayList<>();
        String text = inputArea.getText();
        String[] lines = text.split("\\r?\\n");
        int autoId = 1;
        for (String ln : lines) {
            ln = ln.trim();
            if (ln.isEmpty() || ln.startsWith("//")) continue;
            String[] tok = ln.split("\\s+|,");
            if (tok.length < 3) continue;
            try {
                String pid = tok[0];
                int arrival = Integer.parseInt(tok[1]);
                int burst = Integer.parseInt(tok[2]);
                int priority = tok.length >= 4 ? Integer.parseInt(tok[3]) : 1;
                list.add(new Process(pid, arrival, burst, priority));
            } catch (Exception ex) {}
        }
        return list;
    }

    private void runSelected() {
        List<Process> processes = parseInput();
        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter at least one valid process line.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String alg = (String) algoCombo.getSelectedItem();
        if (alg == null) return;
        int quantum = 2;
        try { quantum = Integer.parseInt(quantumField.getText().trim()); if (quantum <= 0) quantum = 2; } catch (Exception ignored) {}

        ScheduleResult res = null;
        switch (alg) {
            case "FCFS":
                res = Scheduler.fcfs(copyProcesses(processes));
                break;
            case "SJF (non-preemptive)":
                res = Scheduler.sjfNonPreemptive(copyProcesses(processes));
                break;
            case "Round Robin (preemptive)":
                res = Scheduler.roundRobin(copyProcesses(processes), quantum);
                break;
            case "Priority (non-preemptive)":
                res = Scheduler.priorityNonPreemptive(copyProcesses(processes));
                break;
            case "Adaptive Round Robin":
                res = Scheduler.adaptiveRoundRobin(copyProcesses(processes));
                break;
        }

        showResult(res, alg);
    }

    private void runAll() {
        List<Process> processes = parseInput();
        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter at least one valid process line.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        int quantum = 2;
        try { quantum = Integer.parseInt(quantumField.getText().trim()); if (quantum <= 0) quantum = 2; } catch (Exception ignored) {}

        ScheduleResult r1 = Scheduler.fcfs(copyProcesses(processes));
        ScheduleResult r2 = Scheduler.sjfNonPreemptive(copyProcesses(processes));
        ScheduleResult r3 = Scheduler.roundRobin(copyProcesses(processes), quantum);
        ScheduleResult r4 = Scheduler.priorityNonPreemptive(copyProcesses(processes));
        ScheduleResult r5 = Scheduler.adaptiveRoundRobin(copyProcesses(processes));

        sb.append(String.format("%-35s %-10s %-10s %-10s\n", "Algorithm", "AvgWT", "AvgTAT", "ContextSwitches"));
        sb.append("---------------------------------------------------------------\n");
        sb.append(String.format("%-35s %-10.2f %-10.2f %-10d\n", "FCFS", r1.avgWaitingTime(), r1.avgTurnaroundTime(), r1.contextSwitches));
        sb.append(String.format("%-35s %-10.2f %-10.2f %-10d\n", "SJF (non-preemptive)", r2.avgWaitingTime(), r2.avgTurnaroundTime(), r2.contextSwitches));
        sb.append(String.format("%-35s %-10.2f %-10.2f %-10d\n", "Round Robin (quantum="+quantum+")", r3.avgWaitingTime(), r3.avgTurnaroundTime(), r3.contextSwitches));
        sb.append(String.format("%-35s %-10.2f %-10.2f %-10d\n", "Priority (non-preemptive)", r4.avgWaitingTime(), r4.avgTurnaroundTime(), r4.contextSwitches));
        sb.append(String.format("%-35s %-10.2f %-10.2f %-10d\n", "Adaptive Round Robin", r5.avgWaitingTime(), r5.avgTurnaroundTime(), r5.contextSwitches));

        outputArea.setText(sb.toString());
        ganttPanel.setSchedule(r5.segments, "Comparison Overview (Gantt for Adaptive RR shown)");
    }

    private void showResult(ScheduleResult res, String algName) {
        if (res == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("Algorithm: ").append(algName).append("\n\n");
        sb.append(String.format("%-6s %-8s %-8s %-8s\n", "PID","Arrival","Burst","Priority"));
        sb.append("--------------------------------\n");
        for (Process p : res.processes) {
            sb.append(String.format("%-6s %-8d %-8d %-8d\n", p.pid, p.arrivalTime, p.burstTime, p.priority));
        }

        sb.append("\nMetrics:\n");
        sb.append(String.format("Average Waiting Time   : %.2f\n", res.avgWaitingTime()));
        sb.append(String.format("Average Turnaround Time: %.2f\n", res.avgTurnaroundTime()));
        sb.append(String.format("CPU Utilization        : %.2f%%\n", res.cpuUtilization()*100.0));
        sb.append(String.format("Context Switches       : %d\n", res.contextSwitches));
        
        if (algName.equals("Adaptive Round Robin")) {
            sb.append(String.format("Adaptive Time Quantum  : %d\n", res.adaptiveQuantum));
        }
        
        sb.append("\nGantt Chart shown above.");
        outputArea.setText(sb.toString());
        ganttPanel.setSchedule(res.segments, algName + " (Gantt)");
    }

    private List<Process> copyProcesses(List<Process> src) {
        List<Process> c = new ArrayList<>();
        for (Process p : src) c.add(new Process(p.pid, p.arrivalTime, p.burstTime, p.priority));
        return c;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SchedulingSimulator app = new SchedulingSimulator();
            app.setVisible(true);
        });
    }

    // All inner classes remain exactly the same...
    static class Process {
        String pid;
        int arrivalTime;
        int burstTime;
        int remaining;
        int priority;
        int startTime = -1;
        int completionTime = -1;
        int waitingTime = 0;
        int turnaroundTime = 0;

        public Process(String pid, int arrivalTime, int burstTime, int priority) {
            this.pid = pid;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.remaining = burstTime;
            this.priority = priority;
        }
    }

    static class Segment {
        String pid;
        int start;
        int end;

        public Segment(String pid, int start, int end) {
            this.pid = pid; this.start = start; this.end = end;
        }
    }

    static class ScheduleResult {
        List<Process> processes;
        List<Segment> segments;
        int contextSwitches;
        int totalIdleTime;
        int adaptiveQuantum; // For Adaptive RR

        public ScheduleResult(List<Process> processes, List<Segment> segments, int contextSwitches, int totalIdleTime) {
            this.processes = processes;
            this.segments = segments;
            this.contextSwitches = contextSwitches;
            this.totalIdleTime = totalIdleTime;
            this.adaptiveQuantum = 0;
        }
        
        public ScheduleResult(List<Process> processes, List<Segment> segments, int contextSwitches, int totalIdleTime, int adaptiveQuantum) {
            this.processes = processes;
            this.segments = segments;
            this.contextSwitches = contextSwitches;
            this.totalIdleTime = totalIdleTime;
            this.adaptiveQuantum = adaptiveQuantum;
        }

        double avgWaitingTime() {
            double s=0;
            for (Process p : processes) s += p.waitingTime;
            return s / processes.size();
        }

        double avgTurnaroundTime() {
            double s=0;
            for (Process p : processes) s += p.turnaroundTime;
            return s / processes.size();
        }

        double cpuUtilization() {
            int totalBurst = 0;
            int finish = 0;
            for (Process p : processes) {
                totalBurst += p.burstTime;
                finish = Math.max(finish, p.completionTime);
            }
            int totalTime = finish;
            if (totalTime == 0) return 0.0;
            return (double) totalBurst / totalTime;
        }
    }

    static class Scheduler {
        static ScheduleResult fcfs(List<Process> procs) {
            procs.sort(Comparator.comparingInt(p -> p.arrivalTime));
            int time = 0;
            List<Segment> segments = new ArrayList<>();
            int ctx = 0;
            int prevSegPid = -1;
            int totalIdle = 0;

            for (Process p : procs) {
                if (time < p.arrivalTime) {
                    segments.add(new Segment("IDLE", time, p.arrivalTime));
                    totalIdle += (p.arrivalTime - time);
                    time = p.arrivalTime;
                }
                p.startTime = time;
                segments.add(new Segment(p.pid, time, time + p.burstTime));
                if (prevSegPid != -1 && !segments.isEmpty()) ctx++;
                prevSegPid = 1;
                time += p.burstTime;
                p.completionTime = time;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
            }
            return new ScheduleResult(procs, mergeSegments(segments), Math.max(0, ctx-0), totalIdle);
        }

        static ScheduleResult sjfNonPreemptive(List<Process> procs) {
            List<Process> list = new ArrayList<>(procs);
            list.sort(Comparator.comparingInt(p -> p.arrivalTime));
            List<Segment> segments = new ArrayList<>();
            int time = 0;
            int completed = 0;
            int n = list.size();
            boolean[] done = new boolean[n];
            int ctx = 0;

            while (completed < n) {
                int idx = -1;
                int minBurst = Integer.MAX_VALUE;
                for (int i=0;i<n;i++) {
                    Process p = list.get(i);
                    if (!done[i] && p.arrivalTime <= time) {
                        if (p.burstTime < minBurst) {
                            minBurst = p.burstTime;
                            idx = i;
                        }
                    }
                }
                if (idx == -1) {
                    time++;
                    continue;
                }
                Process p = list.get(idx);
                p.startTime = time;
                segments.add(new Segment(p.pid, time, time + p.burstTime));
                if (completed > 0) ctx++;
                time += p.burstTime;
                p.completionTime = time;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
                done[idx] = true;
                completed++;
            }
            return new ScheduleResult(list, mergeSegments(segments), Math.max(0, ctx-0), 0);
        }

        static ScheduleResult roundRobin(List<Process> procs, int quantum) {
            List<Process> list = new ArrayList<>(procs);
            list.sort(Comparator.comparingInt(p -> p.arrivalTime));
            Queue<Process> q = new LinkedList<>();
            int time = 0;
            int idx = 0;
            List<Segment> segments = new ArrayList<>();
            int ctx = 0;
            Process last = null;

            while (true) {
                while (idx < list.size() && list.get(idx).arrivalTime <= time) {
                    q.offer(list.get(idx));
                    idx++;
                }
                if (q.isEmpty()) {
                    if (idx < list.size()) {
                        int nextArrival = list.get(idx).arrivalTime;
                        segments.add(new Segment("IDLE", time, nextArrival));
                        time = nextArrival;
                        continue;
                    } else {
                        break;
                    }
                }
                Process p = q.poll();
                if (p.startTime == -1) p.startTime = time;
                int exec = Math.min(quantum, p.remaining);
                segments.add(new Segment(p.pid, time, time + exec));
                if (last != null && !last.pid.equals(p.pid)) ctx++;
                time += exec;
                p.remaining -= exec;

                while (idx < list.size() && list.get(idx).arrivalTime <= time) {
                    q.offer(list.get(idx));
                    idx++;
                }

                if (p.remaining > 0) {
                    q.offer(p);
                } else {
                    p.completionTime = time;
                    p.turnaroundTime = p.completionTime - p.arrivalTime;
                    p.waitingTime = p.turnaroundTime - p.burstTime;
                }
                last = p;
            }

            for (Process p : list) {
                if (p.startTime == -1) p.startTime = p.arrivalTime;
                if (p.completionTime == -1) p.completionTime = p.startTime + p.burstTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
            }

            return new ScheduleResult(list, mergeSegments(segments), Math.max(0, ctx-0), 0);
        }

        static ScheduleResult priorityNonPreemptive(List<Process> procs) {
            List<Process> list = new ArrayList<>(procs);
            list.sort(Comparator.comparingInt(p -> p.arrivalTime));
            int time = 0;
            int n = list.size();
            boolean[] done = new boolean[n];
            int completed = 0;
            List<Segment> segments = new ArrayList<>();
            int ctx = 0;

            while (completed < n) {
                int idx = -1;
                int best = Integer.MAX_VALUE;
                for (int i=0;i<n;i++) {
                    Process p = list.get(i);
                    if (!done[i] && p.arrivalTime <= time) {
                        if (p.priority < best) {
                            best = p.priority;
                            idx = i;
                        }
                    }
                }
                if (idx == -1) {
                    time++;
                    continue;
                }
                Process p = list.get(idx);
                p.startTime = time;
                segments.add(new Segment(p.pid, time, time + p.burstTime));
                if (completed > 0) ctx++;
                time += p.burstTime;
                p.completionTime = time;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
                done[idx] = true;
                completed++;
            }
            return new ScheduleResult(list, mergeSegments(segments), Math.max(0, ctx-0), 0);
        }

        static ScheduleResult adaptiveRoundRobin(List<Process> procs) {
            List<Process> list = new ArrayList<>(procs);
            list.sort(Comparator.comparingInt(p -> p.arrivalTime));
            
            List<Integer> burstTimes = new ArrayList<>();
            for (Process p : list) {
                burstTimes.add(p.burstTime);
            }
            Collections.sort(burstTimes);
            
            int quantum;
            int n = burstTimes.size();
            if (n % 2 == 1) {
                quantum = burstTimes.get(n / 2);
            } else {
                quantum = (burstTimes.get(n / 2 - 1) + burstTimes.get(n / 2)) / 2;
            }
            
            if (quantum < 1) quantum = 1;
            
            Queue<Process> q = new LinkedList<>();
            int time = 0;
            int idx = 0;
            List<Segment> segments = new ArrayList<>();
            int ctx = 0;
            Process last = null;

            while (true) {
                while (idx < list.size() && list.get(idx).arrivalTime <= time) {
                    q.offer(list.get(idx));
                    idx++;
                }
                if (q.isEmpty()) {
                    if (idx < list.size()) {
                        int nextArrival = list.get(idx).arrivalTime;
                        segments.add(new Segment("IDLE", time, nextArrival));
                        time = nextArrival;
                        continue;
                    } else {
                        break;
                    }
                }
                Process p = q.poll();
                if (p.startTime == -1) p.startTime = time;
                int exec = Math.min(quantum, p.remaining);
                segments.add(new Segment(p.pid, time, time + exec));
                if (last != null && !last.pid.equals(p.pid)) ctx++;
                time += exec;
                p.remaining -= exec;

                while (idx < list.size() && list.get(idx).arrivalTime <= time) {
                    q.offer(list.get(idx));
                    idx++;
                }

                if (p.remaining > 0) {
                    q.offer(p);
                } else {
                    p.completionTime = time;
                    p.turnaroundTime = p.completionTime - p.arrivalTime;
                    p.waitingTime = p.turnaroundTime - p.burstTime;
                }
                last = p;
            }

            for (Process p : list) {
                if (p.startTime == -1) p.startTime = p.arrivalTime;
                if (p.completionTime == -1) p.completionTime = p.startTime + p.burstTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
            }

            return new ScheduleResult(list, mergeSegments(segments), Math.max(0, ctx-0), 0, quantum);
        }

        static List<Segment> mergeSegments(List<Segment> segs) {
            if (segs.isEmpty()) return segs;
            List<Segment> out = new ArrayList<>();
            Segment cur = segs.get(0);
            for (int i=1;i<segs.size();i++) {
                Segment s = segs.get(i);
                if (s.pid.equals(cur.pid) && s.start == cur.end) {
                    cur = new Segment(cur.pid, cur.start, s.end);
                } else {
                    out.add(cur);
                    cur = s;
                }
            }
            out.add(cur);
            return out;
        }
    }

    static class GanttPanel extends JPanel {
        private List<Segment> segments = new ArrayList<>();
        private String title = "";

        public GanttPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }

        public void setSchedule(List<Segment> segs, String title) {
            this.segments = segs == null ? new ArrayList<>() : segs;
            this.title = title;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (segments == null || segments.isEmpty()) {
                g.setColor(Color.BLACK);
                g.drawString("No schedule to display. Run an algorithm.", 10, 20);
                return;
            }

            int margin = 50;
            int w = getWidth() - margin*2;
            int h = getHeight() - 80;
            int y = 40;
            int ganttHeight = Math.max(30, h - 40);

            int start = Integer.MAX_VALUE, end = Integer.MIN_VALUE;
            for (Segment s : segments) {
                start = Math.min(start, s.start);
                end = Math.max(end, s.end);
            }

            int span = Math.max(1, end - start);
            double pxPerUnit = (double) w / span;

            g.setColor(Color.BLACK);
            g.drawString(title, margin, 15);

            int x0 = margin;
            Map<String, Color> colorMap = new HashMap<>();
            Random r = new Random(0);

            for (Segment s : segments) {
                int segX = x0 + (int) Math.round((s.start - start) * pxPerUnit);
                int segW = Math.max(2, (int) Math.round((s.end - s.start) * pxPerUnit));
                Color c = colorMap.computeIfAbsent(s.pid, k -> new Color(100 + r.nextInt(155), 100 + r.nextInt(155), 100 + r.nextInt(155)));
                g.setColor(c);
                g.fillRect(segX, y, segW, ganttHeight);
                g.setColor(Color.BLACK);
                g.drawRect(segX, y, segW, ganttHeight);

                FontMetrics fm = g.getFontMetrics();
                String label = s.pid;
                int strW = fm.stringWidth(label);
                int strX = segX + Math.max(2, (segW - strW)/2);
                int strY = y + ganttHeight/2 + fm.getAscent()/2 - 2;
                g.drawString(label, strX, strY);
                g.drawString(String.valueOf(s.start), segX - 2, y + ganttHeight + 15);
            }

            Segment last = segments.get(segments.size()-1);
            int lastX = x0 + (int) Math.round((last.end - start) * pxPerUnit);
            g.drawString(String.valueOf(last.end), lastX - 2, y + ganttHeight + 15);
        }
    }
}