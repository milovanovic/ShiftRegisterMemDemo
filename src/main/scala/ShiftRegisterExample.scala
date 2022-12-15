package shiftregmem

import chisel3._
import chisel3.util._

import craft.ShiftRegisterMem
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

class ShiftRegisterIO[T <: Data](gen: T, n: Int) extends Bundle {
  require (n >= 0, "Shift register must have non-negative shift")
  val in = Input(gen.cloneType)
  val out = Output(gen.cloneType)
  
  val en = Input(Bool())
  val valid_out = Output(Bool())
  
  override def cloneType: this.type = (new ShiftRegisterIO(gen, n)).asInstanceOf[this.type]
}

class ShiftRegisterExample[T <: Data](gen: T, n: Int, isMem: Boolean, isSram: Option[Boolean]) extends Module {
  val io = IO(new ShiftRegisterIO(gen, n))

  val logn = log2Ceil(n)
  val cnt = RegInit(0.U(logn.W))
  val dataWidth = gen.getWidth

  val memFlag = if (isMem) "mem" else "reg"
  val sramFlag = if (isSram.getOrElse(false)) "_sram" else ""

  val shiftMem = if (memFlag == "mem") ShiftRegisterMem(io.in, n, io.en, name = "simple_shift_reg" + "_width_" + dataWidth.toString + "_depth_" + n.toString + "_" + memFlag + sramFlag) else ShiftRegister(io.in, n, io.en)
  override def desiredName = "shift_reg" + "_width_" + dataWidth.toString + "_depth_" + n.toString + "_" + memFlag + sramFlag

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

object ShiftRegisterApp extends App
{
  implicit def int2bool(b: Int) = if (b == 1) true else false
  if (args.length < 4) {
    println("This application requires at least 4 arguments")
  }
  val buildDirName = args(0).toString
  val width = args(1).toInt
  val depth = args(2).toInt
  val isMem = int2bool(args(3).toInt)
  val isSram : Option[Boolean] = if (args.length == 4) None else Some(true)

  if (isMem & isSram.getOrElse(false)) {
    val arguments = Array(
      "--target-dir", buildDirName,
      "-X", "verilog",
      "--repl-seq-mem", "-c:ShiftRegisterMemExample:-o:mem.conf",
      "--log-level", "info"
    )
  // generate black boxes for memories
    (new ChiselStage).execute(
      arguments,
      Seq(ChiselGeneratorAnnotation(() => new ShiftRegisterExample(SInt(width.W), depth, isMem, isSram))))
  }
  else {
    val arguments = Array(
      "--target-dir", buildDirName,
      "-X", "verilog"
    )
    (new chisel3.stage.ChiselStage).execute(
      arguments,
      Seq(ChiselGeneratorAnnotation(() => new ShiftRegisterExample(SInt(width.W), depth, isMem, isSram))))
  }
}
