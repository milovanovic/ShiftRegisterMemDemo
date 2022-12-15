#!/bin/bash
echo $0
full_path=$(realpath $0)
dir_path=$(dirname $full_path)

target_dir_mem_sram=$dir_path/generated-rtl/mem/sram
target_dir_mem_reg=$dir_path/generated-rtl/mem/reg
target_dir_sram_reg=$dir_path/generated-rtl/reg

depth_array=(2 4 8 16 32 64 128 256 512 1024)
#depth_array=(2)
width_array=(16 24 32)
#width_array=(16)

generate_mem_sram () {
  for depth in "${depth_array[@]}"
  do
    for width in "${width_array[@]}"
    do
      cd $dir_path && sbt "runMain shiftregmem.ShiftRegisterApp $target_dir_mem_sram $depth $width 1 1"
      if [ -d $target_dir_mem_sram ]; then mv $dir_path/mem.conf $target_dir_mem_sram/mem_width_${width}_depth_${depth}.conf;fi
    done
  done
}

generate_mem_reg () {
  for depth in "${depth_array[@]}"
  do
    for width in "${width_array[@]}"
    do
      cd $dir_path && sbt "runMain shiftregmem.ShiftRegisterApp $target_dir_mem_reg $depth $width 1"
    done
  done
}

generate_reg () {
  for depth in "${depth_array[@]}"
  do
    for width in "${width_array[@]}"
    do
      cd $dir_path && sbt "runMain shiftregmem.ShiftRegisterApp $target_dir_sram_reg $depth $width 0"
    done
  done
}

"$@"



