Demonstration of output differences between two different chipyard relases for ShiftRegisterMem
===================

This repository contains `ShiftRegisterMemExample` module accompanied with an appropriate test cases that validate [ShiftRegisterMem](https://github.com/ucb-bar/dsptools/blob/master/rocket/src/main/scala/craft/ShiftRegisterMem.scala) object.

This design is tested with two different backends (`treadle` and `verilator`).

Waveform diagrams for the given example are generated and presented below. - to be changed
![treadle backend](./doc/images/treadle.png)
Waveform diagram generated for `treadle` backend
![verilator backend](./doc/images/verilator.png)
Waveform diagram generated for `verilator` backend


This issue leads to incorrect simulation behaviour of designs where `ShiftRegisterMem` object is extensively used.

If `ShiftRegisterMem` is replaced with simple `ShiftRegister` object from `chisel3.util` library all tests pass without errors for both mentioned backends (no differences between the two are observed).
