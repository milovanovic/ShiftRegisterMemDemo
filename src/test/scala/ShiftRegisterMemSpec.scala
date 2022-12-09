package chisel_version_test

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{FlatSpec, Matchers}

//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers

import scala.util.Random

class ShiftRegisterRandomTester(c: ShiftRegisterMemExample[UInt], testSignal: Seq[Int]) extends PeekPokeTester(c) {
  poke(c.io.en, 0)
  step(3)
  var cntIn = 0
  var cntOut = 0
  var expected = testSignal.iterator
  var enable = 0

  while (cntOut < testSignal.length) {
    enable = Random.nextInt(2)
    if (cntIn < testSignal.length) {
      poke(c.io.en, enable)
      if (peek(c.io.en) == BigInt(1)) {
        poke(c.io.in, testSignal(cntIn))
        cntIn = cntIn + 1
      }
    }
    else {
      poke(c.io.en, enable)
    }
    if (peek(c.io.valid_out) == BigInt(1)) {
      expect(c.io.out, expected.next())
      cntOut = cntOut + 1
    }
    step(1)
  }
  step(10)
}


class ShiftRegisterTester(c: ShiftRegisterMemExample[UInt], testSignal: Seq[Int]) extends PeekPokeTester(c) {
  poke(c.io.en, 0)
  step(3)
  poke(c.io.en, 1)
  var expected = testSignal.iterator

  for (i <- 0 until testSignal.length/2) {
    poke(c.io.in, testSignal(i))
    if (peek(c.io.valid_out) == BigInt(1)) {
      expect(c.io.out, expected.next())
    }
    step(1)
  }

  poke(c.io.en, 0)
  step(4)
  poke(c.io.en, 1)

  for (i <- testSignal.length/2 until (testSignal.length-1)) {
    poke(c.io.in, testSignal(i))
    if (peek(c.io.valid_out) == BigInt(1)) {
      expect(c.io.out, expected.next())
    }
    step(1)
  }
  poke(c.io.en, 0)
  step(10)
}

class ShiftRegisterMemSpec extends FlatSpec with Matchers { //AnyFlatSpec with Matchers {

  def getTestSignal(numSamples: Int): Seq[Int] = {
    (0 until numSamples).map(i => i) // make it simple
  }
  val depthSignal = 12
  val proto = UInt(4.W)
  val depthSR = 4
  val testSignal = getTestSignal(depthSignal)

  it should f"test ShiftRegisterMem with treadle backend and use_sp_mem = false" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
        "--backend-name", "treadle",
        "--tr-write-vcd",
        "--target-dir", "ShiftRegisterMem_treadle",
        "--top-name") :+ "ShiftRegisterMem_treadle",
        () => new ShiftRegisterMemExample(proto, depthSR, isMem = true, isSp = Some(false))) { c =>
    new ShiftRegisterTester(c, testSignal) } should be (true)
  }

  it should f"test ShiftRegisterMem with verilator backend and use_sp_mem = false" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
        "--backend-name", "verilator",
        "--target-dir", "ShiftRegisterMem_verilator",
        "--top-name") :+ "ShiftRegisterMem_verilator",
        () => new ShiftRegisterMemExample(proto, depthSR, isMem = true, isSp = Some(false))) { c =>
    new ShiftRegisterTester(c, testSignal) } should be (true)
  }

  it should f"test ShiftRegisterMem with treadle backend and use_sp_mem = true" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
        "--backend-name", "treadle",
        "--tr-write-vcd",
        "--target-dir", "ShiftRegisterMem_sp_treadle",
        "--top-name") :+ "ShiftRegisterMem_sp_treadle",
        () => new ShiftRegisterMemExample(proto, depthSR, isMem = true, isSp = Some(true))) { c =>
    new ShiftRegisterTester(c, testSignal) } should be (true)
  }

  it should f"test ShiftRegisterMem with verilator backend and use_sp_mem = true" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
        "--backend-name", "verilator",
        "--target-dir", "ShiftRegisterMem_sp_verilator",
        "--top-name") :+ "ShiftRegisterMem_sp_verilator",
        () => new ShiftRegisterMemExample(proto, depthSR, isMem = true, isSp = Some(true))) { c =>
    new ShiftRegisterTester(c, testSignal) } should be (true)
  }

  it should f"test ShiftRegister with treadle backend" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
        "--backend-name", "treadle",
        "--tr-write-vcd",
        "--target-dir", "ShiftRegister_treadle",
        "--top-name") :+ "ShiftRegister_treadle",
        () => new ShiftRegisterMemExample(proto, depthSR, isMem = false)) { c =>
    new ShiftRegisterTester(c, testSignal) } should be (true)
  }

  it should f"test ShiftRegister with verilator backend" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
        "--backend-name", "verilator",
        "--target-dir", "ShiftRegister_verilator",
        "--top-name") :+ "ShiftRegister_verilator",
        () => new ShiftRegisterMemExample(proto, depthSR, isMem = false)) { c =>
    new ShiftRegisterTester(c, testSignal) } should be (true)
  }
}


class ShiftRegisterMemRandomSpec extends FlatSpec with Matchers {

  def getTestSignal(numSamples: Int): Seq[Int] = {
    (0 until numSamples).map(i => i) // make it simple
  }
  val depthSignal = 12
  val proto = UInt(4.W)
  val depthSR = 2 // 4
  val testSignal = getTestSignal(depthSignal)

  it should f"test ShiftRegisterMem with treadle backend and use_sp_mem = true" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
        "--backend-name", "treadle",
        "--tr-write-vcd"),
        () => new ShiftRegisterMemExample(proto, depthSR, isMem = true, isSp = Some(true))) { c =>
    new ShiftRegisterRandomTester(c, testSignal) } should be (true)
  }

  it should f"test ShiftRegisterMem with verilator backend and use_sp_mem = true" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
        "--backend-name", "verilator"),
        () => new ShiftRegisterMemExample(proto, depthSR, isMem = true, isSp = Some(true))) { c =>
    new ShiftRegisterRandomTester(c, testSignal) } should be (true)
  }

  it should f"test ShiftRegister with treadle backend" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
        "--backend-name", "treadle",
        "--tr-write-vcd"),
        () => new ShiftRegisterMemExample(proto, depthSR, isMem = false)) { c =>
    new ShiftRegisterRandomTester(c, testSignal) } should be (true)
  }

  it should f"test ShiftRegister with verilator backend" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
        "--backend-name", "verilator"),
        () => new ShiftRegisterMemExample(proto, depthSR, isMem = false)) { c =>
    new ShiftRegisterRandomTester(c, testSignal) } should be (true)
  }
}

