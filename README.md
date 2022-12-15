Shift Register with and without SyncReadMem object
=======================================================
This repository contains `ShiftRegisterExample` module written in Chisel HDL.

## Prerequisites

The following software package should be installed prior to running this project:
* [sbt](http://www.scala-sbt.org)

## Generate verilog
To generate verilog code for  all instances of interest, run `make` in base directory. All verilog files are generated inside `generated-rtl` directory.
 * `generated-rtl/mem` - design with `SyncReadMem`:
	* `/sram` - generated design (verilog code) contains blackboxes which should be replaced with SRAM
	* `/reg` -  generated design uses SyncReadMem object but those memories are not replaced with blackboxes
 - `generated-rtl/reg` - generated design uses simple [shift register](https://www.chisel-lang.org/api/latest/chisel3/util/ShiftRegister$.html) available in chisel library



