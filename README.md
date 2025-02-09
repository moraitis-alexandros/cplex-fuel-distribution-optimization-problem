# CPLEX Fuel Distribution Optimization Problem

## Overview  
This project develops a **mathematical formulation** and provides solutions for optimizing fuel distribution from a **single depot to multiple storage tanks**, focusing on efficient **time scheduling** for **loading, unloading, and delivery**.

## Problem Context  
In military operations, **fuel distribution** plays a critical role in sustaining logistical operations. The refueling process involves **transporting fuel from a port to multiple demand points (storage tanks) using a heterogeneous fleet of military vehicles**. Due to **operational risks**, tankers must leave the port **as soon as possible** to minimize exposure to threats.

### 📌 Research Problem  
![Research Problem](images/research_problem.png)

## Objective  
The goal is to **minimize the total makespan** of the operation—specifically, reducing the **departure time of the last loaded vehicle** to enhance efficiency and operational security.

---

## Key Contributions  

- **Mathematical Formulation:**  
  - Developed a **Mixed Integer Linear Programming (MILP)** model to solve the problem.  
  - Introduced a new variation of the **Vehicle Scheduling Problem (VSP)**, incorporating queueing and multi-trip constraints.

- **Solution Approach:**  
  - 🛠 **MILP Implementation:** Implemented the optimization model in **Java using the CPLEX library**.  
  - ⚡ **Greedy Algorithm:** Developed a heuristic-based greedy algorithm for **solving larger instances faster**.

### ⚡ Greedy Algorithm Implementation  
![Greedy Algorithm](images/greedy_algorithm.png)

- **Comparative Analysis:**  
  - 📊 Conducted a **performance evaluation** between the MILP model and the greedy approach to assess efficiency.

---

## Practical Applications  

✅ Provides a **validated scheduling framework** to enhance **vehicle dispatch efficiency**.  
✅ Supports **decision-making in logistics and transportation planning**.  
✅ Can be extended to other **time-sensitive fuel distribution and supply chain problems**.

### 🏗️ Framework Implementation  
![Framework Implementation](images/framework.png)

---
