require 'bigdecimal'
require 'benchmark'
require 'gson'
require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)

dumped_string = File.read(filename)

Benchmark.bmbm(BenchOptions.output_width) do |x|
  x.report("gson parse: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { ::Gson::Decoder.new({}).decode(dumped_string) }
  end
end
