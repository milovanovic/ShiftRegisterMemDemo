package shiftMem

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class SyncReadMemTester(c: ReadWriteSmem, testSignal: Seq[Int]) extends PeekPokeTester(c) {
  var expected = testSignal.iterator
  poke(c.io.enable,0)
  poke(c.io.addr, 0)

  poke(c.io.write, 1)

  for (i <- 0 until 8) {
    poke(c.io.addr, i)
    poke(c.io.dataIn, testSignal(i))
    step(1)
  }

  poke(c.io.write, 0)
  step(3)

  for (i <-0 until 5) {
    poke(c.io.enable, 1)
    poke(c.io.addr, i)
    step(1)
  }

  poke(c.io.enable, 0)
  step(5)

  for (i <-5 until 8) {
    poke(c.io.enable, 1)
    poke(c.io.addr, i)
    step(1)
  }

/*  step(4)
  poke(c.io.enable, 0)
  step(4)
  poke(c.io.enable, 1)*/

  step(10)


  /*for (i <- 0 until testSignal.length/2) {
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
  step(2)*/
}

class SyncReadMemMemSpec extends AnyFlatSpec with Matchers {

  def getTestSignal(numSamples: Int): Seq[Int] = {
    (0 until numSamples).map(i => i) // make it simple
  }

  val testSignal = getTestSignal(8)

  it should f"test SyncReadMem with treadle backend" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
        "--backend-name", "treadle",
        "--tr-write-vcd",
        "--target-dir", "SyncReadMem_treadle",
        "--top-name") :+ "SyncReadMem_treadle",
        () => new ReadWriteSmem) { c =>
    new SyncReadMemTester(c, testSignal) } should be (true)
  }

  it should f"test SyncReadMem with verilator backend" in {
    chisel3.iotesters.Driver.execute(Array("-fiwv",
        "--backend-name", "verilator",
        "--target-dir", "SyncReadMem_verilator",
        "--top-name") :+ "SyncReadMem_verilator",
        () => new ReadWriteSmem) { c =>
    new SyncReadMemTester(c, testSignal) } should be (true)
  }
}
