require 'benchmark'
load 'benchmarking/overlay.rb'

require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)

dumped_string = File.read(filename)

parser = Overlay::Parser.new
# iters = BenchOptions.iterations
iters = 20

Benchmark.bmbm(BenchOptions.output_width) do |x|
  x.report("overlay pure parse: #{iters}") do
    iters.times { parser.parse(dumped_string) }
  end
end


# class Vtd
#   Ko = 32
#   So = 40
#   Pm = 0xffffffff
#   Km = 0xff
#   Sm = 0xfffff
#   attr_reader :value

#   def initialize(val = 0)
#     @value = val
#   end
#   def reset
#     @value = 0
#   end
#   def position
#     @value & Pm
#   end
#   def kind
#     @value >> Ko & Km
#   end
#   def size
#     @value >> So & Sm
#   end
#   def position=(val)
#     @value |= val
#   end
#   def kind=(val)
#     @value |= (val << Ko)
#   end
#   def size=(val)
#     @value |= (val << So)
#   end
# end
# Vtd = Struct.new(:position, :kind, :size)
