
base_dir ?= $(abspath .)
target_dir_mem_sram ?= $(base_dir)/generated-rtl/mem/sram
target_dir_mem_reg  ?= $(base_dir)/generated-rtl/mem/reg
target_dir_sram_reg ?= $(base_dir)/generated-rtl/reg

target_list = $(target_dir_mem_sram) $(target_dir_mem_reg) $(target_dir_sram_reg)

SBT ?= sbt
all: clean gen_rtl_reg gen_rtl_mem_sram gen_rtl_mem_reg clean_fir_json

gen_rtl_mem_sram:
	bash generate_verilog.sh generate_mem_sram
gen_rtl_mem_reg:
	bash generate_verilog.sh generate_mem_reg
gen_rtl_reg:
	bash generate_verilog.sh generate_reg

clean_fir_json:
	for target_dir in $(target_list); do if [ -d "$$target_dir" ]; then cd "$$target_dir" && rm -f *.fir *.anno.json;fi done

.PHONY: clean
clean:
	for target_dir in $(target_list); do if [ -d "$$target_dir" ]; then cd "$$target_dir" && rm -f *.*;fi done
