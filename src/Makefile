SRC_FILES = $(wildcard *.fbs)

all:
	flatc --java -o ./ $(SRC_FILES)
	flatc --js -o ./buffers $(SRC_FILES)