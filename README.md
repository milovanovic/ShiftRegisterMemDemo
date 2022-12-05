
Demonstration of output differences between two different chisel versions for ShiftRegisterMem
===================

This repository contains `ShiftRegisterMemExample` module accompanied with an appropriate test cases that validate [ShiftRegisterMem](https://github.com/ucb-bar/dsptools/blob/master/rocket/src/main/scala/craft/ShiftRegisterMem.scala) object. Original `ShiftRegisterMem` object is extended with single port SRAM implementation. Code for dual port SRAM implementation is not changed.

This design is tested with two different backends (`treadle` and `verilator`) and waveform diagrams for the given example are generated and presented in this repository.

Waverform diagram of `ShiftRegisterMem` object as a result of tests execution with **chisel version 3.4.4** (verilator backend):
![verilator backend](./doc/verilator_test_shift_mem.png)

Waverform diagram of `ShiftRegisterMem` object as a result of tests execution with **chisel version 3.4.3** (verilator backend):
![verilator backend](./doc/verilator_test_shift_mem_ok.png)

First conclusions are that  [commit 18e607](https://github.com/chipsalliance/chisel3/commit/18e6077ff935e464850132263fab4c7a06bcb4df) is the reason for tests failure.

If `ShiftRegisterMem` is replaced with simple `ShiftRegister` object from `chisel3.util` library or when single port SRAM implementation is in use, all tests pass without errors for both mentioned backends. Diagram is presented below:

![verilator backend](./doc/verilator_shift_reg.png)

This issue leads to incorrect simulation behaviour of designs where `ShiftRegisterMem` object is extensively used.

## Setup

```
git clone --branch sp_sram_shift_register https://github.com/milovanovic/ShiftRegisterMemDemo.git
cd ShiftRegisterMemDemo

//to run tests
sbt test
```

The output should look like:

![report](./doc/report.png)

