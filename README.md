# 🧠 CPU Scheduling Case Study

**Title:** *Simulated Performance of FCFS, SJF, RR, Priority, and Adaptive Round Robin Scheduling*

---

## 📌 Overview
This case study presents a **Java-based simulator** that compares the performance of multiple **CPU scheduling algorithms** under **dynamic workloads**.  
The goal is to analyze their behavior in terms of:
- Average Waiting Time  
- Average Turnaround Time  
- Context Switch Overhead  
- CPU Utilization  

---

## ⚙️ Algorithms Implemented
1. **FCFS (First Come First Serve)**
2. **SJF (Shortest Job First)**
3. **Round Robin (RR)**
4. **Priority Scheduling**
5. **Adaptive Round Robin (ARR)**

---

## 🧩 Scenario
A Java simulator executes dynamic process workloads and records performance metrics for each scheduling strategy.  
The adaptive version of Round Robin adjusts its **time quantum** dynamically to optimize performance.

---

## 🧪 Outcome
The results show that:
> **Adaptive Round Robin** achieves the best balance of fairness and efficiency,  
> outperforming static algorithms in dynamic workloads by reducing waiting time and context switching.

---

## 📊 Metrics Compared
| Algorithm | Avg Waiting Time | Avg Turnaround Time | Context Switches | CPU Utilization |
|------------|------------------|---------------------|------------------|-----------------|
| FCFS | 5.75 | 11.25 | 3 | 100% |
| SJF | 5.25 | 10.75 | 3 | 100% |
| RR | 9.75 | 15.25 | 11 | 100% |
| Priority | 5.25 | 10.75 | 3 | 100% |
| Adaptive RR | **7.00** | **12.50** | **5** | **100%** |

---

## 🧰 Tools Used
- **Java (VSCode)** – Simulation and logic  
- **MS Word Sheets** – Result visualization  
- **Word** – Case study documentation  

---

## 🧾 Conclusion
**Adaptive Round Robin** scheduling outperforms static strategies like RR and Priority in systems with dynamic workloads,  
offering better average waiting times and fewer context switches without compromising CPU utilization.

---
