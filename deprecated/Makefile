
base_dir ?= $(abspath .)
target_dir_mem_sram ?= $(base_dir)/generated-rtl/mem/sram
target_dir_mem_reg  ?= $(base_dir)/generated-rtl/mem/reg
target_dir_sram_reg ?= $(base_dir)/generated-rtl/reg

target_list = $(target_dir_mem_sram) $(target_dir_mem_reg) $(target_dir_sram_reg)

PROJECT ?= shiftregmem
SBT ?= sbt

WIDTH ?= 16
DEPTH ?= 32

all: clean gen_rtl_mem_sram  move_conf

gen_rtl_mem_sram:
	cd $(base_dir) && $(SBT) "runMain $(PROJECT).ShiftRegisterApp $(target_dir_mem_sram) $(WIDTH) $(DEPTH) 1 1"

gen_rtl_mem_reg:
	cd $(base_dir) && $(SBT) "runMain $(PROJECT).ShiftRegisterApp $(target_dir_mem_reg) $(WIDTH) $(DEPTH) 1 0"

gen_rtl_reg:
	cd $(base_dir) && $(SBT) "runMain $(PROJECT).ShiftRegisterApp $(target_dir_sram_reg) $(WIDTH) $(DEPTH) 0 0"

clean_fir_json:
	for target_dir in $(target_list); do if [ -d "$$target_dir" ]; then cd "$$target_dir" && rm -f *.fir *.anno.json;fi done
move_conf:
	if [ -d $(target_dir_mem_sram) ]; then mv $(base_dir)/mem.conf $(target_dir_mem_sram)/mem_$(DEPTH)_$(WIDTH).conf;fi
.PHONY: clean
clean:
	for target_dir in $(target_list); do if [ -d "$$target_dir" ]; then cd "$$target_dir" && rm -f *.*;fi done
