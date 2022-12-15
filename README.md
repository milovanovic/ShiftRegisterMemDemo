Shift Register with and without SyncReadMem object
=======================================================
This repository contains `ShiftRegisterExample` module accompanied with an appropriate test cases that validate [ShiftRegisterMem](https://github.com/ucb-bar/dsptools/blob/master/rocket/src/main/scala/craft/ShiftRegisterMem.scala) object.

## Prerequisites

The following software packages should be installed prior to running this project:
* [sbt](http://www.scala-sbt.org)
* [Verilator](http://www.veripool.org/wiki/verilator) - only if you want to run tests

## Generate verilog
To generate verilog code for  all instances of interest, run `make` in base directory. All verilog files are generated inside `generated-rtl` directory.
 * `generated-rtl/mem` - design with `SyncReadMem`:
	* `/sram` - generated design (verilog code) contains blackboxes which should be replaced with SRAM
	* `/reg` -  generated design uses SyncReadMem object but those memories are not replaced with blackboxes
 - `generated-rtl/reg` - generated design uses simple [shift register](https://www.chisel-lang.org/api/latest/chisel3/util/ShiftRegister$.html) available in chisel library



