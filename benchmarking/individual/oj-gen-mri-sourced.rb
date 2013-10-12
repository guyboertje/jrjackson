require 'benchmark'
require 'oj'

require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)
hsh = Oj.compat_load(File.read(filename))

Benchmark.bmbm("jackson parse symbol + bigdecimal:  ".size) do |x|
  x.report("oj mri generate: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { Oj.dump(hsh) }
  end
end
