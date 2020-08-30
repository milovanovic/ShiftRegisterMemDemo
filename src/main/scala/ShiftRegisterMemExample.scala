package backendTest

import chisel3._
import chisel3.util._

class ShiftRegisterIO[T <: Data](gen: T, n: Int) extends Bundle {
  require (n >= 0, "Shift register must have non-negative shift")
  val in = Input(gen)
  val out = Output(gen)

  val en = Input(Bool())
  val valid_out = Output(Bool())
}

class ShiftRegisterMemExample[T <: Data](gen: T, n: Int) extends Module {
  val io = IO(new ShiftRegisterIO(gen, n))
  val logn = log2Ceil(n)
  val cnt = RegInit(0.U(logn.W))

  val shiftMem = ShiftRegisterMem(io.in, n, io.en, name = "simple_shift_register")
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
  chisel3.Driver.execute(args,()=>new ShiftRegisterMemExample(UInt(4.W), 10))
}
