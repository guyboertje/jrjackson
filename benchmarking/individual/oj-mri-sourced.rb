require 'bigdecimal'
require 'benchmark'
require 'oj'
require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)

dumped_string = File.read(filename)

Benchmark.bmbm(BenchOptions.output_width) do |x|
  x.report("oj mri parse: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { Oj.compat_load(dumped_string) }
  end
end

