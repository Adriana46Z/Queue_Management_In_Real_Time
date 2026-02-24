# Queue_Management_In_Real_Time

## Description

A Java-based queue management system that simulates and optimizes customer flow in real-time service environments. The application implements two distinct scheduling strategies to efficiently distribute customers across multiple service queues.

The first strategy uses a shortest queue approach, assigning incoming customers to the queue with the fewest clients currently waiting. The second strategy employs a time-based optimization method, directing customers to the queue with the shortest total processing time based on all existing clients and their respective service requirements.

The system features a multi-threaded architecture where each queue operates as an independent thread, processing customers concurrently while maintaining thread-safe operations. The application provides real-time visualization of queue states, tracking metrics such as average waiting time and average service time.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Scheduling Strategies](#scheduling-strategies)
- [License](#license)

---

## Features

- **Two Scheduling Strategies** — Shortest queue and shortest time strategies for customer distribution.
- **Multi-threaded Architecture** — Each queue runs as an independent thread, enabling concurrent processing.
- **Thread-safe Operations** — Safe handling of shared resources across concurrent queue threads.
- **Real-time Visualization** — Live display of queue states as the simulation progresses.
- **Performance Metrics** — Tracks and reports average waiting time and average service time.
- **Log Output** — Simulation results are written to a log file for review.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Concurrency | Java Threads |
| Build / IDE | IntelliJ IDEA |

---

## Getting Started

### Prerequisites

- [Java JDK](https://www.oracle.com/java/technologies/downloads/) (v11 or later recommended)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) or any Java IDE

### Installation

1. Clone the repository

   ```bash
   git clone https://github.com/Adriana46Z/Queue_Management_In_Real_Time.git
   cd Queue_Management_In_Real_Time
   ```

2. Open the project in IntelliJ IDEA

   - File > Open > select the `Queue_Management_In_Real_Time` folder
   - IntelliJ will detect the `.iml` file and configure the project automatically

3. Run the application

   - Locate the main class inside `src/`
   - Right-click > Run, or use the Run button in IntelliJ

4. Check the output

   - Real-time queue states are displayed in the console
   - A `log1.txt` file is generated with the full simulation log

---

## Project Structure

```
Queue_Management_In_Real_Time/
├── src/                  
│   ├── model/           
│   ├── logic/            
│   └── gui/             
├── .idea/                
├── Assignment2.iml       
├── log1.txt              
└── README.md
```

---

## Scheduling Strategies

### Shortest Queue Strategy
Assigns each incoming customer to the queue that currently has the fewest clients waiting. This approach balances queue lengths across all available servers.

### Shortest Time Strategy
Assigns each incoming customer to the queue with the lowest total estimated processing time, calculated based on all existing clients and their individual service durations. This approach minimizes overall waiting time more precisely than simple queue length comparison.
