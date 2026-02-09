# Java Data Structure & Concurrency Assessment

This repository contains solutions to two related technical tasks:

1. A **hierarchical data structure filter implementation**
2. A **high-concurrency cache review**

Both tasks focus on correctness, performance, and production-readiness rather than just passing tests.

---

## Task 1: Hierarchy Filter Method Implementation

### Overview

This task involves implementing a `filter()` method for a hierarchical data structure and to provide test cases.

The method returns a filtered hierarchy while preserving:
- Parent–child relationships
- Structural integrity
- Deterministic behavior

The implementation emphasizes:
- Correct traversal
- Clear assumptions
- Performance optimization

---

### Files

- `HierarchyFilter.java` 
- `HierarchyFilterTest.java`

---

### Implementation Details

1. **Clear, readable and optimized (O(n)) `filter()`implementation**
    - Focuses on correctness and maintainability
    - Suitable for most production codebases
    - Improves performance for large hierarchies

---

### Assumptions

Assumptions made during implementation include:
- The hierarchy is acyclic
- Input data is well-formed
- Filtering is deterministic and side effect free

All assumptions are explicitly documented in code comments.

---

### Testing Strategy

Tests are designed to reflect:

- Happy-path scenarios
- Empty input
- Single-node hierarchies
- Filtering with no matches
- Filtering where only parents or only children match

Tests are named using a **Given / When / Then** style for clarity.

---

## How to Run

1. Ensure Java and Maven/Gradle are installed
2. Run tests using your build tool, for example:

```
mvn test 
```
---
## Task 2: SimpleCache – High Concurrency Review

### Overview

This task reviews a provided `SimpleCache` implementation intended for use in a **high-concurrency production environment**.

Expected load characteristics:
- Thousands of reads per second
- Hundreds of writes per second
- Tens of concurrent threads

The goal is to identify production issues, explain why they occur, and describe their potential impact under real-world conditions.

---

### Files
- `SimpleCache.md`

---

### Review Focus Areas

- Thread safety
- Read/write contention
- Locking strategy
- Time complexity under load
- Memory visibility and consistency
- Production failure scenarios