package chisel_version_test

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

// Extend module with IO
abstract trait HasIO extends Module {
  val io: Bundle
}

class ShiftRegisterIO[T <: Data](gen: T, n: Int) extends Bundle  {
  require (n >= 0, "Shift register must have non-negative shift")
  val in = Input(gen.cloneType)
  val out = Output(gen.cloneType)
  
  val en = Input(Bool())
  val valid_out = Output(Bool())
}

class ShiftRegisterMemExample[T <: Data](gen: T, n: Int, isMem: Boolean = true) extends Module with HasIO {
  val io = IO(new ShiftRegisterIO(gen, n))
  val logn = log2Ceil(n)
  val cnt = RegInit(0.U(logn.W))

  val shiftMem = if (isMem) ShiftRegisterMem(io.in, n, io.en, use_sp_mem = true, name = "simple_shift_register") else ShiftRegister(io.in, n, io.en)
  //val shiftMem = ShiftRegister(io.in, n, io.en)
  io.out := shiftMem
 
  when (io.en === true.B) {
    cnt := cnt +% 1.U
  }
  val initialInDone = RegInit(false.B)
  when (cnt === (n.U - 1.U)) {
    initialInDone := true.B
  }
  // or only initialInDone?
  io.valid_out := initialInDone && io.en
}

object ShiftRegisterMemApp extends App
{
    val arguments = Array(
      "--target-dir", "verilog/ShiftRegisterMem",
      "-X", "verilog",
      "--repl-seq-mem", "-c:ShiftRegisterMemExample:-o:mem.conf",
      "--log-level", "info"
    )
  // generate black boxes for memories
  (new ChiselStage).execute(arguments, Seq(ChiselGeneratorAnnotation(() => new ShiftRegisterMemExample(UInt(16.W), 1024))))
//}

 //(new ChiselStage).execute(Array("--target-dir", "verilog/ShiftRegisterMem"), Seq(ChiselGeneratorAnnotation(() => new ShiftRegisterMemExample(UInt(10.W), 1024))))
}
