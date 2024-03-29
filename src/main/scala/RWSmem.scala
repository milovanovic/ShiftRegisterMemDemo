package chisel_version_test

import chisel3._
import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

class RWSmem extends Module {
  val width: Int = 32
  val io = IO(new Bundle {
    val enable = Input(Bool())
    val write = Input(Bool())
    val addr = Input(UInt(10.W))
    val dataIn = Input(UInt(width.W))
    val dataOut = Output(UInt(width.W))
  })

  val mem = SyncReadMem(1024, UInt(width.W))
  io.dataOut := DontCare
  when(io.enable) {
    val rdwrPort = mem(io.addr)
    when (io.write) { rdwrPort := io.dataIn }
      .otherwise    { io.dataOut := rdwrPort }
  }
}

object RWSMemApp extends App
{
  /*val arguments = Array(
      "--target-dir", "verilog/ShiftRegisterMem",
      "-X", "verilog",
      "--repl-seq-mem", "-c:ShiftRegisterMemExample:-o:mem.conf",
      "--log-level", "info"
    )
  // generate black boxes for memories
  (new ChiselStage).execute(arguments, Seq(ChiselGeneratorAnnotation(() => new ShiftRegisterMemExample(UInt(16.W), 1024))))*/
//}

 (new ChiselStage).execute(Array("--target-dir", "verilog/RWSmem"), Seq(ChiselGeneratorAnnotation(() => new RWSmem())))
}
