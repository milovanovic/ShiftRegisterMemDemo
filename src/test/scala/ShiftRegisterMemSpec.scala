package backendTest

import chisel3._
import chisel3.iotesters.PeekPokeTester

import org.scalatest.{FlatSpec, Matchers}
import scala.util.Random

class ShiftRegisterTester(c: ShiftRegisterMemExample[UInt], testSignal: Seq[Int]) extends PeekPokeTester(c) {
  poke(c.io.en, 0)
  step(3)
  poke(c.io.en, 1)

  for (i <- 0 until testSignal.length/2) {
    poke(c.io.in, testSignal(i))
    step(1)
  }

  poke(c.io.en, 0)
  step(8)
  poke(c.io.en, 1)

  for (i <- testSignal.length/2 until (testSignal.length-1)) {
    poke(c.io.in, testSignal(i))
    step(1)
  }
}

class ShiftRegisterMemSpec extends FlatSpec with Matchers {

  def getTestSignal(numSamples: Int): Seq[Int] = {
    // Add this to get same test case every time
    // Random.setSeed(11110L)
    (0 until numSamples).map(i => Random.nextInt(16))
  }
  val depthSignal = 12
  val proto = UInt(4.W)
  val depthSR = 4
  val testSignal = getTestSignal(depthSignal)

  it should f"test ShiftRegisterMem with treadle backend" in {
    chisel3.iotesters.Driver.execute(
      Array(
        "--tr-mem-to-vcd", "all",
        "--tr-clock-info", "clock:2",  // this sets time scale to more verilator like setting
        "--backend-name", "treadle",
        "--tr-write-vcd",
        "--target-dir", "ShiftRegisterMem_treadle",
        "--top-name") :+ "ShiftRegisterMem_treadle",
      () => new ShiftRegisterMemExample(proto, depthSR)) { c =>
      new ShiftRegisterTester(c, testSignal) } should be (true)
  }

  it should f"test ShiftRegisterMem with verilator backend" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
      "--backend-name", "verilator",
      "--target-dir", "ShiftRegisterMem_verilator",
      "--top-name") :+ "ShiftRegisterMem_verilator",
      () => new ShiftRegisterMemExample(proto, depthSR)) { c =>
      new ShiftRegisterTester(c, testSignal) } should be (true)
  }

  it should f"test ShiftRegisterMem with firrtl backend" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
      "--backend-name", "firrtl",
      "--target-dir", "ShiftRegisterMem_firrtl",
      "--top-name") :+ "ShiftRegisterMem_firrtl",
      () => new ShiftRegisterMemExample(proto, depthSR)) { c =>
      new ShiftRegisterTester(c, testSignal) } should be (true)
  }
}
